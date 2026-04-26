#include veil:fog

uniform sampler2D Sampler0;
uniform float GameTime;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float map(float value, float min1, float max1, float min2, float max2) {
  return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    vec4 warbleColor = texture(Sampler0, texCoord0 + vec2(0, GameTime * -1000));
    
    float lightningSDF = color.r;
    
    float factor = vertexColor.a;
    
    float bolt = 1 - lightningSDF;
    float boltFactor = 1 - factor * 0.5;
    bolt = map(bolt, 1.0 + warbleColor.g * 3, boltFactor, 1, 0);
    bolt = step(1 - bolt, 0.99);
    
    float glow = 1 - lightningSDF;
    glow = map(glow, 1 + warbleColor.g, 1 - factor*2, 1, 0);
    glow = smoothstep(0, 1, glow);
    
    float lightningAlpha = max(bolt, glow) * 0.8;
    vec4 lightningColor = vec4(vertexColor.rgb * lightningAlpha, lightningAlpha);
    fragColor = linear_fog(lightningColor, vertexDistance, FogStart, FogEnd, vec4(0.0));
}

