package com.boneminingnotifier;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ObjectComposition;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Bone Mining Notifier",
	description = "Notifies when a calcified rock depletes in a cardinal direction adjacent to you",
	tags = {"bone", "mining", "calcified", "varlamore", "notifier"}
)
public class BoneMiningNotifierPlugin extends Plugin
{
	private static final String CALCIFIED_ROCK_NAME = "Calcified rocks";

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private BoneMiningNotifierConfig config;

	@Override
	protected void startUp()
	{
		log.debug("Bone Mining Notifier started!");
	}

	@Override
	protected void shutDown()
	{
		log.debug("Bone Mining Notifier stopped!");
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		ObjectComposition comp = client.getObjectDefinition(event.getGameObject().getId());
		if (!CALCIFIED_ROCK_NAME.equals(comp.getName()))
		{
			return;
		}

		if (client.getLocalPlayer() == null)
		{
			return;
		}

		WorldPoint playerTile = client.getLocalPlayer().getWorldLocation();
		WorldPoint rockTile = event.getGameObject().getWorldLocation();

		if (isCardinallyAdjacent(playerTile, rockTile))
		{
			notifier.notify(config.notification(), "Calcified rock depleted next to you!");
		}
	}

	private static boolean isCardinallyAdjacent(WorldPoint a, WorldPoint b)
	{
		if (a.getPlane() != b.getPlane())
		{
			return false;
		}
		int dx = Math.abs(a.getX() - b.getX());
		int dy = Math.abs(a.getY() - b.getY());
		return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
	}

	@Provides
	BoneMiningNotifierConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BoneMiningNotifierConfig.class);
	}
}
