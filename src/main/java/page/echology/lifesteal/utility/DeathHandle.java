package page.echology.lifesteal.utility;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import page.echology.lifesteal.config.Config;

public class DeathHandle {

    public static void player(Player p, Player k, PlayerDeathEvent event) {

        if (!Config.PVP_ENABLED.value(Boolean.class)) return;

        AttributeInstance pAttr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance kAttr = k.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (pAttr == null || kAttr == null) {
            Utils.severe("Could not get attributes for victim/attacker on death");
            return;
        }

        int gainMult = 1;
        int lossMult = 1;

        if (Config.GAIN.value(Number.class) != null && Config.GAIN.value(Number.class).intValue() != 0) gainMult = Config.GAIN.value(Number.class).intValue();
        if (Config.LOSS.value(Number.class) != null && Config.LOSS.value(Number.class).intValue() != 0) lossMult = Config.LOSS.value(Number.class).intValue();


        int upperValue = Config.UPPER.value(Number.class).intValue();
        if (Config.UPPER.value() != null && kAttr.getBaseValue() + gainMult <= upperValue) {
            kAttr.setBaseValue(kAttr.getBaseValue()  + gainMult);
            if (Config.ATTACKER_PVP_MESSAGE.value() != null || Config.ATTACKER_PVP_MESSAGE.value(String.class).length() > 0) {
                String msg = Config.ATTACKER_PVP_MESSAGE.value(String.class)

                        .replaceAll("%attacker%", k.getDisplayName())
                        .replaceAll("%victim%", p.getDisplayName())
                        .replaceAll("%victim-lives%", String.valueOf(pAttr.getBaseValue()))
                        .replaceAll("%attacker-lives%", String.valueOf(kAttr.getBaseValue()));

                k.spigot().sendMessage(Utils.colored(msg));
            }
        }


        int lowerValue = Config.LOWER.value(Number.class).intValue();
        if (Config.LOWER.value() != null && pAttr.getBaseValue() - lossMult <= lowerValue) {


            if (Config.LIVES_MESSAGE.value() != null && Config.LIVES_MESSAGE.value(String.class).length() > 0) {
                String msg = Config.LIVES_MESSAGE.value(String.class)
                        .replaceAll("%attacker%", k.getDisplayName())
                        .replaceAll("%victim%", p.getDisplayName())
                        .replaceAll("%victim-lives%", String.valueOf(pAttr.getBaseValue()))
                        .replaceAll("%attacker-lives%",String.valueOf(kAttr.getBaseValue()));

                p.spigot().sendMessage(Utils.colored(msg));
            }

            Utils.noLives(p);
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()); // check if work
            return;
        } else {
            pAttr.setBaseValue(pAttr.getBaseValue() - lossMult);
        }


        if (Config.DEATH_PVP_MESSAGE.value() != null && Config.DEATH_PVP_MESSAGE.value(String.class).length() > 0) {
            String msg = Config.DEATH_PVP_MESSAGE.value(String.class)
                    .replaceAll("%attacker%", k.getDisplayName())
                    .replaceAll("%victim%", p.getDisplayName())
                    .replaceAll("%victim-lives%", String.valueOf(pAttr.getBaseValue()))
                    .replaceAll("%attacker-lives%", String.valueOf(kAttr.getBaseValue()));
            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }

        if (Config.VICTIM_PVP_MESSAGE.value() != null && Config.VICTIM_PVP_MESSAGE.value(String.class).length() > 0 ) {
            String msg = Config.VICTIM_PVP_MESSAGE.value(String.class)
                    .replaceAll("%attacker%", k.getDisplayName())
                    .replaceAll("%victim%", p.getDisplayName())
                    .replaceAll("%victim-lives%", String.valueOf(pAttr.getBaseValue()))
                    .replaceAll("%attacker-lives%", String.valueOf(kAttr.getBaseValue()));

            p.spigot().sendMessage(Utils.colored(msg));
        }

    }

    public static void other(Player p, PlayerDeathEvent event) {

        if (!Config.OTHER_ENABLED.value(Boolean.class)) return;

        AttributeInstance pAttr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (pAttr == null ) {
            Utils.severe("Could not get attributes for victim on death");
            return;
        }
        int lossMult = 1;
        if (Config.LOSS.value(Number.class) != null && Config.LOSS.value(Number.class).intValue() != 0) lossMult = Config.LOSS.value(Number.class).intValue();
        int lowerValue = Config.LOWER.value(Number.class).intValue();
        if (Config.LOWER.value() != null && pAttr.getBaseValue() - lossMult <= lowerValue) {
            if (Config.LIVES_MESSAGE.value() != null && Config.LIVES_MESSAGE.value() != "") {
                String msg = Config.LIVES_MESSAGE.value(String.class)
                        .replaceAll("%victim%", p.getDisplayName())
                        .replaceAll("%victim-lives%", String.valueOf(pAttr.getBaseValue()));
                p.spigot().sendMessage(Utils.colored(msg));
            }
            Utils.noLives(p);
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()); // check if work
            return;
        } else {
            pAttr.setBaseValue(pAttr.getBaseValue() - lossMult);
        }
        if (Config.DEATH_OTHER_MESSAGE.value() != null && Config.DEATH_OTHER_MESSAGE.value(String.class).length() > 0) {
            String msg = Config.DEATH_OTHER_MESSAGE.value(String.class)
                    .replaceAll("%victim%", p.getDisplayName())
                    .replaceAll("%victim-lives%", String.valueOf(pAttr.getBaseValue()));
            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
        if (Config.VICTIM_OTHER_MESSAGE.value() != null && Config.VICTIM_OTHER_MESSAGE.value(String.class).length() > 0) {
            String msg = Config.VICTIM_OTHER_MESSAGE.value(String.class)
                    .replaceAll("%victim%", p.getDisplayName())
                    .replaceAll("%victim-lives%", String.valueOf(pAttr.getBaseValue()));
            p.spigot().sendMessage(Utils.colored(msg));
        }
    }

}
