package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import com.google.inject.Inject;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatFormatService;
import venture.Aust1n46.chat.utilities.FormatUtils;

public class Broadcast implements VentureCommand {
	@Inject
    private VentureChat plugin;
	@Inject
	private VentureChatFormatService formatService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        ConfigurationSection bs = plugin.getConfig().getConfigurationSection("broadcast");
        String broadcastColor = bs.getString("color", "white");
        String broadcastPermissions = bs.getString("permissions", "None");
        String broadcastDisplayTag = FormatUtils.FormatStringAll(bs.getString("displaytag", "[Broadcast]"));
        if (broadcastPermissions.equalsIgnoreCase("None") || sender.hasPermission(broadcastPermissions)) {
            if (args.length > 0) {
                String bc = "";
                for (int x = 0; x < args.length; x++) {
                    if (args[x].length() > 0) bc += args[x] + " ";
                }
                bc = FormatUtils.FormatStringAll(bc);
                formatService.broadcastToServer(broadcastDisplayTag + ChatColor.valueOf(broadcastColor.toUpperCase()) + " " + bc);
                return;
            } else {
                sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
                        .replace("{command}", "/broadcast")
                        .replace("{args}", "[msg]"));
                return;
            }
        } else {
            sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
            return;
        }
    }
}
