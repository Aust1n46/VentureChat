package mineverse.Aust1n46.chat.json;

public class JsonButton {
	private String text;
	private String command;
	private String permission;
	private String icon;
	private String name;
	private String action;
	
	public JsonButton(String name, String text, String command, String permission, String icon, String action) {
		this.name = name;
		this.text = text;
		this.command = command;
		this.permission = "venturechat." + permission;
		this.icon = icon;
		this.action = action;
	}

	public String getText() {
		return this.text;
	}
	
	public String getAction() {
		return this.action;
	}

	public String getCommand() {
		return this.command;
	}

	public String getPermission() {
		return this.permission;
	}

	public String getIcon() {
		return this.icon;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean hasPermission() {
		return !permission.equalsIgnoreCase("venturechat.none");
	}
}