package net.reactorfailure.platypusclient.modules.m_misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class FreeCamModule extends AbstractModule {
    private static FreeCamModule INSTANCE;

    private Vec3d cameraPos;
    private float cameraYaw;
    private float cameraPitch;

    private float speed = 0.5f;
    private float sprintMultiplier = 3.0f;

    private Vec3d originalPos;
    private float originalYaw;
    private float originalPitch;

    public FreeCamModule() {
        super("mod_freeCam", "FreeCam", "Spectator-like camera mode", ModuleCategory.MISC);
        INSTANCE = this;
    }

    public static FreeCamModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            setEnabled(false);
            return;
        }

        originalPos = client.player.getClientCameraPosVec(2);
        originalYaw = client.player.getYaw();
        originalPitch = client.player.getPitch();

        cameraPos = client.player.getEyePos();
        cameraYaw = client.player.getYaw();
        cameraPitch = client.player.getPitch();

        client.player.noClip = true;

        ClientSide.LOGGER.info("FreeCam enabled at position: {}", cameraPos);
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.player.setYaw(cameraYaw);
        client.player.setPitch(cameraPitch);

        client.player.noClip = false;

        ClientSide.LOGGER.info("FreeCam disabled");
    }

    @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null) return;

        cameraYaw = client.player.getYaw();
        cameraPitch = client.player.getPitch();

        Vec3d movement = getMovementInput(client);

        if (!movement.equals(Vec3d.ZERO)) {
            Vec3d forward = getForwardVector();
            Vec3d right = getRightVector();
            Vec3d up = new Vec3d(0, 1, 0);

            Vec3d velocity = Vec3d.ZERO;
            velocity = velocity.add(forward.multiply(movement.z));
            velocity = velocity.add(right.multiply(movement.x));
            velocity = velocity.add(up.multiply(movement.y));

            float currentSpeed = speed;
            if (client.options.sprintKey.isPressed()) {
                currentSpeed *= sprintMultiplier;
            }

            if (velocity.lengthSquared() > 0) {
                cameraPos = cameraPos.add(velocity.normalize().multiply(currentSpeed));
            }
        }

        client.player.setPosition(originalPos);
        client.player.setVelocity(Vec3d.ZERO);
        client.player.setOnGround(true);
    }

    private Vec3d getMovementInput(MinecraftClient client) {
        if (client.player == null) return Vec3d.ZERO;

        double forward = 0;
        double strafe = 0;
        double vertical = 0;

        if (client.options.forwardKey.isPressed()) forward += 1;
        if (client.options.backKey.isPressed()) forward -= 1;
        if (client.options.leftKey.isPressed()) strafe -= 1;  // Fixed: was += 1
        if (client.options.rightKey.isPressed()) strafe += 1; // Fixed: was -= 1
        if (client.options.jumpKey.isPressed()) vertical += 1;
        if (client.options.sneakKey.isPressed()) vertical -= 1;

        return new Vec3d(strafe, vertical, forward);
    }

    private Vec3d getForwardVector() {
        float yawRad = (float) Math.toRadians(cameraYaw);
        float pitchRad = (float) Math.toRadians(cameraPitch);

        return new Vec3d(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                -Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        );
    }

    private Vec3d getRightVector() {
        float yawRad = (float) Math.toRadians(cameraYaw + 90);

        return new Vec3d(
                -Math.sin(yawRad),
                0,
                Math.cos(yawRad)
        );
    }

    public Vec3d getCameraPos() {
        return cameraPos;
    }

    public float getCameraYaw() {
        return cameraYaw;
    }

    public float getCameraPitch() {
        return cameraPitch;
    }

    // Configuration
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = Math.max(0.1f, Math.min(10.0f, speed));
    }

    @Override
    public Object saveToConfig() {
        return speed;
    }

    @Override
    public void loadFromConfig(Object data) {
        if (data instanceof Number) {
            setSpeed(((Number) data).floatValue());
        }
    }
}
