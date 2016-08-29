package mineverse.Aust1n46.chat.api;

import java.util.UUID;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.bungee.MineverseChatBungee;

import org.bukkit.entity.Player;

//Beginning of the MineverseChat API, this class contains methods to obtain access to a MineverseChatPlayer using either
//a player pointer, a UUID, or a name in the form of a string.
public final class MineverseChatAPI {
	public static MineverseChatPlayer getMineverseChatPlayer(Player player) {
		for(MineverseChatPlayer mcp : MineverseChat.players) {
			if(mcp.getUUID().toString().equals(player.getUniqueId().toString())) {
				return mcp;
			}
		}
		return null;
	}
	
	public static MineverseChatPlayer getMineverseChatPlayer(UUID uuid) {
		for(MineverseChatPlayer mcp : MineverseChat.players) {
			if(mcp.getUUID().toString().equals(uuid.toString())) {
				return mcp;
			}
		}
		return null;
	}
	
	public static MineverseChatPlayer getMineverseChatPlayer(String name) {
		for(MineverseChatPlayer mcp : MineverseChat.players) {
			if(mcp.getName().equalsIgnoreCase(name)) {
				return mcp;
			}
		}
		return null;
	}
	
	public static MineverseChatPlayer getOnlineMineverseChatPlayer(Player player) {
		for(MineverseChatPlayer mcp : MineverseChat.onlinePlayers) {
			if(mcp.getUUID().toString().equals(player.getUniqueId().toString())) {
				return mcp;
			}
		}
		return null;
	}
	
	public static MineverseChatPlayer getOnlineMineverseChatPlayer(UUID uuid) {
		for(MineverseChatPlayer mcp : MineverseChat.onlinePlayers) {
			if(mcp.getUUID().toString().equals(uuid.toString())) {
				return mcp;
			}
		}
		return null;
	}
	
	public static MineverseChatPlayer getOnlineMineverseChatPlayer(String name) {
		for(MineverseChatPlayer mcp : MineverseChat.onlinePlayers) {
			if(mcp.getName().equalsIgnoreCase(name)) {
				return mcp;
			}
		}
		return null;
	}
	
	public static SynchronizedMineverseChatPlayer getSynchronizedMineverseChatPlayer(UUID uuid) {
		for(SynchronizedMineverseChatPlayer smcp : MineverseChatBungee.players) {
			if(smcp.getUUID().toString().equals(uuid.toString())) {
				return smcp;
			}
		}
		return null;
	}
}