package com.dklights.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import com.dklights.DKLightsConfig;
import com.dklights.DKLightsConstants;
import com.dklights.DKLightsHelper;
import com.dklights.DKLightsPlugin;
import com.dklights.enums.HighlightType;
import com.dklights.enums.Lamp;
import com.dklights.enums.LampStatus;
import com.dklights.enums.PathDrawStyle;
import com.dklights.enums.TargetType;
import com.dklights.enums.TimerType;
import com.dklights.enums.Transport;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Slf4j
public class DKLightsOverlay extends Overlay
{

	private final Client client;
	private final DKLightsPlugin plugin;
	private final DKLightsConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private DKLightsOverlay(Client client, DKLightsPlugin plugin, DKLightsConfig config,
			ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}

		Set<WorldPoint> pathPoints = new HashSet<>(plugin.getNavigationManager().getShortestPath());

		Map<WorldPoint, WallObject> doorsMap = plugin.getStateManager().getSpawnedDoors();
		Map<WorldPoint, GameObject> stairsMap = plugin.getStateManager().getSpawnedStairs();

		Set<TileObject> pathRenderedObjects = new HashSet<>();

		renderLamps(graphics);

		drawPathToLocation(graphics, pathPoints, pathRenderedObjects, doorsMap, stairsMap);

		if (config.highlightClosedDoors())
		{
			for (WallObject door : doorsMap.values())
			{
				if (door.getPlane() != client.getTopLevelWorldView().getPlane())
				{
					continue;
				}
				if (!pathRenderedObjects.contains(door))
				{
					renderTileObject(door, config.doorHighlightColor(), graphics, config.objectsHighlightStyle());
				}
			}
		}

		if (config.highlightInformativeStairs())
		{
			for (GameObject stair : plugin.getStateManager().getInformativeStairs())
			{
				if (stair.getPlane() != client.getTopLevelWorldView().getPlane())
				{
					continue;
				}
				if (!pathRenderedObjects.contains(stair))
				{
					renderTileObject(stair, config.informativeStairColor(), graphics, config.objectsHighlightStyle());
				}
			}
		}

		if (config.highlightWireMachine() && plugin.getStateManager().getWireMachine() != null)
		{
			GameObject wireMachine = plugin.getStateManager().getWireMachine();
			if (wireMachine.getId() == DKLightsConstants.WIRE_MACHINE_ACTIVE)
			{
				renderTileObject(wireMachine, config.wireMachineHighlightColor(), graphics,
						config.objectsHighlightStyle());
			}
			else
			{
				renderWireTimer(graphics);
			}
		}

		return null;
	}

	private void renderLamps(Graphics2D graphics)
	{
		for (GameObject lampObject : plugin.getStateManager().getSpawnedLamps().values())
		{
			if (lampObject.getPlane() != client.getTopLevelWorldView().getPlane()
					&& !config.highlightOtherPlanesLamps())
			{
				continue;
			}

			Lamp lamp = DKLightsHelper.getLamp(lampObject.getId());
			if (lamp == null)
			{
				continue;
			}

			LampStatus status = plugin.getStateManager().getLampStatuses().getOrDefault(lamp, LampStatus.UNKNOWN);

			Color color;
			if (status == LampStatus.BROKEN && config.highlightBrokenLamps())
			{
				color = config.getBrokenLampColor();
			}
			else if (status == LampStatus.WORKING && config.highlightWorkingLamps())
			{
				color = config.getWorkingLampColor();
			}
			else if (config.highlightAllLamps())
			{
				color = config.getDefaultLampColor();
			}
			else
			{
				continue;
			}

			if (lampObject.getPlane() != client.getTopLevelWorldView().getPlane() && config.darkenOtherPlanesLamps())
			{
				color = color.darker();
			}

			renderTileObject(lampObject, color, graphics, config.lampsHighlightStyle());
		}
	}

	private void renderTileObject(TileObject tileObject, Color color, Graphics2D graphics, HighlightType style)
	{
		switch (style)
		{
		case BORDER:
			modelOutlineRenderer.drawOutline(tileObject, config.borderThickness(), color, config.borderFeather());
			break;
		case CLICKBOX:
			Shape clickbox = tileObject.getClickbox();
			if (clickbox != null)
			{
				Point mousePosition = client.getMouseCanvasPosition();
				if (clickbox.contains(mousePosition.getX(), mousePosition.getY()))
				{
					graphics.setColor(color.darker());
				}
				else
				{
					graphics.setColor(color);
				}
				graphics.draw(clickbox);
				graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
				graphics.fill(clickbox);
			}
			break;
		}
	}

	private void drawPathToLocation(Graphics2D graphics, Set<WorldPoint> pathPoints,
			Set<TileObject> pathRenderedObjects, Map<WorldPoint, WallObject> doorsMap,
			Map<WorldPoint, GameObject> stairsMap)
	{
		if (!config.showPathToLocation())
		{
			return;
		}

		List<WorldPoint> path = plugin.getNavigationManager().getShortestPath();
		if (path == null || path.isEmpty())
		{
			return;
		}

		TargetType targetType = plugin.getNavigationManager().getCurrentTargetType();
		boolean isUtilityTarget = targetType == TargetType.BANK || targetType == TargetType.WIRING_MACHINE;

		Set<Transport> activeTransports = new HashSet<>();
		Map<WorldPoint, Color> pathPointColors = new HashMap<>();
		PathDrawStyle style = config.pathDrawStyle();
		Point prevScreenPoint = null;
		boolean isAfterClosedDoor = false;

		for (WorldPoint point : path)
		{
			if (point.getPlane() != client.getTopLevelWorldView().getPlane())
			{
				prevScreenPoint = null;
				continue;
			}

			LocalPoint localPoint = LocalPoint.fromWorld(client.getTopLevelWorldView(), point);
			if (localPoint == null)
			{
				prevScreenPoint = null;
				continue;
			}

			Color pathColor = isUtilityTarget ? config.utilityPathColor() : config.pathColor();
			if (!isUtilityTarget && config.maxPathDistance() > 0 && path.size() > config.maxPathDistance())
			{
				long phase = (System.currentTimeMillis() / 600) % 2;
				pathColor = phase == 0 ? pathColor.darker() : pathColor.brighter();
			}

			if (isAfterClosedDoor)
			{
				pathColor = pathColor.darker().darker();
			}

			pathPointColors.put(point, pathColor);

			switch (style)
			{
			case TILES:
				Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
				if (poly != null)
				{
					OverlayUtil.renderPolygon(graphics, poly, pathColor);
				}
				break;

			case TILE_BORDERS:
				Polygon borderPoly = Perspective.getCanvasTilePoly(client, localPoint);
				if (borderPoly != null)
				{
					graphics.setColor(pathColor);
					graphics.draw(borderPoly);
				}
				break;

			case CENTER_LINE:
				Point screenPoint = Perspective.localToCanvas(client, localPoint,
						client.getTopLevelWorldView().getPlane());
				if (screenPoint == null)
				{
					continue;
				}
				if (prevScreenPoint != null)
				{
					graphics.setColor(pathColor);
					graphics.setStroke(new BasicStroke(2));
					graphics.drawLine(prevScreenPoint.getX(), prevScreenPoint.getY(), screenPoint.getX(),
							screenPoint.getY());
				}
				prevScreenPoint = screenPoint;
				break;
			}

			WallObject door = doorsMap.get(point);
			if (door != null)
			{
				renderTileObject(door, pathColor, graphics, config.objectsHighlightStyle());
				pathRenderedObjects.add(door);
				isAfterClosedDoor = true;
			}

			List<Transport> transports = plugin.getPathfinder().getTransportsAt(point);
			for (Transport transport : transports)
			{
				if (pathPoints.contains(transport.getOrigin()) && pathPoints.contains(transport.getDestination()))
				{
					activeTransports.add(transport);
				}
			}
		}

		highlightTransportsOnPath(graphics, activeTransports, pathPointColors, pathRenderedObjects, stairsMap);	
	}

	private void highlightTransportsOnPath(Graphics2D graphics, Set<Transport> activeTransports,
			Map<WorldPoint, Color> pathPointColors, Set<TileObject> pathRenderedObjects, Map<WorldPoint, GameObject> stairsMap)
	{

		if (activeTransports.isEmpty())
		{
			return;
		}

		for (GameObject stair : stairsMap.values())
		{
			if (stair.getPlane() != client.getTopLevelWorldView().getPlane())
			{
				continue;
			}

			WorldPoint stairLocation = stair.getWorldLocation();
			Transport transport = transportBetweenTransportPoints(stairLocation, activeTransports);
			if (transport != null)
			{
				Color highlightColor = pathPointColors.get(transport.getOrigin());

				renderTileObject(stair, highlightColor, graphics, config.objectsHighlightStyle());
				pathRenderedObjects.add(stair);
			}
		}

	}

	private Transport transportBetweenTransportPoints(WorldPoint objectLocation, Set<Transport> activeTransports)
	{
		for (Transport transport : activeTransports)
		{
			if (DKLightsHelper.isLocationBetweenTransportPoints(objectLocation, transport))
			{
				return transport;
			}
		}
		return null;
	}

	private void renderWireTimer(Graphics2D graphics)
	{
		int respawnTick = plugin.getStateManager().getWireRespawnTick();

		if (respawnTick == -1 || plugin.getStateManager().getWireMachine() == null)
		{
			return;
		}

		int currentTick = client.getTickCount();
		int ticksRemaining = respawnTick - currentTick;

		Instant now = Instant.now();
		Instant lastTick = plugin.getLastTickInstant();
		long millisSinceLastTick = Duration.between(lastTick, now).toMillis();

		millisSinceLastTick = Math.max(0, Math.min(millisSinceLastTick, 600));

		double tickFractionPassed = millisSinceLastTick / 600.0;

		double smoothTicksRemaining = Math.max(0.0, (double) ticksRemaining - tickFractionPassed);

		if (smoothTicksRemaining <= 0)
		{
			return;
		}

		double progress = smoothTicksRemaining / DKLightsConstants.WIRE_RESPAWN_TICKS;

		LocalPoint lp = plugin.getStateManager().getWireMachine().getLocalLocation();
		Point point = net.runelite.api.Perspective.getCanvasTextLocation(client, graphics, lp, " ", 0);

		if (point == null)
		{
			return;
		}

		if (config.timerType() == TimerType.PIE)
		{
			ProgressPieComponent pie = new ProgressPieComponent();
			pie.setPosition(point);
			pie.setBorderColor(config.wireMachineHighlightColor());
			pie.setDiameter(20);
			pie.setFill(config.wireMachineHighlightColor());
			// Use the smooth progress value
			pie.setProgress(progress);
			pie.render(graphics);
		}
		else if (config.timerType() == TimerType.TICKS)
		{
			// Ticks should still likely show the discrete value
			String text = String.valueOf(ticksRemaining);
			OverlayUtil.renderTextLocation(graphics, point, text, Color.WHITE);
		}
		else if (config.timerType() == TimerType.SECONDS)
		{
			// Calculate seconds based on the smooth value
			double seconds = smoothTicksRemaining * 0.6;
			String text = String.format("%.1f", seconds);
			OverlayUtil.renderTextLocation(graphics, point, text, Color.WHITE);
		}
	}
}
