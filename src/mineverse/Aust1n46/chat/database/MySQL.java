package mineverse.Aust1n46.chat.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//This class initializes the plugin's connection to the MySQL database if it's enabled.
public class MySQL extends Database {

	public MySQL(String hostname, int port, String database, String user, String password) {
		final HikariConfig config = new HikariConfig();
		//config.setDriverClassName(org.postgresql.Driver.class.getName());
		//final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", hostname, port, database);
		final String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", hostname, port, database);
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(user);
		config.setPassword(password);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);
		final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS VentureChat " +
				"(ID SERIAL PRIMARY KEY, ChatTime TEXT, UUID TEXT, Name TEXT, " +
				"Server TEXT, Channel TEXT, Text TEXT, Type TEXT)";
		try (final Connection conn = dataSource.getConnection();
				 final PreparedStatement statement = conn.prepareStatement(SQL_CREATE_TABLE)) {
				statement.executeUpdate();
		} 
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}