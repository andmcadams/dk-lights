package com.dklights.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum Lamp
{

	// Plane 0 North
	P0_N_LAMP_5(5, 23025, new WorldPoint(2691, 5328, 0), Area.P0_N, "Room just NW of the market"),
	P0_N_LAMP_6(6, 23035, new WorldPoint(2746, 5323, 0), Area.P0_N, "Group of rooms just NE of the market"),
	P0_N_LAMP_7(7, 23036, new WorldPoint(2749, 5329, 0), Area.P0_N, "Group of rooms just NE of the market"),
	P0_N_LAMP_8(8, 23037, new WorldPoint(2742, 5327, 0), Area.P0_N, "Group of rooms just NE of the market"),
	P0_N_LAMP_9(9, 23038, new WorldPoint(2737, 5324, 0), Area.P0_N, "Group of rooms just NE of the market"),
	P0_N_LAMP_10(10, 23026, new WorldPoint(2701, 5345, 0), Area.P0_N, "Bank area"),
	P0_N_LAMP_11(11, 23027, new WorldPoint(2706, 5354, 0), Area.P0_N, "Bank area"),
	P0_N_LAMP_12(12, 23028, new WorldPoint(2701, 5362, 0), Area.P0_N, "Oldak's teleportation lab"),
	P0_N_LAMP_13(13, 23029, new WorldPoint(2706, 5369, 0), Area.P0_N, "Oldak's teleportation lab"),
	P0_N_LAMP_14(14, 23030, new WorldPoint(2745, 5360, 0), Area.P0_N, "NE most room"),
	P0_N_LAMP_15(15, 23031, new WorldPoint(2739, 5360, 0), Area.P0_N, "NE most room"),
	P0_N_LAMP_16(16, 23032, new WorldPoint(2736, 5350, 0), Area.P0_N, "Room east of the bank area"),
	P0_N_LAMP_17(17, 23033, new WorldPoint(2747, 5348, 0), Area.P0_N, "Group of rooms just NE of the market"),
	P0_N_LAMP_18(18, 23034, new WorldPoint(2741, 5344, 0), Area.P0_N, "Group of rooms just NE of the market"),
	P0_N_LAMP_19(19, 23039, new WorldPoint(2744, 5348, 0), Area.P0_N, "Group of rooms just NE of the market"),

	// Plane 0 South
	P0_S_LAMP_0(0, 22988, new WorldPoint(2738, 5283, 0), Area.P0_S, "Group of rooms just SE of the market"),
	P0_S_LAMP_1(1, 22989, new WorldPoint(2749, 5292, 0), Area.P0_S, "Group of rooms just SE of the market"),
	P0_S_LAMP_2(2, 22992, new WorldPoint(2744, 5299, 0), Area.P0_S, "Group of rooms just SE of the market"),
	P0_S_LAMP_3(3, 22993, new WorldPoint(2690, 5302, 0), Area.P0_S, "Group of rooms just SW of the market"),
	P0_S_LAMP_4(4, 22996, new WorldPoint(2698, 5302, 0), Area.P0_S, "Group of rooms just SW of the market"),
	P0_S_LAMP_10(10, 22978, new WorldPoint(2699, 5256, 0), Area.P0_S, "SW most group of rooms"),
	P0_S_LAMP_11(11, 22979, new WorldPoint(2695, 5260, 0), Area.P0_S, "SW most group of rooms"),
	P0_S_LAMP_12(12, 22981, new WorldPoint(2698, 5269, 0), Area.P0_S, "SW most group of rooms"),
	P0_S_LAMP_13(13, 22982, new WorldPoint(2735, 5278, 0), Area.P0_S, "Eastern house in south part of the city"),
	P0_S_LAMP_14(14, 22983, new WorldPoint(2739, 5253, 0), Area.P0_S, "SE most group of rooms"),
	P0_S_LAMP_15(15, 22985, new WorldPoint(2749, 5261, 0), Area.P0_S, "SE most group of rooms"),
	P0_S_LAMP_16(16, 22997, new WorldPoint(2707, 5274, 0), Area.P0_S, "House just west of the wire machine"),
	
	// Plane 1 North
	P1_N_LAMP_5(5, 23010, new WorldPoint(2693, 5331, 1), Area.P1_N, "Western house with a garden"),
	P1_N_LAMP_6(6, 23017, new WorldPoint(2742, 5335, 1), Area.P1_N, "Nursery"),
	P1_N_LAMP_7(7, 23018, new WorldPoint(2738, 5324, 1), Area.P1_N, "House south of the nursery"),
	P1_N_LAMP_8(8, 23024, new WorldPoint(2693, 5333, 1), Area.P1_N, "Western house with a garden"),
	P1_N_LAMP_9(9, 23040, new WorldPoint(2742, 5341, 1), Area.P1_N, "Nursery"),
	P1_N_LAMP_10(10, 23011, new WorldPoint(2697, 5344, 1), Area.P1_N, "Western house north of the garden house"),
	P1_N_LAMP_11(11, 23012, new WorldPoint(2705, 5354, 1), Area.P1_N, "House just south of Oldak's lab"),
	P1_N_LAMP_12(12, 23013, new WorldPoint(2716, 5364, 1), Area.P1_N, "Council chamber"),
	P1_N_LAMP_13(13, 23014, new WorldPoint(2736, 5363, 1), Area.P1_N, "House with Ur-tag"),
	P1_N_LAMP_14(14, 23015, new WorldPoint(2739, 5362, 1), Area.P1_N, "House just east of Ur-tag"),
	P1_N_LAMP_15(15, 23016, new WorldPoint(2733, 5350, 1), Area.P1_N, "House just south of Ur-tag"),
	P1_N_LAMP_16(16, 23023, new WorldPoint(2705, 5348, 1), Area.P1_N, "House just south of Oldak's lab"),
	P1_N_LAMP_17(17, 23041, new WorldPoint(2701, 5366, 1), Area.P1_N, "Upstairs of Oldak's teleportation lab"),

	// Plane 1 South
	P1_S_LAMP_0(0, 22990, new WorldPoint(2699, 5305, 1), Area.P1_S, "Western house"),
	P1_S_LAMP_1(1, 22991, new WorldPoint(2739, 5286, 1), Area.P1_S, "Eastern house"),
	P1_S_LAMP_2(2, 22994, new WorldPoint(2737, 5294, 1), Area.P1_S, "Eastern house"),
	P1_S_LAMP_3(3, 22999, new WorldPoint(2741, 5283, 1), Area.P1_S, "Eastern house"),
	P1_S_LAMP_4(4, 23005, new WorldPoint(2695, 5294, 1), Area.P1_S, "Western house"),
	P1_S_LAMP_10(10, 22986, new WorldPoint(2736, 5272, 1), Area.P1_S, "Upstairs of the eastern house in the south part of the city"),
	P1_S_LAMP_11(11, 23000, new WorldPoint(2731, 5272, 1), Area.P1_S, "Upstairs of the eastern house in the south part of the city"),
	P1_S_LAMP_12(12, 23001, new WorldPoint(2736, 5278, 1), Area.P1_S, "Upstairs of the eastern house in the south part of the city"),
	P1_S_LAMP_13(13, 23002, new WorldPoint(2709, 5270, 1), Area.P1_S, "Upstairs of the house west of the wire machine"),
	P1_S_LAMP_14(14, 23003, new WorldPoint(2707, 5278, 1), Area.P1_S, "Upstairs of the house west of the wire machine"),

	// Plane 2 North
	P2_N_LAMP_9(9, 23019, new WorldPoint(2746, 5355, 2), Area.P2_N, "Zanik's bedroom"),
	P2_N_LAMP_10(10, 23020, new WorldPoint(2739, 5362, 2), Area.P2_N, "Upstairs of the house just east of Ur-tag"),
	P2_N_LAMP_11(11, 23021, new WorldPoint(2736, 5363, 2), Area.P2_N, "Upstairs of the house with Ur-tag"),
	P2_N_LAMP_12(12, 23022, new WorldPoint(2729, 5368, 2), Area.P2_N, "Upstairs of the house with Ur-tag"),

	// Plane 2 South
	P2_S_LAMP_0(0, 22984, new WorldPoint(2741, 5283, 2), Area.P2_S, "Upstairs of the eastern house two houses south of the empty building"),
	P2_S_LAMP_1(1, 22987, new WorldPoint(2737, 5298, 2), Area.P2_S, "Upstairs of the eastern house just south of the empty building"),
	P2_S_LAMP_2(2, 22995, new WorldPoint(2741, 5294, 2), Area.P2_S, "Upstairs of the eastern house just south of the empty building"),
	P2_S_LAMP_3(3, 22998, new WorldPoint(2741, 5287, 2), Area.P2_S, "Upstairs of the eastern house two houses south of the empty building"),
	P2_S_LAMP_4(4, 23004, new WorldPoint(2744, 5282, 2), Area.P2_S, "Upstairs of the eastern house two houses south of the empty building"),
	P2_S_LAMP_5(5, 23006, new WorldPoint(2695, 5294, 2), Area.P2_S, "Upstairs of the western house just north of the train station"),
	P2_S_LAMP_6(6, 23007, new WorldPoint(2699, 5289, 2), Area.P2_S, "Upstairs of the western house just north of the train station"),
	P2_S_LAMP_7(7, 23008, new WorldPoint(2699, 5305, 2), Area.P2_S, "Upstairs of the western house two houses north of the train station"),
	P2_S_LAMP_8(8, 23009, new WorldPoint(2695, 5301, 2), Area.P2_S, "Upstairs of the western house two houses north of the train station"),
	P2_S_LAMP_9(9, 22980, new WorldPoint(2740, 5264, 2), Area.P2_S, "Upstairs of the SE most house");

	private final int bitPosition;
	private final int objectId;
	private final WorldPoint worldPoint;
	private final Area area;
	private final String description;
}
