package com.dklights.overlay;

import com.dklights.DKLightsConfig;
import com.dklights.DKLightsHelper;
import com.dklights.DKLightsPlugin;
import com.dklights.enums.Area;
import com.dklights.enums.TargetType;

import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.LineComponent.LineComponentBuilder;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
public class StatsOverlay extends OverlayPanel
{
	private final DKLightsPlugin plugin;
	private final DKLightsConfig config;
	private final Client client;

	@Inject
	private StatsOverlay(DKLightsPlugin plugin, DKLightsConfig config, Client client)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showStatsOverlay())
		{
			return null;
		}

		// Only show overlay when in Dorgesh-Kaan region
		if (client.getLocalPlayer() == null)
		{
			return null;
		}

		Area currentArea = DKLightsHelper.getArea(client.getLocalPlayer().getWorldLocation());
		if (currentArea == null)
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder().text("Dorgesh-Kaan Lights").color(Color.CYAN).build());

		panelComponent.getChildren()
				.add(LineComponent.builder().left("Target:")
						.right(plugin.getNavigationManager().getCurrentTargetType().getDisplayName())
						.rightColor(config.pathColor()).build());

		if (config.showClosestDistance() && config.showPathToLocation() && plugin.getNavigationManager().getCurrentTargetType() != TargetType.NONE)
		{
			int closestDist = plugin.getNavigationManager().getClosestDistance();

			String distText = (closestDist == 0) ? "---" : (String.valueOf(closestDist) + " tiles");
			LineComponentBuilder line = LineComponent.builder().left("Distance:").right(distText);

			if ((closestDist > config.maxPathDistance() || closestDist == 0)
					&& plugin.getNavigationManager().getCurrentTargetType() == TargetType.LAMP)
			{
				boolean blinkOn = (System.currentTimeMillis() / 600) % 2 == 0;
				line.rightColor(blinkOn ? Color.RED : Color.WHITE);
			}

			panelComponent.getChildren().add(line.build());
		}

		int lampsFixed = plugin.getStatsTracker().getLampsFixed();
		if (lampsFixed > 0)
		{
			panelComponent.getChildren()
					.add(LineComponent.builder().left("Lamps fixed:").right(Integer.toString(lampsFixed)).build());

			int lampsPerHr = plugin.getStatsTracker().getLampsPerHr();
			if (lampsPerHr > 0)
			{
				panelComponent.getChildren()
						.add(LineComponent.builder().left("Lamps/hr:").right(Integer.toString(lampsPerHr)).build());
			}
		}

		panelComponent.getChildren().add(LineComponent.builder().left("Total Fixed:")
				.right(String.valueOf(plugin.getStatsTracker().getTotalLampsFixed())).build());

		return super.render(graphics);
	}
}