package org.kwstudios.play.ragemode.commands;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.kwstudios.play.ragemode.holo.HoloHolder;
import org.kwstudios.play.ragemode.loader.PluginLoader;
import org.kwstudios.play.ragemode.signs.SignConfiguration;
import org.kwstudios.play.ragemode.signs.SignCreator;
import org.kwstudios.play.ragemode.toolbox.ConstantHolder;
import org.kwstudios.play.ragemode.toolbox.GetGames;

public class ReloadConfig {

	public ReloadConfig(Player player, String label, String[] args, FileConfiguration fileConfiguration) {
		PluginLoader pluginLoader = PluginLoader.getInstance();

		pluginLoader.reloadConfig();
		pluginLoader.initStatistics();
		pluginLoader.loadMessages();
		pluginLoader.initStatusMessages();

		SignConfiguration.initSignConfiguration();
		String[] games = GetGames.getGameNames(pluginLoader.getConfig());
		for (String game : games) {
			SignCreator.updateAllSigns(game);
		}
		HoloHolder.initHoloHolder();

		player.sendMessage(ConstantHolder.RAGEMODE_PREFIX
				+ ChatColor.translateAlternateColorCodes('�', PluginLoader.getMessages().RELOADED_SUCCESSFULLY));
		// TODO To be continued...
	}

}
