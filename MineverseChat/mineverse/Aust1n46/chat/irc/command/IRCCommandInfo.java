package mineverse.Aust1n46.chat.irc.command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;

//This class reads in data from the config and creates IRCCommand's and stores them into an array.
public class IRCCommandInfo {
	MineverseChat plugin;
	IRCCommand[] irc;

	public IRCCommandInfo(MineverseChat plugin) {
		this.plugin = plugin;
		String name = "";
		List<String> components;
		String mode;
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("irc.commands");
		irc = new IRCCommand[cs.getKeys(false).size()];
		int x = 0;
		for(String key : cs.getKeys(false)) {
			name = key;
			components = cs.getStringList(key + ".components");
			mode = cs.getString(key + ".mode");
			IRCCommand ircc = new IRCCommand(name, components, mode);
			irc[x++] = ircc;
		}
	}

	public IRCCommand[] getIRCCommands() {
		return irc;
	}

	public IRCCommand getIRCCommandInfo(String name) {
		for(IRCCommand ircc : irc) {
			if(ircc.getName().equalsIgnoreCase(name)) return ircc;
		}
		return null;
	}
}