package zone.vao.nexoAddon.items.mechanics;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.protectionlib.ProtectionLib;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import zone.vao.nexoAddon.NexoAddon;
import zone.vao.nexoAddon.items.Mechanics;

public record Lifesteal(int amount) {
    public static class LifestealListener implements Listener {
        @EventHandler
        public void on(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof LivingEntity attacker))
                return;
            if (!(event.getEntity() instanceof LivingEntity livingEntity))
                return;
            if (attacker instanceof Player player && !ProtectionLib.canInteract(player, event.getEntity().getLocation()))
                return;


            EntityEquipment equipment = attacker.getEquipment();
            if (equipment == null) return;
            ItemStack weapon = equipment.getItemInMainHand();
            if (weapon.isEmpty()) return;

            String nexoItemId = NexoItems.idFromItem(weapon);
            if (nexoItemId == null) return;

            Mechanics mechanics = NexoAddon.getInstance().getMechanics().get(nexoItemId);
            if (mechanics == null) return;

            Lifesteal mechanic = mechanics.getLifesteal();
            if (mechanic == null)
                return;

            AttributeInstance maxHealthAttr = attacker.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealthAttr == null) return;
            double maxHealth = maxHealthAttr.getValue();

            attacker.setHealth(Math.min(attacker.getHealth() + mechanic.amount(), maxHealth));
            livingEntity.setHealth(Math.max(livingEntity.getHealth() - mechanic.amount(), 0));
        }
    }
}
