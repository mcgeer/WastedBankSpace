#!/usr/bin/env python3
"""
Local copy of the checks the RuneLite Plugin Hub packager runs on submitted plugins, so problems show up
here instead of when the plugin is submitted.

Mirrors runelite/plugin-hub-tooling tag v4 (commit 5893c9f0da, the bundle the plugin-hub CI downloads),
package/.../Plugin.java, in PR mode: the mode every submission and update goes through, where every check
is fatal. A few checks can only be approximated outside the real packager; each one says so.

Usage: plugin_hub_checks.py [--internal-name NAME] [--jar-size-limit-mib N] [path/to/built.jar ...]
  --internal-name       the plugin's file name in runelite/plugin-hub/plugins (e.g. wasted-bank-space).
                        Used to validate PluginDescriptor.internalName if the plugin sets one.
  --jar-size-limit-mib  the jarSizeLimitMiB from the plugin's hub descriptor, if it has one (default 10).
With a jar, the plugin classes are validated from the compiled classes the way the hub does; without one,
the source is checked instead.
Exits non-zero if any check fails. Warnings don't fail the build.
"""
import argparse
import glob
import io
import os
import re
import struct
import subprocess
import sys
import zipfile
import zlib

MIB = 1024 * 1024
MAX_JAR_MIB = 10
MAX_SRC_MIB = 10
MAX_ICON_KIB = 256
# The hub fails an icon whose pixel area is over 50*100; 48x72 is what it asks for
MAX_ICON_AREA = 50 * 100
ICON_SIZE = (48, 72)
JAVA_11_MAJOR = 55
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"

PROPERTIES_FILE = "runelite-plugin.properties"
REQUIRED_PROPERTIES = {
	# key: template value the Plugin Hub rejects
	"displayName": "Example",
	"author": "Nobody",
	"description": "An example greeter plugin",
}
ALLOWED_PROPERTIES = {"displayName", "author", "description", "tags", "plugins", "build", "support", "version"}
BUILD_TYPES = {"standard", "gradle"}
PLUGIN_SUPERCLASS = "net/runelite/client/plugins/Plugin"
PLUGIN_DESCRIPTOR = "Lnet/runelite/client/plugins/PluginDescriptor;"
# files the Plugin Hub counts towards the source size limit
CORE_SOURCE = re.compile(r"^(\.github/|docs/|)readme(\..*)?$|^license|^src/main/|runelite-plugin.properties|\.gradle", re.IGNORECASE)
# what the hub's own build.gradle provides under build=standard (everything else in build.gradle is ignored)
STANDARD_BUILD_DEPENDENCIES = re.compile(r"net\.runelite|projectlombok:lombok:1\.18\.30|org\.jetbrains:annotations:23\.0\.0")

# Comments and string/char literals, blanked out before the API patterns run, since the hub only looks at
# symbols the compiled code references (package/.../RecordingTreeScanner.java ignores imports too)
JAVA_NOISE = re.compile(r'//[^\n]*|/\*.*?\*/|"(?:\\.|[^"\\\n])*"|\'(?:\\.|[^\'\\\n])*\'', re.DOTALL)
JAVA_COMMENTS = re.compile(r'//[^\n]*|/\*.*?\*/', re.DOTALL)

# Approximates package/src/main/resources/.../disallowed-apis.txt at the source level
DISALLOWED_APIS = [
	(re.compile(r"\.\s*getVar\s*\(|::\s*getVar\b"), "Client.getVar is disallowed, use client.getVarbitValue / getVarpValue"),
	(re.compile(r"new\s+(okhttp3\.)?OkHttpClient\s*(\.Builder\s*)?\("), "don't create an OkHttpClient, @Inject the client's OkHttpClient"),
	(re.compile(r"new\s+(com\.google\.gson\.)?Gson(Builder)?\s*\("), "don't create a Gson, @Inject the client's Gson (use .newBuilder() to customise it)"),
	(re.compile(r"\bWidgetInfo\b"), "WidgetInfo is disallowed, use ComponentID / InterfaceID"),
	(re.compile(r"\bWidgetID\b"), "WidgetID is disallowed, use ComponentID / InterfaceID"),
	(re.compile(r"getItemStats\s*\([^,()]+,[^()]+\)"), "use the getItemStats(int itemId) overload"),
	(re.compile(r"\b(AccountClient|AccountSession|SessionManager)\b"), "account and session APIs are disallowed"),
]
CHAT_MESSAGE_MANAGER_UPDATE = "ChatMessageManager.update is a no-op and disallowed"

errors = 0
in_actions = os.environ.get("GITHUB_ACTIONS") == "true"


def error(message, file=None, line=None):
	global errors
	errors += 1
	report("error", message, file, line)


def warning(message, file=None, line=None):
	report("warning", message, file, line)


def report(level, message, file, line):
	if in_actions:
		location = ""
		if file:
			location = f" file={file}" + (f",line={line}" if line else "")
		print(f"::{level}{location}::{message}")
	else:
		where = f"{file}:{line}: " if file and line else f"{file}: " if file else ""
		print(f"{level.upper()}: {where}{message}")


# ---- runelite-plugin.properties ---------------------------------------------------------------------------

def read_properties(path):
	"""
	Reads the file the way java.util.Properties.load does, which is what the hub uses: ISO-8859-1, a key ends at
	the first unescaped '=', ':' or whitespace, backslash line continuations, leading whitespace is dropped from
	the value and trailing whitespace is KEPT. Returns {key: (value, line number)}.
	"""
	props = {}
	with open(path, encoding="latin-1") as f:
		raw_lines = f.read().split("\n")
	i = 0
	while i < len(raw_lines):
		number = i + 1
		line = raw_lines[i].rstrip("\r")
		i += 1
		while _continues(line) and i < len(raw_lines):
			line = line[:-1] + raw_lines[i].rstrip("\r").lstrip(" \t\f")
			i += 1
		stripped = line.lstrip(" \t\f")
		if not stripped or stripped[0] in "#!":
			continue
		key, value = _split_property(stripped)
		props[_unescape(key)] = (_unescape(value), number)
	return props


def _continues(line):
	backslashes = len(line) - len(line.rstrip("\\"))
	return backslashes % 2 == 1


def _split_property(s):
	i = 0
	key = []
	while i < len(s):
		c = s[i]
		if c == "\\" and i + 1 < len(s):
			key.append(s[i:i + 2])
			i += 2
			continue
		if c in "=: \t\f":
			break
		key.append(c)
		i += 1
	while i < len(s) and s[i] in " \t\f":
		i += 1
	if i < len(s) and s[i] in "=:":
		i += 1
		while i < len(s) and s[i] in " \t\f":
			i += 1
	return "".join(key), s[i:]


def _unescape(s):
	out = []
	i = 0
	while i < len(s):
		c = s[i]
		if c == "\\" and i + 1 < len(s):
			n = s[i + 1]
			if n == "u" and i + 6 <= len(s):
				try:
					out.append(chr(int(s[i + 2:i + 6], 16)))
					i += 6
					continue
				except ValueError:
					pass
			out.append({"t": "\t", "n": "\n", "r": "\r", "f": "\f"}.get(n, n))
			i += 2
			continue
		out.append(c)
		i += 1
	return "".join(out)


def check_properties():
	"""Returns (plugin classes, build type)."""
	if not os.path.exists(PROPERTIES_FILE):
		error(f"{PROPERTIES_FILE} is missing")
		return [], ""

	props = read_properties(PROPERTIES_FILE)
	for key, template in REQUIRED_PROPERTIES.items():
		value = props.get(key, ("", None))[0]
		if not value or value == template:
			error(f'"{key}" must be set', PROPERTIES_FILE)

	build, line = props.get("build", ("", None))
	if not build:
		error('"build" must be set: add build=standard (recommended) or build=gradle', PROPERTIES_FILE)
	elif build not in BUILD_TYPES:
		hint = " (the hub keeps trailing whitespace, so it does not match)" if build.strip() in BUILD_TYPES else ""
		error(f'build must be one of {sorted(BUILD_TYPES)}, not "{build}"{hint}', PROPERTIES_FILE, line)

	for key, (_, line) in props.items():
		if key not in ALLOWED_PROPERTIES:
			error(f'unknown key "{key}" in {PROPERTIES_FILE}', PROPERTIES_FILE, line)

	plugins, line = props.get("plugins", ("", None))
	classes = [c.strip() for c in re.split(r"[,:;]", plugins) if c.strip()]
	if not classes:
		error('"plugins" must list at least one plugin class', PROPERTIES_FILE, line)
	return classes, build


# ---- plugin classes -----------------------------------------------------------------------------------------

def check_plugin_classes_in_source(classes):
	"""Fallback used when no jar is given. Returns {class name: internalName} for classes that set one."""
	internal_names = {}
	for name in classes:
		path = os.path.join("src", "main", "java", *name.split(".")) + ".java"
		if not os.path.exists(path):
			error(f'plugin class "{name}" not found at {path}', PROPERTIES_FILE)
			continue
		with open(path, encoding="utf-8", errors="replace") as f:
			text = f.read()
		source = JAVA_NOISE.sub(lambda m: re.sub(r"[^\n]", " ", m.group(0)), text)
		if not re.search(r"\bextends\s+(net\.runelite\.client\.plugins\.)?Plugin\b", source) or "@PluginDescriptor" not in source:
			error(f'"{name}" must extend Plugin and have a @PluginDescriptor', path)
		# The internalName value is a string literal, so look for it with only the comments removed
		without_comments = JAVA_COMMENTS.sub(lambda m: re.sub(r"[^\n]", " ", m.group(0)), text)
		descriptor = re.search(r"@PluginDescriptor\s*\(((?:[^()]|\([^()]*\))*)\)", without_comments)
		if descriptor:
			internal = re.search(r"\binternalName\s*=\s*\"([^\"]*)\"", descriptor.group(1))
			if internal:
				internal_names[name] = internal.group(1)
	return internal_names


def check_internal_names(internal_names, expected):
	# New in plugin-hub-tooling v4: a PluginDescriptor.internalName that is set must equal the hub file name
	for name, internal in internal_names.items():
		if expected is None:
			warning(f'"{name}" sets PluginDescriptor.internalName = "{internal}"; pass --internal-name to check it against the Plugin Hub name')
		elif internal != expected:
			error(f'"{name}" sets PluginDescriptor.internalName = "{internal}", but the Plugin Hub name is "{expected}" (PluginDescriptor.internalName must match the real internal name)')


# ---- class file parsing (for the jar checks) ----------------------------------------------------------------

def parse_class(data):
	"""
	Returns (this class, superclass, major version, class annotations). Annotations map the annotation type
	descriptor to {element name: value}, with only string values decoded (others are None).
	"""
	if data[:4] != b"\xca\xfe\xba\xbe":
		raise ValueError("not a class file")
	major = struct.unpack(">H", data[6:8])[0]

	def u2(at):
		return struct.unpack(">H", data[at:at + 2])[0]

	def u4(at):
		return struct.unpack(">I", data[at:at + 4])[0]

	pos = 8
	count = u2(pos)
	pos += 2
	pool = [None] * count
	i = 1
	while i < count:
		tag = data[pos]
		pos += 1
		if tag == 1:
			length = u2(pos)
			pool[i] = data[pos + 2:pos + 2 + length].decode("utf-8", "replace")
			pos += 2 + length
		elif tag in (3, 4):
			pos += 4
		elif tag in (5, 6):
			pos += 8
			i += 1
		elif tag in (7, 8, 16, 19, 20):
			pool[i] = u2(pos)
			pos += 2
		elif tag in (9, 10, 11, 12, 17, 18):
			pos += 4
		elif tag == 15:
			pos += 3
		else:
			raise ValueError(f"unknown constant pool tag {tag}")
		i += 1

	def class_name(index):
		return pool[pool[index]] if index else None

	this_class = class_name(u2(pos + 2))
	super_class = class_name(u2(pos + 4))
	pos += 6
	pos += 2 + 2 * u2(pos)  # interfaces
	for _ in range(2):  # fields, then methods
		members = u2(pos)
		pos += 2
		for _ in range(members):
			pos += 6
			attributes = u2(pos)
			pos += 2
			for _ in range(attributes):
				pos += 6 + u4(pos + 2)

	annotations = {}
	attributes = u2(pos)
	pos += 2
	for _ in range(attributes):
		name = pool[u2(pos)]
		length = u4(pos + 2)
		pos += 6
		if name in ("RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations"):
			annotations.update(_read_annotations(data, pos, pool))
		pos += length
	return this_class, super_class, major, annotations


def _read_annotations(data, pos, pool):
	def u2(at):
		return struct.unpack(">H", data[at:at + 2])[0]

	def read_annotation(at):
		type_name = pool[u2(at)]
		pairs = u2(at + 2)
		at += 4
		elements = {}
		for _ in range(pairs):
			element = pool[u2(at)]
			elements[element], at = read_value(at + 2)
		return type_name, elements, at

	def read_value(at):
		tag = chr(data[at])
		at += 1
		if tag in "BCDFIJSZs":
			value = pool[u2(at)] if tag == "s" else None
			return value, at + 2
		if tag == "e":
			return None, at + 4
		if tag == "c":
			return None, at + 2
		if tag == "@":
			_, _, at = read_annotation(at)
			return None, at
		if tag == "[":
			items = u2(at)
			at += 2
			for _ in range(items):
				_, at = read_value(at)
			return None, at
		raise ValueError(f"unknown annotation element tag {tag!r}")

	annotations = {}
	count = u2(pos)
	pos += 2
	for _ in range(count):
		type_name, elements, pos = read_annotation(pos)
		annotations[type_name] = elements
	return annotations


# ---- the other checks ---------------------------------------------------------------------------------------

def check_license():
	if not os.path.exists("LICENSE"):
		error("Missing LICENSE file (the Plugin Hub recommends BSD 2-Clause)")


def check_icon():
	if not os.path.exists("icon.png"):
		return
	size = os.path.getsize("icon.png")
	if size > MAX_ICON_KIB * 1024:
		error(f"icon.png is {size // 1024}KiB, which is above the limit of {MAX_ICON_KIB}KiB", "icon.png")
	with open("icon.png", "rb") as f:
		data = f.read()
	if data[:8] != PNG_SIGNATURE:
		# The hub decodes the icon with ImageIO, so other formats work, but its size can't be checked here
		warning("icon.png is not a PNG; the Plugin Hub accepts anything Java can decode, but a real PNG is expected", "icon.png")
		return
	if len(data) < 24:
		error("icon.png is truncated", "icon.png")
		return
	width, height = struct.unpack(">II", data[16:24])
	if width * height > MAX_ICON_AREA:
		error(f"icon.png is too high-resolution ({width}x{height}). It should be {ICON_SIZE[0]}x{ICON_SIZE[1]} px", "icon.png")
	elif width > ICON_SIZE[0] or height > ICON_SIZE[1]:
		warning(f"icon.png is {width}x{height}; the Plugin Hub asks for at most {ICON_SIZE[0]}x{ICON_SIZE[1]} px", "icon.png")

	# Walk the chunks and verify their CRCs, since the hub fails an icon it can't decode
	pos = 8
	while True:
		if pos + 8 > len(data):
			error("icon.png is truncated (no IEND chunk)", "icon.png")
			return
		length, chunk_type = struct.unpack(">I4s", data[pos:pos + 8])
		end = pos + 12 + length
		if end > len(data):
			error("icon.png is truncated", "icon.png")
			return
		crc = struct.unpack(">I", data[end - 4:end])[0]
		if zlib.crc32(chunk_type + data[pos + 8:pos + 8 + length]) & 0xFFFFFFFF != crc:
			error(f"icon.png is corrupt (bad CRC in {chunk_type.decode('ascii', 'backslashreplace')} chunk)", "icon.png")
			return
		pos = end
		if chunk_type == b"IEND":
			return


def check_source_size(jar_limit_mib):
	# The hub zips the core files (deflated), skipping every directory whose path contains ".git", so .github/
	# doesn't count, and fails above (max(10, jarSizeLimitMiB) + 1) MiB. Other files are only added while they fit.
	files = subprocess.run(["git", "ls-files"], capture_output=True, text=True, check=True).stdout.splitlines()
	buffer = io.BytesIO()
	with zipfile.ZipFile(buffer, "w", zipfile.ZIP_DEFLATED) as zf:
		for f in files:
			if any(".git" in part for part in f.split("/")[:-1]):
				continue
			if CORE_SOURCE.search(f) and os.path.isfile(f):
				zf.write(f, f)
	size = len(buffer.getvalue())
	limit_mib = max(MAX_SRC_MIB, jar_limit_mib) + 1
	if size > limit_mib * MIB:
		error(f"source is {size / MIB:.1f}MiB compressed, which is above the limit of {limit_mib}MiB")


def check_gradle_files(build_type):
	paths = sorted(glob.glob("*.gradle") + glob.glob("*.gradle.kts"))
	if build_type == "gradle":
		# Root-level gradle files must wrap at 120 columns (a tab counts 8, a non-ASCII character 4)
		for path in paths:
			with open(path, encoding="utf-8", errors="replace") as f:
				for number, line in enumerate(f, 1):
					width = sum(8 if c == "\t" else 4 if ord(c) > 127 else 1 for c in line.rstrip("\r\n"))
					if width > 120:
						error("All gradle files must wrap at 120 characters or less", path, number)
	elif build_type == "standard" and os.path.exists("build.gradle"):
		# The hub replaces build.gradle with its own, which only provides net.runelite:client, Lombok 1.18.30
		# and org.jetbrains:annotations, so anything else the main code needs won't exist there
		with open("build.gradle", encoding="utf-8", errors="replace") as f:
			for number, line in enumerate(f, 1):
				m = re.match(r"\s*(implementation|api|compileOnly|runtimeOnly|annotationProcessor)\b\s*\(?\s*(.+)", line)
				if m and not STANDARD_BUILD_DEPENDENCIES.search(m.group(2)):
					warning(f"build=standard ignores build.gradle, so this dependency won't exist on the Plugin Hub: {m.group(2).strip()}", "build.gradle", number)


def check_disallowed_apis():
	for root, _, names in os.walk(os.path.join("src", "main", "java")):
		for name in sorted(names):
			if not name.endswith(".java"):
				continue
			path = os.path.join(root, name).replace(os.sep, "/")
			with open(path, encoding="utf-8", errors="replace") as f:
				source = JAVA_NOISE.sub(lambda m: re.sub(r"[^\n]", " ", m.group(0)), f.read())

			# The hub matches the exact ChatMessageManager.update symbol, whatever it is called on; follow the
			# names this file gives to a ChatMessageManager instead
			receivers = set(re.findall(r"\bChatMessageManager\s+(\w+)\s*[;=,)]", source))
			update_pattern = None
			if receivers:
				update_pattern = re.compile(r"\b(?:%s|getChatMessageManager\(\))\s*\.\s*update\s*\(" % "|".join(sorted(receivers)))

			for number, line in enumerate(source.splitlines(), 1):
				if line.lstrip().startswith(("import ", "package ")):
					continue
				for pattern, message in DISALLOWED_APIS:
					if pattern.search(line):
						error(message, path, number)
				if update_pattern and update_pattern.search(line):
					error(CHAT_MESSAGE_MANAGER_UPDATE, path, number)


def check_jar(jar, classes, jar_limit_mib):
	"""Returns {plugin class: internalName} for plugin classes that set one."""
	size = os.path.getsize(jar)
	if size > jar_limit_mib * MIB:
		error(f"jar is {size / MIB:.1f}MiB, which is above the limit of {jar_limit_mib}MiB", jar)
	elif size > jar_limit_mib * MIB * 8 // 10:
		warning(f"jar is {size / MIB:.1f}MiB, which is nearing the limit of {jar_limit_mib}MiB", jar)

	internal_names = {}
	with zipfile.ZipFile(jar) as zf:
		entries = set(zf.namelist())
		for entry in sorted(entries):
			if not entry.endswith(".class"):
				continue
			versioned = entry.startswith("META-INF/versions/")
			class_path = re.sub(r"^META-INF/versions/\d+/", "", entry) if versioned else entry
			if class_path.startswith("net/runelite/"):
				error("use of the net.runelite package namespace is not allowed", entry)
			# Only entries under META-INF/versions may be newer than Java 11 (plus module-info)
			if versioned or entry.endswith("module-info.class"):
				continue
			major = struct.unpack(">H", zf.read(entry)[6:8])[0]
			if major > JAVA_11_MAJOR:
				error(f"plugins must be Java 11 compatible (class file version {major})", entry)

		# Validate the plugin classes from the compiled classes, like the hub does
		for name in classes:
			entry = name.replace(".", "/") + ".class"
			if entry not in entries:
				error(f'Plugin class "{name}" is missing from the output jar', jar)
				continue
			try:
				_, super_class, _, annotations = parse_class(zf.read(entry))
			except (ValueError, struct.error, IndexError) as e:
				error(f'Plugin class "{name}" could not be parsed ({e})', entry)
				continue
			if super_class != PLUGIN_SUPERCLASS or PLUGIN_DESCRIPTOR not in annotations:
				error(f'Plugin class "{name}" is not a valid Plugin (it must extend Plugin directly and have @PluginDescriptor)', entry)
				continue
			internal = annotations[PLUGIN_DESCRIPTOR].get("internalName")
			if internal:
				internal_names[name] = internal
	return internal_names


def main():
	parser = argparse.ArgumentParser(description="Run the RuneLite Plugin Hub checks locally")
	parser.add_argument("jars", nargs="*", help="built plugin jar(s) to check")
	parser.add_argument("--internal-name", help="the plugin's file name in runelite/plugin-hub/plugins")
	parser.add_argument("--jar-size-limit-mib", type=int, default=MAX_JAR_MIB, help="jarSizeLimitMiB from the hub descriptor")
	args = parser.parse_args()

	if args.internal_name is not None and not re.fullmatch(r"[a-z0-9-]+", args.internal_name):
		error(f'internal name "{args.internal_name}" must match ^[a-z0-9-]+$')

	classes, build_type = check_properties()
	internal_names = {}
	if args.jars:
		for jar in args.jars:
			internal_names.update(check_jar(jar, classes, args.jar_size_limit_mib))
	else:
		internal_names.update(check_plugin_classes_in_source(classes))
	check_internal_names(internal_names, args.internal_name)
	check_license()
	check_icon()
	check_source_size(args.jar_size_limit_mib)
	check_gradle_files(build_type)
	check_disallowed_apis()

	if errors:
		print(f"\nPlugin Hub checks failed with {errors} error(s)")
		sys.exit(1)
	print("Plugin Hub checks passed")


if __name__ == "__main__":
	main()
