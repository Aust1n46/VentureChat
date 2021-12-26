package venture.Aust1n46.chat.initiators.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

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

/**
 * Class that initializes and executes the plugin's commands.
 */
@Singleton
public class VentureCommandExecutor implements TabExecutor {
	private Map<String, VentureCommand> commands = new HashMap<String, VentureCommand>();

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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] parameters) {
		commands.get(command.getName()).execute(sender, command.getName(), parameters);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onTabComplete(sender, command, label, args);
	}

	@Inject
	public void postConstruct() {
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
		for (String command : commands.keySet()) {
			registerCommand(command, this);
		}

		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			if (plugin.isEnabled()) {
				commands.put("reply", reply);
				commands.put("r", reply);
				registerCommand("reply", this);
				registerCommand("r", this);

				commands.put("mute", mute);
				commands.put("muteall", muteall);
				commands.put("unmute", unmute);
				commands.put("unmuteall", unmuteall);
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
	}

	private void registerCommand(String command, CommandExecutor commandExecutor) {
		if (plugin.getCommand(command) != null) {
			plugin.getCommand(command).setExecutor(commandExecutor);
		}
	}
}
