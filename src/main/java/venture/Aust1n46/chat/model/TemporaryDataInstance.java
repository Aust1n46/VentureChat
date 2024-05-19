package venture.Aust1n46.chat.model;

import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;

public class TemporaryDataInstance {
	private final UUID uuid;
	@Getter
	private int messagePackets;

	private static final HashMap<UUID, TemporaryDataInstance> TEMPORARY_DATA_INSTANCES = new HashMap<>();

	private TemporaryDataInstance(final UUID uuid) {
		this.uuid = uuid;
	}

	public static UUID createTemporaryDataInstance() {
		final UUID uuid = UUID.randomUUID();
		TEMPORARY_DATA_INSTANCES.put(uuid, new TemporaryDataInstance(uuid));
		return uuid;
	}

	public static TemporaryDataInstance getTemporaryDataInstance(final UUID uuid) {
		return TEMPORARY_DATA_INSTANCES.get(uuid);
	}

	public void incrementMessagePackets() {
		messagePackets++;
	}

	public void destroyInstance() {
		TEMPORARY_DATA_INSTANCES.remove(uuid);
	}
}
