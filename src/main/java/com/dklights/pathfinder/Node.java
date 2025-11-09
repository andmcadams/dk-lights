package com.dklights.pathfinder;

import lombok.Data;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Node implements Comparable<Node>
{
	private final WorldPoint worldPoint;
	private Node parent;
	private float gCost; // Distance from start
	private float hCost; // Distance to target (heuristic)
	private float fCost; // Total cost (g + h)

	public Node(WorldPoint worldPoint)
	{
		this.worldPoint = worldPoint;
		this.gCost = Float.MAX_VALUE;
		this.hCost = 0;
		this.fCost = Float.MAX_VALUE;
	}

	public List<WorldPoint> getPath()
	{
		List<WorldPoint> path = new ArrayList<>();
		Node current = this;

		while (current != null)
		{
			path.add(current.worldPoint);
			current = current.parent;
		}

		Collections.reverse(path);
		return path;
	}

	@Override
	public int compareTo(Node other)
	{
		return Float.compare(this.fCost, other.fCost);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Node node = (Node) obj;
		return worldPoint.equals(node.worldPoint);
	}

	@Override
	public int hashCode()
	{
		return worldPoint.hashCode();
	}
}