package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.command.Command;
//import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
//import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
//import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.localization.InternalMessage;
//import net.md_5.bungee.api.chat.ClickEvent;
//import net.md_5.bungee.api.chat.ComponentBuilder;
//import net.md_5.bungee.api.chat.HoverEvent;
//import net.md_5.bungee.api.chat.TextComponent;
//import net.minecraft.server.v1_15_R1.IChatBaseComponent;
//import net.minecraft.server.v1_15_R1.PacketPlayOutChat;

public class Venturechat extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();
	
	public Venturechat() {
		super("venturechat");
	}
	
	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		sender.sendMessage(InternalMessage.VENTURECHAT_VERSION.toString()
				.replace("{version}", plugin.getDescription().getVersion()));
		sender.sendMessage(InternalMessage.VENTURECHAT_AUTHOR.toString());
		return true;
//		if (sender instanceof Player && plugin.getConfig().getString("loglevel", "info").equals("debug")) {
//			Player player = (Player) sender;
//			String title = ChatColor.GOLD + " | " + ChatColor.BLUE.toString() + ChatColor.BOLD + "SpigotAPI chat message" + ChatColor.RESET + ChatColor.GOLD + " | ";
//			String spaces = " ";
//			TextComponent tcSpaces = new TextComponent(spaces);
//			TextComponent message = new TextComponent(title);
//			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to rank up!").create()));
//			message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Sample SpigotAPI Click Event"));
//			tcSpaces.addExtra(message);
//			player.spigot().sendMessage(tcSpaces);
//			
////			sendActionBar(player, "NMS ActionBar message");
//		}
	}
	
//	public static void sendActionBar(Player player, String message) {
//        message= message.replaceAll("%player%", player.getDisplayName());
//        message = ChatColor.translateAlternateColorCodes('&', message);
//        CraftPlayer p = (CraftPlayer) player;
//        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
//        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc);
//        try {
//        	MineverseChat.posField.set(ppoc, MineverseChat.chatMessageType.getEnumConstants()[2]);
//		} 
//        catch (Exception e) {
//			e.printStackTrace();
//        }
//        p.getHandle().playerConnection.sendPacket(ppoc);
//    }
}
