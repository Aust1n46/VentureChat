package venture.Aust1n46.chat.xcut;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class VersionService {
	private static final MinecraftVersion MC1_19 = new MinecraftVersion(1, 19, 0);
	private static final MinecraftVersion MC1_19_1 = new MinecraftVersion(1, 19, 1);
	private static final MinecraftVersion MC1_20_4 = new MinecraftVersion(1, 20, 4);
	
	private MinecraftVersion serverVersion;
	
	@Inject
	public void postConstruct() {
		serverVersion = MinecraftVersion.getCurrentVersion();
	}

	public boolean is1_7() {
		return serverVersion.getMinor() == 7 && serverVersion.getMajor() == 1;
	}

	public boolean is1_8() {
		return serverVersion.getMinor() == 8 && serverVersion.getMajor() == 1;
	}

	public boolean is1_9() {
		return serverVersion.getMinor() == 9 && serverVersion.getMajor() == 1;
	}

	public boolean is1_10() {
		return serverVersion.getMinor() == 10 && serverVersion.getMajor() == 1;
	}

	public boolean is1_11() {
		return serverVersion.getMinor() == 11 && serverVersion.getMajor() == 1;
	}

	public boolean is1_12() {
		return serverVersion.getMinor() == 12 && serverVersion.getMajor() == 1;
	}

	public boolean is1_13() {
		return serverVersion.getMinor() == 13 && serverVersion.getMajor() == 1;
	}

	public boolean is1_14() {
		return serverVersion.getBuild() != 4 && serverVersion.getMinor() == 14 && serverVersion.getMajor() == 1;
	}

	public boolean is1_14_4() {
		return serverVersion.getBuild() == 4 && serverVersion.getMinor() == 14 && serverVersion.getMajor() == 1;
	}

	public boolean is1_15() {
		return serverVersion.getMinor() == 15 && serverVersion.getMajor() == 1;
	}

	public boolean is1_16() {
		return serverVersion.getMinor() == 16 && serverVersion.getMajor() == 1;
	}

	public boolean is1_17() {
		return serverVersion.getMinor() == 17 && serverVersion.getMajor() == 1;
	}

	public boolean is1_18() {
		return serverVersion.getMinor() == 18 && serverVersion.getMajor() == 1;
	}

	public boolean is1_19() {
		return serverVersion.getBuild() == 0 && serverVersion.getMinor() == 19 && serverVersion.getMajor() == 1;
	}

	public boolean isUnder_1_19() {
		return !serverVersion.isAtLeast(MC1_19);
	}

	public boolean isAbove_1_19() {
		return serverVersion.isAtLeast(MC1_19_1);
	}

	public boolean isAtLeast_1_20_4() {
		return serverVersion.isAtLeast(MC1_20_4);
	}
}
