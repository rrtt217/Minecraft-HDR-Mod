#version 330

uniform sampler2D InSampler;
layout(std140) uniform UiLuminance {
    float uiLuminance;
};

in vec2 texCoord;

out vec4 fragColor;

const float PQ_M1 = 2610.0/4096 * 1.0/4;
const float PQ_M2 = 2523.0/4096 * 128;
const float PQ_C1 = 3424.0/4096;
const float PQ_C2 = 2413.0/4096 * 32;
const float PQ_C3 = 2392.0/4096 * 32;

const mat3 BT709_TO_BT2020_MAT = mat3(
    vec3(0.6274039149284363, 0.06909728795289993, 0.0163914393633604),
    vec3(0.3292830288410187, 0.9195404052734375, 0.08801330626010895),
    vec3(0.04331306740641594, 0.01136231515556574, 0.8955952525138855));

void main() {
    vec4 color = texture(InSampler, texCoord);
    // safe sRGB decode
    vec3 s = sign(color.rgb);
    color.rgb = abs(color.rgb);
    bvec3 cutoff = lessThan(color.rgb, vec3(0.04045));
    vec3 higher = pow((color.rgb + vec3(0.055)) / vec3(1.055), vec3(2.4));
    vec3 lower = color.rgb / vec3(12.92);
    color.rgb = mix(higher, lower, cutoff);
    color.rgb *= s;

    #if CURRENT_PRIMARIES == PRIMARIES_BT2020
    color.rgb = BT709_TO_BT2020_MAT * color.rgb;
    #endif

    #if CURRENT_TRANSFER_FUNCTION == TRANSFER_FUNCTION_ST2084_PQ
        // PQ encode
        color.rgb = color.rgb * uiLuminance / 10000.0;
        color.rgb = pow(color.rgb, vec3(PQ_M1));
        color.rgb = (vec3(PQ_C1) + vec3(PQ_C2) * color.rgb) / (vec3(1.0) + vec3(PQ_C3) * color.rgb);
        color.rgb = pow(color.rgb, vec3(PQ_M2));
    #elif CURRENT_TRANSFER_FUNCTION == TRANSFER_FUNCTION_EXT_LINEAR
        // scRGB encode
        color.rgb *= uiLuminance / 80.0;
    #elif CURRENT_TRANSFER_FUNCTION == TRANSFER_FUNCTION_SRGB
        // sRGB encode
        s = sign(color.rgb);
        cutoff = lessThan(color.rgb, vec3(0.0031308));
        higher = vec3(1.055) * pow(color.rgb, vec3(1.0 / 2.4)) - vec3(0.055);
        lower = color.rgb * vec3(12.92);
        color.rgb = mix(higher, lower, cutoff) * s;
    #endif
    fragColor = color;
}