package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.FormatUtils;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;
import venture.Aust1n46.chat.service.VentureChatFormatService;

public class Me implements VentureCommand {
	@Inject
	private VentureChatFormatService formatService;
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission("venturechat.me")) {
            if (args.length > 0) {
                String msg = "";
                for (int x = 0; x < args.length; x++)
                    if (args[x].length() > 0)
                        msg += " " + args[x];
                if (sender instanceof Player && playerApiService.getOnlineMineverseChatPlayer((Player) sender).isFilter()) {
                    msg = formatService.FilterChat(msg);
                }
                if (sender.hasPermission("venturechat.color.legacy")) {
                    msg = FormatUtils.FormatStringLegacyColor(msg);
                }
                if (sender.hasPermission("venturechat.color"))
                    msg = FormatUtils.FormatStringColor(msg);
                if (sender.hasPermission("venturechat.format"))
                    msg = FormatUtils.FormatString(msg);
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    formatService.broadcastToServer("* " + p.getDisplayName() + msg);
                    return;
                }
                formatService.broadcastToServer("* " + sender.getName() + msg);
                return;
            }
            sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
                    .replace("{command}", "/me")
                    .replace("{args}", "[message]"));
            return;
        }
        sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
    }
}
