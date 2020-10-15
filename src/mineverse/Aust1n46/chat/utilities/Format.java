package mineverse.Aust1n46.chat.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.json.JsonFormat;
import mineverse.Aust1n46.chat.versions.VersionHandler;

/**
 * Class containing chat formatting methods.
 */
public class Format {
	private static MineverseChat plugin = MineverseChat.getInstance();

	public static final int LEGACY_COLOR_CODE_LENGTH = 2;
	public static final int HEX_COLOR_CODE_LENGTH = 14;
	public static final String DEFAULT_COLOR_CODE = String.valueOf(ChatColor.WHITE);
	public static final String HEX_COLOR_CODE_PREFIX = "#";
	public static final char BUKKIT_COLOR_CODE_PREFIX_CHAR = '\u00A7';
	public static final String BUKKIT_COLOR_CODE_PREFIX = String.valueOf(BUKKIT_COLOR_CODE_PREFIX_CHAR);
	public static final String BUKKIT_HEX_COLOR_CODE_PREFIX = "x";

	private static final Pattern LEGACY_CHAT_COLOR_DIGITS_PATTERN = Pattern.compile("&([0-9])");
	private static final Pattern LEGACY_CHAT_COLOR_PATTERN = Pattern.compile(
			"(?<!(&x(&[a-fA-F0-9]){5}))(?<!(&x(&[a-fA-F0-9]){4}))(?<!(&x(&[a-fA-F0-9]){3}))(?<!(&x(&[a-fA-F0-9]){2}))(?<!(&x(&[a-fA-F0-9]){1}))(?<!(&x))(&)([0-9a-fA-F])");

	/**
	 * Converts a message to Minecraft JSON formatting while applying the
	 * {@link JsonFormat} from the config.
	 * 
	 * @param sender
	 *            {@link MineverseChatPlayer} wrapper of the message sender.
	 * @param format
	 *            The format section of the message.
	 * @param chat
	 *            The chat section of the message.
	 * @return {@link String}
	 */
	public static String convertToJson(MineverseChatPlayer sender, String format, String chat) {
		JsonFormat JSONformat = MineverseChat.jfInfo.getJsonFormat(sender.getJsonFormat());
		String f = format.replace("\\", "\\\\").replace("\"", "\\\"");
		String c = chat.replace("\\", "\\\\").replace("\"", "\\\"");
		String json = "[\"\",{\"text\":\"\",\"extra\":[";
		String prefix = "";
		String suffix = "";
		try {
			prefix = FormatStringAll(MineverseChat.chat.getPlayerPrefix(sender.getPlayer()));
			suffix = FormatStringAll(MineverseChat.chat.getPlayerSuffix(sender.getPlayer()));
			// Don't apply JSON if the prefix or suffix is just a color code
			if (suffix.isEmpty() || (suffix.length() == 2 && suffix.substring(1).matches("[0-9a-fA-F]"))) {
				suffix = "venturechat_no_suffix_code";
			}
			if (prefix.isEmpty() || (prefix.length() == 2 && prefix.substring(1).matches("[0-9a-fA-F]"))) {
				prefix = "venturechat_no_prefix_code";
			}
		} catch (Exception e) {
			System.out.println("Exception?" + e.getLocalizedMessage());
			if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll(
						"&8[&eVentureChat&8]&e - Prefix and / or suffix don't exist, setting to nothing."));
			}
			suffix = "venturechat_no_suffix_code";
			prefix = "venturechat_no_prefix_code";
		}
		String nickname = "";
		if (sender.getPlayer() != null) {
			nickname = FormatStringAll(sender.getPlayer().getDisplayName());
		}
		json += convertPlaceholders(f, JSONformat, prefix, nickname, suffix, sender);
		json += "]}";
		json += "," + convertLinks(c);
		json += "]";
		if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			System.out.println(json);
			System.out.println("END OF JSON");
			System.out.println("END OF JSON");
			System.out.println("END OF JSON");
			System.out.println("END OF JSON");
			System.out.println("END OF JSON");
		}
		return json;
	}

	/**
	 * Converts the format section of a message to JSON using PlaceholderAPI.
	 * 
	 * @param s
	 * @param format
	 * @param prefix
	 * @param nickname
	 * @param suffix
	 * @param icp
	 * @return {@link String}
	 */
	private static String convertPlaceholders(String s, JsonFormat format, String prefix, String nickname,
			String suffix, MineverseChatPlayer icp) {
		String remaining = s;
		String temp = "";
		int indexStart = -1;
		int indexEnd = -1;
		String placeholder = "";
		String lastCode = DEFAULT_COLOR_CODE;
		do {
			Pattern pattern = Pattern.compile(
					"(" + escapeAllRegex(prefix) + "|" + escapeAllRegex(nickname) + "|" + escapeAllRegex(suffix) + ")");
			Matcher matcher = pattern.matcher(remaining);
			if (matcher.find()) {
				indexStart = matcher.start();
				indexEnd = matcher.end();
				placeholder = remaining.substring(indexStart, indexEnd);
				temp += convertToJsonColors(lastCode + remaining.substring(0, indexStart)) + ",";
				lastCode = getLastCode(lastCode + remaining.substring(0, indexStart));
				String action = "";
				String text = "";
				String hover = "";
				if (placeholder.contains(prefix)) {
					action = format.getClickPrefix();
					text = Format.FormatStringAll(
							PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickPrefixText()));
					for (String st : format.getHoverTextPrefix()) {
						hover += Format.FormatStringAll(st) + "\n";
					}
				}
				if (placeholder.contains(nickname)) {
					action = format.getClickName();
					text = Format.FormatStringAll(
							PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickNameText()));
					for (String st : format.getHoverTextName()) {
						hover += Format.FormatStringAll(st) + "\n";
					}
				}
				if (placeholder.contains(suffix)) {
					action = format.getClickSuffix();
					text = Format.FormatStringAll(
							PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickSuffixText()));
					for (String st : format.getHoverTextSuffix()) {
						hover += Format.FormatStringAll(st) + "\n";
					}
				}
				hover = Format.FormatStringAll(
						PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), hover.substring(0, hover.length() - 1)));
				temp += convertToJsonColors(lastCode + placeholder,
						",\"clickEvent\":{\"action\":\"" + action + "\",\"value\":\"" + text
								+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
								+ convertToJsonColors(hover) + "]}}")
						+ ",";
				lastCode = getLastCode(lastCode + placeholder);
				remaining = remaining.substring(indexEnd);
			} else {
				temp += convertToJsonColors(lastCode + remaining);
				break;
			}
		} while (true);
		return temp;
	}

	/**
	 * Converts URL's to JSON.
	 * 
	 * @param s
	 * @return {@link String}
	 */
	private static String convertLinks(String s) {
		String remaining = s;
		String temp = "";
		int indexLink = -1;
		int indexLinkEnd = -1;
		String link = "";
		String lastCode = DEFAULT_COLOR_CODE;
		do {
			Pattern pattern = Pattern.compile(
					"([a-zA-Z0-9" + BUKKIT_COLOR_CODE_PREFIX + "\\-:/]+\\.[a-zA-Z/0-9" + BUKKIT_COLOR_CODE_PREFIX
							+ "\\-:_#]+(\\.[a-zA-Z/0-9." + BUKKIT_COLOR_CODE_PREFIX + "\\-:#\\?\\+=_]+)?)");
			Matcher matcher = pattern.matcher(remaining);
			if (matcher.find()) {
				indexLink = matcher.start();
				indexLinkEnd = matcher.end();
				link = remaining.substring(indexLink, indexLinkEnd);
				temp += convertToJsonColors(lastCode + remaining.substring(0, indexLink)) + ",";
				lastCode = getLastCode(lastCode + remaining.substring(0, indexLink));
				String https = "";
				if (ChatColor.stripColor(link).contains("https://"))
					https = "s";
				temp += convertToJsonColors(lastCode + link,
						",\"underlined\":\"" + underlineURLs()
								+ "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http" + https + "://"
								+ ChatColor.stripColor(link.replace("http://", "").replace("https://", ""))
								+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
								+ convertToJsonColors(lastCode + link) + "]}}")
						+ ",";
				lastCode = getLastCode(lastCode + link);
				remaining = remaining.substring(indexLinkEnd);
			} else {
				temp += convertToJsonColors(lastCode + remaining);
				break;
			}
		} while (true);
		return temp;
	}

	public static String getLastCode(String s) {
		String ts = "";
		char[] ch = s.toCharArray();
		for (int a = 0; a < s.length() - 1; a++) {
			if (String.valueOf(ch[a + 1]).matches("[lkomnLKOMN]") && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
				ts += String.valueOf(ch[a]) + ch[a + 1];
				a++;
			} else if (String.valueOf(ch[a + 1]).matches("[0123456789abcdefrABCDEFR]")
					&& ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
				ts = String.valueOf(ch[a]) + ch[a + 1];
				a++;
			} else if (ch[a + 1] == 'x' && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
				if (ch.length > a + 13) {
					if (String.valueOf(ch[a + 3]).matches("[0123456789abcdefABCDEF]")
							&& String.valueOf(ch[a + 5]).matches("[0123456789abcdefABCDEF]")
							&& String.valueOf(ch[a + 7]).matches("[0123456789abcdefABCDEF]")
							&& String.valueOf(ch[a + 9]).matches("[0123456789abcdefABCDEF]")
							&& String.valueOf(ch[a + 11]).matches("[0123456789abcdefABCDEF]")
							&& String.valueOf(ch[a + 13]).matches("[0123456789abcdefABCDEF]")
							&& ch[a + 2] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 4] == BUKKIT_COLOR_CODE_PREFIX_CHAR
							&& ch[a + 6] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 8] == BUKKIT_COLOR_CODE_PREFIX_CHAR
							&& ch[a + 10] == BUKKIT_COLOR_CODE_PREFIX_CHAR
							&& ch[a + 12] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
						ts = String.valueOf(ch[a]) + ch[a + 1] + ch[a + 2] + ch[a + 3] + ch[a + 4] + ch[a + 5]
								+ ch[a + 6] + ch[a + 7] + ch[a + 8] + ch[a + 9] + ch[a + 10] + ch[a + 11] + ch[a + 12]
								+ ch[a + 13];
						a += 13;
					}
				}
			}
		}
		return ts;
	}

	/**
	 * Converts a message to JSON colors with no additional JSON extensions.
	 * 
	 * @param s
	 * @return {@link String}
	 */
	private static String convertToJsonColors(String s) {
		return convertToJsonColors(s, "");
	}

	/**
	 * Converts a message to JSON colors with additional JSON extensions.
	 * 
	 * @param s
	 * @param extensions
	 * @return {@link String}
	 */
	private static String convertToJsonColors(String s, String extensions) {
		String remaining = s;
		String temp = "";
		int indexColor = -1;
		int indexNextColor = -1;
		String color = "";
		String modifier = "";
		boolean bold = false;
		boolean obfuscated = false;
		boolean italic = false;
		boolean strikethrough = false;
		boolean underlined = false;
		String previousColor = "";
		int colorLength = LEGACY_COLOR_CODE_LENGTH;
		do {
			if (remaining.length() < LEGACY_COLOR_CODE_LENGTH) {
				temp = "{\"text\":\"" + remaining + "\"},";
				break;
			}
			modifier = "";
			indexColor = remaining.indexOf(BUKKIT_COLOR_CODE_PREFIX);
			previousColor = color;

			color = remaining.substring(1, indexColor + LEGACY_COLOR_CODE_LENGTH);
			if (color.equals(BUKKIT_HEX_COLOR_CODE_PREFIX)) {
				if (remaining.length() >= HEX_COLOR_CODE_LENGTH) {
					color = HEX_COLOR_CODE_PREFIX
							+ remaining.substring(LEGACY_COLOR_CODE_LENGTH, indexColor + HEX_COLOR_CODE_LENGTH)
									.replace(BUKKIT_COLOR_CODE_PREFIX, "");
					colorLength = HEX_COLOR_CODE_LENGTH;
					bold = false;
					obfuscated = false;
					italic = false;
					strikethrough = false;
					underlined = false;
				}
			} else if (!color.matches("[0123456789abcdefABCDEF]")) {
				switch (color) {
				case "l":
				case "L": {
					bold = true;
					break;
				}
				case "k":
				case "K": {
					obfuscated = true;
					break;
				}
				case "o":
				case "O": {
					italic = true;
					break;
				}
				case "m":
				case "M": {
					strikethrough = true;
					break;
				}
				case "n":
				case "N": {
					underlined = true;
					break;
				}
				case "r":
				case "R": {
					bold = false;
					obfuscated = false;
					italic = false;
					strikethrough = false;
					underlined = false;
					color = "f";
					break;
				}
				}
				if (!color.equals("f"))
					color = previousColor;
				if (color.length() == 0)
					color = "f";
			} else {
				bold = false;
				obfuscated = false;
				italic = false;
				strikethrough = false;
				underlined = false;
			}
			if (bold)
				modifier += ",\"bold\":\"true\"";
			if (obfuscated)
				modifier += ",\"obfuscated\":\"true\"";
			if (italic)
				modifier += ",\"italic\":\"true\"";
			if (underlined)
				modifier += ",\"underlined\":\"true\"";
			if (strikethrough)
				modifier += ",\"strikethrough\":\"true\"";
			remaining = remaining.substring(colorLength);
			colorLength = LEGACY_COLOR_CODE_LENGTH;
			indexNextColor = remaining.indexOf(BUKKIT_COLOR_CODE_PREFIX);
			if (indexNextColor == -1) {
				indexNextColor = remaining.length();
			}
			temp += "{\"text\":\"" + remaining.substring(0, indexNextColor) + "\",\"color\":\""
					+ hexidecimalToJsonColorRGB(color) + "\"" + modifier + extensions + "},";
			remaining = remaining.substring(indexNextColor);
		} while (remaining.length() > 1 && indexColor != -1);
		if (temp.length() > 1)
			temp = temp.substring(0, temp.length() - 1);
		return temp;
	}

	private static String hexidecimalToJsonColorRGB(String c) {
		if (c.length() == 1) {
			switch (c) {
			case "0":
				return "black";
			case "1":
				return "dark_blue";
			case "2":
				return "dark_green";
			case "3":
				return "dark_aqua";
			case "4":
				return "dark_red";
			case "5":
				return "dark_purple";
			case "6":
				return "gold";
			case "7":
				return "gray";
			case "8":
				return "dark_gray";
			case "9":
				return "blue";
			case "a":
			case "A":
				return "green";
			case "b":
			case "B":
				return "aqua";
			case "c":
			case "C":
				return "red";
			case "d":
			case "D":
				return "light_purple";
			case "e":
			case "E":
				return "yellow";
			case "f":
			case "F":
				return "white";
			default:
				return "white";
			}
		}
		if (isValidHexColor(c)) {
			return c;
		}
		return "white";
	}

	public static String convertPlainTextToJson(String s, boolean convertURL) {
		if (convertURL) {
			return "[" + Format.convertLinks(s) + "]";
		} else {
			return "[" + convertToJsonColors(DEFAULT_COLOR_CODE + s) + "]";
		}
	}

	public static String formatModerationGUI(String json, Player player, String sender, String channelName, int hash) {
		if (player.hasPermission("venturechat.gui")) {
			json = json.substring(0, json.length() - 1);
			json += "," + Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("guiicon")),
					",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vchatgui " + sender + " " + channelName
							+ " " + hash
							+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
							+ Format.convertToJsonColors(
									Format.FormatStringAll(plugin.getConfig().getString("guitext")))
							+ "]}}")
					+ "]";
		}
		return json;
	}

	public static PacketContainer createPacketPlayOutChat(String json) {
		WrappedChatComponent component = WrappedChatComponent.fromJson(json);
		PacketContainer container = new PacketContainer(PacketType.Play.Server.CHAT);
		container.getModifier().writeDefaults();
		container.getChatComponents().write(0, component);
		return container;
	}

	public static PacketContainer createPacketPlayOutChat(WrappedChatComponent component) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.CHAT);
		container.getModifier().writeDefaults();
		container.getChatComponents().write(0, component);
		return container;
	}

	public static void sendPacketPlayOutChat(Player player, PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String toPlainText(Object o, Class<?> c) {
		List<Object> finalList = new ArrayList<>();
		StringBuilder stringbuilder = new StringBuilder();
		try {
			splitComponents(finalList, o, c);
			for (Object component : finalList) {
				if (VersionHandler.is1_7_10()) {
					stringbuilder.append((String) component.getClass().getMethod("e").invoke(component));
				} else {
					stringbuilder.append((String) component.getClass().getMethod("getText").invoke(component));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
		// System.out.println("my string");
		// System.out.println("my string");
		// System.out.println("my string");
		// System.out.println("my string");
		// System.out.println("my string");
		// System.out.println(stringbuilder.toString());
		// }
		return stringbuilder.toString();
	}

	private static void splitComponents(List<Object> finalList, Object o, Class<?> c) throws Exception {
		// if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
		// for(Method m : c.getMethods()) {
		// System.out.println(m.getName());
		// }
		// }
		if (VersionHandler.is1_7() || VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10()
				|| VersionHandler.is1_11() || VersionHandler.is1_12() || VersionHandler.is1_13()
				|| (VersionHandler.is1_14() && !VersionHandler.is1_14_4())) {
			ArrayList<?> list = (ArrayList<?>) c.getMethod("a").invoke(o, new Object[0]);
			for (Object component : list) {
				ArrayList<?> innerList = (ArrayList<?>) c.getMethod("a").invoke(component, new Object[0]);
				if (innerList.size() > 0) {
					splitComponents(finalList, component, c);
				} else {
					finalList.add(component);
				}
			}
		} else {
			ArrayList<?> list = (ArrayList<?>) c.getMethod("getSiblings").invoke(o, new Object[0]);
			for (Object component : list) {
				ArrayList<?> innerList = (ArrayList<?>) c.getMethod("getSiblings").invoke(component, new Object[0]);
				if (innerList.size() > 0) {
					splitComponents(finalList, component, c);
				} else {
					finalList.add(component);
				}
			}
		}
	}

	/**
	 * Formats a string with both Spigot legacy colors codes and Spigot and
	 * VentureChat hex color codes.
	 * 
	 * @param string
	 *            to format.
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
	 * @param string
	 *            to format.
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
	 * @param string
	 *            to format.
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
	 * @param string
	 *            to format.
	 * @return {@link String}
	 */
	public static String FormatStringAll(String string) {
		String allFormated = Format.FormatString(string);
		allFormated = Format.FormatStringColor(allFormated);
		return allFormated;
	}

	public static String FilterChat(String msg) {
		int t = 0;
		List<String> filters = plugin.getConfig().getStringList("filters");
		for (String s : filters) {
			t = 0;
			String[] pparse = new String[2];
			pparse[0] = " ";
			pparse[1] = " ";
			StringTokenizer st = new StringTokenizer(s, ",");
			while (st.hasMoreTokens()) {
				if (t < 2) {
					pparse[t++] = st.nextToken();
				}
			}
			msg = msg.replaceAll("(?i)" + pparse[0], pparse[1]);
		}
		return msg;
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

	/**
	 * Validates a hex color code.
	 * 
	 * @param color
	 *            to validate.
	 * @return true if color code is valid, false otherwise.
	 */
	public static boolean isValidHexColor(String color) {
		Pattern pattern = Pattern.compile("(^&?#[0-9a-fA-F]{6}\\b)");
		Matcher matcher = pattern.matcher(color);
		return matcher.find();
	}

	/**
	 * Convert a single hex color code to a single Bukkit hex color code.
	 * 
	 * @param color
	 *            to convert.
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
	 * @param string
	 *            to convert.
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

	public static String escapeAllRegex(String input) {
		return input.replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(")
				.replace(")", "\\)").replace("|", "\\|").replace("+", "\\+").replace("*", "\\*");
	}

	public static boolean underlineURLs() {
		return plugin.getConfig().getBoolean("underlineurls", true);
	}

	public static int currentTimeMillis() {
		return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}
}
