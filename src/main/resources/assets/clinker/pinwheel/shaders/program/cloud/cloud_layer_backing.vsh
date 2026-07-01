#version 430
#veil:buffer veil:camera Camera
#include clinker:cloud_layer

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;


uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 SkyColor;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 DisplacementDirection;
uniform ivec2 PlayerCloudCell;
uniform vec2 PlayerCloudCellOffset;
uniform int CloudCellSize;
uniform float CloudHeight;
uniform float AlphaMultiplier;

out vec2 texCoord;
out vec4 vertexColor;

void main() {
    vec3 gridPos = Position;
    float brightness, alpha, baseOffset, displacement;
    sampleCloud(ivec2(int(gridPos.x), int(gridPos.z)) + PlayerCloudCell, vec2(0.0), PlayerCloudCell, PlayerCloudCellOffset, CloudCellSize, FogEnd, 0.8,
                brightness, alpha, baseOffset, displacement);

    gridPos = gridPos * float(CloudCellSize) + vec3(0.0, CloudHeight, 0.0);
    gridPos += DisplacementDirection * (baseOffset + displacement);
    gridPos -= DisplacementDirection * 2.0;

    gl_Position = ProjMat * ModelViewMat * vec4(gridPos, 1.0);

    texCoord = TexCoord;
    vertexColor = vec4(cloudColor(SkyColor.rgb, FogColor.rgb, brightness) * 0.9, alpha * AlphaMultiplier);
}