package com.gmail.filoghost.chestcommands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.chestcommands.util.Utils;

public class Configuration {

	private static String messageNoMoney;
	private static String messageNoIconPermission;
	private static String messageNoRequiredItem;
	private static String messageEconomyBypassed;
	private static String messageNoSpace;
	
    private static String defaultCustomNameColor = "§f";
    private static String defaultLoreColor = "§7";
	
	private Configuration() { }
	
	public static void loadAndCopyMissingNodes() {
		
		ChestCommands.plugin.loadFile("config.yml");
		ChestCommands.plugin.reloadConfig();
		FileConfiguration configYaml = ChestCommands.plugin.getConfig();
		
		for (ConfigNode n : ConfigNode.values()) {
			if (!configYaml.isSet(n.path)) {
				configYaml.set(n.path, n.value);
			}
		}

		ChestCommands.plugin.saveConfig();
		
		
		//now load new values
		messageNoMoney = Utils.colorize(ConfigNode.MESSAGES_NO_MONEY.getString());
		messageNoIconPermission = Utils.colorize(ConfigNode.MESSAGES_NO_ICON_PERMISSION.getString());
		messageNoRequiredItem = Utils.colorize(ConfigNode.MESSAGES_NO_REQUIRED_ITEM.getString());
		messageEconomyBypassed = Utils.colorize(ConfigNode.MESSAGES_ECONOMY_BYPASSED.getString());
		messageNoSpace = Utils.colorize(ConfigNode.MESSAGES_NO_SPACE.getString());
		
		defaultCustomNameColor = Utils.colorize(ConfigNode.DEFAULT_NAME_COLOR.getString());
		defaultLoreColor = Utils.colorize(ConfigNode.DEFAULT_LORE_COLOR.getString());
		
	}
	
	
	
	
	public static void noMoneyMessage(Player player, double amount) {
		if (messageNoMoney != null && messageNoMoney.length() > 0) {
			if (messageNoMoney.contains("%price%")) {
				player.sendMessage(messageNoMoney.replace("%price%", Double.toString(amount)));
			} else {
				player.sendMessage(messageNoMoney);
			}
		}
	}
	
	
	public static void noIconPermissionMessage(Player player, String perm) {
		if (messageNoIconPermission != null && messageNoIconPermission.length() > 0) {
			if (messageNoIconPermission.contains("%permission%")) {
				player.sendMessage(messageNoIconPermission.replace("%permission%", perm));
			} else {
				player.sendMessage(messageNoIconPermission);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void noRequiredItemMessage(Player player, ItemStack item, boolean isDurabilityRestrictive) {
		if (messageNoRequiredItem != null && messageNoRequiredItem.length() > 0) {
			player.sendMessage(messageNoRequiredItem
									.replace("%material%", Utils.getFormattedMaterial(item.getType()))
									.replace("%id%", ""+ item.getTypeId())
									.replace("%amount%", ""+ item.getAmount())
									.replace("%datavalue%", (isDurabilityRestrictive ? ""+item.getDurability() : "any")));
		}
	}
	
	
	public static void noSpaceMessage(Player player) {
		if (messageNoSpace != null && messageNoSpace.length() > 0) {
			player.sendMessage(messageNoSpace);
		}
	}
	
	
	public static void economyBypassMessage(Player player) {
		if (messageEconomyBypassed != null && messageEconomyBypassed.length() > 0) {
			player.sendMessage(messageEconomyBypassed);
		}
	}
	
	public static String addDefaultNameColor(String input) {
		if (input.startsWith("§") || defaultCustomNameColor == null) return input;
		return defaultCustomNameColor + input;
	}
	
	public static String addDefaultLoreColor(String input) {
		if (input.startsWith("§") || defaultLoreColor == null) return input;
		return defaultLoreColor + input;
	}

	
	
	
	public enum ConfigNode {
		
		UPDATE_NOTIFICATIONS("update-notifications", true),
		HIDE_ATTRIBUTES("try-to-hide-attributes", true),
		DEFAULT_NAME_COLOR("default-item-name-color", "&f"),
		DEFAULT_LORE_COLOR("default-lore-color", "&7"),
		MESSAGES_NO_ICON_PERMISSION("messages.no-item-permission", "&cTo use this item you need the permission &e%permission%&c."),
		MESSAGES_NO_MONEY("messages.not-enough-money", "&cYou don't have enough money (&e%price%&c) for this command."),
		MESSAGES_NO_REQUIRED_ITEM("messages.no-required-item", "&cYou must have &e%amount%x %material% &c(ID: %id%, data value: %datavalue%) for this."),
		MESSAGES_ECONOMY_BYPASSED("messages.cost-bypassed", "&aYou have the permission &echestcommands.economy.bypass &aand you didn't pay for the command."),
		MESSAGES_NO_SPACE("messages.no-inventory-space", "&cYou didn't have enough space, items were dropped.");
		
		private String path;
		private Object value;
		
		private ConfigNode(String path, Object value) {
			this.path = path;
			this.value = value;
		}	
		
		public String getPath() {
			return path;
		}
		
		public Object getValue() {
			return value;
		}
		
		public String getString() {
			if (!(value instanceof String)) {
				throw new IllegalArgumentException("Default value must be a String");
			}
			return ChestCommands.plugin.getConfig().getString(path);
		}
		
		public boolean getBoolean() {
			if (!(value instanceof Boolean)) {
				throw new IllegalArgumentException("Default value must be a boolean");
			}
			return ChestCommands.plugin.getConfig().getBoolean(path);
		}
	}
}
