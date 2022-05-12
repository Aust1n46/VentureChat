package mineverse.Aust1n46.chat.api.events;

import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a private message has been sent to a player.
 * This event can not be cancelled.
 *
 * @author LOOHP
 */
public class PrivateMessageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final MineverseChatPlayer sender;
    private final MineverseChatPlayer receiver;
    private final String msg;
    private String echo;
    private String send;
    private String spy;
    private final boolean bungee;

    public PrivateMessageEvent(MineverseChatPlayer sender, MineverseChatPlayer receiver, String msg, String echo, String send, String spy, boolean bungee) {
        super(!Bukkit.isPrimaryThread());
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.echo = echo;
        this.send = send;
        this.spy = spy;
        this.bungee = bungee;
    }

    public MineverseChatPlayer getSender() {
        return sender;
    }

    public MineverseChatPlayer getReceiver() {
        return receiver;
    }

    public String getMsg() {
        return msg;
    }

    public String getEcho() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getSpy() {
        return spy;
    }

    public void setSpy(String spy) {
        this.spy = spy;
    }

    public boolean isBungee() {
        return bungee;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
