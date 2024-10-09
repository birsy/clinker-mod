#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec2 UVOffset;
uniform float Depth;
uniform float Radius;

out vec2 texCoord0;
out vec4 vertexColor;
out vec3 position;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;
    position = (ModelViewMat * vec4(Position, 1.0)).xyz;
}