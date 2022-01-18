package mineverse.Aust1n46.chat.command.chat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Removemessage extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();
	private PacketContainer emptyLinePacketContainer = Format.createPacketPlayOutChat("{\"extra\":[\" \"],\"text\":\"\"}");
	private WrappedChatComponent messageDeletedComponentPlayer = WrappedChatComponent.fromJson(
			"{\"text\":\"\",\"extra\":[{\"text\":\"\",\"extra\":[" + Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("messageremovertext")))
					+ "],\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
					+ Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("messageremoverpermissions"))) + "]}}}]}");

	public Removemessage() {
		super("removemessage");
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/removemessage").replace("{args}", "[hashcode] {channel}"));
			return true;
		}
		final int hash;
		try {
			hash = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(LocalizedMessage.INVALID_HASH.toString());
			return true;
		}
		if (args.length > 1 && ChatChannel.isChannel(args[1]) && ChatChannel.getChannel(args[1]).getBungee() && sender instanceof Player) {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteOutStream);
			try {
				out.writeUTF("RemoveMessage");
				out.writeUTF(String.valueOf(hash));
				((Player) sender).sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else {
			new BukkitRunnable() {
				public void run() {
					final Map<Player, List<PacketContainer>> packets = new HashMap();
					for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
						List<ChatMessage> messages = p.getMessages();
						List<PacketContainer> playerPackets = new ArrayList();
						boolean resend = false;
						for (int fill = 0; fill < 100 - messages.size(); fill++) {
							playerPackets.add(Removemessage.this.emptyLinePacketContainer);
						}
						for (ChatMessage message : messages) {
							if (message.getHash() == hash) {
								WrappedChatComponent removedComponent = p.getPlayer().hasPermission("venturechat.message.bypass")
										? Removemessage.this.getMessageDeletedChatComponentAdmin(message)
										: Removemessage.this.getMessageDeletedChatComponentPlayer();
								message.setComponent(removedComponent);
								message.setHash(-1);
								playerPackets.add(Format.createPacketPlayOutChat(removedComponent));
								resend = true;
								continue;
							}
							if (message.getMessage().contains(ChatColor.stripColor(Format.FormatStringAll(plugin.getConfig().getString("guiicon"))))) {
								String submessage = message.getMessage().substring(0,
										message.getMessage().length() - ChatColor.stripColor(Format.FormatStringAll(plugin.getConfig().getString("guiicon"))).length());
								if (submessage.hashCode() == hash) {
									WrappedChatComponent removedComponent = p.getPlayer().hasPermission("venturechat.message.bypass")
											? Removemessage.this.getMessageDeletedChatComponentAdmin(message)
											: Removemessage.this.getMessageDeletedChatComponentPlayer();
									message.setComponent(removedComponent);
									message.setHash(-1);
									playerPackets.add(Format.createPacketPlayOutChat(removedComponent));
									resend = true;
									continue;
								}
							}
							playerPackets.add(Format.createPacketPlayOutChat(message.getComponent()));

						}
						if (resend) {
							packets.put(p.getPlayer(), playerPackets);
						}
					}
					new BukkitRunnable() {
						public void run() {
							for (Player p : packets.keySet()) {
								List<PacketContainer> pPackets = packets.get(p);
								for (PacketContainer c : pPackets) {
									Format.sendPacketPlayOutChat(p, c);
								}
							}
						}
					}.runTask(plugin);
				}
			}.runTaskAsynchronously(plugin);
			return true;
		}
	}

	public WrappedChatComponent getMessageDeletedChatComponentPlayer() {
		return this.messageDeletedComponentPlayer;
	}

	public WrappedChatComponent getMessageDeletedChatComponentAdmin(ChatMessage message) {
		return WrappedChatComponent.fromJson(
				"[{\"text\":\"\",\"extra\":[{\"text\":\"\",\"extra\":[" + Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("messageremovertext")))
						+ "],\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\"," + message.getColoredMessage() + "}}}]}]");
	}
}
