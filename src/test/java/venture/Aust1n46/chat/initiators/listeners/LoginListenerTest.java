package venture.Aust1n46.chat.initiators.listeners;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.controllers.SpigotFlatFileController;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.UuidService;
import venture.Aust1n46.chat.service.PlayerApiService;
import venture.Aust1n46.chat.xcut.Logger;

@ExtendWith(MockitoExtension.class)
public class LoginListenerTest {
	@Mock
	private VentureChat plugin;
	@Mock
	private UuidService uuidService;
	@Mock
	private SpigotFlatFileController spigotFlatFileController;
	@Mock
	private PluginMessageController pluginMessageController;
	@Mock
	private PlayerApiService playerApiService;
	@Mock
	private ConfigService configService;
	@Mock
	private Logger log;
	@InjectMocks
	private LoginListener loginListener;

	@Mock
	private PlayerQuitEvent mockPlayerQuitEvent;
	@Mock
	private PlayerJoinEvent mockPlayerJoinEvent;
	@Mock
	private Player mockPlayer;
	@Mock
	private VentureChatPlayer mockVentureChatPlayer;
	@Mock
	private ChatChannel mockDefaultChannel;

	@Test
	public void testPlayerQuit() {
		when(mockPlayerQuitEvent.getPlayer()).thenReturn(mockPlayer);
		when(playerApiService.getOnlineMineverseChatPlayer(mockPlayer)).thenReturn(mockVentureChatPlayer);
		loginListener.onPlayerQuit(mockPlayerQuitEvent);
	}

	@Test
	public void testPlayerQuit_playerNull() {
		when(mockPlayerQuitEvent.getPlayer()).thenReturn(mockPlayer);
		when(playerApiService.getOnlineMineverseChatPlayer(mockPlayer)).thenReturn(null);
		loginListener.onPlayerQuit(mockPlayerQuitEvent);
		assertDoesNotThrow(() -> loginListener.onPlayerQuit(mockPlayerQuitEvent));
	}

	@Test
	public void testPlayerJoin_existingPlayer_NoProxy() {
		when(mockPlayerJoinEvent.getPlayer()).thenReturn(mockPlayer);
		when(configService.getDefaultChannel()).thenReturn(mockDefaultChannel);
		when(mockPlayer.getName()).thenReturn("Aust1n46");
		when(configService.isProxyEnabled()).thenReturn(false);
		loginListener.onPlayerJoin(mockPlayerJoinEvent);
	}

	@Test
	public void testPlayerJoin_existingPlayer_Proxy() {
		when(mockPlayerJoinEvent.getPlayer()).thenReturn(mockPlayer);
		when(configService.getDefaultChannel()).thenReturn(mockDefaultChannel);
		when(mockPlayer.getName()).thenReturn("Aust1n46");
		when(configService.isProxyEnabled()).thenReturn(true);
		loginListener.onPlayerJoin(mockPlayerJoinEvent);
	}
}
