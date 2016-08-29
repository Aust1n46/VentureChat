package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.entity.MPlayer;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;

@SuppressWarnings("unused")
public class Chwho extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Chwho(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		String playerlist = "";
		if(sender.hasPermission("venturechat.chwho")) {
			if(args.length > 0) {
				ChatChannel channel = cc.getChannelInfo(args[0]);
				if(channel != null) {
					if(channel.hasPermission()) {
						if(!sender.hasPermission(channel.getPermission())) {
							MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(((Player) sender));
							mcp.removeListening(channel.getName());
							mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to look at this channel.");
							return;
						}
					}
					PluginManager pluginManager = plugin.getServer().getPluginManager();
					long linecount = plugin.getLineLength();
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						if(p.getListening().contains(channel.getName())) {
							if(sender instanceof Player) {
								if(!((Player) sender).canSee(p.getPlayer())) {
									continue;
								}
							}
							if(channel.hasDistance() && sender instanceof Player) {
								if(!this.isPlayerWithinDistance((Player) sender, p.getPlayer(), channel.getDistance())) {
									continue;
								}
							}
							if(pluginManager.isPluginEnabled("Towny") && sender instanceof Player) {
								try {
									Resident r = TownyUniverse.getDataSource().getResident(p.getName());
									Resident pp = TownyUniverse.getDataSource().getResident(((Player) sender).getName());
									if(channel.getName().equalsIgnoreCase("Town")) {
										if(!pp.hasTown()) {
											if(playerlist.length() + p.getName().length() > linecount) {
												playerlist += "\n";
												linecount = linecount + plugin.getLineLength();
											}
											if(!p.isMuted(channel.getName())) {
												playerlist += ChatColor.WHITE + p.getName();
											}
											else {
												playerlist += ChatColor.RED + p.getName();
											}
											playerlist += ChatColor.WHITE + ", ";
											break;
										}
										else if(!r.hasTown()) {
											continue;
										}
										else if(!(r.getTown().getName().equals(pp.getTown().getName()))) {
											continue;
										}
									}
									if(channel.getName().equalsIgnoreCase("Nation")) {
										if(!pp.hasNation()) {
											if(playerlist.length() + p.getName().length() > linecount) {
												playerlist += "\n";
												linecount = linecount + plugin.getLineLength();
											}
											if(!p.isMuted(channel.getName())) {
												playerlist += ChatColor.WHITE + p.getName();
											}
											else {
												playerlist += ChatColor.RED + p.getName();
											}
											playerlist += ChatColor.WHITE + ", ";
											break;
										}
										else if(!r.hasNation()) {
											continue;
										}
										else if(!(r.getTown().getNation().getName().equals(pp.getTown().getNation().getName()))) {
											continue;
										}
									}
								}
								catch(Exception ex) {
									ex.printStackTrace();
								}
							}
							if(pluginManager.isPluginEnabled("Factions") && sender instanceof Player) {
								try {
									MPlayer mplayer = MPlayer.get(p.getPlayer());
									MPlayer mplayerp = MPlayer.get((Player) sender);
									if(channel.getName().equalsIgnoreCase("Faction")) {
										if(!mplayerp.hasFaction()) {
											if(playerlist.length() + p.getName().length() > linecount) {
												playerlist += "\n";
												linecount = linecount + plugin.getLineLength();
											}
											if(!p.isMuted(channel.getName())) {
												playerlist += ChatColor.WHITE + p.getName();
											}
											else {
												playerlist += ChatColor.RED + p.getName();
											}
											playerlist += ChatColor.WHITE + ", ";
											break;
										}
										else if(!mplayerp.hasFaction()) {
											continue;
										}
										else if(!(mplayer.getFactionName().equals(mplayerp.getFactionName()))) {
											continue;
										}
									}
								}
								catch(Exception ex) {
									ex.printStackTrace();
								}
							}
							if(playerlist.length() + p.getName().length() > linecount) {
								playerlist += "\n";
								linecount = linecount + plugin.getLineLength();
							}
							if(!p.isMuted(channel.getName())) {
								playerlist += ChatColor.WHITE + p.getName();
							}
							else {
								playerlist += ChatColor.RED + p.getName();
							}
							playerlist += ChatColor.WHITE + ", ";
						}
					}
					if(playerlist.length() > 2) {
						playerlist = playerlist.substring(0, playerlist.length() - 2);
					}
					sender.sendMessage(ChatColor.GOLD + "Players in Channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
					sender.sendMessage(playerlist);
					return;
				}
				else {
					sender.sendMessage(ChatColor.RED + "Invalid channel: " + args[0]);
					return;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Invalid command: /chwho [channel]");
				return;
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return;
		}
	}

	private boolean isPlayerWithinDistance(Player p1, Player p2, double Distance) {
		Double chDistance = Distance;
		Location locreceip;
		Location locsender = p1.getLocation();
		Location diff;
		if(chDistance > (double) 0) {
			locreceip = p2.getLocation();
			if(locreceip.getWorld() == p1.getWorld()) {
				diff = locreceip.subtract(locsender);
				if(Math.abs(diff.getX()) > chDistance || Math.abs(diff.getZ()) > chDistance) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		return true;
	}
}