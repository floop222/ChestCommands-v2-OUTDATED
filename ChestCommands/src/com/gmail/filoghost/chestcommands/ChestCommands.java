package com.gmail.filoghost.chestcommands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.filoghost.chestcommands.Configuration.ConfigNode;
import com.gmail.filoghost.chestcommands.components.AttachedItem;
import com.gmail.filoghost.chestcommands.components.Command;
import com.gmail.filoghost.chestcommands.components.Icon;
import com.gmail.filoghost.chestcommands.components.IconMenu;
import com.gmail.filoghost.chestcommands.exception.ArmorColorException;
import com.gmail.filoghost.chestcommands.exception.ItemFormatException;
import com.gmail.filoghost.chestcommands.util.EconomyUtil;
import com.gmail.filoghost.chestcommands.util.InventoryUtil;
import com.gmail.filoghost.chestcommands.util.ItemStackReader;
import com.gmail.filoghost.chestcommands.util.Utils;
import com.gmail.filoghost.chestcommands.version.AttributeRemover;

public class ChestCommands extends JavaPlugin {
	
	public static Logger log;
	BukkitScheduler scheduler;
	public static ChestCommands plugin;
	
	public static boolean hideAttributes;
	
	public static List<AttachedItem> attachedItems = new ArrayList<AttachedItem>();
	public static Map<String,IconMenu> yamlFileNameAndMenu;
	public static Map<String,IconMenu> commandAndMenu;
	public static Map<String,IconMenu> menuNameAndMenu;
	public static Map<String, Icon> giveItems;
	
	public static final String PLUGIN_PREFIX = "§2[§aChestCommands§2] ";

	@Override
	public void onEnable() {
		scheduler = this.getServer().getScheduler();
		plugin = this;
		log = getLogger();
		
		//command executor
		getCommand("chestcommands").setExecutor(new CommandHandler(this));

		//register events
		getServer().getPluginManager().registerEvents(new TheListener(), this);
		
		//copy the changelog
		saveResource("changelog.txt", true);
		
		//load the files
		loadConfiguration();
		
		//print errors
		ErrorLogger.printErrors();
		
		//setup for economy
		if (EconomyUtil.setupEconomy()) {
			log.info("Economy plugin found, economy support enabled!");
		} else {
			log.info("Economy not found, economy support disabled!");
		}
		
		//setup the updater
		Updater.UpdaterHandler.setup(this, 56919, PLUGIN_PREFIX, super.getFile(), ChatColor.GREEN, "/chc update", "chest-commands");
		if (ConfigNode.UPDATE_NOTIFICATIONS.getBoolean()) {
			Updater.UpdaterHandler.startupUpdateCheck();
		}
		
		//send metrics
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (Exception e) {}		
	}
	
	@Override
	public void onDisable() {
	}

	
	/**
	 * @return the icon, or null if there were errors.
	 */
	@SuppressWarnings("deprecation")
	public Icon loadIconFromConfigurationSection(ConfigurationSection node, String filename) {
		String cmd = 				node.getString("COMMAND");
		String name =				node.getString("NAME");
		List<String> description =	node.getStringList("LORE");
		String itemString = 		node.getString("ID");
		short data = (short) 		node.getInt("DATA-VALUE");
		double price = 				node.getDouble("PRICE");
		int amount = 				node.getInt("AMOUNT");
		String enchString = 		node.getString("ENCHANTMENT");
		boolean keepOpen = 			node.getBoolean("KEEP-OPEN");
		String perm = 				node.getString("PERMISSION");
		String requiredItem = 		node.getString("REQUIRED-ITEM");
		String color = 				node.getString("COLOR");
		
		int itemId = 7;
		
		if (itemString == null) {
			itemString = "BEDROCK";
			ErrorLogger.addError("The item \"" + node.getName() + "\" in the file \"" + filename + "\" has no ID.");
			if (description != null) description.add("§cID not set.");
		}
		
		try {
			ItemStackReader reader = new ItemStackReader(itemString);
			itemId = reader.getMaterial().getId();
			if (reader.hasDataValue()) data = reader.getDataValue();
			if (reader.hasAmount()) amount = reader.getAmount();
		} catch (ItemFormatException ex) {
			if (description != null) description.add("§cInvalid ID.");
			ErrorLogger.addError("The item \"" + node.getName() + "\" in the file \"" + filename + "\" has an invalid ID. " + ex.getError());
			if (filename.equals("items.yml")) return null;
		}

		Icon icon = new Icon(Material.getMaterial(itemId));
		
		if (price < 0.0) ErrorLogger.addError("The item \"" + node.getName() + "\" in the file \"" + filename + "\" has a negative PRICE.");
		else icon.setPrice(price);
		
		if (InventoryUtil.isLeatherArmor(Material.getMaterial(itemId)) && color != null) {
			LeatherArmorMeta armorMeta = (LeatherArmorMeta) icon.stack.getItemMeta();
			try {
				armorMeta.setColor(InventoryUtil.readArmorColor(color));
				icon.stack.setItemMeta(armorMeta);
			} catch (ArmorColorException ex) {
				ErrorLogger.addError("The armor \"" + node.getName() + "\" in the file \"" + filename + "\" has an invalid COLOR: " + ex.getError());
			}
		}
		
		icon.setRequiredItem(requiredItem);
		icon.setDurability(data);
		icon.setAmount(amount);
		icon.setNameAndLore(name, description);
		icon.setCommands(Command.arrayFromString(cmd));
		icon.setKeepOpen(keepOpen);
		icon.setPermission(perm);
		icon.addEnchantmentBundleArray(Utils.getEnchantmentsBundleFromString(enchString, node.getName(), filename));
		
		return icon;
	}
	
	public void loadConfiguration() {
		
		if (yamlFileNameAndMenu != null) {
			for (IconMenu menuToDestroy : yamlFileNameAndMenu.values()) {
				menuToDestroy.cleanIcons();
			}
		}
		
		//reset all the hashmaps
		yamlFileNameAndMenu = new HashMap<String, IconMenu>();
		commandAndMenu = new HashMap<String, IconMenu>();
		menuNameAndMenu = new HashMap<String, IconMenu>();
		giveItems = new HashMap<String, Icon>();
		attachedItems = new ArrayList<AttachedItem>();
		
		//does everything: copy the file, add missing nodes, etc
		Configuration.loadAndCopyMissingNodes();

		hideAttributes = ConfigNode.HIDE_ATTRIBUTES.getBoolean();
		if (hideAttributes) {
			if (!AttributeRemover.setup()) {
				ErrorLogger.addError("Could not find a compatible NMS code for this bukkit version. Attributes will show up on items."
						+ "If you're using a new version, expect an update soon. The plugin will still work.");
				hideAttributes = false;
			}
		}
		
		
		File menuFolder = new File(getDataFolder(), File.separator + "menu");
		if (!menuFolder.exists()) {
			menuFolder.mkdirs();
			loadFile("menu" + File.separator + "main-menu.yml");
			loadFile("menu" + File.separator + "example.yml");
			loadFile("menu" + File.separator + "plugin-tutorial.yml");
			loadFile("menu" + File.separator + "admin-console.yml");
			loadFile("menu" + File.separator + "simple-shop.yml");			
		}
		
		List<File> filesList = new ArrayList<File>();
		
		File[] files = menuFolder.listFiles();
		if (files != null && files.length > 0) {
			for (File f : files) {
				if (f.isFile()) {
					filesList.add(f);
				} else if (f.isDirectory()) {
					File[] subDirFiles = f.listFiles();
					if (subDirFiles != null && subDirFiles.length > 0) {
						for (File subDirFile : subDirFiles) {
							if (subDirFile.isFile()) {
								filesList.add(subDirFile);
							}
						}
					}
				}
			}
		}
		
		
		for (File listFile : filesList) {
			String filename = listFile.getName();
			if (filename.endsWith(".yml")) {
				log.info("Loading menu: " + filename);
				
				FileConfiguration menuConfig = loadFile(listFile);
				if (menuConfig != null) {
					loadMenu(menuConfig, filename);
				} else {
					ErrorLogger.addError("Could not load menu: " + filename);
				}
			}
		}
		
		
		FileConfiguration itemsYaml = loadFile("items.yml");
		for (String itemInternalName : itemsYaml.getKeys(false)) {

			Icon itemToGive = loadIconFromConfigurationSection(itemsYaml.getConfigurationSection(itemInternalName), "items.yml");
			
			if (itemToGive != null) {
				giveItems.put(itemInternalName, itemToGive);
			}
			
		}
	}
	
	
	public FileConfiguration loadFile(File file) {
		if (file == null || !file.exists()) return null;
		return YamlConfiguration.loadConfiguration(file);
	}
	

	public FileConfiguration loadFile(String fileName) {
		
		File file = new File(getDataFolder(), fileName);
		
		 if (!file.exists()) {
	        	try {
	        	saveResource(fileName, false);
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        		log.severe("Couldn't save " + fileName + " to disk!");
	        		return null;
	        	}
	     }
		return YamlConfiguration.loadConfiguration(file);
	}
	
	@SuppressWarnings("deprecation")
	public void loadMenu(FileConfiguration menuConfig, String fileName) {
		
		if (menuConfig == null) {
			ErrorLogger.addError("The menu " + fileName + " cannot be loaded (is null), check previous errors.");
			return;
		}
		
		//required fields
		String menuName = menuConfig.getString("menu-settings.name");
		int menuRows = menuConfig.getInt("menu-settings.rows");
		
		//optional fields
		String menuCommand = menuConfig.getString("menu-settings.command");
		String menuItem = menuConfig.getString("menu-settings.open-with-item.item-id");
		boolean menuLeftClick = menuConfig.getBoolean("menu-settings.open-with-item.left-click");
		boolean menuRightClick = menuConfig.getBoolean("menu-settings.open-with-item.right-click");
		
		if (menuName == null || menuName.length() == 0) {
			ErrorLogger.addError("The menu " + fileName + " cannot be loaded: file not loaded correctly or 'name' not found. Check your menu settings and possible previous YAML formatting errors.");
			return;
		}
		if (menuRows == 0) {
			ErrorLogger.addError("The menu " + fileName + " cannot be loaded: Cannot find 'rows'. Check your menu-settings");
			return;
		}
		
		menuName = "§r" + Utils.colorize(menuName); //to check later if this is a command chest
		
		if (menuName.length() > 32) menuName = "§rError, name too long!";
		
		//load the menu
		IconMenu menu = new IconMenu(fileName, menuName, (menuRows*9));
		 
		for (String menuNode : menuConfig.getKeys(false)) { if (!menuNode.equals("menu-settings")) {
			
			ConfigurationSection confSection = menuConfig.getConfigurationSection(menuNode);

			int posX = 					confSection.getInt("POSITION-X");
			int posY = 					confSection.getInt("POSITION-Y");

			if (posX == 0 || posY == 0) {
				ErrorLogger.addError("The item \"" + menuNode + "\" in the file \"" + fileName + "\" has a POSITION that is 0 or missing.");
				continue;
			}
			
			Icon icon = loadIconFromConfigurationSection(confSection, fileName);
			if (icon != null) {
				menu.setIcon(icon, posX, posY);
			}
			
		}}
		
		if (menuNameAndMenu.containsKey(menuName)) {
			ErrorLogger.addError("The menu name in the file \"" + fileName + "\" is the same of another: cannot load two menus with the same name.");
			return;
		}
		menuNameAndMenu.put(menuName, menu);
		yamlFileNameAndMenu.put(fileName, menu);
		
		//further operations
		if (menuCommand != null && menuCommand.length() > 0) {
			if (!menuCommand.contains(";")) {
				commandAndMenu.put(menuCommand.trim().toLowerCase(), menu);
			} else {
				for (String splitCommand : menuCommand.split(";")) {
					commandAndMenu.put(splitCommand.trim().toLowerCase(), menu);
				}
			}
		}
		
		if (menuItem != null) {
			
			Material material;
			
			if (Utils.isValidPositiveInteger(menuItem)) {
				material = Material.getMaterial(Integer.parseInt(menuItem));
			} else {
				material = Utils.matchMaterial(menuItem);
			}
			
			if (material != null) {
				if (menuLeftClick || menuRightClick) {
					AttachedItem attachedItem = new AttachedItem(menu, material.getId());
					//set data value only if present
					if (menuConfig.isSet("menu-settings.open-with-item.data-value")) {
						attachedItem.setRestrictiveData((short) menuConfig.getInt("menu-settings.open-with-item.data-value"));
					}
				
					//bind click types
					if (menuLeftClick && menuRightClick) attachedItem.setValidActions(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
					else if (menuLeftClick && !menuRightClick) attachedItem.setValidActions(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);
					else if (!menuLeftClick && menuRightClick) attachedItem.setValidActions(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
					else attachedItem.setValidActions(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK); //default = right click (should never be possible)
				
					attachedItems.add(attachedItem);
				}
			} else {
				ErrorLogger.addError("Invalid item-id \"" + menuItem + "\" in the file \"" + fileName + "\".");
			}
		}
		
		if (menuConfig.isSet("menu-settings.open-sound")) {
			menu.setOpenSound(menuConfig.getString("menu-settings.open-sound"));
		}
	}

	
	public static void openWithPermission(IconMenu menu, Player player) {
		
		
		if (player.hasPermission(menu.getOpenPermission())) {
			menu.open(player.getPlayer());
		} else {
			player.sendMessage("§cYou don't have permission §e" + menu.getOpenPermission());
		}
		
		
	}
	
	public static void openYamlWithPermission(String file, Player player) {
		
		if (!file.endsWith(".yml")) file = file + ".yml";
		
		IconMenu menu = yamlFileNameAndMenu.get(file);
		if (menu == null) {
			player.sendMessage("§cCan't find the menu §e" + file + "§c. Check the console for possible errors.");
			return;
		}
		
		if (!player.hasPermission(menu.getOpenPermission())) {
			player.sendMessage("§cYou don't have permission §e" + menu.getOpenPermission());
			return;
		}
		
		menu.open(player.getPlayer());
	}
	
	public static void openYamlWithoutPermission(String file, Player player) {
		
		if (!file.endsWith(".yml")) file = file + ".yml";
		
		IconMenu menu = yamlFileNameAndMenu.get(file);
		if (menu == null) {
			player.sendMessage("§cCan't find the menu §e" + file + "§c. Check the console for possible errors.");
			return;
		}
		
		menu.open(player.getPlayer());
	}

    public File getPluginFile() {
    	return this.getFile();
    }
	
}