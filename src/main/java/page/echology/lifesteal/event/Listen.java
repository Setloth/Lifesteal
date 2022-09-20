package page.echology.lifesteal.event;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import page.echology.lifesteal.Lifesteal;
import page.echology.lifesteal.config.Config;
import page.echology.lifesteal.utility.DeathHandle;
import page.echology.lifesteal.utility.Utils;

public class Listen implements Listener {

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player p = event.getEntity();
        Player k = p.getKiller();

        if (k != null) {
            DeathHandle.player(p, k, event);
        }
        else {
            DeathHandle.other(p, event);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if (!event.getPlayer().isOp()) return;
        if (Lifesteal.outdated()) Lifesteal.warnOutdated(event.getPlayer());
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        ItemStack item = Lifesteal.item();
        if (item == null) return;
        ItemStack eventitem = event.getItem();
        if (eventitem == null) return;
        if (!item.isSimilar(eventitem)) return;

        event.setCancelled(true);

        AttributeInstance attribute = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attribute == null) {
            Utils.severe("Could not get attributes for player on interact. aborting...");
            return;
        }

        double craftlives = Config.CRAFTING_LIVES.value(Number.class).doubleValue();
        double upperValue = Config.UPPER.value(Number.class).doubleValue();

        if (attribute.getBaseValue() + craftlives <= (Config.UPPER.value() == null ? Float.POSITIVE_INFINITY : upperValue)) {
            attribute.setBaseValue(attribute.getBaseValue()+craftlives);
            if (eventitem.getAmount() == 1) {
                player.getInventory().remove(eventitem);
            }
            else {
                eventitem.setAmount(eventitem.getAmount()-1);
            }
        }

    }

}
