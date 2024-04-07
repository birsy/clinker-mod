#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 lightColor;
in vec4 biomeColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec3 oatColor = vec3(0.556862745, 0.745098039, 0.705882353);
    vec4 texture = texture(Sampler0, texCoord0);
    vec4 mixedColor = vec4(mix(texture.r, biomeColor.r, oatColor.r), mix(texture.g, biomeColor.g, oatColor.g), mix(texture.b, biomeColor.b, oatColor.b), 1.0);

    vec4 color = texture.a * mixedColor * ColorModulator;
    if (color.a < 0.5) {
        discard;
    }
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}