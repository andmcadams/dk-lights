package com.dklights.enums;

import java.util.List;

public enum Direction
{
	WEST(-1, 0),
	EAST(1, 0),
	SOUTH(0, -1),
	NORTH(0, 1),
	SOUTH_WEST(-1, -1),
	SOUTH_EAST(1, -1),
	NORTH_WEST(-1, 1),
	NORTH_EAST(1, 1);

	final int x;
	final int y;

	Direction(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public boolean isDiagonal()
	{
		return this == SOUTH_WEST || this == SOUTH_EAST || this == NORTH_WEST || this == NORTH_EAST;
	}

	public static List<Direction> getCardinalDirections()
	{
		return java.util.Arrays.asList(NORTH, EAST, SOUTH, WEST);
	}
}