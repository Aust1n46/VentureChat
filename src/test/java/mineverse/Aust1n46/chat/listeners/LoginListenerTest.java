package mineverse.Aust1n46.chat.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.database.PlayerData;
import mineverse.Aust1n46.chat.listeners.LoginListener;;

/**
 * Tests {@link LoginListener}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ MineverseChat.class, MineverseChatAPI.class, Bukkit.class, PlayerData.class, PlayerQuitEvent.class})
public class LoginListenerTest {
	private MineverseChat mockPlugin;
	private Player mockPlayer;
	private MineverseChatPlayer mockMCP;
	private ConsoleCommandSender mockConsoleSender;
	private LoginListener testLoginListener;
	private PlayerQuitEvent mockPlayerQuitEvent;
	private File mockDataFile;
	
	@Before
	public void setUp() {
		mockPlugin = PowerMockito.mock(MineverseChat.class);
		mockPlayer = Mockito.mock(Player.class);
		mockMCP = Mockito.mock(MineverseChatPlayer.class);
		mockConsoleSender = Mockito.mock(ConsoleCommandSender.class);
		mockDataFile = Mockito.mock(File.class);
		
		mockPlayerQuitEvent = PowerMockito.mock(PlayerQuitEvent.class);
		PowerMockito.when(mockPlayerQuitEvent.getPlayer()).thenReturn(mockPlayer);
		
		PowerMockito.mockStatic(MineverseChat.class);
		PowerMockito.when(MineverseChat.getInstance()).thenReturn(mockPlugin);
		PowerMockito.when(mockPlugin.getDataFolder()).thenReturn(mockDataFile);
		
		PowerMockito.mockStatic(MineverseChatAPI.class);
		PowerMockito.when(MineverseChatAPI.getMineverseChatPlayer(Mockito.any(Player.class))).thenReturn(mockMCP);
		PowerMockito.when(MineverseChatAPI.getOnlineMineverseChatPlayer(Mockito.any(Player.class))).thenReturn(mockMCP);
		
		PowerMockito.mockStatic(Bukkit.class);
		PowerMockito.when(Bukkit.getConsoleSender()).thenReturn(mockConsoleSender);
		
		PowerMockito.mockStatic(PlayerData.class);
		
		testLoginListener = new LoginListener();
	}

	@After
	public void tearDown() {
		mockPlugin = null;
	}
	
	@Test
	public void testLoginWithNameChange() throws Exception {
		Mockito.when(mockPlayer.getName()).thenReturn("NewName");
		Mockito.when(mockMCP.getName()).thenReturn("OldName");
		Mockito.when(mockPlayer.getDisplayName()).thenReturn("OldName");
		testLoginListener.handleNameChange(mockMCP, mockPlayer);
		Mockito.verify(mockMCP, Mockito.times(1)).setNickname("NewName");
	}
	
	@Test
	public void testPlayerQuit() {
		testLoginListener.onPlayerQuit(mockPlayerQuitEvent);
		Mockito.verify(mockMCP, Mockito.times(1)).clearMessages();
		Mockito.verify(mockMCP, Mockito.times(1)).setOnline(false);
	}
}
