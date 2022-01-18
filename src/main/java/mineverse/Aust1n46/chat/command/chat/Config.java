package mineverse.Aust1n46.chat.command.chat;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.utilities.Format;

public class Config extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();

	public Config() {
		super("config");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.edit")) {
			try {
				switch (args[0]) {
				case "filters": {
					try {
						switch (args[1]) {
						case "page": {
							try {
								if (Integer.parseInt(args[2]) > 0) {
									plugin.reloadConfig();
									List<String> filters = plugin.getConfig().getStringList("filters");
									sender.sendMessage(ChatColor.RED + "List of filters page: " + args[2]);
									for (int a = 0 + (Integer.parseInt(args[2]) - 1) * 97; a <= Integer.parseInt(args[2]) * 97; a++) {
										if (a >= filters.size()) {
											break;
										}
										sender.sendMessage(ChatColor.GREEN + "" + filters.get(a));
									}
									if (filters.size() >= Integer.parseInt(args[2]) * 97) {
										int nextpage = Integer.parseInt(args[2]) + 1;
										sender.sendMessage(ChatColor.RED + "/config filters page " + nextpage);
									}
									break;
								}
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config filters page [number]");
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config filters page [number]");
							}
							break;
						}
						case "add": {
							List<String> filters = plugin.getConfig().getStringList("filters");
							if (args[2].contains(",")) {
								filters.add(args[2]);
								plugin.getConfig().set("filters", filters);
								sender.sendMessage(ChatColor.GREEN + "Added filter " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							}
							sender.sendMessage(ChatColor.RED + "Invalid arguments, regex1,regex2");
							break;
						}
						case "remove": {
							List<String> filters = plugin.getConfig().getStringList("filters");
							if (args[2].contains(",")) {
								filters.remove(args[2]);
								plugin.getConfig().set("filters", filters);
								sender.sendMessage(ChatColor.GREEN + "Removed filter " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							}
							sender.sendMessage(ChatColor.RED + "Invalid arguments, regex1,regex2");
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config filters [page [number], add, remove]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid arguments, /config filters [page [number], add, remove]");
					}
					break;
				}
				case "blockablecommands": {
					try {
						switch (args[1]) {
						case "page": {
							try {
								if (Integer.parseInt(args[2]) > 0) {
									plugin.reloadConfig();
									List<String> blockablecommands = plugin.getConfig().getStringList("blockablecommands");
									sender.sendMessage(ChatColor.RED + "List of blockablecommands page: " + args[2]);
									for (int a = 0 + (Integer.parseInt(args[2]) - 1) * 97; a <= Integer.parseInt(args[2]) * 97; a++) {
										if (a >= blockablecommands.size()) {
											break;
										}
										sender.sendMessage(ChatColor.GREEN + "" + blockablecommands.get(a));
									}
									if (blockablecommands.size() >= Integer.parseInt(args[2]) * 97) {
										int nextpage = Integer.parseInt(args[2]) + 1;
										sender.sendMessage(ChatColor.RED + "/config blockablecommands page " + nextpage);
									}
									break;
								}
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config blockablecommands page [number]");
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config blockablecommands page [number]");
							}
							break;
						}
						case "add": {
							List<String> blockablecommands = plugin.getConfig().getStringList("blockablecommands");
							blockablecommands.add(args[2]);
							plugin.getConfig().set("blockablecommands", blockablecommands);
							sender.sendMessage(ChatColor.GREEN + "Added blockablecommand " + args[2]);
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "remove": {
							List<String> blockablecommands = plugin.getConfig().getStringList("blockablecommands");
							blockablecommands.remove(args[2]);
							plugin.getConfig().set("blockablecommands", blockablecommands);
							sender.sendMessage(ChatColor.GREEN + "Removed blockablecommand " + args[2]);
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config blockablecommands [page [number], add, remove]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid arguments, /config blockablecommands [page [number], add, remove]");
					}
					break;
				}
				case "commandspy": {
					try {
						switch (args[1]) {
						case "worldeditcommands": {
							try {
								switch (args[2]) {
								case "true": {
									plugin.getConfig().getConfigurationSection("commandspy").set("worldeditcommands", true);
									sender.sendMessage(ChatColor.GREEN + "worldeditcommands: has been set to true");
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								case "false": {
									plugin.getConfig().getConfigurationSection("commandspy").set("worldeditcommands", false);
									sender.sendMessage(ChatColor.GREEN + "worldeditcommands: has been set to false");
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								default: {
									sender.sendMessage(ChatColor.RED + "Invalid arguments, /config commandspy worldeditcommands [true/false]");
									break;
								}
								}
							} catch (Exception e) {
								sender.sendMessage(
										ChatColor.GREEN + "worldeditcommands: " + plugin.getConfig().getConfigurationSection("commandspy").getBoolean("worldeditcommands"));
							}
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid arguments, /config commandspy worldeditcommands");
					}
					break;
				}
				case "antispam": {
					try {
						switch (args[1]) {
						case "enabled": {
							try {
								switch (args[2]) {
								case "true": {
									plugin.getConfig().getConfigurationSection("antispam").set("enabled", true);
									sender.sendMessage(ChatColor.GREEN + "enabled: has been set to true");
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								case "false": {
									plugin.getConfig().getConfigurationSection("antispam").set("enabled", false);
									sender.sendMessage(ChatColor.GREEN + "enabled: has been set to false");
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								default: {
									sender.sendMessage(ChatColor.RED + "Invalid arguments, /config antispam enabled [true/false]");
									break;
								}
								}
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "enabled: " + plugin.getConfig().getConfigurationSection("antispam").getBoolean("enabled"));
							}
							break;
						}
						case "spamnumber": {
							try {
								if (Integer.parseInt(args[2]) > 0) {
									plugin.getConfig().getConfigurationSection("antispam").set("spamnumber", Integer.parseInt(args[2]));
									sender.sendMessage(ChatColor.GREEN + "spamnumber: has been set to " + args[2]);
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								sender.sendMessage(ChatColor.RED + "Invalid spamnumber, /config antispam spamnumber [Integer > 0]");
								break;
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "Invalid spamnumber, /config antispam [Integer > 0]");
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "spamnumber: " + plugin.getConfig().getConfigurationSection("antispam").getInt("spamnumber"));
								break;
							}
						}
						case "spamtime": {
							try {
								if (Integer.parseInt(args[2]) > 0) {
									plugin.getConfig().getConfigurationSection("antispam").set("spamtime", Integer.parseInt(args[2]));
									sender.sendMessage(ChatColor.GREEN + "spamtime: has been set to " + args[2]);
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								sender.sendMessage(ChatColor.RED + "Invalid spamtime, /config antispam spamtime [Integer > 0]");
								break;
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "Invalid spamtime, /config antispam spamtime [Integer > 0]");
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "spamtime: " + plugin.getConfig().getConfigurationSection("antispam").getInt("spamtime"));
								break;
							}
						}
						case "mutetime": {
							try {
								if (Integer.parseInt(args[2]) >= 0) {
									plugin.getConfig().getConfigurationSection("antispam").set("mutetime", Integer.parseInt(args[2]));
									sender.sendMessage(ChatColor.GREEN + "mutetime: has been set to " + args[2]);
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								sender.sendMessage(ChatColor.RED + "Invalid mutetime, /config antispam mutetime [Integer >= 0]");
								break;
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "Invalid mutetime, /config antispam mutetime [Integer >= 0]");
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "mutetime: " + plugin.getConfig().getConfigurationSection("antispam").getInt("mutetime"));
								break;
							}
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config antispam [enabled, spamnumber, spamtime, mutetime]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "enabled: " + plugin.getConfig().getConfigurationSection("antispam").getString("enabled"));
						sender.sendMessage(ChatColor.GREEN + "spamnumber: " + plugin.getConfig().getConfigurationSection("antispam").getString("spamnumber"));
						sender.sendMessage(ChatColor.GREEN + "spamtime: " + plugin.getConfig().getConfigurationSection("antispam").getString("spamtime"));
						sender.sendMessage(ChatColor.GREEN + "mutetime: " + plugin.getConfig().getConfigurationSection("antispam").getInt("mutetime"));
					}
					break;
				}
				case "mysql": {
					try {
						switch (args[1]) {
						case "enabled": {
							try {
								switch (args[2]) {
								case "true": {
									plugin.getConfig().getConfigurationSection("mysql").set("enabled", true);
									sender.sendMessage(ChatColor.GREEN + "enabled: has been set to true");
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								case "false": {
									plugin.getConfig().getConfigurationSection("mysql").set("enabled", false);
									sender.sendMessage(ChatColor.GREEN + "enabled: has been set to false");
									plugin.saveConfig();
									plugin.reloadConfig();
									break;
								}
								default: {
									sender.sendMessage(ChatColor.RED + "Invalid arguments, /config mysql enabled [true/false]");
									break;
								}
								}
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "enabled: " + plugin.getConfig().getConfigurationSection("mysql").getBoolean("enabled"));
							}
							break;
						}
						case "user": {
							try {
								plugin.getConfig().getConfigurationSection("mysql").set("user", args[2]);
								sender.sendMessage(ChatColor.GREEN + "user: has been set to " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "user: " + plugin.getConfig().getConfigurationSection("mysql").getString("user"));
								break;
							}
						}
						case "port": {
							try {
								plugin.getConfig().getConfigurationSection("mysql").set("port", args[2]);
								sender.sendMessage(ChatColor.GREEN + "port: has been set to " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "port: " + plugin.getConfig().getConfigurationSection("mysql").getString("port"));
								break;
							}
						}
						case "password": {
							try {
								plugin.getConfig().getConfigurationSection("mysql").set("password", args[2]);
								sender.sendMessage(ChatColor.GREEN + "password: has been set to " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "password: " + plugin.getConfig().getConfigurationSection("mysql").getString("password"));
								break;
							}
						}
						case "host": {
							try {
								plugin.getConfig().getConfigurationSection("mysql").set("host", args[2]);
								sender.sendMessage(ChatColor.GREEN + "host: has been set to " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "host: " + plugin.getConfig().getConfigurationSection("mysql").getString("host"));
								break;
							}
						}
						case "database": {
							try {
								plugin.getConfig().getConfigurationSection("mysql").set("database", args[2]);
								sender.sendMessage(ChatColor.GREEN + "database: has been set to " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.GREEN + "database: " + plugin.getConfig().getConfigurationSection("mysql").getString("database"));
								break;
							}
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config mysql [enabled, user, port, password, host, database]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid arguments, /config mysql [enabled, user, port, password, host, database]");
					}
					break;
				}
				case "pluginname": {
					try {
						plugin.getConfig().set("pluginname", args[1]);
						sender.sendMessage(ChatColor.GREEN + "pluginname: has been set to " + args[1]);
						plugin.saveConfig();
						plugin.reloadConfig();
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "pluginname: " + plugin.getConfig().getString("pluginname"));
						break;
					}
				}
				case "loglevel": {
					try {
						switch (args[1]) {
						case "info": {
							plugin.getConfig().set("loglevel", "info");
							sender.sendMessage(ChatColor.GREEN + "loglevel: has been set to info");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "debug": {
							plugin.getConfig().set("loglevel", "debug");
							sender.sendMessage(ChatColor.GREEN + "loglevel: has been set to debug");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "warning": {
							plugin.getConfig().set("loglevel", "warning");
							sender.sendMessage(ChatColor.GREEN + "loglevel: has been set to warning");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "error": {
							plugin.getConfig().set("loglevel", "error");
							sender.sendMessage(ChatColor.GREEN + "loglevel: has been set to error");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config loglevel [info, debug, warning, error]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "loglevel: " + plugin.getConfig().getString("loglevel"));
					}
					break;
				}
				case "savesenderdata": {
					try {
						switch (args[1]) {
						case "true": {
							plugin.getConfig().set("savesenderdata", true);
							sender.sendMessage(ChatColor.GREEN + "saveplayerdata: has been set to true");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "false": {
							plugin.getConfig().set("saveplayerdata", false);
							sender.sendMessage(ChatColor.GREEN + "saveplayerdata: has been set to false");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config saveplayerdata [true/false]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "saveplayerdata: " + plugin.getConfig().getBoolean("saveplayerdata"));
					}
					break;
				}
				case "saveinterval": {
					try {
						if (Integer.parseInt(args[1]) >= 0) {
							plugin.getConfig().set("saveinterval", Integer.parseInt(args[1]));
							sender.sendMessage(ChatColor.GREEN + "saveinterval: has been set to " + args[1]);
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						sender.sendMessage(ChatColor.RED + "Invalid saveinterval, /config saveinterval [saveinterval]");
						break;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Invalid saveinterval, /config saveinterval [saveinterval]");
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "saveinterval: " + plugin.getConfig().getInt("saveinterval"));
						break;
					}
				}
				case "tellcolor": {
					try {
						if (Format.isValidColor(args[1])) {
							plugin.getConfig().set("tellcolor", args[1].toLowerCase());
							sender.sendMessage(ChatColor.GREEN + "tellcolor: has been set to " + args[1].toLowerCase());
							plugin.saveConfig();
							plugin.reloadConfig();
							Bukkit.getPluginManager().disablePlugin(plugin);
							Bukkit.getPluginManager().enablePlugin(plugin);
							break;
						}
						sender.sendMessage(ChatColor.RED + "Invalid color, /config tellcolor [color]");
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "tellcolor: " + plugin.getConfig().getString("tellcolor"));
						break;
					}
				}
				case "vanishsupport": {
					try {
						switch (args[1]) {
						case "true": {
							plugin.getConfig().set("vanishsupport", true);
							sender.sendMessage(ChatColor.GREEN + "vanishsupport: has been set to true");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "false": {
							plugin.getConfig().set("vanishsupport", false);
							sender.sendMessage(ChatColor.GREEN + "vanishsupport: has been set to false");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config vanishsupport [true/false]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "vanishsupport: " + plugin.getConfig().getBoolean("vanishsupport"));
					}
					break;
				}
				case "tellformatto": {
					try {
						String format = args[1] + " ";
						for (int x = 2; x < args.length; x++) {
							if (args[x].length() > 0)
								format += args[x] + " ";
						}
						format = format.substring(0, format.length() - 1);
						plugin.getConfig().set("tellformatto", format);
						sender.sendMessage(ChatColor.GREEN + "tellformatto: has been set to " + format);
						plugin.saveConfig();
						plugin.reloadConfig();
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "tellformatto: " + plugin.getConfig().getString("tellformatto"));
						break;
					}
				}
				case "tellformatfrom": {
					try {
						String format = args[1] + " ";
						for (int x = 2; x < args.length; x++) {
							if (args[x].length() > 0)
								format += args[x] + " ";
						}
						format = format.substring(0, format.length() - 1);
						plugin.getConfig().set("tellformatfrom", format);
						sender.sendMessage(ChatColor.GREEN + "tellformatfrom: has been set to " + format);
						plugin.saveConfig();
						plugin.reloadConfig();
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "tellformatfrom: " + plugin.getConfig().getString("tellformatfrom"));
						break;
					}
				}
				case "tellformatspy": {
					try {
						String format = args[1] + " ";
						for (int x = 2; x < args.length; x++) {
							if (args[x].length() > 0)
								format += args[x] + " ";
						}
						format = format.substring(0, format.length() - 1);
						plugin.getConfig().set("tellformatspy", format);
						sender.sendMessage(ChatColor.GREEN + "tellformatspy: has been set to " + format);
						plugin.saveConfig();
						plugin.reloadConfig();
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "tellformatspy: " + plugin.getConfig().getString("tellformatspy"));
						break;
					}
				}
				case "replyformatto": {
					try {
						String format = args[1] + " ";
						for (int x = 2; x < args.length; x++) {
							if (args[x].length() > 0)
								format += args[x] + " ";
						}
						format = format.substring(0, format.length() - 1);
						plugin.getConfig().set("replyformatto", format);
						sender.sendMessage(ChatColor.GREEN + "replyformatto: has been set to " + format);
						plugin.saveConfig();
						plugin.reloadConfig();
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "replyformatto: " + plugin.getConfig().getString("replyformatto"));
						break;
					}
				}
				case "replyformatfrom": {
					try {
						String format = args[1] + " ";
						for (int x = 2; x < args.length; x++) {
							if (args[x].length() > 0)
								format += args[x] + " ";
						}
						format = format.substring(0, format.length() - 1);
						plugin.getConfig().set("replyformatfrom", format);
						sender.sendMessage(ChatColor.GREEN + "replyformatfrom: has been set to " + format);
						plugin.saveConfig();
						plugin.reloadConfig();
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "replyformatfrom: " + plugin.getConfig().getString("replyformatfrom"));
						break;
					}
				}
				case "replyformatspy": {
					try {
						String format = args[1] + " ";
						for (int x = 2; x < args.length; x++) {
							if (args[x].length() > 0)
								format += args[x] + " ";
						}
						format = format.substring(0, format.length() - 1);
						plugin.getConfig().set("replyformatspy", format);
						sender.sendMessage(ChatColor.GREEN + "replyformatspy: has been set to " + format);
						plugin.saveConfig();
						plugin.reloadConfig();
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "replyformatspy: " + plugin.getConfig().getString("replyformatspy"));
						break;
					}
				}
				case "partyformat": {
					try {
						String format = args[1] + " ";
						for (int x = 2; x < args.length; x++) {
							if (args[x].length() > 0)
								format += args[x] + " ";
						}
						format = format.substring(0, format.length() - 1);
						plugin.getConfig().set("partyformat", format);
						sender.sendMessage(ChatColor.GREEN + "partyformat: has been set to " + format);
						plugin.saveConfig();
						plugin.reloadConfig();
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
						break;
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "partyformat: " + plugin.getConfig().getString("partyformat"));
						break;
					}
				}
				case "broadcastafk": {
					try {
						switch (args[1]) {
						case "true": {
							plugin.getConfig().set("broadcastafk", true);
							sender.sendMessage(ChatColor.GREEN + "broadcastafk: has been set to true");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "false": {
							plugin.getConfig().set("broadcastafk", false);
							sender.sendMessage(ChatColor.GREEN + "broadcastafk: has been set to false");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config broadcastafk [true/false]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "broadcastafk: " + plugin.getConfig().getBoolean("broadcastafk"));
					}
					break;
				}
				case "formatcleaner": {
					try {
						switch (args[1]) {
						case "true": {
							plugin.getConfig().set("formatcleaner", true);
							sender.sendMessage(ChatColor.GREEN + "formatcleaner: has been set to true");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						case "false": {
							plugin.getConfig().set("formatcleaner", false);
							sender.sendMessage(ChatColor.GREEN + "formatcleaner: has been set to false");
							plugin.saveConfig();
							plugin.reloadConfig();
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config formatcleaner [true/false]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "formatcleaner: " + plugin.getConfig().getBoolean("formatcleaner"));
					}
					break;
				}
				case "broadcast": {
					try {
						switch (args[1]) {
						case "color": {
							try {
								if (Format.isValidColor(args[2])) {
									plugin.getConfig().getConfigurationSection("broadcast").set("color", args[2].toLowerCase());
									sender.sendMessage(ChatColor.GREEN + "color: has been set to " + args[2].toLowerCase());
									plugin.saveConfig();
									plugin.reloadConfig();
									Bukkit.getPluginManager().disablePlugin(plugin);
									Bukkit.getPluginManager().enablePlugin(plugin);
									break;
								}
								sender.sendMessage(ChatColor.RED + "Invalid color, /config broadcast color [color]");
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config broadcast color [color]");
								break;
							}
						}
						case "permissions": {
							try {
								plugin.getConfig().getConfigurationSection("broadcast").set("permissions", args[2]);
								sender.sendMessage(ChatColor.GREEN + "permissions: has been set to " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config broadcast permissions [permission]");
								break;
							}
						}
						case "displaytag": {
							try {
								plugin.getConfig().getConfigurationSection("broadcast").set("displaytag", args[2]);
								sender.sendMessage(ChatColor.GREEN + "displaytag: has been set to " + args[2]);
								plugin.saveConfig();
								plugin.reloadConfig();
								Bukkit.getPluginManager().disablePlugin(plugin);
								Bukkit.getPluginManager().enablePlugin(plugin);
								break;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config broadcast displaytag [displaytag]");
								break;
							}
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config broadcast [color, permissions, displaytag]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.GREEN + "color: " + plugin.getConfig().getConfigurationSection("broadcast").getString("color"));
						sender.sendMessage(ChatColor.GREEN + "permissions: " + plugin.getConfig().getConfigurationSection("broadcast").getString("permissions"));
						sender.sendMessage(ChatColor.GREEN + "displaytag: " + plugin.getConfig().getConfigurationSection("broadcast").getString("displaytag"));
					}
					break;
				}
				case "channels": {
					try {
						if (plugin.getConfig().getConfigurationSection("channels").getString(args[1]) != null) {
							try {
								switch (args[2]) {
								case "color": {
									try {
										if (Format.isValidColor(args[3])) {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("color", args[3].toLowerCase());
											sender.sendMessage(ChatColor.GREEN + "color: has been set to " + args[3].toLowerCase());
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										sender.sendMessage(ChatColor.RED + "Invalid color, /config channels [channel] color [color]");
										break;
									} catch (Exception e) {
										sender.sendMessage(ChatColor.GREEN + "color: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getString("color"));
										break;
									}
								}
								case "chatcolor": {
									try {
										if (Format.isValidColor(args[3]) || args[3].equalsIgnoreCase("None")) {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("chatcolor", args[3].toLowerCase());
											sender.sendMessage(ChatColor.GREEN + "chatcolor: has been set to " + args[3].toLowerCase());
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										sender.sendMessage(ChatColor.RED + "Invalid color, /config channels [channel] chatcolor [color]");
										break;
									} catch (Exception e) {
										sender.sendMessage(
												ChatColor.GREEN + "chatcolor: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getString("chatcolor"));
										break;
									}
								}
								case "mutable": {
									try {
										switch (args[3]) {
										case "true": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("mutable", true);
											sender.sendMessage(ChatColor.GREEN + "mutable: has been set to true");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										case "false": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("mutable", false);
											sender.sendMessage(ChatColor.GREEN + "mutable: has been set to false");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										default: {
											sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels [channel] mutable [true/false]");
											break;
										}
										}
									} catch (Exception e) {
										sender.sendMessage(ChatColor.GREEN + "mutable: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getBoolean("mutable"));
									}
									break;
								}
								case "filter": {
									try {
										switch (args[3]) {
										case "true": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("filter", true);
											sender.sendMessage(ChatColor.GREEN + "filter: has been set to true");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										case "false": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("filter", false);
											sender.sendMessage(ChatColor.GREEN + "filter: has been set to false");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										default: {
											sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels [channel] filter [true/false]");
											break;
										}
										}
									} catch (Exception e) {
										sender.sendMessage(ChatColor.GREEN + "filter: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getBoolean("filter"));
									}
									break;
								}
								case "permissions": {
									try {
										plugin.getConfig().getConfigurationSection("channels." + args[1]).set("permissions", args[3]);
										sender.sendMessage(ChatColor.GREEN + "permissions: has been set to " + args[3]);
										plugin.saveConfig();
										plugin.reloadConfig();
										Bukkit.getPluginManager().disablePlugin(plugin);
										Bukkit.getPluginManager().enablePlugin(plugin);
										break;
									} catch (Exception e) {
										sender.sendMessage(
												ChatColor.GREEN + "permissions: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getString("permissions"));
										break;
									}
								}
								case "alias": {
									try {
										plugin.getConfig().getConfigurationSection("channels." + args[1]).set("alias", args[3]);
										sender.sendMessage(ChatColor.GREEN + "alias: has been set to " + args[3]);
										plugin.saveConfig();
										plugin.reloadConfig();
										Bukkit.getPluginManager().disablePlugin(plugin);
										Bukkit.getPluginManager().enablePlugin(plugin);
										break;
									} catch (Exception e) {
										sender.sendMessage(ChatColor.GREEN + "alias: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getString("alias"));
										break;
									}
								}
								case "default": {
									try {
										switch (args[3]) {
										case "true": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("default", true);
											sender.sendMessage(ChatColor.GREEN + "default: has been set to true");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										case "false": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("default", false);
											sender.sendMessage(ChatColor.GREEN + "default: has been set to false");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										default: {
											sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels [channel] default [true/false]");
											break;
										}
										}
									} catch (Exception e) {
										sender.sendMessage(ChatColor.GREEN + "default: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getBoolean("default"));
									}
									break;
								}
								case "autojoin": {
									try {
										switch (args[3]) {
										case "true": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("autojoin", true);
											sender.sendMessage(ChatColor.GREEN + "autojoin: has been set to true");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										case "false": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("autojoin", false);
											sender.sendMessage(ChatColor.GREEN + "autojoin: has been set to false");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										default: {
											sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels [channel] autojoin [true/false]");
											break;
										}
										}
									} catch (Exception e) {
										sender.sendMessage(
												ChatColor.GREEN + "autojoin: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getBoolean("autojoin"));
									}
									break;
								}
								case "distance": {
									try {
										if (Double.parseDouble(args[3]) >= (double) 0) {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("distance", Double.parseDouble(args[3]));
											sender.sendMessage(ChatColor.GREEN + "distance: has been set to " + args[3]);
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										sender.sendMessage(ChatColor.RED + "Invalid distance, /config channels [channel] distance [distance]");
										break;
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Invalid distance, /config channels [channel] distance [distance]");
										break;
									} catch (Exception e) {
										sender.sendMessage(
												ChatColor.GREEN + "distance: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getDouble("distance"));
										break;
									}
								}
								case "cooldown": {
									try {
										if (Integer.parseInt(args[3]) >= 0) {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("cooldown", Integer.parseInt(args[3]));
											sender.sendMessage(ChatColor.GREEN + "cooldown: has been set to " + args[3]);
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										sender.sendMessage(ChatColor.RED + "Invalid cooldown, /config channels [channel] cooldown [cooldown]");
										break;
									} catch (NumberFormatException e) {
										sender.sendMessage(ChatColor.RED + "Invalid cooldown, /config channels [channel] cooldown [cooldown]");
										break;
									} catch (Exception e) {
										sender.sendMessage(ChatColor.GREEN + "cooldown: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getInt("cooldown"));
										break;
									}
								}
								case "bungeecord": {
									try {
										switch (args[3]) {
										case "true": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("bungeecord", true);
											sender.sendMessage(ChatColor.GREEN + "bungeecord: has been set to true");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										case "false": {
											plugin.getConfig().getConfigurationSection("channels." + args[1]).set("bungeecord", false);
											sender.sendMessage(ChatColor.GREEN + "bungeecord: has been set to false");
											plugin.saveConfig();
											plugin.reloadConfig();
											Bukkit.getPluginManager().disablePlugin(plugin);
											Bukkit.getPluginManager().enablePlugin(plugin);
											break;
										}
										default: {
											sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels [channel] bungeecord [true/false]");
											break;
										}
										}
									} catch (Exception e) {
										sender.sendMessage(
												ChatColor.GREEN + "bungeecord: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getBoolean("bungeecord"));
									}
									break;
								}
								case "format": {
									try {
										String format = args[3] + " ";
										for (int x = 4; x < args.length; x++) {
											if (args[x].length() > 0)
												format += args[x] + " ";
										}
										format = format.substring(0, format.length() - 1);
										plugin.getConfig().getConfigurationSection("channels." + args[1]).set("format", format);
										sender.sendMessage(ChatColor.GREEN + "format: has been set to " + format);
										plugin.saveConfig();
										plugin.reloadConfig();
										Bukkit.getPluginManager().disablePlugin(plugin);
										Bukkit.getPluginManager().enablePlugin(plugin);
										break;
									} catch (Exception e) {
										sender.sendMessage(ChatColor.GREEN + "format: " + plugin.getConfig().getConfigurationSection("channels." + args[1]).getString("format"));
										break;
									}
								}
								default: {
									sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels " + args[1]
											+ " [color, chatcolor, mutable, alias, default, autojoin, distance, cooldown, bungeecord, format]");
									break;
								}
								}
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels " + args[1]
										+ " [color, chatcolor, mutable, alias, default, autojoin, distance, cooldown, bungeecord, format]");
							}
							break;
						}
						switch (args[1]) {
						case "create": {
							try {
								System.out.println(plugin.getConfig().getConfigurationSection("channels").getString(args[2]));
								if (!plugin.getConfig().getConfigurationSection("channels").isSet(args[2])) {
									sender.sendMessage(ChatColor.GREEN + "Created channel " + args[2] + " and assigned it default configuration.");
									plugin.getConfig().createSection("channels." + args[2]);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("color", "white");
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("chatcolor", "white");
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("mutable", true);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("permissions", "None");
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("alias", args[2].toLowerCase().charAt(0));
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("default", false);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("autojoin", true);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("distance", 0.0);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("cooldown", 0);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("bungeecord", false);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("servername", false);
									plugin.getConfig().getConfigurationSection("channels." + args[2]).set("format", "Default");
									plugin.saveConfig();
									plugin.reloadConfig();
									Bukkit.getPluginManager().disablePlugin(plugin);
									Bukkit.getPluginManager().enablePlugin(plugin);
									break;
								}
								sender.sendMessage(ChatColor.RED + "Channel " + args[2] + " already exists.");
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels create [channelname]");
							}
							break;
						}
						case "delete": {
							try {
								if (plugin.getConfig().getConfigurationSection("channels").isSet(args[2])) {
									sender.sendMessage(ChatColor.GREEN + "Deleted channel " + args[2]);
									plugin.getConfig().getConfigurationSection("channels").set(args[2], null);
									plugin.saveConfig();
									plugin.reloadConfig();
									Bukkit.getPluginManager().disablePlugin(plugin);
									Bukkit.getPluginManager().enablePlugin(plugin);
									break;
								}
								sender.sendMessage(ChatColor.RED + "Channel " + args[2] + " doesn't exist.");
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels delete [channelname]");
							}
							break;
						}
						default: {
							sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels [channel, create, delete]");
							break;
						}
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid arguments, /config channels [channel, create, delete]");
					}
					break;
				}
				case "help": {
					sender.sendMessage(ChatColor.GREEN + "/config filters [page [number], add, remove]");
					sender.sendMessage(ChatColor.GREEN + "/config blockablecommands [page [number], add, remove]");
					sender.sendMessage(ChatColor.GREEN + "/config commandspy worldeditcommands [true/false]");
					sender.sendMessage(ChatColor.GREEN + "/config antispam [enabled, spamnumber, spamtime, mutetime]");
					sender.sendMessage(ChatColor.GREEN + "/config mysql [enabled, user, port, password, host, database]");
					sender.sendMessage(ChatColor.GREEN + "/config pluginname [pluginname]");
					sender.sendMessage(ChatColor.GREEN + "/config loglevel [info, debug, warning, error]");
					sender.sendMessage(ChatColor.GREEN + "/config saveplayerdata [true/false]");
					sender.sendMessage(ChatColor.GREEN + "/config saveinterval [saveinterval]");
					sender.sendMessage(ChatColor.GREEN + "/config tellcolor [color]");
					sender.sendMessage(ChatColor.GREEN + "/config vanishsupport [true/false]");
					sender.sendMessage(ChatColor.GREEN + "/config tellformatto [format]");
					sender.sendMessage(ChatColor.GREEN + "/config tellformatfrom [format]");
					sender.sendMessage(ChatColor.GREEN + "/config tellformatspy [format]");
					sender.sendMessage(ChatColor.GREEN + "/config replyformatto [format]");
					sender.sendMessage(ChatColor.GREEN + "/config replyformatfrom [format]");
					sender.sendMessage(ChatColor.GREEN + "/config replyformatspy [format]");
					sender.sendMessage(ChatColor.GREEN + "/config partyformat [format]");
					sender.sendMessage(ChatColor.GREEN + "/config broadcastafk [true/false]");
					sender.sendMessage(ChatColor.GREEN + "/config formatcleaner [true/false]");
					sender.sendMessage(ChatColor.GREEN + "/config broadcast [color, permissions, displaytag]");
					sender.sendMessage(ChatColor.GREEN
							+ "/config channels [channel] [chatcolor, mutable, permissions, alias, default, autojoin, distance, cooldown, bungeecord, format, create , delete]");
					sender.sendMessage(ChatColor.GREEN + "/config help");
					break;
				}
				default: {
					sender.sendMessage(ChatColor.RED + "Invalid arguments, /config help");
					break;
				}
				}
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invalid arguments, /config help");
			}
			return true;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
		return true;
	}
}
