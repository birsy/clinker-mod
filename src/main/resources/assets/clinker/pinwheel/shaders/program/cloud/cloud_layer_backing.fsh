#version 150
#include clinker:dither

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 col = vertexColor;
    int x = int(gl_FragCoord.x), y = int(gl_FragCoord.y);
    col.a = dither(x, y, col.a);
    col.rgb = vec3(
        actuallyDither(x, y, col.r),
        actuallyDither(x, y, col.g),
        actuallyDither(x, y, col.b)
    );
    if (col.a < 0.01) discard;
    fragColor = col;
}



















































































































































