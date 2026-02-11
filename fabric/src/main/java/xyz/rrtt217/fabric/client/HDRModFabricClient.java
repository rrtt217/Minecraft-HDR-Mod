package xyz.rrtt217.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import xyz.rrtt217.GLFWColorManagement;
import xyz.rrtt217.HDRMod;

import static xyz.rrtt217.HDRMod.LOGGER;

public final class HDRModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        // Only for debug.
//        ClientTickEvents.START_CLIENT_TICK.register(client -> {
//            LOGGER.info("Get window with {} nit SDR white level, {} nit max luminance, {} nit min luminance, {} Primaries, {} Transfer function ",
//                    GLFWColorManagement.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()), GLFWColorManagement.glfwGetWindowMaxLuminance(Minecraft.getInstance().getWindow().handle()) ,GLFWColorManagement.glfwGetWindowMinLuminance(Minecraft.getInstance().getWindow().handle()), HDRMod.WindowPrimaries,HDRMod.WindowTransferFunction
//            );
//        });
    }
}
