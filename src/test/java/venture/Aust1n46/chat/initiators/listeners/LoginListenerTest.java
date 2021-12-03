package venture.Aust1n46.chat.initiators.listeners;

import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import venture.Aust1n46.chat.Logger;
import venture.Aust1n46.chat.VentureChat;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.controllers.VentureChatSpigotFlatFileController;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.UUIDService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

@RunWith(MockitoJUnitRunner.class)
public class LoginListenerTest {
	@Mock
	private VentureChat plugin;
	@Mock
	private UUIDService uuidService;
	@Mock
	private VentureChatSpigotFlatFileController spigotFlatFileController;
	@Mock
	private PluginMessageController pluginMessageController;
	@Mock
	private VentureChatPlayerApiService playerApiService;
	@Mock
	private Logger log;
	@InjectMocks
	private LoginListener loginListener;
	
	@Mock
	private PlayerQuitEvent mockPlayerQuitEvent;
	@Mock
	private Player mockPlayer;
	@Mock
	private VentureChatPlayer mockVentureChatPlayer;
	
	@Test
	public void testPlayerQuit() {
		when(mockPlayerQuitEvent.getPlayer()).thenReturn(mockPlayer);
		when(playerApiService.getOnlineMineverseChatPlayer(mockPlayer)).thenReturn(mockVentureChatPlayer);
		loginListener.onPlayerQuit(mockPlayerQuitEvent);
	}
	
	@Test
	public void testPlayerJoin_successful() {
		
	}
}
