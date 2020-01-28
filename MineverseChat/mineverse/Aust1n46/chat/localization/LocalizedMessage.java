package mineverse.Aust1n46.chat.localization;

import mineverse.Aust1n46.chat.utilities.Format;

/**
 * Messages configurable in Messages.yml
 */
public enum LocalizedMessage {
	BLOCKING_MESSAGE("BlockingMessage"),
	BUNGEE_TOGGLE_OFF("BungeeToggleOff"),
	BUNGEE_TOGGLE_ON("BungeeToggleOn"),
	CLEAR_CHAT_SENDER("ClearChatSender"),
	CLEAR_CHAT_SERVER("ClearChatServer"),
	CHANNEL_LIST("ChannelList"),
    CHANNEL_LIST_HEADER("ChannelListHeader"),
    CHANNEL_LIST_WITH_PERMISSIONS("ChannelListWithPermissions"),
    CHANNEL_NO_PERMISSION("ChannelNoPermission"),
    CHANNEL_NO_PERMISSION_VIEW("ChannelNoPermissionView"),
    CHANNEL_PLAYER_LIST_HEADER("ChannelPlayerListHeader"),
    COMMAND_INVALID_ARGUMENTS("CommandInvalidArguments"),
    COMMAND_MUST_BE_RUN_BY_PLAYER("CommandMustBeRunByPlayer"),
    COMMAND_NO_PERMISSION("CommandNoPermission"),
	CONFIG_RELOADED("ConfigReloaded"),
	EMPTY_STRING("EmptyString"),
	IGNORING_MESSAGE("IgnoringMessage"),
	INVALID_CHANNEL("InvalidChannel"),
	Z_END("End");

	private final String message;
	
    LocalizedMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
       return Format.FormatStringAll(Localization.getLocalization().getString(this.message));
    }
}