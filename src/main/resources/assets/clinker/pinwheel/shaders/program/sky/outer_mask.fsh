#version 150

uniform vec4 FogColor;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = FogColor * vertexColor;
}