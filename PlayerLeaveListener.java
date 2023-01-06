package me.kirbyis.nextplayer.listeners;

import me.kirbyis.nextplayer.Nextplayer;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
public class PlayerLeaveListener implements Listener {
    private final Nextplayer plugin;
    private final ArrayList<String> playerNames;


    public PlayerLeaveListener(Nextplayer plugin, ArrayList<String> playerNames) {
        this.playerNames = playerNames;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        if (plugin.autoRemoveEnabled && playerNames.contains(playerName)) {
            playerNames.remove(playerName);
            Bukkit.broadcast(playerName + " lämnade och blev borttagen från listan, du kan togglela detta med /listautoremove", "cozypol.seemessage");
        }
    }
}