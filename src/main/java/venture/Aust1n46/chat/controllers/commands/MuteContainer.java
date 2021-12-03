package venture.Aust1n46.chat.controllers.commands;

public class MuteContainer {
    private String channel;
    private String reason;
    private long duration;

    public MuteContainer(String channel) {
        this(channel, 0, "");
    }

    public MuteContainer(String channel, long duration) {
        this(channel, duration, "");
    }

    public MuteContainer(String channel, String reason) {
        this(channel, 0, reason);
    }

    public MuteContainer(String channel, long duration, String reason) {
        this.channel = channel;
        this.reason = reason;
        this.duration = duration;
    }

    public String getChannel() {
        return channel;
    }

    public boolean hasReason() {
        return !reason.equals("");
    }

    public String getReason() {
        return reason;
    }

    public boolean hasDuration() {
        return duration > 0;
    }

    public long getDuration() {
        return duration;
    }
}
