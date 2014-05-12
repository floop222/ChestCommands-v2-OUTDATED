package com.gmail.filoghost.chestcommands.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;

import com.gmail.filoghost.chestcommands.ChestCommands;
import com.gmail.filoghost.chestcommands.ErrorLogger;

public class Utils {

	public static String colorize(String input) {
		if (input == null) return null;
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
	public static String colorizeAndAddSymbols(String input) {
		if (input == null) return input;
		return AsciiSymbols.placeholdersToSymbols(ChatColor.translateAlternateColorCodes('&', input));
	}
	
	
	public static Material matchMaterial(String input) {
		if (input == null) return null;
		input = input.toLowerCase().replace(" ", "").replace("_", "");
		for (Material mat : Material.values()) {
			if (mat.toString().toLowerCase().replace("_", "").equals(input)) return mat;
		}
		return null;
	}
	
	
	public static String getFormattedMaterial(Material mat) {
		return capitalizeFully(mat.toString().replace("_", " "));
	}
	
	public static EnchantmentBundle[] getEnchantmentsBundleFromString(String input, String nodeName, String fileName) {
		if (input == null || input.length() == 0) return null;
		String[] split = input.split(";");
		EnchantmentBundle[] result = new EnchantmentBundle[split.length];
		
		for (int i = 0; i < split.length; i++) {
			
			StringTokenizer st = new StringTokenizer(split[i], "\\,");
			String enchName = st.nextToken();
			
			Enchantment ench = null;
			try {
				ench = getEnchantmentFromString(enchName);
			} catch (IllegalArgumentException ex) {
				ErrorLogger.addError("The item \"" + nodeName + "\" in the file \"" + fileName + "\" contains an unknown enchantment: \"" + enchName + "\".");
				continue;
			}

			if (ench == null) continue;
			EnchantmentBundle bundle = new EnchantmentBundle(ench);
			
			if (st.hasMoreTokens()) {
				String enchLevel = st.nextToken().trim();
				if (isValidPositiveInteger(enchLevel)) {
					bundle.level = Integer.parseInt(enchLevel);
				} else {
					ErrorLogger.addError("The item \"" + nodeName + "\" in the file \"" + fileName + "\" contains an invalid enchantment level: \"" + enchLevel + "\".");
					continue;
				}
			}
			
			result[i] = bundle;
		}
		
		return result;
	}
	
	public static Enchantment getEnchantmentFromString(String input) throws IllegalArgumentException {
		
		if (input == null || input.length() == 0) return null;
		
		String formattedInput = input.toLowerCase().replace("_", "").replace(" ", "");
		
		if (formattedInput.equals("protection")) return 					Enchantment.PROTECTION_ENVIRONMENTAL;
		else if (formattedInput.equals("fireprotection")) return 			Enchantment.PROTECTION_FIRE;
		else if (formattedInput.equals("featherfalling")) return 			Enchantment.PROTECTION_FALL;
		else if (formattedInput.equals("blastprotection")) return 			Enchantment.PROTECTION_EXPLOSIONS;
		else if (formattedInput.equals("projectileprotection")) return 		Enchantment.PROTECTION_PROJECTILE;
		else if (formattedInput.equals("respiration")) return 				Enchantment.OXYGEN;
		else if (formattedInput.equals("aquaaffinity")) return 				Enchantment.WATER_WORKER;
		else if (formattedInput.equals("thorns")) return 					Enchantment.THORNS;
		else if (formattedInput.equals("sharpness")) return 				Enchantment.DAMAGE_ALL;
		else if (formattedInput.equals("smite")) return 					Enchantment.DAMAGE_UNDEAD;
		else if (formattedInput.equals("baneofarthropods")) return			Enchantment.DAMAGE_ARTHROPODS;
		else if (formattedInput.equals("knockback")) return 				Enchantment.KNOCKBACK;
		else if (formattedInput.equals("fireaspect")) return				Enchantment.FIRE_ASPECT;
		else if (formattedInput.equals("looting")) return 					Enchantment.LOOT_BONUS_MOBS;
		else if (formattedInput.equals("efficiency")) return 				Enchantment.DIG_SPEED;
		else if (formattedInput.equals("silktouch")) return 				Enchantment.SILK_TOUCH;
		else if (formattedInput.equals("unbreaking")) return 				Enchantment.DURABILITY;
		else if (formattedInput.equals("fortune")) return					Enchantment.LOOT_BONUS_BLOCKS;
		else if (formattedInput.equals("power")) return 					Enchantment.ARROW_DAMAGE;
		else if (formattedInput.equals("punch")) return						Enchantment.ARROW_KNOCKBACK;
		else if (formattedInput.equals("flame")) return 					Enchantment.ARROW_FIRE;
		else if (formattedInput.equals("infinity")) return 					Enchantment.ARROW_INFINITE;
		else if (formattedInput.equals("lure")) return						Enchantment.LURE;
		else if (formattedInput.equals("luckofthesea")) return				Enchantment.LUCK;
		
		
		Enchantment ench = Enchantment.getByName(input.toUpperCase().replace(" ", "_"));
		if (ench == null) {
			System.out.println(formattedInput);
			throw new IllegalArgumentException("Unknown enchantment \"" + input + "\"");
		}
		return ench;
	}
	
	
	
	public static boolean isValidPositiveDouble(String input) {
		try {
			double d = Double.parseDouble(input);
			return d >= 0.0;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	
	public static boolean isValidPositiveInteger(String input) {
		try {
			int i = Integer.parseInt(input);
			return i >= 0;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	
	
	public static boolean isValidPositiveShort(String input) {
		try {
			short s = Short.parseShort(input);
			return s >= 0;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	
	
	public static boolean isValidDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	
	public static boolean isValidInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	public static boolean isValidShort(String input) {
		try {
			Short.parseShort(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	
	/**
	 *            - A CHEST -
	 *  
	 *     X  1  2  3  4  5  6  7  8  9
	 *  Y    __ __ __ __ __ __ __ __ __
	 *  1   | 0| 1| 2| 3| 4| 5| 6| 7| 8|
	 *  2   | 9|10|11|12|13|14|15|16|17|
	 *  3   |18|19|20|21|22|23|24|25|26|
	 *  4   |27|28|29|30|31|32|33|34|35|
	 *  5   |36|37|38|39|40|41|42|43|44|
	 *  6   |45|46|47|48|49|50|51|52|53|
	 *  
	 *   input: x = 4, y = 6
	 *   output: 48
	 */
	public static int getAbsolutePosition(int x, int y) {
		

		x--;
		y--;
		
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		
		int r = y*9 + x;
		
		if (r < 0) r = 0;
		
		return r;
	}
	
	
	public static boolean connectToBungeeServer(Player player, String server) {
		
		try {
			
			Messenger messenger = Bukkit.getMessenger();
			if (!messenger.isOutgoingChannelRegistered(ChestCommands.plugin, "BungeeCord")) {
				messenger.registerOutgoingPluginChannel(ChestCommands.plugin, "BungeeCord");
			}
			
			if (server.length() == 0) {
				player.sendMessage("§cTarget server was \"\" (empty string) cannot connect to it.");
				return false;
			}
		
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteArray);
		 
			out.writeUTF("Connect");
			out.writeUTF(server); // Target Server
		    
			player.sendPluginMessage(ChestCommands.plugin, "BungeeCord", byteArray.toByteArray());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			player.sendMessage("§cAn exception has occurred. Please notify OPs about this. (They should look at the console).");
			ChestCommands.log.warning("Could not handle BungeeCord command from " + player.getName() + ": tried to connect to \"" + server + "\".");
			return false;
		}
		
		return true;
	}
	
	public static String capitalizeFully(String input) {
		if (input == null) return null;
		
		String s = input.toLowerCase();
		
		int strLen = s.length();
	    StringBuffer buffer = new StringBuffer(strLen);
	    boolean capitalizeNext = true;
	    for (int i = 0; i < strLen; i++) {
	      char ch = s.charAt(i);

	      if (Character.isWhitespace(ch)) {
	        buffer.append(ch);
	        capitalizeNext = true;
	      } else if (capitalizeNext) {
	        buffer.append(Character.toTitleCase(ch));
	        capitalizeNext = false;
	      } else {
	        buffer.append(ch);
	      }
	    }
	    return buffer.toString();
	}
	
	public static String formatTitle(String title) {
		return "§a§m-------§r  §a§l" + title + "  §a§m-------";
	}
	
}
