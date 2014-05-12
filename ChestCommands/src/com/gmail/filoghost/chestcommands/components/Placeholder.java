package com.gmail.filoghost.chestcommands.components;

import org.bukkit.entity.Player;

import com.gmail.filoghost.chestcommands.util.EconomyUtil;

public enum Placeholder {
	
	PLAYER("%player%", new IPlaceholder() { public String getReplacement(Player whoClicked) {
		return whoClicked.getName();
	}}),
	
	WORLD("%world%", new IPlaceholder() { public String getReplacement(Player whoClicked) {
		return whoClicked.getWorld().getName();
	}}),
	
	ONLINE_PLAYERS("%online_players%", new IPlaceholder() { public String getReplacement(Player whoClicked) {		
		return Integer.toString(whoClicked.getServer().getOnlinePlayers().length);
	}}),
	
	MAX_PLAYERS("%max_players%", new IPlaceholder() { public String getReplacement(Player whoClicked) {		
		return Integer.toString(whoClicked.getServer().getMaxPlayers());
	}}),
	
	MONEY("%money%", new IPlaceholder() { public String getReplacement(Player whoClicked) {
		if (EconomyUtil.hasValidEconomy()) {
			return Double.toString(EconomyUtil.getMoney(whoClicked));
		} else {
			return "0.0";
		}
	}});	
	
	String string;
	IPlaceholder handler;
	
	private Placeholder(String string, IPlaceholder handler) {
		this.string = string;
		this.handler = handler;
	}
	
	public static String replaceAll(String input, Player whoClicked) {
		for (Placeholder placeholder : values()) {
			if (input.contains(placeholder.string)) {
				input = input.replace(placeholder.string, placeholder.handler.getReplacement(whoClicked));
			}
		}
		
		return input;
	}
	
	private static abstract class IPlaceholder {
		public abstract String getReplacement(Player whoClicked);
	}
	
}
