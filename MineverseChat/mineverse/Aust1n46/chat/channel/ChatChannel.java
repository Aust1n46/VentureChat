package mineverse.Aust1n46.chat.channel;

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
	private Boolean filter;
	private Boolean bungee;
	private String format;
	private int cooldown;
	private boolean irc;

	public ChatChannel(String _Name, String _color, String _chatcolor, String _Permission, Boolean _mutable, Boolean _filter, Boolean _defaultchannel, String _alias, Double _distance, Boolean _autojoin, Boolean _bungee, int _cooldown, String _format, boolean irc) {
		name = _Name;
		permission = "venturechat." + _Permission;
		mutable = _mutable;
		this.irc = irc;
		setColor(_color);
		setChatColor(_chatcolor);
		setDefaultChannel(_defaultchannel);
		setAlias(_alias);
		setDistance(_distance);
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