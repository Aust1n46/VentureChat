package venture.Aust1n46.chat.controllers.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatMessage;
import venture.Aust1n46.chat.model.UniversalCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatFormatService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

public class Removemessage extends UniversalCommand {
	@Inject
	private VentureChat plugin;
	@Inject
	private VentureChatFormatService formatService;
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	private PacketContainer emptyLinePacketContainer;
	private WrappedChatComponent messageDeletedComponentPlayer;

	@Inject
	public Removemessage(String name) {
		super(name);
	}

	@Inject
	public void postConstruct() {
		emptyLinePacketContainer = formatService.createPacketPlayOutChat("{\"extra\":[\" \"],\"text\":\"\"}");
		messageDeletedComponentPlayer = WrappedChatComponent.fromJson("{\"text\":\"\",\"extra\":[{\"text\":\"\",\"extra\":["
				+ formatService.convertToJsonColors(FormatUtils.FormatStringAll(plugin.getConfig().getString("messageremovertext")))
				+ "],\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
				+ formatService.convertToJsonColors(FormatUtils.FormatStringAll(plugin.getConfig().getString("messageremoverpermissions"))) + "]}}}]}");
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeCommand(CommandSender sender, String command, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/removemessage").replace("{args}", "[hashcode] {channel}"));
			return;
		}
		final int hash;
		try {
			hash = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(LocalizedMessage.INVALID_HASH.toString());
			return;
		}
		if (args.length > 1 && configService.isChannel(args[1]) && configService.getChannel(args[1]).getBungee()) {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteOutStream);
			try {
				out.writeUTF("RemoveMessage");
				out.writeUTF(String.valueOf(hash));
				pluginMessageController.sendPluginMessage(byteOutStream);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		} else {
			new BukkitRunnable() {
				public void run() {
					final Map<Player, List<PacketContainer>> packets = new HashMap();
					for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
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
								playerPackets.add(formatService.createPacketPlayOutChat(removedComponent));
								resend = true;
								continue;
							}
							if (message.getMessage().contains(ChatColor.stripColor(FormatUtils.FormatStringAll(plugin.getConfig().getString("guiicon"))))) {
								String submessage = message.getMessage().substring(0,
										message.getMessage().length() - ChatColor.stripColor(FormatUtils.FormatStringAll(plugin.getConfig().getString("guiicon"))).length());
								if (submessage.hashCode() == hash) {
									WrappedChatComponent removedComponent = p.getPlayer().hasPermission("venturechat.message.bypass")
											? Removemessage.this.getMessageDeletedChatComponentAdmin(message)
											: Removemessage.this.getMessageDeletedChatComponentPlayer();
									message.setComponent(removedComponent);
									message.setHash(-1);
									playerPackets.add(formatService.createPacketPlayOutChat(removedComponent));
									resend = true;
									continue;
								}
							}
							playerPackets.add(formatService.createPacketPlayOutChat(message.getComponent()));

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
									formatService.sendPacketPlayOutChat(p, c);
								}
							}
						}
					}.runTask(plugin);
				}
			}.runTaskAsynchronously(plugin);
		}
	}

	public WrappedChatComponent getMessageDeletedChatComponentPlayer() {
		return this.messageDeletedComponentPlayer;
	}

	public WrappedChatComponent getMessageDeletedChatComponentAdmin(ChatMessage message) {
		return WrappedChatComponent.fromJson("[{\"text\":\"\",\"extra\":[{\"text\":\"\",\"extra\":["
				+ formatService.convertToJsonColors(FormatUtils.FormatStringAll(plugin.getConfig().getString("messageremovertext")))
				+ "],\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\"," + message.getColoredMessage() + "}}}]}]");
	}
}
