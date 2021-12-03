package venture.Aust1n46.chat;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import mineverse.Aust1n46.chat.localization.Localization;
import mineverse.Aust1n46.chat.utilities.FormatUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.controllers.VentureChatSpigotFlatFileController;
import venture.Aust1n46.chat.initiators.listeners.ChatListener;
import venture.Aust1n46.chat.initiators.listeners.CommandListener;
import venture.Aust1n46.chat.initiators.listeners.LoginListener;
import venture.Aust1n46.chat.initiators.listeners.PacketListener;
import venture.Aust1n46.chat.initiators.listeners.SignListener;
import venture.Aust1n46.chat.initiators.listeners.VentureCommandExecutor;
import venture.Aust1n46.chat.initiators.schedulers.UnmuteScheduler;
import venture.Aust1n46.chat.model.Alias;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.GuiSlot;
import venture.Aust1n46.chat.model.JsonFormat;
import venture.Aust1n46.chat.service.VentureChatDatabaseService;
import venture.Aust1n46.chat.service.VentureChatFormatService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

/**
 * VentureChat Minecraft plugin for servers running Spigot or Paper software.
 *
 * @author Aust1n46
 */
@Singleton
public class VentureChat extends JavaPlugin implements PluginMessageListener {
	public static final boolean ASYNC = true;
	public static final boolean SYNC = false;
	public static final int LINE_LENGTH = 40;
	
	@Inject
	private LoginListener loginListener;
	@Inject
	private ChatListener chatListener;
	@Inject
	private SignListener signListener;
	@Inject
	private CommandListener commandListener;
	@Inject
	private VentureCommandExecutor commandExecutor;
	@Inject
	private PacketListener packetListener;
	@Inject
	private VentureChatPlaceholders ventureChatPlaceholders;
	@Inject
	private VentureChatFormatService formatter;
	@Inject
	private VentureChatDatabaseService databaseService;
	@Inject
	private VentureChatSpigotFlatFileController spigotFlatFileService;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private UnmuteScheduler unmuteScheduler;
	@Inject
	private PluginMessageController pluginMessageController;
	
	private Permission permission = null;
	
	@Override
	public void onEnable() {
		VentureChatPluginModule pluginModule = new VentureChatPluginModule(this);
		pluginModule.createInjector().injectMembers(this);
		
		try {
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Initializing..."));
			if(!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
			if(!file.exists()) {
				Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Config not found! Generating file."));
				saveDefaultConfig();
			}
			else {
				Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Config found! Loading file."));
			}

			saveResource("example_config_always_up_to_date!.yml", true);
		}
		catch(Exception ex) {
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - &cCould not load configuration! Something unexpected went wrong!"));
		}
		
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Checking for Vault..."));
		
		if(!setupPermissions() || !setupChat()) {
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - &cCould not find Vault and/or a Vault compatible permissions plugin!"));
			Bukkit.getPluginManager().disablePlugin(this);
		}

		initializeConfigReaders();
		
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Loading player data"));
		spigotFlatFileService.loadLegacyPlayerData();
		spigotFlatFileService.loadPlayerData();

		registerListeners();
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Registering Listeners"));
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Attaching to Executors"));
		
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Establishing BungeeCord"));
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, PluginMessageController.PLUGIN_MESSAGING_CHANNEL);
		Bukkit.getMessenger().registerIncomingPluginChannel(this, PluginMessageController.PLUGIN_MESSAGING_CHANNEL, this);
		
		PluginManager pluginManager = getServer().getPluginManager();
		if(pluginManager.isPluginEnabled("Towny")) {
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Towny Formatting"));
		}
		if(pluginManager.isPluginEnabled("Jobs")) {
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Jobs Formatting"));
		}
		if(pluginManager.isPluginEnabled("Factions")) {
			String version = pluginManager.getPlugin("Factions").getDescription().getVersion();
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Enabling Factions Formatting version " + version));
		}
		if(pluginManager.isPluginEnabled("PlaceholderAPI")) {
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Enabling PlaceholderAPI Hook"));
		}
		
		ventureChatPlaceholders.register();
		
		startRepeatingTasks();
		
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Enabled Successfully"));	
	}
	
	@Override
	public void onLoad() {
		//new DebugLoggingProvider().enableDebugLogging();
	}
	
	@Override
	public void onDisable() {
		spigotFlatFileService.savePlayerData();
		playerApiService.clearMineverseChatPlayerMap();
		playerApiService.clearNameMap();
		playerApiService.clearOnlineMineverseChatPlayerMap();
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Disabling..."));
		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Disabled Successfully"));
	}
	
	private void startRepeatingTasks() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				spigotFlatFileService.savePlayerData();
				if(getConfig().getString("loglevel", "info").equals("debug")) {
					Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Saving Player Data"));
				}
			}
		}, 0L, getConfig().getInt("saveinterval") * 1200); //one minute * save interval
	}
	
	private void registerListeners() {
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(chatListener, this);
		pluginManager.registerEvents(signListener, this);
		pluginManager.registerEvents(commandListener, this);
		pluginManager.registerEvents(loginListener, this);
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
	}
	
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if(permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return(permission != null);
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		Chat chat = null;
		if(chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return(chat != null);
	}
	
	public void initializeConfigReaders() {
		Localization.initialize(this);
		Alias.initialize(this);
		JsonFormat.initialize(this);
		GuiSlot.initialize(this);
		ChatChannel.initialize(this, formatter, false);
	}
	
	public Permission getVaultPermission() {
		return permission;
	}

	@Override
	public void onPluginMessageReceived(final String channel, final Player player, final byte[] inputStream) {
		pluginMessageController.processInboundPluginMessage(channel, player, inputStream);
	}
}
