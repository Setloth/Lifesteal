package page.echology.lifesteal.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import page.echology.lifesteal.Lifesteal;
import page.echology.lifesteal.utility.Utils;

public enum Config {
    /**
     * Configuration values defined from the config.yml that are accessed via Enum here
     */
    PVP_ENABLED("Kills.pvp.enabled"),
    OTHER_ENABLED("Kills.other.enabled"),
    ATTACKER_PVP_MESSAGE("Kills.pvp.attacker"),
    CRAFTING("Crafting"),
    CRAFTING_ENABLED("Crafting.enabled"),
    CRAFTING_LIVES("Crafting.lives"),
    DEATH_PVP_MESSAGE("Kills.pvp.death-message"),
    GAIN("Health.multiplier.gain"),
    LIVES_COMMAND("Out-Of-Lives.command"),
    LIVES_FORCE_SPECTATOR("Out-Of-Lives.force-spectator"),
    LIVES_MESSAGE_ENABLED("Out-Of-Lives.message.enabled"),
    LIVES_MESSAGE("Out-Of-Lives.message"),
    LOSS_ENABLED("Health.multiplier.loss.enabled"),
    LOSS("Health.multiplier.loss"),
    LOWER("Health.lower-limit"),
    UPPER("Health.upper-limit"),
    VICTIM_PVP_MESSAGE("Kills.pvp.victim"),
    VICTIM_OTHER_MESSAGE("Kills.other.victim"),
    CRAFTING_VERSION("Crafting.auto-version"),
    CRAFTING_SHAPE("Crafting.shape"),
    CRAFTING_ITEM("Crafting.item"),
    CRAFTING_INGREDIENTS("Crafting.ingredients"),
    DEATH_OTHER_MESSAGE("Kills.other.death-message");

    public final String key;

    public final Lifesteal plugin;

    Config(String path) {
        key = path;
        plugin = Lifesteal.instance();
    }

    public <T> T value(@NotNull Class<T> clazz) {
        T o = plugin.getConfig().getObject(key, clazz);
        if (o == null) {
            Utils.warn("Missing configuration found, section '" + key + "' is missing.");
        }
        return o;
    }

    public Object value() {
        return value(Object.class);
    }

    public ConfigurationSection section() {
        ConfigurationSection o = plugin.getConfig().getConfigurationSection(key);
        if (o == null) {
            Utils.severe("Missing configuration found, section '" + key + "' is missing.");
        }
        return o;
    }
}
