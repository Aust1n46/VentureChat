package mineverse.Aust1n46.chat.versions;

import org.bukkit.Bukkit;

//This class contains methods for determining what version of Minecraft the server is running.
public class VersionHandler {

	public static boolean is1_2_5() {
		return Bukkit.getVersion().contains("1.2.5");
	}

	public static boolean is1_3_2() {
		return Bukkit.getVersion().contains("1.3.2");
	}

	public static boolean is1_4_7() {
		return Bukkit.getVersion().contains("1.4.7");
	}

	public static boolean is1_5_2() {
		return Bukkit.getVersion().contains("1.5.2");
	}

	public static boolean is1_6_2() {
		return Bukkit.getVersion().contains("1.6.2");
	}

	public static boolean is1_6_4() {
		return Bukkit.getVersion().contains("1.6.4");
	}

	public static boolean is1_7_2() {
		return Bukkit.getVersion().contains("1.7") && Bukkit.getServer().getClass().getPackage().getName().contains("R1");
	}

	public static boolean is1_7_9() {
		return Bukkit.getVersion().contains("1.7") && Bukkit.getServer().getClass().getPackage().getName().contains("R3");
	}

	public static boolean is1_7_10() {
		return Bukkit.getVersion().contains("1.7") && Bukkit.getServer().getClass().getPackage().getName().contains("R4");
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
	
	public static boolean isCauldron() {
		return false;
	}

	public static boolean matchesVersion(String s) {
		return Bukkit.getVersion().contains(s) || Bukkit.getServer().getClass().getPackage().getName().contains(s);
	}
}