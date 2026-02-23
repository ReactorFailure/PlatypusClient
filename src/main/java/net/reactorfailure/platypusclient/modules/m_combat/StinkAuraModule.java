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


public class StinkAuraModule extends AbstractModule {
    private static StinkAuraModule INSTANCE;

    public StinkAuraModule() {
        super("mod_stinkAura", "Stink Aura", "Makes a force field around you", ModuleCategory.COMBAT);
        INSTANCE = this;
    }

    public static StinkAuraModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Stink Aura enabled - Range: {} blocks", attackRange);
        tickCounter = 0;
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Stink Aura disabled");
    }

    // Attack settings
    private double attackRange = 4.5; // Range in blocks
    private int attackDelay = 10; // Ticks between attacks (10 = 0.5 seconds)
    private int tickCounter = 0;

    // Target filters
    //TODO: put checkbox for these stuff
    private boolean targetPlayers = true;
    private boolean targetMobs = true;
    private boolean targetAnimals = true;

    @Override
    public void tick() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!isEnabled() || client.player == null || client.world == null) return;
            if (client.interactionManager == null) return;

            tickCounter++;

            // Only attack when delay has passed
            if (tickCounter >= attackDelay) {
                tickCounter = 0;

                // Find nearest valid target
                Entity target = findNearestTarget(client);

                if (target != null) {
                    // Look at the target
                    lookAtEntity(client, target);

                    // Attack the target
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

        Entity closestTarget = null;
        double closestDistance = attackRange;

        // Iterate through all entities in the world
        for (Entity entity : client.world.getEntities()) {
            // Skip if not a living entity
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            // Skip self
            if (entity == client.player) continue;

            // Skip dead entities
            if (livingEntity.isDead()) continue;

            // Skip removed entities
            if (entity.isRemoved()) continue;

            // Calculate distance
            double distance = client.player.distanceTo(entity);

            // Skip if out of range
            if (distance > attackRange) continue;

            // Check if valid target
            if (!isValidTarget(entity)) continue;

            // Check if closer than current closest
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = entity;
            }
        }

        return closestTarget;
    }

    private boolean isValidTarget(Entity entity) {
        // Check if entity matches our target filters
        if (entity instanceof PlayerEntity && !targetPlayers) {
            return false;
        }

        if (entity instanceof Monster && targetMobs) {
            return true;
        }

        if (entity instanceof PassiveEntity && targetAnimals) {
            return true;
        }

        // If targeting players, return true for players
        return entity instanceof PlayerEntity && targetPlayers;
    }

    private void lookAtEntity(MinecraftClient client, Entity target) {
        if (client.player == null) return;

        // Calculate the direction to the target
        double deltaX = target.getX() - client.player.getX();
        double deltaY = target.getY() + target.getEyeHeight(target.getPose())
                - (client.player.getY() + client.player.getEyeHeight(client.player.getPose()));
        double deltaZ = target.getZ() - client.player.getZ();

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // Calculate yaw and pitch
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(deltaY, distance) * 180.0 / Math.PI);

        // Set player rotation
        client.player.setYaw(yaw);
        client.player.setPitch(pitch);
    }

    // Getters and setters for configuration
    public double getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(double range) {
        this.attackRange = Math.max(1.0, Math.min(6.0, range));
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int delay) {
        this.attackDelay = Math.max(1, Math.min(40, delay));
    }

    public boolean isTargetingPlayers() {
        return targetPlayers;
    }

    public void setTargetPlayers(boolean target) {
        this.targetPlayers = target;
        ClientSide.LOGGER.info("Stink Aura: Target players = {}", target);
    }

    public boolean isTargetingMobs() {
        return targetMobs;
    }

    public void setTargetMobs(boolean target) {
        this.targetMobs = target;
        ClientSide.LOGGER.info("Stink Aura: Target mobs = {}", target);
    }

    public boolean isTargetingAnimals() {
        return targetAnimals;
    }

    public void setTargetAnimals(boolean target) {
        this.targetAnimals = target;
        ClientSide.LOGGER.info("Stink Aura: Target animals = {}", target);
    }
}
