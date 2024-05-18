package venture.Aust1n46.chat.controllers.commands;

import static venture.Aust1n46.chat.utilities.FormatUtils.LINE_LENGTH;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.FormatService;
import venture.Aust1n46.chat.service.PlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

public class Party extends PlayerCommand {
	@Inject
	private VentureChat plugin;
	@Inject
	private FormatService formatService;
	@Inject
	private PlayerApiService playerApiService;

	@Inject
	public Party(String name) {
		super(name);
	}

	@Override
	public void executeCommand(Player sender, String command, String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(sender);
		if (!mcp.getPlayer().hasPermission("venturechat.party")) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return;
		}
		try {
			switch (args[0]) {
			case "host": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.host")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return;
				}
				if (mcp.isHost()) {
					mcp.setHost(false);
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer hosting a party.");
					for (VentureChatPlayer player : playerApiService.getMineverseChatPlayers()) {
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
				mcp.setParty(mcp.getUuid());
				break;
			}
			case "join": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.join")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return;
				}
				if (args.length > 1) {
					VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[1]);
					if (player != null) {
						if (player.isHost()) {
							if (!mcp.hasParty()) {
								mcp.getPlayer().sendMessage(ChatColor.GREEN + "Joined " + player.getName() + "'s party.");
								mcp.setParty(player.getUuid());
								player.getPlayer().sendMessage(ChatColor.GREEN + mcp.getName() + " joined your party.");
								break;
							}
							mcp.getPlayer().sendMessage(ChatColor.RED + "You are already in " + playerApiService.getMineverseChatPlayer(mcp.getParty()).getName() + "'s party.");
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
					return;
				}
				if (mcp.hasParty()) {
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "Leaving " + playerApiService.getMineverseChatPlayer(mcp.getParty()).getName() + "'s party.");
					mcp.setParty(null);
					if (mcp.isHost()) {
						for (VentureChatPlayer player : playerApiService.getMineverseChatPlayers()) {
							if (player.hasParty() && player.getParty().equals(mcp.getUuid()) && !player.getName().equals(mcp.getName())) {
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
					return;
				}
				if (mcp.isHost()) {
					if (args.length > 1) {
						VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[1]);
						if (player != null) {
							if (!player.getName().equals(mcp.getName())) {
								if (player.hasParty() && player.getParty().equals(mcp.getUuid())) {
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
			case "info": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.info")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return;
				}
				if (mcp.hasParty() && !mcp.isHost()) {
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are in " + playerApiService.getMineverseChatPlayer(mcp.getParty()).getName() + "'s party.");
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
					return;
				}
				if (mcp.isPartyChat()) {
					mcp.setPartyChat(false);
					mcp.getPlayer().sendMessage(ChatColor.GREEN + "Toggled party chat off.");
					break;
				}
				if (mcp.hasConversation()) {
					String tellChat = playerApiService.getMineverseChatPlayer(mcp.getConversation()).getName();
					mcp.setConversation(null);
					for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
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
					return;
				}
				mcp.getPlayer().sendMessage(ChatColor.GREEN
						+ "/party host\n/party join [player]\n/party leave\n/party kick [player]\n/party ban [player]\n/party unban [player]\n/party info\n/party members [player]\n/party chat\n/party help");
				break;
			}
			case "members": {
				if (!mcp.getPlayer().hasPermission("venturechat.party.members")) {
					mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command!");
					return;
				}
				if (args.length > 1) {
					VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[1]);
					if (player != null) {
						if (player.isHost()) {
							String members = "";
							long linecount = LINE_LENGTH;
							for (VentureChatPlayer p : playerApiService.getMineverseChatPlayers()) {
								if (p.getParty() != null && p.getParty().equals(player.getUuid())) {
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
						if (mcp.isFilter()) {
							msg = formatService.filterChat(msg);
						}
						if (mcp.getPlayer().hasPermission("venturechat.color.legacy")) {
							msg = FormatUtils.FormatStringLegacyColor(msg);
						}
						if (mcp.getPlayer().hasPermission("venturechat.color")) {
							msg = FormatUtils.FormatStringColor(msg);
						}
						if (mcp.getPlayer().hasPermission("venturechat.format")) {
							msg = FormatUtils.FormatString(msg);
						}
						if (plugin.getConfig().getString("partyformat").equalsIgnoreCase("Default")) {
							partyformat = ChatColor.GREEN + "[" + playerApiService.getMineverseChatPlayer(mcp.getParty()).getName() + "'s Party] " + mcp.getName() + ":" + msg;
						} else {
							partyformat = FormatUtils.FormatStringAll(plugin.getConfig().getString("partyformat")
									.replace("{host}", playerApiService.getMineverseChatPlayer(mcp.getParty()).getName()).replace("{player}", mcp.getName())) + msg;
						}
						for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
							if ((p.getParty().equals(mcp.getParty()) || p.isSpy())) {
								p.getPlayer().sendMessage(partyformat);
							}
						}
						return;
					}
					mcp.getPlayer().sendMessage(ChatColor.RED + "You are not in a party.");
				}
			}
		} catch (Exception e) {
			mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid arguments, /party help");
		}
		return;
	}
}
