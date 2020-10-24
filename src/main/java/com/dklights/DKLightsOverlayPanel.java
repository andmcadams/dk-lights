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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

public class DKLightsOverlayPanel extends OverlayPanel
{

	private final DKLightsPlugin plugin;

	@Inject
	private DKLightsOverlayPanel(DKLightsPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;

		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.HIGH);
	}

	private void addTextToOverlayPanel(String text)
	{
		panelComponent.getChildren().add(LineComponent.builder().left(text).build());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		HashSet<LampPoint> areaLampPoints = plugin.getBrokenLamps();

		panelComponent.getChildren().clear();

		boolean addedText = false;
		String[] areaNames = {"North Ground Floor", "South Ground Floor", "North Second Floor", "South Second Floor", "North Third Floor", "South Third Floor"};
		if (areaLampPoints != null && areaLampPoints.size() != 10)
		{
			addTextToOverlayPanel("Unknown lights: " + (10 - areaLampPoints.size()));
		}
		for (int i = 0; i < DKLightsEnum.BAD_AREA.value; i++)
		{
			LinkedHashMap<String, Integer> descriptionCount = new LinkedHashMap<>();
			for (LampPoint l : areaLampPoints)
			{
				if (l.getArea().value != i)
				{
					continue;
				}

				if (!descriptionCount.containsKey(l.getDescription()))
				{
					descriptionCount.put(l.getDescription(), 1);
				}
				else
				{
					descriptionCount.put(l.getDescription(), descriptionCount.get(l.getDescription()) + 1);
				}
			}

			if (descriptionCount.size() != 0)
			{
				addTextToOverlayPanel(areaNames[i]);
			}
			for (String s : descriptionCount.keySet())
			{
				String num = " (x" + descriptionCount.get(s) + ")";
				if (descriptionCount.get(s) == 1)
				{
					num = "";
				}
				addTextToOverlayPanel("* " + s + num);
				addedText = true;
			}
		}

		if (!addedText)
		{
			addTextToOverlayPanel("No broken lamps in this area");
		}


		return super.render(graphics);
	}
}
