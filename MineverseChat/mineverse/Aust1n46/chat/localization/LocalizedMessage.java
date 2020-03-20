package mineverse.Aust1n46.chat.localization;

import mineverse.Aust1n46.chat.utilities.Format;

/**
 * Messages configurable in Messages.yml
 */
public enum LocalizedMessage {
	BLOCK_COMMAND_PLAYER("BlockCommandPlayer"),
	BLOCK_COMMAND_SENDER("BlockCommandSender"),
	BLOCKING_MESSAGE("BlockingMessage"),
	BUNGEE_TOGGLE_OFF("BungeeToggleOff"),
	BUNGEE_TOGGLE_ON("BungeeToggleOn"),
	CLEAR_CHAT_SENDER("ClearChatSender"),
	CLEAR_CHAT_SERVER("ClearChatServer"),
	CHANNEL_CANNOT_MUTE("ChannelCannotMute"),
	CHANNEL_LIST("ChannelList"),
    CHANNEL_LIST_HEADER("ChannelListHeader"),
    CHANNEL_LIST_WITH_PERMISSIONS("ChannelListWithPermissions"),
    CHANNEL_NO_PERMISSION("ChannelNoPermission"),
    CHANNEL_NO_PERMISSION_VIEW("ChannelNoPermissionView"),
    CHANNEL_PLAYER_LIST_HEADER("ChannelPlayerListHeader"),
    COMMAND_INVALID_ARGUMENTS("CommandInvalidArguments"),
    COMMAND_INVALID_ARGUMENTS_IGNORE("CommandInvalidArgumentsIgnore"),
    COMMAND_MUST_BE_RUN_BY_PLAYER("CommandMustBeRunByPlayer"),
    COMMAND_NO_PERMISSION("CommandNoPermission"),
    COMMAND_NOT_BLOCKABLE("CommandNotBlockable"),
    COMMANDSPY_OFF("CommandSpyOff"),
    COMMANDSPY_ON("CommandSpyOn"),
	CONFIG_RELOADED("ConfigReloaded"),
	ENTER_PRIVATE_CONVERSATION("EnterPrivateConversation"),
	ENTER_PRIVATE_CONVERSATION_SPY("EnterPrivateConversationSpy"),
	EXIT_PRIVATE_CONVERSATION("ExitPrivateConversation"),
	EXIT_PRIVATE_CONVERSATION_SPY("ExitPrivateConversationSpy"),
	FILTER_OFF("FilterOff"),
	FILTER_ON("FilterOn"),
	FORCE_ALL("ForceAll"),
	FORCE_PLAYER("ForcePlayer"),
	IGNORE_LIST_HEADER("IgnoreListHeader"),
	IGNORE_PLAYER_CANT("IgnorePlayerCant"),
	IGNORE_PLAYER_OFF("IgnorePlayerOff"),
	IGNORE_PLAYER_ON("IgnorePlayerOn"),
	IGNORE_YOURSELF("IgnoreYourself"),
	IGNORING_MESSAGE("IgnoringMessage"),
	INVALID_CHANNEL("InvalidChannel"),
	INVALID_HASH("InvalidHash"),
	INVALID_TIME("InvalidTime"),
	KICK_CHANNEL("KickChannel"),
	KICK_CHANNEL_ALL_PLAYER("KickChannelAllPlayer"),
	KICK_CHANNEL_ALL_SENDER("KickChannelAllSender"),
	LEAVE_CHANNEL("LeaveChannel"),
	LISTEN_CHANNEL("ListenChannel"),
	MESSAGE_TOGGLE_OFF("MessageToggleOff"),
	MESSAGE_TOGGLE_ON("MessageToggleOn"),
	MUST_LISTEN_ONE_CHANNEL("MustListenOneChannel"),
	MUTE_PLAYER_ALL_PLAYER("MutePlayerAllPlayer"),
	MUTE_PLAYER_ALL_SENDER("MutePlayerAllSender"),
	MUTE_PLAYER_PLAYER("MutePlayerPlayer"),
	MUTE_PLAYER_PLAYER_TIME("MutePlayerPlayerTime"),
	MUTE_PLAYER_SENDER("MutePlayerSender"),
	MUTE_PLAYER_SENDER_TIME("MutePlayerSenderTime"),
	NO_PLAYER_TO_REPLY_TO("NoPlayerToReplyTo"),
	NOTIFICATIONS_OFF("NotificationsOff"),
	NOTIFICATIONS_ON("NotificationsOn"),
	PLAYER_ALREADY_MUTED("PlayerAlreadyMuted"),
	PLAYER_NOT_MUTED("PlayerNotMuted"),
	PLAYER_OFFLINE("PlayerOffline"),
	PLAYER_OFFLINE_NO_PERMISSIONS_CHECK("PlayerOfflineNoPermissionsCheck"),
	RANGED_SPY_OFF("RangedSpyOff"),
	RANGED_SPY_ON("RangedSpyOn"),
	SET_CHANNEL("SetChannel"),
	SET_CHANNEL_ALL_PLAYER("SetChannelAllPlayer"),
	SET_CHANNEL_ALL_SENDER("SetChannelAllSender"),
	SET_CHANNEL_PLAYER_CHANNEL_NO_PERMISSION("SetChannelPlayerChannelNoPermission"),
	SET_CHANNEL_SENDER("SetChannelSender"),
	SPY_OFF("SpyOff"),
	SPY_ON("SpyOn"),
	UNBLOCK_COMMAND_PLAYER("UnblockCommandPlayer"),
	UNBLOCK_COMMAND_SENDER("UnblockCommandSender"),
	UNMUTE_PLAYER_ALL_PLAYER("UnmutePlayerAllPlayer"),
	UNMUTE_PLAYER_ALL_SENDER("UnmutePlayerAllSender"),
	UNMUTE_PLAYER_PLAYER("UnmutePlayerPlayer"),
	UNMUTE_PLAYER_SENDER("UnmutePlayerSender");

	private final String message;
	
    LocalizedMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
       return Format.FormatStringAll(Localization.getLocalization().getString(this.message));
    }
}