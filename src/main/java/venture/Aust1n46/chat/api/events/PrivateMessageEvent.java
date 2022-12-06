package venture.Aust1n46.chat.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import venture.Aust1n46.chat.model.VentureChatPlayer;

/**
 * Event called when a private message has been sent to a player.
 * This event can not be cancelled.
 *
 * @author LOOHP
 */
@Getter
public class PrivateMessageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final VentureChatPlayer sender;
    private final VentureChatPlayer receiver;
    private final String msg;
    private @Getter @Setter boolean cancelled;
    private @Setter String echo;
    private @Setter String send;
    private @Setter String spy;
    private final boolean bungee;

    public PrivateMessageEvent(VentureChatPlayer sender, VentureChatPlayer receiver, String msg, String echo, String send, String spy, boolean bungee, boolean async) {
        super(async);
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.echo = echo;
        this.send = send;
        this.spy = spy;
        this.bungee = bungee;
        this.cancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}