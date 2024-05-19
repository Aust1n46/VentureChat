package venture.Aust1n46.chat.service;

import static venture.Aust1n46.chat.utilities.FormatUtils.BUKKIT_COLOR_CODE_PREFIX;
import static venture.Aust1n46.chat.utilities.FormatUtils.BUKKIT_COLOR_CODE_PREFIX_CHAR;
import static venture.Aust1n46.chat.utilities.FormatUtils.BUKKIT_HEX_COLOR_CODE_PREFIX;
import static venture.Aust1n46.chat.utilities.FormatUtils.DEFAULT_COLOR_CODE;
import static venture.Aust1n46.chat.utilities.FormatUtils.HEX_COLOR_CODE_PREFIX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import me.clip.placeholderapi.PlaceholderAPI;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.ClickAction;
import venture.Aust1n46.chat.model.Filter;
import venture.Aust1n46.chat.model.JsonAttribute;
import venture.Aust1n46.chat.model.JsonFormat;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.utilities.FormatUtils;
import venture.Aust1n46.chat.xcut.VersionService;

/**
 * Class containing chat formatting methods.
 */
@Singleton
public class FormatService {
	public static final int LEGACY_COLOR_CODE_LENGTH = 2;
	public static final int HEX_COLOR_CODE_LENGTH = 14;
	private static final Pattern PLACEHOLDERAPI_PLACEHOLDER_PATTERN = Pattern.compile("\\{([^\\{\\}]+)\\}");
	public static final String DEFAULT_MESSAGE_SOUND = "ENTITY_PLAYER_LEVELUP";
	public static final String DEFAULT_LEGACY_MESSAGE_SOUND = "LEVEL_UP";

	@Inject
	private VentureChat plugin;
	@Inject
	private PlayerApiService playerApiService;
	@Inject
	private ConfigService configService;
	@Inject
	private VersionService versionService;

	/**
	 * Converts a message to Minecraft JSON formatting while applying the
	 * {@link JsonFormat} from the config.
	 *
	 * @param sender {@link VentureChatPlayer} wrapper of the message sender.
	 * @param format The format section of the message.
	 * @param chat   The chat section of the message.
	 * @return {@link String}
	 */
	public String convertToJson(VentureChatPlayer sender, String format, String chat) {
		JsonFormat JSONformat = configService.getJsonFormat(sender.getJsonFormat());
		String c = escapeJsonChars(chat);
		String json = "[\"\",{\"text\":\"\",\"extra\":[";
		json += convertPlaceholders(format, JSONformat, sender);
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
	private String convertPlaceholders(String s, JsonFormat format, VentureChatPlayer icp) {
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
				formattedPlaceholder = escapeJsonChars(FormatUtils.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), placeholder)));
				temp += convertToJsonColors(escapeJsonChars(lastCode + remaining.substring(0, indexStart))) + ",";
				lastCode = getLastCode(lastCode + remaining.substring(0, indexStart));
				boolean placeholderHasJsonAttribute = false;
				for (JsonAttribute jsonAttribute : format.getJsonAttributes()) {
					if (placeholder.contains(jsonAttribute.getName().replace("{", "").replace("}", ""))) {
						final StringBuilder hover = new StringBuilder();
						for (String st : jsonAttribute.getHoverText()) {
							hover.append(FormatUtils.FormatStringAll(st) + "\n");
						}
						final String hoverText;
						if(!hover.isEmpty()) {
							hoverText = escapeJsonChars(FormatUtils.FormatStringAll(
									PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), hover.substring(0, hover.length() - 1))));
						} else {
							hoverText = StringUtils.EMPTY;
						}
						final ClickAction clickAction = jsonAttribute.getClickAction();
						final String actionJson;
						if (clickAction == ClickAction.NONE) {
							actionJson = StringUtils.EMPTY;
						} else {
							final String clickText = escapeJsonChars(FormatUtils.FormatStringAll(
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
	private String convertLinks(String s) {
		String remaining = s;
		String temp = "";
		int indexLink = -1;
		int indexLinkEnd = -1;
		String link = "";
		String lastCode = DEFAULT_COLOR_CODE;
		do {
			Pattern pattern = Pattern.compile("([a-zA-Z0-9" + BUKKIT_COLOR_CODE_PREFIX + "\\-:/]+\\.[a-zA-Z/0-9" + BUKKIT_COLOR_CODE_PREFIX + "\\-:_#]+(\\.[a-zA-Z/0-9."
					+ BUKKIT_COLOR_CODE_PREFIX + "\\-:;,#\\?\\+=_]+)?)");
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
						",\"underlined\":" + underlineURLs() + ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http" + https + "://"
								+ ChatColor.stripColor(link.replace("http://", "").replace("https://", ""))
								+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + convertToJsonColors(lastCode + link) + "]}}")
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

	public String getLastCode(String s) {
		String ts = "";
		char[] ch = s.toCharArray();
		for (int a = 0; a < s.length() - 1; a++) {
			if (String.valueOf(ch[a + 1]).matches("[lkomnLKOMN]") && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
				ts += String.valueOf(ch[a]) + ch[a + 1];
				a++;
			} else if (String.valueOf(ch[a + 1]).matches("[0123456789abcdefrABCDEFR]") && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
				ts = String.valueOf(ch[a]) + ch[a + 1];
				a++;
			} else if (ch[a + 1] == 'x' && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
				if (ch.length > a + 13) {
					if (String.valueOf(ch[a + 3]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 5]).matches("[0123456789abcdefABCDEF]")
							&& String.valueOf(ch[a + 7]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 9]).matches("[0123456789abcdefABCDEF]")
							&& String.valueOf(ch[a + 11]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 13]).matches("[0123456789abcdefABCDEF]")
							&& ch[a + 2] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 4] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 6] == BUKKIT_COLOR_CODE_PREFIX_CHAR
							&& ch[a + 8] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 10] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 12] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
						ts = String.valueOf(ch[a]) + ch[a + 1] + ch[a + 2] + ch[a + 3] + ch[a + 4] + ch[a + 5] + ch[a + 6] + ch[a + 7] + ch[a + 8] + ch[a + 9] + ch[a + 10]
								+ ch[a + 11] + ch[a + 12] + ch[a + 13];
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
	public String convertToJsonColors(String s) {
		return convertToJsonColors(s, "");
	}

	/**
	 * Converts a message to JSON colors with additional JSON extensions.
	 *
	 * @param s
	 * @param extensions
	 * @return {@link String}
	 */
	private String convertToJsonColors(String s, String extensions) {
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
					color = HEX_COLOR_CODE_PREFIX + remaining.substring(LEGACY_COLOR_CODE_LENGTH, indexColor + HEX_COLOR_CODE_LENGTH).replace(BUKKIT_COLOR_CODE_PREFIX, "");
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
				if (versionService.isAtLeast_1_20_4()) {
					modifier += ",\"bold\":true";
				} else {
					modifier += ",\"bold\":\"true\"";
				}
			if (obfuscated)
				if (versionService.isAtLeast_1_20_4()) {
					modifier += ",\"obfuscated\":true";
				} else {
					modifier += ",\"obfuscated\":\"true\"";
				}
			if (italic)
				if (versionService.isAtLeast_1_20_4()) {
					modifier += ",\"italic\":true";
				} else {
					modifier += ",\"italic\":\"true\"";
				}
			if (underlined)
				if (versionService.isAtLeast_1_20_4()) {
					modifier += ",\"underlined\":true";
				} else {
					modifier += ",\"underlined\":\"true\"";
				}
			if (strikethrough)
				if (versionService.isAtLeast_1_20_4()) {
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
			temp += "{\"text\":\"" + remaining.substring(0, indexNextColor) + "\",\"color\":\"" + hexidecimalToJsonColorRGB(color) + "\"" + modifier + extensions + "},";
			remaining = remaining.substring(indexNextColor);
		} while (remaining.length() > 1 && indexColor != -1);
		if (temp.length() > 1)
			temp = temp.substring(0, temp.length() - 1);
		return temp;
	}

	private String hexidecimalToJsonColorRGB(String c) {
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
		if (FormatUtils.isValidHexColor(c)) {
			return c;
		}
		return "white";
	}

	public String convertPlainTextToJson(String s, boolean convertURL) {
		s = escapeJsonChars(s);
		if (convertURL) {
			return "[" + convertLinks(s) + "]";
		} else {
			return "[" + convertToJsonColors(DEFAULT_COLOR_CODE + s) + "]";
		}
	}

	private String escapeJsonChars(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private String formatModerationGUI(String json, Player player, String sender, String channelName, int hash) {
		if (player.hasPermission("venturechat.gui")) {
			json = json.substring(0, json.length() - 1);
			json += "," + convertToJsonColors(FormatUtils.FormatStringAll(plugin.getConfig().getString("guiicon")),
					",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vchatgui " + sender + " " + channelName + " " + hash
							+ "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
							+ convertToJsonColors(FormatUtils.FormatStringAll(plugin.getConfig().getString("guitext"))) + "]}}")
					+ "]";
		}
		return json;
	}

	public PacketContainer createPacketPlayOutChat(String json) {
		final PacketContainer container;
		if (versionService.isAtLeast_1_20_4()) { // 1.20.4+
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getChatComponents().write(0, WrappedChatComponent.fromJson(json));
			container.getBooleans().write(0, false);
		} else if (versionService.isAbove_1_19()) { // 1.19.1 -> 1.20.3
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getStrings().write(0, json);
			container.getBooleans().write(0, false);
		} else if (versionService.isUnder_1_19()) { // 1.7 -> 1.19
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

	public PacketContainer createPacketPlayOutChat(WrappedChatComponent component) {
		final PacketContainer container;
		if (versionService.isAtLeast_1_20_4()) { // 1.20.4+
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getChatComponents().write(0, component);
			container.getBooleans().write(0, false);
		} else if (versionService.isAbove_1_19()) { // 1.19.1 -> 1.20.3
			container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
			container.getStrings().write(0, component.getJson());
			container.getBooleans().write(0, false);
		} else if (versionService.isUnder_1_19()) { // 1.7 -> 1.19
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

	public void sendPacketPlayOutChat(Player player, PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createAndSendChatMessage(final String json, final String channelName, final int hash, final Set<Player> recipients, final String sender) {
		for (final Player player : recipients) {
			final String finalJson = formatModerationGUI(json, player, sender, channelName, hash);
			final PacketContainer packet = createPacketPlayOutChat(finalJson);
			sendPacketPlayOutChat(player, packet);
		}
	}

	public void createAndSendExternalChatMessage(final String message, final String channelName, final String sender) {
		final String json = convertPlainTextToJson(message, true);
		final int hash = FormatUtils.stripColor(message).hashCode();
		playerApiService.getOnlineMineverseChatPlayers()
			.stream()
			.filter(vcp -> configService.isListening(vcp, channelName))
			.forEach(vcp -> {
				final String finalJSON = formatModerationGUI(json, vcp.getPlayer(), sender, channelName, hash);
				final PacketContainer packet = createPacketPlayOutChat(finalJSON);
				sendPacketPlayOutChat(vcp.getPlayer(), packet);
			});
	}

	@SuppressWarnings("unchecked")
	public String toColoredText(Object o, Class<?> c) {
		if (versionService.is1_7()) {
			return "\"extra\":[{\"text\":\"Hover to see original message is not currently supported in 1.7\",\"color\":\"red\"}]";
		}
		List<Object> finalList = new ArrayList<>();
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("\"extra\":[");
		try {
			splitComponents(finalList, o, c);
			for (Object component : finalList) {
				try {
					if (versionService.is1_8() || versionService.is1_9() || versionService.is1_10() || versionService.is1_11() || versionService.is1_12() || versionService.is1_13()
							|| versionService.is1_14() || versionService.is1_15() || versionService.is1_16() || versionService.is1_17()) {
						String text = (String) component.getClass().getMethod("getText").invoke(component);
						Object chatModifier = component.getClass().getMethod("getChatModifier").invoke(component);
						Object color = chatModifier.getClass().getMethod("getColor").invoke(chatModifier);
						String colorString = "white";
						if (color != null) {
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
						if (color != null) {
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
				} catch (Exception e) {
					return "\"extra\":[{\"text\":\"Something went wrong. Could not access color.\",\"color\":\"red\"}]";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String coloredText = stringbuilder.toString();
		if (coloredText.endsWith(",")) {
			coloredText = coloredText.substring(0, coloredText.length() - 1);
		}
		coloredText += "]";
		return coloredText;
	}

	public String toPlainText(Object o, Class<?> c) {
		List<Object> finalList = new ArrayList<>();
		StringBuilder stringbuilder = new StringBuilder();
		try {
			splitComponents(finalList, o, c);
			for (Object component : finalList) {
				if (versionService.is1_7()) {
					stringbuilder.append((String) component.getClass().getMethod("e").invoke(component));
				} else if (versionService.is1_8() || versionService.is1_9() || versionService.is1_10() || versionService.is1_11() || versionService.is1_12()
						|| versionService.is1_13() || versionService.is1_14() || versionService.is1_15() || versionService.is1_16() || versionService.is1_17()) {
					stringbuilder.append((String) component.getClass().getMethod("getText").invoke(component));
				} else {
					stringbuilder.append((String) component.getClass().getMethod("getString").invoke(component));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringbuilder.toString();
	}

	private void splitComponents(List<Object> finalList, Object o, Class<?> c) throws Exception {
		if (versionService.is1_7() || versionService.is1_8() || versionService.is1_9() || versionService.is1_10() || versionService.is1_11() || versionService.is1_12()
				|| versionService.is1_13() || (versionService.is1_14() && !versionService.is1_14_4())) {
			ArrayList<?> list = (ArrayList<?>) c.getMethod("a").invoke(o, new Object[0]);
			for (Object component : list) {
				ArrayList<?> innerList = (ArrayList<?>) c.getMethod("a").invoke(component, new Object[0]);
				if (innerList.size() > 0) {
					splitComponents(finalList, component, c);
				} else {
					finalList.add(component);
				}
			}
		} else if (versionService.is1_14_4() || versionService.is1_15() || versionService.is1_16() || versionService.is1_17()) {
			ArrayList<?> list = (ArrayList<?>) c.getMethod("getSiblings").invoke(o, new Object[0]);
			for (Object component : list) {
				ArrayList<?> innerList = (ArrayList<?>) c.getMethod("getSiblings").invoke(component, new Object[0]);
				if (innerList.size() > 0) {
					splitComponents(finalList, component, c);
				} else {
					finalList.add(component);
				}
			}
		} else {
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

	public String filterChat(final String message) {
		String filteredMessage = message;
		final List<Filter> filters = configService.getFilters();
		for (final Filter filter : filters) {
			filteredMessage = filteredMessage.replaceAll("(?i)" + filter.getMatcher(), filter.getReplacer()); // (?i) = case insensitive
		}
		return filteredMessage;
	}

	public String underlineURLs() {
		final boolean configValue = plugin.getConfig().getBoolean("underlineurls", true);
		if (versionService.isAtLeast_1_20_4()) {
			return String.valueOf(configValue);
		} else {
			return "\"" + configValue + "\"";
		}
	}

	public void broadcastToServer(String message) {
		for (VentureChatPlayer mcp : playerApiService.getOnlineMineverseChatPlayers()) {
			mcp.getPlayer().sendMessage(message);
		}
	}

	public void playMessageSound(VentureChatPlayer mcp) {
		Player player = mcp.getPlayer();
		String soundName = plugin.getConfig().getString("message_sound", DEFAULT_MESSAGE_SOUND);
		if (!soundName.equalsIgnoreCase("None")) {
			Sound messageSound = getSound(soundName);
			player.playSound(player.getLocation(), messageSound, 1, 0);
		}
	}

	private Sound getSound(String soundName) {
		if (Arrays.asList(Sound.values()).stream().map(Sound::toString).collect(Collectors.toList()).contains(soundName)) {
			return Sound.valueOf(soundName);
		}
		plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Message sound invalid!"));
		return getDefaultMessageSound();
	}

	private Sound getDefaultMessageSound() {
		if (versionService.is1_7() || versionService.is1_8()) {
			return Sound.valueOf(DEFAULT_LEGACY_MESSAGE_SOUND);
		} else {
			return Sound.valueOf(DEFAULT_MESSAGE_SOUND);
		}
	}
}
