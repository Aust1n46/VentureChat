package venture.Aust1n46.chat.proxy;

import java.util.List;

public interface VentureChatProxySource {
	public void sendPluginMessage(String serverName, byte[] data);
	
	public List<VentureChatProxyServer> getServers();
	
	public VentureChatProxyServer getServer(String serverName);
	
	public void sendConsoleMessage(String message);
	
	public boolean isOfflineServerAcknowledgementSet();
}
