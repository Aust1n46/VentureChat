package mineverse.Aust1n46.chat.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;

public class GuiSlot {
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static List<GuiSlot> guiSlots;

	private String text;
	private String command;
	private String permission;
	private Material icon;
	private String name;
	private int durability;
	private int slot;

	public GuiSlot(String name, String icon, int durability, String text, String permission, String command, int slot) {
		this.name = name;
		this.text = text;
		this.command = command;
		this.permission = "venturechat." + permission;
		this.icon = Material.valueOf(icon.toUpperCase());
		this.durability = durability;
		this.slot = slot;
	}

	public static void initialize() {
		guiSlots  = new ArrayList<GuiSlot>();
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("venturegui");
		for (String key : cs.getKeys(false)) {
			String name = key;
			String icon = cs.getString(key + ".icon");
			int durability = cs.getInt(key + ".durability");
			String text = cs.getString(key + ".text");
			String permission = cs.getString(key + ".permission");
			String command = cs.getString(key + ".command");
			int slot = cs.getInt(key + ".slot");
			guiSlots.add(new GuiSlot(name, icon, durability, text, permission, command, slot));
		}
	}

	public static List<GuiSlot> getGuiSlots() {
		return guiSlots;
	}

	public String getText() {
		return text;
	}

	public String getCommand() {
		return command;
	}

	public String getPermission() {
		return permission;
	}

	public Material getIcon() {
		return icon;
	}

	public int getDurability() {
		return durability;
	}

	public String getName() {
		return name;
	}

	public int getSlot() {
		return slot;
	}

	public boolean hasPermission() {
		return !permission.equalsIgnoreCase("venturechat.none");
	}
}
