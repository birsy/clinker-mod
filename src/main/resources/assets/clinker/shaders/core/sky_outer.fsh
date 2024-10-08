#version 150

uniform sampler2D Sampler0;

uniform float WiggleTime;
uniform vec4 FogColor;
uniform vec4 SkyColor;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

vec2 uvRez(vec2 uv, float rez) { return floor(uv * rez) / rez; }

void main() {
    float mixFactor = vertexColor.r;
    mixFactor *= mixFactor;
    vec2 uv = uvRez(texCoord0, 128.0);
    mixFactor = clamp(mixFactor + (texture(Sampler0, uv + vec2(sin(WiggleTime + uv.x*5) * 0.1)).a) * vertexColor.g, 0.0, 1.0);
    vec4 color = vec4(mix(SkyColor, FogColor, mixFactor).rgb, vertexColor.a);
    if (color.a < 0.1) discard;

    fragColor = color * ColorModulator;
}
