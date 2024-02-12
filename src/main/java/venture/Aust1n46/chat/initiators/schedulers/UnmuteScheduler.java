package venture.Aust1n46.chat.initiators.schedulers;

import java.util.Iterator;

import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.MuteContainer;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.PlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

public class UnmuteScheduler {
	@Inject
	private VentureChat plugin;
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private PlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public void postConstruct() {
		final Server server = plugin.getServer();
		BukkitScheduler scheduler = server.getScheduler();
		scheduler.runTaskTimerAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
					long currentTimeMillis = System.currentTimeMillis();
					Iterator<MuteContainer> iterator = p.getMutes().iterator();
					while (iterator.hasNext()) {
						MuteContainer mute = iterator.next();
						if (configService.isChannel(mute.getChannel())) {
							ChatChannel channel = configService.getChannel(mute.getChannel());
							long timemark = mute.getDuration();
							if (timemark == 0) {
								continue;
							}
							if (plugin.getConfig().getString("loglevel", "info").equals("trace")) {
								System.out.println(currentTimeMillis + " " + timemark);
							}
							if (currentTimeMillis >= timemark) {
								iterator.remove();
								p.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString().replace("{player}", p.getName())
										.replace("{channel_color}", channel.getColor()).replace("{channel_name}", mute.getChannel()));
								if (channel.getBungee()) {
									pluginMessageController.synchronize(p, true);
								}
							}
						}
					}
				}
				if (plugin.getConfig().getString("loglevel", "info").equals("trace")) {
					server.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Updating Player Mutes"));
				}
			}
		}, 0L, 60L); // three second interval
	}
}
