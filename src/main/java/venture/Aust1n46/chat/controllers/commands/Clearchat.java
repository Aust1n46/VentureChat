package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.localization.InternalMessage;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Clearchat implements VentureCommand {
	@Inject
    private VentureChatPlayerApiService ventureChatApi;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission("venturechat.clearchat")) {
            for (VentureChatPlayer player : ventureChatApi.getOnlineMineverseChatPlayers()) {
                if (!player.getPlayer().hasPermission("venturechat.clearchat.bypass")) {
                    for (int a = 1; a <= 20; a++)
                        player.getPlayer().sendMessage(InternalMessage.EMPTY_STRING.toString());
                    player.getPlayer().sendMessage(LocalizedMessage.CLEAR_CHAT_SERVER.toString());
                }
            }
            sender.sendMessage(LocalizedMessage.CLEAR_CHAT_SENDER.toString());
            return;
        }
        sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
        return;
    }
}
