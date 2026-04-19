package xyz.rrtt217.HDRMod.fabric.client;

import net.fabricmc.api.ClientModInitializer;

public final class HDRModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        // Only for debug.
//        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
//        ClientTickEvents.START_CLIENT_TICK.register(client -> {
//           if(HDRMod.UiLuminanceUBO != null) HDRMod.UiLuminanceUBO.update(config.useSDRWhiteLevelAsUiLuminance ? GLFWColorManagementUtils.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().handle()) : config.UiLuminance);
//        });
    }
}
