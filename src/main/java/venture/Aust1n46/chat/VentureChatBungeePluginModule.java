package venture.Aust1n46.chat;

import com.google.inject.AbstractModule;

import venture.Aust1n46.chat.proxy.VentureChatBungee;

public class VentureChatBungeePluginModule extends AbstractModule {
	private final VentureChatBungee plugin;

	public VentureChatBungeePluginModule(final VentureChatBungee plugin) {
		this.plugin = plugin;
	}

	@Override
	protected void configure() {
		this.bind(VentureChatBungee.class).toInstance(plugin);
	}
}
