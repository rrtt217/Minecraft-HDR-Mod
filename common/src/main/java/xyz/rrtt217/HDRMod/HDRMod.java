package xyz.rrtt217.HDRMod;

import com.mojang.blaze3d.platform.InputConstants;
import dev.vitrail.Vitrail;
import dev.vitrail.render.PackChain;
import dev.vitrail.screen.ScreenText;
import dev.vitrail.settings.PackSession;
import io.homo.superresolution.common.presentation.vulkan.VulkanPresentationFeature;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.slf4j.LoggerFactory;
import xyz.rrtt217.HDRMod.compat.sr.SRVulkanPresentationColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.core.api.HDRModApiImpl;
import xyz.rrtt217.HDRMod.core.color.ColorTransformRenderer;
import org.slf4j.Logger;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.core.interop.GLInteropResourceManager;
import xyz.rrtt217.HDRMod.core.interop.StubGLInteropResourceManager;
import xyz.rrtt217.HDRMod.util.color.ColorManagementInfoProvider;
import xyz.rrtt217.HDRMod.util.color.SDLColorManagementInfoProvider;

import java.nio.file.Path;

import static xyz.rrtt217.HDRMod.mixin.HDRModMixinPlugin.*;

public final class HDRMod {
    public static final String MOD_ID = "hdr_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Global Renderers.
    public static ColorTransformRenderer PresentationColorTransformRenderer;
    public static ColorTransformRenderer ScreenshotColorTransformRenderer;
    public static ColorTransformRenderer ReplayColorTransformRenderer;

    // Key Mapping.
    public static final KeyMapping.Category HDRModCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hdr_mod","main"));
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.hdr_mod.open_config_menu", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F9, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping HDR_SCREENSHOT = new KeyMapping(
            "key.hdr_mod.take_hdr_screenshot", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_F10, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping TOGGLE_HDR = new KeyMapping(
            "key.hdr_mod.toggle_hdr", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping VALUE_UP = new KeyMapping(
            "key.hdr_mod.value_up", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping VALUE_DOWN = new KeyMapping(
            "key.hdr_mod.value_down", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping TOGGLE_VALUE_ADJUSTED = new KeyMapping(
            "key.hdr_mod.toggle_value_adjusted", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping TOGGLE_VALUE_ADJUSTED_BACKWARDS = new KeyMapping(
            "key.hdr_mod.toggle_value_adjusted_backwards", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            -1, // The default keycode
            HDRModCategory // The category translation key used to categorize in the Controls screen
    );

    public static Minecraft minecraft;

    public static boolean isReplayRendering = false;

    public static ConfigHolder<HDRModConfig> configHolder;

    public static HDRModApiImpl apiImpl;

    public static ColorManagementInfoProvider colorManagementInfoProvider;

    public static GLInteropResourceManager glInteropResourceManager;

    public static void earlyInit() {
        if(configHolder == null) configHolder = AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
        if(apiImpl == null) apiImpl = new HDRModApiImpl();
    }

    public HDRMod() {
    }

    public static void init() {
        // Register config.
        if(configHolder == null) configHolder = AutoConfig.register(HDRModConfig.class, Toml4jConfigSerializer::new);
        HDRModConfig config = configHolder.getConfig();
        if(colorManagementInfoProvider == null) {
            if (hasSr && VulkanPresentationFeature.isRequested()) {
                colorManagementInfoProvider = new SRVulkanPresentationColorManagementInfoProvider();
            } else if (hasBlazeSdl) {
                colorManagementInfoProvider = new SDLColorManagementInfoProvider();
            } else {
                colorManagementInfoProvider = new ColorManagementInfoProvider(config);
            }
        }
        glInteropResourceManager = new StubGLInteropResourceManager();

        if(apiImpl == null) apiImpl = new HDRModApiImpl();
        apiImpl.previousEnableHDR = config.enableHDR;

        if(hasIris){
            apiImpl.addHDRStateChangeListener(state -> {
                if(IrisApi.getInstance().isShaderPackInUse()){
                    try{
                        Iris.reload();
                    }
                    catch (Exception ignored){}
                }
            });
            // Currently we can't be better without a lot more work.
            apiImpl.addHDRCompatibleShaderpackStateSupplier(IrisApi.getInstance()::isShaderPackInUse);
        }

        if(hasVitrail){
            apiImpl.addHDRStateChangeListener(state -> {
                // Reload method from vitrail code. Original method is private.
                Path directory = PackChain.session()
                        .map(PackSession::gameDirectory)
                        .orElseGet(() -> Vitrail.platform().gameDirectory());

                PackChain.reload(directory);

                MutableComponent said = PackChain.lastError()
                        .map(reason -> Component.translatable(ScreenText.RELOAD_FAILED, reason)
                                .withStyle(ChatFormatting.RED))
                        .orElseGet(() -> Component.translatable(ScreenText.PACK_RELOADED));

                // In a world only, which is Iris's own guard: outside one there is no chat to say it in, and
                // what was read is in the log either way.
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    minecraft.player.sendSystemMessage(said);
                }
            });
            // Currently we can't be better without a lot more work.
            apiImpl.addHDRCompatibleShaderpackStateSupplier(() -> (!PackChain.noPackWanted() && !PackChain.packMissing()));
        }

        configHolder.registerSaveListener(apiImpl::onConfigSave);

        LOGGER.debug("HDRMod Initialized!");
    }
}