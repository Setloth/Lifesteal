package page.echology.lifesteal.utility;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import page.echology.lifesteal.Lifesteal;
import page.echology.lifesteal.config.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.BiConsumer;

public class Utils {

    public static void severe(String l) {
        Bukkit.getLogger().severe("[Lifesteal - Error] An internal exception has been encountered: \n"+l+"\nTime: "+ new Date());
    }

    public static void warn(String l) {
        Bukkit.getLogger().warning("[Lifesteal - Warning] "+l+"\nTime: "+ new Date());
    }

    public static void log(String l) {
        Bukkit.getLogger().info("[Lifesteal - Log] "+l+"\nTime: "+ new Date());
    }

    public static ComponentBuilder component(String ...input) {
        ComponentBuilder builder = new ComponentBuilder();
        for (String s : input) {
            builder.append(colored(s));
        }
        return builder;
    }

    public static BaseComponent[] hyperLink(String text, String url) {

        return new ComponentBuilder()
                .append(colored(text))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, url)).create();
    }

    public static @NotNull BaseComponent[] colored(String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void reload(Lifesteal plugin, BiConsumer<Exception, Long> consumer) {
        long start = System.currentTimeMillis();
        try {
            plugin.reloadConfig();
            plugin.updateRecipe();
            consumer.accept(null, (System.currentTimeMillis()-start));
        } catch (Exception e) {
            consumer.accept(e, (System.currentTimeMillis()-start));
        }

    }

    public static @NotNull BaseComponent[] permissionMessage(String permission) {
        return colored("&cYou do not have the required permission to execute this command, contact a server administrator if you believe this is a problem.\n&cMissing permission: "+permission);
    }

    public static void noLives(Player p){
        if ((Config.LIVES_COMMAND.value() != null)) {
            for (Object o : Config.LIVES_COMMAND.value(ArrayList.class)) {
                String cmd = ((String) o).replaceAll("%player%", p.getName());
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }

        if (Config.LIVES_FORCE_SPECTATOR.value(Boolean.class).equals(true)) {
            p.setGameMode(GameMode.SPECTATOR);
        }
    }


}
