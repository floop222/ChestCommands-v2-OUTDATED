package com.gmail.filoghost.chestcommands.version;

import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.chestcommands.util.VersionUtil;

public class AttributeRemover {

	private static IAttributeRemover editor;
	
	public interface IAttributeRemover {
		public ItemStack removeAttributes(ItemStack item);
	}
	
	public static boolean setup() {
		String version = VersionUtil.getBukkitVersion();
		
		if (version.equals("v1_6_R2")) {
			editor = new v1_6_R2();
		} else if (version.equals("v1_6_R3")) {
			editor = new v1_6_R3();
		} else if (version.equals("v1_7_R1")) {
			editor = new v1_7_R1();
		} else if (version.equals("v1_7_R2")) {
			editor = new v1_7_R2();
		} else if (version.equals("v1_7_R3")) {
			editor = new v1_7_R3();
		} else {
			editor = null;
			return false;
		}
		
		return true;
	}
	
	public static ItemStack remove(ItemStack target) {
		if (editor == null) return target;
		return editor.removeAttributes(target);
	}
}
