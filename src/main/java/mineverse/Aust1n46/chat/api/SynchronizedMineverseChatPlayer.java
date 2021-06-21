package mineverse.Aust1n46.chat.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import mineverse.Aust1n46.chat.command.mute.MuteContainer;

public class SynchronizedMineverseChatPlayer {
    private UUID uuid;
    private Set<String> listening;
    private HashMap<String, MuteContainer> mutes;
    private Set<UUID> ignores;
    private int messagePackets;
    private List<String> messageData = new ArrayList<String>();
    private boolean spy;
    private boolean messageToggle;

    public SynchronizedMineverseChatPlayer(UUID uuid, Set<String> listening, HashMap<String, MuteContainer> mutes, Set<UUID> ignores, boolean spy, boolean messageToggle) {
        this.uuid = uuid;
        this.listening = listening;
        this.mutes = mutes;
        this.ignores = ignores;
        this.spy = spy;
        this.messageToggle = messageToggle;
    }

    public SynchronizedMineverseChatPlayer(UUID uuid) {
        this.uuid = uuid;
        listening = new HashSet<String>();
        mutes = new HashMap<String, MuteContainer>();
        ignores = new HashSet<UUID>();
        spy = false;
        messageToggle = true;
    }

    public List<String> getMessageData() {
        return this.messageData;
    }

    public void addData(String s) {
        this.messageData.add(s);
    }

    public void clearMessageData() {
        this.messageData.clear();
    }

    public int getMessagePackets() {
        return this.messagePackets;
    }

    public void incrementMessagePackets() {
        this.messagePackets++;
    }

    public void clearMessagePackets() {
        this.messagePackets = 0;
    }

    public void addIgnore(SynchronizedMineverseChatPlayer smcp) {
        this.ignores.add(smcp.getUUID());
    }

    public void removeIgnore(SynchronizedMineverseChatPlayer smcp) {
        this.ignores.remove(smcp.getUUID());
    }

    public Set<UUID> getIgnores() {
        return this.ignores;
    }

    public void addMute(String channel, long time, String reason) {
        mutes.put(channel, new MuteContainer(channel, time, reason));
    }

    public void clearMutes() {
        this.mutes.clear();
    }

    public Collection<MuteContainer> getMutes() {
        return this.mutes.values();
    }

    public void addListening(String channel) {
        this.listening.add(channel);
    }

    public void removeListening(String channel) {
        this.listening.remove(channel);
    }

    public Set<String> getListening() {
        return this.listening;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean isSpy() {
        return this.spy;
    }

    public void setSpy(boolean spy) {
        this.spy = spy;
    }

    public boolean getMessageToggle() {
        return this.messageToggle;
    }

    public void setMessageToggle(boolean messageToggle) {
        this.messageToggle = messageToggle;
    }
}