package venture.Aust1n46.chat;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class VentureChatPlaceholders extends PlaceholderExpansion {
	@Inject
	private VentureChat plugin;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null) {
            return null;
        }
        VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(p);
        if (mcp == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("something_else_you_think_of")) {
            return "value for that identifier *";
        }
        if (identifier.startsWith("channel_")) {
            ChatChannel currentChannel = mcp.isQuickChat() ? mcp.getQuickChannel() : mcp.getCurrentChannel();
            if (currentChannel == null) {
                return "";
            }
            switch (identifier) {
                case "channel_name":
                    return currentChannel.getName();
                case "channel_alias":
                    return currentChannel.getAlias();
                case "channel_color":
                    return currentChannel.getColor();
                case "channel_chatcolor":
                    return currentChannel.getChatColor();
                case "channel_is_bungee":
                    return currentChannel.getBungee() ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
                case "channel_cooldown":
                    return currentChannel.getCooldown() + "";
                case "channel_distance":
                    return currentChannel.getDistance() + "";
                case "channel_prefix":
                	return currentChannel.getPrefix();
            }
        }
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Aust1n46";
    }

    @Override
    public String getIdentifier() {
        return "venturechat";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
