package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.ConfigService;

public class Chlist implements VentureCommand {
	@Inject
	private ConfigService configService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        sender.sendMessage(LocalizedMessage.CHANNEL_LIST_HEADER.toString());
        for (ChatChannel chname : configService.getChatChannels()) {
            if (chname.hasPermission()) {
                if (sender.hasPermission(chname.getPermission())) {
                    sender.sendMessage(LocalizedMessage.CHANNEL_LIST_WITH_PERMISSIONS.toString()
                            .replace("{channel_color}", (chname.getColor()).toString())
                            .replace("{channel_name}", chname.getName())
                            .replace("{channel_alias}", chname.getAlias()));
                }
            } else {
                sender.sendMessage(LocalizedMessage.CHANNEL_LIST.toString()
                        .replace("{channel_color}", chname.getColor().toString())
                        .replace("{channel_name}", chname.getName())
                        .replace("{channel_alias}", chname.getAlias()));
            }
        }
        return;
    }
}
