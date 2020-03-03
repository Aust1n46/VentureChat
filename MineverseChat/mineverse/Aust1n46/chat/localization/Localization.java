package mineverse.Aust1n46.chat.localization;

import java.io.File;

import mineverse.Aust1n46.chat.MineverseChat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

//This class is used to create objects of localization for different languages.
public class Localization { 
	private static MineverseChat plugin;
	private static FileConfiguration localization;
	private static File localizationFile;
	
	public static void initialize() {
		plugin = MineverseChat.getInstance();
		localizationFile = new File(plugin.getDataFolder().getAbsolutePath(), "Messages.yml");
		if(!localizationFile.isFile()) {
			plugin.saveResource("Messages.yml", true);		
		}
		localization = YamlConfiguration.loadConfiguration(localizationFile);
	}
	
	public static FileConfiguration getLocalization() {		
		return localization;
	}
}