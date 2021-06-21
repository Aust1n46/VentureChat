package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.VentureCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Nick implements VentureCommand {
    private MineverseChat plugin = MineverseChat.getInstance();

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission("venturechat.nick")) {
            if (args.length > 0) {
                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
                        return;
                    }
                    MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
                    if (args[0].equalsIgnoreCase("Off")) {
                        mcp.getPlayer().setDisplayName(mcp.getName());
                        mcp.setNickname(mcp.getName());
                        mcp.getPlayer().sendMessage(ChatColor.GOLD + "You no longer have a nickname.");
                        String name = mcp.getName();
                        if (name.length() >= 16) {
                            name = name.substring(0, 16);
                        }
                        if (plugin.getConfig().getBoolean("nickname-in-tablist", false)) {
                            mcp.getPlayer().setPlayerListName(Format.FormatStringAll(name));
                        }
                        return;
                    }
                    if (!mcp.getPlayer().hasPermission("venturechat.nick.bypass")) {
                        for (String s : plugin.getConfig().getStringList("nicknames")) {
                            if (s.equalsIgnoreCase(args[0])) {
                                mcp.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to set your nickname to " + args[0]);
                                return;
                            }
                        }
                    }
                    mcp.getPlayer().setDisplayName(Format.FormatStringAll(args[0]));
                    mcp.setNickname(args[0]);
                    mcp.getPlayer().sendMessage(ChatColor.GOLD + "Set your nickname to " + Format.FormatStringAll(args[0]));
                    if (args[0].length() >= 16) {
                        args[0] = args[0].substring(0, 16);
                    }
                    if (plugin.getConfig().getBoolean("nickname-in-tablist", false)) {
                        mcp.getPlayer().setPlayerListName(Format.FormatStringAll(args[0]));
                    }
                    return;
                }
                if (sender.hasPermission("venturechat.nick.others")) {
                    MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
                        return;
                    }
                    if (args[1].equalsIgnoreCase("Off")) {
                        player.setNickname(player.getName());
                        sender.sendMessage(ChatColor.GOLD + "Removed " + ChatColor.RED + player.getName() + ChatColor.GOLD + "'s nickname.");
                        if (player.isOnline()) {
                            player.getPlayer().sendMessage(ChatColor.GOLD + "You no longer have a nickname.");
                            player.getPlayer().setDisplayName(player.getName());
                            String playerName = player.getName();
                            if (playerName.length() >= 16) {
                                playerName = playerName.substring(0, 16);
                            }
                            if (plugin.getConfig().getBoolean("nickname-in-tablist", false)) {
                                player.getPlayer().setPlayerListName(Format.FormatStringAll(playerName));
                            }
                        }
                        return;
                    }
                    if (!sender.hasPermission("venturechat.nick.bypass")) {
                        for (String s : plugin.getConfig().getStringList("nicknames")) {
                            if (s.equalsIgnoreCase(args[1])) {
                                sender.sendMessage(ChatColor.RED + "You are not allowed to set " + player.getName() + "'s nickname to " + args[1]);
                                return;
                            }
                        }
                    }
                    player.setNickname(args[1]);
                    sender.sendMessage(ChatColor.GOLD + "Set " + ChatColor.RED + player.getName() + ChatColor.GOLD + "'s nickname to " + Format.FormatStringAll(args[1]));
                    if (player.isOnline()) {
                        player.getPlayer().setDisplayName(Format.FormatStringAll(args[1]));
                        player.getPlayer().sendMessage(ChatColor.GOLD + "Your nickname has been to set to " + Format.FormatStringAll(args[1]));
                        if (args[1].length() >= 16) {
                            args[1] = args[1].substring(0, 16);
                        }
                        if (plugin.getConfig().getBoolean("nickname-in-tablist", false)) {
                            player.getPlayer().setPlayerListName(Format.FormatStringAll(args[1]));
                        }
                    }
                    return;
                }
                sender.sendMessage(ChatColor.RED + "You do not have permission to set other players nicknames.");
                return;
            }
            sender.sendMessage(ChatColor.RED + "Invalid command: /nick {player} [nickname]");
            return;
        }
        sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
    }
}
