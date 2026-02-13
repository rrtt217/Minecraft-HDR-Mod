#version 330

uniform sampler2D InSampler;

in vec2 texCoord;

out vec4 fragColor;

const float PQ_M1 = 2610.0/4096 * 1.0/4;
const float PQ_M2 = 2523.0/4096 * 128;
const float PQ_C1 = 3424.0/4096;
const float PQ_C2 = 2413.0/4096 * 32;
const float PQ_C3 = 2392.0/4096 * 32;

void main() {
    vec4 color = texture(InSampler, texCoord);
    color.rgb = pow(color.rgb, vec3(2.2));
    color = color * 203.0 / 10000.0;
    color.rgb = pow(color.rgb, vec3(PQ_M1));
    color.rgb = (vec3(PQ_C1) + vec3(PQ_C2) * color.rgb) / (vec3(1.0) + vec3(PQ_C3) * color.rgb);
    color.rgb = pow(color.rgb, vec3(PQ_M2));
    fragColor = color;
}