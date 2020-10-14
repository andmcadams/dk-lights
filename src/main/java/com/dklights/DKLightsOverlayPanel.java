package com.dklights;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

public class DKLightsOverlayPanel extends OverlayPanel
{

	private final DKLightsPlugin plugin;

	@Inject
	private DKLightsOverlayPanel(DKLightsPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;

		setLayer(OverlayLayer.ALWAYS_ON_TOP);
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
		String[] areaNames = {"P0N", "P0S", "P1N", "P1S", "P2N", "P2S"};
		for (int i = 0; i < DKLightsEnum.BAD_AREA.value; i++)
		{
			HashMap<String, Integer> descriptionCount = new HashMap<>();
			for (LampPoint l : areaLampPoints)
			{
				if (l.getArea().value != i)
					continue;

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
				addTextToOverlayPanel(areaNames[i]);
			for (String s : descriptionCount.keySet())
			{
				String num = " (x" + descriptionCount.get(s) + ")";
				if (descriptionCount.get(s) == 1)
				{
					num = "";
				}
				addTextToOverlayPanel(s + num);
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
