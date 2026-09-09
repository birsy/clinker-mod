#version 430
#include veil:space_helper

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV;
layout(location = 2) in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec2 CloudHeight;
uniform vec2 CloudScale;

out vec3 pos;
out vec2 texCoord;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    pos = vec3(
        Position.x * CloudScale.x,
        mix(CloudHeight.x, CloudHeight.y, Position.y * 0.5 + 0.5),
        Position.z * CloudScale.x
    );
    texCoord = UV;
    vertexColor = Color;
}