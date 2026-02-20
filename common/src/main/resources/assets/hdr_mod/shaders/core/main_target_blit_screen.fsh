#version 150

uniform sampler2D DiffuseSampler;

uniform float UiBrightness;
uniform float EotfEmulate;

uniform int CurrentPrimaries;
uniform int CurrentTransferFunction;

in vec2 texCoord;

out vec4 fragColor;

const float PQ_M1 = 2610.0/4096 * 1.0/4;
const float PQ_M2 = 2523.0/4096 * 128;
const float PQ_C1 = 3424.0/4096;
const float PQ_C2 = 2413.0/4096 * 32;
const float PQ_C3 = 2392.0/4096 * 32;

vec3 PQ_Encode(vec3 c, float scaling) {
    c *= scaling / 10000.0;
    c = pow(c, vec3(PQ_M1));
    c = (vec3(PQ_C1) + vec3(PQ_C2) * c) / (vec3(1.0) + vec3(PQ_C3) * c);
    return pow(c, vec3(PQ_M2));
}

const mat3 BT709_TO_BT2020_MAT = mat3(
    vec3(0.6274039149284363, 0.06909728795289993, 0.0163914393633604),
    vec3(0.3292830288410187, 0.9195404052734375, 0.08801330626010895),
    vec3(0.04331306740641594, 0.01136231515556574, 0.8955952525138855));

vec3 sRGB_DecodeSafe(vec3 c) {
    vec3 s = sign(c);
    c = abs(c);
    bvec3 cutoff = lessThan(c, vec3(0.04045));
    vec3 higher = pow((c + vec3(0.055)) / vec3(1.055), vec3(2.4));
    vec3 lower = c / vec3(12.92);
    return mix(higher, lower, cutoff) * s;
}

vec3 sRGB_EncodeSafe(vec3 c) {
    vec3 s = sign(c);
    c = abs(c);
    bvec3 cutoff = lessThan(c, vec3(0.0031308));
    vec3 higher = vec3(1.055) * pow(c, vec3(1.0 / 2.4)) - vec3(0.055);
    vec3 lower = c * vec3(12.92);
    return mix(higher, lower, cutoff) * s;
}

vec3 EOTFEmulate(vec3 color, float threshold) {
  const float gamma = 2.2; //only need and best at 2.2 (others will have obvious piecewise cutoff)

  //scale
  color /= threshold;

  //setup
  vec3 colorS = sign(color);
  vec3 colorA = abs(color);
  vec3 colorCorrected = pow(sRGB_EncodeSafe(colorA), vec3(gamma));

  //resolve
  color = mix(colorS * colorCorrected, color, step(1.0, colorA));

  //scale
  color *= threshold;

  return color;
}


void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    //sRGB decode
    color.rgb = sRGB_DecodeSafe(color.rgb);

    //To BT2020
    if(CurrentPrimaries == 6){
        color.rgb = BT709_TO_BT2020_MAT * color.rgb;
    }

    //EOTF Emulate / Gamma Correction 2.2
    if (EotfEmulate > 0) {
        color.xyz *= UiBrightness / 203.; //scale 203 as 1
        color.rgb = EOTFEmulate(color.rgb, EotfEmulate / 203.);
        color.xyz /= UiBrightness / 203.; //scale back
    }

    //Transfer
    if(CurrentTransferFunction == 11)
    {
        color.rgb = PQ_Encode(color.rgb, UiBrightness);
    }
    else if(CurrentTransferFunction == 5)
    {
        // scRGB encode
        color.rgb *= UiBrightness / 80.0;
    }
    else if(CurrentTransferFunction == 9)
    {
        // sRGB encode
        color.rgb *= UiBrightness / 203.0;
        color.rgb = sRGB_EncodeSafe(color.rgb);
    }
    fragColor = color;
}
