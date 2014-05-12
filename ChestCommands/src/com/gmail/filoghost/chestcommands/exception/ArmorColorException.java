package com.gmail.filoghost.chestcommands.exception;

@SuppressWarnings("serial")
public class ArmorColorException extends Exception {

	private String error;
	
	public ArmorColorException(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
}
