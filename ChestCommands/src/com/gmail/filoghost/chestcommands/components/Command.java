package com.gmail.filoghost.chestcommands.components;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.gmail.filoghost.chestcommands.Configuration;
import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.exception.ItemFormatException;
import com.gmail.filoghost.chestcommands.util.EconomyUtil;
import com.gmail.filoghost.chestcommands.util.ItemStackReader;
import com.gmail.filoghost.chestcommands.util.Utils;

public class Command {

	private String command;
	private CommandType type;
	
	private Command(String command, CommandType type) {
		this.command = command;
		this.type = type;
	}
	
	public String getCommand() {
		return command;
	}
	
	public CommandType getType() {
		return type;
	}

	
	public static Command[] arrayFromString(String input) {
		
		if (input == null || input.length() == 0) return new Command[] {new Command("", CommandType.DEFAULT)};
		
		String[] commandStrings = input.split(";");
		Command[] commands = new Command[commandStrings.length];
		
		for (int i = 0; i < commandStrings.length; i++) {
			commands[i] = fromString(commandStrings[i]);
		}
		
		return commands;
	}
	
	private static Command fromString(String input) {
		
		if (input == null || input.length() == 0) return new Command("", CommandType.DEFAULT);
		
		input = input.trim();
		
		CommandType type = CommandType.DEFAULT;
		
		if (input.toLowerCase().startsWith("console:"))		{ 		input = input.substring(8);			type = CommandType.CONSOLE;				}
		if (input.toLowerCase().startsWith("op:")) 			{ 		input = input.substring(3);			type = CommandType.OP;					}
		if (input.toLowerCase().startsWith("open:")) 		{ 		input = input.substring(5);			type = CommandType.OPEN_MENU;			}
		if (input.toLowerCase().startsWith("tell:")) 		{		input = input.substring(5);			type = CommandType.TELL;				}
		if (input.toLowerCase().startsWith("broadcast:")) 	{ 		input = input.substring(10);		type = CommandType.BROADCAST;			}
		if (input.toLowerCase().startsWith("server:")) 		{		input = input.substring(7);			type = CommandType.SERVER;				}
		if (input.toLowerCase().startsWith("give:")) 		{		input = input.substring(5);			type = CommandType.GIVE;				}
		if (input.toLowerCase().startsWith("giveitem:")) 	{		input = input.substring(9);			type = CommandType.GIVE_SAVED_ITEM;		}
		if (input.toLowerCase().startsWith("givemoney:")) 	{		input = input.substring(10);			type = CommandType.GIVE_MONEY;		}
		
		input = Utils.colorizeAndAddSymbols(input).trim();
		
		return new Command(input, type);
	}
	

	public void execute(Player player) {
		
		if (command == null || command.length() == 0) return;
		
		//with placeholders
		String localCommand = Placeholder.replaceAll(command, player);
    	
		switch (type) {
				
			
			///////////////////////////////////////
			case CONSOLE:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), localCommand);
				break;
				
			
				
			///////////////////////////////////////
			case OP:
				boolean isOp = player.isOp();
                player.setOp(true);
                
                try {
                	player.chat("/" + localCommand);
                } catch (Exception local) { }
                
                
                try {
                	player.setOp(isOp);
                } catch (Exception danger) {
                	
                	danger.printStackTrace();
                	player.setOp(false);
                	ChestCommands.log.severe("An exception has occurred while removing " + player.getName() + " from OPs, while executing a command. OP or not, he was removed from OPs!");
                
                }
				break;
				
					
				
			///////////////////////////////////////
			case OPEN_MENU:
				ChestCommands.openYamlWithPermission(localCommand, player);
				break;
				
				
					
			///////////////////////////////////////
			case SERVER:
				Utils.connectToBungeeServer(player, localCommand);
				break;
				
					
				
			///////////////////////////////////////
			case GIVE:	
				try {
					
					ItemStack itemToGive = new ItemStackReader(command).createStack();
					HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(itemToGive);
					if (!remainingItems.isEmpty()) {
						Configuration.noSpaceMessage(player);
						Location whereToDrop = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(0.5));				
						for (ItemStack remaining : remainingItems.values()) {
							player.getWorld().dropItem(whereToDrop, remaining).setVelocity(new Vector(0.0, 0.0, 0.0));
						}
					}
        			
        		} catch (ItemFormatException ife) {
        			player.sendMessage("§cThe admin has misconfigured the item to give. " + ife.getError());
        		}
				break;
			
			///////////////////////////////////////
			case GIVE_SAVED_ITEM:
				Icon item = ChestCommands.giveItems.get(command);
				if (item != null) {
					HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(item.stack);
					if (!remainingItems.isEmpty()) {
						Configuration.noSpaceMessage(player);
						for (ItemStack remaining : remainingItems.values()) {
							player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(0.5)), remaining).setVelocity(new Vector(0.0, 0.0, 0.0));
						}
					}
				} else {
					player.sendMessage("§cThe item does not exist or was not loaded correctly in \"items.yml\". Please contact the admin.");
				}
				break;
				
				
				
			///////////////////////////////////////
			case GIVE_MONEY:
				if (!EconomyUtil.hasValidEconomy()) {
					player.sendMessage("§cSorry, no compatible economy plugin was found. Make sure that Vault is installed.");
					return;
				}
				
				if (!Utils.isValidPositiveDouble(command)) {
					player.sendMessage("§cThe item was misconfigured: invalid money amount §e" + command + "§c.");
					return;
				}
				
				EconomyUtil.giveMoney(player, Double.parseDouble(command));
				break;
					
			
			///////////////////////////////////////
			case BROADCAST:
				Bukkit.broadcastMessage(localCommand);
				break;
				
				
				
			///////////////////////////////////////
			case TELL:
				player.sendMessage(localCommand);
				break;
				
				
				
			///////////////////////////////////////
			default:
				player.chat("/" + localCommand);
				break;
			
		}
	}


	public enum CommandType { DEFAULT, CONSOLE,OP, OPEN_MENU, TELL, BROADCAST, SERVER, GIVE, GIVE_SAVED_ITEM, GIVE_MONEY }
}
