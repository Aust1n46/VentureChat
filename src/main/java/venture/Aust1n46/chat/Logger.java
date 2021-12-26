package venture.Aust1n46.chat;

import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.initiators.application.VentureChat;

@Singleton
public class Logger {
	private static final String LOG_PREFIX = "[VentureChat] ";
	
	private org.slf4j.Logger parent = LoggerFactory.getLogger(Logger.class);

	@Inject
	private VentureChat plugin;

	public void info(String message) {
		parent.info(LOG_PREFIX + message);
	}

	public void info(String format, Object arg) {
		parent.info(LOG_PREFIX + format, arg);
	}

	public void info(String format, Object arg1, Object arg2) {
		parent.info(LOG_PREFIX + format, arg1, arg2);
	}

	public void info(String format, Object... arguments) {
		parent.info(LOG_PREFIX + format, arguments);
	}

	public void debug(String message) {
		if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			info(message);
		}
	}

	public void debug(String format, Object arg) {
		if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			info(format, arg);
		}
	}

	public void debug(String format, Object arg1, Object arg2) {
		if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			info(format, arg1, arg2);
		}
	}

	public void debug(String format, Object... arguments) {
		if (plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			info(format, arguments);
		}
	}

	public void warn(String message) {
		parent.warn(message);
	}

	public void error(String message) {
		parent.error(message);
	}
}
