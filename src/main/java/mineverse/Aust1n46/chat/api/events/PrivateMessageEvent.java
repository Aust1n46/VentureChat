package mineverse.Aust1n46.chat.api.events;

import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a private message has been sent.
 *
 * @author Heliosares
 */
public class PrivateMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();


    private final MineverseChatPlayer from;
    private final MineverseChatPlayer to;
    private final boolean isLocal;
    private String chat;
    private boolean cancelled;
    private String errorMessage;

    public PrivateMessageEvent(MineverseChatPlayer from, MineverseChatPlayer to, String chat, boolean local) {
        super(false);
        this.from = from;
        this.to = to;
        this.chat = chat;
        this.isLocal = local;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return true if the message was sent from this server, otherwise false
     */
    public boolean isLocal() {
        return isLocal;
    }

    /**
     * @return The error message set by a cancelling plugin, if any
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets a message to be shown to the sender if cancelled
     *
     * @param errorMessage The message to be sent
     */
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
        return " " + this.chat.trim();
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