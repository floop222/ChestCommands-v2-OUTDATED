package com.gmail.filoghost.chestcommands.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AsciiSymbols {
	
	public static Map<String, String> replacements = new HashMap<String, String>();
	
	static {
		replacements.put("<3", 		"\u2764"); //heart
		replacements.put("[*]", 	"\u2605"); //black star
		replacements.put("[**]", 	"\u2739"); //twelve pointed black star
		replacements.put("[p]", 	"\u25CF"); //black point
		replacements.put("[v]", 	"\u2714"); //check mark
		replacements.put("[+]", 	"\u25C6"); //small diamond
		replacements.put("[++]",	"\u2726"); //black four pointed star
		replacements.put("[x]", 	"\u2588"); //full black block
		replacements.put("[/]", 	"\u258C"); //half black block
		replacements.put("[cross]", "\u2720"); //maltese cross
		replacements.put("[arrow_right]", 	"\u27A1");
		replacements.put("[arrow_down]", 	"\u2B07");
		replacements.put("[arrow_left]", 	"\u2B05");
		replacements.put("[arrow_up]", 		"\u2B06");

	}
	
	public static String placeholdersToSymbols(String s) {
		
		if (s == null) return null;
		
		for (Entry<String, String> entry : replacements.entrySet()) {
			s = s.replace(entry.getKey(), entry.getValue());
		}
		
		return s;
	}
	
	public static String symbolsToPlaceholders(String s) {
		
		if (s == null) return null;
		
		for (Entry<String, String> entry : replacements.entrySet()) {
			s.replace(entry.getValue(), entry.getKey());
		}
		
		return s;
		
	}
	
	public static String toAscii(String s) {
		if (s == null) return null;
		
		return s.replaceAll("\\P{ASCII}", "");
	}
	
}
