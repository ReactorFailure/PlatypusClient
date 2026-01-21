package net.reactorfailure.platypusclient;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.*;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;


public class DiscordRPCManager {
    private static DiscordRPCManager INSTANCE;

    private static final long APPID = 1461393597180940532L;

    private IPCClient client;
    private boolean connected = false;
    private long startTime;
    private int tickCounter = 0;
    private static final int UPDATE_INTERVAL = 40; // ~2 seconds

    private PresenceState lastState = null;
    private String lastDimensionId = null;

    private enum PresenceState {
        MENU,
        SINGLEPLAYER,
        MULTIPLAYER
    }

    public static DiscordRPCManager get() {
        if (INSTANCE == null) {
            INSTANCE = new DiscordRPCManager();
        }
        return INSTANCE;
    }


    public void connect() {
        if (connected) return;

        try {
            client = new IPCClient(APPID);
            client.setListener(new IPCListener() {

                @Override
                public void onPacketSent(IPCClient client, Packet packet) {

                }

                @Override
                public void onPacketReceived(IPCClient client, Packet packet) {

                }

                @Override
                public void onActivityJoin(IPCClient client, String secret) {

                }

                @Override
                public void onActivitySpectate(IPCClient client, String secret) {

                }

                @Override
                public void onActivityJoinRequest(IPCClient client, String secret, User user) {

                }

                @Override
                public void onReady(IPCClient client) {
                    ClientSide.LOGGER.info("Discord RPC Ready");
                    connected = true;
                    startTime = System.currentTimeMillis() / 1000;
                    updatePresence(true);
                }

                @Override
                public void onClose(IPCClient client, JsonObject json) {

                }

                @Override
                public void onDisconnect(IPCClient client, Throwable t) {
                    ClientSide.LOGGER.warn("Discord RPC disconnected");
                    connected = false;
                }
            });

            client.connect();

        } catch (NoDiscordClientException e) {
            ClientSide.LOGGER.warn("Discord not running, RPC disabled");
        } catch (Exception e) {
            ClientSide.LOGGER.error("Failed to init Discord RPC", e);
        }
    }


    public void tick() {
        if (!connected) return;

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc != null && mc.world != null) {
            String currentDim = mc.world.getRegistryKey().getValue().toString();

            if (!currentDim.equals(lastDimensionId)) {
                lastDimensionId = currentDim;
                updatePresence();
            }
        }

        tickCounter++;
        if (tickCounter >= UPDATE_INTERVAL) {
            tickCounter = 0;
            updatePresence();
        }
    }


    private void updatePresence(boolean force) {
        if (!connected || client == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        PresenceState newState;
        RichPresence.Builder builder = new RichPresence.Builder()
                .setActivityType(ActivityType.Playing)
                .setStartTimestamp(startTime)
                .setStatusDisplayType(StatusDisplayType.Name);

        if (mc == null || mc.world == null) {
            lastDimensionId = null;
            newState = PresenceState.MENU;
            builder.setDetails("In Main Menu")
                    .setState("Doing something");

        } else if (mc.getCurrentServerEntry() != null) {
            newState = PresenceState.MULTIPLAYER;
            ServerInfo server = mc.getCurrentServerEntry();
            builder.setDetails("Playing Multiplayer")
                    .setState(server.name);

        } else {
            newState = PresenceState.SINGLEPLAYER;
            builder.setDetails("Playing Singleplayer")
                    .setState(getDimensionName(mc));
        }

        builder.setLargeImage(getBigImageKey(newState, mc));

        if (force || newState != lastState) {
            lastState = newState;
            client.sendRichPresence(builder.build());
            ClientSide.LOGGER.info("RPC updated at {}", newState);
        }
    }

    public void updatePresence() {
        updatePresence(true);
    }

    private String getDimensionName(MinecraftClient mc) {
        if (mc.world == null) return "Unknown (wtf?)";

        return switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "overworld" -> "Overworld";
            case "the_nether" -> "The Nether";
            case "the_end" -> "The End";
            default -> "Custom Dimension (where is bro)";
        };
    }

    private String getBigImageKey(PresenceState state, MinecraftClient mc) {
        return switch (state) {
            case MENU -> "menu";
            case MULTIPLAYER -> "multiplayer";
            case SINGLEPLAYER -> {
                if (mc != null && mc.world != null) {
                    yield switch (mc.world.getRegistryKey().getValue().getPath()) {
                        case "overworld" -> "overworld";
                        case "the_nether" -> "nether";
                        case "the_end" -> "end";
                        default -> "fallback";
                    };
                }
                yield "fallback";
            }
        };
    }

    public void disconnect() {
        if (client != null) {
            try {
                client.sendRichPresence(null);
                client.close();
            } catch (Exception ignored) {}
        }
        connected = false;
        client = null;
    }
}
