package mineverse.Aust1n46.chat;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.PlaceholderHook;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;

public class VentureChatPlaceholders extends PlaceholderHook {
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if(p == null) {
			return null;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(p);
		if(mcp == null) {
			return "";
		}
		if(identifier.equalsIgnoreCase("nickname")) {
			return mcp.hasNickname() ? mcp.getNickname() : mcp.getName();
		}
		if(identifier.equalsIgnoreCase("something_else_you_think_of")) {
			return "value for that identifier *";
		}
		if(identifier.startsWith("channel_")) {
			if(mcp.getCurrentChannel() == null) {
				return "";
			}
			switch(identifier) {
			case "channel_name":
				return mcp.getCurrentChannel().getName();
			case "channel_alias":
				return mcp.getCurrentChannel().getAlias();
			case "channel_color":
				return this.textToHex(mcp.getCurrentChannel().getColor());
			case "channel_chatcolor":
				return this.textToHex(mcp.getCurrentChannel().getChatColor());
			case "channel_is_bungee":
				return mcp.getCurrentChannel().getBungee() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
			case "channel_cooldown":
				return mcp.getCurrentChannel().getCooldown() + "";
			case "channel_distance":
				return mcp.getCurrentChannel().getDistance() + "";
			}
		}
		return null;
	}
	
	private String textToHex(String color) {
		if(color.equalsIgnoreCase("black")) return "0";
		if(color.equalsIgnoreCase("dark_blue")) return "1";
		if(color.equalsIgnoreCase("dark_green")) return "2";
		if(color.equalsIgnoreCase("dark_aqua")) return "3";
		if(color.equalsIgnoreCase("dark_red")) return "4";
		if(color.equalsIgnoreCase("dark_purple")) return "5";
		if(color.equalsIgnoreCase("gold")) return "6";
		if(color.equalsIgnoreCase("gray")) return "7";
		if(color.equalsIgnoreCase("dark_gray")) return "8";
		if(color.equalsIgnoreCase("blue")) return "9";
		if(color.equalsIgnoreCase("green")) return "a";
		if(color.equalsIgnoreCase("aqua")) return "b";
		if(color.equalsIgnoreCase("red")) return "c";
		if(color.equalsIgnoreCase("light_purple")) return "d";
		if(color.equalsIgnoreCase("yellow")) return "e";
		if(color.equalsIgnoreCase("white")) return "f";
		return "f";
	}
}