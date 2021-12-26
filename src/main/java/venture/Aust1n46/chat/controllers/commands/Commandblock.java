package venture.Aust1n46.chat.controllers.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Commandblock implements VentureCommand {
	@Inject
    private VentureChat plugin;
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission("venturechat.commandblock")) {
            if (args.length > 1) {
                VentureChatPlayer player = playerApiService.getOnlineMineverseChatPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
                            .replace("{args}", args[0]));
                    return;
                }
                boolean match = false;
                for (String cb : (List<String>) plugin.getConfig().getStringList("blockablecommands"))
                    if (args[1].equals("/" + cb))
                        match = true;
                if (match || player.isBlockedCommand(args[1])) {
                    if (!player.isBlockedCommand(args[1])) {
                        player.addBlockedCommand(args[1]);
                        player.getPlayer().sendMessage(LocalizedMessage.BLOCK_COMMAND_PLAYER.toString()
                                .replace("{command}", args[1]));
                        sender.sendMessage(LocalizedMessage.BLOCK_COMMAND_SENDER.toString()
                                .replace("{player}", player.getName())
                                .replace("{command}", args[1]));
                        return;
                    }
                    player.removeBlockedCommand(args[1]);
                    player.getPlayer().sendMessage(LocalizedMessage.UNBLOCK_COMMAND_PLAYER.toString()
                            .replace("{command}", args[1]));
                    sender.sendMessage(LocalizedMessage.UNBLOCK_COMMAND_SENDER.toString()
                            .replace("{player}", player.getName())
                            .replace("{command}", args[1]));
                    return;
                }
                sender.sendMessage(LocalizedMessage.COMMAND_NOT_BLOCKABLE.toString());
                return;
            }
            sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
                    .replace("{command}", "/commandblock")
                    .replace("{args}", "[player] [command]"));
            return;
        }
        sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
    }
}
