#version 430
#include veil:fog
#veil:buffer veil:camera Camera

uniform sampler2D Sampler2;
uniform vec4 Color;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;
uniform float Radius;

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;

out vec2 texCoord0;
out vec4 vertexColor;

float smin( float a, float b, float k ) {
    k *= 4.0;
    float h = max( k-abs(a-b), 0.0 )/k;
    return min(a,b) - h*h*k*(1.0/4.0);
}

void main() {
    vec4 viewSpaceOrigin = ModelViewMat * vec4(Position, 1.0);
    float distanceFromCamera = length(viewSpaceOrigin);
    float forwardOffsetDistance = max(smin(Radius * 0.5, distanceFromCamera - 1.5, 1.0), 0.0);
    vec4 offset = vec4(normalize(viewSpaceOrigin.xyz) * -forwardOffsetDistance, 0.0);

    vec2 normalizedTexCoord = TexCoord * 2.0 - 1.0;
    vec3 billboardOffset = normalize(normalizedTexCoord.x * Camera.IViewMat[0].xyz + normalizedTexCoord.y * Camera.IViewMat[1].xyz) * Radius;
    vec3 vertexPos = Position + billboardOffset;
    vec4 viewPos = (ModelViewMat * vec4(vertexPos, 1.0)) + offset;

    gl_Position = ProjMat * viewPos;
    texCoord0 = TexCoord;
    vertexColor = Color * vec4(1.0, 1.0, 1.0, smoothstep(64.0, 32.0, distanceFromCamera));
}