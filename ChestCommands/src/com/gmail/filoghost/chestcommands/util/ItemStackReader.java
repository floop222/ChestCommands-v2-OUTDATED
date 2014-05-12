package com.gmail.filoghost.chestcommands.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.chestcommands.exception.ItemFormatException;

public class ItemStackReader {

	private boolean hasDataValue = false;
	private boolean hasAmount = false;
	private Material mat = Material.STONE; //in the worst case (bad exception handling) we just get stone
	private int amount = 1;
	private short dataValue = 0;
	
	
	
	/**
	 * Reads item in the format "id:data, amount"
	 * id can be either the id of the material or its name.
	 * for example wool:5, 3 is a valid input.
	 */
	@SuppressWarnings("deprecation")
	public ItemStackReader(String input) throws ItemFormatException {
		
		if (input == null) {
			throw new ItemFormatException("Invalid 'null' item");
		}
		
		input = input.replace(" ", ""); //cut spaces
		
		/*
		 * Read the eventual amount and cut it from the input string
		 */
		String[] splitByComma = input.split(",");
		
		if (splitByComma.length > 1) {
			if (!Utils.isValidPositiveInteger(splitByComma[1])) throw new ItemFormatException("Invalid amount: \"" + splitByComma[1] + "\"");
			amount = Integer.parseInt(splitByComma[1]);
			if (amount == 0) throw new ItemFormatException("Invalid amount: \"" + splitByComma[1] + "\"");
			hasAmount = true;
			
			input = splitByComma[0]; //cut down the amount
		}
		
		
		/*
		 * Read the eventual data value and cut it from the input string
		 */
		String[] splitByColons = input.split(":");
		
		if (splitByColons.length > 1) {
			if (!Utils.isValidPositiveShort(splitByColons[1])) throw new ItemFormatException("Invalid data value: \"" + splitByColons[1] + "\"");
			dataValue = Short.parseShort(splitByColons[1]);
			hasDataValue = true;
			
			input = splitByColons[0]; //cut down the data value
		}
		
		Material material;
		
		if (Utils.isValidPositiveInteger(input)) {
			material = Material.getMaterial(Integer.parseInt(input));
		} else {
			material = Utils.matchMaterial(input);
		}
		
		if (material == null || material == Material.AIR) throw new ItemFormatException("Invalid ID: \"" + input + "\"");
		this.mat = material;
	}
	
	public boolean hasDataValue() {
		return hasDataValue;
	}
	
	public boolean hasAmount() {
		return hasAmount;
	}
	
	public Material getMaterial() {
		return mat;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public short getDataValue() {
		return dataValue;
	}
	
	public ItemStack createStack() {
		return new ItemStack(mat, amount, dataValue);
	}
	
}
