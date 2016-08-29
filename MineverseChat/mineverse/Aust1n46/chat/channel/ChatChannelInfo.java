package mineverse.Aust1n46.chat.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

//This class stores an array of all of the channels and contains some channel related helper methods.
public class ChatChannelInfo {
	MineverseChat plugin;
	List<String> filters;
	List<String> blockablecommands;
	ChatChannel[] cc;
	public boolean useSuffix;
	public boolean chatPrefix;
	public String tellColor;
	private ChatChannel defaultChannel;
	public String defaultColor;

	// Broadcast Variables
	public String broadcastColor;
	public String broadcastDisplayTag;
	public String broadcastPermissions;

	@SuppressWarnings("unchecked")
	public ChatChannelInfo(MineverseChat _plugin) { //Contructor that creates the channels and puts them into their array
		plugin = _plugin;
		filters = (List<String>) plugin.getConfig().getList("filters");
		blockablecommands = (List<String>) plugin.getConfig().getList("blockablecommands");
		String _color = "";
		String _chatcolor = "";
		String _name = "";
		String _permission = "";
		Boolean _mutable = false;
		Boolean _filter = false;
		Boolean _defaultchannel = false;
		String _alias = "";
		Double _distance = (double) 0;
		Boolean _autojoin = false;
		Boolean _bungee = false;
		String _format = "";
		boolean irc = false;
		int _cooldown = 0;
		tellColor = "gray";
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("channels");
		ConfigurationSection bs = plugin.getConfig().getConfigurationSection("broadcast");
		broadcastColor = bs.getString("color", "white");
		broadcastPermissions = bs.getString("permissions", "");
		broadcastDisplayTag = Format.FormatStringAll(bs.getString("displaytag", "[Broadcast]"));
		chatPrefix = false;
		if(plugin.getConfig().getBoolean("chatPrefix", false)) {
			chatPrefix = plugin.getConfig().getBoolean("chatPrefix", false);
		}
		plugin.setLogLevel(plugin.getConfig().getString("loglevel", "INFO").toUpperCase());
		tellColor = plugin.getConfig().getString("tellcolor", "gray");
		int len = (cs.getKeys(false)).size();
		cc = new ChatChannel[len];
		int x = 0;
		for(String key : cs.getKeys(false)) {
			_color = (String) cs.getString(key + ".color", "white");
			_chatcolor = (String) cs.getString(key + ".chatcolor", "white");
			if(!(isValidColor(_color))) {
				plugin.getServer().getLogger().info("[" + plugin.getName() + "] " + _color + " is not valid. Changing to white.");
				_color = "white";
			}
			if(!(isValidColor(_chatcolor)) && !_chatcolor.equalsIgnoreCase("None")) {
				plugin.getServer().getLogger().info("[" + plugin.getName() + "] " + _chatcolor + " is not valid. Changing to white.");
				_chatcolor = "white";
			}
			_name = key;
			_permission = (String) cs.getString(key + ".permissions", "None");
			_mutable = (Boolean) cs.getBoolean(key + ".mutable", false);
			_filter = (Boolean) cs.getBoolean(key + ".filter", true);
			_bungee = (Boolean) cs.getBoolean(key + ".bungeecord", false);
			_format = cs.getString(key + ".format", "Default");
			irc = cs.getBoolean(key + ".irc", false);
			_defaultchannel = (Boolean) cs.getBoolean(key + ".default", false);
			_alias = (String) cs.getString(key + ".alias", "None");
			_distance = (Double) cs.getDouble(key + ".distance", (double) 0);
			_cooldown = (int) cs.getInt(key + ".cooldown", 0);
			_autojoin = (Boolean) cs.getBoolean(key + ".autojoin", false);
			ChatChannel c = new ChatChannel(_name, _color, _chatcolor, _permission, _mutable, _filter, _defaultchannel, _alias, _distance, _autojoin, _bungee, _cooldown, _format, irc);
			cc[x++] = c;
			if(_defaultchannel) {
				defaultChannel = c;
				defaultColor = _color;
			}
		}
	}

	public List<ChatChannel> getAutojoinList() {
		List<ChatChannel> joinlist = new ArrayList<ChatChannel>();
		for(ChatChannel c : cc) {
			if(c.getAutojoin()) {
				joinlist.add(c);
			}
		}
		return joinlist;
	}

	int getChannelCount() {
		return cc.length;
	}

	void logChannelList() {
		for(ChatChannel p : cc) {
			plugin.getServer().getLogger().info("[" + plugin.getName() + "]" + p.getName() + ":" + p.getChatColor() + ":" + p.getPermission() + ":" + p.isMutable() + ":" + p.isFiltered() + ":" + p.isDefaultchannel());
		}
	}

	public Boolean isValidColor(String _color) {
		Boolean bFound = false;
		for(ChatColor bkColors : ChatColor.values()) {
			if(_color.equalsIgnoreCase(bkColors.name())) {
				bFound = true;
			}
		}
		return bFound;
	}

	public ChatChannel[] getChannelsInfo() {
		return cc;
	}

	public ChatChannel getChannelInfo(String ChannelName) {
		for(ChatChannel c : cc) {
			if(c.getName().equalsIgnoreCase(ChannelName) || c.getAlias().equalsIgnoreCase(ChannelName)) {
				return c;
			}
		}
		return null;
	}
	
	public ChatChannel getDefaultChannel() {
		return this.defaultChannel;
	}
	
	public boolean isChannel(String channel) {
		return this.getChannelInfo(channel) != null;
	}

	public String FilterChat(String msg) {
		int t = 0;
		for(String s : filters) {
			t = 0;
			String[] pparse = new String[2];
			pparse[0] = " ";
			pparse[1] = " ";
			StringTokenizer st = new StringTokenizer(s, ",");
			while(st.hasMoreTokens()) {
				if(t < 2) {
					pparse[t++] = st.nextToken();
				}
			}
			msg = msg.replaceAll("(?i)" + pparse[0], pparse[1]);
		}
		return msg;
	}

	List<String> getFilters() {
		return filters;
	}
}