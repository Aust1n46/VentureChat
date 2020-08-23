package mineverse.Aust1n46.chat.json;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;

//This class stores JsonFormat objects in an array and the constructor creates them by reading in data from the config file.
public class JsonFormatInfo {
	private JsonFormat[] jf;
	
	public JsonFormatInfo(MineverseChat plugin) {
		String name;
		int priority = 0;
		List<String> hoverTextName;
		List<String> hoverTextPrefix;
		List<String> hoverTextSuffix;
		String clickPrefix;
		String clickName;
		String clickNameText;
		String clickPrefixText;
		String clickSuffix;
		String clickSuffixText;
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("jsonformatting");
		jf = new JsonFormat[cs.getKeys(false).size()];
		int x = 0;
		for(String key : cs.getKeys(false)) {
			name = key;
			priority = cs.getInt(key + ".priority", 0);
			hoverTextName = cs.getStringList(key + ".hover_name");
			hoverTextPrefix = cs.getStringList(key + ".hover_prefix");
			hoverTextSuffix = cs.getStringList(key + ".hover_suffix");
			clickPrefix = cs.getString(key + ".click_prefix");
			clickName = cs.getString(key + ".click_name");
			clickNameText = cs.getString(key + ".click_name_text");
			clickPrefixText = cs.getString(key + ".click_prefix_text");
			clickSuffixText = cs.getString(key + ".click_suffix_text");
			clickSuffix = cs.getString(key + ".click_suffix");
			JsonFormat j = new JsonFormat(name, priority, hoverTextName, clickName, clickNameText, hoverTextPrefix, clickPrefix, clickPrefixText, clickSuffix, clickSuffixText, hoverTextSuffix);
			jf[x ++] = j;
		}
	}
	
	public JsonFormat[] getJsonFormats() {
		return this.jf;
	}
	
	public JsonFormat getJsonFormat(String name) {
		for(JsonFormat j : this.jf) {
			if(j.getName().equalsIgnoreCase(name))
				return j;
		}
		return getJsonFormat("Default");
	}
}