package com.dklights;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public class LampPoint
{

	@Getter
	private int bitPosition;

	@Getter
	private WorldPoint worldPoint;

	@Getter
	private String description;

	public LampPoint(int bitPosition, WorldPoint worldPoint, String description)
	{
		this.bitPosition = bitPosition;
		this.worldPoint = worldPoint;
		this.description = description;
	}
}
