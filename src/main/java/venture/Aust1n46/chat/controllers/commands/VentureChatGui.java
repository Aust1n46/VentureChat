package venture.Aust1n46.chat.controllers.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.inject.Inject;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.FormatUtils;
import mineverse.Aust1n46.chat.versions.VersionHandler;
import venture.Aust1n46.chat.VentureChat;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.GuiSlot;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class VentureChatGui implements VentureCommand {
	@Inject
    private VentureChat plugin;
	@Inject
	private VentureChatPlayerApiService playerApiService;

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
                    .replace("{command}", "/venturechatgui")
                    .replace("{args}", "[player] [channel] [hashcode]"));
            return;
        }
        VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
        if (mcp.getPlayer().hasPermission("venturechat.gui")) {
            VentureChatPlayer target = playerApiService.getMineverseChatPlayer(args[0]);
            if (target == null && !args[0].equals("Discord")) {
                mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
                        .replace("{args}", args[0]));
                return;
            }
            if (ChatChannel.isChannel(args[1])) {
                ChatChannel channel = ChatChannel.getChannel(args[1]);
                final int hash;
                try {
                    hash = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(LocalizedMessage.INVALID_HASH.toString());
                    return;
                }
                if (args[0].equals("Discord")) {
                    this.openInventoryDiscord(mcp, channel, hash);
                    return;
                }
                this.openInventory(mcp, target, channel, hash);
                return;
            }
            mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString()
                    .replace("{args}", args[1]));
            return;
        }
        mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
        return;
    }

    @SuppressWarnings("deprecation")
    private void openInventory(VentureChatPlayer mcp, VentureChatPlayer target, ChatChannel channel, int hash) {
        Inventory inv = Bukkit.createInventory(null, this.getSlots(), "VentureChat: " + target.getName() + " GUI");
        ItemStack close = null;
        ItemStack skull = null;
        if (VersionHandler.is1_7()) {
            close = new ItemStack(Material.BEDROCK);
        } else {
            close = new ItemStack(Material.BARRIER);
        }

        if (VersionHandler.is1_7() || VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12()) {
            skull = new ItemStack(Material.getMaterial("SKULL_ITEM"));
        } else {
            skull = new ItemStack(Material.PLAYER_HEAD);
        }

        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "" + ChatColor.ITALIC + "Close GUI");
        close.setItemMeta(closeMeta);

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(target.getName());
        skullMeta.setDisplayName(ChatColor.AQUA + target.getName());
        List<String> skullLore = new ArrayList<String>();
        skullLore.add(ChatColor.GRAY + "Channel: " + channel.getColor() + channel.getName());
        skullLore.add(ChatColor.GRAY + "Hash: " + channel.getColor() + hash);
        skullMeta.setLore(skullLore);
        skull.setItemMeta(skullMeta);
        skull.setDurability((short) 3);
        inv.setItem(0, skull);

        for (GuiSlot g : GuiSlot.getGuiSlots()) {
            if (!g.hasPermission() || mcp.getPlayer().hasPermission(g.getPermission())) {
                if (this.checkSlot(g.getSlot())) {
                    plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&cGUI: " + g.getName() + " has invalid slot: " + g.getSlot() + "!"));
                    continue;
                }
                ItemStack gStack = new ItemStack(g.getIcon());
                gStack.setDurability((short) g.getDurability());
                ItemMeta gMeta = gStack.getItemMeta();
                String displayName = g.getText().replace("{player_name}", target.getName()).replace("{channel}", channel.getName()).replace("{hash}", hash + "");
                if (target.isOnline()) {
                    displayName = PlaceholderAPI.setBracketPlaceholders(target.getPlayer(), displayName);
                }
                gMeta.setDisplayName(FormatUtils.FormatStringAll(displayName));
                List<String> gLore = new ArrayList<String>();
                gMeta.setLore(gLore);
                gStack.setItemMeta(gMeta);
                inv.setItem(g.getSlot(), gStack);
            }
        }

        inv.setItem(8, close);
        mcp.getPlayer().openInventory(inv);
    }

    @SuppressWarnings("deprecation")
    private void openInventoryDiscord(VentureChatPlayer mcp, ChatChannel channel, int hash) {
        Inventory inv = Bukkit.createInventory(null, this.getSlots(), "VentureChat: Discord_Message GUI");
        ItemStack close = null;
        ItemStack skull = null;
        if (VersionHandler.is1_7()) {
            close = new ItemStack(Material.BEDROCK);
        } else {
            close = new ItemStack(Material.BARRIER);
        }

        if (VersionHandler.is1_7() || VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12()) {
            skull = new ItemStack(Material.getMaterial("SKULL_ITEM"));
        } else {
            skull = new ItemStack(Material.PLAYER_HEAD);
        }

        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName("�oClose GUI");
        close.setItemMeta(closeMeta);

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner("Scarsz");
        skullMeta.setDisplayName("�bDiscord_Message");
        List<String> skullLore = new ArrayList<String>();
        skullLore.add("�7Channel: " + channel.getColor() + channel.getName());
        skullLore.add("�7Hash: " + channel.getColor() + hash);
        skullMeta.setLore(skullLore);
        skull.setItemMeta(skullMeta);
        skull.setDurability((short) 3);
        inv.setItem(0, skull);

        for (GuiSlot g : GuiSlot.getGuiSlots()) {
            if (!g.hasPermission() || mcp.getPlayer().hasPermission(g.getPermission())) {
                if (this.checkSlot(g.getSlot())) {
                    plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&cGUI: " + g.getName() + " has invalid slot: " + g.getSlot() + "!"));
                    continue;
                }
                ItemStack gStack = new ItemStack(g.getIcon());
                gStack.setDurability((short) g.getDurability());
                ItemMeta gMeta = gStack.getItemMeta();
                String displayName = g.getText().replace("{player_name}", "Discord_Message").replace("{channel}", channel.getName()).replace("{hash}", hash + "");
                gMeta.setDisplayName(FormatUtils.FormatStringAll(displayName));
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
        if (rows == 2)
            return 18;
        if (rows == 3)
            return 27;
        if (rows == 4)
            return 36;
        if (rows == 5)
            return 45;
        if (rows == 6)
            return 54;
        return 9;
    }
}
