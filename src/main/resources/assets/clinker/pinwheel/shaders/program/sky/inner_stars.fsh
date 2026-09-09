#version 150

uniform sampler2D StarTextureSampler;

in vec2 texCoord;
in vec4 vertexColor;
in float fanciness;

out vec4 fragColor;

void main() {
    vec4 starTexture = texture(StarTextureSampler, texCoord);
    float starAlpha = mix(starTexture.r, starTexture.g, fanciness);
    vec4 starColor = vertexColor * starAlpha;
    if (starColor.a < 0.001) discard;
    fragColor = starColor;
}