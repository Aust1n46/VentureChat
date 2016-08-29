package mineverse.Aust1n46.chat.gui;

import org.bukkit.Material;

public class GuiSlot {
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

	public String getText() {
		return this.text;
	}

	public String getCommand() {
		return this.command;
	} 

	public String getPermission() {
		return this.permission;
	}

	public Material getIcon() {
		return this.icon;
	}
	
	public int getDurability() {
		return this.durability;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getSlot() {
		return this.slot;
	}
	
	public boolean hasPermission() {
		return !permission.equalsIgnoreCase("mineversechat.none");
	}
}