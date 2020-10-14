package com.dklights;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DKLightsHelper
{

	// Anything with y >= 5312 is in the north map square of Dorgesh-Kaan
	public static final int WORLDMAP_LINE = 5312;

	// HashMap containing WorldPoints for each lamp on Plane Pn for N(orth) and S(outh)
	public static final HashMap<Integer, WorldPoint> P0_N = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P0_S = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P1_N = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P1_S = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P2_N = new HashMap<>();
	public static final HashMap<Integer, WorldPoint> P2_S = new HashMap<>();

	public static final HashMap<Integer, HashMap<Integer, WorldPoint>[]> maps = new HashMap<>();

	// Initialize the HashMaps for each region with the points found in them.
	// The key refers to the bit in the Dorgesh-Kaan lamps varbit that the lamp
	// is indicated by (little-endian bits). The value is the WorldPoint of the lamp.
	public void init()
	{
		P0_N.put(5, new WorldPoint(2691, 5328, 0));
		P0_N.put(6, new WorldPoint(2746, 5323, 0));
		P0_N.put(7, new WorldPoint(2749, 5329, 0));
		P0_N.put(8, new WorldPoint(2742, 5327, 0));
		P0_N.put(9, new WorldPoint(2737, 5324, 0));
		P0_N.put(10, new WorldPoint(2701, 5345, 0));
		P0_N.put(11, new WorldPoint(2706, 5354, 0));
		P0_N.put(12, new WorldPoint(2701, 5362, 0));
		P0_N.put(13, new WorldPoint(2706, 5369, 0));
		P0_N.put(14, new WorldPoint(2745, 5360, 0));
		P0_N.put(15, new WorldPoint(2739, 5360, 0));
		P0_N.put(16, new WorldPoint(2736, 5350, 0));
		P0_N.put(17, new WorldPoint(2747, 5348, 0));
		P0_N.put(18, new WorldPoint(2741, 5344, 0));
		P0_N.put(19, new WorldPoint(2744, 5348, 0));

		P0_S.put(0, new WorldPoint(2738, 5283, 0));
		P0_S.put(1, new WorldPoint(2749, 5292, 0));
		P0_S.put(2, new WorldPoint(2744, 5299, 0));
		P0_S.put(3, new WorldPoint(2690, 5302, 0));
		P0_S.put(4, new WorldPoint(2698, 5302, 0));
		P0_S.put(10, new WorldPoint(2699, 5256, 0));
		P0_S.put(11, new WorldPoint(2695, 5260, 0));
		P0_S.put(12, new WorldPoint(2698, 5269, 0));
		P0_S.put(13, new WorldPoint(2735, 5278, 0));
		P0_S.put(14, new WorldPoint(2739, 5253, 0));
		P0_S.put(15, new WorldPoint(2749, 5261, 0));
		P0_S.put(16, new WorldPoint(2707, 5274, 0));

		P1_N.put(5, new WorldPoint(2693, 5331, 1));
		P1_N.put(6, new WorldPoint(2742, 5335, 1));
		P1_N.put(7, new WorldPoint(2738, 5324, 1));
		P1_N.put(8, new WorldPoint(2693, 5333, 1));
		P1_N.put(9, new WorldPoint(2742, 5341, 1));
		P1_N.put(10, new WorldPoint(2697, 5344, 1));
		P1_N.put(11, new WorldPoint(2705, 5354, 1));
		P1_N.put(12, new WorldPoint(2716, 5364, 1));
		P1_N.put(13, new WorldPoint(2736, 5363, 1));
		P1_N.put(14, new WorldPoint(2739, 5362, 1));
		P1_N.put(15, new WorldPoint(2733, 5350, 1));
		P1_N.put(16, new WorldPoint(2705, 5348, 1));

		P1_S.put(0, new WorldPoint(2699, 5305, 1));
		P1_S.put(1, new WorldPoint(2739, 5286, 1));
		P1_S.put(2, new WorldPoint(2737, 5294, 1));
		P1_S.put(3, new WorldPoint(2741, 5283, 1));
		P1_S.put(4, new WorldPoint(2695, 5294, 1));
		P1_S.put(10, new WorldPoint(2736, 5272, 1));
		P1_S.put(11, new WorldPoint(2731, 5272, 1));
		P1_S.put(12, new WorldPoint(2736, 5278, 1));
		P1_S.put(13, new WorldPoint(2709, 5270, 1));
		P1_S.put(14, new WorldPoint(2707, 5278, 1));

		P2_N.put(9, new WorldPoint(2746, 5355, 2));
		P2_N.put(10, new WorldPoint(2739, 5362, 2));
		P2_N.put(11, new WorldPoint(2736, 5363, 2));
		P2_N.put(12, new WorldPoint(2729, 5368, 2));

		P2_S.put(0, new WorldPoint(2741, 5283, 2));
		P2_S.put(1, new WorldPoint(2737, 5298, 2));
		P2_S.put(2, new WorldPoint(2741, 5294, 2));
		P2_S.put(3, new WorldPoint(2741, 5287, 2));
		P2_S.put(4, new WorldPoint(2744, 5282, 2));
		P2_S.put(5, new WorldPoint(2695, 5294, 2));
		P2_S.put(6, new WorldPoint(2699, 5289, 2));
		P2_S.put(7, new WorldPoint(2699, 5305, 2));
		P2_S.put(8, new WorldPoint(2695, 5301, 2));
		P2_S.put(9, new WorldPoint(2740, 5264, 2));

		maps.put(DKLightsEnum.P0_N.value, new HashMap[]{P0_N, P0_S});
		maps.put(DKLightsEnum.P0_S.value, new HashMap[]{P0_S, P0_N});
		maps.put(DKLightsEnum.P1_N.value, new HashMap[]{P1_N, P1_S});
		maps.put(DKLightsEnum.P1_S.value, new HashMap[]{P1_S, P1_N});
		maps.put(DKLightsEnum.P2_N.value, new HashMap[]{P2_N, P2_S});
		maps.put(DKLightsEnum.P2_S.value, new HashMap[]{P2_S, P2_N});
	}

	// Determine which region of Dorgesh-Kaan the player is currently in.
	// The city is split across a northern and southern map square.
	// The interpretation of the Dorgesh-Kaan lamps varbit depends on whether the player is in the
	// north or south square and which plane the player is located in.
	public DKLightsEnum determineLocation(WorldPoint w)
	{

		// Note that this is very explicit for readability.
		if (w != null)
		{
			int plane = w.getPlane();
			int y = w.getY();
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
		return DKLightsEnum.BAD_AREA;
	}

	// Return a list of WorldPoint objects corresponding to the tiles that broken lamps are on
	// based on the players current region.
	// Typically, the parameter lamps should be the value of the Dorgesh-Kaan lamps varbit (4038).
	public ArrayList<WorldPoint> findBrokenLamps(int lamps, DKLightsEnum currentArea)
	{
		BitSet bits = BitSet.valueOf(new long[]{lamps});
		ArrayList<WorldPoint> lampPoints = new ArrayList<>();

		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i+1))
		{
			if (i == Integer.MAX_VALUE)
				break;
			// For this set bit, grab the lamp loc based on the current area
			WorldPoint w = maps.get(currentArea.value)[0].get(i);

			// If there is no lamp indicated by the bit i in the first map square,
			// the bit actually refers to a lamp in the other map square.
			if (w == null)
				w = maps.get(currentArea.value)[1].get(i);

			if (w != null)
				lampPoints.add(w);
			else
				log.info("Bit " + i + " has a null value for both arrays!");
		}
		return lampPoints;
	}



	// Return a sorted ArrayList of n world points such that the WorldPoint closest to the player
	// is at index 0 and the farthest point is at index n-1.
	public ArrayList<WorldPoint> sortBrokenLamps(ArrayList<WorldPoint> lampPoints, WorldPoint currentPoint)
	{

		ArrayList<WorldPoint> sortedPoints = new ArrayList<>(lampPoints);

		Comparator<WorldPoint> comparator = new Comparator<WorldPoint>()
		{
			public int compare(WorldPoint a, WorldPoint b)
			{
				return currentPoint.distanceTo(a) - currentPoint.distanceTo(b);
			}
		};

		sortedPoints.sort(comparator);

		return sortedPoints;
	}
}
