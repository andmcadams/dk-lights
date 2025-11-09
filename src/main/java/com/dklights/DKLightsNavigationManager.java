package com.dklights;

import com.dklights.enums.Direction;
import com.dklights.enums.InventoryState;
import com.dklights.enums.Lamp;
import com.dklights.enums.LampStatus;
import com.dklights.enums.TargetType;
import com.dklights.pathfinder.Pathfinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

@Slf4j
public class DKLightsNavigationManager
{

	private final Client client;
	private final DKLightsConfig config;
	private final Pathfinder pathfinder;
	private final ExecutorService pathfindingExecutor;

	@Getter
	private List<WorldPoint> shortestPath = new ArrayList<>();
	@Getter
	private int closestDistance = 0;
	@Getter
	private TargetType currentTargetType = TargetType.NONE;

	private Lamp lastLoggedClosestLamp = null;
	private CompletableFuture<Void> currentClosestLampTask;
	private long lastClosestLampCalculation = 0;
	private static final long CLOSEST_LAMP_COOLDOWN_MS = 600;

	private final Set<Lamp> brokenLamps = new HashSet<>();

	public DKLightsNavigationManager(Client client, DKLightsConfig config, Pathfinder pathfinder,
			ExecutorService pathfindingExecutor)
	{
		this.client = client;
		this.config = config;
		this.pathfinder = pathfinder;
		this.pathfindingExecutor = pathfindingExecutor;
	}

	public void update(Map<Lamp, LampStatus> lampStatuses, Map<Lamp, Set<Direction>> lampWallCache,
			InventoryState inventoryState, WorldPoint playerLocation, GameObject wireMachine)
	{

		if (playerLocation == null || pathfinder == null || pathfindingExecutor == null)
		{
			return;
		}

		WorldPoint targetLocation;

		switch (inventoryState)
		{
		case NO_LIGHT_BULBS:
			targetLocation = DKLightsConstants.BANK_LOCATION;
			currentTargetType = TargetType.BANK;
			break;

		case ONLY_EMPTY_BULBS:
			targetLocation = DKLightsConstants.WIRE_MACHINE_LOCATION;
			currentTargetType = TargetType.WIRING_MACHINE;
			break;

		case HAS_WORKING_BULBS:
            targetLocation = null;
			currentTargetType = TargetType.LAMP;
            break;

		default:
			currentTargetType = TargetType.NONE;
			shortestPath.clear();
			return;
		}

        if (!config.showPathToLocation())
		{
			if (!shortestPath.isEmpty())
			{
				shortestPath.clear();
			}
			if (closestDistance != 0)
			{
				closestDistance = 0;
			}
			if (currentClosestLampTask != null && !currentClosestLampTask.isDone())
			{
				currentClosestLampTask.cancel(true);
			}
			return;
		}

		if (targetLocation != null)
		{
			calculatePathToTarget(targetLocation, currentTargetType, playerLocation);
		}
        else
        {
			findClosestBrokenLamp(lampStatuses, lampWallCache, playerLocation);
        }
	}

	public void shutDown()
	{
		if (currentClosestLampTask != null)
		{
			currentClosestLampTask.cancel(true);
		}
	}

	public void clearPathAndTarget()
	{
		shortestPath.clear();
		closestDistance = 0;
		currentTargetType = TargetType.NONE;
		if (currentClosestLampTask != null && !currentClosestLampTask.isDone())
		{
			currentClosestLampTask.cancel(true);
		}
	}

	private void findClosestBrokenLamp(Map<Lamp, LampStatus> lampStatuses, Map<Lamp, Set<Direction>> lampWallCache,
			WorldPoint playerLocation)
	{
		brokenLamps.clear();
		for (Map.Entry<Lamp, LampStatus> entry : lampStatuses.entrySet())
		{
			if (entry.getValue() == LampStatus.BROKEN)
			{
				brokenLamps.add(entry.getKey());
			}
		}
		final Set<Lamp> lampsToCheck = new HashSet<>(brokenLamps);

		if (lampsToCheck.isEmpty())
		{
			if (lastLoggedClosestLamp != null)
			{
				log.info("No broken lamps found");
				lastLoggedClosestLamp = null;
			}
			shortestPath.clear();
			closestDistance = 0;
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastClosestLampCalculation < CLOSEST_LAMP_COOLDOWN_MS)
		{
			return;
		}
		lastClosestLampCalculation = currentTime;

		if (currentClosestLampTask != null && !currentClosestLampTask.isDone())
		{
			currentClosestLampTask.cancel(true);
		}

		final WorldPoint playerPos = playerLocation;

		currentClosestLampTask = CompletableFuture.runAsync(() ->
		{
			try
			{
				final Set<WorldPoint> brokenLampLocations = lampsToCheck.stream().map(Lamp::getWorldPoint)
						.collect(Collectors.toSet());

				if (Thread.currentThread().isInterrupted())
				{
					return;
				}

				List<WorldPoint> path = pathfinder.findNearestPath(playerPos, brokenLampLocations, lampWallCache);

				if (Thread.currentThread().isInterrupted())
				{
					return;
				}

				if (path != null && !path.isEmpty())
				{
					final List<WorldPoint> finalPath = new ArrayList<>(path);
					final int finalDistance = finalPath.size();
					final WorldPoint destination = finalPath.get(finalPath.size() - 1);

					final Lamp finalClosestLamp = lampsToCheck.stream()
							.filter(lamp -> lamp.getWorldPoint().equals(destination)).findFirst().orElse(null);

					shortestPath = finalPath;
					closestDistance = finalDistance;

					if (finalClosestLamp != null && !finalClosestLamp.equals(lastLoggedClosestLamp))
					{
						lastLoggedClosestLamp = finalClosestLamp;
					}
				}
				else
				{
					shortestPath.clear();
					closestDistance = 0;
					if (lastLoggedClosestLamp != null)
					{
						lastLoggedClosestLamp = null;
					}
				}
			}
			catch (Exception e)
			{
				log.error("Error during closest lamp calculation (BFS)", e);
				shortestPath.clear();
				closestDistance = 0;
				lastLoggedClosestLamp = null;
			}
		}, pathfindingExecutor).exceptionally(throwable ->
		{
			if (!(throwable instanceof java.util.concurrent.CancellationException))
			{
				log.error("Closest lamp calculation (BFS) failed", throwable);
			}
			return null;
		});
	}

	private void calculatePathToTarget(WorldPoint targetLocation, TargetType targetType, WorldPoint playerLocation)
	{
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastClosestLampCalculation < CLOSEST_LAMP_COOLDOWN_MS)
		{
			return;
		}
		lastClosestLampCalculation = currentTime;

		if (currentClosestLampTask != null && !currentClosestLampTask.isDone())
		{
			currentClosestLampTask.cancel(true);
		}

		final WorldPoint playerPos = playerLocation;
		final WorldPoint target = targetLocation;

		if (DKLightsHelper.isInBankArea(playerLocation) && targetType == TargetType.BANK)
		{
			shortestPath.clear();
			return;
		}

		currentClosestLampTask = CompletableFuture.runAsync(() ->
		{
			try
			{
				if (Thread.currentThread().isInterrupted())
				{
					return;
				}

				List<WorldPoint> path = pathfinder.findPath(playerPos, target);

				if (!Thread.currentThread().isInterrupted())
				{
					if (path != null && !path.isEmpty())
					{
						shortestPath = new ArrayList<>(path);
						closestDistance = path.size();
					}
					else
					{
						shortestPath = new ArrayList<>();
						shortestPath.add(playerPos);
						shortestPath.add(target);
						closestDistance = target.distanceTo(playerPos);
					}
				}
			}
			catch (Exception e)
			{
				log.error("Error calculating path to {}: {}", targetType, e.getMessage());
				if (!Thread.currentThread().isInterrupted())
				{
					shortestPath = new ArrayList<>();
					shortestPath.add(playerPos);
					shortestPath.add(target);
					closestDistance = target.distanceTo(playerPos);
				}
			}
		}, pathfindingExecutor).exceptionally(throwable ->
		{
			if (!(throwable instanceof java.util.concurrent.CancellationException))
			{
				log.error("Path calculation to {} failed", targetType, throwable);
			}
			return null;
		});
	}
}
