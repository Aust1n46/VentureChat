package mineverse.Aust1n46.chat.database;

import java.io.File;
import java.io.IOException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import mineverse.Aust1n46.chat.MineverseChat;

//This class initializes the connection to a SQLite database, which has no implementations currently in the plugin.
public class SQLite extends Database {

	public SQLite(String dbLocation) {
		File dataFolder = MineverseChat.getInstance().getDataFolder();
		if (!dataFolder.exists()) dataFolder.mkdirs();
		File databaseFile = new File(dataFolder, dbLocation);
		try {
			if (!databaseFile.exists()) databaseFile.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		HikariConfig config = new HikariConfig();
		final String jdbcUrl = String.format("jdbc:sqlite:%s", databaseFile);
		config.setJdbcUrl(jdbcUrl);
		dataSource = new HikariDataSource(config);
	}
}