package mineverse.Aust1n46.chat.utilities;

import static mineverse.Aust1n46.chat.MineverseChat.getInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.ClickAction;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.json.JsonAttribute;
import mineverse.Aust1n46.chat.json.JsonFormat;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.versions.VersionHandler;

/**
 * Class containing chat formatting methods.
 */
public class Format {
	public static final int LEGACY_COLOR_CODE_LENGTH = 2;
	public static final int HEX_COLOR_CODE_LENGTH = 14;
	public static final String HEX_COLOR_CODE_PREFIX = "#";
	public static final char BUKKIT_COLOR_CODE_PREFIX_CHAR = '\u00A7';
	public static final String BUKKIT_COLOR_CODE_PREFIX = String.valueOf(BUKKIT_COLOR_CODE_PREFIX_CHAR);
	public static final String BUKKIT_HEX_COLOR_CODE_PREFIX = "x";
	public static final String DEFAULT_COLOR_CODE = BUKKIT_COLOR_CODE_PREFIX + "f";

	private static final Pattern LEGACY_CHAT_COLOR_DIGITS_PATTERN = Pattern.compile("&([0-9])");
	private static final Pattern LEGACY_CHAT_COLOR_PATTERN = Pattern.compile(
			"(?<!(&x(&[a-fA-F0-9]){5}))(?<!(&x(&[a-fA-F0-9]){4}))(?<!(&x(&[a-fA-F0-9]){3}))(?<!(&x(&[a-fA-F0-9]){2}))(?<!(&x(&[a-fA-F0-9]){1}))(?<!(&x))(&)([0-9a-fA-F])");
	
	private static final Pattern PLACEHOLDERAPI_PLACEHOLDER_PATTERN = Pattern.compile("\\{([^\\{\\}]+)\\}");
	
	public static final long MILLISECONDS_PER_DAY = 86400000;
	public static final long MILLISECONDS_PER_HOUR = 3600000;
	public static final long MILLISECONDS_PER_MINUTE = 60000;
	public static final long MILLISECONDS_PER_SECOND = 1000;
	
	public static final String DEFAULT_MESSAGE_SOUND = "ENTITY_PLAYER_LEVELUP";
	public static final String DEFAULT_LEGACY_MESSAGE_SOUND = "LEVEL_UP";

	/**
     * Converts a message to Minecraft JSON formatting while applying the
     * {@link JsonFormat} from the config.
     *
     * @param sender {@link MineverseChatPlayer} wrapper of the message sender.
     * @param format The format section of the message.
     * @param chat   The chat section of the message.
     * @return {@link String}
     */
	public static String convertToJson(MineverseChatPlayer sender, String format, String chat) {
		JsonFormat JSONformat = JsonFormat.getJsonFormat(sender.getJsonFormat());
		String c = escapeJsonChars(chat);
		String json = "[\"\",{\"text\":\"\",\"extra\":[";
		json += convertPlaceholders(format, JSONformat, sender);
		json += "]}";
		json += "," + convertLinks(c);
		json += "]";
		if (getInstance().getConfig().getString("loglevel", "info").equals("debug")) {
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
	private static String convertPlaceholders(String s, JsonFormat format, MineverseChatPlayer icp) {
		String remaining = s;
		String temp = "";
		int indexStart = -1;
		int indexEnd = -1;
		String placeholder = "";
		String formattedPlaceholder = "";
		String lastCode = DEFAULT_COLOR_CODE;
		do {
			Matcher matcher = PLACEHOLDERAPI_PLACEHOLDER_PATTERN.matcher(remaining);
			if (matcher.find()) {
				indexStart = matcher.start();
				indexEnd = matcher.end();
				placeholder = remaining.substring(indexStart, indexEnd);
				formattedPlaceholder = escapeJsonChars(Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), placeholder)));
				temp += convertToJsonColors(escapeJsonChars(lastCode + remaining.substring(0, indexStart))) + ",";
				lastCode = getLastCode(lastCode + remaining.substring(0, indexStart));
				boolean placeholderHasJsonAttribute = false;
				for (JsonAttribute jsonAttribute : format.getJsonAttributes()) {
					if (placeholder.contains(jsonAttribute.getName().replace("{", "").replace("}", ""))) {
						final StringBuilder hover = new StringBuilder();
						for (String st : jsonAttribute.getHoverText()) {
							hover.append(Format.FormatStringAll(st) + "\n");
						}
						final String hoverText;
						if(!hover.isEmpty()) {
							hoverText = escapeJsonChars(Format.FormatStringAll(
									PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), hover.substring(0, hover.length() - 1))));
						} else {
							hoverText = StringUtils.EMPTY;
						}
						final ClickAction clickAction = jsonAttribute.getClickAction();
						final String actionJson;
						if (clickAction == ClickAction.NONE) {
							actionJson = StringUtils.EMPTY;
						} else {
							final String clickText = escapeJsonChars(Format.FormatStringAll(
									PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), jsonAttribute.getClickText())));
							actionJson = ",\"clickEvent\":{\"action\":\"" + jsonAttribute.getClickAction().toString() + "\",\"value\":\"" + clickText
							+ "\"}";
						}
						final String hoverJson;
						if (hoverText.isEmpty()) {
							hoverJson = StringUtils.EMPTY;
						} else {
							hoverJson = ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
									+ convertToJsonColors(hoverText) + "]}}";
						}
						temp += convertToJsonColors(lastCode + formattedPlaceholder, actionJson + hoverJson) + ",";
						placeholderHasJsonAttribute = true;
						break;
					}
				}
				if (!placeholderHasJsonAttribute) {
					temp += convertToJsonColors(lastCode + formattedPlaceholder) + ",";
				}
				lastCode = getLastCode(lastCode + formattedPlaceholder);
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
							+ "\\-:_#]+(\\.[a-zA-Z/0-9." + BUKKIT_COLOR_CODE_PREFIX + "\\-:;,#\\?\\+=_]+)?)");
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
						",\"underlined\":" + underlineURLs()
								+ ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http" + https + "://"
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
	public static String convertToJsonColors(String s) {
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
				if (VersionHandler.isAtLeast_1_20_4()) {
					modifier += ",\"bold\":true";
				} else {
					modifier += ",\"bold\":\"true\"";
				}
			if (obfuscated)
				if (VersionHandler.isAtLeast_1_20_4()) {
					modifier += ",\"obfuscated\":true";
				} else {
					modifier += ",\"obfuscated\":\"true\"";
				}
			if (italic)
				if (VersionHandler.isAtLeast_1_20_4()) {
					modifier += ",\"italic\":true";
				} else {
					modifier += ",\"italic\":\"true\"";
				}
			if (underlined)
				if (VersionHandler.isAtLeast_1_20_4()) {
					modifier += ",\"underlined\":true";
				} else {
					modifier += ",\"underlined\":\"true\"";
				}
			if (strikethrough)
				if (VersionHandler.isAtLeast_1_20_4()) {
					modifier += ",\"strikethrough\":true";
				} else {
					modifier += ",\"strikethrough\":\"true\"";
				}
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
		s = escapeJsonChars(s);
		if (convertURL) {
			return "[" + Format.convertLinks(s) + "]";
		} else {
			return "[" + convertToJsonColors(DEFAULT_COLOR_CODE + s) + "]";
		}
	}
	
	private static String escapeJsonChars(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	public static String formatModerationGUI(String json, Player player, String sender, String channelName, int hash) {
		if (player.hasPermission("venturechat.gui")) {
			json = json.substring(0, json.length() - 1);
			json += "," + Format.convertToJsonColors(Format.FormatStringAll(getInstance().getConfig().getString("guiicon")),
					",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vchatgui " + sender + " " + channelName
							+ " " + hash
							+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
							+ Format.convertToJsonColors(
									Format.FormatStringAll(getInstance().getConfig().getString("guitext")))
							+ "]}}")
					+ "]";
		}
		return json;
	}

	public static PacketContainer createPacketPlayOutChat(String json) {
		final PacketContainer container;
		if (VersionHandler.isAtLeast_1_20_4()) { // 1.20.4+
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getChatComponents().write(0, WrappedChatComponent.fromJson(json));
			container.getBooleans().write(0, false);
		} else if (VersionHandler.isAbove_1_19()) { // 1.19.1 -> 1.20.3
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getStrings().write(0, json);
			container.getBooleans().write(0, false);
		} else if (VersionHandler.isUnder_1_19()) { // 1.7 -> 1.19
			WrappedChatComponent component = WrappedChatComponent.fromJson(json);
			container = new PacketContainer(PacketType.Play.Server.CHAT);
			container.getModifier().writeDefaults();
			container.getChatComponents().write(0, component);
		} else { // 1.19
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getStrings().write(0, json);
			container.getIntegers().write(0, 1);
		}
		return container;
	}

	public static PacketContainer createPacketPlayOutChat(WrappedChatComponent component) {
		final PacketContainer container;
		if (VersionHandler.isAtLeast_1_20_4()) { // 1.20.4+
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getChatComponents().write(0, component);
			container.getBooleans().write(0, false);
		} else if (VersionHandler.isAbove_1_19()) { // 1.19.1 -> 1.20.3
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getStrings().write(0, component.getJson());
			container.getBooleans().write(0, false);
		} else if (VersionHandler.isUnder_1_19()) { // 1.7 -> 1.19
			container = new PacketContainer(PacketType.Play.Server.CHAT);
			container.getModifier().writeDefaults();
			container.getChatComponents().write(0, component);
		} else { // 1.19
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getStrings().write(0, component.getJson());
			container.getIntegers().write(0, 1);
		}
		return container;
	}

	public static void sendPacketPlayOutChat(Player player, PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String toColoredText(Object o, Class<?> c) {
		if (VersionHandler.is1_7()) {
			return "\"extra\":[{\"text\":\"Hover to see original message is not currently supported in 1.7\",\"color\":\"red\"}]";
		} 
		List<Object> finalList = new ArrayList<>();
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("\"extra\":[");
		try {
			splitComponents(finalList, o, c);
			for (Object component : finalList) {		
				try {
					if (VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12() || VersionHandler.is1_13() || VersionHandler.is1_14() || VersionHandler.is1_15() || VersionHandler.is1_16() || VersionHandler.is1_17()) {
						String text = (String) component.getClass().getMethod("getText").invoke(component);
						Object chatModifier = component.getClass().getMethod("getChatModifier").invoke(component);
						Object color = chatModifier.getClass().getMethod("getColor").invoke(chatModifier);
						String colorString = "white";
						if (color != null ) {
							colorString = color.getClass().getMethod("b").invoke(color).toString();
						}
						boolean bold = (boolean) chatModifier.getClass().getMethod("isBold").invoke(chatModifier);
						boolean strikethrough = (boolean) chatModifier.getClass().getMethod("isStrikethrough").invoke(chatModifier);
						boolean italic = (boolean) chatModifier.getClass().getMethod("isItalic").invoke(chatModifier);
						boolean underlined = (boolean) chatModifier.getClass().getMethod("isUnderlined").invoke(chatModifier);
						boolean obfuscated = (boolean) chatModifier.getClass().getMethod("isRandom").invoke(chatModifier);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("text", text);
						jsonObject.put("color", colorString);
						jsonObject.put("bold", bold);
						jsonObject.put("strikethrough", strikethrough);
						jsonObject.put("italic", italic);
						jsonObject.put("underlined", underlined);
						jsonObject.put("obfuscated", obfuscated);
						stringbuilder.append(jsonObject.toJSONString() + ",");
					} else {
						String text = (String) component.getClass().getMethod("getString").invoke(component);
						Object chatModifier = component.getClass().getMethod("c").invoke(component);
						Object color = chatModifier.getClass().getMethod("a").invoke(chatModifier);
						String colorString = "white";
						if (color != null ) {
							colorString = color.getClass().getMethod("b").invoke(color).toString();
						}
						boolean bold = (boolean) chatModifier.getClass().getMethod("b").invoke(chatModifier);
						boolean italic = (boolean) chatModifier.getClass().getMethod("c").invoke(chatModifier);
						boolean strikethrough = (boolean) chatModifier.getClass().getMethod("d").invoke(chatModifier);
						boolean underlined = (boolean) chatModifier.getClass().getMethod("e").invoke(chatModifier);
						boolean obfuscated = (boolean) chatModifier.getClass().getMethod("f").invoke(chatModifier);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("text", text);
						jsonObject.put("color", colorString);
						jsonObject.put("bold", bold);
						jsonObject.put("strikethrough", strikethrough);
						jsonObject.put("italic", italic);
						jsonObject.put("underlined", underlined);
						jsonObject.put("obfuscated", obfuscated);
						stringbuilder.append(jsonObject.toJSONString() + ",");
					}
				}
				catch(Exception e) {
					return "\"extra\":[{\"text\":\"Something went wrong. Could not access color.\",\"color\":\"red\"}]";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String coloredText = stringbuilder.toString();
		if(coloredText.endsWith(",")) {
			coloredText = coloredText.substring(0, coloredText.length() - 1);
		}
		coloredText += "]";
		return coloredText;
	}

	public static String toPlainText(Object o, Class<?> c) {
		List<Object> finalList = new ArrayList<>();
		StringBuilder stringbuilder = new StringBuilder();
		try {
			splitComponents(finalList, o, c);
			for (Object component : finalList) {
				if (VersionHandler.is1_7()) {
					stringbuilder.append((String) component.getClass().getMethod("e").invoke(component));
				} else if(VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12() || VersionHandler.is1_13() || VersionHandler.is1_14() || VersionHandler.is1_15() || VersionHandler.is1_16() || VersionHandler.is1_17()){
					stringbuilder.append((String) component.getClass().getMethod("getText").invoke(component));
				}
				else {
					stringbuilder.append((String) component.getClass().getMethod("getString").invoke(component));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringbuilder.toString();
	}

	private static void splitComponents(List<Object> finalList, Object o, Class<?> c) throws Exception {
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
		} else if(VersionHandler.is1_14_4() || VersionHandler.is1_15() || VersionHandler.is1_16() || VersionHandler.is1_17()) {
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
		else {
			ArrayList<?> list = (ArrayList<?>) c.getMethod("b").invoke(o, new Object[0]);
			for (Object component : list) {
				ArrayList<?> innerList = (ArrayList<?>) c.getMethod("b").invoke(component, new Object[0]);
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
		String allFormated = Format.FormatString(string);
		allFormated = Format.FormatStringColor(allFormated);
		return allFormated;
	}

	public static String FilterChat(String msg) {
		int t = 0;
		List<String> filters = getInstance().getConfig().getStringList("filters");
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
			// (?i) = case insensitive
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
     * @param color to validate.
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

	public static String escapeAllRegex(String input) {
		return input.replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(")
				.replace(")", "\\)").replace("|", "\\|").replace("+", "\\+").replace("*", "\\*");
	}

	public static String underlineURLs() {
		final boolean configValue = getInstance().getConfig().getBoolean("underlineurls", true);
		if (VersionHandler.isAtLeast_1_20_4()) {
			return String.valueOf(configValue);
		} else {
			return "\"" + configValue + "\"";
		}
	}
	
	public static String parseTimeStringFromMillis(long millis) {
		String timeString = "";
		if(millis >= Format.MILLISECONDS_PER_DAY) {
			long numberOfDays = millis / Format.MILLISECONDS_PER_DAY;
			millis -= Format.MILLISECONDS_PER_DAY * numberOfDays;
			
			String units = LocalizedMessage.UNITS_DAY_PLURAL.toString();
			if (numberOfDays == 1) {
				units = LocalizedMessage.UNITS_DAY_SINGULAR.toString();
			}
			timeString += numberOfDays + " " + units + " ";
		}
		
		if(millis >= Format.MILLISECONDS_PER_HOUR) {
			long numberOfHours = millis / Format.MILLISECONDS_PER_HOUR;
			millis -= Format.MILLISECONDS_PER_HOUR * numberOfHours;

			String units = LocalizedMessage.UNITS_HOUR_PLURAL.toString();
			if (numberOfHours == 1) {
				units = LocalizedMessage.UNITS_HOUR_SINGULAR.toString();
			}
			timeString += numberOfHours + " " + units + " ";
		}
		
		if(millis >= Format.MILLISECONDS_PER_MINUTE) {
			long numberOfMinutes = millis / Format.MILLISECONDS_PER_MINUTE;
			millis -= Format.MILLISECONDS_PER_MINUTE * numberOfMinutes;

			String units = LocalizedMessage.UNITS_MINUTE_PLURAL.toString();
			if (numberOfMinutes == 1) {
				units = LocalizedMessage.UNITS_MINUTE_SINGULAR.toString();
			}
			timeString += numberOfMinutes + " " + units + " ";
		}
		
		if(millis >= Format.MILLISECONDS_PER_SECOND) {
			long numberOfSeconds = millis / Format.MILLISECONDS_PER_SECOND;
			millis -= Format.MILLISECONDS_PER_SECOND * numberOfSeconds;

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
	
	public static void broadcastToServer(String message) {
		for(MineverseChatPlayer mcp : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
			mcp.getPlayer().sendMessage(message);
		}
	}
	
	public static void playMessageSound(MineverseChatPlayer mcp) {
		Player player = mcp.getPlayer();
		String soundName = getInstance().getConfig().getString("message_sound", DEFAULT_MESSAGE_SOUND);
		if(!soundName.equalsIgnoreCase("None")) {
			Sound messageSound = getSound(soundName);
			player.playSound(player.getLocation(), messageSound, 1, 0);
		}
	}
	
	private static Sound getSound(String soundName) {
		if(Arrays.asList(Sound.values()).stream().map(Sound::toString).collect(Collectors.toList()).contains(soundName)) {
			return Sound.valueOf(soundName);
		}
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Message sound invalid!"));
		return getDefaultMessageSound();
	}
	
	private static Sound getDefaultMessageSound() {
		if(VersionHandler.is1_7() || VersionHandler.is1_8()) {
			return Sound.valueOf(DEFAULT_LEGACY_MESSAGE_SOUND);
		}
		else {
			return Sound.valueOf(DEFAULT_MESSAGE_SOUND);
		}
	}
	
	public static String stripColor(String message) {
		return message.replaceAll("(\u00A7([a-z0-9]))", "");
	}
}
