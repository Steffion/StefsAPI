package nl.Steffion.PLUGINNAME;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StefsAPI {
	/**
	 * StefsAPI - Made by Steffion.
	 * 
	 * You're allowed to use this engine for own usage, you're not allowed to
	 * republish the engine. Using this for your own plugin is allowed when a
	 * credit is placed somewhere in the plugin.
	 * 
	 * Thanks for your cooperate!
	 * 
	 * @author Steffion
	 */

	public static String engineVersion = "5.0.5";
	public static String engineAuthors = "Steffion";

	public static ArrayList<String> newConfigs = new ArrayList<String>();
	public static ArrayList<Command> commands = new ArrayList<Command>();

	public static void enableAPI() {
		StefsAPI.MessageHandler
				.buildMessage()
				.addSender("$")
				.setMessage(
						"%TAGPlugin is being enabled and using %AStefsAPI%N %version. Made by %A%authors%N.")
				.changeVariable("version", "v" + engineVersion)
				.changeVariable("authors", engineAuthors).build();
	}

	public static class ConfigHandler {
		private static StefsAPI api = new StefsAPI();

		public static Config createConfig(String name) {
			return api.new Config(name);
		}

		public static Config createConfig(String name, String location) {
			return api.new Config(name, location);
		}

		public static boolean deleteConfig(Config config) {
			if (config.file.exists()) {
				config.file.delete();
				return true;
			} else {
				return false;
			}
		}

		public static void addDefault(Config config, String location,
				Object value) {
			config.load();
			if (config.getFile().get(location) == null) {
				config.getFile().set(location, value);
				config.save();
			}
		}

		public static void displayNewFiles() {
			for (String file : newConfigs) {
				MessageHandler
						.buildMessage()
						.addSender("$")
						.setMessage(
								"%TAG%WUnable to find config file '%A" + file
										+ ".yml%W', creating new one!").build();
			}

			newConfigs = new ArrayList<String>();
		}
	}

	public class Config {
		public String name;
		public String location;
		public File file;
		public FileConfiguration fileC;
		public ConfigurationSection fileCS;

		public Config (String name) {
			this.name = name;
			this.file = new File("plugins/" + PLUGINNAME.pdfFile.getName(),
					name + ".yml");
			this.fileC = new YamlConfiguration();
			this.checkFile(name, "");
			this.fileCS = fileC.getConfigurationSection("");
			this.load();
		}

		public Config (String name, String location) {
			this.name = name;
			this.location = location;
			this.file = new File("plugins/" + PLUGINNAME.pdfFile.getName()
					+ "/" + location, name + ".yml");
			this.fileC = new YamlConfiguration();
			this.checkFile(name, location + "/");
			this.fileCS = fileC.getConfigurationSection("");
			this.load();
		}

		public void checkFile(String name, String location) {
			if (!this.file.exists()) {
				try {
					this.file.getParentFile().mkdirs();
					this.file.createNewFile();
					newConfigs.add(location + name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void save() {
			try {
				this.fileC.save(this.file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void load() {
			this.checkFile(file.getName(), this.location);
			if (this.file.exists()) {
				try {
					this.fileC.load(this.file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public FileConfiguration getFile() {
			return this.fileC;
		}
	}

	public static class MessageHandler {
		private static StefsAPI api = new StefsAPI();

		public static MessageBuilder buildMessage() {
			return api.new MessageBuilder();
		}

		public static String replaceColours(String message) {
			return message.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2")
					.replaceAll("&u", "\n");
		}

		public static String replacePrefixes(String message) {
			return message.replaceAll("%N", NORMAL())
					.replaceAll("%W", WARNING()).replaceAll("%E", ERROR())
					.replaceAll("%A", ARGUMENT()).replaceAll("%H", HEADER())
					.replaceAll("%TAG", TAG());
		}

		public static String NORMAL() {
			return PLUGINNAME.config.getFile().getString("chat.normal");
		}

		public static String WARNING() {
			return PLUGINNAME.config.getFile().getString("chat.warning");
		}

		public static String ERROR() {
			return PLUGINNAME.config.getFile().getString("chat.error");
		}

		public static String ARGUMENT() {
			return PLUGINNAME.config.getFile().getString("chat.argument");
		}

		public static String HEADER() {
			return PLUGINNAME.config.getFile().getString("chat.header");
		}

		public static String TAG() {
			return PLUGINNAME.config.getFile().getString("chat.header")
					+ PLUGINNAME.config.getFile().getString("chat.tag")
					+ PLUGINNAME.config.getFile().getString("chat.normal");
		}
	}

	public class MessageBuilder {
		ArrayList<String> senders = new ArrayList<String>();
		String message;
		Config message_config;
		Map<String, String> replacements = new HashMap<String, String>();

		public MessageBuilder () {
		}

		public MessageBuilder addSender(String sender) {
			this.senders.add(sender);
			return this;
		}

		public MessageBuilder setMessage(String message) {
			this.message = message;
			this.message_config = null;
			return this;
		}

		public MessageBuilder setMessage(String message, Config config) {
			this.message = message;
			this.message_config = config;
			return this;
		}

		public MessageBuilder changeVariable(String variable, String replacement) {
			this.replacements.put(variable, replacement);
			return this;
		}

		public MessageBuilder build() {
			for (String sender : this.senders) {
				String message = this.message;
				Config messages_config = this.message_config;
				String finalmessage;

				if (messages_config != null) {
					if (messages_config.getFile().get(message) != null) {
						finalmessage = (String) messages_config.getFile().get(
								message);
					} else {
						finalmessage = "&9[" + PLUGINNAME.pdfFile.getName()
								+ "] &cERROR: Message string &e" + message
								+ "&c has not been found in the &e"
								+ messages_config.name + ".yml&c config.";
					}
				} else {
					finalmessage = message;
				}

				for (Map.Entry<String, String> replacement_pairs : this.replacements
						.entrySet()) {
					finalmessage = finalmessage.replaceAll("%"
							+ replacement_pairs.getKey(),
							replacement_pairs.getValue());
				}

				finalmessage = MessageHandler.replacePrefixes(finalmessage);
				finalmessage = MessageHandler.replaceColours(finalmessage);

				if (!finalmessage.startsWith("-")) {
					if (sender == "*") {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.sendMessage(finalmessage);
						}
					} else if (sender == "$") {
						Bukkit.getConsoleSender().sendMessage(finalmessage);
					} else {
						Player player = Bukkit.getPlayer(sender);
						if (player != null) {
							player.sendMessage(finalmessage);
						}
					}
				}
			}

			this.replacements = new HashMap<String, String>();
			return this;
		}
	}

	public static class CommandHandler {
		private static StefsAPI api = new StefsAPI();

		public static Command registerCommand(String label, String[] arguments,
				String[] aliases, String name, String help,
				PermissionType typePermission, ExecutedCommand command,
				String usage) {
			return api.new Command(label, arguments, aliases, name, help,
					typePermission, command, usage);
		}
	}

	public class Command {
		String label;
		String[] arguments;
		String[] aliases;
		String name;
		String help;
		PermissionType typePermission;
		ExecutedCommand command;
		String usage;

		public Command (String label, String[] arguments, String[] aliases,
				String name, String help, PermissionType typePermission,
				ExecutedCommand command, String usage) {
			this.label = label;
			this.arguments = arguments;
			this.aliases = aliases;
			this.name = name;
			this.help = help;
			this.typePermission = typePermission;
			this.command = command;
			this.usage = usage;

			ConfigHandler.addDefault(PLUGINNAME.config, "commandEnabled."
					+ name, true);
			ConfigHandler.addDefault(PLUGINNAME.messages, "help." + name, help);
			commands.add(this);
		}
	}

	public static class ExecutedCommand {
		public boolean execute(Player player, String playerName,
				org.bukkit.command.Command cmd, String label, String[] args) {
			return true;
		}
	}

	public static class PermissionHandler {
		public static boolean hasPermission(Player player, String permission,
				PermissionType typePermission, Boolean message) {
			if (player == null) {
				return true;
			}

			if (typePermission == PermissionType.ALL) {
				return true;
			} else if (typePermission == PermissionType.OP) {
				if (player.isOp()) {
					return true;
				}
			} else if (typePermission == PermissionType.ADMIN) {
				if (player.hasPermission(PLUGINNAME.mainPermission + "admin")) {
					return true;
				}
			} else if (typePermission == PermissionType.MODERATOR) {
				if (player.hasPermission(PLUGINNAME.mainPermission
						+ "moderator")) {
					return true;
				}
			} else if (typePermission == PermissionType.PLAYER) {
				if (player.hasPermission(PLUGINNAME.mainPermission + "player")) {
					return true;
				}
			}

			if (player.hasPermission("*")) {
				return true;
			} else if (player.hasPermission(PLUGINNAME.mainPermission + "*")) {
				return true;
			} else if (player.hasPermission(PLUGINNAME.mainPermission
					+ permission)) {
				return true;
			} else if (player.hasPermission(PLUGINNAME.mainPermission
					+ permission + ".*")) {
				return true;
			} else {
				if (message) {
					MessageHandler
							.buildMessage()
							.addSender(player.getName())
							.setMessage("error.noPermission",
									PLUGINNAME.messages).build();
				}
			}

			return false;
		}
	}

	public enum PermissionType {
		ALL, PLAYER, MODERATOR, ADMIN, OP;
	}
}
