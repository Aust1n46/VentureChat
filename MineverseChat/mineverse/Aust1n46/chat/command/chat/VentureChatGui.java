package mineverse.Aust1n46.chat.command.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.gui.GuiSlot;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.UUIDFetcher;

public class VentureChatGui extends MineverseCommand {
	private MineverseChat plugin;

	public VentureChatGui(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return;
		}
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Invalid command: /venturechatgui [player] [channel] [hash]");
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(mcp.getPlayer().hasPermission("venturechat.gui")) {
			MineverseChatPlayer target = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(target == null) {
				//mcp.getPlayer().sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				UUID uuid = null;
				try {
					uuid = UUIDFetcher.getUUIDOf(args[0]);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				String name = args[0];
				ChatChannel current = MineverseChat.ccInfo.getDefaultChannel();
				Set<UUID> ignores = new HashSet<UUID>();
				Set<String> listening = new HashSet<String>();
				listening.add(current.getName());
				HashMap<String, Integer> mutes = new HashMap<String, Integer>();
				Set<String> blockedCommands = new HashSet<String>();
				List<String> mail = new ArrayList<String>();
				String jsonFormat = "Default";
				target = new MineverseChatPlayer(uuid, name, current, ignores, listening, mutes, blockedCommands, mail, false, null, true, true, name, jsonFormat, false, false, false, true, true);
				MineverseChat.players.add(target);
			}
			if(MineverseChat.ccInfo.isChannel(args[1])) {
				ChatChannel channel = MineverseChat.ccInfo.getChannelInfo(args[1]);
				int hash = Integer.parseInt(args[2]);
				this.openInventory(mcp, target, channel, hash);
				return;
			}
			mcp.getPlayer().sendMessage(ChatColor.RED + "Invalid channel: " + args[1]);
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command.");
		return;
	}
	
	private void openInventory(MineverseChatPlayer mcp, MineverseChatPlayer target, ChatChannel channel, int hash) {
		Inventory inv = Bukkit.createInventory(null, this.getSlots(), target.getName() + " GUI");
		ItemStack close = new ItemStack(Material.BARRIER);
		ItemMeta closeMeta = close.getItemMeta();
		closeMeta.setDisplayName("§oClose GUI");
		close.setItemMeta(closeMeta);
		
		ItemStack skull = new ItemStack(Material.SKULL_ITEM);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta(); 
		skullMeta.setOwner(target.getName()); 
		skullMeta.setDisplayName("§b" + target.getName());
		List<String> skullLore = new ArrayList<String>();
		skullLore.add("§7Channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
		skullLore.add("§7Hash: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + hash);
		skullMeta.setLore(skullLore);
		skull.setItemMeta(skullMeta); 
		skull.setDurability((short) 3);
		inv.setItem(0, skull);

		for(GuiSlot g : MineverseChat.gsInfo.getGuiSlots()) {
			if(!g.hasPermission() || mcp.getPlayer().hasPermission(g.getPermission())) {
				if(this.checkSlot(g.getSlot())) {
					MineverseChat.getInstance().getServer().getConsoleSender().sendMessage(Format.FormatStringAll("&cGUI: " + g.getName() + " has invalid slot: " + g.getSlot() + "!"));
					continue;
				}
				ItemStack gStack = new ItemStack(g.getIcon());
				gStack.setDurability((short) g.getDurability());
				ItemMeta gMeta = gStack.getItemMeta();
				String displayName = g.getText().replace("{player_name}", target.getName()).replace("{channel}", channel.getName()).replace("{hash}", hash + "");
				if(target.isOnline()) {
					displayName = PlaceholderAPI.setBracketPlaceholders(target.getPlayer(), displayName);
				}
				gMeta.setDisplayName(Format.FormatStringAll(displayName));
				List<String> gLore = new ArrayList<String>();
				gMeta.setLore(gLore);
				gStack.setItemMeta(gMeta);
				inv.setItem(g.getSlot(), gStack);
			}
		}

		inv.setItem(8, close);
		mcp.getPlayer().openInventory(inv);
	}
	
	private boolean checkSlot(int slot) {
		return slot == 0 || slot == 8;
	}
	
	private int getSlots() {
		int rows = plugin.getConfig().getInt("guirows", 1);
		if(rows == 2)
			return 18;
		if(rows == 3)
			return 27;
		if(rows == 4)
			return 36;
		if(rows == 5)
			return 45;
		if(rows == 6)
			return 54;
		return 9;
	}
}