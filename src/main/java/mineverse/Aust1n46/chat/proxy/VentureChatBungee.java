package mineverse.Aust1n46.chat.proxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import mineverse.Aust1n46.chat.database.ProxyPlayerData;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.UUIDFetcher;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

/**
 * VentureChat Minecraft plugin for BungeeCord.
 *
 * @author Aust1n46
 */
public class VentureChatBungee extends Plugin implements Listener, VentureChatProxySource {
	private static Configuration bungeeConfig;
	private File bungeePlayerDataDirectory;

	@Override
	public void onEnable() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File config = new File(getDataFolder(), "bungeeconfig.yml");
		try {
			if(!config.exists()) {
				Files.copy(getResourceAsStream("bungeeconfig.yml"), config.toPath());
			}
			bungeeConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "bungeeconfig.yml"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		bungeePlayerDataDirectory = new File(getDataFolder().getAbsolutePath() + "/PlayerData");
		ProxyPlayerData.loadLegacyBungeePlayerData(bungeePlayerDataDirectory, this);
		ProxyPlayerData.loadProxyPlayerData(bungeePlayerDataDirectory, this);
		
		this.getProxy().registerChannel(VentureChatProxy.PLUGIN_MESSAGING_CHANNEL_STRING);
		this.getProxy().getPluginManager().registerListener(this, this);
	}

	@Override
	public void onDisable() {
		ProxyPlayerData.saveProxyPlayerData(bungeePlayerDataDirectory, this);
	}
	
	@EventHandler
	public void onPlayerJoin(ServerSwitchEvent event) {
		updatePlayerNames();
	}
	
	@EventHandler
	public void onPlayerLeave(ServerDisconnectEvent event) {
		updatePlayerNames();
	}
	
	@EventHandler
	public void onPlayerJoinNetwork(PostLoginEvent event) {
		UUIDFetcher.checkOfflineUUIDWarningProxy(event.getPlayer().getUniqueId(), this);
	}
	
	private void updatePlayerNames() {
		try {
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(outstream);
			out.writeUTF("PlayerNames");
			out.writeInt(getProxy().getPlayers().size());
			for(ProxiedPlayer pp : getProxy().getPlayers()) {
				out.writeUTF(pp.getName());
			}
			
			for(String send : getProxy().getServers().keySet()) {
				if(getProxy().getServers().get(send).getPlayers().size() > 0) {
					getProxy().getServers().get(send).sendData(VentureChatProxy.PLUGIN_MESSAGING_CHANNEL_STRING, outstream.toByteArray());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		if(!event.getTag().equals(VentureChatProxy.PLUGIN_MESSAGING_CHANNEL_STRING) && !event.getTag().contains("viaversion:")) {
			return;
		}
		// Critical to prevent client from sending or receiving messages
		event.setCancelled(true);

		if(!(event.getSender() instanceof Server)) {
			return;
		}
		String serverName = ((Server) event.getSender()).getInfo().getName();
		VentureChatProxy.onPluginMessage(event.getData(), serverName, this);
	}

	@Override
	public void sendPluginMessage(String serverName, byte[] data) {
		getProxy().getServers().get(serverName).sendData(VentureChatProxy.PLUGIN_MESSAGING_CHANNEL_STRING, data);
	}

	@Override
	public List<VentureChatProxyServer> getServers() {
		return getProxy().getServers().values().stream().map(bungeeServer -> new VentureChatProxyServer(bungeeServer.getName(), bungeeServer.getPlayers().isEmpty())).collect(Collectors.toList());
	}

	@Override
	public VentureChatProxyServer getServer(String serverName) {
		ServerInfo server = (ServerInfo) getProxy().getServers().get(serverName);
		return new VentureChatProxyServer(serverName, server.getPlayers().isEmpty());
	}

	@Override
	public void sendConsoleMessage(String message) {
		ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Format.FormatStringAll(message)));
	}

	@Override
	public boolean isOfflineServerAcknowledgementSet() {
		return bungeeConfig.getBoolean("offline_server_acknowledgement");
	}
}
