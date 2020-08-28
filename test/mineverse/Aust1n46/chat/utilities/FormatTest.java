package mineverse.Aust1n46.chat.utilities;

import static mineverse.Aust1n46.chat.utilities.Format.BUKKIT_COLOR_CODE_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.After;
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
		String input = "Hello";
		String expectedResult = "";
		
		String result = Format.getLastCode(input);
		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testGetLastCodeColorAfterFormat() {
		String input = "Hello";
		String expectedResult = "";
		
		String result = Format.getLastCode(input);
		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testGetLastCodeColorBeforeFormat() {
		String input = "Hello";
		String expectedResult = "";
		
		String result = Format.getLastCode(input);
		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testFilterChat() {  
		String test = "I am an ass";
		String expectedResult = "I am an donut";
		
		String result = Format.FilterChat(test);
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testIsValidColor() { 
		String color = "red";
		
		boolean result = Format.isValidColor(color);
		assertTrue(result);
	}
	
	@Test
	public void testIsInvalidColor() {
		String color = "randomString";
		
		boolean result = Format.isValidColor(color);
		assertFalse(result);
	}
	
	@Test
	public void testIsValidHexColor() {
		String hexColor = "#ff00ff";
		
		boolean result = Format.isValidHexColor(hexColor);
		assertTrue(result);
	}
	
	@Test
	public void testIsInvalidHexColor() {
		String hexColor = "#random";
		
		boolean result = Format.isValidHexColor(hexColor);
		assertFalse(result);
	}
	
	@Test
	public void testConvertHexColorCodeToBukkitColorCode() {
		String hexColor = "#ff00ff";
		String expectedResult = "񖶄";
		
		String result = Format.convertHexColorCodeToBukkitColorCode(hexColor);
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testConvertHexColorCodeStringToBukkitColorCodeString() {
		String input = "#ff00ffHelloThere#00ff00Austin";
		String expectedResult = "񖶄HelloThere񖶄񖶄Austin";
		
		String result = Format.convertHexColorCodeStringToBukkitColorCodeString(input);
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testConvertLegacyColorCodeStringToBukkitColorCodeString() {
		String input = "Hello &cThere Austin";
		String expectedResult = "Hello " + BUKKIT_COLOR_CODE_PREFIX + "cThere Austin";

		String result = Format.FormatStringLegacyColor(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testRemoveSpigoteHexColorCodeFromStringWithLegacyFormatting() {
		String input = "&x&f&f&f&f&f&fHello There Austin";
		String expectedResult = "&x&f&f&f&f&f&fHello There Austin";

		String result = Format.FormatStringLegacyColor(input);
		assertEquals(expectedResult, result);
	}
}
