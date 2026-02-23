package net.reactorfailure.platypusclient.modules.m_combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

import java.util.List;

public class AutoEquipTotemModule extends AbstractModule {
    private static AutoEquipTotemModule INSTANCE;

    private float healthThreshold = 8.0f;
    private int checkDelay = 2;
    private int tickCounter = 0;
    private boolean lastCheckFoundTotem = true;
    private boolean totemEquipped = false;

    public AutoEquipTotemModule() {
        super("mod_autoTotem", "Auto Equip Totem", "Automatically equips a totem to your off-hand when low health", ModuleCategory.COMBAT);
        INSTANCE = this;
    }

    public static AutoEquipTotemModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Auto Totem enabled - Health threshold: {} HP ({} hearts)",
                healthThreshold, healthThreshold / 2);
        tickCounter = 0;
        lastCheckFoundTotem = true;
        totemEquipped = false;
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Auto Totem disabled");
        totemEquipped = false;
    }

    @Override
    public void tick() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!isEnabled() || client.player == null) return;
            if (client.interactionManager == null) return;

            tickCounter++;

            if (tickCounter >= checkDelay) {
                tickCounter = 0;
                checkHealthAndEquipTotem(client);
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error in Auto Totem tick: ", e);
            setEnabled(false);
        }
    }

    private void checkHealthAndEquipTotem(MinecraftClient client) {
        PlayerInventory inventory = client.player.getInventory();
        ItemStack offhandStack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);

        float currentHealth = client.player.getHealth();
        boolean isDangerous = currentHealth <= healthThreshold;

        if (isDangerous) {
            if (!offhandStack.isOf(Items.TOTEM_OF_UNDYING)) {
                int totemSlot = findTotemSlot(inventory);

                if (totemSlot == -1) {
                    if (lastCheckFoundTotem) {
                        showNoTotemToast(client);
                        lastCheckFoundTotem = false;
                        ClientSide.LOGGER.warn("No totems found in inventory! Health: {} HP", currentHealth);
                    }
                    return;
                }

                lastCheckFoundTotem = true;
                equipTotem(client, totemSlot);
                totemEquipped = true;
                ClientSide.LOGGER.info("Health critical ({} HP)! Equipped totem to offhand", currentHealth);
            }
        } else {
            if (totemEquipped) {
                totemEquipped = false;
                ClientSide.LOGGER.info("Health restored to safe levels ({} HP)", currentHealth);
            }
        }
    }

    private int findTotemSlot(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            if (inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }

        for (int i = 9; i < 36; i++) {
            if (inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }

        return -1; // No totem found
    }

    private void equipTotem(MinecraftClient client, int slot) {
        try {
            int screenSlot = slot < 9 ? slot + 36 : slot;

            client.interactionManager.clickSlot(
                    client.player.playerScreenHandler.syncId,
                    screenSlot,
                    0,
                    SlotActionType.PICKUP,
                    client.player
            );

            client.interactionManager.clickSlot(
                    client.player.playerScreenHandler.syncId,
                    45,
                    0,
                    SlotActionType.PICKUP,
                    client.player
            );

            client.interactionManager.clickSlot(
                    client.player.playerScreenHandler.syncId,
                    screenSlot,
                    0,
                    SlotActionType.PICKUP,
                    client.player
            );

            ClientSide.LOGGER.info("Equipped totem from slot {}", slot);
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error equipping totem: ", e);
        }
    }

    private void showNoTotemToast(MinecraftClient client) {
        client.getToastManager().add(
                SystemToast.create(
                        client,
                        SystemToast.Type.NARRATOR_TOGGLE,
                        Text.literal("Auto Totem"),
                        Text.literal("No totems found! Low health!")
                )
        );
    }

    public float getHealthThreshold() {
        return healthThreshold;
    }

    public void setHealthThreshold(float threshold) {
        this.healthThreshold = Math.max(1.0f, Math.min(20.0f, threshold));
        ClientSide.LOGGER.info("Auto Totem health threshold set to {} HP ({} hearts)",
                healthThreshold, healthThreshold / 2);
    }

    public int getCheckDelay() {
        return checkDelay;
    }

    public void setCheckDelay(int delay) {
        this.checkDelay = Math.max(1, Math.min(20, delay));
    }

    @Override
    public Object saveToConfig() {
        // Save both health threshold and check delay
        return new float[] { healthThreshold, checkDelay };
    }

    @Override
    public void loadFromConfig(Object data) {
        if (data instanceof List<?> list) {
            if (list.size() >= 2) {
                if (list.get(0) instanceof Number) {
                    setHealthThreshold(((Number) list.getFirst()).floatValue());
                }
                if (list.get(1) instanceof Number) {
                    setCheckDelay(((Number) list.get(1)).intValue());
                }
            }
        }
    }
}
