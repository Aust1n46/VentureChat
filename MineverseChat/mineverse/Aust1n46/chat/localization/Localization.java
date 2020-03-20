package mineverse.Aust1n46.chat.localization;

import java.io.File;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

//This class is used to create objects of localization for different languages.
public class Localization { 
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static FileConfiguration localization;
	
	public static void initialize() {
		File localizationFile = new File(plugin.getDataFolder().getAbsolutePath(), "Messages.yml");
		if(!localizationFile.isFile()) {
			plugin.saveResource("Messages.yml", true);	
		}
		
		localization = YamlConfiguration.loadConfiguration(localizationFile);
		
		String fileVersion = localization.getString("Version", "null");
		String currentVersion = plugin.getDescription().getVersion();
		
		if(!fileVersion.equals(currentVersion)) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Version Change Detected!  Saving Old Messages.yml and Generating Latest File"));
			localizationFile.renameTo(new File(plugin.getDataFolder().getAbsolutePath(), "Messages_Old.yml"));
			plugin.saveResource("Messages.yml", true);
			localization = YamlConfiguration.loadConfiguration(localizationFile);
		}
	}
	
	public static FileConfiguration getLocalization() {		
		return localization;
	}
}