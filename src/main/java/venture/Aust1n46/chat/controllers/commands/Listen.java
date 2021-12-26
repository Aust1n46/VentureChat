package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Listen implements VentureCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
            return;
        }
        VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
        if (args.length > 0) {
            ChatChannel channel = configService.getChannel(args[0]);
            if (channel == null) {
                mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString()
                        .replace("{args}", args[0]));
                return;
            }
            if (channel.hasPermission()) {
                if (!mcp.getPlayer().hasPermission(channel.getPermission())) {
                    mcp.removeListening(channel.getName());
                    mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_NO_PERMISSION.toString());
                    return;
                }
            }
            mcp.addListening(channel.getName());
            mcp.getPlayer().sendMessage(LocalizedMessage.LISTEN_CHANNEL.toString()
                    .replace("{channel_color}", channel.getColor() + "")
                    .replace("{channel_name}", channel.getName()));
            if (channel.getBungee()) {
                pluginMessageController.synchronize(mcp, true);
            }
            return;
        }
        mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
                .replace("{command}", "/listen")
                .replace("{args}", "[channel]"));
    }
}
