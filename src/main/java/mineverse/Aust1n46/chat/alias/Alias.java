package mineverse.Aust1n46.chat.alias;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;

public class Alias {
    private static MineverseChat plugin = MineverseChat.getInstance();
    private static List<Alias> aliases;

    private String name;
    private int arguments;
    private List<String> components;
    private String permission;

    public Alias(String name, int arguments, List<String> components, String permission) {
        this.name = name;
        this.arguments = arguments;
        this.components = components;
        this.permission = "venturechat." + permission;
    }

    public static void initialize() {
        aliases = new ArrayList<Alias>();
        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("alias");
        for (String key : cs.getKeys(false)) {
            String name = key;
            int arguments = cs.getInt(key + ".arguments", 0);
            List<String> components = cs.getStringList(key + ".components");
            String permissions = cs.getString(key + ".permissions", "None");
            aliases.add(new Alias(name, arguments, components, permissions));
        }
    }

    public static List<Alias> getAliases() {
        return aliases;
    }

    public String getName() {
        return name;
    }

    public int getArguments() {
        return arguments;
    }

    public List<String> getComponents() {
        return components;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission() {
        return !permission.equalsIgnoreCase("venturechat.none");
    }
}
