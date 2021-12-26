package venture.Aust1n46.chat.controllers.commands;

import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

public class RangedSpy implements VentureCommand {
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
            return;
        }
        VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
        if (mcp.getPlayer().hasPermission("venturechat.rangedspy")) {
            if (!mcp.getRangedSpy()) {
                mcp.setRangedSpy(true);
                mcp.getPlayer().sendMessage(LocalizedMessage.RANGED_SPY_ON.toString());
                return;
            }
            mcp.setRangedSpy(false);
            mcp.getPlayer().sendMessage(LocalizedMessage.RANGED_SPY_OFF.toString());
            return;
        }
        mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
        return;
    }
}
