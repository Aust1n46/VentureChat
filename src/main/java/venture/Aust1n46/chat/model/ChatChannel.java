package venture.Aust1n46.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChatChannel {
	public static final String NO_PERMISSIONS = "venturechat.none";

	private String name;
	private String color;
	private String colorRaw;
	private String chatColor;
	private String chatColorRaw;
	private String permission;
	private String speakPermission;
	private boolean mutable;
	private boolean filtered;
	private boolean defaultChannel;
	private String alias;
	private double distance;
	private boolean autoJoinEnabled;
	private boolean bungeeEnabled;
	private int cooldown;
	private String format;
	private String prefix;

	public boolean isPermissionRequired() {
		return !NO_PERMISSIONS.equalsIgnoreCase(permission);
	}

	public boolean isSpeakPermissionRequired() {
		return !NO_PERMISSIONS.equalsIgnoreCase(speakPermission);
	}
}
