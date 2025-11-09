package com.dklights.pathfinder;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import com.dklights.enums.Transport;
import com.dklights.enums.Direction;

public class CollisionMap
{

	private static final java.util.List<Direction> ORDINAL_VALUES = java.util.Arrays.asList(Direction.values());

	private final SplitFlagMap collisionData;

	public byte[] getPlanes()
	{
		return collisionData.getRegionMapPlaneCounts();
	}

	public CollisionMap(SplitFlagMap collisionData)
	{
		this.collisionData = collisionData;
	}

	private boolean get(int x, int y, int z, int flag)
	{
		return collisionData.get(x, y, z, flag);
	}

	public boolean n(int x, int y, int z)
	{
		return get(x, y, z, 0);
	}

	public boolean s(int x, int y, int z)
	{
		return n(x, y - 1, z);
	}

	public boolean e(int x, int y, int z)
	{
		return get(x, y, z, 1);
	}

	public boolean w(int x, int y, int z)
	{
		return e(x - 1, y, z);
	}

	private boolean ne(int x, int y, int z)
	{
		return n(x, y, z) && e(x, y + 1, z) && e(x, y, z) && n(x + 1, y, z);
	}

	private boolean nw(int x, int y, int z)
	{
		return n(x, y, z) && w(x, y + 1, z) && w(x, y, z) && n(x - 1, y, z);
	}

	private boolean se(int x, int y, int z)
	{
		return s(x, y, z) && e(x, y - 1, z) && e(x, y, z) && s(x + 1, y, z);
	}

	private boolean sw(int x, int y, int z)
	{
		return s(x, y, z) && w(x, y - 1, z) && w(x, y, z) && s(x - 1, y, z);
	}

	public boolean isBlocked(int x, int y, int z)
	{
		return !n(x, y, z) && !s(x, y, z) && !e(x, y, z) && !w(x, y, z);
	}

	public String getCollisionFlags(WorldPoint point)
	{
		int x = point.getX();
		int y = point.getY();
		int z = point.getPlane();
		return "N: " + n(x, y, z) + ", S: " + s(x, y, z) + ", E: " + e(x, y, z) + ", W: " + w(x, y, z);
	}

	public List<Node> getValidNeighbors(Node node, java.util.Map<WorldPoint, java.util.List<Transport>> transports)
	{
		List<Node> neighbors = new ArrayList<>();
		WorldPoint p = node.getWorldPoint();
		final int x = p.getX();
		final int y = p.getY();
		final int z = p.getPlane();

		boolean[] traversable = new boolean[8];

		traversable[0] = w(x, y, z); // WEST
		traversable[1] = e(x, y, z); // EAST
		traversable[2] = s(x, y, z); // SOUTH
		traversable[3] = n(x, y, z); // NORTH
		traversable[4] = sw(x, y, z); // SOUTH_WEST
		traversable[5] = se(x, y, z); // SOUTH_EAST
		traversable[6] = nw(x, y, z); // NORTH_WEST
		traversable[7] = ne(x, y, z); // NORTH_EAST

		for (int i = 0; i < traversable.length; i++)
		{
			if (traversable[i])
			{
				Direction d = ORDINAL_VALUES.get(i);
				int newX = x + d.getX();
				int newY = y + d.getY();
				neighbors.add(new Node(new WorldPoint(newX, newY, z)));
			}
		}

		if (transports != null && transports.containsKey(p))
		{
			for (Transport transport : transports.get(p))
			{
				neighbors.add(new Node(transport.getDestination()));
			}
		}

		return neighbors;
	}
}