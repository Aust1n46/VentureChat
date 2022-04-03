package venture.Aust1n46.chat.controllers;

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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.controllers.commands.Broadcast;
import venture.Aust1n46.chat.controllers.commands.BungeeToggle;
import venture.Aust1n46.chat.controllers.commands.Channel;
import venture.Aust1n46.chat.controllers.commands.ChannelAlias;
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
import venture.Aust1n46.chat.controllers.commands.Ignore;
import venture.Aust1n46.chat.controllers.commands.Kickchannel;
import venture.Aust1n46.chat.controllers.commands.Kickchannelall;
import venture.Aust1n46.chat.controllers.commands.Leave;
import venture.Aust1n46.chat.controllers.commands.Listen;
import venture.Aust1n46.chat.controllers.commands.Me;
import venture.Aust1n46.chat.controllers.commands.Message;
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
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.utilities.FormatUtils;

@Singleton
public class CommandController {
	private static final String COMMAND_CONFIG_VERSION = "3.3.0";

	@Inject
	private VentureChat plugin;
	@Inject
	private ConfigService configService;

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
	@Inject
	private Message message;
	@Inject
	private Ignore ignore;
	@Inject
	private ChannelAlias channelAlias;

	private final Map<String, Command> commands = new HashMap<>();
	private Map<String, Command> knownCommands;

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
		commands.put("broadcast", broadcast);
		commands.put("channel", channel);
		commands.put("join", channel);
		commands.put("channelinfo", channelinfo);
		commands.put("chatinfo", chatinfo);
		commands.put("chatreload", chatreload);
		commands.put("chlist", chlist);
		commands.put("chwho", chwho);
		commands.put("clearchat", clearchat);
		commands.put("commandblock", commandblock);
		commands.put("commandspy", commandspy);
		commands.put("edit", edit);
		commands.put("filter", filter);
		commands.put("force", force);
		commands.put("forceall", forceall);
		commands.put("kickchannel", kickchannel);
		commands.put("kickchannelall", kickchannelall);
		commands.put("leave", leave);
		commands.put("listen", listen);
		commands.put("me", me);
		commands.put("venturechat", venturechat);
		commands.put("notifications", notifications);
		commands.put("party", party);
		commands.put("rangedspy", rangedSpy);
		commands.put("removemessage", removemessage);
		commands.put("setchannel", setchannel);
		commands.put("setchannelall", setchannelall);
		commands.put("spy", spy);
		commands.put("venturechatgui", ventureChatGui);
		commands.put("messagetoggle", messageToggle);
		commands.put("bungeetoggle", bungeeToggle);
		commands.put("reply", reply);
		commands.put("mute", mute);
		commands.put("muteall", muteall);
		commands.put("unmute", unmute);
		commands.put("unmuteall", unmuteall);
		commands.put("message", message);
		commands.put("ignore", ignore);
		for (final ChatChannel chatChannel : configService.getChatChannels()) {
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

	private void registerCommand(final String commandLabel, final Command command) {
		knownCommands.put(commandLabel, command);
	}
}
