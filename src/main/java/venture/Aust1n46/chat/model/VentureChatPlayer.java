package venture.Aust1n46.chat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Wrapper for {@link Player}
 * 
 * @author Aust1n46
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentureChatPlayer {
	@Setter(value = AccessLevel.NONE)
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

	public VentureChatPlayer(UUID uuid, String name, ChatChannel currentChannel, Set<UUID> ignores, Set<String> listening, HashMap<String, MuteContainer> mutes,
			Set<String> blockedCommands, boolean host, UUID party, boolean filter, boolean notifications, String jsonFormat, boolean spy, boolean commandSpy, boolean rangedSpy,
			boolean messageToggle, boolean bungeeToggle) {
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
		this.spy = spy;
		this.rangedSpy = rangedSpy;
		this.commandSpy = commandSpy;
		this.messages = new ArrayList<ChatMessage>();
		this.jsonFormat = jsonFormat;
		this.cooldowns = new HashMap<ChatChannel, Long>();
		this.spam = new HashMap<ChatChannel, List<Long>>();
		this.messageToggle = messageToggle;
		this.bungeeToggle = bungeeToggle;
	}

	public VentureChatPlayer(UUID uuid, String name, ChatChannel currentChannel) {
		this.uuid = uuid;
		this.name = name;
		this.currentChannel = currentChannel;
		this.ignores = new HashSet<UUID>();
		this.listening = new HashSet<String>();
		listening.add(currentChannel.getName());
		this.mutes = new HashMap<String, MuteContainer>();
		this.blockedCommands = new HashSet<String>();
		this.filter = true;
		this.notifications = true;
		this.messages = new ArrayList<ChatMessage>();
		this.jsonFormat = "Default";
		this.cooldowns = new HashMap<ChatChannel, Long>();
		this.spam = new HashMap<ChatChannel, List<Long>>();
		this.messageToggle = true;
		this.bungeeToggle = true;
	}

	public boolean getRangedSpy() {
		if (isOnline()) {
			if (!getPlayer().hasPermission("venturechat.rangedspy")) {
				setRangedSpy(false);
				return false;
			}
		}
		return this.rangedSpy;
	}

	public boolean setCurrentChannel(ChatChannel channel) {
		if (channel != null) {
			this.currentChannel = channel;
			return true;
		}
		return false;
	}

	public void addIgnore(UUID ignore) {
		this.ignores.add(ignore);
	}

	public void removeIgnore(UUID ignore) {
		this.ignores.remove(ignore);
	}

	public boolean isListening(String channel) {
		return listening.contains(channel);
	}

	public boolean addListening(String channel) {
		if (channel != null) {
			this.listening.add(channel);
			return true;
		}
		return false;
	}

	public boolean removeListening(String channel) {
		if (channel != null) {
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
		if (channel != null && time >= 0) {
			mutes.put(channel, new MuteContainer(channel, time, reason));
			return true;
		}
		return false;
	}

	public boolean removeMute(String channel) {
		if (channel != null) {
			mutes.remove(channel);
			return true;
		}
		return false;
	}

	public boolean isMuted(String channel) {
		return channel != null ? this.mutes.containsKey(channel) : false;
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

	public boolean hasParty() {
		return this.party != null;
	}

	public Player getPlayer() {
		return this.online ? this.player : null;
	}

	public boolean hasConversation() {
		return this.conversation != null;
	}

	public boolean isSpy() {
		if (this.isOnline()) {
			if (!this.getPlayer().hasPermission("venturechat.spy")) {
				this.setSpy(false);
				return false;
			}
		}
		return this.spy;
	}

	public boolean hasCommandSpy() {
		if (this.isOnline()) {
			if (!this.getPlayer().hasPermission("venturechat.commandspy")) {
				this.setCommandSpy(false);
				return false;
			}
		}
		return this.commandSpy;
	}

	public boolean setQuickChannel(ChatChannel channel) {
		if (channel != null) {
			this.quickChannel = channel;
			return true;
		}
		return false;
	}

	public boolean hasReplyPlayer() {
		return this.replyPlayer != null;
	}

	public boolean addCooldown(ChatChannel channel, long time) {
		if (channel != null && time > 0) {
			cooldowns.put(channel, time);
			return true;
		}
		return false;
	}

	public boolean removeCooldown(ChatChannel channel) {
		if (channel != null) {
			cooldowns.remove(channel);
			return true;
		}
		return false;
	}

	public boolean hasCooldown(ChatChannel channel) {
		return channel != null && this.cooldowns != null ? this.cooldowns.containsKey(channel) : false;
	}

	public boolean hasSpam(ChatChannel channel) {
		return channel != null && this.spam != null ? this.spam.containsKey(channel) : false;
	}

	public boolean addSpam(ChatChannel channel) {
		if (channel != null) {
			spam.put(channel, new ArrayList<Long>());
			return true;
		}
		return false;
	}

	public void addMessage(ChatMessage message) {
		if (this.messages.size() >= 100) {
			this.messages.remove(0);
		}
		this.messages.add(message);
	}

	public void clearMessages() {
		this.messages.clear();
	}
}
