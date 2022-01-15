package venture.Aust1n46.chat.utilities;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import venture.Aust1n46.chat.localization.LocalizedMessage;

public class FormatUtils {
	public static final char BUKKIT_COLOR_CODE_PREFIX_CHAR = '\u00A7';
	public static final String BUKKIT_COLOR_CODE_PREFIX = String.valueOf(BUKKIT_COLOR_CODE_PREFIX_CHAR);
	public static final String HEX_COLOR_CODE_PREFIX = "#";
	public static final String BUKKIT_HEX_COLOR_CODE_PREFIX = "x";
	public static final String DEFAULT_COLOR_CODE = BUKKIT_COLOR_CODE_PREFIX + "f";
	
	public static final long MILLISECONDS_PER_DAY = 86400000;
	public static final long MILLISECONDS_PER_HOUR = 3600000;
	public static final long MILLISECONDS_PER_MINUTE = 60000;
	public static final long MILLISECONDS_PER_SECOND = 1000;
	
	public static final int LINE_LENGTH = 40;
	
	private static final Pattern LEGACY_CHAT_COLOR_DIGITS_PATTERN = Pattern.compile("&([0-9])");
	private static final Pattern LEGACY_CHAT_COLOR_PATTERN = Pattern.compile(
			"(?<!(&x(&[a-fA-F0-9]){5}))(?<!(&x(&[a-fA-F0-9]){4}))(?<!(&x(&[a-fA-F0-9]){3}))(?<!(&x(&[a-fA-F0-9]){2}))(?<!(&x(&[a-fA-F0-9]){1}))(?<!(&x))(&)([0-9a-fA-F])");
	
	/**
     * Convert a single hex color code to a single Bukkit hex color code.
     *
     * @param color to convert.
     * @return {@link String}
     */
	public static String convertHexColorCodeToBukkitColorCode(String color) {
		color = color.replace("&", "");
		StringBuilder bukkitColorCode = new StringBuilder(BUKKIT_COLOR_CODE_PREFIX + BUKKIT_HEX_COLOR_CODE_PREFIX);
		for (int a = 1; a < color.length(); a++) {
			bukkitColorCode.append(BUKKIT_COLOR_CODE_PREFIX + color.charAt(a));
		}
		return bukkitColorCode.toString().toLowerCase();
	}

	/**
     * Convert an entire String of hex color codes to Bukkit hex color codes.
     *
     * @param string to convert.
     * @return {@link String}
     */
	public static String convertHexColorCodeStringToBukkitColorCodeString(String string) {
		Pattern pattern = Pattern.compile("(&?#[0-9a-fA-F]{6})");
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			int indexStart = matcher.start();
			int indexEnd = matcher.end();
			String hexColor = string.substring(indexStart, indexEnd);
			String bukkitColor = convertHexColorCodeToBukkitColorCode(hexColor);
			string = string.replaceAll(hexColor, bukkitColor);
			matcher.reset(string);
		}
		return string;
	}
	
	/**
     * Formats a string with both Spigot legacy colors codes and Spigot and
     * VentureChat hex color codes.
     *
     * @param string to format.
     * @return {@link String}
     */
	public static String FormatStringColor(String string) {
		String allFormated = string;
		allFormated = LEGACY_CHAT_COLOR_DIGITS_PATTERN.matcher(allFormated).replaceAll("\u00A7$1");

		allFormated = allFormated.replaceAll("&[x]", BUKKIT_COLOR_CODE_PREFIX + "x");
		allFormated = allFormated.replaceAll("&[aA]", BUKKIT_COLOR_CODE_PREFIX + "a");
		allFormated = allFormated.replaceAll("&[bB]", BUKKIT_COLOR_CODE_PREFIX + "b");
		allFormated = allFormated.replaceAll("&[cC]", BUKKIT_COLOR_CODE_PREFIX + "c");
		allFormated = allFormated.replaceAll("&[dD]", BUKKIT_COLOR_CODE_PREFIX + "d");
		allFormated = allFormated.replaceAll("&[eE]", BUKKIT_COLOR_CODE_PREFIX + "e");
		allFormated = allFormated.replaceAll("&[fF]", BUKKIT_COLOR_CODE_PREFIX + "f");

		allFormated = allFormated.replaceAll("%", "\\%");

		allFormated = convertHexColorCodeStringToBukkitColorCodeString(allFormated);
		return allFormated;
	}

	/**
     * Formats a string with only legacy Spigot color codes &[0-9a-f]. Does not
     * format the legacy color codes that make up a Spigot hex color code.
     *
     * @param string to format.
     * @return {@link String}
     */
	public static String FormatStringLegacyColor(String string) {
		String allFormated = string;

		allFormated = LEGACY_CHAT_COLOR_PATTERN.matcher(allFormated).replaceAll("\u00A7$13");
		allFormated = allFormated.replaceAll(BUKKIT_COLOR_CODE_PREFIX + "[A]", BUKKIT_COLOR_CODE_PREFIX + "a");
		allFormated = allFormated.replaceAll(BUKKIT_COLOR_CODE_PREFIX + "[B]", BUKKIT_COLOR_CODE_PREFIX + "b");
		allFormated = allFormated.replaceAll(BUKKIT_COLOR_CODE_PREFIX + "[C]", BUKKIT_COLOR_CODE_PREFIX + "c");
		allFormated = allFormated.replaceAll(BUKKIT_COLOR_CODE_PREFIX + "[D]", BUKKIT_COLOR_CODE_PREFIX + "d");
		allFormated = allFormated.replaceAll(BUKKIT_COLOR_CODE_PREFIX + "[E]", BUKKIT_COLOR_CODE_PREFIX + "e");
		allFormated = allFormated.replaceAll(BUKKIT_COLOR_CODE_PREFIX + "[F]", BUKKIT_COLOR_CODE_PREFIX + "f");

		allFormated = allFormated.replaceAll("%", "\\%");
		return allFormated;
	}

	/**
     * Formats a string with Spigot formatting codes.
     *
     * @param string to format.
     * @return {@link String}
     */
	public static String FormatString(String string) {
		String allFormated = string;
		allFormated = allFormated.replaceAll("&[kK]", BUKKIT_COLOR_CODE_PREFIX + "k");
		allFormated = allFormated.replaceAll("&[lL]", BUKKIT_COLOR_CODE_PREFIX + "l");
		allFormated = allFormated.replaceAll("&[mM]", BUKKIT_COLOR_CODE_PREFIX + "m");
		allFormated = allFormated.replaceAll("&[nN]", BUKKIT_COLOR_CODE_PREFIX + "n");
		allFormated = allFormated.replaceAll("&[oO]", BUKKIT_COLOR_CODE_PREFIX + "o");
		allFormated = allFormated.replaceAll("&[rR]", BUKKIT_COLOR_CODE_PREFIX + "r");

		allFormated = allFormated.replaceAll("%", "\\%");
		return allFormated;
	}

	/**
     * Formats a string with Spigot legacy colors codes, Spigot and VentureChat hex
     * color codes, and Spigot formatting codes.
     *
     * @param string to format.
     * @return {@link String}
     */
	public static String FormatStringAll(String string) {
		String allFormated = FormatString(string);
		allFormated = FormatStringColor(allFormated);
		return allFormated;
	}
	
	/**
     * Validates a hex color code.
     *
     * @param color to validate.
     * @return true if color code is valid, false otherwise.
     */
	public static boolean isValidHexColor(String color) {
		Pattern pattern = Pattern.compile("(^&?#[0-9a-fA-F]{6}\\b)");
		Matcher matcher = pattern.matcher(color);
		return matcher.find();
	}
	
	public static boolean isValidColor(String color) {
		Boolean bFound = false;
		for (ChatColor bkColors : ChatColor.values()) {
			if (color.equalsIgnoreCase(bkColors.name())) {
				bFound = true;
			}
		}
		return bFound;
	}

	public static String escapeAllRegex(String input) {
		return input.replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(")
				.replace(")", "\\)").replace("|", "\\|").replace("+", "\\+").replace("*", "\\*");
	}
	
	public static String parseTimeStringFromMillis(long millis) {
		String timeString = "";
		if(millis >= MILLISECONDS_PER_DAY) {
			long numberOfDays = millis / MILLISECONDS_PER_DAY;
			millis -= MILLISECONDS_PER_DAY * numberOfDays;
			
			String units = LocalizedMessage.UNITS_DAY_PLURAL.toString();
			if (numberOfDays == 1) {
				units = LocalizedMessage.UNITS_DAY_SINGULAR.toString();
			}
			timeString += numberOfDays + " " + units + " ";
		}
		
		if(millis >= MILLISECONDS_PER_HOUR) {
			long numberOfHours = millis / MILLISECONDS_PER_HOUR;
			millis -= MILLISECONDS_PER_HOUR * numberOfHours;

			String units = LocalizedMessage.UNITS_HOUR_PLURAL.toString();
			if (numberOfHours == 1) {
				units = LocalizedMessage.UNITS_HOUR_SINGULAR.toString();
			}
			timeString += numberOfHours + " " + units + " ";
		}
		
		if(millis >= MILLISECONDS_PER_MINUTE) {
			long numberOfMinutes = millis / MILLISECONDS_PER_MINUTE;
			millis -= MILLISECONDS_PER_MINUTE * numberOfMinutes;

			String units = LocalizedMessage.UNITS_MINUTE_PLURAL.toString();
			if (numberOfMinutes == 1) {
				units = LocalizedMessage.UNITS_MINUTE_SINGULAR.toString();
			}
			timeString += numberOfMinutes + " " + units + " ";
		}
		
		if(millis >= MILLISECONDS_PER_SECOND) {
			long numberOfSeconds = millis / MILLISECONDS_PER_SECOND;
			millis -= MILLISECONDS_PER_SECOND * numberOfSeconds;

			String units = LocalizedMessage.UNITS_SECOND_PLURAL.toString();
			if (numberOfSeconds == 1) {
				units = LocalizedMessage.UNITS_SECOND_SINGULAR.toString();
			}
			timeString += numberOfSeconds + " " + units;
		}
		return timeString.trim();
	}
	
	public static long parseTimeStringToMillis(String timeInput) {
		long millis = 0L;
		timeInput = timeInput.toLowerCase();
		char validChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'd', 'h', 'm', 's' };
		if(containsInvalidChars(validChars, timeInput)) {
			return -1;
		}
		
		long countDayTokens = timeInput.chars().filter(ch -> ch == 'd').count();
		long countHourTokens = timeInput.chars().filter(ch -> ch == 'h').count();
		long countMinuteTokens = timeInput.chars().filter(ch -> ch == 'm').count();
		long countSecondTokens = timeInput.chars().filter(ch -> ch == 's').count();
		if(countDayTokens > 1 || countHourTokens > 1 || countMinuteTokens > 1 || countSecondTokens > 1) {
			return -1;
		}
		
		int indexOfSecondToken = timeInput.indexOf("s");
		int indexOfMinuteToken = timeInput.indexOf("m");
		int indexOfHourToken = timeInput.indexOf("h");
		int indexOfDayToken = timeInput.indexOf("d");
		if(indexOfDayToken != -1) {
			if((indexOfHourToken != -1 && indexOfHourToken < indexOfDayToken) || (indexOfMinuteToken != -1 && indexOfMinuteToken < indexOfDayToken) || (indexOfSecondToken != -1 && indexOfSecondToken < indexOfDayToken)) {
				return -1;
			}
		}
		if(indexOfHourToken != -1) {
			if((indexOfMinuteToken != -1 && indexOfMinuteToken < indexOfHourToken) || (indexOfSecondToken != -1 && indexOfSecondToken < indexOfHourToken)) {
				return -1;
			}
		}
		if(indexOfMinuteToken != -1) {
			if((indexOfSecondToken != -1 && indexOfSecondToken < indexOfMinuteToken)) {
				return -1;
			}
		}
		
		if(indexOfDayToken != -1) {
			int numberOfDays = Integer.parseInt(timeInput.substring(0, indexOfDayToken));
			timeInput = timeInput.substring(indexOfDayToken + 1);
			millis += MILLISECONDS_PER_DAY * numberOfDays;
		}
		if(timeInput.length() > 0) {
			indexOfHourToken = timeInput.indexOf("h");
			if(indexOfHourToken != -1) {
				int numberOfHours = Integer.parseInt(timeInput.substring(0, indexOfHourToken));
				timeInput = timeInput.substring(indexOfHourToken + 1);
				millis += MILLISECONDS_PER_HOUR * numberOfHours;
			}
		}
		if(timeInput.length() > 0) {
			indexOfMinuteToken = timeInput.indexOf("m");
			if(indexOfMinuteToken != -1) {
				int numberOfMinutes = Integer.parseInt(timeInput.substring(0, indexOfMinuteToken));
				timeInput = timeInput.substring(indexOfMinuteToken + 1);
				millis += MILLISECONDS_PER_MINUTE * numberOfMinutes;
			}
		}
		if(timeInput.length() > 0) {
			indexOfSecondToken = timeInput.indexOf("s");
			if(indexOfSecondToken != -1) {
				int numberOfSeconds = Integer.parseInt(timeInput.substring(0, indexOfSecondToken));
				timeInput = timeInput.substring(indexOfSecondToken + 1);
				millis += MILLISECONDS_PER_SECOND * numberOfSeconds;
			}
		}
		return millis;
	}
	
	private static boolean containsInvalidChars(char[] validChars, String validate) {
		for(char c : validate.toCharArray()) {
			boolean isValidChar = false;
			for(char v : validChars) {
				if(c == v) {
					isValidChar = true;
				}
			}
			if(!isValidChar) {
				return true;
			}
		}
		return false;
	}
	
	public static String stripColor(String message) {
		return message.replaceAll("(\u00A7([a-z0-9]))", "");
	}
	
	/**
	 * Returns whether the passed UUID is a v3 UUID. Offline UUIDs are v3, online
	 * are v4.
	 *
	 * @param uuid the UUID to check
	 * @return whether the UUID is a v3 UUID & thus is offline
	 */
	public static boolean uuidIsOffline(UUID uuid) {
		return uuid.version() == 3;
	}
}
