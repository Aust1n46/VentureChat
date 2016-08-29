package mineverse.Aust1n46.chat.versions;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

//This class returns the CommandMap for Minecraft version 1.8
public class V1_8 {
	public static CommandMap v1_8() {
		CommandMap cmap = null;
		try {
			if(Bukkit.getServer() instanceof CraftServer) {
				final Field f = CraftServer.class.getDeclaredField("commandMap");
				f.setAccessible(true);
				cmap = (CommandMap) f.get(Bukkit.getServer());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return cmap;
	}
}