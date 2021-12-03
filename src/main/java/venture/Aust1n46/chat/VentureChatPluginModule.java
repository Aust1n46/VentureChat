package venture.Aust1n46.chat;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class VentureChatPluginModule extends AbstractModule {
	private final VentureChat plugin;

	public VentureChatPluginModule(final VentureChat plugin) {
		this.plugin = plugin;
	}

	public Injector createInjector() {
		return Guice.createInjector(this);
	}

	@Override
	protected void configure() {
		this.bind(VentureChat.class).toInstance(plugin);
	}
}
