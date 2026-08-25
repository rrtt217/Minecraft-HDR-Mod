package xyz.rrtt217.HDRMod.compat.vitrail;

import dev.vitrail.uniform.UniformCatalog;
import dev.vitrail.uniform.UniformShape;
import net.minecraft.client.Minecraft;
import xyz.rrtt217.HDRMod.HDRMod;

public final class HDRModValues {
    private HDRModValues() {}
    public static void register(UniformCatalog.Builder builder) {
        builder.add("HdrGameMinimumBrightness", UniformShape.FLOAT, (world, out) -> {
            out.set(HDRMod.colorManagementInfoProvider.getCurrentGameMinimumBrightness(Minecraft.getInstance().getWindow().handle()));
        });
        builder.add("HdrGamePeakBrightness", UniformShape.FLOAT, (world, out) -> {
            out.set(HDRMod.colorManagementInfoProvider.getCurrentGamePeakBrightness(Minecraft.getInstance().getWindow().handle()));
        });
        builder.add("HdrGamePaperWhiteBrightness", UniformShape.FLOAT, (world, out) -> {
            out.set(HDRMod.colorManagementInfoProvider.getCurrentGamePaperWhiteBrightness(Minecraft.getInstance().getWindow().handle()));
        });        builder.add("HdrUIBrightness", UniformShape.FLOAT, (world, out) -> {
            out.set(HDRMod.colorManagementInfoProvider.getCurrentUIBrightness(Minecraft.getInstance().getWindow().handle()));
        });

    }
}
