package mineverse.Aust1n46.chat.database;

import java.sql.SQLException;

import mineverse.Aust1n46.chat.MineverseChat;

//This class opens the connection to the database if it's enabled.
public class DatabaseSender {
	private static MineverseChat plugin = MineverseChat.getInstance();
	
	public static void writeToMySQL(String time, String uuid, String name, String server, String channel, String text, String type, String timeValue, String uuidValue, String nameValue, String serverValue, String channelValue, String textValue, String typeValue) {
		try {
			if(plugin.c.isClosed()) {
				try {
					plugin.c = plugin.MySQL.openConnection();
				}
				catch(ClassNotFoundException e) {				
					e.printStackTrace();
					return;
				}
			}
			plugin.c.createStatement().executeUpdate("INSERT INTO `VentureChat` (`" + time + "`, `" + uuid + "`, `" + name + "`, `" + server + "`, `" + channel + "`, `" + text + "`, `" + type + "`) VALUES ('" + timeValue + "', '" + uuidValue + "', '" + nameValue + "', '" + serverValue + "', '" + channelValue + "', '" + textValue + "', '" + typeValue + "');");
		}
		catch(SQLException e) {
			e.printStackTrace();
			return;
		}
	}
}