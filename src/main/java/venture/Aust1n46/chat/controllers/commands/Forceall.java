package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Forceall implements VentureCommand {
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission("venturechat.forceall")) {
            if (args.length < 1) {
                sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
                        .replace("{command}", "/forceall").replace("{args}", "[message]"));
                return;
            }
            String forcemsg = "";
            for (int x = 0; x < args.length; x++) {
                if (args[x].length() > 0) {
                    forcemsg += args[x] + " ";
                }
            }
            sender.sendMessage(LocalizedMessage.FORCE_ALL.toString().replace("{message}", forcemsg));
            for (VentureChatPlayer player : playerApiService.getOnlineMineverseChatPlayers()) {
                player.getPlayer().chat(forcemsg);
            }
            return;
        }
        sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
    }
}
