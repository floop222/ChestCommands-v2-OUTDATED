package com.gmail.filoghost.chestcommands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gmail.filoghost.chestcommands.components.AttachedItem;
import com.gmail.filoghost.chestcommands.components.IconMenu;

public class TheListener implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
		
		String invName = event.getInventory().getTitle();		
		if (!invName.startsWith("§r")) return; //safeguard against bugs
		event.setCancelled(true);
		
		IconMenu menu = ChestCommands.menuNameAndMenu.get(invName);
		if (menu == null) return;
		
		menu.onInventoryClick(event); //pass the event to the menu
	}
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void interactEvent(PlayerInteractEvent event) {

		Player p = event.getPlayer();	
		Action a = event.getAction();
		
		//Cartelli
		if (a.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();
			
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equalsIgnoreCase("§1[menu]")) {
					//Found a menu!
					String name = sign.getLine(1);					
					ChestCommands.openYamlWithPermission(name, p);
					event.setCancelled(true);
					return;
				}
			}	
		}
		
		for (AttachedItem boundItem : ChestCommands.attachedItems) {
			
			if (boundItem.isValidTrigger(p.getItemInHand(), event.getAction())) {
				
				if (p.hasPermission(boundItem.menu.getOpenWithItemPermission())) {
					event.setCancelled(true);
					boundItem.menu.open(p);
				}
			}
		}
	}
	
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void handleCommandEvent(PlayerCommandPreprocessEvent e) {		
		//get the command without the slash
		String cmd = e.getMessage().substring(1);
		
		//checks if the string is empty
		if (cmd.length() == 0) return;
		
		IconMenu menu = ChestCommands.commandAndMenu.get(cmd.toLowerCase());
		
		if (menu != null) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			System.out.println(p.getName() + " issued server command: /" + cmd);	
			ChestCommands.openWithPermission(menu, p);
		}
	}
	
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void signChange(SignChangeEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission(Permissions.SIGN_CREATE)) return;
		String[] lines = event.getLines();
		
		if (lines[0].equalsIgnoreCase("[menu]")) {
			
			//sign creation
			String inputName = lines[1];
			
			if (inputName.length() == 0 || inputName == null) {
				event.setLine(0, "§4" + lines[0]);
				p.sendMessage("§cYou must set a valid menu name in the second line. (Was empty)");
				return;
			}
			
			if (!inputName.endsWith(".yml")) inputName = inputName + ".yml";
			
			if (API.isExistingMenu(inputName)) {
				//valid menu
				event.setLine(0, "§1" + lines[0]);
				p.sendMessage("§aSuccessfully created a sign for the menu " + inputName + ".");
			} else {
				//invalid!
				event.setLine(0, "§4" + lines[0]);
				p.sendMessage("§cCan't find the menu §e" + inputName + "§c. Check the console for possible errors, and remember that the name is CaSe SeNSiTivE.");
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Updater.UpdaterHandler.notifyIfUpdateWasFound(event.getPlayer(), Permissions.COMMAND_UPDATE);
	}

}
