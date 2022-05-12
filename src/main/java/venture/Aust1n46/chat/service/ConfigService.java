package venture.Aust1n46.chat.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.Alias;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.GuiSlot;
import venture.Aust1n46.chat.model.JsonAttribute;
import venture.Aust1n46.chat.model.JsonFormat;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.utilities.FormatUtils;

@Singleton
public class ConfigService {
	@Inject
	private VentureChat plugin;

	private final HashMap<String, ChatChannel> chatChannels = new HashMap<String, ChatChannel>();
	private final HashMap<String, JsonFormat> jsonFormats = new HashMap<String, JsonFormat>();
	private final List<Alias> aliases = new ArrayList<>();
	private final List<GuiSlot> guiSlots = new ArrayList<>();
	private boolean aliasesRegisteredAsCommands;
	private ChatChannel defaultChatChannel;
	private String defaultColor;

	@Inject
	public void postConstruct() {
		aliasesRegisteredAsCommands = true;
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("channels");
		for (String key : cs.getKeys(false)) {
			String color = cs.getString(key + ".color", "white");
			String chatColor = cs.getString(key + ".chatcolor", "white");
			String name = key;
			String permission = cs.getString(key + ".permissions", "None");
			String speakPermission = cs.getString(key + ".speak_permissions", "None");
			boolean mutable = cs.getBoolean(key + ".mutable", false);
			boolean filter = cs.getBoolean(key + ".filter", true);
			boolean bungee = cs.getBoolean(key + ".bungeecord", false);
			String format = cs.getString(key + ".format", "Default");
			boolean defaultChannel = cs.getBoolean(key + ".default", false);
			String alias = cs.getString(key + ".alias", "None");
			double distance = cs.getDouble(key + ".distance", (double) 0);
			int cooldown = cs.getInt(key + ".cooldown", 0);
			boolean autojoin = cs.getBoolean(key + ".autojoin", false);
			String prefix = cs.getString(key + ".channel_prefix");
			ChatChannel chatChannel = new ChatChannel(name, color, chatColor, permission, speakPermission, mutable, filter, defaultChannel, alias, distance, autojoin, bungee,
					cooldown, prefix, format);
			chatChannels.put(name.toLowerCase(), chatChannel);
			chatChannels.put(alias.toLowerCase(), chatChannel);
			if (defaultChannel) {
				defaultChatChannel = chatChannel;
				defaultColor = color;
			}
		}
		// Error handling for missing default channel in the config.
		if (defaultChatChannel == null) {
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - &cNo default channel found!"));
			defaultChatChannel = new ChatChannel("MissingDefault", "red", "red", "None", "None", false, true, true, "md", 0, true, false, 0, "&f[&cMissingDefault&f]",
					"{venturechat_channel_prefix} {vault_prefix}{player_displayname}&c:");
			defaultColor = defaultChatChannel.getColor();
			chatChannels.put("missingdefault", defaultChatChannel);
			chatChannels.put("md", defaultChatChannel);
		}

		jsonFormats.clear();
		ConfigurationSection jsonFormatSection = plugin.getConfig().getConfigurationSection("jsonformatting");
		for (String jsonFormat : jsonFormatSection.getKeys(false)) {
			int priority = jsonFormatSection.getInt(jsonFormat + ".priority", 0);
			List<JsonAttribute> jsonAttributes = new ArrayList<>();
			ConfigurationSection jsonAttributeSection = jsonFormatSection.getConfigurationSection(jsonFormat + ".json_attributes");
			if (jsonAttributeSection != null) {
				for (String attribute : jsonAttributeSection.getKeys(false)) {
					List<String> hoverText = jsonAttributeSection.getStringList(attribute + ".hover_text");
					String clickAction = jsonAttributeSection.getString(attribute + ".click_action", "");
					String clickText = jsonAttributeSection.getString(attribute + ".click_text", "");
					jsonAttributes.add(new JsonAttribute(attribute, hoverText, clickAction, clickText));
				}
			}
			jsonFormats.put(jsonFormat.toLowerCase(), new JsonFormat(jsonFormat, priority, jsonAttributes));
		}

		aliases.clear();
		ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("alias");
		for (String key : configurationSection.getKeys(false)) {
			String name = key;
			int arguments = configurationSection.getInt(key + ".arguments", 0);
			List<String> components = configurationSection.getStringList(key + ".components");
			String permissions = configurationSection.getString(key + ".permissions", "None");
			aliases.add(new Alias(name, arguments, components, permissions));
		}

		guiSlots.clear();
		cs = plugin.getConfig().getConfigurationSection("venturegui");
		for (String key : cs.getKeys(false)) {
			String name = key;
			String icon = cs.getString(key + ".icon");
			int durability = cs.getInt(key + ".durability");
			String text = cs.getString(key + ".text");
			String permission = cs.getString(key + ".permission");
			String command = cs.getString(key + ".command");
			int slot = cs.getInt(key + ".slot");
			guiSlots.add(new GuiSlot(name, icon, durability, text, permission, command, slot));
		}
	}

	public boolean areAliasesRegisteredAsCommands() {
		return aliasesRegisteredAsCommands;
	}

	/**
	 * Get list of chat channels.
	 * 
	 * @return {@link Collection}&lt{@link ChatChannel}&gt
	 */
	public Collection<ChatChannel> getChatChannels() {
		return new HashSet<ChatChannel>(chatChannels.values());
	}

	/**
	 * Get a chat channel by name.
	 * 
	 * @param channelName name of channel to get.
	 * @return {@link ChatChannel}
	 */
	public ChatChannel getChannel(String channelName) {
		return chatChannels.get(channelName.toLowerCase());
	}

	/**
	 * Checks if the chat channel exists.
	 * 
	 * @param channelName name of channel to check.
	 * @return true if channel exists, false otherwise.
	 */
	public boolean isChannel(String channelName) {
		return getChannel(channelName) != null;
	}

	public boolean isListening(VentureChatPlayer ventureChatPlayer, String channel) {
		if (ventureChatPlayer.isOnline()) {
			if (isChannel(channel)) {
				ChatChannel chatChannel = getChannel(channel);
				if (chatChannel.hasPermission()) {
					if (!ventureChatPlayer.getPlayer().hasPermission(chatChannel.getPermission())) {
						if (ventureChatPlayer.getCurrentChannel().equals(chatChannel)) {
							ventureChatPlayer.setCurrentChannel(getDefaultChannel());
						}
						ventureChatPlayer.removeListening(channel);
						return false;
					}
				}
			}
		}
		return ventureChatPlayer.isListening(channel);
	}

	/**
	 * Get default chat channel color.
	 * 
	 * @return {@link String}
	 */
	public String getDefaultColor() {
		return defaultColor;
	}

	/**
	 * Get default chat channel.
	 * 
	 * @return {@link ChatChannel}
	 */
	public ChatChannel getDefaultChannel() {
		return defaultChatChannel;
	}

	/**
	 * Get list of chat channels with autojoin set to true.
	 * 
	 * @return {@link List}&lt{@link ChatChannel}&gt
	 */
	public List<ChatChannel> getAutojoinList() {
		List<ChatChannel> joinlist = new ArrayList<ChatChannel>();
		for (ChatChannel c : chatChannels.values()) {
			if (c.getAutojoin()) {
				joinlist.add(c);
			}
		}
		return joinlist;
	}

	public Collection<JsonFormat> getJsonFormats() {
		return jsonFormats.values();
	}

	public JsonFormat getJsonFormat(String name) {
		return jsonFormats.get(name.toLowerCase());
	}

	public List<Alias> getAliases() {
		return aliases;
	}

	public List<GuiSlot> getGuiSlots() {
		return guiSlots;
	}

	public boolean isProxyEnabled() {
		try {
			return plugin.getServer().spigot().getConfig().getBoolean("settings.bungeecord")
					|| plugin.getServer().spigot().getPaperConfig().getBoolean("settings.velocity-support.enabled");
		} catch (NoSuchMethodError exception) { // Thrown if server isn't Paper.
			return false;
		}
	}
}
