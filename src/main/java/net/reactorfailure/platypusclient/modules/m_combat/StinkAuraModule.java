package net.reactorfailure.platypusclient.modules.m_combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_utils.options.BooleanOption;


public class StinkAuraModule extends AbstractModule {
    private static StinkAuraModule INSTANCE;

    private double attackRange = 4.5;
    private int    attackDelay = 10;
    private int    tickCounter = 0;

    private final BooleanOption optTargetPlayers =
            new BooleanOption("targetPlayers", "Target Players", true);

    private final BooleanOption optTargetMobs =
            new BooleanOption("targetMobs", "Target Mobs", true);

    private final BooleanOption optTargetAnimals =
            new BooleanOption("targetAnimals", "Target Animals", true);


    public StinkAuraModule() {
        super("mod_sa", "Stink Aura", "Makes a force field around you", ModuleCategory.COMBAT);
        INSTANCE = this;

        // Register options so the dashboard and config system pick them up
        addOption(optTargetPlayers);
        addOption(optTargetMobs);
        addOption(optTargetAnimals);
    }

    public static StinkAuraModule get() { return INSTANCE; }


    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Stink Aura enabled - Range: {} blocks", attackRange);
        tickCounter = 0;
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Stink Aura disabled");
    }

    @Override
    public void tick() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!isEnabled() || client.player == null || client.world == null) return;
            if (client.interactionManager == null) return;

            tickCounter++;

            if (tickCounter >= attackDelay) {
                tickCounter = 0;

                Entity target = findNearestTarget(client);
                if (target != null) {
                    lookAtEntity(client, target);
                    client.interactionManager.attackEntity(client.player, target);
                    client.player.swingHand(Hand.MAIN_HAND);
                    ClientSide.LOGGER.debug("Stink Aura attacked: {}", target.getName().getString());
                }
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error in Stink Aura tick: ", e);
            setEnabled(false);
        }
    }

    private Entity findNearestTarget(MinecraftClient client) {
        if (client.world == null || client.player == null) return null;

        Entity closest  = null;
        double  closestDist = attackRange;

        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (entity == client.player) continue;
            if (living.isDead() || entity.isRemoved()) continue;

            double dist = client.player.distanceTo(entity);
            if (dist > attackRange) continue;
            if (!isValidTarget(entity)) continue;

            if (dist < closestDist) {
                closestDist = dist;
                closest     = entity;
            }
        }

        return closest;
    }

    private boolean isValidTarget(Entity entity) {
        if (entity instanceof Monster)       return optTargetMobs.getValue();
        if (entity instanceof PassiveEntity) return optTargetAnimals.getValue();
        if (entity instanceof PlayerEntity)  return optTargetPlayers.getValue();
        return false;
    }

    private void lookAtEntity(MinecraftClient client, Entity target) {
        if (client.player == null) return;

        double dx = target.getX() - client.player.getX();
        double dy = target.getY() + target.getEyeHeight(target.getPose())
                - (client.player.getY() + client.player.getEyeHeight(client.player.getPose()));
        double dz = target.getZ() - client.player.getZ();

        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        float yaw   = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(dy, horizontalDist) * 180.0 / Math.PI);

        client.player.setYaw(yaw);
        client.player.setPitch(pitch);
    }

    @Override
    public Object saveToConfig() {
        // attackRange and attackDelay are not options yet, save manually
        return new double[]{ attackRange, attackDelay };
    }

    @Override
    public void loadFromConfig(Object data) {
        if (data instanceof java.util.List<?> list && list.size() >= 2) {
            setAttackRange(((Number) list.get(0)).doubleValue());
            setAttackDelay(((Number) list.get(1)).intValue());
        }
    }

    public double getAttackRange()           { return attackRange; }
    public void   setAttackRange(double r)   { attackRange = Math.max(1.0, Math.min(6.0, r)); }

    public int  getAttackDelay()             { return attackDelay; }
    public void setAttackDelay(int d)        { attackDelay = Math.max(1, Math.min(40, d)); }

    public boolean isTargetingPlayers()              { return optTargetPlayers.getValue(); }
    public void    setTargetPlayers(boolean v)        { optTargetPlayers.setValue(v); }

    public boolean isTargetingMobs()                 { return optTargetMobs.getValue(); }
    public void    setTargetMobs(boolean v)           { optTargetMobs.setValue(v); }

    public boolean isTargetingAnimals()              { return optTargetAnimals.getValue(); }
    public void    setTargetAnimals(boolean v)        { optTargetAnimals.setValue(v); }
}
