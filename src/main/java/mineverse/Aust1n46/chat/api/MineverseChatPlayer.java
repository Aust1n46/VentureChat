package mineverse.Aust1n46.chat.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.mute.MuteContainer;
import mineverse.Aust1n46.chat.json.JsonFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

//Wrapper class for Player, this class stores additional information along with a players Player pointer.  
//This data is read in from the PlayerData file and is also saved to that file when the server is closed.
public class MineverseChatPlayer {
	private UUID uuid;
	private String name;
	private ChatChannel currentChannel;
	private Set<UUID> ignores;
	private Set<String> listening;
	private HashMap<String, MuteContainer> mutes;
	private Set<String> blockedCommands;
	private boolean host;
	private UUID party;
	private boolean filter;
	private boolean notifications;
	private String nickname;
	private boolean online;
	private Player player;
	private boolean hasPlayed;
	private UUID conversation;
	private boolean spy;
	private boolean commandSpy;
	private boolean quickChat;
	private ChatChannel quickChannel;
	private UUID replyPlayer;
	private HashMap<ChatChannel, Long> cooldowns;
	private boolean partyChat;
	private HashMap<ChatChannel, List<Long>> spam;
	private boolean modified;
	private List<ChatMessage> messages;
	private String jsonFormat;
	private boolean editing;
	private int editHash;
	private boolean rangedSpy;
	private boolean messageToggle;
	private boolean bungeeToggle;
	
	public MineverseChatPlayer(UUID uuid, String name, ChatChannel currentChannel, Set<UUID> ignores, Set<String> listening, HashMap<String, MuteContainer> mutes, Set<String> blockedCommands, boolean host, UUID party, boolean filter, boolean notifications, String nickname, String jsonFormat, boolean spy, boolean commandSpy, boolean rangedSpy, boolean messageToggle, boolean bungeeToggle) {
		this.uuid = uuid;
		this.name = name;
		this.currentChannel = currentChannel;
		this.ignores = ignores;
		this.listening = listening;
		this.mutes = mutes;
		this.blockedCommands = blockedCommands;
		this.host = host;
		this.party = party;
		this.filter = filter;
		this.notifications = notifications;
		this.nickname = nickname;
		this.online = false;
		this.player = null;
		this.hasPlayed = false;
		this.conversation = null;
		this.spy = spy;
		this.rangedSpy = rangedSpy;
		this.commandSpy = commandSpy;
		this.quickChat = false;
		this.quickChannel = null;
		this.replyPlayer = null;
		this.partyChat = false;
		this.modified = false;
		this.messages = new ArrayList<ChatMessage>();
		this.jsonFormat = jsonFormat;
		this.cooldowns = new HashMap<ChatChannel, Long>();
		this.spam = new HashMap<ChatChannel, List<Long>>();
		this.messageToggle = messageToggle;
		this.bungeeToggle = bungeeToggle;
	}
	
	public MineverseChatPlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.currentChannel = ChatChannel.getDefaultChannel();
		this.ignores = new HashSet<UUID>();
		this.listening = new HashSet<String>();
		listening.add(currentChannel.getName());
		this.mutes = new HashMap<String, MuteContainer>();
		this.blockedCommands = new HashSet<String>();
		this.host = false;
		this.party = null;
		this.filter = true;
		this.notifications = true;
		this.nickname = name;
		this.online = false;
		this.player = null;
		this.hasPlayed = false;
		this.conversation = null;
		this.spy = false;
		this.rangedSpy = false;
		this.commandSpy = false;
		this.quickChat = false;
		this.quickChannel = null;
		this.replyPlayer = null;
		this.partyChat = false;
		this.modified = false;
		this.messages = new ArrayList<ChatMessage>();
		this.jsonFormat = "Default";
		this.cooldowns = new HashMap<ChatChannel, Long>();
		this.spam = new HashMap<ChatChannel, List<Long>>();
		this.messageToggle = true;
		this.bungeeToggle = true;
	}
	
	public boolean getBungeeToggle() {
		return this.bungeeToggle;
	}
	
	public void setBungeeToggle(boolean bungeeToggle) {
		this.bungeeToggle = bungeeToggle;
	}
	
	public boolean getMessageToggle() {
		return this.messageToggle;
	}
	
	public void setMessageToggle(boolean messageToggle) {
		this.messageToggle = messageToggle;
	}
	
	public boolean getRangedSpy() {
		if(isOnline()) {
			if(!getPlayer().hasPermission("venturechat.rangedspy")) {
				setRangedSpy(false);
				return false;
			}
		}
		return this.rangedSpy;
	}
	
	public void setRangedSpy(boolean rangedSpy) {
		this.rangedSpy = rangedSpy;
	}
	
	public int getEditHash() {
		return this.editHash;
	}
	
	public void setEditHash(int editHash) {
		this.editHash = editHash;
	}
	
	public boolean isEditing() {
		return this.editing;
	}
	
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public ChatChannel getCurrentChannel() {
		return this.currentChannel;
	}

	public boolean setCurrentChannel(ChatChannel channel) {
		if(channel != null) {
			this.currentChannel = channel;
			return true;
		}
		return false;
	}

	public Set<UUID> getIgnores() {
		return this.ignores;
	}

	public void addIgnore(UUID ignore) {
		this.ignores.add(ignore);
	}

	public void removeIgnore(UUID ignore) {
		this.ignores.remove(ignore);
	}

	public Set<String> getListening() {
		return this.listening;
	}
	
	public boolean isListening(String channel) {
		if(this.isOnline()) {
			if(ChatChannel.isChannel(channel)) {
				ChatChannel chatChannel = ChatChannel.getChannel(channel);
				if(chatChannel.hasPermission()) {
					if(!this.getPlayer().hasPermission(chatChannel.getPermission())) {
						if(this.getCurrentChannel().equals(chatChannel)) {
							this.setCurrentChannel(ChatChannel.getDefaultChannel());
						}
						this.removeListening(channel);
						return false;
					}
				}
			}
		}
		return this.listening.contains(channel);
	}

	public boolean addListening(String channel) {
		if(channel != null) {
			this.listening.add(channel);
			return true;
		}
		return false;
	}

	public boolean removeListening(String channel) {
		if(channel != null) {
			this.listening.remove(channel);
			return true;
		}
		return false;
	}

	public void clearListening() {
		this.listening.clear();
	}

	public Collection<MuteContainer> getMutes() {
		return this.mutes.values();
	}
	
	public MuteContainer getMute(String channel) {
		return mutes.get(channel);
	}

	public boolean addMute(String channel) {
		return addMute(channel, 0, "");
	}
	
	public boolean addMute(String channel, long time) {
		return addMute(channel, time, "");
	}
	
	public boolean addMute(String channel, String reason) {
		return addMute(channel, 0, reason);
	}
	
	public boolean addMute(String channel, long time, String reason) {
		if(channel != null && time >= 0) {
			mutes.put(channel, new MuteContainer(channel, time, reason));
			return true;
		}
		return false;
	}

	public boolean removeMute(String channel) {
		if(channel != null) {
			mutes.remove(channel);
			return true;
		}
		return false;
	}

	public boolean isMuted(String channel) {
		return channel != null ? this.mutes.containsKey(channel) : false;
	}

	public Set<String> getBlockedCommands() {
		return this.blockedCommands;
	}

	public void addBlockedCommand(String command) {
		this.blockedCommands.add(command);
	}

	public void removeBlockedCommand(String command) {
		this.blockedCommands.remove(command);
	}

	public boolean isBlockedCommand(String command) {
		return this.blockedCommands.contains(command);
	}

	public boolean isHost() {
		return this.host;
	}

	public void setHost(boolean host) {
		this.host = host;
	}

	public UUID getParty() {
		return this.party;
	}

	public void setParty(UUID party) {
		this.party = party;
	}

	public boolean hasParty() {
		return this.party != null;
	}

	public boolean hasFilter() {
		return this.filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	public boolean hasNotifications() {
		return this.notifications;
	}

	public void setNotifications(boolean notifications) {
		this.notifications = notifications;
	}

	public String getNickname() {
		return this.online ? this.player.getDisplayName() : this.nickname;
	}

	public void setNickname(String nick) {
		this.nickname = nick;
	}

	public boolean hasNickname() {
		return !this.name.equals(this.nickname);
	}

	public boolean isOnline() {
		return this.online;
	}

	public void setOnline(boolean online) {
		this.online = online;
		if(this.online) {
			this.player = Bukkit.getPlayer(name);
		}
		else {
			this.player = null;
		}
	}

	public Player getPlayer() {
		return this.online ? this.player : null;
	}

	public boolean hasPlayed() {
		return this.hasPlayed;
	}

	public void setHasPlayed(boolean played) {
		this.hasPlayed = played;
	}

	public UUID getConversation() {
		return this.conversation;
	}

	public void setConversation(UUID conversation) {
		this.conversation = conversation;
	}

	public boolean hasConversation() {
		return this.conversation != null;
	}

	public boolean isSpy() {
		if(this.isOnline()) {
			if(!this.getPlayer().hasPermission("venturechat.spy")) {
				this.setSpy(false);
				return false;
			}
		}
		return this.spy;
	}

	public void setSpy(boolean spy) {
		this.spy = spy;
	}

	public boolean hasCommandSpy() {
		if(this.isOnline()) {
			if(!this.getPlayer().hasPermission("venturechat.commandspy")) {
				this.setCommandSpy(false);
				return false;
			}
		}
		return this.commandSpy;
	}

	public void setCommandSpy(boolean commandSpy) {
		this.commandSpy = commandSpy;
	}

	public boolean isQuickChat() {
		return this.quickChat;
	}

	public void setQuickChat(boolean quickChat) {
		this.quickChat = quickChat;
	}

	public ChatChannel getQuickChannel() {
		return this.quickChannel;
	}

	public boolean setQuickChannel(ChatChannel channel) {
		if(channel != null) {
			this.quickChannel = channel;
			return true;
		}
		return false;
	}

	public boolean hasQuickChannel() {
		return this.quickChannel != null;
	}

	public UUID getReplyPlayer() {
		return this.replyPlayer;
	}

	public void setReplyPlayer(UUID replyPlayer) {
		this.replyPlayer = replyPlayer;
	}

	public boolean hasReplyPlayer() {
		return this.replyPlayer != null;
	}

	public boolean isPartyChat() {
		return this.partyChat;
	}

	public void setPartyChat(boolean partyChat) {
		this.partyChat = partyChat;
	}

	public HashMap<ChatChannel, Long> getCooldowns() {
		return this.cooldowns;
	}

	public boolean addCooldown(ChatChannel channel, long time) {
		if(channel != null && time > 0) {
			cooldowns.put(channel, time);
			return true;
		}
		return false;
	}

	public boolean removeCooldown(ChatChannel channel) {
		if(channel != null) {
			cooldowns.remove(channel);
			return true;
		}
		return false;
	}

	public boolean hasCooldown(ChatChannel channel) {
		return channel != null && this.cooldowns != null ? this.cooldowns.containsKey(channel) : false;
	}
	
	public HashMap<ChatChannel, List<Long>> getSpam() {
		return this.spam;
	}
	
	public boolean hasSpam(ChatChannel channel) {
		return channel != null && this.spam != null ? this.spam.containsKey(channel) : false;
	}
	
	public boolean addSpam(ChatChannel channel) {
		if(channel != null) {
			spam.put(channel, new ArrayList<Long>());
			return true;
		}
		return false;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean wasModified() {
		return this.modified;
	}

	public List<ChatMessage> getMessages() {
		return this.messages;
	}

	public void addMessage(ChatMessage message) {
		if(this.messages.size() >= 100) {
			this.messages.remove(0);
		}
		this.messages.add(message);
	}

	public void clearMessages() {
		this.messages.clear();
	}

	public String getJsonFormat() {
		return this.jsonFormat;
	}

	public void setJsonFormat() {
		this.jsonFormat = "Default";
		for(JsonFormat j : JsonFormat.getJsonFormats()) {
			if(this.getPlayer().isPermissionSet("venturechat.json." + j.getName())) {
				if(JsonFormat.getJsonFormat(this.getJsonFormat()).getPriority() > j.getPriority()) {
					this.jsonFormat = j.getName();
				}
			}
		}
	}
}
