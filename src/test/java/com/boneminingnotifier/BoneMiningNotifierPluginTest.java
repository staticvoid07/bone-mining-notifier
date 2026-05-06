package com.boneminingnotifier;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BoneMiningNotifierPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BoneMiningNotifierPlugin.class);
		RuneLite.main(args);
	}
}
