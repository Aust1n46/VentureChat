package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Leave implements VentureCommand {
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
        if (args.length > 0) {
            ChatChannel channel = ChatChannel.getChannel(args[0]);
            if (channel == null) {
                mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString()
                        .replace("{args}", args[0]));
                return;
            }
            mcp.removeListening(channel.getName());
            mcp.getPlayer().sendMessage(LocalizedMessage.LEAVE_CHANNEL.toString()
                    .replace("{channel_color}", channel.getColor() + "")
                    .replace("{channel_name}", channel.getName()));
            boolean isThereABungeeChannel = channel.getBungee();
            if (mcp.getListening().size() == 0) {
                mcp.addListening(ChatChannel.getDefaultChannel().getName());
                mcp.setCurrentChannel(ChatChannel.getDefaultChannel());
                if (ChatChannel.getDefaultChannel().getBungee()) {
                    isThereABungeeChannel = true;
                }
                mcp.getPlayer().sendMessage(LocalizedMessage.MUST_LISTEN_ONE_CHANNEL.toString());
                mcp.getPlayer().sendMessage(LocalizedMessage.SET_CHANNEL.toString()
                        .replace("{channel_color}", ChatColor.valueOf(ChatChannel.getDefaultColor().toUpperCase()) + "")
                        .replace("{channel_name}", ChatChannel.getDefaultChannel().getName()));
            }
            if (isThereABungeeChannel) {
            	pluginMessageController.synchronize(mcp, true);
            }
            return;
        }
        mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
                .replace("{command}", "/leave")
                .replace("{args}", "[channel]"));
    }
}
