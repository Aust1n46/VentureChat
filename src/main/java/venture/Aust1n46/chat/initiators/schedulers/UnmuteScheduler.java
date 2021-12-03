package venture.Aust1n46.chat.initiators.schedulers;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.FormatUtils;
import venture.Aust1n46.chat.VentureChat;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.controllers.commands.MuteContainer;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class UnmuteScheduler {
	@Inject
	private VentureChat plugin;
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	
	@Inject
	public void postConstruct() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskTimerAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
					long currentTimeMillis = System.currentTimeMillis();
					Iterator<MuteContainer> iterator = p.getMutes().iterator();
					while (iterator.hasNext()) {
						MuteContainer mute = iterator.next();
						if(ChatChannel.isChannel(mute.getChannel())) {
							ChatChannel channel = ChatChannel.getChannel(mute.getChannel());
							long timemark = mute.getDuration();
							if (timemark == 0) {
								continue;
							}
							if (plugin.getConfig().getString("loglevel", "info").equals("trace")) {
								System.out.println(currentTimeMillis + " " + timemark);
							}
							if (currentTimeMillis >= timemark) {
								iterator.remove();
								p.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString()
										.replace("{player}", p.getName()).replace("{channel_color}", channel.getColor())
										.replace("{channel_name}", mute.getChannel()));
								if(channel.getBungee()) {
									pluginMessageController.synchronize(p, true);
								}
							}
						}
					}
				}
				if (plugin.getConfig().getString("loglevel", "info").equals("trace")) {
					Bukkit.getConsoleSender()
							.sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Updating Player Mutes"));
				}
			}
		}, 0L, 60L); // three second interval
	}
}
