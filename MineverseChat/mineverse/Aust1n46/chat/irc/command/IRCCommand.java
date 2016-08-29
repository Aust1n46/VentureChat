package mineverse.Aust1n46.chat.irc.command;

import java.util.List;

//This class is where IRCCommand objects are created using information from the config file.
public class IRCCommand {
	private String name;
	private List<String> components;
	private String mode;

	public IRCCommand(String name, List<String> components, String mode) {
		this.name = name;
		this.components = components;
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public List<String> getComponents() {
		return components;
	}

	public String getMode() {
		return mode;
	}

	public boolean hasMode() {
		return !mode.equals("*");
	}
}