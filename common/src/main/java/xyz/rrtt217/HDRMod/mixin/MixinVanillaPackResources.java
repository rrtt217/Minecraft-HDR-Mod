package xyz.rrtt217.HDRMod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Mixin(VanillaPackResources.class)
public abstract class MixinVanillaPackResources {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    private void onGetResource(PackType packType, ResourceLocation resourceLocation,
                               CallbackInfoReturnable<IoSupplier<InputStream>> cir) {
        // Determine prefix based on PackType
        String prefix;
        if (packType == PackType.CLIENT_RESOURCES) {
            prefix = "assets/";
        } else if (packType == PackType.SERVER_DATA) {
            prefix = "data/";
        } else {
            // Do not handle other types.
            return;
        }

        // Construct classpath：prefix + namespace + path
        String classPath = prefix + "hdr_mod" + "/" + resourceLocation.getPath();
        // LOGGER.info("Getting resource: " + classPath);
        if(!classPath.contains("shader")) return;

        // Use current thread classloader to get URL
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource(classPath);

        if (resourceUrl != null) {
            // Create IoSupplier，Open new stream
            IoSupplier<InputStream> supplier = () -> {
                try {
                    LOGGER.info("Getting resource: " + classPath);
                    return resourceUrl.openStream();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to open stream for " + resourceUrl, e);
                }
            };
            cir.setReturnValue(supplier);
        }
    }
}