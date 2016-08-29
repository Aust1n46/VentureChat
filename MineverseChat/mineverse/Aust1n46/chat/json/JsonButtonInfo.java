package mineverse.Aust1n46.chat.json;

import mineverse.Aust1n46.chat.MineverseChat;

import org.bukkit.configuration.ConfigurationSection;

public class JsonButtonInfo {
	private JsonButton[] jb;
	private MineverseChat plugin = MineverseChat.getInstance();
	
	public JsonButtonInfo() {
		String name;
		String text;
		String icon;
		String command;
		String permission;
		String action;
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("jsonbuttons");
		jb = new JsonButton[cs.getKeys(false).size()];
		int x = 0;
		for(String key : cs.getKeys(false)) {
			name = key;
			text = cs.getString(key + ".text");
			icon = cs.getString(key + ".icon");
			command = cs.getString(key + ".command");
			permission = cs.getString(key + ".permission");
			action = cs.getString(key + ".click_action");
			JsonButton j = new JsonButton(name, text, command, permission, icon, action);
			jb[x ++] = j;
		}
	}
	
	public JsonButton[] getJsonButtons() {
		return this.jb;
	}
	
	public JsonButton getJsonButton(String name) {
		for(JsonButton j : this.jb) {
			if(j.getName().equalsIgnoreCase(name))
				return j;
		}
		return null;
	}
}