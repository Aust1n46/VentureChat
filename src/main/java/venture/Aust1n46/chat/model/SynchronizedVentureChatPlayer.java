package venture.Aust1n46.chat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class SynchronizedVentureChatPlayer {
	private UUID uuid;
	@Default
	private Set<String> listening = new HashSet<>();
	@Default
	private HashMap<String, MuteContainer> mutes = new HashMap<>();
	@Default
	private Set<UUID> ignores = new HashSet<>();
	@Default
	private List<String> messageData = new ArrayList<>();
	@Default
	private boolean messageToggleEnabled = true;
	private boolean spy;
	private int messagePackets;
}
