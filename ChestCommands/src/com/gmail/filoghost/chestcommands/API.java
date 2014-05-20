package com.gmail.filoghost.chestcommands;

import java.util.Set;

import org.bukkit.entity.Player;

import com.gmail.filoghost.chestcommands.components.IconMenu;

public class API {

	/**
	 * @return the list of loaded menus (file names).
	 */
	public static Set<String> getLoadedMenus() {
		return ChestCommands.yamlFileNameAndMenu.keySet();
	}
	
	/**
	 * Note: this fail silently and should not trigger exceptions.
	 * 
	 * @param player the target player
	 * @param fileName the name of the menu e.g.: "menu.yml"
	 * @return if the menu was successfully open.
	 */
	public static boolean openMenu(Player player, String fileName) {
		if (player == null) return false;
		if (!player.isOnline()) return false;

		IconMenu menu = ChestCommands.yamlFileNameAndMenu.get(fileName);
		if (menu == null) return false;
		
		menu.open(player);
		return true;
	}
	
	/**
	 * 
	 * @param fileName the name of the menu e.g.: "menu.yml"
	 * @return if the menu exists and is correctly loaded.
	 */
	public static boolean isExistingMenu(String fileName) {
		return ChestCommands.yamlFileNameAndMenu.containsKey(fileName);
	}
	
	// Cannot be instantiated.
	private API() {}
}
