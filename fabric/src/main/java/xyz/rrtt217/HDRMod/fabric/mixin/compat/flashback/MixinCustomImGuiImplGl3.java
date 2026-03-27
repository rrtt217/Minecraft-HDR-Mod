package xyz.rrtt217.HDRMod.fabric.mixin.compat.flashback;

import com.moulberry.flashback.editor.ui.CustomImGuiImplGl3;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.GLFWColorManagement;

import java.lang.reflect.Field;

@Mixin(CustomImGuiImplGl3.class)
public class MixinCustomImGuiImplGl3 {

    @Unique
    protected int attribLocationUIBrightness = 0;
    @Unique
    protected int attribLocationEotfEmulate = 0;
    @Unique
    protected int attribLocationPrimaries = 0;
    @Unique
    protected int attribLocationTransferFunction = 0;

    @Inject(method = "createDeviceObjects", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20;glGetUniformLocation(ILjava/lang/CharSequence;)I"))
    private void hdr_mod$getHDRModUniformsLocation(CallbackInfoReturnable<Boolean> cir) throws NoSuchFieldException, IllegalAccessException {
        Field dataField = CustomImGuiImplGl3.class.getDeclaredField("data");
        dataField.setAccessible(true);
        Object dataObj = dataField.get(this);

        Field shaderHandleField = dataObj.getClass().getDeclaredField("shaderHandle");
        shaderHandleField.setAccessible(true);
        int shaderHandle = (int) shaderHandleField.get(dataObj);
        this.attribLocationUIBrightness = GL20.glGetUniformLocation(shaderHandle,"uiBrightness");
        this.attribLocationEotfEmulate = GL20.glGetUniformLocation(shaderHandle,"eotfEmulate");
        this.attribLocationPrimaries = GL20.glGetUniformLocation(shaderHandle,"primaries");
        this.attribLocationTransferFunction =  GL20.glGetUniformLocation(shaderHandle, "transferFunction");
    }

    @Inject(method = "setupRenderState", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL32;glUniform1i(II)V"))
    private void hdr_mod$setHDRModUniforms(CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        GL32.glUniform1f(attribLocationUIBrightness, config.uiBrightness < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().getWindow()) : config.uiBrightness);
        GL32.glUniform1f(attribLocationEotfEmulate, config.customEotfEmulate < 0 ? GLFWColorManagement.glfwGetWindowSdrWhiteLevel(Minecraft.getInstance().getWindow().getWindow()) : config.customEotfEmulate);
        GL32.glUniform1i(attribLocationPrimaries, config.autoSetPrimaries ? GLFWColorManagement.glfwGetWindowPrimaries(Minecraft.getInstance().getWindow().getWindow()) : config.customPrimaries.getId());
        GL32.glUniform1i(attribLocationTransferFunction, config.autoSetTransferFunction ? GLFWColorManagement.glfwGetWindowTransfer(Minecraft.getInstance().getWindow().getWindow()) : config.customTransferFunction.getId());
    }
    @Redirect(method = "createDeviceObjects", at = @At(value = "INVOKE", target = "Lcom/moulberry/flashback/editor/ui/CustomImGuiImplGl3;fragmentShaderGlsl120()Ljava/lang/String;"))
    private String hdr_mod$redirectToColorTransformProgramGlsl120(CustomImGuiImplGl3 instance) throws NoSuchFieldException, IllegalAccessException {
        Field dataField = CustomImGuiImplGl3.class.getDeclaredField("data");
        dataField.setAccessible(true);
        Object dataObj = dataField.get(this);

        Field glslVersionField = dataObj.getClass().getDeclaredField("glslVersion");
        glslVersionField.setAccessible(true);
        String glslVersion = (String) glslVersionField.get(dataObj);
        return glslVersion + "\n" +
                "#ifdef GL_ES\n" +
                "    precision mediump float;\n" +
                "#endif\n" +
                "uniform sampler2D Texture;\n" +
                "uniform float uiBrightness;\n" +
                "uniform float eoftEmulate;\n" +
                "uniform int primaries;\n" +
                "uniform int transferFunction;\n" +
                "varying vec2 Frag_UV;\n" +
                "varying vec4 Frag_Color;\n" +
                "const float PQ_M1 = 2610.0/4096 * 1.0/4;\n" +
                "const float PQ_M2 = 2523.0/4096 * 128;\n" +
                "const float PQ_C1 = 3424.0/4096;\n" +
                "const float PQ_C2 = 2413.0/4096 * 32;\n" +
                "const float PQ_C3 = 2392.0/4096 * 32;\n" +
                "vec3 PQ_Encode(vec3 c, float scaling) {\n" +
                "    c *= scaling / 10000.0;\n" +
                "    c = pow(c, vec3(PQ_M1));\n" +
                "    c = (vec3(PQ_C1) + vec3(PQ_C2) * c) / (vec3(1.0) + vec3(PQ_C3) * c);\n" +
                "    return pow(c, vec3(PQ_M2));\n" +
                "}\n" +
                "const mat3 BT709_TO_BT2020_MAT = mat3(\n" +
                "    vec3(0.6274039149284363, 0.06909728795289993, 0.0163914393633604),\n" +
                "    vec3(0.3292830288410187, 0.9195404052734375, 0.08801330626010895),\n" +
                "    vec3(0.04331306740641594, 0.01136231515556574, 0.8955952525138855));\n" +
                "vec3 sRGB_DecodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.04045));\n" +
                "    vec3 higher = pow((c + vec3(0.055)) / vec3(1.055), vec3(2.4));\n" +
                "    vec3 lower = c / vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 sRGB_EncodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.0031308));\n" +
                "    vec3 higher = vec3(1.055) * pow(c, vec3(1.0 / 2.4)) - vec3(0.055);\n" +
                "    vec3 lower = c * vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 EOTFEmulate(vec3 color, float threshold) {\n" +
                "  const float gamma = 2.2;\n" +
                "  color /= threshold;\n" +
                "  vec3 colorS = sign(color);\n" +
                "  vec3 colorA = abs(color);\n" +
                "  vec3 colorCorrected = pow(sRGB_EncodeSafe(colorA), vec3(gamma));\n" +
                "  color = mix(colorS * colorCorrected, color, step(1.0, colorA));\n" +
                "  color *= threshold;\n" +
                "  return color;\n" +
                "}\n" +
                "void main()\n" +
                "{\n" +
                "    vec4 color = Frag_Color * texture2D(Texture, Frag_UV.st);\n" +
                "    color.rgb = sRGB_DecodeSafe(color.rgb);\n" +
                "    if(primaries == 6)\n" +
                "        color.rgb = BT709_TO_BT2020_MAT * color.rgb;\n" +
                "    if (eoftEmulate > 0) {\n" +
                "        color.xyz *= uiBrightness / 203.0;\n" +
                "        color.rgb = EOTFEmulate(color.rgb, eoftEmulate / 203.0);\n" +
                "        color.xyz /= uiBrightness / 203.0;\n" +
                "    }\n" +
                "    if(transferFunction == 11){\n" +
                "        color.rgb = PQ_Encode(color.rgb, uiBrightness);\n" +
                "    }\n" +
                "    else if(transferFunction == 5){\n" +
                "        color.rgb *= uiBrightness / 80.0;\n" +
                "    }\n" +
                "    else if((transferFunction == 9) || (transferFunction == 10)){\n" +
                "        color.rgb *= uiBrightness / 203.0;\n" +
                "        color.rgb = sRGB_EncodeSafe(color.rgb);\n" +
                "    }\n" +
                "    gl_FragColor = color;\n" +
                "}\n";
    }
    @Redirect(method = "createDeviceObjects", at = @At(value = "INVOKE", target = "Lcom/moulberry/flashback/editor/ui/CustomImGuiImplGl3;fragmentShaderGlsl130()Ljava/lang/String;"))
    private String hdr_mod$redirectToColorTransformProgramGlsl130(CustomImGuiImplGl3 instance) throws NoSuchFieldException, IllegalAccessException {
        Field dataField = CustomImGuiImplGl3.class.getDeclaredField("data");
        dataField.setAccessible(true);
        Object dataObj = dataField.get(this);

        Field glslVersionField = dataObj.getClass().getDeclaredField("glslVersion");
        glslVersionField.setAccessible(true);
        String glslVersion = (String) glslVersionField.get(dataObj);
        return glslVersion + "\n" +
                "uniform sampler2D Texture;\n" +
                "uniform float uiBrightness;\n" +
                "uniform float eoftEmulate;\n" +
                "uniform int primaries;\n" +
                "uniform int transferFunction;\n" +
                "in vec2 Frag_UV;\n" +
                "in vec4 Frag_Color;\n" +
                "out vec4 Out_Color;\n" +
                "const float PQ_M1 = 2610.0/4096 * 1.0/4;\n" +
                "const float PQ_M2 = 2523.0/4096 * 128;\n" +
                "const float PQ_C1 = 3424.0/4096;\n" +
                "const float PQ_C2 = 2413.0/4096 * 32;\n" +
                "const float PQ_C3 = 2392.0/4096 * 32;\n" +
                "vec3 PQ_Encode(vec3 c, float scaling) {\n" +
                "    c *= scaling / 10000.0;\n" +
                "    c = pow(c, vec3(PQ_M1));\n" +
                "    c = (vec3(PQ_C1) + vec3(PQ_C2) * c) / (vec3(1.0) + vec3(PQ_C3) * c);\n" +
                "    return pow(c, vec3(PQ_M2));\n" +
                "}\n" +
                "const mat3 BT709_TO_BT2020_MAT = mat3(\n" +
                "    vec3(0.6274039149284363, 0.06909728795289993, 0.0163914393633604),\n" +
                "    vec3(0.3292830288410187, 0.9195404052734375, 0.08801330626010895),\n" +
                "    vec3(0.04331306740641594, 0.01136231515556574, 0.8955952525138855));\n" +
                "vec3 sRGB_DecodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.04045));\n" +
                "    vec3 higher = pow((c + vec3(0.055)) / vec3(1.055), vec3(2.4));\n" +
                "    vec3 lower = c / vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 sRGB_EncodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.0031308));\n" +
                "    vec3 higher = vec3(1.055) * pow(c, vec3(1.0 / 2.4)) - vec3(0.055);\n" +
                "    vec3 lower = c * vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 EOTFEmulate(vec3 color, float threshold) {\n" +
                "  const float gamma = 2.2;\n" +
                "  color /= threshold;\n" +
                "  vec3 colorS = sign(color);\n" +
                "  vec3 colorA = abs(color);\n" +
                "  vec3 colorCorrected = pow(sRGB_EncodeSafe(colorA), vec3(gamma));\n" +
                "  color = mix(colorS * colorCorrected, color, step(1.0, colorA));\n" +
                "  color *= threshold;\n" +
                "  return color;\n" +
                "}\n" +
                "void main()\n" +
                "{\n" +
                "    vec4 color = Frag_Color * texture2D(Texture, Frag_UV.st);\n" +
                "    color.rgb = sRGB_DecodeSafe(color.rgb);\n" +
                "    if(primaries == 6)\n" +
                "        color.rgb = BT709_TO_BT2020_MAT * color.rgb;\n" +
                "    if (eoftEmulate > 0) {\n" +
                "        color.xyz *= uiBrightness / 203.0;\n" +
                "        color.rgb = EOTFEmulate(color.rgb, eoftEmulate / 203.0);\n" +
                "        color.xyz /= uiBrightness / 203.0;\n" +
                "    }\n" +
                "    if(transferFunction == 11){\n" +
                "        color.rgb = PQ_Encode(color.rgb, uiBrightness);\n" +
                "    }\n" +
                "    else if(transferFunction == 5){\n" +
                "        color.rgb *= uiBrightness / 80.0;\n" +
                "    }\n" +
                "    else if((transferFunction == 9) || (transferFunction == 10)){\n" +
                "        color.rgb *= uiBrightness / 203.0;\n" +
                "        color.rgb = sRGB_EncodeSafe(color.rgb);\n" +
                "    }\n" +
                "    Out_Color = color;\n" +
                "}\n";
    }
    @Redirect(method = "createDeviceObjects", at = @At(value = "INVOKE", target = "Lcom/moulberry/flashback/editor/ui/CustomImGuiImplGl3;fragmentShaderGlsl300es()Ljava/lang/String;"))
    private String hdr_mod$redirectToColorTransformProgramGlsl300es(CustomImGuiImplGl3 instance) throws NoSuchFieldException, IllegalAccessException {
        Field dataField = CustomImGuiImplGl3.class.getDeclaredField("data");
        dataField.setAccessible(true);
        Object dataObj = dataField.get(this);

        Field glslVersionField = dataObj.getClass().getDeclaredField("glslVersion");
        glslVersionField.setAccessible(true);
        String glslVersion = (String) glslVersionField.get(dataObj);
        return glslVersion + "\n" +
                "precision mediump float;\n" +
                "uniform sampler2D Texture;\n" +
                "uniform float uiBrightness;\n" +
                "uniform float eoftEmulate;\n" +
                "uniform int primaries;\n" +
                "uniform int transferFunction;\n" +
                "in vec2 Frag_UV;\n" +
                "in vec4 Frag_Color;\n" +
                "layout (location = 0) out vec4 Out_Color;\n" +
                "const float PQ_M1 = 2610.0/4096 * 1.0/4;\n" +
                "const float PQ_M2 = 2523.0/4096 * 128;\n" +
                "const float PQ_C1 = 3424.0/4096;\n" +
                "const float PQ_C2 = 2413.0/4096 * 32;\n" +
                "const float PQ_C3 = 2392.0/4096 * 32;\n" +
                "vec3 PQ_Encode(vec3 c, float scaling) {\n" +
                "    c *= scaling / 10000.0;\n" +
                "    c = pow(c, vec3(PQ_M1));\n" +
                "    c = (vec3(PQ_C1) + vec3(PQ_C2) * c) / (vec3(1.0) + vec3(PQ_C3) * c);\n" +
                "    return pow(c, vec3(PQ_M2));\n" +
                "}\n" +
                "const mat3 BT709_TO_BT2020_MAT = mat3(\n" +
                "    vec3(0.6274039149284363, 0.06909728795289993, 0.0163914393633604),\n" +
                "    vec3(0.3292830288410187, 0.9195404052734375, 0.08801330626010895),\n" +
                "    vec3(0.04331306740641594, 0.01136231515556574, 0.8955952525138855));\n" +
                "vec3 sRGB_DecodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.04045));\n" +
                "    vec3 higher = pow((c + vec3(0.055)) / vec3(1.055), vec3(2.4));\n" +
                "    vec3 lower = c / vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 sRGB_EncodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.0031308));\n" +
                "    vec3 higher = vec3(1.055) * pow(c, vec3(1.0 / 2.4)) - vec3(0.055);\n" +
                "    vec3 lower = c * vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 EOTFEmulate(vec3 color, float threshold) {\n" +
                "  const float gamma = 2.2;\n" +
                "  color /= threshold;\n" +
                "  vec3 colorS = sign(color);\n" +
                "  vec3 colorA = abs(color);\n" +
                "  vec3 colorCorrected = pow(sRGB_EncodeSafe(colorA), vec3(gamma));\n" +
                "  color = mix(colorS * colorCorrected, color, step(1.0, colorA));\n" +
                "  color *= threshold;\n" +
                "  return color;\n" +
                "}\n" +
                "void main()\n" +
                "{\n" +
                "    vec4 color = Frag_Color * texture2D(Texture, Frag_UV.st);\n" +
                "    color.rgb = sRGB_DecodeSafe(color.rgb);\n" +
                "    if(primaries == 6)\n" +
                "        color.rgb = BT709_TO_BT2020_MAT * color.rgb;\n" +
                "    if (eoftEmulate > 0) {\n" +
                "        color.xyz *= uiBrightness / 203.0;\n" +
                "        color.rgb = EOTFEmulate(color.rgb, eoftEmulate / 203.0);\n" +
                "        color.xyz /= uiBrightness / 203.0;\n" +
                "    }\n" +
                "    if(transferFunction == 11){\n" +
                "        color.rgb = PQ_Encode(color.rgb, uiBrightness);\n" +
                "    }\n" +
                "    else if(transferFunction == 5){\n" +
                "        color.rgb *= uiBrightness / 80.0;\n" +
                "    }\n" +
                "    else if((transferFunction == 9) || (transferFunction == 10)){\n" +
                "        color.rgb *= uiBrightness / 203.0;\n" +
                "        color.rgb = sRGB_EncodeSafe(color.rgb);\n" +
                "    }\n" +
                "    Out_Color = color;\n" +
                "}\n";
    }
    @Redirect(method = "createDeviceObjects", at = @At(value = "INVOKE", target = "Lcom/moulberry/flashback/editor/ui/CustomImGuiImplGl3;fragmentShaderGlsl410Core()Ljava/lang/String;"))
    private String hdr_mod$redirectToColorTransformProgramGlsl410Core(CustomImGuiImplGl3 instance) throws NoSuchFieldException, IllegalAccessException {
        Field dataField = CustomImGuiImplGl3.class.getDeclaredField("data");
        dataField.setAccessible(true);
        Object dataObj = dataField.get(this);

        Field glslVersionField = dataObj.getClass().getDeclaredField("glslVersion");
        glslVersionField.setAccessible(true);
        String glslVersion = (String) glslVersionField.get(dataObj);
        return glslVersion + "\n" +
                "in vec2 Frag_UV;\n" +
                "in vec4 Frag_Color;\n" +
                "uniform sampler2D Texture;\n" +
                "uniform float uiBrightness;\n" +
                "uniform float eoftEmulate;\n" +
                "uniform int primaries;\n" +
                "uniform int transferFunction;\n" +
                "layout (location = 0) out vec4 Out_Color;\n" +
                "const float PQ_M1 = 2610.0/4096 * 1.0/4;\n" +
                "const float PQ_M2 = 2523.0/4096 * 128;\n" +
                "const float PQ_C1 = 3424.0/4096;\n" +
                "const float PQ_C2 = 2413.0/4096 * 32;\n" +
                "const float PQ_C3 = 2392.0/4096 * 32;\n" +
                "vec3 PQ_Encode(vec3 c, float scaling) {\n" +
                "    c *= scaling / 10000.0;\n" +
                "    c = pow(c, vec3(PQ_M1));\n" +
                "    c = (vec3(PQ_C1) + vec3(PQ_C2) * c) / (vec3(1.0) + vec3(PQ_C3) * c);\n" +
                "    return pow(c, vec3(PQ_M2));\n" +
                "}\n" +
                "const mat3 BT709_TO_BT2020_MAT = mat3(\n" +
                "    vec3(0.6274039149284363, 0.06909728795289993, 0.0163914393633604),\n" +
                "    vec3(0.3292830288410187, 0.9195404052734375, 0.08801330626010895),\n" +
                "    vec3(0.04331306740641594, 0.01136231515556574, 0.8955952525138855));\n" +
                "vec3 sRGB_DecodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.04045));\n" +
                "    vec3 higher = pow((c + vec3(0.055)) / vec3(1.055), vec3(2.4));\n" +
                "    vec3 lower = c / vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 sRGB_EncodeSafe(vec3 c) {\n" +
                "    vec3 s = sign(c);\n" +
                "    c = abs(c);\n" +
                "    bvec3 cutoff = lessThan(c, vec3(0.0031308));\n" +
                "    vec3 higher = vec3(1.055) * pow(c, vec3(1.0 / 2.4)) - vec3(0.055);\n" +
                "    vec3 lower = c * vec3(12.92);\n" +
                "    return mix(higher, lower, cutoff) * s;\n" +
                "}\n" +
                "vec3 EOTFEmulate(vec3 color, float threshold) {\n" +
                "  const float gamma = 2.2;\n" +
                "  color /= threshold;\n" +
                "  vec3 colorS = sign(color);\n" +
                "  vec3 colorA = abs(color);\n" +
                "  vec3 colorCorrected = pow(sRGB_EncodeSafe(colorA), vec3(gamma));\n" +
                "  color = mix(colorS * colorCorrected, color, step(1.0, colorA));\n" +
                "  color *= threshold;\n" +
                "  return color;\n" +
                "}\n" +
                "void main()\n" +
                "{\n" +
                "    vec4 color = Frag_Color * texture2D(Texture, Frag_UV.st);\n" +
                "    color.rgb = sRGB_DecodeSafe(color.rgb);\n" +
                "    if(primaries == 6)\n" +
                "        color.rgb = BT709_TO_BT2020_MAT * color.rgb;\n" +
                "    if (eoftEmulate > 0) {\n" +
                "        color.xyz *= uiBrightness / 203.0;\n" +
                "        color.rgb = EOTFEmulate(color.rgb, eoftEmulate / 203.0);\n" +
                "        color.xyz /= uiBrightness / 203.0;\n" +
                "    }\n" +
                "    if(transferFunction == 11){\n" +
                "        color.rgb = PQ_Encode(color.rgb, uiBrightness);\n" +
                "    }\n" +
                "    else if(transferFunction == 5){\n" +
                "        color.rgb *= uiBrightness / 80.0;\n" +
                "    }\n" +
                "    else if((transferFunction == 9) || (transferFunction == 10)){\n" +
                "        color.rgb *= uiBrightness / 203.0;\n" +
                "        color.rgb = sRGB_EncodeSafe(color.rgb);\n" +
                "    }\n" +
                "    Out_Color = color;\n" +
                "}\n";
    }
}
