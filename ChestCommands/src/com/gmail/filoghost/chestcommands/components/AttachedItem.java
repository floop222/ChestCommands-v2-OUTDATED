package com.gmail.filoghost.chestcommands.components;

import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class AttachedItem {

	public IconMenu menu;
	Integer id;
	Short data;
	Action[] validActions;
	
	public AttachedItem(IconMenu menu, int id) {
		this.menu = menu;
		this.id = id;
	}
	
	public void setRestrictiveData(Short data) {
		this.data = data;
	}
	
	public void setValidActions(Action... validActions) {
		this.validActions = validActions;
	}
	
	public boolean isValidAction(Action action) {
		for (Action validAction : validActions) {
			if (action == validAction) return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isValidTrigger(ItemStack item, Action action) {
		//invalid id or item
		if (this.id == 0 || item == null) return false;
		
		int id = item.getTypeId();
		
		//first, they must have the same id
		if (this.id != id)												return false;
		// check that data value was set (if this was set && this is different from the item -> return false)
		if (this.data != null && this.data != item.getDurability()) 	return false;
		//check for actions
		if (!isValidAction(action))										return false;
			
		return true;
	}
}
