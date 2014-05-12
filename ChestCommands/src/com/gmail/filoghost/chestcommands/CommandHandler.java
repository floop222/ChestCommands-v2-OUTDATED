package com.gmail.filoghost.chestcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.filoghost.chestcommands.CommandHandler.CommandValidator.ArgumentException;
import com.gmail.filoghost.chestcommands.components.IconMenu;
import com.gmail.filoghost.chestcommands.util.Utils;

public class CommandHandler implements CommandExecutor {
	public ChestCommands plugin;
	
	public CommandHandler(ChestCommands main) {
		plugin = main;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { try {
		
		
		
		if (args.length == 0) {
			sender.sendMessage(Utils.formatTitle("Chest Commands"));
			sender.sendMessage("§eVersion: §7" + plugin.getDescription().getVersion());
			sender.sendMessage("§eDeveloper: §7filoghost");
			sender.sendMessage("§eCommands: §7/" + label + " help");
			return true;
		}		
		
		if (args[0].equalsIgnoreCase("reload")) {
			CommandValidator.checkPerm(sender, Permissions.COMMAND_RELOAD);
			ChestCommands.plugin.loadConfiguration();
			if (ErrorLogger.getAmount() == 0) {
				sender.sendMessage(ChestCommands.PLUGIN_PREFIX + "§aConfiguration reloaded!");
			} else {
				sender.sendMessage(ChestCommands.PLUGIN_PREFIX + "§cConfiguration reloaded with errors, please check the console for the log.");
				ErrorLogger.printErrors();
			}
			return true;
		}
		
		
		
		if (args[0].equalsIgnoreCase("list")) {
			CommandValidator.checkPerm(sender, Permissions.COMMAND_LIST);
			sender.sendMessage(Utils.formatTitle("List of loaded menus"));
			for (String file : ChestCommands.yamlFileNameAndMenu.keySet()) {
				sender.sendMessage("§7- §e" + file);
			}
			sender.sendMessage("§6§oIf a file is missing, check your console for possible errors.");
			return true;
			
		}
		

		
		if (args[0].equalsIgnoreCase("open")) {
			CommandValidator.checkPerm(sender, Permissions.COMMAND_OPEN);
			CommandValidator.isTrue(args.length >= 2, "§cNot enough arguments. §e/" + label + " open <file> [player]");

			if (args.length < 3) {
				
				CommandValidator.isTrue((sender instanceof Player), "§cCannot open menu from console. Please specify a player.");
				
				if (!args[1].endsWith(".yml")) args[1] += ".yml";
				
				CommandValidator.isTrue(API.isExistingMenu(args[1]), "§cFile §e" + args[1] + " §cnot found or not correctly loaded. Check the console for possible errors.");
				
				ChestCommands.openYamlWithPermission(args[1], (Player) sender);
				return true;
				
			} else {
				
				CommandValidator.checkPerm(sender, Permissions.COMMAND_OPEN_OTHERS);
				
				Player target = Bukkit.getPlayerExact(args[2]);
				CommandValidator.isTrue(target != null, "The target player is not online.");

				if (!args[1].endsWith(".yml")) args[1] += ".yml";
				
				CommandValidator.isTrue(API.isExistingMenu(args[1]), "File §e" + args[1] + " §cnot found or not correctly loaded. Check the console for possible errors.");
				
				ChestCommands.yamlFileNameAndMenu.get(args[1]).open(target);
				sender.sendMessage("§aOpened menu for " + target.getName());
				return true;

			}
		}
		

		if (args[0].equalsIgnoreCase("update")) {
			CommandValidator.checkPerm(sender, Permissions.COMMAND_UPDATE);
			Updater.UpdaterHandler.manuallyCheckUpdates(sender);
			return true;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("checkperms")) {
			CommandValidator.checkPerm(sender, Permissions.COMMAND_CHECKPERMS);
			CommandValidator.isTrue(args.length >= 2, "Not enough arguments. §e/" + label + " checkperms <player>");
			Player target = Bukkit.getPlayerExact(args[1]);
			CommandValidator.isTrue(target != null, "The target player is not online.");
			
			sender.sendMessage(Utils.formatTitle("Permissions of " + target.getName()));
			for (IconMenu menu :  ChestCommands.menuNameAndMenu.values()) {
				
				String openCommandMessage = target.hasPermission(menu.getOpenPermission()) ? "§ahas permission" : "§cno permission";
				String openItemMessage = target.hasPermission(menu.getOpenWithItemPermission()) ? "§ahas permission" : "§cno permission";
				
				sender.sendMessage("§e" + menu.getFileName() + "§7: " + openCommandMessage);
				sender.sendMessage("§e" + menu.getFileName() + " (item)§7: " + openItemMessage);
				
			}
			return true;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("help")) {
			CommandValidator.checkPerm(sender, Permissions.COMMAND_HELP);
			sender.sendMessage(Utils.formatTitle("Commands"));
			sender.sendMessage("§a/" + label + " reload §7- Reloads the plugin");
			sender.sendMessage("§a/" + label + " open <file> [player] §7- Opens a menu");
			sender.sendMessage("§a/" + label + " list §7- List of loaded menus");
			sender.sendMessage("§a/" + label + " checkperms <player> §7- For debugging permission");
			sender.sendMessage("§a/" + label + " update §7- Try to update the plugin");
			return true;
		}
		
		
		
		sender.sendMessage("§cCommand not found. Use /" + label + " for help.");
		return true;
		
	} catch (ArgumentException ex) {
		ex.sendError(sender);
		return true;
	}}

	public static class CommandValidator {
		
		
		public static void checkPerm(CommandSender sender, String permission) throws ArgumentException {
			if (!sender.hasPermission(permission)) {
				throw new ArgumentException("You don't have permission §e" + permission);
			}
		}
		
		public static void isTrue(boolean b, String errorMessage) throws ArgumentException {
			if (!b) {
				throw new ArgumentException(errorMessage);
			}
		}
		
		public static class ArgumentException extends Exception {

			private static final long serialVersionUID = 1L;
			private String errorMessage;
			
			public ArgumentException(String errorMessage) {
				this.errorMessage = "§c" + errorMessage;
			}
			
			public void sendError(CommandSender sender) {
				sender.sendMessage(this.errorMessage);
			}
			
		}
		
	}

}
