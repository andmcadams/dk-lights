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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
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
	private static int lamps;

	@Getter
	private DKLightsEnum currentArea;

	@Getter
	private WorldPoint currentPoint;

	private static DKLightsHelper helper;

	// This varbit tells you which lamps are broken based on plane and map square
	private static final int DK_LIGHTS = 4038;

	@Getter
	private static HashSet<LampPoint> brokenLamps;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Startup");
		overlayManager.add(overlayPanel);
		helper = new DKLightsHelper();
		helper.init();
		brokenLamps = new HashSet<>();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Shutdown");
		overlayManager.remove(overlayPanel);
		client.clearHintArrow();
	}

	private static boolean tickFlag = true;

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}
		WorldPoint tempPoint = player.getWorldLocation();
		DKLightsEnum tempArea = helper.determineLocation(tempPoint);
		// Do not do anything if the player is not in Dorgesh-Kaan.
		// This should fix the issue of arrows being removed in places other than DK.
		if (tempArea == DKLightsEnum.BAD_AREA)
		{
			currentArea = tempArea;
			return;
		}

		int tempLamps = client.getVarbitValue(DK_LIGHTS);

		// Because the varbit updates AFTER location change, we should wait a tick if the area
		// changes but the lamp varbit does not.
		// Otherwise, the new area may be updated with the bits from the previous area.
		if (tempArea != currentArea && tempLamps == lamps && tickFlag)
		{
			tickFlag = false;
			return;
		}
		tickFlag = true;

		// If we have changed areas or the lamps varb, we need to reload the overlay.
		if (tempArea != currentArea || tempLamps != lamps)
		{
			currentArea = tempArea;
			ArrayList<LampPoint> lampPoints = helper.getAreaLamps(tempLamps, currentArea);
			for (LampPoint l : lampPoints)
			{
				if (l.isBroken())
				{
					brokenLamps.add(l);
				}
				else
				{
					brokenLamps.remove(l);
				}
			}
		}

		// Point to the closest broken lamp after moving or fixing a lamp
		// Note that tempArea != currentArea => tempPoint != currentPoint
		if (tempPoint != currentPoint || tempLamps != lamps)
		{
			currentPoint = tempPoint;
			lamps = tempLamps;
			if (brokenLamps != null && brokenLamps.size() > 0)
			{
				ArrayList<LampPoint> sortedLamps = helper.sortBrokenLamps(brokenLamps, currentPoint);
				if (!sortedLamps.isEmpty())
				{
					LampPoint closestLamp = sortedLamps.get(0);
					client.clearHintArrow();
					if (currentPoint.getPlane() == closestLamp.getWorldPoint().getPlane())
						client.setHintArrow(closestLamp.getWorldPoint());
				}
			}
			else
			{
				client.clearHintArrow();
			}
		}
	}
}
