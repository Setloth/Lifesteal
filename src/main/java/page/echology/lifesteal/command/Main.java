package page.echology.lifesteal.command;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import dev.triumphteam.cmd.core.flag.Flags;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import page.echology.lifesteal.Lifesteal;
import page.echology.lifesteal.config.Config;
import page.echology.lifesteal.utility.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Command("lifesteal")
public class Main extends BaseCommand {

    @Default
    public void mainCmd(CommandSender sender) {
        sender.spigot().sendMessage(Utils.component("&e&lLifesteal Command\n\n&6/lifesteal lives (player) &7- check someone's lives\n&6/lifesteal lives (player) -a (number) &7- add to someone's lives\n&6/lifesteal lives (player) -s (number) &7- set someone's lives\n&6/lifesteal withdraw (number) &7- withdraw lives as items\n&6/lifesteal config &7- configure the plugin crafting\n").create());
    }

    @SubCommand("withdraw")
    public void withdraw(CommandSender sender, int amount) {
        if (!(sender instanceof Player player)) return;

        boolean cen = Config.CRAFTING_ENABLED.value(Boolean.class);
        if (!cen) {
            sender.spigot().sendMessage(Utils.component("&cCrafting is not enabled. Enable the crafting section in the config.yml to be able to use the withdraw feature.").create());
            return;
        }

        int craftlives = Config.CRAFTING_LIVES.value(Number.class).intValue();
        AtomicInteger remove = new AtomicInteger(craftlives * amount);

        if (remove.get() < 1) {
            sender.spigot().sendMessage(Utils.component("&cYou must supply a number that is more than &60").create());
            return;
        }
        AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (health == null) {
            sender.spigot().sendMessage(Utils.component("&cFailed to get your maximum health, this is most likely not your fault.").create());

            Utils.severe("GENERIC_MAX_HEALTH found on "+player.getName()+" is null");

            return;
        }

        if (remove.get() > health.getBaseValue()) {
            sender.spigot().sendMessage(Utils.component("&cThe amount of lives required &6(",String.valueOf(remove.get()),")&c exceeds your current max health!").create());
            return;
        }
        ItemStack item = Lifesteal.item();
        item.setAmount(amount);
        final Map<Integer, ItemStack> map = player.getInventory().addItem(item);

        if (!map.isEmpty())
            map.forEach((k, v) -> remove.addAndGet(-(v.getAmount() * craftlives)));
        health.setBaseValue(health.getBaseValue() - remove.get());

        if (remove.get() == 1) sender.spigot().sendMessage(Utils.component("&61&a life has been withdrawn as an item").create());
        if (remove.get() != 1) sender.spigot().sendMessage(Utils.component("&6",String.valueOf(remove.get()),"&a lives have been withdrawn as items").create());
    }

    @SubCommand("version")
    public void version(CommandSender sender) {
        String serverVersion = Bukkit.getServer().getVersion(),
                pluginVersion = Lifesteal.instance().getDescription().getVersion();
        sender.spigot().sendMessage(
                Utils.component("\n\n&6Debug Information",
                        "\n\n&eServer Version: ",
                        "&a"+serverVersion,
                        "\n&ePlugin Version: ",
                        (Lifesteal.outdated() ? "&c" : "&a")+pluginVersion,
                        "\n&ePlugin Link: "
                ).append(Utils.hyperLink("&bhere", "https://x.echology.page/lifesteal")).append("\n\n").create()
        );
    }

    @SubCommand("reload")
    @Permission({"lifesteal.command.reload"})
    public void reload(CommandSender sender) {

        sender.spigot().sendMessage(Utils.colored("&6Reloading configuration..."));
        Utils.reload(Lifesteal.instance(), (Exception error, Long time) -> {
            if (error != null) {
                error.printStackTrace();
                sender.spigot().sendMessage(Utils.colored("&cThere was an issue reloading the config, you may have to delete the config.yml file and restart the server."));
                return;
            }
            sender.spigot().sendMessage(Utils.component("&aReload complete! - ",String.valueOf(time.doubleValue()/1000)," seconds").create());
        });
    }

    @SubCommand("lives")
    @Permission({"lifesteal.command.lives"})
    @CommandFlags({@Flag(flag="s", longFlag="set", argument=int.class), @Flag(flag="a", longFlag="add", argument=int.class)})
    public void lives(CommandSender sender, Player target, Flags flags) {


        AttributeInstance attrb = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attrb == null) {
            sender.spigot().sendMessage(Utils.component("&cFailed to get the maximum health of the target player, this is most likely not your fault.").create());

            Utils.severe("GENERIC_MAX_HEALTH found on "+target.getName()+" is null");

            return;
        }

        if (flags.getValue("s").isEmpty() && flags.getValue("a").isEmpty()) {
            sender.spigot().sendMessage(Utils.component("&6"+target.getDisplayName(), " &6has ", "&a"+attrb.getBaseValue()," &6live"+(attrb.getBaseValue() == 1 ? "" : "s")).create());
        }

        else {

            flags.getValue("s", int.class).ifPresent(attrb::setBaseValue);

            flags.getValue("a", int.class).ifPresent(a -> attrb.setBaseValue(attrb.getBaseValue()+a));


            if (Config.LOWER.value() != null && (attrb.getBaseValue() < Config.LOWER.value(Number.class).doubleValue())) {
                Utils.noLives(target);
                attrb.setBaseValue(Config.LOWER.value(Number.class).floatValue());
            } else if (Config.UPPER.value() != null && attrb.getBaseValue() > Config.UPPER.value(Number.class).doubleValue()) {
                attrb.setBaseValue(Config.UPPER.value(Number.class).floatValue());
            }

            sender.spigot().sendMessage(Utils.component("&6"+target.getDisplayName(), " &6has had their lives updated to ", "&a"+attrb.getBaseValue()).create());

        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @SubCommand("config")
    @Permission({"lifesteal.command.config"})
    public void config(CommandSender sender) {
        if (!(sender instanceof Player player)) return;



        Gui gui = Gui.gui(GuiType.WORKBENCH).title(Component.text("Configure Crafting")).create();
        gui.enableAllInteractions();

        gui.open(player);

        gui.setCloseGuiAction(event -> {
            List<ItemStack> items = new java.util.ArrayList<>((Arrays.stream(event.getInventory().getContents()).toList()));

            ItemStack result = items.remove(0);
            List<ItemStack> cleansed = new ArrayList<>();
            HashMap<String, ItemStack> toShape = new HashMap<>();
            StringBuilder shape = new StringBuilder(9);

            for (ItemStack item : items) {
                if (item == null) cleansed.add(new ItemStack(Material.AIR));
                else cleansed.add(item);

            }

            for (ItemStack item : cleansed) {
                if (!toShape.containsValue(item) && !item.getType().isAir())
                    toShape.put((toShape.size() + 1) + "", item);

                String k = getKeyByValue(toShape, item);

                shape.append(k == null ? " " : k);

            }



            List<ItemStack> addBack = new ArrayList<>(cleansed.stream().toList());
            addBack.add(result== null ? new ItemStack(Material.AIR) : result);

            player.getInventory().addItem(addBack.toArray(new ItemStack[0]));


            if (result == null) return;

            FileConfiguration config = Lifesteal.instance().getConfig();

            config.set(Config.CRAFTING_ITEM.key, result);
            String ingredientsK = Config.CRAFTING_INGREDIENTS.key;

            config.set(ingredientsK, toShape);

            List<String> formedShape = new ArrayList<>();
            int i = 0;
            while (i < shape.length()) {
                formedShape.add(shape.substring(i, Math.min(i+3, shape.length())));
                i+=3;
            }

            config.set(Config.CRAFTING_SHAPE.key, formedShape);
            config.set(Config.CRAFTING_ENABLED.key, true);

            Lifesteal.instance().saveConfig();

            sender.spigot().sendMessage(Utils.component("&eConfig has been updated, reload the config with &a/lifesteal reload&e to update!").create());


        });
    }


}
