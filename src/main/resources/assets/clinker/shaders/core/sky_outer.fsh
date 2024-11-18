#version 150

uniform sampler2D Sampler0;

uniform float WiggleTime;
uniform vec4 FogColor;
uniform vec4 SkyColor1;
uniform vec4 SkyColor2;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

vec2 uvRez(vec2 uv, float rez) { return floor(uv * rez) / rez; }
float map(float value, float min1, float max1, float min2, float max2) {
    value = clamp(value, min(min1, max1), max(min1, max1));
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

void main() {
    float mixFactor = vertexColor.r;
    mixFactor *= mixFactor;
    vec2 uv = uvRez(texCoord0, 128.0);
    float cloudTex = (texture(Sampler0, uv + vec2(sin(WiggleTime + uv.x*5) * 0.1)).a);
    mixFactor = clamp(mixFactor + cloudTex * vertexColor.g, 0.0, 1.0);

    vec4 color = mix(SkyColor1, SkyColor2, mixFactor);
    color = mix(SkyColor1, color, smoothstep(0, 0.5, vertexColor.r));
    float fogLerp = map(1 - (vertexColor.r - (1 - cloudTex)*0.5), 0, 0.5, 1.0, 0.0);
    fogLerp = max(fogLerp, map(1 - vertexColor.r, 0, 0.3, 1.0, 0.0));
    color = mix(color, FogColor, fogLerp * fogLerp);
    //vec4 color = vertexColor;//mix(SkyColor1, SkyColor2, vertexColor.a);//vec4(mix(SkyColor1, SkyColor2, mixFactor).rgb, vertexColor.a);
    if (color.a < 0.1) discard;

    fragColor = color * ColorModulator;
}
