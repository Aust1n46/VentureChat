package mineverse.Aust1n46.chat.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.ClickAction;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

public class JsonFormat {
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static HashMap<String, JsonFormat> jsonFormats;

	private List<JsonAttribute> jsonAttributes;
	private int priority;
	private String name;

	public JsonFormat(String name, int priority, List<JsonAttribute> jsonAttributes) {
		this.name = name;
		this.priority = priority;
		this.jsonAttributes = jsonAttributes;
	}

	public static void initialize() {
		jsonFormats = new HashMap<String, JsonFormat>();
		ConfigurationSection jsonFormatSection = plugin.getConfig().getConfigurationSection("jsonformatting");
		for (String jsonFormat : jsonFormatSection.getKeys(false)) {
			int priority = jsonFormatSection.getInt(jsonFormat + ".priority", 0);
			List<JsonAttribute> jsonAttributes = new ArrayList<>();
			ConfigurationSection jsonAttributeSection = jsonFormatSection.getConfigurationSection(jsonFormat + ".json_attributes");
			if (jsonAttributeSection != null) {
				for (String attribute : jsonAttributeSection.getKeys(false)) {
					List<String> hoverText = jsonAttributeSection.getStringList(attribute + ".hover_text");
					String clickActionText = jsonAttributeSection.getString(attribute + ".click_action", "none");
					try {
						ClickAction clickAction = ClickAction.valueOf(clickActionText.toUpperCase());
						String clickText = jsonAttributeSection.getString(attribute + ".click_text", "");
						jsonAttributes.add(new JsonAttribute(attribute, hoverText, clickAction, clickText));
					} catch (IllegalArgumentException | NullPointerException exception) {
						plugin.getServer().getConsoleSender()
								.sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Illegal click_action: " + clickActionText + " in jsonFormat: " + jsonFormat));
					}
				}
			}
			jsonFormats.put(jsonFormat.toLowerCase(), new JsonFormat(jsonFormat, priority, jsonAttributes));
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

	public int getPriority() {
		return priority;
	}

	public List<JsonAttribute> getJsonAttributes() {
		return jsonAttributes;
	}
}
