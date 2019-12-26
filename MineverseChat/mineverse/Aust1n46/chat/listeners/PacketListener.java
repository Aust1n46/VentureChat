package mineverse.Aust1n46.chat.listeners;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import mineverse.Aust1n46.chat.ChatMessage;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.VersionHandler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

//This class listens for chat packets and intercepts them before they are sent to the Player.
//The packets are modified to include advanced json formating and the message remover button if the 
//player has permission to remove messages.
public class PacketListener extends PacketAdapter {
	public PacketListener(MineverseChat plugin) {
		super(plugin, ListenerPriority.MONITOR, new PacketType[] { PacketType.Play.Server.CHAT });
		this.plugin = plugin;
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		if(event.isCancelled() || event.getPacketType() != PacketType.Play.Server.CHAT || event.getPacket().getChatComponents().read(0) == null) {
			return;
		}
		
		try {
			if(VersionHandler.is1_7_10() || VersionHandler.is1_7_9() || VersionHandler.is1_7_2()) {
				if((MineverseChat.posField != null) && !(((boolean) MineverseChat.posField.get(event.getPacket().getHandle())))) {
					return;
				}
			}
			else if(VersionHandler.is1_8()) {
				if((MineverseChat.posField != null) && (((Byte) MineverseChat.posField.get(event.getPacket().getHandle())).intValue() > 1)) {
					return;
				}
			}
			else if(VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11()){
				if((MineverseChat.posField != null) && (((Byte) MineverseChat.posField.get(event.getPacket().getHandle())).intValue() > 1)) {
					return;
				}
			}
			else {
				if((MineverseChat.posField != null) && ((Object) MineverseChat.posField.get(event.getPacket().getHandle())) == MineverseChat.chatMessageType.getEnumConstants()[2]) {
					return;
				}
			}
		}
		catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		WrappedChatComponent chat = event.getPacket().getChatComponents().read(0);
		WrappedChatComponent originalChat = event.getPacket().getChatComponents().read(0);	
		String message = null;
		int hash = -1;
		//System.out.println(chat.getJson());
		//System.out.println(MineverseChat.lastChatMessage.getMessage());
		message = MineverseChat.toPlainText(chat.getHandle(), chat.getHandleType());
		//System.out.println(chat.getJson());
		//System.out.println(message + " message");
		hash = message != null ? message.hashCode() : -1;
		//System.out.println("remover goes in here?");
		ChatMessage lastChatMessage = MineverseChat.lastChatMessage;
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		if(lastChatMessage != null && lastChatMessage.getHash() == hash) {
			String json = MineverseChat.lastJson;
			if(mcp.getPlayer().hasPermission("venturechat.gui")) {
				json = json.substring(0, json.length() - 1);
				json += "," + Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("guiicon")), ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vchatgui " + lastChatMessage.getSender() + " " + lastChatMessage.getChannel() + " " + lastChatMessage.getHash() +"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("guitext"))) + "]}}") + "]";
				//json += ",{\"text\":\"" + "json test" + "\"}]";
			}
			//System.out.println("," + Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("guiicon")), ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vchatgui " + lastChatMessage.getSender() + " " + lastChatMessage.getChannel() + " " + lastChatMessage.getHash() +"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[" + Format.convertToJsonColors(Format.FormatStringAll(plugin.getConfig().getString("guitext"))) + "]}}") + "]}]");
			//System.out.println("\nline break\n");
			//System.out.println(json);
			chat.setJson(json);
			event.getPacket().getChatComponents().write(0, chat);
		}
		if((message != null) && (chat.getHandle() != null) && mcp != null) {
			mcp.addMessage(new ChatMessage(originalChat, chat, lastChatMessage != null ? lastChatMessage.getSender() : null, message, hash));
		}
	}

	@SuppressWarnings("unused")
	private String getMessage(String json) {
		JSONArray components = (JSONArray) ((JSONObject) JSONValue.parse(json)).get("extra");
		Iterator<?> iterator = components.iterator();
		StringBuilder builder = new StringBuilder();
		while(iterator.hasNext()) {
			builder.append(((JSONObject) iterator.next()).get("text").toString());
		}
		return builder.toString();
	}
}