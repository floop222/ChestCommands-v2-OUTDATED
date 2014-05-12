package com.gmail.filoghost.chestcommands.components;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.filoghost.chestcommands.Configuration;
import com.gmail.filoghost.chestcommands.util.EnchantmentBundle;
import com.gmail.filoghost.chestcommands.util.Utils;

public class Icon {
	
	public ItemStack stack;
	public double price;
	public Command[] commands;
	public String permission = null;
	public String requiredItem = null;
	public boolean keepOpen = false;
	
	public Icon (Material material) {
		if (material == null) material = Material.BEDROCK;
		stack = new ItemStack(material);
	}
	
	public void setAmount(int amount) {
		if (amount < 1) amount = 1;
		if (amount > 64) amount = 64;
		
		stack.setAmount(amount);
	}
	
	public void setDurability(short data) {
		if (data == 0) return;
		stack.setDurability(data);
	}
	
	public void addEnchantmentBundleArray(EnchantmentBundle[] bundles) {
		if (bundles == null || bundles.length == 0) return;
		for (EnchantmentBundle bundle : bundles) {
			addEnchantmentBundle(bundle);
		}
	}
	
	public void addEnchantmentBundle(EnchantmentBundle bundle) {
		if (bundle == null) return;
		if (bundle.ench == null) return;
		if (bundle.level <= 0) bundle.level = 1;
		stack.addUnsafeEnchantment(bundle.ench, bundle.level);
	}
	
	public void setPrice(double price) {
		if (price < 0.0) return;
		this.price = price;
	}
	
	public void setKeepOpen(boolean keepOpen) {
		this.keepOpen = keepOpen;
	}
	
	public void setCommands(Command[] commands) {
		this.commands = commands;
	}
	
	/**
	 * 
	 * @param name can be null.
	 * @param lore can be null.
	 */
	public void setNameAndLore(String name, List<String> lore) {
    	
    	ItemMeta meta = stack.getItemMeta();
    	
    	if (name != null) {
    		name = Utils.colorizeAndAddSymbols(name);
    		name = Configuration.addDefaultNameColor(name);

    		meta.setDisplayName(name);
    	}
    	
    	if (lore != null && lore.size() > 0) {
    		
    		String line;
    		
    		for (int i = 0; i < lore.size() ; i++) {
    			line = lore.get(i);
    			line = Utils.colorizeAndAddSymbols(line);
    			line = Configuration.addDefaultLoreColor(line);
    			
    			lore.set(i, line);
    		}
    		
            meta.setLore(lore);
    	}
    	
        stack.setItemMeta(meta);
    }
	
	public ItemStack getCloneStack() {
		return stack.clone();
	}

	
	public String getNameForErrors() {	
		if (!stack.getItemMeta().hasDisplayName()) return "{no-name}";
		return ChatColor.stripColor(stack.getItemMeta().getDisplayName());
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	public void setRequiredItem(String item) {
		this.requiredItem = item;
	}
}
