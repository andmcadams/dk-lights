package com.dklights.pathfinder;

import com.dklights.enums.Transport;

import lombok.extern.slf4j.Slf4j;

import com.dklights.enums.Direction;
import com.dklights.enums.Lamp;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import net.runelite.api.coords.WorldPoint;

@Slf4j
public class Pathfinder
{
	private static final int MAX_ITERATIONS = 10000;
	private static final int MAX_PATH_LENGTH = 512;

	private final CollisionMap collisionMap;
	private final Map<WorldPoint, List<Transport>> transports;

	public Pathfinder() throws IOException
	{
		SplitFlagMap map = SplitFlagMap.loadFromResources();
		this.collisionMap = new CollisionMap(map);
		this.transports = new HashMap<>();

		for (Transport transport : Transport.values())
		{
			transports.computeIfAbsent(transport.getOrigin(), k -> new ArrayList<>()).add(transport);
		}
	}

	public List<WorldPoint> findNearestPath(WorldPoint start, Set<WorldPoint> targets,
			Map<Lamp, Set<Direction>> lampWallCache)
	{
		if (start == null || targets == null || targets.isEmpty())
		{
			return new ArrayList<>();
		}

		if (targets.contains(start))
		{
			List<WorldPoint> path = new ArrayList<>();
			path.add(start);
			return path;
		}

		Set<WorldPoint> reachableTargets = new HashSet<>();
		Map<WorldPoint, WorldPoint> adjacentToTargetMap = new HashMap<>();

		for (WorldPoint target : targets)
		{
			Lamp targetLamp = getLampFromWorldPoint(target);
			if (targetLamp == null)
				continue;

			Set<Direction> walledDirections = lampWallCache.getOrDefault(targetLamp, Collections.emptySet());

			for (Direction direction : Direction.getCardinalDirections())
			{
				if (walledDirections.contains(direction))
				{
					continue;
				}

				WorldPoint adjacent = new WorldPoint(target.getX() + direction.getX(), target.getY() + direction.getY(),
						target.getPlane());

				if (!getNeighbors(new Node(adjacent)).isEmpty())
				{
					reachableTargets.add(adjacent);
					adjacentToTargetMap.put(adjacent, target);
				}
			}
		}

		if (reachableTargets.isEmpty())
		{
			log.debug("Could not find any reachable tiles adjacent to targets.");
			return new ArrayList<>();
		}

		Queue<WorldPoint> queue = new ArrayDeque<>();
		Set<WorldPoint> visited = new HashSet<>();
		Map<WorldPoint, WorldPoint> parentMap = new HashMap<>();

		queue.add(start);
		visited.add(start);

		int iterations = 0;

		while (!queue.isEmpty() && iterations < MAX_ITERATIONS)
		{
			iterations++;
			WorldPoint current = queue.poll();

			if (reachableTargets.contains(current))
			{
				return reconstructPath(parentMap, start, current);
			}

			List<Node> neighborNodes = collisionMap.getValidNeighbors(new Node(current), transports);

			for (Node neighborNode : neighborNodes)
			{
				WorldPoint neighborPoint = neighborNode.getWorldPoint();

				if (visited.add(neighborPoint))
				{
					parentMap.put(neighborPoint, current);
					queue.add(neighborPoint);
				}
			}
		}

		if (iterations >= MAX_ITERATIONS)
		{
			log.debug("findNearestPath hit MAX_ITERATIONS ({})", MAX_ITERATIONS);
		}

		return new ArrayList<>();
	}

	private List<WorldPoint> reconstructPath(Map<WorldPoint, WorldPoint> parentMap, WorldPoint start, WorldPoint end)
	{
		List<WorldPoint> path = new ArrayList<>();
		WorldPoint current = end;

		while (current != null && !current.equals(start))
		{
			path.add(current);
			current = parentMap.get(current);
		}

		if (current != null && current.equals(start))
		{
			path.add(start);
		}

		Collections.reverse(path);

		if (path.isEmpty() || !path.get(0).equals(start))
		{
			return new ArrayList<>();
		}

		return path;
	}

	private Lamp getLampFromWorldPoint(WorldPoint worldPoint)
	{
		for (Lamp lamp : Lamp.values())
		{
			if (lamp.getWorldPoint().equals(worldPoint))
			{
				return lamp;
			}
		}
		return null;
	}

	public List<WorldPoint> findPath(WorldPoint start, WorldPoint end)
	{
		return findPathInternal(start, end, MAX_ITERATIONS);
	}

	public boolean isTransportLocation(WorldPoint point)
	{
		return transports.containsKey(point);
	}

	public List<Transport> getTransportsAt(WorldPoint point)
	{
		return transports.getOrDefault(point, new ArrayList<>());
	}

	private List<WorldPoint> findPathInternal(WorldPoint start, WorldPoint end, int maxIterations)
	{
		if (start == null || end == null)
		{
			return new ArrayList<>();
		}

		if (start.equals(end))
		{
			List<WorldPoint> path = new ArrayList<>();
			path.add(start);
			return path;
		}

		int roughDistance = Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY());
		if (roughDistance > MAX_PATH_LENGTH)
		{
			log.debug("Pathfinding rejected - too far apart: " + roughDistance + " > " + MAX_PATH_LENGTH);
			return new ArrayList<>();
		}

		boolean crossPlane = start.getPlane() != end.getPlane();

		WorldPoint bestTarget = null;
		int closestDistance = Integer.MAX_VALUE;

		if (!crossPlane)
		{
			for (Direction direction : Direction.getCardinalDirections())
			{
				WorldPoint nearby = new WorldPoint(end.getX() + direction.getX(), end.getY() + direction.getY(),
						end.getPlane());

				List<Node> nearbyNeighbors = getNeighbors(new Node(nearby));
				if (!nearbyNeighbors.isEmpty())
				{
					boolean hasValidConnection = false;

					for (Node neighbor : nearbyNeighbors)
					{
						WorldPoint neighborPoint = neighbor.getWorldPoint();
						if (neighborPoint.getPlane() == nearby.getPlane())
						{
							int distanceFromStart = Math.abs(start.getX() - neighborPoint.getX())
									+ Math.abs(start.getY() - neighborPoint.getY());
							int nearbyDistanceFromStart = Math.abs(start.getX() - nearby.getX())
									+ Math.abs(start.getY() - nearby.getY());

							if (distanceFromStart <= nearbyDistanceFromStart + 2)
							{
								hasValidConnection = true;
								break;
							}
						}
					}

					if (hasValidConnection)
					{
						int distance = Math.abs(start.getX() - nearby.getX()) + Math.abs(start.getY() - nearby.getY());
						if (distance < closestDistance)
						{
							closestDistance = distance;
							bestTarget = nearby;
						}
					}
				}
			}

			if (bestTarget != null)
			{
				end = bestTarget;
			}
			else
			{
				log.debug("DEBUG: No walkable directions found for lamp at " + end);
				return new ArrayList<>();
			}
		}

		PriorityQueue<Node> openSet = new PriorityQueue<>();
		Set<WorldPoint> closedSet = new HashSet<>();
		int iterations = 0;

		Node startNode = new Node(start);
		startNode.setGCost(0);
		startNode.setHCost(calculateHeuristic(start, end));
		startNode.setFCost(startNode.getHCost());
		openSet.add(startNode);

		while (!openSet.isEmpty() && iterations < maxIterations)
		{
			iterations++;

			Node currentNode = openSet.poll();

			if (currentNode.getWorldPoint().equals(end))
			{
				List<WorldPoint> path = currentNode.getPath();
				if (path.size() > MAX_PATH_LENGTH)
				{
					return new ArrayList<>();
				}
				return path;
			}

			if (crossPlane && currentNode.getWorldPoint().getPlane() == end.getPlane())
			{
				int dx = Math.abs(currentNode.getWorldPoint().getX() - end.getX());
				int dy = Math.abs(currentNode.getWorldPoint().getY() - end.getY());
				if (dx <= 1 && dy <= 1)
				{
					List<WorldPoint> path = currentNode.getPath();
					if (path.size() > MAX_PATH_LENGTH)
					{
						return new ArrayList<>();
					}
					return path;
				}
			}

			if (!crossPlane && currentNode.getGCost() <= 3)
			{
				int directDistance = Math.abs(currentNode.getWorldPoint().getX() - end.getX())
						+ Math.abs(currentNode.getWorldPoint().getY() - end.getY());
				if (directDistance <= 1)
				{
					List<Node> currentNeighbors = getNeighbors(currentNode);
					boolean canReachTarget = false;
					for (Node neighbor : currentNeighbors)
					{
						if (neighbor.getWorldPoint().equals(end))
						{
							canReachTarget = true;
							break;
						}
					}

					if (canReachTarget)
					{
						List<WorldPoint> path = currentNode.getPath();
						path.add(end);
						return path;
					}
				}
			}

			closedSet.add(currentNode.getWorldPoint());

			for (Node neighbor : getNeighbors(currentNode))
			{
				if (closedSet.contains(neighbor.getWorldPoint()))
				{
					continue;
				}

				float transportCost = 1;
				if (transports.containsKey(currentNode.getWorldPoint()))
				{
					for (Transport transport : transports.get(currentNode.getWorldPoint()))
					{
						if (transport.getDestination().getPlane() == end.getPlane())
						{
							transportCost = Math.max(transport.getDuration() * 0.9f, 1);
						}
						else
						{
							transportCost = Math.max(transport.getDuration(), 2);
						}
						break;
					}
				}
				else
				{
					WorldPoint current = currentNode.getWorldPoint();
					WorldPoint next = neighbor.getWorldPoint();
					int dx = Math.abs(next.getX() - current.getX());
					int dy = Math.abs(next.getY() - current.getY());

					if (dx == 1 && dy == 1)
					{
						transportCost += 0.01;
					}

					if (current.getPlane() != end.getPlane())
					{
						int closestTransportDistance = Integer.MAX_VALUE;
						for (Map.Entry<WorldPoint, List<Transport>> entry : transports.entrySet())
						{
							WorldPoint transportLoc = entry.getKey();
							if (transportLoc.getPlane() == current.getPlane())
							{
								for (Transport transport : entry.getValue())
								{
									if (transport.getDestination().getPlane() == end.getPlane())
									{
										int tDist = Math.max(Math.abs(current.getX() - transportLoc.getX()),
												Math.abs(current.getY() - transportLoc.getY()));
										closestTransportDistance = Math.min(closestTransportDistance, tDist);
									}
								}
							}
						}

						if (closestTransportDistance != Integer.MAX_VALUE)
						{
							int nextClosestTransportDistance = Integer.MAX_VALUE;
							for (Map.Entry<WorldPoint, List<Transport>> entry : transports.entrySet())
							{
								WorldPoint transportLoc = entry.getKey();
								if (transportLoc.getPlane() == next.getPlane())
								{
									for (Transport transport : entry.getValue())
									{
										if (transport.getDestination().getPlane() == end.getPlane())
										{
											int tDist = Math.max(Math.abs(next.getX() - transportLoc.getX()),
													Math.abs(next.getY() - transportLoc.getY()));
											nextClosestTransportDistance = Math.min(nextClosestTransportDistance,
													tDist);
										}
									}
								}
							}

							if (nextClosestTransportDistance > closestTransportDistance)
							{
								transportCost += 0.1;
							}
						}
					}
				}
				float tentativeGCost = currentNode.getGCost() + transportCost;

				if (tentativeGCost > MAX_PATH_LENGTH)
				{
					continue;
				}

				if (tentativeGCost < neighbor.getGCost() || !openSet.contains(neighbor))
				{
					neighbor.setGCost(tentativeGCost);
					neighbor.setHCost(calculateHeuristic(neighbor.getWorldPoint(), end));
					neighbor.setFCost(neighbor.getGCost() + neighbor.getHCost());
					neighbor.setParent(currentNode);

					if (!openSet.contains(neighbor))
					{
						openSet.add(neighbor);
					}
				}
			}
		}

		if (iterations >= maxIterations)
		{
			log.debug("DEBUG: Pathfinding hit MAX_ITERATIONS (" + maxIterations + ")");
		}
		else
		{
			log.debug("DEBUG: Pathfinding failed - openSet empty after " + iterations + " iterations");
		}
		return new ArrayList<>();
	}

	private List<Node> getNeighbors(Node node)
	{
		List<Node> neighbors = collisionMap.getValidNeighbors(node, transports);
		return neighbors;
	}

	private int calculateHeuristic(WorldPoint from, WorldPoint to)
	{
		int dx = Math.abs(from.getX() - to.getX());
		int dy = Math.abs(from.getY() - to.getY());

		int chebyshevDistance = Math.max(dx, dy);

		if (from.getPlane() != to.getPlane())
		{
			int minTransportDistance = Integer.MAX_VALUE;
			boolean foundRelevantTransport = false;

			for (Map.Entry<WorldPoint, List<Transport>> entry : transports.entrySet())
			{
				WorldPoint transportLocation = entry.getKey();

				for (Transport transport : entry.getValue())
				{
					if (transport.getDestination().getPlane() == to.getPlane()
							|| transport.getOrigin().getPlane() == from.getPlane())
					{

						int transportDx = Math.abs(from.getX() - transportLocation.getX());
						int transportDy = Math.abs(from.getY() - transportLocation.getY());
						int distanceToTransport = Math.max(transportDx, transportDy);

						int destDx = Math.abs(transport.getDestination().getX() - to.getX());
						int destDy = Math.abs(transport.getDestination().getY() - to.getY());
						int distanceFromTransport = Math.max(destDx, destDy);

						int totalEstimatedDistance = distanceToTransport + distanceFromTransport;

						if (totalEstimatedDistance < minTransportDistance)
						{
							minTransportDistance = totalEstimatedDistance;
							foundRelevantTransport = true;
						}
					}
				}
			}

			if (foundRelevantTransport)
			{
				return minTransportDistance;
			}
		}

		return chebyshevDistance;
	}
}