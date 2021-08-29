package mineverse.Aust1n46.chat.bungee;

import java.util.List;

public interface VentureChatProxySource {
	public void sendPluginMessage(String serverName, byte[] data);
	
	public List<VentureChatProxyServer> getServers();
	
	public VentureChatProxyServer getServer(String serverName);
	
	public void sendConsoleMessage(String message);
	
	public boolean isOfflineServerAcknowledgementSet();
}
