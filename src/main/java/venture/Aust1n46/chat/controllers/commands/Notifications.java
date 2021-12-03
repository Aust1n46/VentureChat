package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Notifications implements VentureCommand {
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
            return;
        }

        VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
        if (!mcp.hasNotifications()) {
            mcp.setNotifications(true);
            mcp.getPlayer().sendMessage(LocalizedMessage.NOTIFICATIONS_ON.toString());
            return;
        }
        mcp.setNotifications(false);
        mcp.getPlayer().sendMessage(LocalizedMessage.NOTIFICATIONS_OFF.toString());
        return;
    }
}
