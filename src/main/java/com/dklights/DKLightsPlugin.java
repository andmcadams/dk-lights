/*
 * Copyright (c) 2020, andmcadams
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.dklights;

import com.dklights.enums.InventoryState;
import com.dklights.overlay.DKLightsOverlay;
import com.dklights.overlay.StatsOverlay;
import com.dklights.overlay.TeleportOverlay;
import com.dklights.overlay.LegacyOverlay;
import com.dklights.pathfinder.Pathfinder;
import com.google.inject.Provides;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Dorgesh-Kaan Lights",
    description="Makes it easier to find broken lamps in Dorgesh-Kaan.",
    tags={"dorgesh","lamps","lights","kaan","dorgesh-kaan"}
)
public class DKLightsPlugin extends Plugin
{

	@Inject
	@Getter
	private Client client;

	@Inject
	@Getter
	private DKLightsConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private DKLightsOverlay overlay;

	@Inject
	private TeleportOverlay teleportOverlay;

	@Inject
	private StatsOverlay statsOverlay;

	@Inject
	private LegacyOverlay legacyOverlay;

    @Getter
	private DKLightsNavigationManager navigationManager;
	@Getter
	private DKLightsStatsTracker statsTracker;
	@Getter
	private DKLightsStateManager stateManager;

	private ExecutorService pathfindingExecutor;

	@Getter
	private Pathfinder pathfinder;

	@Getter
	private Instant lastTickInstant = Instant.now();

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		overlayManager.add(teleportOverlay);
		overlayManager.add(statsOverlay);
		overlayManager.add(legacyOverlay);

		statsTracker = new DKLightsStatsTracker();
		stateManager = new DKLightsStateManager(client, statsTracker);

		pathfindingExecutor = Executors.newSingleThreadExecutor(r ->
		{
			Thread t = new Thread(r, "DKLights-Pathfinder");
			t.setDaemon(true);
			return t;
		});

		try
		{
			pathfinder = new Pathfinder();
		}
		catch (IOException e)
		{
			log.error("Failed to load pathfinder collision data", e);
			return;
		}

		navigationManager = new DKLightsNavigationManager(client, config, pathfinder, pathfindingExecutor);

	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Dorgesh-Kaan Lights stopped!");
		overlayManager.remove(overlay);
		overlayManager.remove(teleportOverlay);
		overlayManager.remove(statsOverlay);
		overlayManager.remove(legacyOverlay);

		navigationManager.shutDown();

		if (stateManager != null)
		{
			stateManager.shutDown();
		}
		if (navigationManager != null)
		{
			navigationManager.shutDown();
		}
		if (pathfindingExecutor != null)
		{
			pathfindingExecutor.shutdown();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		stateManager.onGameObjectSpawned(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		stateManager.onGameObjectDespawned(event.getGameObject());
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		stateManager.onWallObjectSpawned(event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event)
	{
		stateManager.onWallObjectDespawned(event.getWallObject());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		stateManager.onGameStateChanged(gameStateChanged.getGameState());
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		stateManager.onChatMessage(chatMessage);
		statsTracker.onChatMessage(chatMessage);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		lastTickInstant = Instant.now();

		if (client.getLocalPlayer() == null)
		{
			return;
		}

		stateManager.onGameTick();

		if (stateManager.getCurrentArea() == null)
		{
			if (navigationManager != null)
			{
				navigationManager.clearPathAndTarget();
			}
			return;
		}

		InventoryState inventoryState = InventoryState.NO_LIGHT_BULBS.getInventoryState(client);
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		navigationManager.update(stateManager.getLampStatuses(), stateManager.getLampWallCache(), inventoryState,
				playerLocation, stateManager.getWireMachine());

		client.clearHintArrow();
	}

	@Provides
	DKLightsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DKLightsConfig.class);
	}
}

