package mineverse.Aust1n46.chat.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;

@Getter
@Setter
@AllArgsConstructor
public class ModerationGuiInventory implements InventoryHolder {
	private MineverseChatPlayer targetMcp;
	private ChatChannel channel;
	private int hash;

	@Override
	public Inventory getInventory() {
		return null;
	}
}
