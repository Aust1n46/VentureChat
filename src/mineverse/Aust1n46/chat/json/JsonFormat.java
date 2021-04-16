package mineverse.Aust1n46.chat.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;

public class JsonFormat {
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static HashMap<String, JsonFormat> jsonFormats = new HashMap<String, JsonFormat>();
	
	private List<String> hoverTextName;
	private List<String> hoverTextPrefix;
	private List<String> hoverTextSuffix;
	private String clickName;
	private String clickNameText;
	private String clickPrefix;
	private String clickPrefixText;
	private String clickSuffix;
	private String clickSuffixText;
	private int priority;
	private String name;
	
	public JsonFormat(String name, int priority, List<String> hoverTextName, String clickName, String clickNameText, List<String> hoverTextPrefix, String clickPrefix, String clickPrefixText, String clickSuffix, String clickSuffixText, List<String> hoverTextSuffix) {
		this.name = name;
		this.priority = priority;
		this.hoverTextName = hoverTextName;
		this.clickNameText = clickNameText;
		this.hoverTextPrefix = hoverTextPrefix;
		this.clickPrefix = clickPrefix;
		this.clickPrefixText = clickPrefixText;
		this.clickName = clickName;
		this.clickSuffix = clickSuffix;
		this.clickSuffixText = clickSuffixText;
		this.hoverTextSuffix = hoverTextSuffix;
	}
	
	public static void initialize() {
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("jsonformatting");
		for(String key : cs.getKeys(false)) {
			String name = key;
			int priority = cs.getInt(key + ".priority", 0);
			List<String> hoverTextName = cs.getStringList(key + ".hover_name");
			List<String> hoverTextPrefix = cs.getStringList(key + ".hover_prefix");
			List<String> hoverTextSuffix = cs.getStringList(key + ".hover_suffix");
			String clickPrefix = cs.getString(key + ".click_prefix");
			String clickName = cs.getString(key + ".click_name");
			String clickNameText = cs.getString(key + ".click_name_text");
			String clickPrefixText = cs.getString(key + ".click_prefix_text");
			String clickSuffixText = cs.getString(key + ".click_suffix_text");
			String clickSuffix = cs.getString(key + ".click_suffix");
			jsonFormats.put(name.toLowerCase(), new JsonFormat(name, priority, hoverTextName, clickName, clickNameText, hoverTextPrefix, clickPrefix, clickPrefixText, clickSuffix, clickSuffixText, hoverTextSuffix));
		}
	}
	
	public static Collection<JsonFormat> getJsonFormats() {
		return jsonFormats.values();
	}
	
	public static JsonFormat getJsonFormat(String name) {
		return jsonFormats.get(name.toLowerCase());
	}
	
	public String getName() {
		return name;
	}
	
	public String getClickName() {
		return clickName;
	}
	
	public String getClickNameText() {
		return clickNameText;
	}
	
	public String getClickSuffix() {
		return clickSuffix;
	}
	
	public String getClickSuffixText() {
		return clickSuffixText;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public List<String> getHoverTextName() {
		return hoverTextName;
	}
	
	public List<String> getHoverTextPrefix() {
		return hoverTextPrefix;
	}
	
	public List<String> getHoverTextSuffix() {
		return hoverTextSuffix;
	}
	
	public String getClickPrefix() {
		return clickPrefix;
	}
	
	public String getClickPrefixText() {
		return clickPrefixText;
	}
}
