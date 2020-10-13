package com.dklights;

import java.awt.Dimension;
import java.awt.Graphics2D;
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

		int lamps = plugin.getLamps();
		String lampBitString = Integer.toString(lamps, 2);

		for(int i = lampBitString.length()-1; i >= 0; i--)
		{
			addTextToOverlayPanel(lampBitString.length()-1-i + ": " + lampBitString.charAt(i));
		}


		return super.render(graphics);
	}
}
