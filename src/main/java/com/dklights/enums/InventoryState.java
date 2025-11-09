package com.dklights.enums;

import com.dklights.DKLightsConstants;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

public enum InventoryState
{
	NO_LIGHT_BULBS,
	ONLY_EMPTY_BULBS,
	HAS_WORKING_BULBS;

	public InventoryState getInventoryState(Client client)
	{
		ItemContainer inventory = client.getItemContainer(DKLightsConstants.INVENTORY_ID);
		if (inventory == null)
		{
			return InventoryState.NO_LIGHT_BULBS;
		}

		boolean hasWorkingBulbs = false;
		boolean hasEmptyBulbs = false;

		for (Item item : inventory.getItems())
		{
			if (item.getId() == DKLightsConstants.LIGHT_BULB_WORKING)
			{
				hasWorkingBulbs = true;
			}
			else if (item.getId() == DKLightsConstants.LIGHT_BULB_EMPTY)
			{
				hasEmptyBulbs = true;
			}
		}

		if (hasWorkingBulbs)
		{
			return HAS_WORKING_BULBS;
		}
		else if (hasEmptyBulbs)
		{
			return ONLY_EMPTY_BULBS;
		}
		else
		{
			return NO_LIGHT_BULBS;
		}
	}
}
