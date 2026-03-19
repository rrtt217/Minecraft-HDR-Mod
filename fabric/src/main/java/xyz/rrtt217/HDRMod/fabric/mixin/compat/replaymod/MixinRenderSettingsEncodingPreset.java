package xyz.rrtt217.HDRMod.fabric.mixin.compat.replaymod;

import com.replaymod.render.RenderSettings;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import xyz.rrtt217.HDRMod.config.HDRModConfig;

@Mixin(RenderSettings.EncodingPreset.class)
public class MixinRenderSettingsEncodingPreset {
    @Final
    @Shadow
    private String preset;

    /**
     * @author rrtt217
     * @reason overwrite default cmd prefix on HDR export enabled
     */
    @Overwrite
    public String getValue(){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableReplayHDRVideoExport) return  "-color_primaries bt2020 -color_trc smpte2084 -color_range pc -y -f rawvideo -pix_fmt rgba64 -s %WIDTH%x%HEIGHT% -r %FPS% -i - %FILTERS%" + this.preset;
        return "-y -f rawvideo -pix_fmt bgra -s %WIDTH%x%HEIGHT% -r %FPS% -i - %FILTERS%" + this.preset;
    }
}
