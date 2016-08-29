package mineverse.Aust1n46.chat.alias;

import java.util.List;

public class Alias {
	private String name;
	private int arguments;
	private List<String> components;
	private String permission;

	public Alias(String name, int arguments, List<String> components, String permission) {
		this.name = name;
		this.arguments = arguments;
		this.components = components;
		this.permission = "venturechat." + permission;
	}

	public String getName() {
		return name;
	}

	public int getArguments() {
		return arguments;
	}

	public List<String> getComponents() {
		return components;
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasPermission() {
		return !permission.equalsIgnoreCase("venturechat.none");
	}
}