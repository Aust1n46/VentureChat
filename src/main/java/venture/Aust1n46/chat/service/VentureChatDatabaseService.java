package venture.Aust1n46.chat.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.configuration.ConfigurationSection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.utilities.FormatUtils;

/**
 * Initializes and handles writing to the chat logging database.
 */
@Singleton
public class VentureChatDatabaseService {
	@Inject
	private VentureChat plugin;

	private HikariDataSource dataSource;

	@Inject
	public void postConstruct() {
		try {
			ConfigurationSection mysqlConfig = plugin.getConfig().getConfigurationSection("mysql");
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
				final String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useSSL=false", host, port, database);
				config.setJdbcUrl(jdbcUrl);
				config.setUsername(user);
				config.setPassword(password);
				config.addDataSourceProperty("cachePrepStmts", "true");
				config.addDataSourceProperty("prepStmtCacheSize", "250");
				config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
				dataSource = new HikariDataSource(config);
				final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS VentureChat " + "(ID SERIAL PRIMARY KEY, ChatTime TEXT, UUID TEXT, Name TEXT, "
						+ "Server TEXT, Channel TEXT, Text TEXT, Type TEXT)";
				final Connection conn = dataSource.getConnection();
				final PreparedStatement statement = conn.prepareStatement(SQL_CREATE_TABLE);
				statement.executeUpdate();
			}
		} catch (Exception exception) {
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Database could not be loaded. Is it running?"));
		}
	}

	public boolean isEnabled() {
		return dataSource != null;
	}

	public void writeVentureChat(String uuid, String name, String server, String channel, String text, String type) {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = formatter.format(currentDate.getTime());
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			try (final Connection conn = dataSource.getConnection();
					final PreparedStatement statement = conn
							.prepareStatement("INSERT INTO VentureChat " + "(ChatTime, UUID, Name, Server, Channel, Text, Type) " + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
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
