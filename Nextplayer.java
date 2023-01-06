package me.kirbyis.nextplayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.kirbyis.nextplayer.listeners.PlayerLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
public class Nextplayer extends JavaPlugin implements CommandExecutor {

    // Create a list to store player names
    private static ArrayList<String> playerNames = new ArrayList<>();
    private static List<String> onlinePlayers = new ArrayList<>();
    private String currentPlayer;
    private String nextPlayer;
    private String gameState;
    private int elapsedTime = 0;
    private int totalSecs = 0;
    private int hours;
    private int minutes;
    private int seconds;
    private boolean gameStarted = false;
    private String timeString = "§r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r §r";

    // Declare the timerTask variable
    BukkitTask timerTask;

    // Add the autoRemoveEnabled field
    public boolean autoRemoveEnabled = true;


    @Override
    public void onEnable() {
        // Register the commands
        this.getCommand("addplayer").setExecutor(this);
        this.getCommand("removeplayer").setExecutor(this);
        this.getCommand("printlist").setExecutor(this);
        this.getCommand("clearlist").setExecutor(this);
        this.getCommand("cycle").setExecutor(this);
        this.getCommand("addall").setExecutor(this);
        this.getCommand("listautoremove").setExecutor(this);
        this.getCommand("skip").setExecutor(this);
        this.getCommand("currentandnext").setExecutor(this);
        this.getCommand("togglegame").setExecutor(this);
        this.getCommand("timerreset").setExecutor(this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this, playerNames), this);
        // Register the placeholders
        new PlaceholderExpansion() {
            @Override
            public String getIdentifier() {
                return "cozypol";
            }

            @Override
            public String getAuthor() {
                return "Kirby";
            }

            @Override
            public String getVersion() {
                return "1.0.0";
            }
            @Override
            public boolean canRegister() {
                return true;
            }
            @Override
            public boolean persist() {
                return true;
            }

            @Override
            public String onPlaceholderRequest(Player player, String placeholder) {
                if (placeholder.equals("currentplayer")) {
                    if (!(currentPlayer == null)) {
                        return currentPlayer;
                    } else {
                        return "";
                    }
                } else if (placeholder.equals("nextplayer")) {
                    if (!(nextPlayer == null)) {
                        return nextPlayer;
                    } else {
                        return "";
                    }
                } else if (placeholder.equals("gamestate")) {
                    if (!gameStarted) {
                        return "§c§lJoina §b§lNu§c§l!§b§l!§c§l!§b§l! §c§l>§b§l>§c§l>";
                    } else {
                        return "§c§lEtt spel pågår!";
                    }
                } else if (placeholder.equals("elapsedtime")) {
                    if (!(timeString == null)) {
                        return timeString;
                    } else {
                        return "";
                    }
                }
                return null;
            }
        }.register();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "addplayer":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String senderName = player.getName();
                    if (sender.hasPermission("playerlist.addplayer")) {
                        if (args.length > 0) {
                            if (playerNames.contains(args[0])) {
                                Bukkit.broadcast("", "cozypol.seemessage");
                                Bukkit.broadcast("§c§l" + senderName + "§r försökte lägga till §6§l" + args[0] + "§r till listan men de var redan med i listan.", "cozypol.seemessage");
                            } else {
                                playerNames.add(args[0]);
                                Bukkit.broadcast("", "cozypol.seemessage");
                                Bukkit.broadcast("§c§l" + senderName + "§r lade till §6§l" + args[0] + "§r till listan", "cozypol.seemessage");
                                if (playerNames.size() <= 1) {
                                    currentPlayer = playerNames.get(0);
                                } else {
                                    currentPlayer = playerNames.get(0);
                                    nextPlayer = playerNames.get(1);
                                }
                            }
                        } else {
                            sender.sendMessage("");
                            sender.sendMessage("Använding: /addplayer <namn>");
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage("Du har inte tillåtelse till detta kommando.");
                    }
                } else {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare.");
                }
                return true;
            case "removeplayer":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String senderName = player.getName();
                    if (sender.hasPermission("playerlist.removeplayer")) {
                        if (args.length > 0) {
                            String playerToRemove = args[0];
                            if (playerNames.contains(playerToRemove)) {
                                playerNames.remove(playerToRemove);
                                Bukkit.broadcast("", "cozypol.seemessage");
                                Bukkit.broadcast("§c§l" + senderName + "§r tog bort §6§l" + playerToRemove + "§r från listan", "cozypol.seemessage");

                                // Update current and next player
                                if (playerNames.size() > 0) {
                                    currentPlayer = playerNames.get(0);
                                } else {
                                    currentPlayer = null;
                                }
                                if (playerNames.size() > 1) {
                                    nextPlayer = playerNames.get(1);
                                } else {
                                    nextPlayer = null;
                                }
                            } else {
                                Bukkit.broadcast("", "cozypol.seemessage");
                                Bukkit.broadcast("§c§l" + senderName + "§r försökte ta bort §6§l" + playerToRemove + "§r från listan men den personen fanns inte i listan", "cozypol.seemessage");
                            }
                        } else {
                            sender.sendMessage("");
                            sender.sendMessage("Användning: /removeplayer,/pdel,/rplayer <namn>");
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage("Du har inte tillgång till detta kommando.");
                    }
                } else {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare.");
                }
                return true;
            case "printlist":
                if (playerNames.isEmpty()) {
                    sender.sendMessage("");
                    sender.sendMessage("Listan är tom");
                } else {
                    sender.sendMessage("");
                    sender.sendMessage("§a§lLista av spelare");
                    sender.sendMessage("");
                    for (String playerUsername : playerNames) {
                        sender.sendMessage(playerUsername);
                    }
                }
                return true;
            case "clearlist":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String senderName = player.getName();
                    if (sender.hasPermission("playerlist.clearlist")) {
                        if (!(playerNames.size() == 0)) {
                            playerNames.clear();
                            Bukkit.broadcast("", "cozypol.seemessage");
                            Bukkit.broadcast("§c§l" + senderName + "§r rensade listan.", "cozypol.seemessage");
                    } else {
                            sender.sendMessage("");
                            sender.sendMessage("Listan är redan rensad.");
                    }
                } else {
                        sender.sendMessage("");
                        sender.sendMessage("Du har inte tillåtelse till detta kommando.");
                    }
                } else {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare.");
                }
                return true;
            case "cycle":
                // Check if the sender is a player
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String senderName = player.getName();
                    // Check if the player has permission to use the command
                    if (playerNames.isEmpty()) {
                        sender.sendMessage("");
                        sender.sendMessage("Listan är tom");
                    } else {
                        if (sender.hasPermission("playerlist.cycle")) {
                            Bukkit.broadcast("", "cozypol.seemessage");
                            Bukkit.broadcast("§c§l" + senderName + "§r gjorde §b§l/cycle", "cozypol.seemessage");
                            // Check if the list is empty
                            // Get the current player
                            currentPlayer = playerNames.get(0);

                            // Check if the list has more than one player
                            if (playerNames.size() > 1) {
                                // Get the next player
                                nextPlayer = playerNames.get(1);
                                playerNames.remove(0);
                                playerNames.add(currentPlayer);

                                // Send the names of the current and next players to the sender
                                sender.sendMessage("");
                                sender.sendMessage("§a§lNU§3§l: §6" + currentPlayer);
                                sender.sendMessage("");
                                sender.sendMessage("§a§lNÄSTA§3§l: §6" + nextPlayer);
                            } else {
                                // If the list has only one player, send the name of the current player to the sender
                                sender.sendMessage("");
                                sender.sendMessage("§a§lNU§3§l: §6" + currentPlayer);
                                sender.sendMessage("");
                                sender.sendMessage("§a§lNÄSTA§3§l: §6Det finns bara en spelare i listan");
                            }
                        } else {
                            // If the player does not have permission, send an error message
                            sender.sendMessage("");
                            sender.sendMessage("Du har inte tillgång till detta kommando.");
                        }
                    }
                } else {
                    // If the sender is not a player, send an error message
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare.");
                }
                return true;
            case "addall":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String senderName = player.getName();
                    if (sender.hasPermission("playerlist.addall")) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (!playerNames.contains(onlinePlayer.getName())) {
                                playerNames.add(onlinePlayer.getName());
                                if (playerNames.size() <= 1) {
                                    currentPlayer = playerNames.get(0);
                                } else {
                                    currentPlayer = playerNames.get(0);
                                    nextPlayer = playerNames.get(1);
                                }
                            }
                        }
                        Bukkit.broadcast("", "cozypol.seemessage");
                        Bukkit.broadcast("§c§l" + senderName + "§r lade till alla online spelare till listan", "cozypol.seemessage");
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage("Du har inte tillåtelse till detta kommando.");
                    }
                } else {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare.");
                }
                return true;
            case "listautoremove":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String senderName = player.getName();
                    if (sender.hasPermission("playerlist.listautoremove")) {
                        if (autoRemoveEnabled) {
                            Bukkit.broadcast("", "cozypol.seemessage");
                            Bukkit.broadcast("§c§l" + senderName + "§r stängde av §b§lAuto-Remove.", "cozypol.seemessage");
                            autoRemoveEnabled = false;
                        } else {
                            Bukkit.broadcast("", "cozypol.seemessage");
                            Bukkit.broadcast("§c§l" + senderName + "§r satte på §b§lAuto-Remove.", "cozypol.seemessage");
                            autoRemoveEnabled = true;
                        }
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage("Du har inte tillåtelse till detta kommando");
                    }
                } else {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare.");
                }
                return true;
            case "skip":
                if (sender.hasPermission("playerlist.skip")) {
                    // Check if the sender is a player
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        String senderName = player.getName();
                        // Check if the list is empty
                        if (playerNames.isEmpty()) {
                            sender.sendMessage("");
                            sender.sendMessage("Listan är tom");
                            return true;
                        }
                        // Check if the number of players to skip was provided as an argument
                        if (args.length == 0) {
                            currentPlayer = playerNames.get(0);
                            nextPlayer = playerNames.get(1);
                            playerNames.remove(0);
                            playerNames.add(currentPlayer);
                            Bukkit.broadcast("", "cozypol.seemessage");
                            Bukkit.broadcast("§c§l" + senderName + " §rgjorde §b§l/skip", "cozypol.seemessage");
                            sender.sendMessage("");
                            sender.sendMessage("§a§lNU§3§l: §6" + currentPlayer);
                            sender.sendMessage("");
                            if (nextPlayer != null) {
                                sender.sendMessage("§a§lNÄSTA§3§l: §6" + nextPlayer);
                            } else {
                                sender.sendMessage("§a§lNÄSTA§3§l: §6Det finns bara en spelare i listan");
                            }
                            return true;
                        }
                        // Try to parse the argument as an integer
                        int numToSkip;
                        try {
                            numToSkip = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("The number of players to skip must be a valid §b§linteger (number)!");
                            return true;
                        }
                        // Skip the specified number of players
                        for (int i = 0; i < numToSkip; i++) {
                            // Remove the current player from the beginning of the list and add it to the end
                            currentPlayer = playerNames.get(0);
                            playerNames.remove(0);
                            playerNames.add(currentPlayer);
                        }
                        // Set the next player to the one at the beginning of the list
                        currentPlayer = playerNames.get(0);
                        if (playerNames.size() > 1) {
                            nextPlayer = playerNames.get(1);
                        } else {
                            nextPlayer = null;
                        }
                        // Send a message to the broadcast channel
                        Bukkit.broadcast("", "cozypol.seemessage");
                        Bukkit.broadcast("§c§l" + senderName + " §rgjorde §b§l/skip " + numToSkip, "cozypol.seemessage");
                        // Send the names of the current and next players to the sender
                        sender.sendMessage("");
                        sender.sendMessage("§a§lNU§3§l: §6" + currentPlayer);
                        sender.sendMessage("");
                        if (nextPlayer != null) {
                            sender.sendMessage("§a§lNÄSTA§3§l: §6" + nextPlayer);
                        } else {
                            sender.sendMessage("§a§lNÄSTA§3§l: §6Det finns bara en spelare i listan");
                        }
                        return true;
                    } else {
                        sender.sendMessage("");
                        sender.sendMessage("Det här kommandot går endast att användas av spelare");
                        return true;
                    }
                }
            case "currentandnext":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare");
                    return true;
                }
                if (!sender.hasPermission("playerlist.currentandnext")) {
                    sender.sendMessage("");
                    sender.sendMessage("Du har inte tillgång till detta kommand");
                    return true;
                }
                sender.sendMessage("");
                sender.sendMessage("§a§lNU§3§l: §6" + currentPlayer);
                sender.sendMessage("");
                sender.sendMessage("§a§lNÄSTA§3§l: §6" + nextPlayer);
                return true;
            case "togglegame":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare");
                    return true;
                }
                Player player = (Player) sender;
                String senderName = player.getName();
                if (!sender.hasPermission("playerlist.togglegame")) {
                    sender.sendMessage("");
                    sender.sendMessage("Du har inte tillgång till detta kommand");
                    return true;
                }
                if (!gameStarted) {
                    // Initialize `timerTask` with a new task that will run every 20 ticks (1 second).
                    this.timerTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
                        // Increment elapsedTime by 20
                        Nextplayer.this.elapsedTime += 20;
                        // Calculate the number of hours, minutes, and seconds that have passed
                        totalSecs = elapsedTime/20;
                        hours = totalSecs / 3600;
                        minutes = (totalSecs % 3600) / 60;
                        seconds = totalSecs % 60;

                        // Use String.format to create the timeString
                        timeString = String.format("%02d h, %02d min, %02d sec", hours, minutes, seconds);
                    }, 0, 20);
                    gameStarted = true;
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§c§l" + senderName + "§r startade spelet");
                    return true;
                } else if (gameStarted) {
                    gameStarted = false;
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§c§l" + senderName + "§r avslutade spelet");
                    this.timerTask.cancel();
                    return true;
                }
            case "timerreset":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("");
                    sender.sendMessage("Det här kommandot går endast att användas av spelare");
                    return true;
                }
                Player player2 = (Player) sender;
                String senderName2 = player2.getName();
                if (!sender.hasPermission("playerlist.timerreset")) {
                    sender.sendMessage("");
                    sender.sendMessage("Du har inte tillgång till detta kommand");
                    return true;
                }
                if (!gameStarted) {
                    elapsedTime = 0;
                    totalSecs = elapsedTime/20;
                    hours = totalSecs / 3600;
                    minutes = (totalSecs % 3600) / 60;
                    seconds = totalSecs % 60;


                    // Send a message to the sender to confirm that the timer has been reset
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§c§l" + senderName2 + "§r ställde om timern");
                    return true;
                } else if (gameStarted) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§c§l" + senderName2 + "§r försökte ställa om timern, men man kan inte ställa om timern när ett spel pågår");
                }
            default:
                return false;
        }
    }
}