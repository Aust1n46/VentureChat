package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class BungeeToggle implements VentureCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
            return;
        }
        VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
        if (mcp.getPlayer().hasPermission("venturechat.bungeetoggle")) {
            if (!mcp.isBungeeToggle()) {
                mcp.setBungeeToggle(true);
                mcp.getPlayer().sendMessage(LocalizedMessage.BUNGEE_TOGGLE_ON.toString());
                pluginMessageController.synchronize(mcp, true);
                return;
            }
            mcp.setBungeeToggle(false);
            mcp.getPlayer().sendMessage(LocalizedMessage.BUNGEE_TOGGLE_OFF.toString());
            pluginMessageController.synchronize(mcp, true);
            return;
        }
        mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
        return;
    }
}
