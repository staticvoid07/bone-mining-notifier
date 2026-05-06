package com.boneminingnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;

@ConfigGroup("boneminingnotifier")
public interface BoneMiningNotifierConfig extends Config
{
	@ConfigItem(
		keyName = "notification",
		name = "Bone mining notification",
		description = "Notify when a calcified rock depletes next to you"
	)
	default Notification notification()
	{
		return Notification.ON;
	}
}
