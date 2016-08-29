package mineverse.Aust1n46.chat.permissions;

import java.lang.reflect.*;

import mineverse.Aust1n46.chat.LogLevels;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;

//This class used to check if the plugin PermissionsEx was available, this is now a legacy class.
@SuppressWarnings("unused")
public class MineversePermissions { 
	private MineverseChat plugin;
	private String name;
	private ChatChannelInfo cc;

	public MineversePermissions(MineverseChat plugin, ChatChannelInfo _cc) {
		this.plugin = plugin;
		name = plugin.getName();
		cc = _cc;
	}

	@SuppressWarnings("rawtypes")
	public boolean PermissionsExAvailable() {
		try {
			Class c = Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx");
			Method m[] = c.getDeclaredMethods();
			for(int i = 0; i < m.length; i++) {
				plugin.logme(LogLevels.DEBUG, "PermissionsExAvailable", m[i].toString());
			}
			return true;
		}
		catch(Throwable e) {
			plugin.logme(LogLevels.ERROR, "PermissionsExAvailable", e.getMessage());
		}
		return false;
	}
}