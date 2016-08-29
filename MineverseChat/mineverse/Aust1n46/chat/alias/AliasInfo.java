package mineverse.Aust1n46.chat.alias;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;

public class AliasInfo {
	private Alias[] aa;

	public AliasInfo(MineverseChat plugin) {
		String name = "";
		int arguments = 0;
		String permissions;
		List<String> components;
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("alias");
		aa = new Alias[cs.getKeys(false).size()];
		int x = 0;
		for(String key : cs.getKeys(false)) {
			name = key;
			arguments = cs.getInt(key + ".arguments", 0);
			components = cs.getStringList(key + ".components");
			permissions = cs.getString(key + ".permissions", "None");
			Alias a = new Alias(name, arguments, components, permissions);
			aa[x++] = a;
		}
	}

	public Alias[] getAliases() {
		return aa;
	}

	public Alias getAliasInfo(String name) {
		for(Alias a : aa) {
			if(a.getName().equalsIgnoreCase(name)) 
				return a;
		}
		return null;
	}
}