package com.gmail.filoghost.chestcommands.exception;

@SuppressWarnings("serial")
public class ItemFormatException extends Exception {

	private String error;
	
	public ItemFormatException(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
}
