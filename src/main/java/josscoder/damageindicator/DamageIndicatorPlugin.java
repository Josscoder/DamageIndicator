package josscoder.damageindicator;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.UUID;

public class DamageIndicatorPlugin extends PluginBase implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || event.getFinalDamage() <= 0) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        Player player = (Player) damager;

        long eid = Entity.entityCount++;

        AddPlayerPacket addPlayerPacket = new AddPlayerPacket();
        addPlayerPacket.entityUniqueId = eid;
        addPlayerPacket.entityRuntimeId = eid;
        addPlayerPacket.x = (float) entity.getX();
        addPlayerPacket.y = (float) (entity.getY() + 1);
        addPlayerPacket.z = (float) entity.getZ();
        addPlayerPacket.item = new BlockAir().toItem();
        addPlayerPacket.uuid = UUID.randomUUID();
        addPlayerPacket.username = TextFormat.RED + "-" + (int) event.getFinalDamage();
        addPlayerPacket.metadata = (new EntityMetadata())
                .putFloat(Entity.DATA_SCALE, 0.01f);
        player.dataPacket(addPlayerPacket);

        getServer().getScheduler().scheduleDelayedTask(this, () -> {
            RemoveEntityPacket removeEntityPacket = new RemoveEntityPacket();
            removeEntityPacket.eid = eid;
            player.dataPacket(removeEntityPacket);
        }, 20);
    }
}