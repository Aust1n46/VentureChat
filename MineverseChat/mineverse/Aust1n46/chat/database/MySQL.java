package mineverse.Aust1n46.chat.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//This class initializes the plugins connection to the MySQL database if it's enabled.
public class MySQL extends Database {
	private final String user;
	private final String database;
	private final String password;
	private final int port;
	private final String hostname;

	public MySQL(String hostname, int port, String database, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	@Override
	public void init() {
		HikariConfig config = new HikariConfig();
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
		try {
			Connection conn = dataSource.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS VentureChat " +
				"(ID SERIAL PRIMARY KEY, ChatTime TEXT, UUID TEXT, Name TEXT, " +
				"Server TEXT, Channel TEXT, Text TEXT, Type TEXT)");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}