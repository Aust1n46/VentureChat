package mineverse.Aust1n46.chat.irc;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.irc.command.IRCCommandInfo;
import mineverse.Aust1n46.chat.irc.listeners.MessageListener;

import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.hooks.ListenerAdapter;

//This class is the IRC bot using the PircBotX library, the bot is setup and connected to the IRC channel in this class.
@SuppressWarnings("rawtypes")
public class Bot extends ListenerAdapter {
	public PircBotX bot;
	public String channel;
	private MineverseChat plugin;
	private IRCCommandInfo ircc;
	private ChatChannelInfo cc;

	public Bot(MineverseChat plugin, ChatChannelInfo cc, IRCCommandInfo ircc) {
		this.plugin = plugin;
		this.ircc = ircc;
		this.cc = cc;
	}

	@SuppressWarnings("unchecked")
	public void init() {
		ConfigurationSection cs = plugin.getConfig().getConfigurationSection("irc");
		Configuration configuration = new Configuration.Builder().setName(cs.getString("nick")).setLogin(cs.getString("login")).setAutoNickChange(true).setCapEnabled(true).addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), cs.getBoolean("trustallcertificates"))).setServer(cs.getString("server"), cs.getInt("port")).addListener(new MessageListener(cc, ircc)).addAutoJoinChannel(cs.getString("channel")).buildConfiguration();
		channel = cs.getString("channel");
		bot = new PircBotX(configuration);
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					bot.startBot();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				bot.sendIRC().joinChannel(plugin.getConfig().getConfigurationSection("irc").getString("channel"));
				System.out.println("Bot logging into channel.");
			}
		}, cs.getLong("channeldelay") * 20);
	}

	public void terminate() {
		bot.sendIRC().quitServer("VentureChat Bot");
	}
}