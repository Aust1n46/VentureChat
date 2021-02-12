package mineverse.Aust1n46.chat.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.bungee.MineverseChatBungee;

/**
 * API class for looking up wrapped {@link MineverseChatPlayer} objects from
 * {@link Player}, {@link UUID}, or {@link String} user names.
 * 
 * @author Aust1n46
 */
public final class MineverseChatAPI {

	/**
	 * Get a MineverseChatPlayer wrapper from a Bukkit Player instance.
	 * 
	 * @param player
	 *            {@link Player} object.
	 * @return {@link MineverseChatPlayer}
	 */
	public static MineverseChatPlayer getMineverseChatPlayer(Player player) {
		for (MineverseChatPlayer mcp : MineverseChat.players) {
			try {
				if (mcp.getName().equals(player.getName())) {
					return mcp;
				}
			} catch (Exception exception) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a UUID.
	 * 
	 * @param uuid
	 *            {@link UUID}.
	 * @return {@link MineverseChatPlayer}
	 */
	public static MineverseChatPlayer getMineverseChatPlayer(UUID uuid) {
		for (MineverseChatPlayer mcp : MineverseChat.players) {
			try {
				if (mcp.getUUID().toString().equals(uuid.toString())) {
					return mcp;
				}
			} catch (Exception exception) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a user name.
	 * 
	 * @param name
	 *            {@link String}.
	 * @return {@link MineverseChatPlayer}
	 */
	public static MineverseChatPlayer getMineverseChatPlayer(String name) {
		for (MineverseChatPlayer mcp : MineverseChat.players) {
			try {
				if (mcp.getName().equalsIgnoreCase(name)) {
					return mcp;
				}
			} catch (Exception exception) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a Bukkit Player instance. Only checks
	 * current online players. Much more efficient!
	 * 
	 * @param player
	 *            {@link Player} object.
	 * @return {@link MineverseChatPlayer}
	 */
	public static MineverseChatPlayer getOnlineMineverseChatPlayer(Player player) {
		for (MineverseChatPlayer mcp : MineverseChat.onlinePlayers) {
			try {
				if (mcp.getName().equals(player.getName())) {
					return mcp;
				}
			} catch (Exception exception) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a UUID. Only checks current online
	 * players. Much more efficient!
	 * 
	 * @param uuid
	 *            {@link UUID}.
	 * @return {@link MineverseChatPlayer}
	 */
	public static MineverseChatPlayer getOnlineMineverseChatPlayer(UUID uuid) {
		for (MineverseChatPlayer mcp : MineverseChat.onlinePlayers) {
			try {
				if (mcp.getUUID().toString().equals(uuid.toString())) {
					return mcp;
				}
			} catch (Exception exception) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Get a MineverseChatPlayer wrapper from a user name. Only checks current
	 * online players. Much more efficient!
	 * 
	 * @param name
	 *            {@link String}.
	 * @return {@link MineverseChatPlayer}
	 */
	public static MineverseChatPlayer getOnlineMineverseChatPlayer(String name) {
		for (MineverseChatPlayer mcp : MineverseChat.onlinePlayers) {
			try {
				if (mcp.getName().equalsIgnoreCase(name)) {
					return mcp;
				}
			} catch (Exception exception) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Get a SynchronizedMineverseChatPlayer from a UUID.
	 * 
	 * @param uuid
	 *            {@link UUID}
	 * @return {@link SynchronizedMineverseChatPlayer}
	 */
	public static SynchronizedMineverseChatPlayer getSynchronizedMineverseChatPlayer(UUID uuid) {
		for (SynchronizedMineverseChatPlayer smcp : MineverseChatBungee.players) {
			try {
				if (smcp.getUUID().toString().equals(uuid.toString())) {
					return smcp;
				}
			} catch (Exception exception) {
				continue;
			}
		}
		return null;
	}
}
