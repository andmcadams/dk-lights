package com.dklights.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import javax.inject.Inject;

import com.dklights.DKLightsConfig;
import com.dklights.DKLightsConstants;
import com.dklights.DKLightsPlugin;
import com.dklights.enums.TargetType;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class TeleportOverlay extends WidgetItemOverlay
{
	private final DKLightsPlugin plugin;
	private final DKLightsConfig config;

	@Inject
	private TeleportOverlay(DKLightsPlugin plugin, DKLightsConfig config, ItemManager itemManager)
	{
		this.plugin = plugin;
		this.config = config;
		showOnInventory();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		if (itemId != DKLightsConstants.TELEPORT_SPHERE_ID)
		{
			return;
		}

        int closestDist = plugin.getNavigationManager().getClosestDistance();

		if ((closestDist > config.maxPathDistance() || closestDist == 0)
				&& plugin.getNavigationManager().getCurrentTargetType() == TargetType.LAMP)
		{
			Rectangle bounds = itemWidget.getCanvasBounds();
			Color oldColor = graphics.getColor();

			Color base = config.pathColor();

			long phase = (System.currentTimeMillis() / 600) % 2;
			int alpha = (phase == 0) ? 64 : 192;

			graphics.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
			graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

			graphics.setColor(oldColor);
		}
	}
}