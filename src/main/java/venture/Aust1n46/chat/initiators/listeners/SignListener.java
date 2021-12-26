package venture.Aust1n46.chat.initiators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

//This class listens for text being added to signs, and it formats them to allow colors and formatting.
@Singleton
public class SignListener implements Listener {
	@Inject
	private VentureChatPlayerApiService playerApiService;
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(event.getPlayer());
		for(int a = 0; a < event.getLines().length; a++) {
			String line = event.getLine(a);
			if(mcp.getPlayer().hasPermission("venturechat.color.legacy")) {
				line = FormatUtils.FormatStringLegacyColor(line);
			}
			if(mcp.getPlayer().hasPermission("venturechat.color")) {
				line = FormatUtils.FormatStringColor(line);
			}
			if(mcp.getPlayer().hasPermission("venturechat.format")) {
				line = FormatUtils.FormatString(line);
			}
			event.setLine(a, line);
		}
	}
}
