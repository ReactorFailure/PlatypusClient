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
import net.reactorfailure.platypusclient.modules.L_utils.BooleanOption;
import net.reactorfailure.platypusclient.modules.L_utils.SliderOption;


public class StinkAuraModule extends AbstractModule {
    private static StinkAuraModule INSTANCE;

    private double attackRange = 4.5;
    private int attackDelay = 10;
    private int tickCounter = 0;

    private final SliderOption optRange = new SliderOption(
            "attackRange", "Attack Range",
            1.0, 6.0, 4.5,
            0.5,
            "Close", "Far",
            v -> this.attackRange = v
    );

    private final SliderOption optDelay = new SliderOption(
            "attackDelay", "Attack Speed",
            2, 20, 10,
            1,
            "Very Fast", "Normal",
            v -> this.attackDelay = v.intValue()
    );

    private final BooleanOption optTargetMobs    = new BooleanOption("targetMobs", "Target Mobs", true);
    private final BooleanOption optTargetAnimals = new BooleanOption("targetAnimals", "Target Animals", true);
    private final BooleanOption optTargetPlayers = new BooleanOption("targetPlayers", "Target Players", true);


    public StinkAuraModule() {
        super("mod_stinkAura", "Stink Aura", "Makes a force field around you", ModuleCategory.COMBAT);
        INSTANCE = this;

        addOption(optRange);
        addOption(optDelay);
        addOption(optTargetMobs);
        addOption(optTargetAnimals);
        addOption(optTargetPlayers);
    }

    public static StinkAuraModule get() { return INSTANCE; }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Stink Aura enabled – range: {} blocks, delay: {} ticks", attackRange, attackDelay);
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
                }
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error in Stink Aura tick: ", e);
            setEnabled(false);
        }
    }

    private Entity findNearestTarget(MinecraftClient client) {
        Entity closest = null;
        double closestDist = attackRange;

        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (entity == client.player) continue;
            if (living.isDead() || entity.isRemoved()) continue;

            double dist = client.player.distanceTo(entity);
            if (dist > attackRange || !isValidTarget(entity)) continue;

            if (dist < closestDist) {
                closestDist = dist;
                closest = entity;
            }
        }
        return closest;
    }

    private boolean isValidTarget(Entity entity) {
        if (entity instanceof Monster) return optTargetMobs.getValue();
        if (entity instanceof PassiveEntity) return optTargetAnimals.getValue();
        if (entity instanceof PlayerEntity) return optTargetPlayers.getValue();

        return false;
    }

    private void lookAtEntity(MinecraftClient client, Entity target) {
        double dx = target.getX() - client.player.getX();
        double dy = target.getY() + target.getEyeHeight(target.getPose()) - (client.player.getY() + client.player.getEyeHeight(client.player.getPose()));
        double dz = target.getZ() - client.player.getZ();
        double hDist = Math.sqrt(dx * dx + dz * dz);

        client.player.setYaw((float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f);
        client.player.setPitch((float)-(Math.atan2(dy, hDist) * 180.0 / Math.PI));
    }

    // Setters and Getters lol
    public double getAttackRange() { return attackRange; }
    public void setAttackRange(double r) { optRange.setValue(r); }
    public int  getAttackDelay() { return attackDelay; }
    public void setAttackDelay(int d) { optDelay.setValue((double) d); }
    public boolean isTargetingMobs() { return optTargetMobs.getValue(); }
    public void setTargetMobs(boolean v) { optTargetMobs.setValue(v); }
    public boolean isTargetingAnimals() { return optTargetAnimals.getValue(); }
    public void setTargetAnimals(boolean v) { optTargetAnimals.setValue(v); }
    public boolean isTargetingPlayers() { return optTargetPlayers.getValue(); }
    public void setTargetPlayers(boolean v) { optTargetPlayers.setValue(v); }
}
