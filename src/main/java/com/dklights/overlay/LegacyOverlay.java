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
package com.dklights.overlay;

import com.dklights.DKLightsConfig;
import com.dklights.DKLightsPlugin;
import com.dklights.enums.Area;
import com.dklights.enums.Lamp;
import com.dklights.enums.LampStatus;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class LegacyOverlay extends OverlayPanel
{

	private final DKLightsPlugin plugin;
    private final DKLightsConfig config;

	@Inject
	private LegacyOverlay(DKLightsPlugin plugin, DKLightsConfig config)
	{
		super(plugin);
        this.config = config;
		this.plugin = plugin;

		setPosition(OverlayPosition.TOP_LEFT);
	}

	private void addTextToOverlayPanel(String text)
	{
		panelComponent.getChildren().add(LineComponent.builder().left(text).build());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
        if (!config.showLegacyOverlay())
        {
            return null;
        }

		Area currentArea = plugin.getStateManager().getCurrentArea();
        if (currentArea == null)
		{
			return null;
		}
        
		panelComponent.getChildren().clear();
        
		Map<Lamp, LampStatus> allStatuses = plugin.getStateManager().getLampStatuses();
		int unknownCount = 0;

		boolean addedText = false;

		for (Area area : Area.values())
		{
			LinkedHashMap<String, Integer> descriptionCount = new LinkedHashMap<>();
			for (Map.Entry<Lamp, LampStatus> entry : allStatuses.entrySet())
            {
                Lamp l = entry.getKey();
                LampStatus status = entry.getValue();

                if (status == LampStatus.UNKNOWN)
                {
                    unknownCount++;
                    continue;
                }

                if (l.getArea() != area || status != LampStatus.BROKEN)
				{
					continue;
				}
            }

			if (!descriptionCount.isEmpty())
			{
				addTextToOverlayPanel(area.getName());
				for (Map.Entry<String, Integer> descEntry : descriptionCount.entrySet())
				{
					String num = descEntry.getValue() == 1 ? "" : " (x" + descEntry.getValue() + ")";
					addTextToOverlayPanel("* " + descEntry.getKey() + num);
					addedText = true;
				}
			}
		}
        
        if (unknownCount > 0)
        {
            panelComponent.getChildren().add(0, LineComponent.builder().left("Unknown lamps: " + unknownCount).build());
        }

		if (!addedText)
		{
			addTextToOverlayPanel("No broken lamps in this area");
		}


		return super.render(graphics);
	}
}
