package com.dklights;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
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

		ArrayList<LampPoint> lampPoints = plugin.getLampPoints();

		panelComponent.getChildren().clear();

		HashMap<String, Integer> descriptionCount = new HashMap<>();
		for(LampPoint l : lampPoints)
		{
			if (!descriptionCount.containsKey(l.getDescription()))
				descriptionCount.put(l.getDescription(), 1);
			else
				descriptionCount.put(l.getDescription(), descriptionCount.get(l.getDescription())+1);
		}

		for (String s : descriptionCount.keySet())
		{
			String num = " (x" + descriptionCount.get(s) + ")";
			if (descriptionCount.get(s) == 1)
				num = "";
			addTextToOverlayPanel(s + num);
		}


		return super.render(graphics);
	}
}
