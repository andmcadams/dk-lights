package com.dklights;

import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
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

	@Getter
	private WorldPoint currentPoint;

	private static DKLightsHelper helper;
	private static final int DK_LIGHTS = 4038;

	@Getter
	private static ArrayList<LampPoint> lampPoints;

	@Override
	protected void startUp() throws Exception
	{
		log.info("DKLights started!");
		overlayManager.add(overlayPanel);
		helper = new DKLightsHelper();
		helper.init();
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
		Player player = client.getLocalPlayer();
		if (player == null)
			return;
		WorldPoint tempPoint = player.getWorldLocation();
		DKLightsEnum tempArea = helper.determineLocation(currentPoint);
		int tempLamps = client.getVarbitValue(DK_LIGHTS);

		// If we have changed areas or the lamps varb, we need to reload the overlay.
		if (tempArea != currentArea || tempLamps != lamps)
		{
			currentArea = tempArea;
			if (tempArea == DKLightsEnum.BAD_AREA)
				return;
			lampPoints = helper.findBrokenLamps(tempLamps, currentArea);
		}

		// Point to the closest broken lamp after moving or fixing a lamp
		// Note that tempArea != currentArea => tempPoint != currentPoint
		if (tempPoint != currentPoint || tempLamps != lamps)
		{
			currentPoint = tempPoint;
			lamps = tempLamps;
			if (lampPoints.size() > 0)
			{
				LampPoint closestLamp = helper.sortBrokenLamps(lampPoints, currentPoint).get(0);
				client.clearHintArrow();
				client.setHintArrow(closestLamp.getWorldPoint());
			}
			else
				client.clearHintArrow();
		}
	}

}
