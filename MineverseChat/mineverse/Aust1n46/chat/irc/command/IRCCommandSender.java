package mineverse.Aust1n46.chat.irc.command;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.pircbotx.Channel;

//This class is a custom CommandSender that allows the IRC bot to relay commands from IRC to the server.
public class IRCCommandSender implements CommandSender {
	private Channel channel;

	public IRCCommandSender(Channel channel) {
		this.channel = channel;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	@Override
	public boolean hasPermission(String arg0) {
		return true;
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return true;
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return true;
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return true;
	}

	@Override
	public void recalculatePermissions() {

	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {

	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean arg0) {

	}

	@Override
	public String getName() {
		return "Server";
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public void sendMessage(String message) {
		channel.send().message(message);
		Bukkit.getConsoleSender().sendMessage(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		for(String s : messages) {
			channel.send().message(s);
		}
		Bukkit.getConsoleSender().sendMessage(messages);
	}

	@Override
	public Spigot spigot() {
		// TODO Auto-generated method stub
		return null;
	}
}