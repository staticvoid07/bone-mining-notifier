package com.boneminingnotifier;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Bone Mining Notifier",
	description = "Notifies when a calcified rock next to you depletes, and when the one you are facing respawns",
	tags = {"bone", "mining", "calcified", "varlamore", "notifier", "respawn"}
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

	// Set while the scene is loading, so objects spawned by the load itself
	// aren't mistaken for rocks respawning.
	private boolean sceneLoading;

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

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (sceneLoading)
		{
			return;
		}

		ObjectComposition comp = client.getObjectDefinition(event.getGameObject().getId());
		if (!CALCIFIED_ROCK_NAME.equals(comp.getName()))
		{
			return;
		}

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		WorldPoint playerTile = player.getWorldLocation();
		WorldPoint rockTile = event.getGameObject().getWorldLocation();

		if (isCardinallyAdjacent(playerTile, rockTile)
			&& directionTo(playerTile, rockTile) == new Angle(player.getOrientation()).getNearestDirection())
		{
			notifier.notify(config.respawnNotification(), "Calcified rock respawned in front of you!");
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			sceneLoading = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		sceneLoading = false;
	}

	// Assumes a and b are cardinally adjacent
	private static Direction directionTo(WorldPoint a, WorldPoint b)
	{
		if (b.getX() > a.getX())
		{
			return Direction.EAST;
		}
		if (b.getX() < a.getX())
		{
			return Direction.WEST;
		}
		return b.getY() > a.getY() ? Direction.NORTH : Direction.SOUTH;
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
