package mineverse.Aust1n46.chat.gui;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;

public class GuiSlotInfo {
	private GuiSlot[] gs;
	private MineverseChat plugin = MineverseChat.getInstance();
	
	public GuiSlotInfo() {
		String name;
		String text;
		String icon;
		int durability;
		String command;
		String permission;
		int slot;
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("venturegui");
		gs = new GuiSlot[cs.getKeys(false).size()];
		int x = 0;
		for(String key : cs.getKeys(false)) {
			name = key;
			icon = cs.getString(key + ".icon");
			durability = cs.getInt(key + ".durability");
			text = cs.getString(key + ".text");
			permission = cs.getString(key + ".permission");
			command = cs.getString(key + ".command");
			slot = cs.getInt(key + ".slot");
			GuiSlot g = new GuiSlot(name, icon, durability, text, permission, command, slot);
			gs[x ++] = g;
		}
	}
	
	public GuiSlot[] getGuiSlots() {
		return this.gs;
	}
	
	public GuiSlot getGuiSlot(String name) {
		for(GuiSlot g : this.gs) {
			if(g.getName().equalsIgnoreCase(name))
				return g;
		}
		return null;
	}
}