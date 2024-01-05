package mineverse.Aust1n46.chat.versions;

import com.comphenix.protocol.utility.MinecraftVersion;

public final class VersionHandler {

	public static final MinecraftVersion SERVER_VERSION = MinecraftVersion.getCurrentVersion();
	private static final MinecraftVersion MC1_19 = new MinecraftVersion(1, 19, 0);
	private static final MinecraftVersion MC1_19_1 = new MinecraftVersion(1, 19, 1);
	private static final MinecraftVersion MC1_20_4 = new MinecraftVersion(1, 20, 4);

	private VersionHandler() {
	}

	public static boolean is1_7() {
		return SERVER_VERSION.getMinor() == 7 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_8() {
		return SERVER_VERSION.getMinor() == 8 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_9() {
		return SERVER_VERSION.getMinor() == 9 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_10() {
		return SERVER_VERSION.getMinor() == 10 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_11() {
		return SERVER_VERSION.getMinor() == 11 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_12() {
		return SERVER_VERSION.getMinor() == 12 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_13() {
		return SERVER_VERSION.getMinor() == 13 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_14() {
		return SERVER_VERSION.getBuild() != 4 && SERVER_VERSION.getMinor() == 14 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_14_4() {
		return SERVER_VERSION.getBuild() == 4 && SERVER_VERSION.getMinor() == 14 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_15() {
		return SERVER_VERSION.getMinor() == 15 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_16() {
		return SERVER_VERSION.getMinor() == 16 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_17() {
		return SERVER_VERSION.getMinor() == 17 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_18() {
		return SERVER_VERSION.getMinor() == 18 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean is1_19() {
		return SERVER_VERSION.getBuild() == 0 && SERVER_VERSION.getMinor() == 19 && SERVER_VERSION.getMajor() == 1;
	}

	public static boolean isUnder_1_19() {
		return !SERVER_VERSION.isAtLeast(MC1_19);
	}

	public static boolean isAbove_1_19() {
		return SERVER_VERSION.isAtLeast(MC1_19_1);
	}
	
	public static boolean isAtLeast_1_20_4() {
		return SERVER_VERSION.isAtLeast(MC1_20_4);
	}
}
