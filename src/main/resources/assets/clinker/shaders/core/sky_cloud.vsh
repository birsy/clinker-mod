//#version 150
//
//in vec3 Position;
//in vec4 Color;
//in vec2 UV0;
//
//uniform mat4 ModelViewMat;
//uniform mat4 ProjMat;
//uniform float GameTime;
//uniform float FogStart; //maximum UV
//uniform float FogEnd; //ring distance
//
//out vec4 vertexColor;
//out vec2 texCoord0;
//
//void main() {
//    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
//
//    vertexColor = Color;
//    texCoord0 = UV0;
//}

#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform vec4 RingColor;
uniform float UVSquish;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vec2 uv = UV0;//(UV0 * 2.0) - vec2(1.0);
    uv.y = (uv.y * 2.0) - 1.0;
    uv.y /= 1.0-UVSquish;
    uv.y = (uv.y + 1.0) * 0.5;
   // uv.x *= (1.0 - UVSquish);
    //uv = (uv + vec2(1.0)) / 0.5;
    vertexColor = Color * RingColor;
    texCoord0 = uv;
}