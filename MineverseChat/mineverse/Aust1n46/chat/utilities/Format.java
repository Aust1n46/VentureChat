package mineverse.Aust1n46.chat.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.json.JsonFormat;

//This class is where all formatting methods are stored.
public class Format { 
	private static MineverseChat plugin = MineverseChat.getInstance();
	
	public static String convertToJson(ChatMessage lastChatMessage) {
		MineverseChatPlayer icp = MineverseChatAPI.getMineverseChatPlayer(lastChatMessage.getSender());
		JsonFormat format = MineverseChat.jfInfo.getJsonFormat(icp.getJsonFormat());
		String f = lastChatMessage.getFormat().replace("\\", "\\\\").replace("\"", "\\\"");
		String c = lastChatMessage.getChat().replace("\\", "\\\\").replace("\"", "\\\"");
		String json = "[\"\",{\"text\":\"\",\"extra\":[";
		String prefix = "";
		String suffix = "";
		try {
			prefix = FormatStringAll(MineverseChat.chat.getPlayerPrefix(icp.getPlayer()));
			suffix = FormatStringAll(MineverseChat.chat.getPlayerSuffix(icp.getPlayer()));
			if(suffix.equals("")) {
				suffix = "venturechat_no_suffix_code";
			}
			if(prefix.equals("")) {
				prefix = "venturechat_no_prefix_code";
			}
		}
		catch(Exception e) {
			if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
				Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Prefix and / or suffix don't exist, setting to nothing."));
			}
		}	
		String nickname = "";
		if(icp.getPlayer() != null) {
			nickname = FormatStringAll(icp.getPlayer().getDisplayName());
		}
		/*int prefixIndex = f.indexOf(prefix);
		if(prefixIndex == -1) {
			prefixIndex = 0;
			prefix = "";
		}
		int nameIndex = f.indexOf(nickname);
		if(nameIndex == -1) {
			nameIndex = 0;
			nickname = "";
		}
		int suffixIndex = f.indexOf(suffix);
		if(suffixIndex == -1) {
			suffixIndex = 0;
			suffix = "";
		}*/
		/*int lower = prefixIndex;
		int middle = nameIndex;
		//int higher = suffixIndex;
		String lowerText = prefix;
		List<String> lowerList = format.getHoverTextPrefix();
		String middleText = nickname;
		List<String> middleList = format.getHoverTextName();
		String clickLowerAction = format.getClickPrefix();
		String clickLowerActionText = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickPrefixText());
		String clickMiddleAction = format.getClickName();
		String clickMiddleActionText = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickNameText());
		if(nameIndex < prefixIndex) {
			lower = nameIndex;
			middle = prefixIndex;
			lowerText = nickname;
			lowerList = format.getHoverTextName();
			middleText = prefix;
			middleList = format.getHoverTextPrefix();
			clickLowerAction = format.getClickName();
			clickLowerActionText = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickNameText());
			clickMiddleAction = format.getClickPrefix();
			clickMiddleActionText = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickPrefixText());
		}
		String hoverLower = "";
		for(String s : lowerList) {
			hoverLower += s + "\n";
		}
		hoverLower = FormatStringAll(hoverLower.substring(0, hoverLower.length() - 1));
		if(pluginManager.isPluginEnabled("PlaceholderAPI")) {
			hoverLower = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), hoverLower);
		}
		String hoverMiddle = "";
		for(String s : middleList) {
			hoverMiddle += s + "\n";
		}
		hoverMiddle = FormatStringAll(hoverMiddle.substring(0, hoverMiddle.length() - 1));
		if(pluginManager.isPluginEnabled("PlaceholderAPI")) {
			hoverMiddle = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), hoverMiddle);
		}*/
		
		//json += convertToJsonColors(f.substring(0, lower));
		//json += "]},{\"text\":\"" + lowerText + "\",\"clickEvent\":{\"action\":\"" + clickLowerAction + "\",\"value\":\"" + clickLowerActionText + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + convertToJsonColors(hoverLower) + "]}}}";
		//json += ",{\"text\":\"\",\"extra\":[" + convertToJsonColors(getLastCode(lowerText) + f.substring(lower + lowerText.length(), middle)) + "]}";
		//json += ",{\"text\":\"" + getLastCode(lowerText + f.substring(lower + lowerText.length(), middle)) + middleText + "\",\"clickEvent\":{\"action\":\"" + clickMiddleAction + "\",\"value\":\"" + clickMiddleActionText + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + convertToJsonColors(hoverMiddle) + "]}}}";
		//json += "," + convertToJsonColors(getLastCode(lowerText + f.substring(lower + lowerText.length(), middle)) + f.substring(middle + middleText.length(), f.length())) + "," + convertLinks(getLastCode(lowerText + f.substring(lower + lowerText.length(), middle) + f.substring(middle + middleText.length(), f.length())) + c);		
		
		json += convertPlaceholders(f, format, prefix, nickname, suffix, icp);
		json += "]}";
		json += "," + convertLinks(c);		
		json += "]";
		if(plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			System.out.println(json);
		}
		return json;
	}
	
	private static String convertPlaceholders(String s, JsonFormat format, String prefix, String nickname, String suffix, MineverseChatPlayer icp) {
		String remaining = s;
		String temp = "";
		int indexStart = -1;
		int indexEnd = -1;
		String placeholder = "";
		String lastCode = "§f";
		do {
			Pattern pattern = Pattern.compile("(" + prefix.replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)") + "|" + nickname.replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)") + "|" + suffix.replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)") + ")");
			Matcher matcher = pattern.matcher(remaining);
			if(matcher.find()) {
				indexStart = matcher.start();
				indexEnd = matcher.end();
				placeholder = remaining.substring(indexStart, indexEnd);
				temp += convertToJsonColors(lastCode + remaining.substring(0, indexStart)) + ",";
				lastCode = getLastCode(lastCode + remaining.substring(0, indexStart));
				String action = "";
				if(placeholder.contains(prefix))
					action = format.getClickPrefix();
				if(placeholder.contains(nickname))
					action = format.getClickName();
				if(placeholder.contains(suffix))
					action = format.getClickSuffix();
				String text = "";
				if(placeholder.contains(prefix))
					text = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickPrefixText());
				if(placeholder.contains(nickname))
					text = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickNameText());
				if(placeholder.contains(suffix))
					text = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), format.getClickSuffixText());
				String hover = "";
				if(placeholder.contains(prefix)) {
					hover = "";
					for(String st : format.getHoverTextPrefix()) {
						hover += Format.FormatStringAll(st) + "\n";
					}
				}
				if(placeholder.contains(nickname)) {
					hover = "";
					for(String st : format.getHoverTextName()) {
						hover += Format.FormatStringAll(st) + "\n";
					}
				}
				if(placeholder.contains(suffix)) {
					hover = "";
					for(String st : format.getHoverTextSuffix()) {
						hover += Format.FormatStringAll(st) + "\n";
					}
				}
				hover = PlaceholderAPI.setBracketPlaceholders(icp.getPlayer(), hover.substring(0, hover.length() - 1));
				temp += convertToJsonColors(lastCode + placeholder, ",\"clickEvent\":{\"action\":\"" + action + "\",\"value\":\"" + text + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + convertToJsonColors(hover) + "]}}") + ",";
				lastCode = getLastCode(lastCode + placeholder);
				remaining = remaining.substring(indexEnd);
			}
			else {
				temp += convertToJsonColors(lastCode + remaining);
				break;
			}
		}
		while(true);
		return temp;
	}
	
	private static String convertLinks(String s) {
		String remaining = s;
		String temp = "";
		int indexLink = -1;
		int indexLinkEnd = -1;
		String link = "";
		String lastCode = "§f";
		do {
			Pattern pattern = Pattern.compile("([a-zA-Z0-9§\\-:/]+\\.[a-zA-Z/0-9§\\-:_#]+(\\.[a-zA-Z/0-9.§\\-:#\\?\\+=_]+)?)");
			Matcher matcher = pattern.matcher(remaining);
			if(matcher.find()) {
				indexLink = matcher.start();
				indexLinkEnd = matcher.end();
				link = remaining.substring(indexLink, indexLinkEnd);	
				temp += convertToJsonColors(lastCode + remaining.substring(0, indexLink)) + ",";
				lastCode = getLastCode(lastCode + remaining.substring(0, indexLink));
				String https = "";
				if(ChatColor.stripColor(link).contains("https://")) 
					https = "s";
				temp += convertToJsonColors(lastCode + link, ",\"underlined\":\"" + plugin.getConfig().getBoolean("underlineurls", true) + "\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http" + https + "://" + ChatColor.stripColor(link.replace("http://", "").replace("https://", "")) + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + convertToJsonColors(lastCode + link) + "]}}") + ",";
				lastCode = getLastCode(lastCode + link);
				remaining = remaining.substring(indexLinkEnd);
			}
			else {
				temp += convertToJsonColors(lastCode + remaining);
				break;
			}
		}
		while(true);
		return temp;
	}
	
	public static String getLastCode(String s) {
		String ts = "";
		char[] ch = s.toCharArray();
		for(int a = 0; a < s.length() - 1; a ++) {
			if(String.valueOf(ch[a + 1]).matches("[lkonmr0123456789abcdef]") && ch[a] == '§') {
				ts += String.valueOf(ch[a]) + ch[a + 1];
				if(String.valueOf(ch[a + 1]).matches("[0123456789abcdefr]")) {
					ts = String.valueOf(ch[a]) + ch[a + 1];
				}
			}				
		}
		return ts;
	}
	
	/*
	private static String getLastFormat(String s) {
		Stack<Character> temp = new Stack<Character>();
		String ts = "";
		for(char c : s.toCharArray()) {
			temp.push(c);
		}
		String i = "";
		while(!temp.isEmpty()) {
			i += temp.pop();
		}
		char[] ch = i.toCharArray();
		for(int a = 0; a < i.length() - 1; a ++) {
			if(String.valueOf(ch[a]).matches("[lkonmr]") && ch[a + 1] == '§') {
				ts += String.valueOf(ch[a + 1]) + ch[a];
			}				
		}
		return ts;
	}
	
	private static String getLastColor(String s) {
		Stack<Character> temp = new Stack<Character>();
		for(char c : s.toCharArray()) {
			temp.push(c);
		}
		String i = "";
		while(!temp.isEmpty()) {
			i += temp.pop();
		}
		char[] ch = i.toCharArray();
		for(int a = 0; a < i.length() - 1; a ++) {
			if(String.valueOf(ch[a]).matches("[0123456789abcdef]") && ch[a + 1] == '§') {
				return String.valueOf(ch[a + 1]) + ch[a];
			}				
		}
		return "§f";
	}*/
	
	public static String convertToJsonColors(String s) {
		return convertToJsonColors(s, "");
	}
	
	public static String convertToJsonColors(String s, String extensions) {
		String remaining = s;
		String temp = "";
		int indexColor = -1;
		int indexNextColor = -1;
		String color = "";
		String modifier = "";
		boolean bold = false;
		boolean obfuscated = false;
		boolean italic = false;
		boolean underlined = false;
		boolean strikethrough = false;
		String previousColor = "";
		do {
			if(remaining.length() < 2) {
				temp = "{\"text\":\"" + remaining + "\"},";
				break;
			}
			modifier = "";
			indexColor = remaining.indexOf("§");	
			previousColor = color;			
			color = remaining.substring(1, indexColor + 2);
			if(!color.matches("[0123456789abcdef]")) {				
				switch(color) {
					case "l": {
						bold = true;
						break;
					}
					case "k": {
						obfuscated = true;
						break;
					}
					case "o": {
						italic = true;
						break;
					}
					case "n": {
						underlined = true;
						break;
					}
					case "m": {
						strikethrough = true;
						break;
					}
					case "r": {
						bold = false;
						obfuscated = false;
						italic = false;
						underlined = false;
						strikethrough = false;
						color = "f";
						break;
					}
				}
				if(!color.equals("f"))
					color = previousColor;
				if(color.length() == 0)
					color = "f";
			}
			else {				
				bold = false;
				obfuscated = false;
				italic = false;
				underlined = false;
				strikethrough = false;
			}		
			if(bold)
				modifier += ",\"bold\":\"true\"";	
			if(obfuscated)
				modifier += ",\"obfuscated\":\"true\"";	
			if(italic)
				modifier += ",\"italic\":\"true\"";		
			if(underlined)
				modifier += ",\"underlined\":\"true\"";		
			if(strikethrough)
				modifier += ",\"strikethrough\":\"true\"";	
			remaining = remaining.substring(2);
			indexNextColor = remaining.indexOf("§");
			if(indexNextColor == -1) {
				indexNextColor = remaining.length();
			}
			temp += "{\"text\":\"" + remaining.substring(0, indexNextColor) + "\",\"color\":\"" + hexidecimalToJsonColor(color) + "\"" + modifier + extensions + "},";
			remaining = remaining.substring(indexNextColor);
		} 
		while(remaining.length() > 1 && indexColor != -1); 
		if(temp.length() > 1)
			temp = temp.substring(0, temp.length() - 1);
		return temp;
	}
	
	private static String hexidecimalToJsonColor(String c) {
		switch(c) {
			case "0": return "black";
			case "1": return "dark_blue";
			case "2": return "dark_green";
			case "3": return "dark_aqua";
			case "4": return "dark_red";
			case "5": return "dark_purple";
			case "6": return "gold";
			case "7": return "gray";
			case "8": return "dark_gray";
			case "9": return "blue";
			case "a": return "green";
			case "b": return "aqua";
			case "c": return "red";
			case "d": return "light_purple";
			case "e": return "yellow";
			case "f": return "white";
		}
		return "";
	}
	
	protected static Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
	protected static Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
	protected static Pattern chatBoldPattern = Pattern.compile("(?i)&([L])");
	protected static Pattern chatStrikethroughPattern = Pattern.compile("(?i)&([M])");
	protected static Pattern chatUnderlinePattern = Pattern.compile("(?i)&([N])");
	protected static Pattern chatItalicPattern = Pattern.compile("(?i)&([O])");
	protected static Pattern chatResetPattern = Pattern.compile("(?i)&([R])");

	public static String FormatStringColor(String string) {
		String allFormated = string;
		allFormated = chatColorPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = allFormated.replaceAll("%", "\\%");
		return allFormated;
	}

	public static String FormatString(String string) {
		String allFormated = string;
		allFormated = chatMagicPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatBoldPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatStrikethroughPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatUnderlinePattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatItalicPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatResetPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = allFormated.replaceAll("%", "\\%");
		return allFormated;
	}

	public static String FormatPlayerName(String playerPrefix, String playerDisplayName, String playerSuffix) {
		playerPrefix = chatColorPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatMagicPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatBoldPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatStrikethroughPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatUnderlinePattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatItalicPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatResetPattern.matcher(playerPrefix).replaceAll("\u00A7$1");

		playerSuffix = chatColorPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatMagicPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatBoldPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatStrikethroughPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatUnderlinePattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatItalicPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatResetPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		return playerPrefix + playerDisplayName.trim() + playerSuffix;
	}

	public static String FormatStringAll(String string) {
		String allFormated = string;
		allFormated = chatColorPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatMagicPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatBoldPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatStrikethroughPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatUnderlinePattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatItalicPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatResetPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = allFormated.replaceAll("%", "\\%");
		return allFormated;
	}
}