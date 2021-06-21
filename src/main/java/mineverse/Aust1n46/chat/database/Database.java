package mineverse.Aust1n46.chat.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

/**
 * Initializes and handles writing to the chat logging database.
 */
public class Database {
	private static HikariDataSource dataSource = null;

	public static void initializeMySQL() {
		try {
			ConfigurationSection mysqlConfig = MineverseChat.getInstance().getConfig().getConfigurationSection("mysql");
			if (mysqlConfig.getBoolean("enabled", false)) {
				String host = mysqlConfig.getString("host");
				int port = mysqlConfig.getInt("port");
				String database = mysqlConfig.getString("database");
				String user = mysqlConfig.getString("user");
				String password = mysqlConfig.getString("password");

				final HikariConfig config = new HikariConfig();
				// config.setDriverClassName(org.postgresql.Driver.class.getName());
				// final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", hostname,
				// port, database);
				final String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useSSL=false", host,
						port, database);
				config.setJdbcUrl(jdbcUrl);
				config.setUsername(user);
				config.setPassword(password);
				config.addDataSourceProperty("cachePrepStmts", "true");
				config.addDataSourceProperty("prepStmtCacheSize", "250");
				config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
				dataSource = new HikariDataSource(config);
				final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS VentureChat "
						+ "(ID SERIAL PRIMARY KEY, ChatTime TEXT, UUID TEXT, Name TEXT, "
						+ "Server TEXT, Channel TEXT, Text TEXT, Type TEXT)";
				final Connection conn = dataSource.getConnection();
				final PreparedStatement statement = conn.prepareStatement(SQL_CREATE_TABLE);
				statement.executeUpdate();
			}
		} catch (Exception exception) {
			Bukkit.getConsoleSender().sendMessage(
					Format.FormatStringAll("&8[&eVentureChat&8]&c - Database could not be loaded. Is it running?"));
		}
	}

	public static boolean isEnabled() {
		return dataSource != null;
	}

	public static void writeVentureChat(String uuid, String name, String server, String channel, String text,
			String type) {
		MineverseChat plugin = MineverseChat.getInstance();
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = formatter.format(currentDate.getTime());
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try (final Connection conn = dataSource.getConnection();
					final PreparedStatement statement = conn.prepareStatement(
							"INSERT INTO VentureChat " + "(ChatTime, UUID, Name, Server, Channel, Text, Type) "
									+ "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
				statement.setString(1, date);
				statement.setString(2, uuid);
				statement.setString(3, name);
				statement.setString(4, server);
				statement.setString(5, channel);
				statement.setString(6, text);
				statement.setString(7, type);
				statement.executeUpdate();
			} catch (SQLException error) {
				error.printStackTrace();
			}
		});
	}
}
