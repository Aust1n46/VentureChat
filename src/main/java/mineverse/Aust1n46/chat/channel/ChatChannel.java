package mineverse.Aust1n46.chat.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

/**
 * Chat channel object pojo. Class also contains static initialization methods
 * for reading chat channels from the config file.
 * 
 * @author Aust1n46
 */
public class ChatChannel {
	private static final String PERMISSION_PREFIX = "venturechat.";
	private static final String NO_PERMISSIONS = "venturechat.none";

	private static MineverseChat plugin = MineverseChat.getInstance();
	private static ChatChannel defaultChatChannel;
	private static String defaultColor;
	private static HashMap<String, ChatChannel> chatChannels;
	
	@Deprecated
	private static ChatChannel[] channels;

	private String name;
	private String permission;
	private String speakPermission;
	private boolean mutable;
	private String color;
	private String chatColor;
	private boolean defaultChannel;
	private boolean autojoin;
	private String alias;
	private double distance;
	private boolean filter;
	private boolean bungee;
	private String format;
	private int cooldown;
	private String prefix;

	/**
	 * Read chat channels from config file and initialize channel array.
	 */
	public static void initialize() {
		chatChannels = new HashMap<String, ChatChannel>();
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("channels");
		int len = (cs.getKeys(false)).size();
		channels = new ChatChannel[len];
		int counter = 0;
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
			ChatChannel chatChannel = new ChatChannel(name, color, chatColor, permission, speakPermission, mutable,
					filter, defaultChannel, alias, distance, autojoin, bungee, cooldown, prefix, format);
			channels[counter++] = chatChannel;
			chatChannels.put(name.toLowerCase(), chatChannel);
			chatChannels.put(alias.toLowerCase(), chatChannel);
			if (defaultChannel) {
				defaultChatChannel = chatChannel;
				defaultColor = color;
			}
		}
		// Error handling for missing default channel in the config.
		if(defaultChatChannel == null) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - &cNo default channel found!"));
			defaultChatChannel = new ChatChannel("MissingDefault", "red", "red", "None", "None", false,
					true, true, "md", 0, true, false, 0, "&f[&cMissingDefault&f]", "{venturechat_channel_prefix} {vault_prefix}{player_displayname}&c:");
			defaultColor = defaultChatChannel.getColor();
			chatChannels.put("missingdefault", defaultChatChannel);
			chatChannels.put("md", defaultChatChannel);
		}
	}

	/**
	 * Get array of chat channels.
	 * 
	 * @return {@link ChatChannel}[]
	 */
	@Deprecated
	public static ChatChannel[] getChannels() {
		return channels;
	}
	
	/**
	 * Get list of chat channels.
	 * 
	 * @return {@link Collection}&lt{@link ChatChannel}&gt
	 */
	public static Collection<ChatChannel> getChatChannels() {
		return new HashSet<ChatChannel>(chatChannels.values());
	}

	/**
	 * Get a chat channel by name.
	 * 
	 * @param channelName
	 *            name of channel to get.
	 * @return {@link ChatChannel}
	 */
	public static ChatChannel getChannel(String channelName) {
		return chatChannels.get(channelName.toLowerCase());
	}

	/**
	 * Checks if the chat channel exists.
	 * 
	 * @param channelName
	 *            name of channel to check.
	 * @return true if channel exists, false otherwise.
	 */
	public static boolean isChannel(String channelName) {
		return getChannel(channelName) != null;
	}

	/**
	 * Get default chat channel color.
	 * 
	 * @return {@link String}
	 */
	public static String getDefaultColor() {
		return defaultColor;
	}

	/**
	 * Get default chat channel.
	 * 
	 * @return {@link ChatChannel}
	 */
	public static ChatChannel getDefaultChannel() {
		return defaultChatChannel;
	}

	/**
	 * Get list of chat channels with autojoin set to true.
	 * 
	 * @return {@link List}&lt{@link ChatChannel}&gt
	 */
	public static List<ChatChannel> getAutojoinList() {
		List<ChatChannel> joinlist = new ArrayList<ChatChannel>();
		for (ChatChannel c : channels) {
			if (c.getAutojoin()) {
				joinlist.add(c);
			}
		}
		return joinlist;
	}

	/**
	 * Parameterized constructor a {@link ChatChannel}.
	 * 
	 * @param name
	 * @param color
	 * @param chatColor
	 * @param permission
	 * @param speakPermission
	 * @param mutable
	 * @param filter
	 * @param defaultChannel
	 * @param alias
	 * @param distance
	 * @param autojoin
	 * @param bungee
	 * @param cooldown
	 * @param format
	 */
	public ChatChannel(String name, String color, String chatColor, String permission, String speakPermission,
			boolean mutable, boolean filter, boolean defaultChannel, String alias, double distance, boolean autojoin,
			boolean bungee, int cooldown, String prefix, String format) {
		this.name = name;
		this.color = color;
		this.chatColor = chatColor;
		this.permission = PERMISSION_PREFIX + permission;
		this.speakPermission = PERMISSION_PREFIX + speakPermission;
		this.mutable = mutable;
		this.filter = filter;
		this.defaultChannel = defaultChannel;
		this.alias = alias;
		this.distance = distance;
		this.autojoin = autojoin;
		this.bungee = bungee;
		this.cooldown = cooldown;
		this.format = format;
		this.prefix = prefix;
	}

	/**
	 * Deprecated parameterized constructor a {@link ChatChannel}.
	 * 
	 * @param name
	 * @param color
	 * @param chatColor
	 * @param permission
	 * @param speakPermission
	 * @param mutable
	 * @param filter
	 * @param defaultChannel
	 * @param alias
	 * @param distance
	 * @param autojoin
	 * @param bungee
	 * @param cooldown
	 * @param format
	 */
	@Deprecated
	public ChatChannel(String name, String color, String chatColor, String permission, String speakPermission,
			Boolean mutable, Boolean filter, Boolean defaultChannel, String alias, Double distance, Boolean autojoin,
			Boolean bungee, int cooldown, String format) {
		this.name = name;
		this.color = color;
		this.chatColor = chatColor;
		this.permission = PERMISSION_PREFIX + permission;
		this.speakPermission = PERMISSION_PREFIX + speakPermission;
		this.mutable = mutable;
		this.filter = filter;
		this.defaultChannel = defaultChannel;
		this.alias = alias;
		this.distance = distance;
		this.autojoin = autojoin;
		this.bungee = bungee;
		this.cooldown = cooldown;
		this.format = format;
	}

	/**
	 * Get the name of the chat channel.
	 * 
	 * @return {@link String}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the format of the chat channel.
	 * 
	 * @return {@link String}
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Get the cooldown of the chat channel in seconds.
	 * 
	 * @return int
	 */
	public int getCooldown() {
		return cooldown;
	}
	
	/**
	 * Get the prefix of the chat channel.
	 * @return String
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Check if the chat channel is BungeeCord enabled.
	 * 
	 * @return {@link Boolean#TRUE} if the chat channel is BungeeCord enabled,
	 *         {@link Boolean#FALSE} otherwise.
	 */
	public Boolean getBungee() {
		return Boolean.valueOf(bungee);
	}

	/**
	 * Get the permissions node for the chat channel.
	 * 
	 * @return {@link String}
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * Check if autojoin is enabled for the chat channel.
	 * 
	 * @return {@link Boolean#TRUE} if autojoin is enabled, {@link Boolean#FALSE}
	 *         otherwise.
	 */
	public Boolean getAutojoin() {
		return Boolean.valueOf(autojoin);
	}

	/**
	 * Check if the chat channel allows muting.
	 * 
	 * @return {@link Boolean#TRUE} if muting is allowed, {@link Boolean#FALSE}
	 *         otherwise.
	 */
	public Boolean isMutable() {
		return Boolean.valueOf(mutable);
	}

	/**
	 * Get the formatted color of the chat channel.
	 * 
	 * @return {@link String}. Returns {@link Format#DEFAULT_COLOR_CODE} if the
	 *         color is invalid.
	 */
	public String getColor() {
		if (Format.isValidColor(color)) {
			return String.valueOf(ChatColor.valueOf(color.toUpperCase()));
		}
		if (Format.isValidHexColor(color)) {
			return Format.convertHexColorCodeToBukkitColorCode(color);
		}
		return Format.DEFAULT_COLOR_CODE;
	}

	/**
	 * Get the raw color value of the chat channel.
	 * 
	 * @return {@link String}
	 */
	public String getColorRaw() {
		return color;
	}

	/**
	 * Get the formatted chat color of the chat channel.
	 * 
	 * @return {@link String}. Returns {@link Format#DEFAULT_COLOR_CODE} if the chat
	 *         color is invalid.
	 */
	public String getChatColor() {
		if (chatColor.equalsIgnoreCase("None")) {
			return chatColor;
		}
		if (Format.isValidColor(chatColor)) {
			return String.valueOf(ChatColor.valueOf(chatColor.toUpperCase()));
		}
		if (Format.isValidHexColor(chatColor)) {
			return Format.convertHexColorCodeToBukkitColorCode(chatColor);
		}
		return Format.DEFAULT_COLOR_CODE;
	}

	/**
	 * Get the raw chat color value of the chat channel.
	 * 
	 * @return {@link String}
	 */
	public String getChatColorRaw() {
		return chatColor;
	}

	/**
	 * Check if the chat channel is the default chat channel.
	 * 
	 * @return {@link Boolean#TRUE} if the chat channel is the default chat channel,
	 *         {@link Boolean#FALSE} otherwise.
	 */
	public Boolean isDefaultchannel() {
		return Boolean.valueOf(defaultChannel);
	}

	/**
	 * Get the alias of the chat channel.
	 * 
	 * @return {@link String}
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Get the distance of the chat channel in blocks.
	 * 
	 * @return {@link Double}
	 */
	public Double getDistance() {
		return Double.valueOf(distance);
	}

	/**
	 * Checks if the chat channel has a distance set.
	 * 
	 * @return {@link Boolean#TRUE} if the distance is greater than zero,
	 *         {@link Boolean#FALSE} otherwise.
	 */
	public Boolean hasDistance() {
		return Boolean.valueOf(distance > 0);
	}

	/**
	 * Checks if the chat channel has a cooldown set.
	 * 
	 * @return {@link Boolean#TRUE} if the cooldown is greater than zero,
	 *         {@link Boolean#FALSE} otherwise.
	 */
	public Boolean hasCooldown() {
		return Boolean.valueOf(cooldown > 0);
	}

	/**
	 * Checks if the chat channel has a permission set.
	 * 
	 * @return {@link Boolean#TRUE} if the permission does not equal
	 *         {@link ChatChannel#NO_PERMISSIONS}, {@link Boolean#FALSE} otherwise.
	 */
	public Boolean hasPermission() {
		return Boolean.valueOf(!permission.equalsIgnoreCase(NO_PERMISSIONS));
	}

	/**
	 * Checks if the chat channel has a speak permission set.
	 * 
	 * @return true if the speak permission does not equal
	 *         {@link ChatChannel#NO_PERMISSIONS}, false otherwise.
	 */
	public boolean hasSpeakPermission() {
		return !speakPermission.equalsIgnoreCase(NO_PERMISSIONS);
	}

	/**
	 * Get the speak permissions node for the chat channel.
	 * 
	 * @return {@link String}
	 */
	public String getSpeakPermission() {
		return speakPermission;
	}

	/**
	 * Checks if the chat channel has the filter enabled.
	 * 
	 * @return {@link Boolean#TRUE} if the chat channel has the filter enabled,
	 *         {@link Boolean#FALSE} otherwise.
	 */
	public Boolean isFiltered() {
		return Boolean.valueOf(filter);
	}

	/**
	 * Compares the chat channel by name to determine equality.
	 * 
	 * @param channel
	 *            Object to compare for equality.
	 * @return true if the objects are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object channel) {
		return channel instanceof ChatChannel && this.name.equals(((ChatChannel) channel).getName());
	}
}
