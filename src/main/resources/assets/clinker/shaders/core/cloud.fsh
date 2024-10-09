#version 150

uniform sampler2D Sampler0;

uniform vec4 FogColor;
uniform vec4 SkyColor;

uniform vec4 ColorModulator;
uniform vec2 UVOffset;
uniform float Depth;
uniform float Radius;
uniform float GameTime;

in vec2 texCoord0;
in vec4 vertexColor;
in vec3 position;

out vec4 fragColor;

vec2 uvRez(vec2 uv, float rez) { return floor(uv * rez) / rez; }
float map(float value, float min1, float max1, float min2, float max2) {
    value = clamp(value, min(min1, max1), max(min1, max1));
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

void main() {
    vec2 uv = uvRez((texCoord0*Radius + UVOffset), 1.0) / 400.0F;
    vec4 textureA = texture(Sampler0, (uv + GameTime*2.5)*0.5);
    vec4 textureB = texture(Sampler0, (uv + GameTime*3)*0.8);
    vec4 textureC = texture(Sampler0, (uv + GameTime*-4)*1.0 + textureB.r);

    float fadeAway = smoothstep(0.0, 1.0, map(length(position), 10.0, 20.0, 0.0, 1.0));

    float dpth = (1.0 - Depth);
    dpth *= dpth;

    float clouds = mix(textureA.b, textureB.b, sin(GameTime * 100.0));// * 0.7 + textureC.b * 0.3;
    //clouds = map(clouds, 0.0, 1.0, 0.0, 1.0);

    float difference = (clouds - (1.0 - Depth));
    difference *= 10.0;
    difference = clamp(difference, 0.0, 1.0);

    float alpha = length(((texCoord0 * 2.0) - 1.0) * Radius);
    alpha = map(alpha, 0.0, Radius - (Radius*0.4) * Depth * Depth, 0.0, 1.0);
    alpha = 1.0 - alpha;

    vec4 color = vec4(clamp(FogColor.rgb * (1 + dpth * 0.75), vec3(0.0), vec3(1.0)), difference * alpha * alpha * alpha * fadeAway * 0.5 * Depth);
    float mixFactor = 1.0 - Depth;
    mixFactor = clamp(mixFactor * 3.0, 0.0, 1.0);
    mixFactor = sqrt(mixFactor);
    mixFactor = map(mixFactor, 0.0, 1.0, 0.5, 1.0);
    color = mix(vec4(SkyColor.rgb, color.a), color, mixFactor);

    fragColor = color * ColorModulator;
}