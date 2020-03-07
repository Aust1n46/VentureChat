package mineverse.Aust1n46.chat.command.chat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.utilities.Format;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class Removemessage extends MineverseCommand {
	private PacketContainer emptyLinePacketContainer = Format.createPacketPlayOutChat(WrappedChatComponent.fromJson("{\"extra\":[\" \"],\"text\":\"\"}"));
	private MineverseChat plugin;
	private WrappedChatComponent messageDeletedComponentPlayer;

	public Removemessage(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(CommandSender sender, String command, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Invalid command: /removemessage [hashcode]");
			return;
		}
		final int hash;
		try {
			hash = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid hashcode.");
			return;
		}
		if(args.length > 1 && MineverseChat.ccInfo.isChannel(args[1]) && MineverseChat.ccInfo.getChannelInfo(args[1]).getBungee() && sender instanceof Player) {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteOutStream);
			try {
				out.writeUTF("RemoveMessage");
				out.writeUTF(String.valueOf(hash));
				((Player) sender).sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
				out.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		else {
			new BukkitRunnable() {
				public void run() {
					final Map<Player, List<PacketContainer>> packets = new HashMap();
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						List<ChatMessage> messages = p.getMessages();
						List<PacketContainer> playerPackets = new ArrayList();
						boolean resend = false;
						for(int fill = 0; fill < 100 - messages.size(); fill++) {
							playerPackets.add(Removemessage.this.emptyLinePacketContainer);
						}
						for(ChatMessage message : messages) {
							if(message.getHash() == hash) {
								WrappedChatComponent removedComponent = p.getPlayer().hasPermission("venturechat.message.bypass") ? Removemessage.this.getMessageDeletedChatComponentAdmin(message) : Removemessage.this.getMessageDeletedChatComponentPlayer();
								message.setComponent(removedComponent);
								message.setHash(-1);
								playerPackets.add(Format.createPacketPlayOutChat(removedComponent));
								resend = true;
								continue;
							}
							if(message.getMessage().contains(ChatColor.stripColor(Format.FormatStringAll(plugin.getConfig().getString("guiicon"))))) {
								String submessage = message.getMessage().substring(0, message.getMessage().length() - ChatColor.stripColor(Format.FormatStringAll(plugin.getConfig().getString("guiicon"))).length());
								if(submessage.hashCode() == hash) {
									WrappedChatComponent removedComponent = p.getPlayer().hasPermission("venturechat.message.bypass") ? Removemessage.this.getMessageDeletedChatComponentAdmin(message) : Removemessage.this.getMessageDeletedChatComponentPlayer();
									message.setComponent(removedComponent);
									message.setHash(-1);
									playerPackets.add(Format.createPacketPlayOutChat(removedComponent));
									resend = true;
									continue;
								}
							}
							playerPackets.add(Format.createPacketPlayOutChat(message.getComponent()));
							
						}
						if(resend) {
							packets.put(p.getPlayer(), playerPackets);
						}
					}
					new BukkitRunnable() {
						public void run() {
							for(Player p : packets.keySet()) {
								List<PacketContainer> pPackets = packets.get(p);
								for(PacketContainer c : pPackets) {
									Format.sendPacketPlayOutChat(p, c);
								}
							}
						}
					}.runTask(plugin);
				}
			}.runTaskAsynchronously(plugin);
		}
	}

	public WrappedChatComponent getMessageDeletedChatComponentPlayer() {
		if(this.messageDeletedComponentPlayer == null) {
			this.messageDeletedComponentPlayer = WrappedChatComponent.fromJson("{\"text\":\"\",\"extra\":[{\"text\":\"" + Format.FormatStringAll(plugin.getConfig().getString("messageremovertext")) + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Format.FormatStringAll(plugin.getConfig().getString("messageremoverpermissions")) + "\"}]}}}]}");
		}
		return this.messageDeletedComponentPlayer;
	}

	public WrappedChatComponent getMessageDeletedChatComponentAdmin(ChatMessage message) {
		String oMessage = message.getComponent().getJson().substring(1, message.getComponent().getJson().length() - 11);
		if(message.getMessage().contains(ChatColor.stripColor(Format.FormatStringAll(plugin.getConfig().getString("guiicon"))))) {
			oMessage = oMessage.substring(0, oMessage.length() - ChatColor.stripColor(Format.FormatStringAll(plugin.getConfig().getString("guiicon"))).length() - 3) + "\"}]";
		}
		return WrappedChatComponent.fromJson(Format.FormatStringAll("{\"text\":\"\",\"extra\":[{\"text\":\"" + Format.FormatStringAll(plugin.getConfig().getString("messageremovertext")) + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"&7Message: \"," + oMessage + "}}}]}"));
	}
}