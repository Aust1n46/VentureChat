package mineverse.Aust1n46.chat.utilities;

import static mineverse.Aust1n46.chat.utilities.Format.BUKKIT_COLOR_CODE_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import mineverse.Aust1n46.chat.MineverseChat;

/**
 * Tests {@link Format}.
 */
public class FormatTest {
	private static MockedStatic<MineverseChat> mockedMineverseChat;

	private static MineverseChat mockPlugin;
	private FileConfiguration mockConfig;

	private List<String> filters;

	@BeforeClass
	public static void init() {
		mockedMineverseChat = Mockito.mockStatic(MineverseChat.class);
		mockPlugin = Mockito.mock(MineverseChat.class);
		Mockito.when(MineverseChat.getInstance()).thenReturn(mockPlugin);
	}
	
	@AfterClass
	public static void close() {
		mockedMineverseChat.close();
	}
	
	@Before
	public void setUp() {
		filters = new ArrayList<String>();
		filters.add("ass,donut");

		mockConfig = Mockito.mock(FileConfiguration.class);
		Mockito.when(mockPlugin.getConfig()).thenReturn(mockConfig);
		Mockito.when(mockConfig.getStringList("filters")).thenReturn(filters);
	}

	@After
	public void tearDown() {
		filters = new ArrayList<String>();
	}

	@Test
	public void testGetLastCodeSingleColor() {
		String input = BUKKIT_COLOR_CODE_PREFIX + "cHello";
		String expectedResult = BUKKIT_COLOR_CODE_PREFIX + "c";

		String result = Format.getLastCode(input);

		assertEquals(expectedResult, result);
	}

	@Test
	public void testGetLastCodeColorAfterFormat() {
		String input = BUKKIT_COLOR_CODE_PREFIX + "o" + BUKKIT_COLOR_CODE_PREFIX + "cHello";
		String expectedResult = BUKKIT_COLOR_CODE_PREFIX + "c";

		String result = Format.getLastCode(input);

		assertEquals(expectedResult, result);
	}

	@Test
	public void testGetLastCodeColorBeforeFormat() {
		String input = BUKKIT_COLOR_CODE_PREFIX + "c" + BUKKIT_COLOR_CODE_PREFIX + "oHello";
		String expectedResult = BUKKIT_COLOR_CODE_PREFIX + "c" + BUKKIT_COLOR_CODE_PREFIX + "o";

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
		String expectedResult = BUKKIT_COLOR_CODE_PREFIX + "x" + BUKKIT_COLOR_CODE_PREFIX + "f"
				+ BUKKIT_COLOR_CODE_PREFIX + "f" + BUKKIT_COLOR_CODE_PREFIX + "0" + BUKKIT_COLOR_CODE_PREFIX + "0"
				+ BUKKIT_COLOR_CODE_PREFIX + "f" + BUKKIT_COLOR_CODE_PREFIX + "f";

		String result = Format.convertHexColorCodeToBukkitColorCode(hexColor);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testConvertHexColorCodeStringToBukkitColorCodeString() {
		String input = "#ff00ffHello" + BUKKIT_COLOR_CODE_PREFIX + "cThere#00ff00Austin";
		String expectedResult = BUKKIT_COLOR_CODE_PREFIX + "x" + BUKKIT_COLOR_CODE_PREFIX + "f"
				+ BUKKIT_COLOR_CODE_PREFIX + "f" + BUKKIT_COLOR_CODE_PREFIX + "0" + BUKKIT_COLOR_CODE_PREFIX + "0"
				+ BUKKIT_COLOR_CODE_PREFIX + "f" + BUKKIT_COLOR_CODE_PREFIX + "fHello" + BUKKIT_COLOR_CODE_PREFIX
				+ "cThere" + BUKKIT_COLOR_CODE_PREFIX + "x" + BUKKIT_COLOR_CODE_PREFIX + "0" + BUKKIT_COLOR_CODE_PREFIX
				+ "0" + BUKKIT_COLOR_CODE_PREFIX + "f" + BUKKIT_COLOR_CODE_PREFIX + "f" + BUKKIT_COLOR_CODE_PREFIX + "0"
				+ BUKKIT_COLOR_CODE_PREFIX + "0Austin";

		String result = Format.convertHexColorCodeStringToBukkitColorCodeString(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testFormatStringLegacyColor_NoColorCode() {
		String input = "Hello There Austin";
		String expectedResult = "Hello There Austin";

		String result = Format.FormatStringLegacyColor(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testFormatStringLegacyColor_LegacyCodeOnly() {
		String input = "Hello &cThere Austin";
		String expectedResult = "Hello " + BUKKIT_COLOR_CODE_PREFIX + "cThere Austin";

		String result = Format.FormatStringLegacyColor(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testFormatStringLegacyColor_SpigotHexCodeOnly() {
		String input = "&x&f&f&f&f&f&fHello There Austin";
		String expectedResult = "&x&f&f&f&f&f&fHello There Austin";

		String result = Format.FormatStringLegacyColor(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testFormatStringLegacyColor_BothColorCodes() {
		String input = "&x&f&f&f&f&f&f&cHello There Austin";
		String expectedResult = "&x&f&f&f&f&f&f" + BUKKIT_COLOR_CODE_PREFIX + "cHello There Austin";

		String result = Format.FormatStringLegacyColor(input);
		assertEquals(expectedResult, result);
	}
}
