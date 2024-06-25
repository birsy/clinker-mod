#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    float distance = color.a;
    float distanceParabola = distance * (1.0 - distance) * 4;

    float time = vertexColor.a;
    float factor1 = smoothstep(0.0, 1.0, time * 1.5);
    float factor3 = smoothstep(0.0, 1.0, smoothstep(0.0, 1.0, time));
    float factor2 = (factor1 + factor3) * 0.5;

    float alpha = min(map(distance, factor3, factor2, 0.0, 1.0), map(distance, factor2, factor1, 1.0, 0.0)) * distanceParabola;

    color.a = 1.0;
    color *= vec4(vertexColor.rgb, 1.0) * ColorModulator * alpha;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
