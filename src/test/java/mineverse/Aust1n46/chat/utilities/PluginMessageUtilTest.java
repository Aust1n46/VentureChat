package mineverse.Aust1n46.chat.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PluginMessageUtilTest {
	@Test
	public void testPayloadAtLimitIsAccepted() {
		assertFalse(PluginMessageUtil.exceedsServerboundLimit(
				new byte[PluginMessageUtil.MAX_SERVERBOUND_PAYLOAD_SIZE]));
	}

	@Test
	public void testPayloadAboveLimitIsDropped() {
		int payloadSize = PluginMessageUtil.MAX_SERVERBOUND_PAYLOAD_SIZE + 1;

		assertTrue(PluginMessageUtil.exceedsServerboundLimit(new byte[payloadSize]));
		assertTrue(PluginMessageUtil.createDropMessage(payloadSize).contains(payloadSize + " bytes"));
	}
}
