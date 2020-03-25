package mineverse.Aust1n46.chat.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariDataSource;

import mineverse.Aust1n46.chat.MineverseChat;

//Parent class for both the MySQL and SQLite database classes.
public abstract class Database {

	protected HikariDataSource dataSource = null;

	public abstract void init();

	public void writeVentureChat(String time, String uuid, String name, String server, String channel, String text, String type) {
		MineverseChat plugin = MineverseChat.getInstance();
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				final Connection conn = dataSource.getConnection();
				conn.setAutoCommit(false);
				final PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO VentureChat " +
					"(ChatTime, UUID, Name, Server, Channel, Text, Type) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?)");
				statement.setString(1, time);
				statement.setString(2, uuid);
				statement.setString(3, name);
				statement.setString(4, server);
				statement.setString(5, channel);
				statement.setString(6, text);
				statement.setString(7, type);
				statement.executeUpdate();
				conn.commit();
			} catch(SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}
}