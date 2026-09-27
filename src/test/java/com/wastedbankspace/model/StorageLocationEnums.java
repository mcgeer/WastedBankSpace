/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2026, Brettgod1355
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.wastedbankspace.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Test helper that finds the storage locations by scanning the compiled
 * {@code com.wastedbankspace.model.locations} package of the plugin itself, so tests also cover a location that is
 * added later, instead of relying on a hand-maintained list that would share the blind spot of the production code.
 */
public final class StorageLocationEnums
{
	public static final String LOCATIONS_PACKAGE = "com.wastedbankspace.model.locations";

	private StorageLocationEnums()
	{
	}

	/**
	 * @return every top-level class in the locations package that implements {@link StorableItem}, sorted by name.
	 * Only the plugin's own classes are scanned, never the test classes.
	 */
	public static List<Class<?>> findLocationClasses()
	{
		List<Class<?>> locations = new ArrayList<>();
		for (String className : listClassNames())
		{
			Class<?> type = load(className);
			if (StorableItem.class.isAssignableFrom(type) && !type.isInterface())
			{
				locations.add(type);
			}
		}
		if (locations.isEmpty())
		{
			throw new AssertionError("No StorableItem classes found in " + LOCATIONS_PACKAGE);
		}
		locations.sort(Comparator.comparing(Class::getName));
		return locations;
	}

	/**
	 * @return the constants of a location enum, in declaration order
	 */
	public static List<StorableItem> itemsOf(Class<?> location)
	{
		if (!location.isEnum())
		{
			throw new AssertionError(location.getName() + " implements StorableItem but is not an enum,"
				+ " so it cannot be registered as a storage location");
		}
		List<StorableItem> items = new ArrayList<>();
		for (Object constant : location.getEnumConstants())
		{
			items.add((StorableItem) constant);
		}
		return items;
	}

	/**
	 * @return the item IDs of every constant of a location enum
	 */
	public static Set<Integer> itemIdsOf(Class<?> location)
	{
		Set<Integer> ids = new HashSet<>();
		for (StorableItem item : itemsOf(location))
		{
			ids.add(item.getItemID());
		}
		return ids;
	}

	/**
	 * @return the location enum that declares the given item
	 */
	public static Class<?> locationOf(StorableItem item)
	{
		return ((Enum<?>) item).getDeclaringClass();
	}

	private static List<String> listClassNames()
	{
		String packagePath = LOCATIONS_PACKAGE.replace('.', '/');
		List<String> fileNames = new ArrayList<>();
		Path root = pluginClassesRoot();
		try
		{
			if (Files.isDirectory(root))
			{
				try (DirectoryStream<Path> files = Files.newDirectoryStream(root.resolve(packagePath), "*.class"))
				{
					for (Path file : files)
					{
						fileNames.add(file.getFileName().toString());
					}
				}
			}
			else
			{
				try (JarFile jar = new JarFile(root.toFile()))
				{
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements())
					{
						String name = entries.nextElement().getName();
						if (name.startsWith(packagePath + "/") && name.endsWith(".class")
							&& name.indexOf('/', packagePath.length() + 1) < 0)
						{
							fileNames.add(name.substring(packagePath.length() + 1));
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new IllegalStateException("Could not list " + LOCATIONS_PACKAGE + " in " + root, e);
		}

		List<String> classNames = new ArrayList<>();
		for (String fileName : fileNames)
		{
			// Nested and anonymous classes (e.g. enum constant bodies) are covered by their top-level class
			if (!fileName.contains("$"))
			{
				classNames.add(LOCATIONS_PACKAGE + "." + fileName.substring(0, fileName.length() - ".class".length()));
			}
		}
		return classNames;
	}

	private static Path pluginClassesRoot()
	{
		CodeSource source = StorableItem.class.getProtectionDomain().getCodeSource();
		if (source == null)
		{
			throw new IllegalStateException("Cannot tell where the plugin classes were loaded from");
		}
		try
		{
			return Paths.get(source.getLocation().toURI());
		}
		catch (URISyntaxException e)
		{
			throw new IllegalStateException("Unexpected code source " + source.getLocation(), e);
		}
	}

	private static Class<?> load(String className)
	{
		try
		{
			return Class.forName(className, false, StorableItem.class.getClassLoader());
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalStateException("Found " + className + " on disk but could not load it", e);
		}
	}
}
