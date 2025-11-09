package com.dklights;

import java.awt.Color;

import com.dklights.enums.DisplayFloorType;
import com.dklights.enums.HighlightType;
import com.dklights.enums.PathDrawStyle;
import com.dklights.enums.TimerType;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("dklights")
public interface DKLightsConfig extends Config {

    @ConfigItem(
		keyName = "showLegacyOverlay",
		name = "Show Legacy Overlay",
		description = "Show the legacy overlay with a list of broken lamps.",
		position = 1
	)
	default boolean showLegacyOverlay()
	{
		return false;
	}

	// --- Section: Pathfinding ---
	@ConfigSection(
		name = "Pathfinding",
		description = "Settings for pathfinding to lamps and utilities.",
		position = 2
	)
	String pathSection = "pathSection";

	@ConfigItem(
		keyName = "showPathToLocation",
		name = "Show Path",
		description = "Draw a tile path to the closest broken lamp or utility location (bank/wire machine).",
		section = pathSection,
		position = 1
	)
	default boolean showPathToLocation() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "pathDrawStyle",
		name = "Path Draw Style",
		description = "The visual style of the drawn path.",
		section = pathSection,
		position = 2
	)
	default PathDrawStyle pathDrawStyle() 
	{
		return PathDrawStyle.TILES;
	}

	@ConfigItem(
		keyName = "pathColor",
		name = "Lamp Path Color",
		description = "Color of the path to the closest broken lamp.",
		section = pathSection,
		position = 3
	)
	default Color pathColor() 
	{
		return new Color(255, 102, 102);
	}

	@ConfigItem(
		keyName = "utilityPathColor",
		name = "Utility Path Color",
		description = "Color of the path to the bank or wire machine.",
		section = pathSection,
		position = 4
	)
	default Color utilityPathColor() 
	{
		return new Color(102, 255, 102);
	}

	@ConfigItem(
		keyName = "maxPathDistance",
		name = "Teleport Hint Distance",
		description = "Show a teleport hint if the path to the closest lamp is longer than this. 0 to disable.",
		section = pathSection,
		position = 5
	)
	default int maxPathDistance() 
	{
		return 64;
	}

	// --- Section: Lamp Highlights ---
	@ConfigSection(
		name = "Lamp Highlights",
		description = "Settings for highlighting Dorgesh-Kaan lamps.",
		position = 10
	)
	String lampsSection = "lampsSection";

	@ConfigItem(
		keyName = "lampsHighlightStyle",
		name = "Lamp Highlight Style",
		description = "Choose how to highlight lamps.",
		section = lampsSection,
		position = 1
	)
	default HighlightType lampsHighlightStyle()
	 {
		return HighlightType.CLICKBOX;
	}

	@ConfigItem(
		keyName = "highlightBrokenLamps",
		name = "Broken Lamps",
		description = "Highlight known broken lamps.",
		section = lampsSection,
		position = 2
	)
	default boolean highlightBrokenLamps() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "brokenLampColor",
		name = "Broken Lamp Color",
		description = "Color for broken lamps.",
		section = lampsSection,
		position = 3
	)
	default Color getBrokenLampColor() 
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "highlightWorkingLamps",
		name = "Working Lamps",
		description = "Highlight known working lamps.",
		section = lampsSection,
		position = 4
	)
	default boolean highlightWorkingLamps() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "workingLampColor",
		name = "Working Lamp Color",
		description = "Color for working lamps.",
		section = lampsSection,
		position = 5
	)
	default Color getWorkingLampColor() 
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "highlightAllLamps",
		name = "All Lamps",
		description = "Highlight all lamps.",
		section = lampsSection,
		position = 6
	)
	default boolean highlightAllLamps() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "defaultLampColor",
		name = "Default Lamp Color",
		description = "Color for lamps when 'All Lamps' is enabled.",
		section = lampsSection,
		position = 7
	)
	default Color getDefaultLampColor() 
	{
		return Color.GRAY;
	}

	@ConfigItem(
		keyName = "highlightAllPlanesLamps",
		name = "Highlight Lamps on Other Floors",
		description = "Show highlights for lamps on different floors from you.",
		section = lampsSection,
		position = 8
	)
	default boolean highlightOtherPlanesLamps() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "darkenOtherPlanesLamps",
		name = "Darken Lamps on Other Floors",
		description = "Darkens the highlight color for lamps on different floors. Requires 'Highlight Lamps on Other Floors' to be on.",
		section = lampsSection,
		position = 9
	)
	default boolean darkenOtherPlanesLamps() 
	{
		return true;
	}

	// --- Section: Other Highlights ---
	@ConfigSection(
		name = "Other Highlights",
		description = "Settings for highlighting objects like the wire machine, doors, and stairs.",
		position = 20
	)
	String hintsSection = "hintsSection";

	@ConfigItem(
		keyName = "objectsHighlightStyle",
		name = "Object Highlight Style",
		description = "Choose how to highlight objects (stairs, doors, wire machine).",
		section = hintsSection,
		position = 1
	)
	default HighlightType objectsHighlightStyle() 
	{
		return HighlightType.CLICKBOX;
	}

	@ConfigItem(
		keyName = "highlightWireMachine",
		name = "Highlight Wire Machine",
		description = "Highlight the wire machine and show a respawn timer.",
		section = hintsSection,
		position = 2
	)
	default boolean highlightWireMachine() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "wireMachineHighlightColor",
		name = "Wire Machine Color",
		description = "The color to highlight the wire machine.",
		section = hintsSection,
		position = 3
	)
	default Color wireMachineHighlightColor() 
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		keyName = "timerType",
		name = "Wire Machine Timer Style",
		description = "The UI style for displaying the respawn timer on the wire machine.",
		section = hintsSection,
		position = 4
	)
	default TimerType timerType() 
	{
		return TimerType.PIE;
	}

	@ConfigItem(
		keyName = "highlightInformativeStairs",
		name = "Highlight Informative Stairs",
		description = "Highlight stairs/ladders that lead to areas with unknown lamp information.",
		section = hintsSection,
		position = 5
	)
	default boolean highlightInformativeStairs() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "informativeStairColor",
		name = "Informative Stair Color",
		description = "The color to highlight informative stairs/ladders.",
		section = hintsSection,
		position = 6
	)
	default Color informativeStairColor() 
	{
		return new Color(255, 102, 183);
	}

	@ConfigItem(
		keyName = "highlightClosedDoors",
		name = "Highlight All Closed Doors",
		description = "Highlights all closed doors in Dorgesh-Kaan, not just those on your path.",
		section = hintsSection,
		position = 7
	)
	default boolean highlightClosedDoors() 
	{
		return false;
	}

	@ConfigItem(
		keyName = "doorHighlightColor",
		name = "Door Highlight Color",
		description = "Color for the 'Highlight All Closed Doors' option.",
		section = hintsSection,
		position = 8
	)
	default Color doorHighlightColor() 
	{
		return Color.YELLOW;
	}

	// --- Section: UI & Overlays ---
	@ConfigSection(
		name = "UI & Overlays",
		description = "Settings for the side panel map and statistics overlay.",
		position = 30
	)
	String uiSection = "uiSection";

	@ConfigItem(
		keyName = "showStatsOverlay",
		name = "Show Stats Overlay",
		description = "Show the overlay with target, distance, and lamps/hr stats.",
		section = uiSection,
		position = 5
	)
	default boolean showStatsOverlay() 
	{
		return true;
	}

	@ConfigItem(
		keyName = "showClosestDistance",
		name = "Show Distance",
		description = "Show the 'Distance:' line in the Stats Overlay.",
		section = uiSection,
		position = 6
	)
	default boolean showClosestDistance() 
	{
		return true;
	}

	// --- Section: Global Highlight Styles ---
	@ConfigSection(
		name = "Global Highlight Styles",
		description = "General settings that affect all 'Border' style highlights.",
		position = 40
	)
	String highlightStyleSection = "highlightStyleSection";

	@Range(min = 1, max = 4)
	@ConfigItem(
		keyName = "borderThickness",
		name = "Border Thickness",
		description = "Thickness (in pixels) for border-style highlights.",
		section = highlightStyleSection,
		position = 1
	)
	default int borderThickness() 
	{
		return 2;
	}

	@Range(min = 0, max = 4)
	@ConfigItem(
		keyName = "borderFeather",
		name = "Border Feather",
		description = "Feathering amount for border-style highlights. 0 to disable.",
		section = highlightStyleSection,
		position = 2
	)
	default int borderFeather() 
	{
		return 0;
	}
}
