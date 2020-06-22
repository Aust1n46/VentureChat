package mineverse.Aust1n46.chat.channel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

//This class is used to create ChatChannel objects, which store all the information for a channel.  This
//information is read in from the config file when the server starts up.
public class ChatChannel {
	private String name;
	private String permission;
	private Boolean mutable;
	private String color;
	private String chatcolor;
	private Boolean defaultChannel;
	private Boolean autojoin;
	private String alias;
	private Double distance;
	private Boolean distanceIsCube;
	private Boolean filter;
	private Boolean bungee;
	private String format;
	private int cooldown;
	private boolean irc;
	
	private static MineverseChat plugin = MineverseChat.getInstance();
	private static ChatChannel defaultChatChannel;
	private static ChatChannel[] channels;
	private static String defaultColor;
	
	public static void initialize() {
		String _color = "";
		String _chatcolor = "";
		String _name = "";
		String _permission = "";
		Boolean _mutable = false;
		Boolean _filter = false;
		Boolean _defaultchannel = false;
		String _alias = "";
		Double _distance = (double) 0;
		Boolean _distanceIsCube = false;
		Boolean _autojoin = false;
		Boolean _bungee = false;
		String _format = "";
		int _cooldown = 0;
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("channels");
		int len = (cs.getKeys(false)).size();
		channels = new ChatChannel[len];
		int x = 0;
		for(String key : cs.getKeys(false)) {
			_color = (String) cs.getString(key + ".color", "white");
			_chatcolor = (String) cs.getString(key + ".chatcolor", "white");
			if(!(Format.isValidColor(_color))) {
				plugin.getServer().getLogger().info("[" + plugin.getName() + "] " + _color + " is not valid. Changing to white.");
				_color = "white";
			}
			if(!(Format.isValidColor(_chatcolor)) && !_chatcolor.equalsIgnoreCase("None")) {
				plugin.getServer().getLogger().info("[" + plugin.getName() + "] " + _chatcolor + " is not valid. Changing to white.");
				_chatcolor = "white";
			}
			_name = key;
			_permission = (String) cs.getString(key + ".permissions", "None");
			_mutable = (Boolean) cs.getBoolean(key + ".mutable", false);
			_filter = (Boolean) cs.getBoolean(key + ".filter", true);
			_bungee = (Boolean) cs.getBoolean(key + ".bungeecord", false);
			_format = cs.getString(key + ".format", "Default");
			_defaultchannel = (Boolean) cs.getBoolean(key + ".default", false);
			_alias = (String) cs.getString(key + ".alias", "None");
			_distance = (Double) cs.getDouble(key + ".distance", (double) 0);
			_distanceIsCube = (Boolean) cs.getBoolean(key + ".distanceIsCube", false);
			_cooldown = (int) cs.getInt(key + ".cooldown", 0);
			_autojoin = (Boolean) cs.getBoolean(key + ".autojoin", false);
			ChatChannel c = new ChatChannel(_name, _color, _chatcolor, _permission, _mutable, _filter, _defaultchannel, _alias, _distance, _distanceIsCube, _autojoin, _bungee, _cooldown, _format);
			channels[x++] = c;
			if(_defaultchannel) {
				defaultChatChannel = c;
				defaultColor = _color;
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

	public ChatChannel(String _Name, String _color, String _chatcolor, String _Permission, Boolean _mutable, Boolean _filter, Boolean _defaultchannel, String _alias, Double _distance, Boolean _distanceIsCube, Boolean _autojoin, Boolean _bungee, int _cooldown, String _format) {
		name = _Name;
		permission = "venturechat." + _Permission;
		mutable = _mutable;
		setColor(_color);
		setChatColor(_chatcolor);
		setDefaultChannel(_defaultchannel);
		setAlias(_alias);
		setDistance(_distance);
		setDistanceIsCube(_distanceIsCube);
		setFilter(_filter);
		setAutojoin(_autojoin);
		setBungee(_bungee);
		setCooldown(_cooldown);
		setFormat(_format);
	}

	public String getName() {
		return name;
	}

	public void setFormat(String _format) {
		format = _format;
	}

	public String getFormat() {
		return format;
	}

	public void setCooldown(int _cooldown) {
		cooldown = _cooldown;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setBungee(Boolean _bungee) {
		bungee = _bungee;
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

	public void setAutojoin(Boolean _autojoin) {
		autojoin = _autojoin;
	}

	public Boolean isMutable() {
		return mutable;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getChatColor() {
		return chatcolor;
	}

	public void setChatColor(String chatcolor) {
		this.chatcolor = chatcolor;
	}

	public Boolean isDefaultchannel() {
		return defaultChannel;
	}

	public void setDefaultChannel(Boolean defaultChannel) {
		this.defaultChannel = defaultChannel;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public void setDistanceIsCube(Boolean distanceIsCube) { this.distanceIsCube = distanceIsCube; }

	public Boolean getDistanceIsCube() { return distanceIsCube; }

	public Boolean hasDistance() {
		return distance > 0;
	}

	public Boolean hasCooldown() {
		return cooldown > 0;
	}

	public Boolean hasPermission() {
		return !permission.equalsIgnoreCase("venturechat.none");
	}

	public Boolean isFiltered() {
		return filter;
	}

	public void setFilter(Boolean filter) {
		this.filter = filter;
	}

	public boolean isIRC() {
		return irc;
	}
	
	@Override
	public boolean equals(Object channel) {
		return channel instanceof ChatChannel && this.name.equals(((ChatChannel) channel).getName());
	}
}