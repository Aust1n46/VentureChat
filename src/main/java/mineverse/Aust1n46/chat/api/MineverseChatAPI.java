package mineverse.Aust1n46.chat.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;

/**
 * API class for looking up wrapped {@link MineverseChatPlayer} objects from
 * {@link Player}, {@link UUID}, or {@link String} user names.
 *
 * @author Aust1n46
 */
public final class MineverseChatAPI {
    private static HashMap<UUID, MineverseChatPlayer> playerMap = new HashMap<UUID, MineverseChatPlayer>();
    private static HashMap<String, UUID> namesMap = new HashMap<String, UUID>();
    private static HashMap<UUID, MineverseChatPlayer> onlinePlayerMap = new HashMap<UUID, MineverseChatPlayer>();
    private static List<String> networkPlayerNames = new ArrayList<String>();

    private static HashMap<UUID, SynchronizedMineverseChatPlayer> proxyPlayerMap = new HashMap<UUID, SynchronizedMineverseChatPlayer>();

    public static List<String> getNetworkPlayerNames() {
        return networkPlayerNames;
    }

    public static void clearNetworkPlayerNames() {
        networkPlayerNames.clear();
    }

    public static void addNetworkPlayerName(String name) {
        networkPlayerNames.add(name);
    }

    public static void addSynchronizedMineverseChatPlayerToMap(SynchronizedMineverseChatPlayer smcp) {
        proxyPlayerMap.put(smcp.getUUID(), smcp);
    }

    @Deprecated
    public static void clearBungeePlayerMap() {
        clearProxyPlayerMap();
    }
    
    public static void clearProxyPlayerMap() {
        proxyPlayerMap.clear();
    }

    public static Collection<SynchronizedMineverseChatPlayer> getSynchronizedMineverseChatPlayers() {
        return proxyPlayerMap.values();
    }

    public static void addNameToMap(MineverseChatPlayer mcp) {
        namesMap.put(mcp.getName(), mcp.getUUID());
    }

    public static void removeNameFromMap(String name) {
        namesMap.remove(name);
    }

    public static void clearNameMap() {
        namesMap.clear();
    }

    @SuppressWarnings("deprecation")
    public static void addMineverseChatPlayerToMap(MineverseChatPlayer mcp) {
        playerMap.put(mcp.getUUID(), mcp);
        MineverseChat.players.add(mcp);
    }

    @SuppressWarnings("deprecation")
    public static void clearMineverseChatPlayerMap() {
        playerMap.clear();
        MineverseChat.players.clear();
    }

    public static Collection<MineverseChatPlayer> getMineverseChatPlayers() {
        return playerMap.values();
    }

    @SuppressWarnings("deprecation")
    public static void addMineverseChatOnlinePlayerToMap(MineverseChatPlayer mcp) {
        onlinePlayerMap.put(mcp.getUUID(), mcp);
        MineverseChat.onlinePlayers.add(mcp);
    }

    @SuppressWarnings("deprecation")
    public static void removeMineverseChatOnlinePlayerToMap(MineverseChatPlayer mcp) {
        onlinePlayerMap.remove(mcp.getUUID());
        MineverseChat.onlinePlayers.remove(mcp);
    }

    @SuppressWarnings("deprecation")
    public static void clearOnlineMineverseChatPlayerMap() {
        onlinePlayerMap.clear();
        MineverseChat.onlinePlayers.clear();
    }

    public static Collection<MineverseChatPlayer> getOnlineMineverseChatPlayers() {
        return onlinePlayerMap.values();
    }

    /**
     * Get a MineverseChatPlayer wrapper from a Bukkit Player instance.
     *
     * @param player {@link Player} object.
     * @return {@link MineverseChatPlayer}
     */
    public static MineverseChatPlayer getMineverseChatPlayer(Player player) {
        return getMineverseChatPlayer(player.getUniqueId());
    }

    /**
     * Get a MineverseChatPlayer wrapper from a UUID.
     *
     * @param uuid {@link UUID}.
     * @return {@link MineverseChatPlayer}
     */
    public static MineverseChatPlayer getMineverseChatPlayer(UUID uuid) {
        return playerMap.get(uuid);
    }

    /**
     * Get a MineverseChatPlayer wrapper from a user name.
     *
     * @param name {@link String}.
     * @return {@link MineverseChatPlayer}
     */
    public static MineverseChatPlayer getMineverseChatPlayer(String name) {
        return getMineverseChatPlayer(namesMap.get(name));
    }

    /**
     * Get a MineverseChatPlayer wrapper from a Bukkit Player instance. Only checks
     * current online players. Much more efficient!
     *
     * @param player {@link Player} object.
     * @return {@link MineverseChatPlayer}
     */
    public static MineverseChatPlayer getOnlineMineverseChatPlayer(Player player) {
        return getOnlineMineverseChatPlayer(player.getUniqueId());
    }

    /**
     * Get a MineverseChatPlayer wrapper from a UUID. Only checks current online
     * players. Much more efficient!
     *
     * @param uuid {@link UUID}.
     * @return {@link MineverseChatPlayer}
     */
    public static MineverseChatPlayer getOnlineMineverseChatPlayer(UUID uuid) {
        return onlinePlayerMap.get(uuid);
    }

    /**
     * Get a MineverseChatPlayer wrapper from a user name. Only checks current
     * online players. Much more efficient!
     *
     * @param name {@link String}.
     * @return {@link MineverseChatPlayer}
     */
    public static MineverseChatPlayer getOnlineMineverseChatPlayer(String name) {
        return getOnlineMineverseChatPlayer(namesMap.get(name));
    }

    /**
     * Get a SynchronizedMineverseChatPlayer from a UUID.
     *
     * @param uuid {@link UUID}
     * @return {@link SynchronizedMineverseChatPlayer}
     */
    public static SynchronizedMineverseChatPlayer getSynchronizedMineverseChatPlayer(UUID uuid) {
        return proxyPlayerMap.get(uuid);
    }
}
