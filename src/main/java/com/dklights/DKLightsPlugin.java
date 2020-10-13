package com.dklights;

import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Dorgesh-Kaan Lights"
)
public class DKLightsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private DKLightsOverlayPanel overlayPanel;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private int lamps;

	@Getter
	private DKLightsEnum currentArea;

	private static DKLightsHelper helper;
	private static final int DK_LIGHTS = 4038;

	@Override
	protected void startUp() throws Exception
	{
		log.info("DKLights started!");
		overlayManager.add(overlayPanel);
		helper = new DKLightsHelper();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("DKLights stopped!");
		overlayManager.remove(overlayPanel);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		DKLightsEnum tempArea = helper.determineLocation(client);
		int tempLamps = client.getVarbitValue(DK_LIGHTS);

		// If we have changed areas or the lamps varb, we need to reload the overlay.
		if (tempArea != currentArea || tempLamps != lamps)
		{
			currentArea = tempArea;
			lamps = tempLamps;
			helper.findBrokenLamps(lamps, currentArea);
		}
	}

}
