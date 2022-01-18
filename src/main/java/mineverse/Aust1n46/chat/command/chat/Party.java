package mineverse.Aust1n46.chat.command.chat;

import static mineverse.Aust1n46.chat.MineverseChat.LINE_LENGTH;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.utilities.Format;

public class Party extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();

	public Party() {
		super("party");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return true;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if (!mcp.getPlayer().hasPermission("venturechat.party")) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}
		try {
			switch (args[0]) {
			case "host": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.host")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				if (mcp.isHost()) {
					mcp.setHost(false);
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer hosting a party.");
					for (MineverseChatPlayer player : MineverseChatAPI.getMineverseChatPlayers()) {
						if (player.hasParty() && player.getParty().equals(mcp.getParty())) {
							player.setParty(null);
							if (player.isOnline()) {
								player.getPlayer().sendMessage(ChatColor.RED + mcp.getName() + " is no longer hosting a party.");
							} else {
								player.setModified(true);
							}
						}
					}
					mcp.setParty(null);
					break;
				}
				mcp.setHost(true);
				mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are now hosting a party.");
				mcp.setParty(mcp.getUUID());
				break;
			}
			case "join": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.join")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				if (args.length > 1) {
					MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[1]);
					if (player != null) {
						if (player.isHost()) {
							if (!mcp.hasParty()) {
								/*
								 * if(plugin.getMetadata(player, "MineverseChat.party.ban." + tp.getName(),
								 * plugin)) { player.sendMessage(ChatColor.RED + "You are banned from " +
								 * tp.getName() + "'s party."); break; }
								 */
								mcp.getPlayer().sendMessage(ChatColor.GREEN + "Joined " + player.getName() + "'s party.");
								mcp.setParty(player.getUUID());
								player.getPlayer().sendMessage(ChatColor.GREEN + mcp.getName() + " joined your party.");
								break;
							}
							mcp.getPlayer().sendMessage(ChatColor.RED + "You are already in " + MineverseChatAPI.getMineverseChatPlayer(mcp.getParty()).getName() + "'s party.");
							break;
						}
						mcp.getPlayer().sendMessage(ChatColor.RED + player.getName() + " is not hosting a party.");
						break;
					}
					mcp.getPlayer().sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not online.");
					break;
				}
				mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /party join [player]");
				break;
			}
			case "leave": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.leave")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				if (mcp.hasParty()) {
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "Leaving " + MineverseChatAPI.getMineverseChatPlayer(mcp.getParty()).getName() + "'s party.");
					mcp.setParty(null);
					if (mcp.isHost()) {
						for (MineverseChatPlayer player : MineverseChatAPI.getMineverseChatPlayers()) {
							if (player.hasParty() && player.getParty().equals(mcp.getUUID()) && !player.getName().equals(mcp.getName())) {
								player.setParty(null);
								if (player.isOnline()) {
									player.getPlayer().sendMessage(ChatColor.RED + mcp.getName() + " is no longer hosting a party.");
								} else {
									player.setModified(true);
								}
							}
						}
					}
					mcp.setHost(false);
					break;
				}
				mcp.getPlayer().sendMessage(ChatColor.RED + "You are not in a party.");
				break;
			}
			case "kick": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.kick")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				if (mcp.isHost()) {
					if (args.length > 1) {
						MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[1]);
						if (player != null) {
							if (!player.getName().equals(mcp.getName())) {
								if (player.hasParty() && player.getParty().equals(mcp.getUUID())) {
									player.setParty(null);
									player.getPlayer().sendMessage(ChatColor.RED + "You have been kicked out of " + mcp.getName() + "'s party.");
									mcp.getPlayer().sendMessage(ChatColor.RED + "You have kicked " + player.getName() + " out of your party.");
									break;
								}
								mcp.getPlayer().sendMessage(ChatColor.RED + "Player " + player.getName() + " is not in your party.");
								break;
							}
							mcp.getPlayer().sendMessage(ChatColor.RED + "You cannot kick yourself.");
							break;
						}
						mcp.getPlayer().sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not online.");
						break;
					}
					mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /party kick [playername]");
					break;
				}
				mcp.getPlayer().sendMessage(ChatColor.RED + "You are not hosting a party.");
				break;
			}
			/*
			 * case "ban": { if(mcp.isHost()) { if(args.length > 1) { Player tp =
			 * Bukkit.getPlayer(args[1]); if(tp != null) {
			 * if(!tp.getName().equals(player.getName())) {
			 * tp.setMetadata("MineverseChat.party.ban." + player.getUniqueId().toString(),
			 * new FixedMetadataValue(plugin, true)); if(plugin.getMetadataString(tp,
			 * "MineverseChat.party", plugin).equals(plugin.getMetadataString(player,
			 * "MineverseChat.party", plugin))) { tp.setMetadata("MineverseChat.party", new
			 * FixedMetadataValue(plugin, "")); } tp.sendMessage(ChatColor.RED +
			 * "You have been banned from " + player.getName() + "'s party.");
			 * player.sendMessage(ChatColor.RED + "You have banned " + tp.getName() +
			 * " from your party."); break; } player.sendMessage(ChatColor.RED +
			 * "You cannot ban yourself."); break; } player.sendMessage(ChatColor.RED +
			 * "Player: " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not online.");
			 * break; } player.sendMessage(ChatColor.RED +
			 * "Invalid command: /party ban [playername]"); break; }
			 * player.sendMessage(ChatColor.RED + "You are not hosting a party."); break; }
			 * case "unban": { if(plugin.getMetadata(player, "MineverseChat.party.host",
			 * plugin)) { if(args.length > 1) { Player tp = Bukkit.getPlayer(args[1]); if(tp
			 * != null) { if(!tp.getName().equals(player.getName())) {
			 * tp.setMetadata("MineverseChat.party.ban." + player.getUniqueId(), new
			 * FixedMetadataValue(plugin, false)); tp.sendMessage(ChatColor.RED +
			 * "You have been unbanned from " + player.getName() + "'s party.");
			 * player.sendMessage(ChatColor.RED + "You have unbanned " + tp.getName() +
			 * " from your party."); break; } player.sendMessage(ChatColor.RED +
			 * "You cannot unban yourself."); break; } player.sendMessage(ChatColor.RED +
			 * "Player: " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not online.");
			 * break; } player.sendMessage(ChatColor.RED +
			 * "Invalid command: /party unban [playername]"); break; }
			 * player.sendMessage(ChatColor.RED + "You are not hosting a party."); break; }
			 */
			case "info": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.info")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				if (mcp.hasParty() && !mcp.isHost()) {
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are in " + MineverseChatAPI.getMineverseChatPlayer(mcp.getParty()).getName() + "'s party.");
				} else if (mcp.isHost()) {
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are hosting a party.");
				} else {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You are not hosting a party and you are not in a party.");
				}
				if (mcp.isPartyChat()) {
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "Party chat on.");
					break;
				}
				mcp.getPlayer().sendMessage(ChatColor.GREEN + "Party chat off.");
				break;
			}
			case "chat": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.chat")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				if (mcp.isPartyChat()) {
					mcp.setPartyChat(false);
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "Toggled party chat off.");
					break;
				}
				if (mcp.hasConversation()) {
					String tellChat = MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName();
					mcp.setConversation(null);
					for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
						if (p.isSpy()) {
							p.getPlayer().sendMessage(mcp.getName() + " is no longer in a private conversation with " + tellChat + ".");
						}
					}
					mcp.getPlayer().sendMessage("You are no longer in private conversation with " + tellChat);
				}
				mcp.setPartyChat(true);
				mcp.getPlayer().sendMessage(ChatColor.GREEN + "Toggled party chat on.");
				break;
			}
			case "help": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.help")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				mcp.getPlayer().sendMessage(ChatColor.GREEN
						+ "/party host\n/party join [player]\n/party leave\n/party kick [player]\n/party ban [player]\n/party unban [player]\n/party info\n/party members [player]\n/party chat\n/party help");
				break;
			}
			case "members": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.members")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return true;
				}
				if (args.length > 1) {
					MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[1]);
					if (player != null) {
						if (player.isHost()) {
							String members = "";
							long linecount = LINE_LENGTH;
							for (MineverseChatPlayer p : MineverseChatAPI.getMineverseChatPlayers()) {
								if (p.getParty() != null && p.getParty().equals(player.getUUID())) {
									if (members.length() + p.getName().length() > linecount) {
										members += "\n";
										linecount = linecount + LINE_LENGTH;
									}
									if (p.isOnline()) {
										members += ChatColor.GREEN + p.getName() + ChatColor.WHITE + ", ";
									} else {
										members += ChatColor.RED + p.getName() + ChatColor.WHITE + ", ";
									}
								}
							}
							if (members.length() > 2) {
								members = members.substring(0, members.length() - 2);
							}
							mcp.getPlayer().sendMessage(ChatColor.GREEN + "Members in " + player.getName() + "'s party: " + members);
							break;
						}
						mcp.getPlayer().sendMessage(ChatColor.RED + "Player " + player.getName() + " is not hosting a party.");
						break;
					}
					mcp.getPlayer().sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not online.");
					break;
				}
				mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid command: /party members [player]");
				break;
			}
			}
			if (args[0].length() > 0) {
				if (!args[0].equals("host") && !args[0].equals("join") && !args[0].equals("leave") && !args[0].equals("kick") && !args[0].equals("info") && !args[0].equals("chat")
						&& !args[0].equals("help") && !args[0].equals("members") && !args[0].equals("ban") && !args[0].equals("unban")) {
					if (mcp.hasParty()) {
						String msg = "";
						String partyformat = "";
						for (int x = 0; x < args.length; x++) {
							if (args[x].length() > 0)
								msg += " " + args[x];
						}
						if (mcp.hasFilter()) {
							msg = Format.FilterChat(msg);
						}
						if (mcp.getPlayer().hasPermission("venturechat.color.legacy")) {
							msg = Format.FormatStringLegacyColor(msg);
						}
						if (mcp.getPlayer().hasPermission("venturechat.color")) {
							msg = Format.FormatStringColor(msg);
						}
						if (mcp.getPlayer().hasPermission("venturechat.format")) {
							msg = Format.FormatString(msg);
						}
						if (plugin.getConfig().getString("partyformat").equalsIgnoreCase("Default")) {
							partyformat = ChatColor.GREEN + "[" + MineverseChatAPI.getMineverseChatPlayer(mcp.getParty()).getName() + "'s Party] " + mcp.getName() + ":" + msg;
						} else {
							partyformat = Format.FormatStringAll(plugin.getConfig().getString("partyformat")
									.replace("{host}", MineverseChatAPI.getMineverseChatPlayer(mcp.getParty()).getName()).replace("{player}", mcp.getName())) + msg;
						}
						for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
							if ((p.getParty().equals(mcp.getParty()) || p.isSpy())) {
								p.getPlayer().sendMessage(partyformat);
							}
						}
						return true;
					}
					mcp.getPlayer().sendMessage(ChatColor.RED + "You are not in a party.");
				}
			}
		} catch (Exception e) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid arguments, /party help");
		}
		return true;
	}
}
