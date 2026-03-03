package net.ensdaireplugin.ensdaire.commands;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.arena.Arena;
import net.ensdaireplugin.ensdaire.game.GameState;
import net.ensdaireplugin.ensdaire.gui.ArenaSelectGui;
import net.ensdaireplugin.ensdaire.gui.StatsGui;
import net.ensdaireplugin.ensdaire.player.PlayerData;
import net.ensdaireplugin.ensdaire.utils.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class EnsDaireCommand implements CommandExecutor, TabCompleter {

    private final EnsDaire plugin;

    public EnsDaireCommand(EnsDaire plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String pref = plugin.getConfig().getString("messages.prefix", "§8[§bEnsDaire§8] §r");

        if (args.length == 0) {
            sendHelp(sender, pref);
            return true;
        }

        switch (args[0].toLowerCase()) {

            // ─── OYUNCU KOMUTLARI ───
            case "katil", "join" -> {
                requirePlayer(sender, () -> {
                    Player p = (Player) sender;
                    if (args.length >= 2) {
                        joinByName(p, args[1], pref);
                    } else {
                        ArenaSelectGui.open(plugin, p);
                    }
                });
            }
            case "cik", "leave", "quit", "ayrıl", "ayril" -> {
                requirePlayer(sender, () -> {
                    Player p = (Player) sender;
                    Arena arena = plugin.getArenaManager().getByPlayer(p.getUniqueId());
                    if (arena == null) { p.sendMessage(pref + "§cBir oyunda değilsin."); return; }
                    arena.removePlayer(p);
                });
            }
            case "istatistik", "stats", "stat" -> {
                requirePlayer(sender, () -> StatsGui.open(plugin, (Player) sender));
            }
            case "top", "liste" -> {
                showTop(sender, pref);
            }
            case "arenalar", "arenas" -> {
                listArenas(sender, pref);
            }

            // ─── ADMIN KOMUTLARI ───
            case "admin" -> {
                if (!sender.hasPermission("ensdaire.admin")) { sender.sendMessage(pref + Msg.get(plugin, "messages.no-permission")); return true; }
                if (args.length < 2) { sendAdminHelp(sender, pref); return true; }
                
                if (args[1].equalsIgnoreCase("gui") || args[1].equalsIgnoreCase("menu")) {
                    requirePlayer(sender, () -> {
                        if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin gui <arena>"); return; }
                        net.ensdaireplugin.ensdaire.gui.AdminGui.open(plugin, (Player) sender, args[2]);
                    });
                    return true;
                }
                
                handleAdmin(sender, args, pref);
            }

            default -> sendHelp(sender, pref);
        }

        return true;
    }

    private void joinByName(Player player, String arenaId, String pref) {
        if (plugin.getArenaManager().getByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(pref + Msg.get(plugin, "messages.already-in-game"));
            return;
        }
        Arena arena = plugin.getArenaManager().get(arenaId);
        if (arena == null) {
            player.sendMessage(pref + Msg.get(plugin, "messages.arena-not-found", "{arena}", arenaId));
            return;
        }
        Arena.JoinResult result = arena.addPlayer(player);
        switch (result) {
            case FULL         -> player.sendMessage(pref + Msg.get(plugin, "messages.game-full",
                                    "{current}", String.valueOf(arena.getPlayers().size()),
                                    "{max}", String.valueOf(arena.getMaxPlayers())));
            case NOT_JOINABLE -> player.sendMessage(pref + Msg.get(plugin, "messages.game-not-joinable"));
            case DISABLED     -> player.sendMessage(pref + "§cBu arena kapalı.");
            default -> {}
        }
    }

    private void showTop(CommandSender sender, String pref) {
        List<PlayerData> top = plugin.getPlayerDataManager().getTop(10);
        sender.sendMessage("§8§m══════════════════════");
        sender.sendMessage("§b§l     🏆 En İyi Oyuncular");
        sender.sendMessage("§8§m══════════════════════");
        if (top.isEmpty()) { sender.sendMessage("§7Henüz veri yok."); return; }
        for (int i = 0; i < top.size(); i++) {
            PlayerData d = top.get(i);
            String medal = i == 0 ? "§6§l🥇" : i == 1 ? "§7§l🥈" : i == 2 ? "§c§l🥉" : "§8#" + (i + 1);
            sender.sendMessage(medal + " §e" + d.getName()
                + " §8| §6" + d.getTokens() + " jeton"
                + " §8| §7" + d.getWins() + " kazanma"
                + " §8| §f" + d.getRank());
        }
        sender.sendMessage("§8§m══════════════════════");
    }

    private void listArenas(CommandSender sender, String pref) {
        sender.sendMessage("§b§lArenalar §8(" + plugin.getArenaManager().getArenaCount() + "):");
        for (Arena a : plugin.getArenaManager().all()) {
            String stateColor = switch (a.getState()) {
                case WAITING, COUNTDOWN -> "§a";
                case RUNNING -> "§e";
                case DISABLED -> "§c";
                default -> "§7";
            };
            sender.sendMessage("  §8▸ §f" + a.getId()
                + " §8| " + stateColor + a.getState()
                + " §8| §7" + a.getPlayers().size() + "/" + a.getMaxPlayers() + " oyuncu");
        }
    }

    private void handleAdmin(CommandSender sender, String[] args, String pref) {
        String sub = args[1].toLowerCase();
        switch (sub) {
            case "olustur", "create" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin olustur <isim>"); return; }
                if (plugin.getArenaManager().get(args[2]) != null) {
                    sender.sendMessage(pref + "§cBu isimde arena zaten var!"); return;
                }
                Arena a = plugin.getArenaManager().create(args[2]);
                sender.sendMessage(pref + "§aArena oluşturuldu: §e" + args[2]);
                sender.sendMessage(pref + "§7Sonraki adım: §e/ed admin kapsul <arena> §7ile kapsül konumlarını ekle.");
            }
            case "sil", "delete" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin sil <arena>"); return; }
                boolean deleted = plugin.getArenaManager().delete(args[2]);
                sender.sendMessage(deleted ? pref + "§aArena silindi: §e" + args[2] : pref + "§cArena bulunamadı!");
            }
            case "lobi", "lobby" -> {
                requirePlayer(sender, () -> {
                    if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin lobi <arena>"); return; }
                    Arena a = getArena(sender, args[2], pref); if (a == null) return;
                    a.setLobbySpawn(((Player) sender).getLocation());
                    plugin.getArenaManager().saveArenas();
                    sender.sendMessage(pref + "§aLobi spawnu ayarlandı: §e" + args[2]);
                });
            }
            case "spectator", "izleyici" -> {
                requirePlayer(sender, () -> {
                    if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin izleyici <arena>"); return; }
                    Arena a = getArena(sender, args[2], pref); if (a == null) return;
                    a.setSpectatorSpawn(((Player) sender).getLocation());
                    plugin.getArenaManager().saveArenas();
                    sender.sendMessage(pref + "§aİzleyici spawnu ayarlandı: §e" + args[2]);
                });
            }
            case "kapsul", "capsule" -> {
                requirePlayer(sender, () -> {
                    if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin kapsul <arena>"); return; }
                    Arena a = getArena(sender, args[2], pref); if (a == null) return;
                    a.getCapsuleSpawns().add(((Player) sender).getLocation());
                    plugin.getArenaManager().saveArenas();
                    sender.sendMessage(pref + "§aKapsül §e#" + a.getCapsuleSpawns().size() + " §aeklendi → §e" + args[2]);
                });
            }
            case "shulker" -> {
                requirePlayer(sender, () -> {
                    if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin shulker <arena>"); return; }
                    Arena a = getArena(sender, args[2], pref); if (a == null) return;
                    a.getShulkerSpawns().add(((Player) sender).getLocation());
                    plugin.getArenaManager().saveArenas();
                    sender.sendMessage(pref + "§aShulker noktası §e#" + a.getShulkerSpawns().size() + " §aeklendi → §e" + args[2]);
                });
            }
            case "kapsulsil", "removecapsule" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin kapsulsil <arena>"); return; }
                Arena a = getArena(sender, args[2], pref); if (a == null) return;
                if (!a.getCapsuleSpawns().isEmpty()) {
                    a.getCapsuleSpawns().remove(a.getCapsuleSpawns().size() - 1);
                    plugin.getArenaManager().saveArenas();
                    sender.sendMessage(pref + "§aSon kapsül noktası silindi. Kalan: §e" + a.getCapsuleSpawns().size());
                }
            }
            case "shulkersil", "removeshulker" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin shulkersil <arena>"); return; }
                Arena a = getArena(sender, args[2], pref); if (a == null) return;
                if (!a.getShulkerSpawns().isEmpty()) {
                    a.getShulkerSpawns().remove(a.getShulkerSpawns().size() - 1);
                    plugin.getArenaManager().saveArenas();
                    sender.sendMessage(pref + "§aSon shulker noktası silindi.");
                }
            }
            case "baslat", "start" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin baslat <arena>"); return; }
                Arena a = getArena(sender, args[2], pref); if (a == null) return;
                a.startGame(); // launchGame() private olduğundan startGame() ekledik aşağıda
                sender.sendMessage(pref + "§aOyun başlatıldı: §e" + args[2]);
            }
            case "durdur", "stop" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin durdur <arena>"); return; }
                Arena a = getArena(sender, args[2], pref); if (a == null) return;
                a.stopAllTasks();
                a.getCircleManager().removeAllCircles();
                a.getShulkerManager().clearAll();
                a.setState(GameState.WAITING);
                sender.sendMessage(pref + "§aOyun durduruldu: §e" + args[2]);
            }
            case "ac", "enable" -> {
                if (args.length < 3) return;
                Arena a = getArena(sender, args[2], pref); if (a == null) return;
                a.setState(GameState.WAITING);
                plugin.getArenaManager().saveArenas();
                sender.sendMessage(pref + "§aArena açıldı: §e" + args[2]);
            }
            case "kapat", "disable" -> {
                if (args.length < 3) return;
                Arena a = getArena(sender, args[2], pref); if (a == null) return;
                a.stopAllTasks();
                a.setState(GameState.DISABLED);
                plugin.getArenaManager().saveArenas();
                sender.sendMessage(pref + "§cArena kapatıldı: §e" + args[2]);
            }
            case "bilgi", "info" -> {
                if (args.length < 3) return;
                Arena a = getArena(sender, args[2], pref); if (a == null) return;
                showArenaInfo(sender, a);
            }
            case "reload" -> {
                plugin.reloadConfig();
                sender.sendMessage(pref + "§aConfig yeniden yüklendi.");
            }
            default -> sendAdminHelp(sender, pref);
        }
    }

    private void showArenaInfo(CommandSender sender, Arena a) {
        sender.sendMessage("§8§m══════════════════════");
        sender.sendMessage("§b§l Arena: §f" + a.getId());
        sender.sendMessage("§8§m══════════════════════");
        sender.sendMessage("§eDurum: §f" + a.getState());
        sender.sendMessage("§eOyuncular: §f" + a.getPlayers().size() + "/" + a.getMaxPlayers());
        sender.sendMessage("§eLobi: §f" + (a.getLobbySpawn() != null ? "§a✓" : "§c✗"));
        sender.sendMessage("§eİzleyici: §f" + (a.getSpectatorSpawn() != null ? "§a✓" : "§c✗"));
        sender.sendMessage("§eKapsüller: §f" + a.getCapsuleSpawns().size());
        sender.sendMessage("§eShulker Noktaları: §f" + a.getShulkerSpawns().size());
        if (a.getState() == GameState.RUNNING) {
            sender.sendMessage("§eRound: §f" + a.getCurrentRound());
            sender.sendMessage("§eSağ kalan: §f" + a.getAlivePlayers().size());
        }
        sender.sendMessage("§8§m══════════════════════");
    }

    private void sendHelp(CommandSender sender, String pref) {
        sender.sendMessage("§8§m══════════════════════");
        sender.sendMessage("§b§l  EnsDaire §7- Komutlar");
        sender.sendMessage("§8§m══════════════════════");
        sender.sendMessage("§e/ed katil §7[arena] §8- §7Oyuna katıl (GUI veya direkt)");
        sender.sendMessage("§e/ed cik §8- §7Oyundan ayrıl");
        sender.sendMessage("§e/ed istatistik §8- §7İstatistik menüsü");
        sender.sendMessage("§e/ed top §8- §7En iyi oyuncular");
        sender.sendMessage("§e/ed arenalar §8- §7Tüm arenalar");
        if (sender.hasPermission("ensdaire.admin")) {
            sender.sendMessage("§c/ed admin §8- §7Admin komutları");
        }
        sender.sendMessage("§8§m══════════════════════");
    }

    private void sendAdminHelp(CommandSender sender, String pref) {
        sender.sendMessage("§8§m══════════════════════");
        sender.sendMessage("§c§l  EnsDaire §7- Admin Komutları");
        sender.sendMessage("§8§m══════════════════════");
        sender.sendMessage("§c/ed admin olustur §f<isim>");
        sender.sendMessage("§c/ed admin sil §f<arena>");
        sender.sendMessage("§c/ed admin lobi §f<arena>  §8← §7Bulunduğun konumu atar");
        sender.sendMessage("§c/ed admin izleyici §f<arena>");
        sender.sendMessage("§c/ed admin kapsul §f<arena>  §8← §7Kapsül noktası ekle");
        sender.sendMessage("§c/ed admin kapsulsil §f<arena>");
        sender.sendMessage("§c/ed admin shulker §f<arena>  §8← §7Shulker noktası ekle");
        sender.sendMessage("§c/ed admin shulkersil §f<arena>");
        sender.sendMessage("§c/ed admin baslat §f<arena>");
        sender.sendMessage("§c/ed admin durdur §f<arena>");
        sender.sendMessage("§c/ed admin ac/kapat §f<arena>");
        sender.sendMessage("§c/ed admin bilgi §f<arena>");
        sender.sendMessage("§c/ed admin reload");
        sender.sendMessage("§8§m══════════════════════");
    }

    private Arena getArena(CommandSender sender, String id, String pref) {
        Arena a = plugin.getArenaManager().get(id);
        if (a == null) sender.sendMessage(pref + Msg.get(plugin, "messages.arena-not-found", "{arena}", id));
        return a;
    }

    private void requirePlayer(CommandSender sender, Runnable action) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Msg.get(plugin, "messages.player-only"));
        } else {
            action.run();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> opts = new ArrayList<>(List.of("katil","cik","istatistik","top","arenalar"));
            if (sender.hasPermission("ensdaire.admin")) opts.add("admin");
            return filter(opts, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            return filter(List.of("olustur","sil","lobi","izleyici","kapsul","kapsulsil",
                "shulker","shulkersil","baslat","durdur","ac","kapat","bilgi","reload"), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("admin")) {
            return filter(plugin.getArenaManager().all().stream()
                .map(Arena::getId).toList(), args[2]);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("katil") || args[0].equalsIgnoreCase("join"))) {
            return filter(plugin.getArenaManager().all().stream()
                .map(Arena::getId).toList(), args[1]);
        }
        return List.of();
    }

    private List<String> filter(List<String> list, String prefix) {
        return list.stream().filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
            .collect(java.util.stream.Collectors.toList());
    }
}
