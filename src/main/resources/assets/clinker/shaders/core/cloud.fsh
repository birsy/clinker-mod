#version 150

uniform sampler2D Sampler0;

uniform vec4 FogColor;
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

void main() {
    vec2 uv = uvRez(texCoord0 + UVOffset, Radius * 2.0);
    uv += GameTime;
    vec4 texture = texture(Sampler0, uv);
    float alpha = 1.0 - length(texCoord0 * 2.0 - vec2(1.0));

    float dpth = Depth;
    dpth *= dpth;
    float difference = (texture.r - (1.0 - Depth));
    difference *= 5.0;
    difference = clamp(difference, 0.0, 1.0);
    vec4 color = vec4(FogColor.rgb + (1.0 - dpth) * 0.2, difference * alpha * alpha);

    fragColor = color * ColorModulator;
}