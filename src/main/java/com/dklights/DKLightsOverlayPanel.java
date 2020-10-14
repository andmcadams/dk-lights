package com.dklights;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
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

		for(LampPoint l : lampPoints)
		{
			addTextToOverlayPanel(l.getDescription());
		}


		return super.render(graphics);
	}
}
