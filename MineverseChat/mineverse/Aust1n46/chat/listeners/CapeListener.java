package mineverse.Aust1n46.chat.listeners;

import java.util.ArrayList;
import java.util.List;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.VersionHandler;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.util.EulerAngle;

public class CapeListener implements Listener {
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if(!p.getName().equals("Aust1n46") || !MineverseChat.capeToggle) {
			return;
		}
		MineverseChat.cape.remove();
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(!p.getName().equals("Aust1n46") || !MineverseChat.capeToggle) {
			return;
		}
		MineverseChat.cape = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
		MineverseChat.banner = this.getBanner();
		this.equipCape(p, MineverseChat.banner);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if(!p.getName().equals("Aust1n46") || !MineverseChat.capeToggle) {
			return;
		}
		Location loc = new Location(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY() + 1.05D, event.getTo().getZ(), event.getTo().getYaw(), event.getTo().getPitch());
		if(p.isSneaking()) {
			loc.setY(event.getTo().getY() + 0.85D);
		}
		ArmorStand cape = MineverseChat.cape;
		cape.teleport(loc);
		Double add = Double.valueOf(Math.abs(p.getVelocity().getX()) * 3.0D + Math.abs(p.getVelocity().getZ()) * 3.0D);
		cape.setHeadPose(cape.getHeadPose().setZ(cape.getHeadPose().getZ()).setX(-2.45D + add.doubleValue()));
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		if(!p.getName().equals("Aust1n46") || !MineverseChat.capeToggle) {
			return;
		}
		Location loc = new Location(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY() + 1.05D, event.getTo().getZ(), event.getTo().getYaw(), event.getTo().getPitch());
		ArmorStand cape = MineverseChat.cape;
		cape.teleport(loc);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if(!p.getName().equals("Aust1n46") || !MineverseChat.capeToggle) {
			return;
		}
		Location loc = new Location(event.getRespawnLocation().getWorld(), event.getRespawnLocation().getX(), event.getRespawnLocation().getY() + 1.05D, event.getRespawnLocation().getZ(), event.getRespawnLocation().getYaw(), event.getRespawnLocation().getPitch());
		ArmorStand cape = MineverseChat.cape;
		cape.teleport(loc);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if(!p.getName().equals("Aust1n46") || !MineverseChat.capeToggle) {
			return;
		}
		Location loc = new Location(event.getEntity().getWorld(), event.getEntity().getLocation().getX(), -1.0D, event.getEntity().getLocation().getZ());
		ArmorStand cape = MineverseChat.cape;
		cape.teleport(loc);
	}

	public void equipCape(Player p, ItemStack i) {
		Location loc = new Location(p.getLocation().getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1.05D, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
		ArmorStand cape = MineverseChat.cape;
		cape.setHelmet(i);
		cape.setHeadPose(cape.getHeadPose().setX(cape.getHeadPose().getX() - 2.4D));
		cape.setSmall(true);
		cape.setNoDamageTicks(Integer.MAX_VALUE);
		cape.setVisible(false);
		cape.setGravity(false);
		cape.setArms(false);
		cape.setBasePlate(false);
		cape.teleport(loc);
		cape.setVisible(false);
	}

	public EulerAngle getAngle(Double d) {
		return new EulerAngle(0.0D, d.doubleValue(), 0.0D);
	}

	public void capeInHand(Player p) {
		if(p.getName().equals("Aust1n46")) {
			if(VersionHandler.is1_7() || VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12()) {
				if(p.getInventory().getItemInHand().getType().equals(Material.getMaterial("BANNER"))) {
					equipCape(p, p.getInventory().getItemInHand());
					BannerMeta b = (BannerMeta) p.getInventory().getItemInHand().getItemMeta();
					this.addBanner("cape", b);
					p.setItemInHand(null);
				}
			}
			else {
				if(p.getInventory().getItemInHand().getType().equals(Material.BLACK_BANNER)) {
					equipCape(p, p.getInventory().getItemInHand());
					BannerMeta b = (BannerMeta) p.getInventory().getItemInHand().getItemMeta();
					this.addBanner("cape", b);
					p.setItemInHand(null);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		final Player p = event.getPlayer();
		if(event.getMessage().startsWith("/cape")) {
			if(p.getName().equals("Aust1n46") && !VersionHandler.is1_7_10() && !VersionHandler.is1_7_9() && !VersionHandler.is1_7_2()) {
				//capeInHand(p);
				if(MineverseChat.capeToggle) {
					MineverseChat.cape.remove();
					MineverseChat.capeToggle = false;
					p.sendMessage(ChatColor.GOLD + "Cape hidden!");
					event.setCancelled(true);
					return;
				}
				MineverseChat.cape = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
				this.equipCape(p, MineverseChat.banner);
				p.sendMessage(ChatColor.GOLD + "Cape shown!");
				MineverseChat.capeToggle = true;
				MineverseChat.banner = this.getBanner();
				event.setCancelled(true);
				return;
			}	
		}
	}

	public List<String> getSerializedPatterns(BannerMeta b) {
		List<String> patterns = new ArrayList<String>();
		for(Pattern p : b.getPatterns()) {
			String s = p.getPattern().toString() + ";" + p.getColor().toString();
			patterns.add(s);
		}
		return patterns;
	}

	public void addBanner(String s, BannerMeta b) {
		FileConfiguration f = MineverseChat.getInstance().getConfig();
		f.set(s.toUpperCase() + ".display_name", s);
		s = s.toUpperCase();
		f.set(s + ".base_color", b.getBaseColor().toString());
		f.set(s + ".patterns", getSerializedPatterns(b));
		MineverseChat.getInstance().saveConfig();
	}

	public ItemStack getBanner() {
		ItemStack i = null;
		if(VersionHandler.is1_7() || VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12()) {
			i = new ItemStack(Material.getMaterial("BANNER"), 1);
		}
		else {
			i = new ItemStack(Material.BLACK_BANNER, 1);
		}	
		BannerMeta bMeta = (BannerMeta) i.getItemMeta();
		if(VersionHandler.is1_7() || VersionHandler.is1_8() || VersionHandler.is1_9() || VersionHandler.is1_10() || VersionHandler.is1_11() || VersionHandler.is1_12()) {
			bMeta.setBaseColor(DyeColor.valueOf("BLACK"));
		}
		List<String> patterns = new ArrayList<String>();
		patterns.add("STRIPE_BOTTOM;MAGENTA");
		patterns.add("STRIPE_TOP;MAGENTA");
		patterns.add("STRAIGHT_CROSS;MAGENTA");
		patterns.add("BORDER;SILVER");
		patterns.add("CREEPER;SILVER");
		patterns.add("CROSS;BLACK");
		patterns.add("CURLY_BORDER;GRAY");
		patterns.add("RHOMBUS_MIDDLE;BLACK");
		patterns.add("SKULL;MAGENTA");
		patterns.add("STRIPE_SMALL;BLACK");
		patterns.add("CURLY_BORDER;BLACK");
		for(String pat : patterns) {
			String[] parts = pat.split(";");
			DyeColor d = DyeColor.valueOf(parts[1]);
			PatternType pt = PatternType.valueOf(parts[0]);
			bMeta.addPattern(new Pattern(d, pt));
		}
		String display = Format.FormatStringAll("Cape");
		bMeta.setDisplayName(display);
		List<String> lore = new ArrayList<String>();
		bMeta.setLore(lore);
		i.setItemMeta(bMeta);
		return i;
	}
}