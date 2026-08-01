package mineverse.Aust1n46.chat.utilities;

/**
 * Size checks shared by the backend and proxy plugin messaging transports.
 */
public final class PluginMessageUtil {
	public static final int MAX_SERVERBOUND_PAYLOAD_SIZE = 32767;

	private PluginMessageUtil() {
	}

	public static boolean exceedsServerboundLimit(byte[] data) {
		return data.length > MAX_SERVERBOUND_PAYLOAD_SIZE;
	}

	public static String createDropMessage(int payloadSize) {
		return "&8[&eVentureChat&8]&c Dropping this message because it is " + payloadSize
				+ " bytes, which exceeds the plugin messaging channel limit of "
				+ MAX_SERVERBOUND_PAYLOAD_SIZE + " bytes.";
	}
}
