package com.dklights;

import java.util.BitSet;
import java.util.HashMap;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DKLightsHelper
{

	// Anything with y >= 5312 is in the north chunk of Dorgesh-Kaan
	public static final int WORLDMAP_LINE = 5312;

	public static final HashMap<Integer, WorldPoint> P0_N = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P0_S = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P1_N = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P1_S = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P2_N = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P2_S = new HashMap<>();

	public void init()
	{
		P0_N.put(0, new WorldPoint(0, 1, 1));
	}

	public DKLightsEnum determineLocation(Client client)
	{

		Player player = client.getLocalPlayer();

		// Note that this is very explicit for readability.
		if (player != null)
		{
			WorldPoint w = player.getWorldLocation();
			if (w != null)
			{
				int plane = w.getPlane();
				int y = w.getY();
				log.info("P: " + plane + "\ty: " + y);
				if (plane == 0)
				{
					if (y >= WORLDMAP_LINE)
						return DKLightsEnum.P0_N;
					else
						return DKLightsEnum.P0_S;
				}
				else if (plane == 1)
				{
					if (y >= WORLDMAP_LINE)
						return DKLightsEnum.P1_N;
					else
						return DKLightsEnum.P1_S;
				}
				else if (plane == 2)
				{
					if (y >= WORLDMAP_LINE)
						return DKLightsEnum.P2_N;
					else
						return DKLightsEnum.P2_S;
				}
			}
		}
		return DKLightsEnum.BAD_AREA;
	}

	public WorldPoint[] findBrokenLamps(int lamps, DKLightsEnum currentArea)
	{
		BitSet bits = BitSet.valueOf(new long[]{lamps});

		log.info("bits: " + bits);
		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i+1))
		{
			if (i == Integer.MAX_VALUE)
				break;
			// For this set bit, grab the lamp loc based on the current area
			log.info("Bit " + i + " is set.");
		}
		WorldPoint[] lampPoints = {};
		return lampPoints;
	}
}
