package com.dklights;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.coords.WorldPoint;
import java.util.Set;
import java.util.regex.Pattern;

public final class DKLightsConstants 
{
    private DKLightsConstants() {}

    public static final int NAV_BUTTON_PRIORITY = 7;
    public static final int DORGESHKAAN_LAMPS_VARBIT = 4038;
    public static final int WIRE_RESPAWN_TICKS = 8;
    public static final int INVENTORY_ID = 93;

    // Icon image path
    public static final String ICON_IMAGE_PATH = "/light_orb_32x32.png";

    // Chat messages and patterns
    public static final String NEARBY_LAMP_CHAT_MESSAGE = "There is another broken lamp nearby";
    public static final String TOTAL_LAMPS_FIXED_CHAT_MESSAGE = "You replace the orb. Total lights fixed: ";
    public static final Pattern NEARBY_LAMP_PATTERN = Pattern.compile("There is another broken lamp nearby, (\\w+(?:\\s\\w+)?) of here (\\w+(?:\\s\\w+)*)\\.");
    public static final Pattern TOTAL_LAMPS_PATTERN = Pattern.compile("You replace the orb\\. Total lights fixed: (?:<col=[^>]*>)?([0-9,]+)");

    // Coordinates
    public static final int WORLD_MAP_LINE = 5312; // Y
    public static final int DK_WEST_VALUE = 2688; // X
    public static final int DK_EAST_VALUE = 2751; // X
    public static final int DK_NORTH_VALUE = 5375; // Y
    public static final int DK_SOUTH_VALUE = 5248; // Y

    public static final WorldPoint MAP_NORTH_WEST_CORNER = new WorldPoint(2688, 5375, 0);
    public static final WorldPoint MAP_SOUTH_EAST_CORNER = new WorldPoint(2751, 5248, 0);

    // Object IDs
    public static final int TELEPORT_SPHERE_ID = 10972;
    public static final int LIGHT_BULB_EMPTY = 10980;
    public static final int LIGHT_BULB_WORKING = 10973;
    public static final int WIRE_MACHINE_ACTIVE = 22730;
    public static final int WIRE_MACHINE_INACTIVE = 22731;

    public static final Set<Integer> WIRE_MACHINE_IDS = ImmutableSet.of(
            WIRE_MACHINE_ACTIVE,
            WIRE_MACHINE_INACTIVE
    );

    public static final Set<Integer> DOOR_IDS = ImmutableSet.of(
            22914,
            22921
    );

public static final Set<Integer> STAIR_IDS_UP = ImmutableSet.of(
            22931,
            22934,
            22937,
            22939,
            22941
    );
    
public static final Set<Integer> STAIR_IDS_DOWN = ImmutableSet.of(
            22932,
            22933,
            22935,
            22936,
            22938,
            22940,
            22942
    );
    
    public static final Set<Integer> STAIR_IDS = ImmutableSet.<Integer>builder()
            .addAll(STAIR_IDS_UP)
            .addAll(STAIR_IDS_DOWN)
            .build();
    
    // Bank location in Dorgesh-Kaan
    public static final WorldPoint WIRE_MACHINE_LOCATION = new WorldPoint(2716, 5274, 0);
    public static final WorldPoint BANK_LOCATION = new WorldPoint(2702, 5349, 0);
}
