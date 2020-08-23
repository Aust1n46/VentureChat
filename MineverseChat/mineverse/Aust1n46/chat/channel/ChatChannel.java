package mineverse.Aust1n46.chat.channel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

//This class is used to create ChatChannel objects, which store all the information for a channel.  This
//information is read in from the config file when the server starts up.
public class ChatChannel {
	private static final String PERMISSION_PREFIX = "venturechat.";
	private static final String NO_PERMISSIONS = "venturechat.none";
	
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static ChatChannel defaultChatChannel;
	private static ChatChannel[] channels;
	private static String defaultColor;
	
	private String name;
	private String permission;
	private String speakPermission;
	private Boolean mutable;
	private String color;
	private String chatColor;
	private Boolean defaultChannel;
	private Boolean autojoin;
	private String alias;
	private Double distance;
	private Boolean filter;
	private Boolean bungee;
	private String format;
	private int cooldown;
	
	public static void initialize() {
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("channels");
		int len = (cs.getKeys(false)).size();
		channels = new ChatChannel[len];
		int counter = 0;
		for(String key : cs.getKeys(false)) {
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
			ChatChannel chatChannel = new ChatChannel(name, color, chatColor, permission, speakPermission, mutable, filter, defaultChannel, alias, distance, autojoin, bungee, cooldown, format);
			channels[counter++] = chatChannel;
			if(defaultChannel) {
				defaultChatChannel = chatChannel;
				defaultColor = color;
			}
		}
	}
	
	public static ChatChannel[] getChannels() {
		return channels;
	}
	
	public static ChatChannel getChannel(String ChannelName) {
		for(ChatChannel c : channels) {
			if(c.getName().equalsIgnoreCase(ChannelName) || c.getAlias().equalsIgnoreCase(ChannelName)) {
				return c;
			}
		}
		return null;
	}
	
	public static boolean isChannel(String channel) {
		return getChannel(channel) != null;
	}
	
	public static String getDefaultColor() {
		return defaultColor;
	}
	
	public static ChatChannel getDefaultChannel() {
		return defaultChatChannel;
	}
	
	public static List<ChatChannel> getAutojoinList() {
		List<ChatChannel> joinlist = new ArrayList<ChatChannel>();
		for(ChatChannel c : channels) {
			if(c.getAutojoin()) {
				joinlist.add(c);
			}
		}
		return joinlist;
	}

	public ChatChannel(String name, String color, String chatColor, String permission, String speakPermission, Boolean mutable, Boolean filter, Boolean defaultChannel, String alias, Double distance, Boolean autojoin, Boolean bungee, int cooldown, String format) {
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

	public String getName() {
		return name;
	}

	public String getFormat() {
		return format;
	}

	public int getCooldown() {
		return cooldown;
	}

	public Boolean getBungee() {
		return bungee;
	}

	public String getPermission() {
		return permission;
	}

	public Boolean getAutojoin() {
		return autojoin;
	}

	public Boolean isMutable() {
		return mutable;
	}

	public String getColor() {
		if(Format.isValidColor(color)) {
			return String.valueOf(ChatColor.valueOf(color.toUpperCase()));
		}
		if(Format.isValidHexColor(color)) {
			return Format.convertHexColorCodeToBukkitColorCode(color);
		}
		return Format.DEFAULT_COLOR_CODE;
	}
	
	public String getColorRaw() {
		return color;
	}

	public String getChatColor() {
		if(chatColor.equalsIgnoreCase("None")) {
			return chatColor;
		}
		if(Format.isValidColor(chatColor)) {
			return String.valueOf(ChatColor.valueOf(chatColor.toUpperCase()));
		}
		if(Format.isValidHexColor(chatColor)) {
			return Format.convertHexColorCodeToBukkitColorCode(chatColor);
		}
		return Format.DEFAULT_COLOR_CODE;
	}
	
	public String getChatColorRaw() {
		return chatColor;
	}

	public Boolean isDefaultchannel() {
		return defaultChannel;
	}

	public String getAlias() {
		return alias;
	}

	public Double getDistance() {
		return distance;
	}

	public Boolean hasDistance() {
		return distance > 0;
	}

	public Boolean hasCooldown() {
		return cooldown > 0;
	}

	public Boolean hasPermission() {
		return !permission.equalsIgnoreCase(NO_PERMISSIONS);
	}
	
	public boolean hasSpeakPermission() {
		return !speakPermission.equalsIgnoreCase(NO_PERMISSIONS);
	}
	
	public String getSpeakPermission() {
		return speakPermission;
	}

	public Boolean isFiltered() {
		return filter;
	}
	
	@Override
	public boolean equals(Object channel) {
		return channel instanceof ChatChannel && this.name.equals(((ChatChannel) channel).getName());
	}
}