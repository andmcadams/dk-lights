package com.dklights.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Area
{
	P0_N("North Ground Floor"),
	P0_S("South Ground Floor"),
	P1_N("North Second Floor"),
	P1_S("South Second Floor"),
	P2_N("North Third Floor"),
	P2_S("South Third Floor");

	private final String name;

	public Area getOpposite()
	{
		switch (this)
		{
		case P0_N:
			return P0_S;
		case P0_S:
			return P0_N;
		case P1_N:
			return P1_S;
		case P1_S:
			return P1_N;
		case P2_N:
			return P2_S;
		case P2_S:
			return P2_N;
		default:
			return null;
		}
	}

    public final String getName() {
        return name;
    }
}
