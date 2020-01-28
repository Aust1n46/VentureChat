package mineverse.Aust1n46.chat.localization;

import java.io.File;

import mineverse.Aust1n46.chat.MineverseChat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

//This class is used to create objects of localization for different languages.
public class Localization { 
	//private FileConfiguration localization;
	//private File localizationFile;
	//private MineverseChat plugin;
	
	/*public Localization() {
		this.plugin = MineverseChat.getInstance();
		localizationFile = new File(plugin.getDataFolder().getAbsolutePath(), "en_default.yml");
		if(!localizationFile.exists()) {
			try {
				Files.copy(plugin.getResource("en_default.yml"), localizationFile.toPath());
			}
			catch(IOException exception) {
				exception.printStackTrace();
			}
		}
		localization = YamlConfiguration.loadConfiguration(localizationFile);
	}
	
	public Localization(String name) {	
		this.plugin = MineverseChat.getInstance();
		localizationFile = new File(plugin.getDataFolder().getAbsolutePath(), name + ".yml");
		if(!localizationFile.exists()) {
			new Localization();
			return;		
		}
		localization = YamlConfiguration.loadConfiguration(localizationFile);
	}
	
	public FileConfiguration getLocalizedMessages() {		
		return localization;
	}*/
	
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