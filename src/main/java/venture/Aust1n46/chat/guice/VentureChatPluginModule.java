package venture.Aust1n46.chat.guice;

import com.google.inject.AbstractModule;

import venture.Aust1n46.chat.initiators.application.VentureChat;

public class VentureChatPluginModule extends AbstractModule {
	private final VentureChat plugin;

	public VentureChatPluginModule(final VentureChat plugin) {
		this.plugin = plugin;
	}

	@Override
	protected void configure() {
		this.bind(VentureChat.class).toInstance(plugin);
	}
}
