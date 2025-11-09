package com.dklights.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetType
{
	NONE("None"),
	LAMP("Lamp"),
	BANK("Bank"),
	WIRING_MACHINE("Wiring machine");

	private final String displayName;
}
