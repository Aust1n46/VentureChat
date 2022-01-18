package mineverse.Aust1n46.chat.command.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Edit extends Command {
	private PacketContainer emptyLinePacketContainer = Format.createPacketPlayOutChat("{\"extra\":[\" \"],\"text\":\"\"}");
	private MineverseChat plugin = MineverseChat.getInstance();
	private WrappedChatComponent messageDeletedComponentPlayer;

	public Edit() {
		super("edit");
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/edit").replace("{args}", "[hashcode]"));
			return true;
		}
		final int hash;
		try {
			hash = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(LocalizedMessage.INVALID_HASH.toString());
			return true;
		}
		new BukkitRunnable() {
			public void run() {
				final Map<Player, List<PacketContainer>> packets = new HashMap();
				for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
					List<ChatMessage> messages = p.getMessages();
					List<PacketContainer> playerPackets = new ArrayList();
					boolean resend = false;
					for (int fill = 0; fill < 100 - messages.size(); fill++) {
						playerPackets.add(Edit.this.emptyLinePacketContainer);
					}
					for (ChatMessage message : messages) {
						if (message.getHash() == hash) {
							WrappedChatComponent removedComponent = p.getPlayer().hasPermission("venturechat.message.bypass")
									? Edit.this.getMessageDeletedChatComponentAdmin(message)
									: Edit.this.getMessageDeletedChatComponentPlayer();
							message.setComponent(removedComponent);
							message.setHash(-1);
							playerPackets.add(Format.createPacketPlayOutChat(removedComponent));
							resend = true;
							continue;
						}
						if (message.getMessage().contains(Format.FormatStringAll(plugin.getConfig().getString("messageremovericon")))) {
							String submessage = message.getMessage().substring(0, message.getMessage().length() - plugin.getConfig().getString("messageremovericon").length() - 1)
									.replaceAll("(�([a-z0-9]))", "");
							if (submessage.hashCode() == hash) {
								WrappedChatComponent removedComponent = p.getPlayer().hasPermission("venturechat.message.bypass")
										? Edit.this.getMessageDeletedChatComponentAdmin(message)
										: Edit.this.getMessageDeletedChatComponentPlayer();
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

	public WrappedChatComponent getMessageDeletedChatComponentPlayer() {
		if (this.messageDeletedComponentPlayer == null) {
			this.messageDeletedComponentPlayer = WrappedChatComponent.fromJson(
					"{\"text\":\"\",\"extra\":[{\"text\":\"<message removed>\",\"color\":\"red\",\"italic\":\"true\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
							+ Format.FormatStringAll(plugin.getConfig().getString("messageremoverpermissions")) + "\"}]}}}]}");
		}
		return this.messageDeletedComponentPlayer;
	}

	public WrappedChatComponent getMessageDeletedChatComponentAdmin(ChatMessage message) {
		String oMessage = message.getComponent().getJson().substring(1, message.getComponent().getJson().length() - 11);
		if (message.getMessage().contains(Format.FormatStringAll(plugin.getConfig().getString("messageremovericon")))) {
			oMessage = oMessage.substring(0, oMessage.length() - plugin.getConfig().getString("messageremovericon").length() - 4) + "\"}]";
		}
		return WrappedChatComponent.fromJson(
				Format.FormatStringAll("{\"text\":\"\",\"extra\":[{\"text\":\"&c&o<message removed>\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"&7Message: \","
						+ oMessage + "}}}]}"));
	}
}
