package mineverse.Aust1n46.chat.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import mineverse.Aust1n46.chat.MineverseChat;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { MineverseChat.class })
public class FormatTest {
	
	private MineverseChat mockPlugin;
	private FileConfiguration mockConfig;
	
	private List<String> filters;
	
	@Before
	public void setUp() {
		filters = new ArrayList<String>(); 
		filters.add("ass,donut"); 
		
		mockPlugin = Mockito.mock(MineverseChat.class);
		mockConfig = Mockito.mock(FileConfiguration.class);
		
		PowerMockito.mockStatic(MineverseChat.class);
		PowerMockito.when(MineverseChat.getInstance()).thenReturn(mockPlugin);
		Mockito.when(mockPlugin.getConfig()).thenReturn(mockConfig); 
		Mockito.when(mockConfig.getStringList("filters")).thenReturn(filters); 
	}
	
	@After
	public void tearDown() {
		mockPlugin = null;
		mockConfig = null;
		filters = new ArrayList<String>();
	}

	@Test
	public void testGetLastCodeSingleColor() {
		String input = "§cHello";
		String expectedResult = "§c";
		
		String result = Format.getLastCode(input);
		
		Assert.assertEquals(expectedResult, result);
	}
	
	@Test
	public void testGetLastCodeColorAfterFormat() {
		String input = "§o§cHello";
		String expectedResult = "§c";
		
		String result = Format.getLastCode(input);
		
		Assert.assertEquals(expectedResult, result);
	}
	
	@Test
	public void testGetLastCodeColorBeforeFormat() {
		String input = "§c§oHello";
		String expectedResult = "§c§o";
		
		String result = Format.getLastCode(input);
		
		Assert.assertEquals(expectedResult, result);
	}
	
	@Test
	public void testFilterChat() {  
		String test = "I am an ass";
		String expectedResult = "I am an donut";
		
		String result = Format.FilterChat(test);
		Assert.assertEquals(expectedResult, result);
	}
	
	@Test
	public void testIsValidColor() { 
		String color = "red";
		
		boolean result = Format.isValidColor(color);
		Assert.assertTrue(result);
	}
	
	@Test
	public void testIsInvalidColor() {
		String color = "randomString";
		
		boolean result = Format.isValidColor(color);
		Assert.assertFalse(result);
	}
}
