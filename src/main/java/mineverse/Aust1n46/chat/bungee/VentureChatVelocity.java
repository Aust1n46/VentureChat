package mineverse.Aust1n46.chat.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

@Plugin(id = "venturechat", name = "VentureChat", version = "3.1.0",
description = "#1 Channels Chat plugin! Spigot + Bungee. Supports PlaceholderAPI + JSON formatting. Moderation GUI!", authors = {"Aust1n46"})
public class VentureChatVelocity implements VentureChatProxySource {
	private final ProxyServer proxyServer;
	private final ChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.create(VentureChatProxy.PLUGIN_MESSAGING_CHANNEL_NAMESPACE, VentureChatProxy.PLUGIN_MESSAGING_CHANNEL_NAME);

	@Inject
	public VentureChatVelocity(ProxyServer server, Logger logger) {
		this.proxyServer = server;
	}
	
	@Subscribe
	public void onInitialize(ProxyInitializeEvent event) {
		proxyServer.getChannelRegistrar().register(channelIdentifier);
	}
	
	@Subscribe
	public void onPlayerJoin(ServerPostConnectEvent event) {
		updatePlayerNames();
	}
	
	@Subscribe
	public void onPlayerQuit(DisconnectEvent event) {
		updatePlayerNames();
	}
	
	private void updatePlayerNames() {
		try {
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(outstream);
			out.writeUTF("PlayerNames");
			out.writeInt(proxyServer.getPlayerCount());
			for(Player player : proxyServer.getAllPlayers()) {
				out.writeUTF(player.getUsername());
			}
			getServers().forEach(send -> {
				if(!send.isEmpty()) {
					sendPluginMessage(send.getName(), outstream.toByteArray());
				}
			});
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Subscribe
	public void onPluginMessage(PluginMessageEvent event) {
		String channelIdentifierId = event.getIdentifier().getId();
		if(!channelIdentifierId.equals(VentureChatProxy.PLUGIN_MESSAGING_CHANNEL_STRING) && !channelIdentifierId.contains("viaversion:")) {
			return;
		}
		if(!(event.getSource() instanceof ServerConnection)) {
			return;
		}
		String serverName = ((ServerConnection) event.getSource()).getServerInfo().getName();
		VentureChatProxy.onPluginMessage(event.getData(), serverName, this);
		event.setResult(ForwardResult.handled());
	}

	@Override
	public void sendPluginMessage(String serverName, byte[] data) {
		Optional<RegisteredServer> server = proxyServer.getServer(serverName);
		if(server.isPresent()) {
			server.get().sendPluginMessage(channelIdentifier, data);
		}
	}

	@Override
	public List<VentureChatProxyServer> getServers() {
		return proxyServer.getAllServers().stream().map(velocityServer -> new VentureChatProxyServer(velocityServer.getServerInfo().getName(), velocityServer.getPlayersConnected().isEmpty())).collect(Collectors.toList());
	}

	@Override
	public VentureChatProxyServer getServer(String serverName) {
		RegisteredServer server = proxyServer.getServer(serverName).get();
		return new VentureChatProxyServer(serverName, server.getPlayersConnected().isEmpty());
	}
}
