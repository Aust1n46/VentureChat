package mineverse.Aust1n46.chat.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.channel.ChatChannel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

//Wrapper class for Player, this class stores additional information along with a players Player pointer.  
//This data is read in from the PlayerData file and is also saved to that file when the server is closed.
public class MineverseChatPlayer {
	private UUID uuid;
	private String name;
	private ChatChannel currentChannel;
	private Set<UUID> ignores;
	private Set<String> listening;
	private HashMap<String, Integer> mutes;
	private Set<String> blockedCommands;
	private List<String> mail;
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
	private boolean afk;
	private boolean quickChat;
	private ChatChannel quickChannel;
	private UUID replyPlayer;
	private HashMap<ChatChannel, Integer> cooldowns;
	private boolean partyChat;
	private HashMap<ChatChannel, List<Integer>> spam;
	private boolean modified;
	private List<ChatMessage> messages;
	private String jsonFormat;
	private boolean editing;
	private int editHash;
	private boolean rangedSpy;
	private boolean messageToggle;
	private boolean bungeeToggle;

	//buttons variable no longer used
	@Deprecated
	public MineverseChatPlayer(UUID uuid, String name, ChatChannel currentChannel, Set<UUID> ignores, Set<String> listening, HashMap<String, Integer> mutes, Set<String> blockedCommands, List<String> mail, boolean host, UUID party, boolean filter, boolean notifications, String nickname, String jsonFormat, boolean spy, boolean commandSpy, boolean rangedSpy, boolean buttons, boolean messageToggle, boolean bungeeToggle) {
		this.uuid = uuid;
		this.name = name;
		this.currentChannel = currentChannel;
		this.ignores = ignores;
		this.listening = listening;
		this.mutes = mutes;
		this.blockedCommands = blockedCommands;
		this.mail = mail;
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
		this.afk = false;
		this.quickChat = false;
		this.quickChannel = null;
		this.replyPlayer = null;
		this.partyChat = false;
		this.modified = false;
		this.messages = new ArrayList<ChatMessage>();
		this.jsonFormat = jsonFormat;
		this.cooldowns = new HashMap<ChatChannel, Integer>();
		this.spam = new HashMap<ChatChannel, List<Integer>>();
		this.messageToggle = messageToggle;
		this.bungeeToggle = bungeeToggle;
	}
	
	public MineverseChatPlayer(UUID uuid, String name, ChatChannel currentChannel, Set<UUID> ignores, Set<String> listening, HashMap<String, Integer> mutes, Set<String> blockedCommands, List<String> mail, boolean host, UUID party, boolean filter, boolean notifications, String nickname, String jsonFormat, boolean spy, boolean commandSpy, boolean rangedSpy, boolean messageToggle, boolean bungeeToggle) {
		this.uuid = uuid;
		this.name = name;
		this.currentChannel = currentChannel;
		this.ignores = ignores;
		this.listening = listening;
		this.mutes = mutes;
		this.blockedCommands = blockedCommands;
		this.mail = mail;
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
		this.afk = false;
		this.quickChat = false;
		this.quickChannel = null;
		this.replyPlayer = null;
		this.partyChat = false;
		this.modified = false;
		this.messages = new ArrayList<ChatMessage>();
		this.jsonFormat = jsonFormat;
		this.cooldowns = new HashMap<ChatChannel, Integer>();
		this.spam = new HashMap<ChatChannel, List<Integer>>();
		this.messageToggle = messageToggle;
		this.bungeeToggle = bungeeToggle;
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

	public HashMap<String, Integer> getMutes() {
		return this.mutes;
	}

	public boolean addMute(String channel, int time) {
		if(channel != null && time >= 0) {
			mutes.put(channel, time);
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

	public List<String> getMail() {
		return this.mail;
	}

	public void addMail(String mail) {
		this.mail.add(mail);
	}

	public void removeMail(String mail) {
		this.mail.remove(mail);
	}

	public void removeMail(int index) {
		this.mail.remove(index);
	}

	public void clearMail() {
		this.mail.clear();
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
		return this.nickname;
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
		return online ? this.player : null;
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
		return this.spy;
	}

	public void setSpy(boolean spy) {
		this.spy = spy;
	}

	public boolean hasCommandSpy() {
		return this.commandSpy;
	}

	public void setCommandSpy(boolean commandSpy) {
		this.commandSpy = commandSpy;
	}

	public boolean isAFK() {
		return this.afk;
	}

	public void setAFK(boolean afk) {
		this.afk = afk;
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

	public HashMap<ChatChannel, Integer> getCooldowns() {
		return this.cooldowns;
	}

	public boolean addCooldown(ChatChannel channel, int time) {
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
	
	public HashMap<ChatChannel, List<Integer>> getSpam() {
		return this.spam;
	}
	
	public boolean hasSpam(ChatChannel channel) {
		return channel != null && this.spam != null ? this.spam.containsKey(channel) : false;
	}
	
	public boolean addSpam(ChatChannel channel) {
		if(channel != null) {
			spam.put(channel, new ArrayList<Integer>());
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

	public void removeMessage(ChatMessage message) {
		this.messages.remove(message);
	}

	public void removeMessage(int hash) {
		for(ChatMessage m : this.messages) {
			if(m.getHash() == hash) {
				m = new ChatMessage(m.getComponent(), m.getSender(), ChatColor.RED + "Message Removed", (ChatColor.RED + "Message Removed").hashCode());
			}
		}
	}

	public void clearMessages() {
		this.messages.clear();
	}

	public String getJsonFormat() {
		return this.jsonFormat;
	}

	public void setJsonFormat(String jsonFormat) {
		this.jsonFormat = jsonFormat;
	}
}