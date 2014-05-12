package com.gmail.filoghost.chestcommands.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.chestcommands.exception.ArmorColorException;

public class InventoryUtil {
	
	
	public static boolean hasInventoryFull(Player player) {
		return player.getInventory().firstEmpty() == -1;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean containsAtLeast(Inventory inv, int id, int minAmount) {
		int contained = 0;
		
		for (ItemStack item : inv.getContents()) {
			if (item != null && item.getTypeId() == id) {
				contained += item.getAmount();
			}
		}
		
		return contained >= minAmount;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean containsAtLeast(Inventory inv, int id, int minAmount, short data) {
		int contained = 0;
		
		for (ItemStack item : inv.getContents()) {
			if (item != null && item.getTypeId() == id && item.getDurability() == data) {
				contained += item.getAmount();
			}
		}
		
		return contained >= minAmount;
	}
	
	public static Color readArmorColor(String serializedColor) throws ArmorColorException {
		
		String[] pieces = serializedColor.replace(" ", "").split(","); // remove spaces and split
		if (pieces.length < 3) {
			throw new ArmorColorException(serializedColor);
		}
		
		try {
			int red = Integer.parseInt(pieces[0]);
			int green = Integer.parseInt(pieces[1]);
			int blue = Integer.parseInt(pieces[2]);
			
			if (red < 0 || red > 255) throw new ArmorColorException(serializedColor);
			if (green < 0 || green > 255) throw new ArmorColorException(serializedColor);
			if (blue < 0 || blue > 255) throw new ArmorColorException(serializedColor);
			
			return Color.fromRGB(red, green, blue);
				
		} catch (NumberFormatException ex) {
			throw new ArmorColorException(serializedColor);
		} catch (IllegalArgumentException ex) {
			throw new ArmorColorException(serializedColor);
		}
	}
	
	public static boolean isLeatherArmor(Material mat) {
		return mat == Material.LEATHER_BOOTS || mat == Material.LEATHER_LEGGINGS || mat == Material.LEATHER_CHESTPLATE || mat == Material.LEATHER_HELMET;
	}
}
