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
		description = "Notify when a calcified rock depletes next to you",
		position = 0
	)
	default Notification notification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "respawnNotification",
		name = "Rock respawn notification",
		description = "Notify when the calcified rock you are facing becomes minable again",
		position = 1
	)
	default Notification respawnNotification()
	{
		return Notification.ON;
	}
}
