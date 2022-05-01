package venture.Aust1n46.chat.xcut;

import com.google.inject.Inject;

import venture.Aust1n46.chat.initiators.application.VentureChat;

public class VersionService {
	@Inject
	private VentureChat plugin;

	public boolean is1_7() {
		return plugin.getServer().getVersion().contains("1.7");
	}

	public boolean is1_8() {
		return plugin.getServer().getVersion().contains("1.8");
	}

	public boolean is1_9() {
		return plugin.getServer().getVersion().contains("1.9");
	}

	public boolean is1_10() {
		return plugin.getServer().getVersion().contains("1.10");
	}

	public boolean is1_11() {
		return plugin.getServer().getVersion().contains("1.11");
	}

	public boolean is1_12() {
		return plugin.getServer().getVersion().contains("1.12");
	}

	public boolean is1_13() {
		return plugin.getServer().getVersion().contains("1.13");
	}

	public boolean is1_14() {
		return plugin.getServer().getVersion().contains("1.14");
	}

	public boolean is1_14_4() {
		return plugin.getServer().getVersion().contains("1.14.4");
	}

	public boolean is1_15() {
		return plugin.getServer().getVersion().contains("1.15");
	}

	public boolean is1_16() {
		return plugin.getServer().getVersion().contains("1.16");
	}

	public boolean is1_17() {
		return plugin.getServer().getVersion().contains("1.17");
	}
}
