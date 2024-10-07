#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;
uniform float UVRatio;
uniform float RingDistance;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float map(float value, float min1, float max1, float min2, float max2) {

    value = clamp(value, min(min1, max1), max(min1, max1));
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

float noise(vec2 uv) {
    return texture(Sampler0, uv).r;
}

float streaks(vec2 uv) {
    return texture(Sampler0, uv).g;
}

float bubbles(vec2 uv) {
    return texture(Sampler0, uv).b;
}

float detailNoise(vec2 uv) {
    return (noise(uv) + noise(uv * 2) * 0.5 + noise(uv * 4) * 0.25) / 1.75;
}

vec2 uvRez(vec2 uv, float rez) { return floor(uv * rez) / rez; }
vec2 uvRez(vec2 uv, vec2 rez) { return floor(uv * rez) / rez; }

void main() {
    //noise
    float size = 1.0;
    vec2 resolution = 400.0 * vec2(UVRatio, 1.0);
    vec2 uv = texCoord0 * size;
    uv += RingDistance * 83.141592653;
    float speed = -15.0 * (1.0 - RingDistance);
    float uvMix = map(1.0 - (uv.x), 0.5, 1.0, 0.0, 1.0);
    float verticalOffset = ((sin((uvRez(uv, resolution).x) * 2.0 * 3.141592653 * 8.0 - (GameTime * speed * 0.1)) + 1.0) * 0.5) * 0.01;

    vec2 mixUV = uvRez(uv, resolution) + vec2((GameTime * speed * 0.3), verticalOffset) * 0.5;
    float mixNoise = noise(mixUV);
    mixNoise = smoothstep(0.0, 1.0, mixNoise);
    mixNoise = smoothstep(0.0, 1.0, mixNoise);
    mixNoise = smoothstep(0.0, 1.0, mixNoise);

    vec2 offsetUV = uvRez(uv, resolution) * vec2(2.0, 1.0) + vec2((GameTime * speed * 0.2), verticalOffset);
    float offsetNoise = noise(offsetUV) * mix(0.8, 0.05, mixNoise) * 0.5;

    vec2 bubbleUV = uvRez(uv, resolution) * 2.0 + vec2(offsetNoise, offsetNoise-(GameTime * speed));
    float bubble = bubbles(bubbleUV);

    float streak = streaks(bubbleUV);

    float noisevalue = mix(bubble, streak, mixNoise);

    //gradient
    vec2 guv0 = uvRez(uv, resolution) * vec2(1.0, 2.0);
    float gradientstreak = streaks(guv0);
    float streakStrength = 0.2;

    float gradientTop = 0.85;
    float gradientMiddle = 0.5;
    float gradientBottom = 0.05;
    float gradientOffset = 0.2;

    vec2 gradientUV = uvRez(texCoord0.xy, resolution * size);
    float g1 = map(gradientUV.y, 1.0, gradientTop, 0.0, 1.0);
    float g2 = map(gradientUV.y, gradientTop - gradientOffset, gradientMiddle - gradientstreak * streakStrength, 1.0, 0.0);
    float g3 = map(gradientUV.y, gradientMiddle - gradientstreak * streakStrength, gradientBottom + gradientOffset, 0.0, 1.0);
    float g4 = map(gradientUV.y, gradientBottom, 0.0, 1.0, 0.0);
    float gradient = g1 * g2 + g3 * g4;
    gradient = smoothstep(0.0, 1.0, gradient);
    gradient = smoothstep(0.0, 1.0, gradient);

    float noiseStrength = 0.5;
    float value = mix(gradient, noisevalue, noiseStrength);
    value -= 0.5;
    value *= 5.0;
    value = clamp(value, 0.0, 1.0);

    noisevalue *= (1.0 - gradient) * (1 - RingDistance) * 0.5;
    fragColor = vertexColor * vec4(1.0 + noisevalue, 1.0 + noisevalue, 1.0 + noisevalue, value);
}