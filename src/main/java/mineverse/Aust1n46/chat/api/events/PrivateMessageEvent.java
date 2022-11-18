package mineverse.Aust1n46.chat.api.events;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a message has been sent to a channel.
 * This event can not be cancelled.
 *
 * @author Aust1n46
 */
public class PrivateMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();


    private final MineverseChatPlayer from;
    private final MineverseChatPlayer to;
    private String chat;
    private boolean cancelled;
    private String errorMessage;

    public PrivateMessageEvent(MineverseChatPlayer from, MineverseChatPlayer to, String chat) {
        super(MineverseChat.ASYNC);
        this.from = from;
        this.to = to;
        this.chat = chat;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MineverseChatPlayer getFrom() {
        return from;
    }

    public MineverseChatPlayer getTo() {
        return to;
    }

    public String getChat() {
        return this.chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}