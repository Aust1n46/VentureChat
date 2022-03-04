package venture.Aust1n46.chat.controllers;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.controllers.commands.Broadcast;
import venture.Aust1n46.chat.controllers.commands.BungeeToggle;
import venture.Aust1n46.chat.controllers.commands.Channel;
import venture.Aust1n46.chat.controllers.commands.Channelinfo;
import venture.Aust1n46.chat.controllers.commands.Chatinfo;
import venture.Aust1n46.chat.controllers.commands.Chatreload;
import venture.Aust1n46.chat.controllers.commands.Chlist;
import venture.Aust1n46.chat.controllers.commands.Chwho;
import venture.Aust1n46.chat.controllers.commands.Clearchat;
import venture.Aust1n46.chat.controllers.commands.Commandblock;
import venture.Aust1n46.chat.controllers.commands.Commandspy;
import venture.Aust1n46.chat.controllers.commands.Edit;
import venture.Aust1n46.chat.controllers.commands.Filter;
import venture.Aust1n46.chat.controllers.commands.Force;
import venture.Aust1n46.chat.controllers.commands.Forceall;
import venture.Aust1n46.chat.controllers.commands.IgnoreCommandExecutor;
import venture.Aust1n46.chat.controllers.commands.Kickchannel;
import venture.Aust1n46.chat.controllers.commands.Kickchannelall;
import venture.Aust1n46.chat.controllers.commands.Leave;
import venture.Aust1n46.chat.controllers.commands.Listen;
import venture.Aust1n46.chat.controllers.commands.Me;
import venture.Aust1n46.chat.controllers.commands.MessageCommandExecutor;
import venture.Aust1n46.chat.controllers.commands.MessageToggle;
import venture.Aust1n46.chat.controllers.commands.Mute;
import venture.Aust1n46.chat.controllers.commands.Muteall;
import venture.Aust1n46.chat.controllers.commands.Notifications;
import venture.Aust1n46.chat.controllers.commands.Party;
import venture.Aust1n46.chat.controllers.commands.RangedSpy;
import venture.Aust1n46.chat.controllers.commands.Removemessage;
import venture.Aust1n46.chat.controllers.commands.Reply;
import venture.Aust1n46.chat.controllers.commands.Setchannel;
import venture.Aust1n46.chat.controllers.commands.Setchannelall;
import venture.Aust1n46.chat.controllers.commands.Spy;
import venture.Aust1n46.chat.controllers.commands.Unmute;
import venture.Aust1n46.chat.controllers.commands.Unmuteall;
import venture.Aust1n46.chat.controllers.commands.VentureChatGui;
import venture.Aust1n46.chat.controllers.commands.Venturechat;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.utilities.FormatUtils;

/**
 * Class that initializes and executes the plugin's commands.
 */
@Singleton
public class CommandController implements TabExecutor {
	private static final String COMMAND_CONFIG_VERSION = "3.3.0";

	private Map<String, VentureCommand> commandsOld = new HashMap<>();

	@Inject
	private VentureChat plugin;
	@Inject
	private MessageCommandExecutor messageCommandExecutor;
	@Inject
	private IgnoreCommandExecutor ignoreCommandExecutor;

	@Inject
	private Broadcast broadcast;
	@Inject
	private Channel channel;
	@Inject
	private Channelinfo channelinfo;
	@Inject
	private Chatinfo chatinfo;
	@Inject
	private Chatreload chatreload;
	@Inject
	private Chlist chlist;
	@Inject
	private Chwho chwho;
	@Inject
	private Clearchat clearchat;
	@Inject
	private Commandblock commandblock;
	@Inject
	private Commandspy commandspy;
	@Inject
	private Edit edit;
	@Inject
	private Filter filter;
	@Inject
	private Force force;
	@Inject
	private Forceall forceall;
	@Inject
	private Kickchannel kickchannel;
	@Inject
	private Kickchannelall kickchannelall;
	@Inject
	private Leave leave;
	@Inject
	private Listen listen;
	@Inject
	private Me me;
	@Inject
	private Venturechat venturechat;
	@Inject
	private Notifications notifications;
	@Inject
	private Party party;
	@Inject
	private RangedSpy rangedSpy;
	@Inject
	private Removemessage removemessage;
	@Inject
	private Setchannel setchannel;
	@Inject
	private Setchannelall setchannelall;
	@Inject
	private Spy spy;
	@Inject
	private VentureChatGui ventureChatGui;
	@Inject
	private MessageToggle messageToggle;
	@Inject
	private BungeeToggle bungeeToggle;
	@Inject
	private Reply reply;
	@Inject
	private Mute mute;
	@Inject
	private Muteall muteall;
	@Inject
	private Unmute unmute;
	@Inject
	private Unmuteall unmuteall;

	private Constructor<PluginCommand> pluginCommandConstructor;

	private final Map<String, Command> commands = new HashMap<>();
	private Map<String, Command> knownCommands;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] parameters) {
		commandsOld.get(command.getName()).execute(sender, command.getName(), parameters);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commandsOld.get(command.getName()).onTabComplete(sender, command, label, args);
	}

	@SuppressWarnings("unchecked")
	@Inject
	public void postConstruct() {
		final Server server = plugin.getServer();
		final File commandsFile = new File(plugin.getDataFolder().getAbsolutePath(), "commands.yml");
		if (!commandsFile.isFile()) {
			plugin.saveResource("commands.yml", true);
		}
		FileConfiguration commandsFileConfiguration = YamlConfiguration.loadConfiguration(commandsFile);
		final String fileVersion = commandsFileConfiguration.getString("Version", "null");
		if (!fileVersion.equals(COMMAND_CONFIG_VERSION)) {
			server.getConsoleSender()
					.sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Version Change Detected!  Saving Old commands.yml and Generating Latest File"));
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
						.sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Unable to access CommandMap on Spigot. If this issue persists, try using Paper."));
				e.printStackTrace();
			}
		}
		try {
			pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			pluginCommandConstructor.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		commandsOld.put("broadcast", broadcast);
//		commandsOld.put("channel", channel);
//		commandsOld.put("join", channel);
		commandsOld.put("channelinfo", channelinfo);
		commandsOld.put("chatinfo", chatinfo);
		commandsOld.put("chatreload", chatreload);
		commandsOld.put("chlist", chlist);
		commandsOld.put("chwho", chwho);
		commandsOld.put("clearchat", clearchat);
		commandsOld.put("commandblock", commandblock);
		commandsOld.put("commandspy", commandspy);
		commandsOld.put("edit", edit);
		commandsOld.put("filter", filter);
		commandsOld.put("force", force);
		commandsOld.put("forceall", forceall);
		commandsOld.put("kickchannel", kickchannel);
		commandsOld.put("kickchannelall", kickchannelall);
		commandsOld.put("leave", leave);
		commandsOld.put("listen", listen);
		commandsOld.put("me", me);
		commandsOld.put("venturechat", venturechat);
		commandsOld.put("notifications", notifications);
		commandsOld.put("party", party);
		commandsOld.put("rangedspy", rangedSpy);
		commandsOld.put("removemessage", removemessage);
		commandsOld.put("setchannel", setchannel);
		commandsOld.put("setchannelall", setchannelall);
		commandsOld.put("spy", spy);
		commandsOld.put("venturechatgui", ventureChatGui);
		commandsOld.put("messagetoggle", messageToggle);
		commandsOld.put("bungeetoggle", bungeeToggle);
		for (String command : commandsOld.keySet()) {
			registerCommand(command, this);
		}

		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			if (plugin.isEnabled()) {
				commandsOld.put("reply", reply);
				commandsOld.put("r", reply);
				registerCommand("reply", this);
				registerCommand("r", this);

				commandsOld.put("mute", mute);
				commandsOld.put("muteall", muteall);
				commandsOld.put("unmute", unmute);
				commandsOld.put("unmuteall", unmuteall);
				registerCommand("mute", this);
				registerCommand("muteall", this);
				registerCommand("unmute", this);
				registerCommand("unmuteall", this);

				registerCommand("message", messageCommandExecutor);
				registerCommand("msg", messageCommandExecutor);
				registerCommand("tell", messageCommandExecutor);
				registerCommand("whisper", messageCommandExecutor);

				registerCommand("ignore", ignoreCommandExecutor);
			}
		}, 0);
		
		
		registerCommand("channel", channel);
	}

	private void registerCommand(final String command, final CommandExecutor commandExecutor) {
		try {
			final PluginCommand pluginCommand = pluginCommandConstructor.newInstance(command, plugin);
			pluginCommand.setExecutor(commandExecutor);
			knownCommands.put(command, pluginCommand);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void registerCommand(final String commandLabel, final Command command) {
		knownCommands.put(commandLabel, command);
	}
}
