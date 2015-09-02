package org.kwstudios.play.ragemode.loader;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.kwstudios.play.ragemode.commands.CommandParser;
import org.kwstudios.play.ragemode.commands.StopGame;
import org.kwstudios.play.ragemode.database.MySQLConnector;
import org.kwstudios.play.ragemode.events.EventListener;
import org.kwstudios.play.ragemode.gameLogic.PlayerList;
import org.kwstudios.play.ragemode.metrics.Metrics;
import org.kwstudios.play.ragemode.statistics.YAMLStats;
import org.kwstudios.play.ragemode.updater.Updater;

import com.mysql.jdbc.authentication.MysqlClearPasswordPlugin;

/*import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;*/

public class PluginLoader extends JavaPlugin {

	private static PluginLoader instance = null;
	private static MySQLConnector mySqlConnector = null;
//	private ProtocolManager protocolManager;

	@Override
	public void onEnable() {
		super.onEnable();

		PluginLoader.instance = this;
		
/*		protocolManager = ProtocolLibrary.getProtocolManager();		
		protocolManager.addPacketListener(
				new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
					@Override
					public void onPacketSending(PacketEvent event) {
						if (PlayerList.isPlayerPlaying(event.getPlayer().getUniqueId().toString()) && PlayerList.isGameRunning(PlayerList.getPlayersGame(event.getPlayer()))) {
							if (!TabAPI.allowedPackets.contains(event.getPacket().toString())) {
								event.setCancelled(true);
							} else {
								TabAPI.allowedPackets.remove(event.getPacket().toString());
							}
						}
					}
				});
		new TabAPI(protocolManager);*/

		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		logger.info(pluginDescriptionFile.getName() + " was loaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
		// getConfig().options().copyDefaults(true);
		// saveConfig();
		new PlayerList(getConfig());
		new EventListener(this, getConfig());

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

		new Updater(this, "http://mc.kwstudios.org/plugin-updater/ragemode.html");
		
		if(getConfig().isSet("settings.global.statistics")) {
			if(getConfig().isSet("settings.global.statistics.yaml")) {
				if(getConfig().getBoolean("settings.global.statistics.yaml"))
					YAMLStats.initS();
			}
			else
				getConfig().set("settings.global.statistics.yaml", "false");
			
			if(getConfig().isSet("settings.global.statistics.mySQL")) {
				if(getConfig().isSet("settings.global.statistics.mySQL")) {
					if(getConfig().getBoolean("settings.global.statistics.mySQL.enable"))
						mySqlConnector = new MySQLConnector("empty", 1337, "empty", "empty", "empty");						
				}
				else
					getConfig().set("settings.global.statistics.mySQL.enable", "false");
			}
			else
				getConfig().set("settings.global.statistics.mySQL.enable", "false");
		}
		else {
			getConfig().set("settings.global.statistics.yaml", "true");
			getConfig().set("settings.global.statistics.mySQL.enable", "false");
			getConfig().set("settings.global.statistics.mySQL.url", "put.your.databaseURL.here");
			getConfig().set("settings.global.statistics.mySQL.port", "put.your.databasePort.here");
			getConfig().set("settings.global.statistics.mySQL.database", "put.your.databaseName.here");
			getConfig().set("settings.global.statistics.mySQL.username", "put.your.databaseUsername.here");
			getConfig().set("settings.global.statistics.mySQL.password", "put.your.databasePassword.here");		
		}
		saveConfig();
		
	}

	@Override
	public void onDisable() {
		super.onDisable();
		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		StopGame.stopAllGames(getConfig(), logger);

		logger.info(pluginDescriptionFile.getName() + " was unloaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a Player!");
			return false;
		}

		Player player = (Player) sender;

		CommandParser commandParser = new CommandParser(player, command, label, args, getConfig());

		if (!commandParser.isCommand()) {
			return false;
		}

		saveConfig();

		return true;
	}

	public static PluginLoader getInstance() {
		return PluginLoader.instance;
	}

	public static MySQLConnector getMySqlConnector() {
		return mySqlConnector;
	}
	
}
