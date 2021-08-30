package mineverse.Aust1n46.chat.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.database.PlayerData;

/**
 * Tests {@link LoginListener}.
 */
public class LoginListenerTest {
	private static MockedStatic<MineverseChat> mockedMineverseChat;
	private static MockedStatic<Bukkit> mockedBukkit;
	private static MockedStatic<PlayerData> mockedPlayerData;
	private static MockedStatic<MineverseChatAPI> mockedMineverseChatAPI;
	
	private static MineverseChat mockPlugin;
	private Player mockPlayer;
	private MineverseChatPlayer mockMCP;
	private ConsoleCommandSender mockConsoleSender;
	private LoginListener testLoginListener;
	private PlayerQuitEvent mockPlayerQuitEvent;
	private static File mockDataFile;
	
	@BeforeClass
	public static void init() {
		mockedMineverseChat = Mockito.mockStatic(MineverseChat.class);
		mockPlugin = Mockito.mock(MineverseChat.class);
		Mockito.when(MineverseChat.getInstance()).thenReturn(mockPlugin);
		mockedBukkit = Mockito.mockStatic(Bukkit.class);
		mockDataFile = Mockito.mock(File.class);
		Mockito.when(mockPlugin.getDataFolder()).thenReturn(mockDataFile);
		Mockito.when(mockDataFile.getAbsolutePath()).thenReturn("");
		mockedPlayerData = Mockito.mockStatic(PlayerData.class);
		mockedMineverseChatAPI = Mockito.mockStatic(MineverseChatAPI.class);	
	}
	
	@AfterClass
	public static void close() {
		mockedMineverseChat.close();
		mockedBukkit.close();
		mockedPlayerData.close();
		mockedMineverseChatAPI.close();
	}
	
	@Before
	public void setUp() {
		mockPlayer = Mockito.mock(Player.class);
		mockMCP = Mockito.mock(MineverseChatPlayer.class);
		mockConsoleSender = Mockito.mock(ConsoleCommandSender.class);
		mockPlayerQuitEvent = Mockito.mock(PlayerQuitEvent.class);
		Mockito.when(mockPlayerQuitEvent.getPlayer()).thenReturn(mockPlayer);
		Mockito.when(MineverseChatAPI.getMineverseChatPlayer(Mockito.any(Player.class))).thenReturn(mockMCP);
		Mockito.when(MineverseChatAPI.getOnlineMineverseChatPlayer(Mockito.any(Player.class))).thenReturn(mockMCP);		
		Mockito.when(Bukkit.getConsoleSender()).thenReturn(mockConsoleSender);
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
		testLoginListener.handleNameChange(mockMCP, mockPlayer);
		Mockito.verify(mockMCP, Mockito.times(1)).setName("NewName");
	}
	
	@Test
	public void testPlayerQuit() {
		testLoginListener.onPlayerQuit(mockPlayerQuitEvent);
		Mockito.verify(mockMCP, Mockito.times(1)).clearMessages();
		Mockito.verify(mockMCP, Mockito.times(1)).setOnline(false);
	}
}
