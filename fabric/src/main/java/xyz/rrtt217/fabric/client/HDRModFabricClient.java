package xyz.rrtt217.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import xyz.rrtt217.GLFWColorManagement;

import static xyz.rrtt217.HDRMod.LOGGER;

public final class HDRModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            //LOGGER.info("GLFWColorManagement.glfwGetWindowTransfer:{}", GLFWColorManagement.glfwGetWindowTransfer(Minecraft.getInstance().getWindow().handle()));
        }
        );
    }
}
