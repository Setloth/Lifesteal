package page.echology.lifesteal;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import page.echology.lifesteal.bstats.Metrics;
import page.echology.lifesteal.command.Main;
import page.echology.lifesteal.config.Config;
import page.echology.lifesteal.event.Listen;
import page.echology.lifesteal.utility.Utils;

import java.util.List;
import java.util.Objects;


public final class Lifesteal extends JavaPlugin {

    static Lifesteal INSTANCE;

    static Metrics METRICS;

    static boolean OUTDATED = false, ONLINE;

    static ItemStack ITEM;

    static long START_TIME;

    static volatile String SERVER_ID, SERVER_NAME, VERSION, CURRENT;

    static Server SERVER;

    static API api;

    static ShapedRecipe recipe;

    @Override
    public void onEnable() {
        try {
            START_TIME = System.currentTimeMillis();

            INSTANCE = this;

            VERSION = this.getDescription().getVersion();

            api = new API(this);

            api.version(v -> {
                CURRENT = v;
                if (!Objects.equals(VERSION, v)) {
                    OUTDATED = true;
                    warnOutdated();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        warnOutdated(p);
                    }
                }

            });

            int id = 12750;

            METRICS = new Metrics(this, id);

            saveDefaultConfig();

            getServer().getPluginManager().registerEvents(new Listen(), this);

            BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);

            manager.registerCommand(
                    new Main()
            );


            try {
                updateRecipe();
            } catch (Exception e) {
                Utils.severe("There was an error while trying to update the recipe.");
                e.printStackTrace();
            }

            Utils.log("Lifesteal has been loaded :)");

        } catch(Exception e) {
            Utils.severe("There seems to have been an error. Please ask for help by providing an image of this console output in the #support channel on my Discord server: https://www.echology.page/socials/discord");
            e.printStackTrace();
        }

    }

    public static Lifesteal instance() {
        return INSTANCE;
    }

    public static boolean outdated() {
        return OUTDATED;
    }

    public static ItemStack item() {
        return ITEM;
    }

    public static void warnOutdated(CommandSender sender) {
        sender.spigot().sendMessage(Utils.colored("\n\n&6&l[!] &r&c&oLifesteal is outdated! Please update it via the Minehut dashboard. If you do not update, it may stop working, and you will not receive the latest improvements. \n&7Current installed version: &e&l"+Lifesteal.VERSION +"&r&7\nLatest released version: &e&l"+Lifesteal.CURRENT+"\n\n"));
    }
    public static void warnOutdated() {
        warnOutdated(Bukkit.getConsoleSender());
    }

    public static ShapedRecipe recipe() {
        return recipe;
    }

    public void updateRecipe() {
        Bukkit.getServer().removeRecipe(new NamespacedKey(this, "heart"));

        boolean cen = Config.CRAFTING_ENABLED.value(Boolean.class);
        if (!cen) {
            return;
        }

        ConfigurationSection CRAFTING = Config.CRAFTING.section();

        Utils.log("Loading recipe...");

        ITEM = CRAFTING.getItemStack("item");

        if (Config.CRAFTING_VERSION.value(Boolean.class)) {
            getConfig().set("Crafting.item", ITEM);
            saveConfig();
        }


        if (ITEM == null) {
            Utils.severe("Result item for crafting is not correct, aborting recipe registration...");
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "heart"), ITEM);

        List<String> shape = CRAFTING.getStringList("shape");

        recipe.shape(shape.get(0), shape.get(1), shape.get(2));

        ConfigurationSection section = CRAFTING.getConfigurationSection("ingredients");
        if (section == null) {
            Utils.severe("Ingredients section is not correct, aborting recipe registration...");
            return;
        }

        for (String key : section.getKeys(false)) {
            ItemStack i = section.getItemStack(key);
            if (i == null) continue;
            RecipeChoice rc = new RecipeChoice.ExactChoice(i);
            if (Config.CRAFTING_VERSION.value(Boolean.class)) {
                section.set(key, i);
                saveConfig();
                i = section.getItemStack(key);
                if (i == null) continue;
                rc = new RecipeChoice.ExactChoice(i);
            }
            recipe.setIngredient(key.charAt(0), rc);
        }
        recipe.setGroup("lifesteal");

        Lifesteal.recipe = recipe;
        Bukkit.addRecipe(recipe);

        Utils.log("Recipe has been loaded!"); // \nResulting item: "+ITEM.toString()+"\nUsing: "+Arrays.toString(recipe.getIngredientMap().values().stream().map(item -> item.getType().name()).toArray())+"\nShape: "+ Arrays.toString(recipe.getShape()));
    }
}
