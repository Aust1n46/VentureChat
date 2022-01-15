package venture.Aust1n46.chat.service.proxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import com.google.inject.Singleton;

import venture.Aust1n46.chat.model.SynchronizedVentureChatPlayer;

@Singleton
public class VentureChatProxyPlayerApiService {
	private final HashMap<UUID, SynchronizedVentureChatPlayer> proxyPlayerMap = new HashMap<>();

	public void addSynchronizedMineverseChatPlayerToMap(SynchronizedVentureChatPlayer smcp) {
		proxyPlayerMap.put(smcp.getUUID(), smcp);
	}

	public void clearProxyPlayerMap() {
		proxyPlayerMap.clear();
	}

	public Collection<SynchronizedVentureChatPlayer> getSynchronizedMineverseChatPlayers() {
		return proxyPlayerMap.values();
	}

	/**
	 * Get a SynchronizedMineverseChatPlayer from a UUID.
	 *
	 * @param uuid {@link UUID}
	 * @return {@link SynchronizedVentureChatPlayer}
	 */
	public SynchronizedVentureChatPlayer getSynchronizedMineverseChatPlayer(UUID uuid) {
		return proxyPlayerMap.get(uuid);
	}
}
