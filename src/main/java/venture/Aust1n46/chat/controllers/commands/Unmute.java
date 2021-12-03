package venture.Aust1n46.chat.controllers.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Unmute implements VentureCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission("venturechat.mute")) {
            if (args.length < 2) {
                sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/unmute")
                        .replace("{args}", "[channel] [player]"));
                return;
            }
            if (ChatChannel.isChannel(args[0])) {
                ChatChannel channel = ChatChannel.getChannel(args[0]);
                if (channel.getBungee()) {
                    sendBungeeCordUnmute(sender, args[1], channel);
                    return;
                }
                VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[1]);
                if (player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
                    sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[1]));
                    return;
                }
                if (!player.isMuted(channel.getName())) {
                    sender.sendMessage(LocalizedMessage.PLAYER_NOT_MUTED.toString()
                            .replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
                            .replace("{channel_name}", channel.getName()));
                    return;
                }
                player.removeMute(channel.getName());
                sender.sendMessage(LocalizedMessage.UNMUTE_PLAYER_SENDER.toString()
                        .replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
                        .replace("{channel_name}", channel.getName()));
                if (player.isOnline()) {
                    player.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString()
                            .replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
                            .replace("{channel_name}", channel.getName()));
                } else {
                    player.setModified(true);
                }
                return;
            }
            sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[0]));
            return;
        } else {
            sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
            return;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], ChatChannel.getChatChannels().stream().map(ChatChannel::getName).collect(Collectors.toList()), completions);
            Collections.sort(completions);
            return completions;
        }
        if (args.length == 2) {
            if (ChatChannel.isChannel(args[0])) {
                ChatChannel chatChannelObj = ChatChannel.getChannel(args[0]);
                if (chatChannelObj.getBungee()) {
                    StringUtil.copyPartialMatches(args[1], playerApiService.getNetworkPlayerNames(), completions);
                    Collections.sort(completions);
                    return completions;
                }
                StringUtil.copyPartialMatches(args[1], playerApiService.getOnlineMineverseChatPlayers().stream().filter(mcp -> mcp.isMuted(chatChannelObj.getName())).map(VentureChatPlayer::getName).collect(Collectors.toList()), completions);
                Collections.sort(completions);
                return completions;
            }
        }
        return Collections.emptyList();
    }

    private void sendBungeeCordUnmute(CommandSender sender, String playerToUnmute, ChatChannel channel) {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOutStream);
        try {
            out.writeUTF("Unmute");
            out.writeUTF("Send");
            if (sender instanceof Player) {
                out.writeUTF(((Player) sender).getUniqueId().toString());
            } else {
                out.writeUTF("VentureChat:Console");
            }
            out.writeUTF(playerToUnmute);
            out.writeUTF(channel.getName());
            pluginMessageController.sendPluginMessage(byteOutStream);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
