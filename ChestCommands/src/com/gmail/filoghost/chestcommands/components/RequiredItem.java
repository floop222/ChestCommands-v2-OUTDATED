package com.gmail.filoghost.chestcommands.components;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.chestcommands.exception.ItemFormatException;
import com.gmail.filoghost.chestcommands.util.ItemStackReader;

public class RequiredItem {

	public int id;
	public int amount = 1;
	public short data;
	public boolean isDurabilityRestrictive = false;
	
	@SuppressWarnings("deprecation")
	public RequiredItem(int id) {
		if (id == 0) throw new IllegalArgumentException("The required item can't be air!");
		if (Material.getMaterial(id) == null) throw new IllegalArgumentException("The required item has a non-existing material id!");
		this.id = id;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack createStack() {
		return new ItemStack(id, amount, data);
	}
	
	public void setAmount(int amount) {
		if (amount <= 0) throw new IllegalArgumentException("The amount cannot be 0 or lower!");
		this.amount = amount;
	}
	
	public void setRestrictiveDurability(short data) {
		if (data < 0) throw new IllegalArgumentException("The data value cannot be lower than 0!");
		this.data = data;
		isDurabilityRestrictive = true;
	}
	
	public boolean isValidDurability(short data) {
		if (!isDurabilityRestrictive) return true;
		return data == this.data;
	}
	
	@SuppressWarnings("deprecation")
	public boolean hasItem(Player player) {
		int amountFound = 0;
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null && item.getTypeId() == id && isValidDurability(item.getDurability())) {
				amountFound += item.getAmount();
			}
		}
		
		return amountFound >= amount;
	}
	
	@SuppressWarnings("deprecation")
	public boolean takeItem(Player player) {
		
		int itemsToTake = amount; //start from amount and decrease
		
		ItemStack[] contents = player.getInventory().getContents();
		ItemStack current = null;
		
		
		for (int i = 0; i < contents.length; i++) {

			current = contents[i];
			
			if (current != null && current.getTypeId() == id && isValidDurability(current.getDurability())) {
				if (current.getAmount() > itemsToTake) {
					current.setAmount(current.getAmount() - itemsToTake);
					return true;
				} else {
					itemsToTake -= current.getAmount();
					player.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
			}
			
			//reached the end
			if (itemsToTake <= 0) return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static RequiredItem fromString(String input) throws ItemFormatException {
		ItemStackReader reader = new ItemStackReader(input);
		RequiredItem item = new RequiredItem(reader.getMaterial().getId());
		if (reader.hasAmount()) item.setAmount(reader.getAmount());
		if (reader.hasDataValue()) item.setRestrictiveDurability(reader.getDataValue());
		
		return item;
	}
}
