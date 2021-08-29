package mineverse.Aust1n46.chat.proxy;

public class VentureChatProxyServer {
	private String name;
	private boolean empty;
	
	public VentureChatProxyServer(String name, boolean empty) {
		this.name = name;
		this.empty = empty;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public String getName() {
		return name;
	}
}
