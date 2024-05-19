package venture.Aust1n46.chat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
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
@Builder
public class VentureChatPlayer {
	@Setter(value = AccessLevel.NONE)
	private UUID uuid;
	private String name;
	private Player player;
	private boolean online;
	private ChatChannel currentChannel;
	private boolean quickChat;
	private ChatChannel quickChannel;
	private UUID conversation;
	private UUID replyPlayer;
	private boolean hasPlayed;
	private boolean modified;
	private boolean rangedSpy;
	private boolean spy;
	private boolean commandSpy;
	private UUID party;
	private boolean host;
	private boolean partyChat;
	private boolean editing;
	private int editHash;
	@Default
	private boolean filter = true;
	@Default
	private boolean notifications = true;
	@Default
	private boolean messageToggle = true;
	@Default
	private boolean bungeeToggle = true;
	@Default
	private String jsonFormat = "Default";
	@Default
	private Set<String> listening = new HashSet<>();
	@Default
	private Map<String, MuteContainer> mutes = new HashMap<>();
	@Default
	private Set<UUID> ignores = new HashSet<>();
	@Default
	private Map<ChatChannel, Long> cooldowns = new HashMap<>();
	@Default
	private Map<ChatChannel, List<Long>> spam = new HashMap<>();
	@Default
	private Set<String> blockedCommands = new HashSet<>();
	@Default
	private List<ChatMessage> messages = new ArrayList<>();
}
