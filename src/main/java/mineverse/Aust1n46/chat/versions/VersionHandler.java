package mineverse.Aust1n46.chat.versions;

import org.bukkit.Bukkit;

//This class contains methods for determining what version of Minecraft the server is running.
public class VersionHandler {

	public static boolean is1_7() {
		return Bukkit.getVersion().contains("1.7");
	}
	
	public static boolean is1_8() {
		return Bukkit.getVersion().contains("1.8");
	}
	
	public static boolean is1_9() {
		return Bukkit.getVersion().contains("1.9");
	}
	
	public static boolean is1_10() {
		return Bukkit.getVersion().contains("1.10");
	}
	
	public static boolean is1_11() {
		return Bukkit.getVersion().contains("1.11");
	}
	
	public static boolean is1_12() {
		return Bukkit.getVersion().contains("1.12");
	}
	
	public static boolean is1_13() {
		return Bukkit.getVersion().contains("1.13");
	}
	
	public static boolean is1_14() {
		return Bukkit.getVersion().contains("1.14");
	}
	
	public static boolean is1_14_4() {
		return Bukkit.getVersion().contains("1.14.4");
	}
	
	public static boolean is1_15() {
		return Bukkit.getVersion().contains("1.15");
	}
	
	public static boolean is1_16() {
		return Bukkit.getVersion().contains("1.16");
	}
	
	public static boolean is1_17() {
		return Bukkit.getVersion().contains("1.17");
	}
	
	public static boolean is1_18() {
		return Bukkit.getVersion().contains("1.18");
	}
	
	public static boolean is1_19() {
		return Bukkit.getVersion().contains("1.19");
	}
	
	public static boolean isUnder_1_19() {
		return is1_7() || is1_8() || is1_9() || is1_10() || is1_11() || is1_12() || is1_13() || is1_14() || is1_15() || is1_16() || is1_17() || is1_18();
	}
}
