package com.gmail.filoghost.chestcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ErrorLogger {

	private static List<String> errors = new ArrayList<String>();

	public static void addError(String error) {
		
		if (error == null || error.length() == 0) return;
		
		errors.add(ChatColor.stripColor(error));
		
	}
	
	public static int getAmount() {
		return errors.size();
	}
	
	public static void printErrors() {
		
		if (errors.size() == 0) return;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(ChestCommands.plugin, new Runnable() { public void run() {
			
			System.out.println("----------------------------------------------------------");
			
			for (String error : errors) {
				ChestCommands.log.info(error);
			}
			
			System.out.println("----------------------------------------------------------");
			
			errors = new ArrayList<String>();
			
		}}, 1L);
	}
}
