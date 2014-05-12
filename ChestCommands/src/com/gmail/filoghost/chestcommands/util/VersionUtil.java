package com.gmail.filoghost.chestcommands.util;

import org.bukkit.Bukkit;

public class VersionUtil {

	public static String getBukkitVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}
	
}
