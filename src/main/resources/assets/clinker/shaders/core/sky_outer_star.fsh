#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float TwinkleTime;

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 starTexture = texture(Sampler0, texCoord0);
    float starAlpha = mix(starTexture.r, starTexture.g, vertexColor.b);
    vec4 starColor = vec4(texture(Sampler1, vec2(vertexColor.g, 0.0)).rgb, starAlpha * vertexColor.r * vertexColor.a);
    if (starColor.a < 0.001) discard;
    fragColor = starColor * ColorModulator;
}
