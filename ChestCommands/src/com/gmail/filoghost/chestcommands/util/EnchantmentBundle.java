package com.gmail.filoghost.chestcommands.util;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentBundle {

	public Enchantment ench;
	public int level;
	
	public EnchantmentBundle(Enchantment ench) {
		this(ench, 1);
	}
	
	public EnchantmentBundle(Enchantment ench, int level) {
		this.ench = ench;
		this.level = level;
		if (this.level < 1) this.level = 1;
	}
}
