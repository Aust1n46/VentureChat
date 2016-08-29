package mineverse.Aust1n46.chat.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

//This class formats the chat by replacing format place holders with their data.
public class FormatTags {

	public static String ChatFormat(String format, Player p, MineverseChat plugin, ChatChannelInfo cc, ChatChannel channel, boolean json) {
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		String town = "";
		String name = p.getName();
		String nickname = p.getDisplayName();
		String nation = "";
		String surname = "";
		String ttitle = "";
		String ftitle = "";
		String role = "";
		String faction = "";
		String ptown = "";
		String pnation = "";
		String prefix = "";
		String suffix = "";
		String group = "";
		String groupprefix = "";
		String groupsuffix = "";
		String mana = "";
		String herolevel = "";
		String heroxp = "";
		String heroclass = "";
		String party = "";
		String herosecondclass = "";
		String herosecondlevel = "";
		String herosecondxp = "";
		String heromaster = "";
		String herosecondmaster = "";
		String health = p.getHealthScale() + "";
		String world = p.getWorld().getName();
		String xp = p.getExpToLevel() + "";
		if(channel.getBungee()) {
			nickname = p.getDisplayName();
		}
		try {
			prefix = Format.FormatStringAll(MineverseChat.chat.getPlayerPrefix(p));
			suffix = Format.FormatStringAll(MineverseChat.chat.getPlayerSuffix(p));
			group = MineverseChat.chat.getPrimaryGroup(p);
			groupprefix = Format.FormatStringAll(MineverseChat.chat.getGroupPrefix(p.getWorld(), group));
			groupsuffix = Format.FormatStringAll(MineverseChat.chat.getGroupSuffix(p.getWorld(), group));
		}
		catch(Exception e) {
			if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
				System.out.println("[" + plugin.getConfig().getString("pluginname", "MineverseChat") + "] Prefix and / or suffix don't exist, setting to nothing");
			}
		}
		if(pluginManager.isPluginEnabled("Towny")) {
			try {
				Resident r = TownyUniverse.getDataSource().getResident(p.getName());
				if(r.hasTown()) {
					town = r.getTown().getName();
					ptown = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + r.getTown().getName() + ChatColor.WHITE + "]";
				}
				if(r.hasNation()) {
					nation = r.getTown().getNation().getName();
					pnation = ChatColor.WHITE + "[" + ChatColor.GOLD + r.getTown().getNation().getName() + ChatColor.WHITE + "]";
				}
				if(r.isMayor() || r.isKing()) {
					ttitle = r.getFormattedName().replace(" " + p.getName(), "");
				}
				else {
					ttitle = r.getTitle();
				}
				if(r.hasSurname()) {
					surname = r.getSurname();
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if(pluginManager.isPluginEnabled("Heroes")) {
			try {
				Heroes heroes = (Heroes) pluginManager.getPlugin("Heroes");
				CharacterManager manager = heroes.getCharacterManager();
				Hero h = manager.getHero(p);
				mana = h.getMana() + "";
				if(h.getHeroClass() != null) {
					heroxp = h.getExperience(h.getHeroClass()) + "";
					herolevel = h.getLevel(h.getHeroClass()) + "";
					heroclass = h.getHeroClass().getName();
					if(h.isMaster(h.getHeroClass())) {
						heromaster = "Master";
					}
				}
				if(h.getSecondClass() != null) {
					herosecondclass = h.getSecondClass().getName();
					herosecondlevel = h.getLevel(h.getSecondClass()) + "";
					herosecondxp = h.getExperience(h.getSecondClass()) + "";
					if(h.isMaster(h.getSecondClass())) {
						herosecondmaster = "Master";
					}
				}
				if(h.hasParty()) {
					party = h.getParty().toString();
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		/*if(pluginManager.isPluginEnabled("Factions")) {
			try {
				MPlayer mplayer = MPlayer.get(p);
				if(mplayer.hasFaction()) {
					role = mplayer.getRole().getPrefix();
				}
				faction = mplayer.getFaction().getName();
				if(mplayer.hasTitle()) {
					ftitle = mplayer.getTitle();
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}*/
		String end = Format.FormatStringAll(format.replace("{town}", town).replace("{nickname}", nickname).replace("{nation}", nation).replace("{name}", name).replace("{townytitle}", ttitle).replace("{suffix}", suffix).replace("{faction}", faction).replace("{factionstitle}", ftitle).replace("{servername}", p.getServer().getServerName()).replace("{pretown}", ptown).replace("{prenation}", pnation).replace("{group}", group).replace("{groupprefix}", groupprefix).replace("{groupsuffix}", groupsuffix).replace("{role}", role).replace("{world}", world).replace("{xp}", xp).replace("{mana}", mana).replace("{herolevel}", herolevel).replace("{heroclass}", heroclass).replace("{heroxp}", heroxp).replace("{health}", health).replace("{herosecondclass}", herosecondclass).replace("{party}", party).replace("{herosecondmaster}", herosecondmaster).replace("{heromaster}", heromaster).replace("{herosecondlevel}", herosecondlevel).replace("{herosecondxp}", herosecondxp).replace("{surname}", surname));
		if(!json) {
			end = end.replace("{prefix}", prefix);
		}
		else {
			end = end.replace("{prefix}", prefix);
		}
		if(pluginManager.isPluginEnabled("PlaceholderAPI")) {
			end = PlaceholderAPI.setBracketPlaceholders(p, Format.FormatStringAll(end));
		}
		return end;
	}

	public static String TabFormat(String format, Player p, MineverseChat plugin, ChatChannelInfo cc) {
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		String town = "";
		String name = p.getName();
		String displayname = p.getDisplayName();
		String nation = "";
		String surname = "";
		String ttitle = "";
		String ftitle = "";
		String role = "";
		String faction = "";
		String ptown = "";
		String pnation = "";
		String prefix = "";
		String suffix = "";
		String group = "";
		String groupprefix = "";
		String groupsuffix = "";
		String mana = "";
		String herolevel = "";
		String heroxp = "";
		String heroclass = "";
		String party = "";
		String herosecondclass = "";
		String herosecondlevel = "";
		String herosecondxp = "";
		String heromaster = "";
		String herosecondmaster = "";
		String health = p.getHealthScale() + "";
		String world = p.getWorld().getName();
		String xp = p.getExpToLevel() + "";
		try {
			prefix = Format.FormatStringAll(MineverseChat.chat.getPlayerPrefix(p));
			suffix = Format.FormatStringAll(MineverseChat.chat.getPlayerSuffix(p));
			group = MineverseChat.chat.getPrimaryGroup(p);
			groupprefix = Format.FormatStringAll(MineverseChat.chat.getGroupPrefix(p.getWorld(), group));
			groupsuffix = Format.FormatStringAll(MineverseChat.chat.getGroupSuffix(p.getWorld(), group));
		}
		catch(Exception e) {
			if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
				System.out.println("[" + plugin.getConfig().getString("pluginname", "MineverseChat") + "] Prefix and / or suffix don't exist, setting to nothing");
			}
		}
		if(pluginManager.isPluginEnabled("Towny")) {
			try {
				Resident r = TownyUniverse.getDataSource().getResident(p.getName());
				if(r.hasTown()) {
					town = r.getTown().getName();
					ptown = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + r.getTown().getName() + ChatColor.WHITE + "]";
				}
				if(r.hasNation()) {
					nation = r.getTown().getNation().getName();
					pnation = ChatColor.WHITE + "[" + ChatColor.GOLD + r.getTown().getNation().getName() + ChatColor.WHITE + "]";
				}
				if(r.isMayor() || r.isKing()) {
					ttitle = r.getFormattedName().replace(" " + p.getName(), "");
				}
				else {
					ttitle = r.getTitle();
				}
				if(r.hasSurname()) {
					surname = r.getSurname();
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if(pluginManager.isPluginEnabled("Heroes")) {
			try {
				Heroes heroes = (Heroes) pluginManager.getPlugin("Heroes");
				CharacterManager manager = heroes.getCharacterManager();
				Hero h = manager.getHero(p);
				mana = h.getMana() + "";
				if(h.getHeroClass() != null) {
					heroxp = h.getExperience(h.getHeroClass()) + "";
					herolevel = h.getLevel(h.getHeroClass()) + "";
					heroclass = h.getHeroClass().getName();
					if(h.isMaster(h.getHeroClass())) {
						heromaster = "Master";
					}
				}
				if(h.getSecondClass() != null) {
					herosecondclass = h.getSecondClass().getName();
					herosecondlevel = h.getLevel(h.getSecondClass()) + "";
					herosecondxp = h.getExperience(h.getSecondClass()) + "";
					if(h.isMaster(h.getSecondClass())) {
						herosecondmaster = "Master";
					}
				}
				if(h.hasParty()) {
					party = h.getParty().toString();
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		/*if(pluginManager.isPluginEnabled("Factions")) {
			try {
				MPlayer mplayer = MPlayer.get(p);
				if(mplayer.hasFaction()) {
					role = mplayer.getRole().getPrefix();
				}
				faction = mplayer.getFaction().getName();
				if(mplayer.hasTitle()) {
					ftitle = mplayer.getTitle();
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}*/
		return Format.FormatStringAll(format.replace("{town}", town).replace("{nation}", nation).replace("{name}", name).replace("{townytitle}", ttitle).replace("{prefix}", prefix).replace("{suffix}", suffix).replace("{faction}", faction).replace("{factionstitle}", ftitle).replace("{servername}", p.getServer().getServerName()).replace("{pretown}", ptown).replace("{prenation}", pnation).replace("{group}", group).replace("{groupprefix}", groupprefix).replace("{groupsuffix}", groupsuffix).replace("{role}", role).replace("{world}", world).replace("{xp}", xp).replace("{mana}", mana).replace("{herolevel}", herolevel).replace("{heroclass}", heroclass).replace("{heroxp}", heroxp).replace("{health}", health).replace("{herosecondclass}", herosecondclass).replace("{party}", party).replace("{herosecondmaster}", herosecondmaster).replace("{heromaster}", heromaster).replace("{herosecondlevel}", herosecondlevel).replace("{herosecondxp}", herosecondxp).replace("{surname}", surname).replace("{displayname}", displayname));
	}
}