package mineverse.Aust1n46.chat.command;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.chat.Broadcast;
import mineverse.Aust1n46.chat.command.chat.BungeeToggle;
import mineverse.Aust1n46.chat.command.chat.Channel;
import mineverse.Aust1n46.chat.command.chat.Channelinfo;
import mineverse.Aust1n46.chat.command.chat.Chatinfo;
import mineverse.Aust1n46.chat.command.chat.Chatreload;
import mineverse.Aust1n46.chat.command.chat.Chlist;
import mineverse.Aust1n46.chat.command.chat.Chwho;
import mineverse.Aust1n46.chat.command.chat.Clearchat;
import mineverse.Aust1n46.chat.command.chat.Commandblock;
import mineverse.Aust1n46.chat.command.chat.Commandspy;
import mineverse.Aust1n46.chat.command.chat.Config;
import mineverse.Aust1n46.chat.command.chat.Edit;
import mineverse.Aust1n46.chat.command.chat.Filter;
import mineverse.Aust1n46.chat.command.chat.Force;
import mineverse.Aust1n46.chat.command.chat.Forceall;
import mineverse.Aust1n46.chat.command.chat.Kickchannel;
import mineverse.Aust1n46.chat.command.chat.Kickchannelall;
import mineverse.Aust1n46.chat.command.chat.Leave;
import mineverse.Aust1n46.chat.command.chat.Listen;
import mineverse.Aust1n46.chat.command.chat.Me;
import mineverse.Aust1n46.chat.command.chat.Party;
import mineverse.Aust1n46.chat.command.chat.RangedSpy;
import mineverse.Aust1n46.chat.command.chat.Removemessage;
import mineverse.Aust1n46.chat.command.chat.Setchannel;
import mineverse.Aust1n46.chat.command.chat.Setchannelall;
import mineverse.Aust1n46.chat.command.chat.VentureChatGui;
import mineverse.Aust1n46.chat.command.chat.Venturechat;
import mineverse.Aust1n46.chat.command.message.Ignore;
import mineverse.Aust1n46.chat.command.message.Message;
import mineverse.Aust1n46.chat.command.message.MessageToggle;
import mineverse.Aust1n46.chat.command.message.Notifications;
import mineverse.Aust1n46.chat.command.message.Reply;
import mineverse.Aust1n46.chat.command.message.Spy;
import mineverse.Aust1n46.chat.command.mute.Mute;
import mineverse.Aust1n46.chat.command.mute.Muteall;
import mineverse.Aust1n46.chat.command.mute.Unmute;
import mineverse.Aust1n46.chat.command.mute.Unmuteall;
import mineverse.Aust1n46.chat.utilities.Format;

/**
 * Class that initializes and executes the plugin's commands.
 */
public class VentureCommandExecutor {
	private static final String VERSION = "3.3.0";
	private static final Map<String, Command> commands = new HashMap<>();
	private static final MineverseChat plugin = MineverseChat.getInstance();

	private static Map<String, Command> knownCommands;

	@SuppressWarnings("unchecked")
	public static void initialize() {
		final Server server = plugin.getServer();
		final File commandsFile = new File(plugin.getDataFolder().getAbsolutePath(), "commands.yml");
		if (!commandsFile.isFile()) {
			plugin.saveResource("commands.yml", true);
		}
		FileConfiguration commandsFileConfiguration = YamlConfiguration.loadConfiguration(commandsFile);
		final String fileVersion = commandsFileConfiguration.getString("Version", "null");
		if (!fileVersion.equals(VERSION)) {
			server.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Version Change Detected!  Saving Old commands.yml and Generating Latest File"));
			commandsFile.renameTo(new File(plugin.getDataFolder().getAbsolutePath(), "commands_old_" + fileVersion + ".yml"));
			plugin.saveResource("commands.yml", true);
			commandsFileConfiguration = YamlConfiguration.loadConfiguration(commandsFile);
		}
		try {
			knownCommands = server.getCommandMap().getKnownCommands(); // Paper :)
		}
		// Spigot :(
		catch (final NoSuchMethodError error) {
			try {
				final Field commandMapField = server.getClass().getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				final SimpleCommandMap simpleCommandMap = (SimpleCommandMap) commandMapField.get(server);
				final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
				knownCommandsField.setAccessible(true);
				knownCommands = (Map<String, Command>) knownCommandsField.get(simpleCommandMap);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				server.getConsoleSender()
						.sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Unable to access CommandMap on Spigot. If this issue persists, try using Paper."));
				e.printStackTrace();
			}
		}
		commands.put("broadcast", new Broadcast());
		commands.put("channel", new Channel());
		commands.put("channelinfo", new Channelinfo());
		commands.put("chatinfo", new Chatinfo());
		commands.put("chatreload", new Chatreload());
		commands.put("chlist", new Chlist());
		commands.put("chwho", new Chwho());
		commands.put("clearchat", new Clearchat());
		commands.put("commandblock", new Commandblock());
		commands.put("commandspy", new Commandspy());
		commands.put("config", new Config());
		commands.put("edit", new Edit());
		commands.put("filter", new Filter());
		commands.put("force", new Force());
		commands.put("forceall", new Forceall());
		commands.put("kickchannel", new Kickchannel());
		commands.put("kickchannelall", new Kickchannelall());
		commands.put("leave", new Leave());
		commands.put("listen", new Listen());
		commands.put("me", new Me());
		commands.put("venturechat", new Venturechat());
		commands.put("notifications", new Notifications());
		commands.put("party", new Party());
		commands.put("rangedspy", new RangedSpy());
		commands.put("removemessage", new Removemessage());
		commands.put("setchannel", new Setchannel());
		commands.put("setchannelall", new Setchannelall());
		commands.put("spy", new Spy());
		commands.put("venturechatgui", new VentureChatGui());
		commands.put("messagetoggle", new MessageToggle());
		commands.put("bungeetoggle", new BungeeToggle());
		commands.put("mute", new Mute());
		commands.put("muteall", new Muteall());
		commands.put("unmute", new Unmute());
		commands.put("unmuteall", new Unmuteall());
		commands.put("reply", new Reply());
		commands.put("message", new Message());
		commands.put("ignore", new Ignore());
		final ChannelAlias channelAlias = new ChannelAlias();
		for (final ChatChannel chatChannel : ChatChannel.getChatChannels()) {
			final String alias = chatChannel.getAlias();
			commands.put(alias, channelAlias);
		}
		final ConfigurationSection commandsSection = commandsFileConfiguration.getConfigurationSection("commands");
		for (final String commandName : commandsSection.getKeys(false)) {
			final ConfigurationSection commandSection = commandsSection.getConfigurationSection(commandName);
			final boolean isEnabled = commandSection.getBoolean("enabled", true);
			if (!isEnabled) {
				commands.remove(commandName);
			} else {
				final Command command = commands.get(commandName);
				if (command != null) {
					final List<String> aliases = commandSection.getStringList("aliases");
					for (final String alias : aliases) {
						commands.put(alias, command);
					}
					commands.put("venturechat:" + commandName, command);
				}
			}
		}
		// Initial registration is required to ensure commands are recognized by the
		// server after enabling every plugin
		for (final Entry<String, Command> commandEntry : commands.entrySet()) {
			registerCommand(commandEntry.getKey(), commandEntry.getValue());
		}
		// Forcibly re-register enabled VentureChat commands on a delay to ensure they
		// have priority
		server.getScheduler().runTaskLater(plugin, () -> {
			for (final Entry<String, Command> commandEntry : commands.entrySet()) {
				registerCommand(commandEntry.getKey(), commandEntry.getValue());
			}
		}, 10);
	}

	public static void registerCommand(final String commandLabel, final Command command) {
		knownCommands.put(commandLabel, command);
	}
}
