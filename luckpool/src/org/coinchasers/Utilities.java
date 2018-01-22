package org.coinchasers;

public class Utilities {
	
//	Methods with no one true home
	
	public Utilities() {
		
	}
	
	public static String capitiliseString(String string) {
		String[] split = string.split("");
		String sorted = "";
		sorted = sorted + split[0].toUpperCase();
		for(int x = 1; x < split.length; x++) {
			sorted = sorted + split[x];
		}
		return sorted;
	}
	
	public static String decapitiliseString(String string) {
		String[] split = string.split("");
		String sorted = "";
		sorted = sorted + split[0].toLowerCase();
		for(int x = 1; x < split.length; x++) {
			sorted = sorted + split[x];
		}
		return sorted;
	}
	
}