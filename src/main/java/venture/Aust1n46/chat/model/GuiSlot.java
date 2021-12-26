package venture.Aust1n46.chat.model;

import org.bukkit.Material;

import lombok.Getter;

@Getter
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

	public boolean hasPermission() {
		return !permission.equalsIgnoreCase("venturechat.none");
	}
}
