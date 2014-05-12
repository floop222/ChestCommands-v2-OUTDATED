package com.gmail.filoghost.chestcommands.components;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.gmail.filoghost.chestcommands.Configuration;
import com.gmail.filoghost.chestcommands.ErrorLogger;
import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.Permissions;
import com.gmail.filoghost.chestcommands.exception.ItemFormatException;
import com.gmail.filoghost.chestcommands.util.EconomyUtil;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.version.AttributeRemover;

public class IconMenu implements Listener {
	 
	private String yamlFile;
    private String name;
    private int size;
    private Sound openSound;
    
    private Icon[] icons;
   
    //constructor
    public IconMenu(String yamlFile, String name, int size) {
    	this.yamlFile = yamlFile;
        this.name = name;
        this.size = size;
        this.icons = new Icon[size];
    }
   
    public IconMenu setIcon(Icon icon, int x, int y) {
    	
    	//avoid ArrayOutOfBoundsException
    	int absolutePosition = Utils.getAbsolutePosition(x, y);
    	if (absolutePosition >= icons.length) return this;

    	
    	if (icons[absolutePosition] != null) {
    		ErrorLogger.addError("In the menu \"" + name + "\" two items have the same position: X=" + x + "  Y=" + y);
    		//let override
    	}
    	
    	icons[absolutePosition] = icon;
        return this;
    }
   
    public void open(final Player player) {
    	
    	final Inventory inventory = Bukkit.createInventory(player, size, name);
    	
    	for (int i = 0; i < icons.length; i++) {
    		if (icons[i] != null) {
    			inventory.setItem(i, ChestCommands.hideAttributes ? AttributeRemover.remove(icons[i].stack) : icons[i].stack);
    		}
    	}

    	Bukkit.getScheduler().scheduleSyncDelayedTask(ChestCommands.plugin, new Runnable() { public void run() {
    		player.openInventory(inventory);
    		if (openSound != null) {
        		player.playSound(player.getLocation(), openSound, 1F, 1F);
        	}
    	}}, 1L);
    }
   
    public void cleanIcons() {
    	icons = new Icon[size];
    }
   
    public void onInventoryClick(InventoryClickEvent event) {
            int slot = event.getRawSlot();
            
            //check if it's inside the menu
            if (slot >= 0 && slot < size) {
            	
            	final Player player = (Player) event.getWhoClicked();
            	Icon icon = icons[slot];
            	
            	//it may be null
            	if (icon == null) return;
            	
            	//schedule inventory close
            	if (!icon.keepOpen) {
                    player.closeInventory();
            	}

            	String cmd = icon.commands[0].getCommand();
            	double price = icon.price;
            	
            	if (cmd == null || cmd.length() == 0) return;
            	
            	/*
            	 * Permission check
            	 */
            	if (icon.permission != null) {
            		if (!player.hasPermission(icon.permission)) {
            			Configuration.noIconPermissionMessage(player, icon.permission);
            			return;
            		}
            	}
            	
            	/*
            	 * Economy check
            	 */
            	if (price > 0.0) {
            		
            		if (player.hasPermission(Permissions.ECONOMY_BYPASS)) {
            			Configuration.economyBypassMessage(player);
            		} else {
            		
            			if (!EconomyUtil.hasValidEconomy()) {
            				player.sendMessage("§cThis command has a price, but no compatible economy plugin was found. For security, the command has been blocked.");
            				ChestCommands.log.warning("You specified a price for the item named \"" + icon.getNameForErrors() + "\" but you don't have any compatible economy plugin. The command was blocked.");
            				return;
            			}
                  		
            			if (!EconomyUtil.hasMoney(player, price)) {
            				Configuration.noMoneyMessage(player, price);
            				return;
            			}
            		}
            	}
            	
            	
            	/*
            	 * Required item check
            	 */            	
            	RequiredItem requiredItem = null;
            	if (icon.requiredItem != null) {
            		
            		try {
						requiredItem = RequiredItem.fromString(icon.requiredItem);
					} catch (ItemFormatException e) {
						player.sendMessage("§cThe admin has misconfigured the required item. " + e.getError());
						ChestCommands.log.warning("The REQUIRED-ITEM in the file " + yamlFile + " is not valid. " + ChatColor.stripColor(e.getError()));
						return;
					}
            		
            		if (!requiredItem.hasItem(player)) {
            			Configuration.noRequiredItemMessage(player, requiredItem.createStack(), requiredItem.isDurabilityRestrictive);
            			return;
            		}
            	}
            	
            	if (price > 0.0 && EconomyUtil.hasValidEconomy() && !player.hasPermission(Permissions.ECONOMY_BYPASS)) {
            		EconomyUtil.takeMoney(player, price);
            	}
            	if (requiredItem != null) {
            		requiredItem.takeItem(player);
            	}

            	
            	for (Command c : icon.commands) {
            		c.execute(player);
            	}
            }
    }

	public String getFileName() {
		return yamlFile;
	}
	
	public String getOpenPermission() {
		return "chestcommands.open." + yamlFile;
	}
	
	public String getOpenWithItemPermission() {
		return "chestcommands.item." + yamlFile;
	}
	
	public void setOpenSound(String sound) {
		try {
			this.openSound = Sound.valueOf(sound.toUpperCase().replace("-", "_").replace(" ", "_"));
		} catch (IllegalArgumentException ex) {
			ErrorLogger.addError("Unknown open-sound \"" + sound + "\" in the file \"" + yamlFile + "\".");
		}
	}

}
