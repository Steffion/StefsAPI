package nl.Steffion.PLUGINNAME;

import nl.Steffion.PLUGINNAME.StefsAPI.Config;
import nl.Steffion.PLUGINNAME.StefsAPI.PermissionType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PLUGINNAME extends JavaPlugin implements Listener {

	public static PLUGINNAME plugin;
	public static PluginDescriptionFile pdfFile;
	public static Config config;
	public static Config messages;

	public static String mainPermission;

	public void onEnable() {
		plugin = this;
		pdfFile = getDescription();

		mainPermission = pdfFile.getName().toLowerCase() + ".";

		config = StefsAPI.ConfigHandler.createConfig("config", "");
		messages = StefsAPI.ConfigHandler.createConfig("messages", "");

		StefsAPI.ConfigHandler.addDefault(config, "chat.tag",
				"[" + pdfFile.getName() + "] ");
		StefsAPI.ConfigHandler.addDefault(config, "chat.normal", "&b");
		StefsAPI.ConfigHandler.addDefault(config, "chat.warning", "&6");
		StefsAPI.ConfigHandler.addDefault(config, "chat.error", "&c");
		StefsAPI.ConfigHandler.addDefault(config, "chat.argument", "&e");
		StefsAPI.ConfigHandler.addDefault(config, "chat.header", "&9");
		StefsAPI.ConfigHandler.addDefault(config, "chat.header_high",
				"%H_______.[ %A%title%H ]._______");

		StefsAPI.CommandHandler.registerCommand(pdfFile.getName(), null, null,
				"info", "Displays the plugin's info.", PermissionType.ALL,
				new BasicCommands().new InfoCommand(), null);
		StefsAPI.CommandHandler.registerCommand(pdfFile.getName(),
				new String[] { "info" }, new String[] { "i" }, "info",
				"Displays the plugin's info.", PermissionType.ALL,
				new BasicCommands().new InfoCommand(), pdfFile.getName()
						+ " [info/i]");
		StefsAPI.CommandHandler.registerCommand(pdfFile.getName(),
				new String[] { "help" }, new String[] { "h" }, "help",
				"Shows a list of commands.", PermissionType.ALL,
				new BasicCommands().new HelpCommand(), pdfFile.getName()
						+ " <help/h> [page number]");
		StefsAPI.CommandHandler.registerCommand(pdfFile.getName(),
				new String[] { "reload" }, new String[] { "r" }, "reload",
				"Reloads all configs.", PermissionType.MODERATOR,
				new BasicCommands().new ReloadCommand(), pdfFile.getName()
						+ " <reload/r>");
		StefsAPI.CommandHandler
				.registerCommand(
						"test",
						new String[] { "*" },
						new String[] { "*" },
						"test",
						"This is a test command and can be removed from the main class. This is just a example command.",
						PermissionType.ALL,
						new BasicCommands().new InfoCommand(),
						"test [random args]");

		StefsAPI.ConfigHandler.addDefault(messages, "normal.reloadedConfigs",
				"%TAG&aReloaded configs!");

		StefsAPI.ConfigHandler.addDefault(messages, "error.noPermission",
				"%TAG%EYou don't have the permissions to do that!");
		StefsAPI.ConfigHandler
				.addDefault(
						messages,
						"error.commandNotEnabled",
						"%TAG%EThis command has been disabled! Ask your administrator if you belive this is an error.");
		StefsAPI.ConfigHandler.addDefault(messages, "error.onlyIngame",
				"%TAG%EThis is an only in-game command!");
		StefsAPI.ConfigHandler.addDefault(messages, "error.commandNotFound",
				"%TAG%ECouldn't find the command. Try %A/" + pdfFile.getName()
						+ " <help/h> [page number] %Efor more info.");

		StefsAPI.ConfigHandler
				.addDefault(messages, "log.enabledPlugin",
						"%TAG%name&a&k + %N%version is now Enabled. Made by %A%authors%N.");
		StefsAPI.ConfigHandler
				.addDefault(messages, "log.disabledPlugin",
						"%TAG%name&c&k - %N%version is now Disabled. Made by %A%authors%N.");

		StefsAPI.ConfigHandler.displayNewFiles();

		StefsAPI.enableAPI();

		StefsAPI.MessageHandler.buildMessage().addSender("$")
				.setMessage("log.enabledPlugin", messages)
				.changeVariable("name", pdfFile.getName())
				.changeVariable("version", "v" + pdfFile.getVersion())
				.changeVariable("authors", pdfFile.getAuthors().get(0)).build();

	}

	public void onDisable() {
		StefsAPI.MessageHandler.buildMessage().addSender("$")
				.setMessage("log.disabledPlugin", messages)
				.changeVariable("name", pdfFile.getName())
				.changeVariable("version", "v" + pdfFile.getVersion())
				.changeVariable("authors", pdfFile.getAuthors().get(0)).build();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = null;
		String playerName = "$";
		if (sender instanceof Player) {
			player = (Player) sender;
			playerName = player.getName();
		}

		for (nl.Steffion.PLUGINNAME.StefsAPI.Command command : StefsAPI.commands) {
			String[] arguments = command.arguments;
			String[] aliases = command.aliases;

			if (cmd.getName().equalsIgnoreCase(command.label)) {
				int i = 0;
				boolean equals = true;

				if (arguments == null) {
					if (args.length != 0) {
						equals = false;
					}
				} else {
					if (!arguments[0].equals("*")) {
						if (args.length >= arguments.length) {
							for (String argument : arguments) {
								for (String alias : aliases) {
									if (!argument.equalsIgnoreCase(args[i])
											&& !alias.equalsIgnoreCase(args[i])) {
										equals = false;
									}

									i = i + 1;
								}
							}
						} else {
							equals = false;
						}
					}
				}

				if (equals) {
					if (StefsAPI.PermissionHandler.hasPermission(player,
							command.name, command.typePermission, true)) {
						if (config.getFile().getBoolean(
								"commandEnabled." + command.name)) {
							command.command.execute(player, playerName, cmd,
									label, args);
						} else {
							StefsAPI.MessageHandler
									.buildMessage()
									.addSender(playerName)
									.setMessage("error.commandNotEnabled",
											messages).build();
						}
					}

					return true;
				}
			}
		}

		new BasicCommands().new UnknownCommand().execute(player, playerName,
				cmd, label, args);
		return true;
	}
}