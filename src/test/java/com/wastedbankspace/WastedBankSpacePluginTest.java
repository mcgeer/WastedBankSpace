package com.wastedbankspace;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WastedBankSpacePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WastedBankSpacePlugin.class);
		RuneLite.main(args);
	}
}