package venture.Aust1n46.chat.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.inject.Singleton;

import venture.Aust1n46.chat.model.VentureChatPlayer;

/**
 * API class for looking up wrapped {@link VentureChatPlayer} objects from
 * {@link Player}, {@link UUID}, or {@link String} user names.
 *
 * @author Aust1n46
 */
@Singleton
public class PlayerApiService {
	private final HashMap<UUID, VentureChatPlayer> playerMap = new HashMap<>();
	private final HashMap<String, UUID> namesMap = new HashMap<>();
	private final HashMap<UUID, VentureChatPlayer> onlinePlayerMap = new HashMap<>();
	private final List<String> networkPlayerNames = new ArrayList<>();

	public void addNameToMap(VentureChatPlayer mcp) {
		namesMap.put(mcp.getName(), mcp.getUuid());
	}

	public void removeNameFromMap(String name) {
		namesMap.remove(name);
	}

	public void clearNameMap() {
		namesMap.clear();
	}

	public void addMineverseChatPlayerToMap(VentureChatPlayer mcp) {
		playerMap.put(mcp.getUuid(), mcp);
	}

	public void clearMineverseChatPlayerMap() {
		playerMap.clear();
	}

	public Collection<VentureChatPlayer> getMineverseChatPlayers() {
		return playerMap.values();
	}

	public void addMineverseChatOnlinePlayerToMap(VentureChatPlayer mcp) {
		onlinePlayerMap.put(mcp.getUuid(), mcp);
	}

	public void removeMineverseChatOnlinePlayerToMap(VentureChatPlayer mcp) {
		onlinePlayerMap.remove(mcp.getUuid());
	}

	public void clearOnlineMineverseChatPlayerMap() {
		onlinePlayerMap.clear();
	}

	public Collection<VentureChatPlayer> getOnlineMineverseChatPlayers() {
		return onlinePlayerMap.values();
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a Bukkit Player instance.
	 *
	 * @param player {@link Player} object.
	 * @return {@link VentureChatPlayer}
	 */
	public VentureChatPlayer getMineverseChatPlayer(Player player) {
		return getMineverseChatPlayer(player.getUniqueId());
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a UUID.
	 *
	 * @param uuid {@link UUID}.
	 * @return {@link VentureChatPlayer}
	 */
	public VentureChatPlayer getMineverseChatPlayer(UUID uuid) {
		return playerMap.get(uuid);
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a user name.
	 *
	 * @param name {@link String}.
	 * @return {@link VentureChatPlayer}
	 */
	public VentureChatPlayer getMineverseChatPlayer(String name) {
		return getMineverseChatPlayer(namesMap.get(name));
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a Bukkit Player instance. Only checks
	 * current online players. Much more efficient!
	 *
	 * @param player {@link Player} object.
	 * @return {@link VentureChatPlayer}
	 */
	public VentureChatPlayer getOnlineMineverseChatPlayer(final Player player) {
		return getOnlineMineverseChatPlayer(player.getUniqueId());
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a UUID. Only checks current online
	 * players. Much more efficient!
	 *
	 * @param uuid {@link UUID}.
	 * @return {@link VentureChatPlayer}
	 */
	public VentureChatPlayer getOnlineMineverseChatPlayer(UUID uuid) {
		return onlinePlayerMap.get(uuid);
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a user name. Only checks current
	 * online players. Much more efficient!
	 *
	 * @param name {@link String}.
	 * @return {@link VentureChatPlayer}
	 */
	public VentureChatPlayer getOnlineMineverseChatPlayer(String name) {
		return getOnlineMineverseChatPlayer(namesMap.get(name));
	}

	public List<String> getNetworkPlayerNames() {
		return networkPlayerNames;
	}

	public void clearNetworkPlayerNames() {
		networkPlayerNames.clear();
	}

	public void addNetworkPlayerName(String name) {
		networkPlayerNames.add(name);
	}
}
