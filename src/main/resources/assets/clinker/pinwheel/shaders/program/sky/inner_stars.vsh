#version 150
#include veil:space_helper

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV;
layout(location = 2) in vec4 Color;

uniform sampler2D StarColorSampler;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform float GameTime;
uniform float TwinkleTime;

out vec2 texCoord;
out vec4 vertexColor;
out float fanciness;

mat3 rotateY(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat3(
    c,  0.0,  s,
    0.0, 1.0, 0.0,
    -s,  0.0,  c
    );
}

void main() {
    float kindOfRadius = 1.0 + Color.b * 6.0;
    float rotationSpeed = 1.0 / (kindOfRadius);
    rotationSpeed *= Color.r * 2.0 - 1.0;
    vec3 rotatedPos = rotateY(TwinkleTime * 0.01 * rotationSpeed) * Position;

    vec4 viewPos = ModelViewMat * vec4(rotatedPos, 1.0);
    gl_Position = ProjMat * viewPos;

    // twinkle animation
    float twinkle = GameTime * 1000.0 * ((Color.b * 2.0) - 1.0);

    vec2 rUV = UV * 2.0 - 1.0;
    // scale
    rUV /= mix(mix(Color.b, 1.0, sin(twinkle * 0.9 + Color.a * 100.0) * 2.0 - 1.0), 1.0, 0.9);
    // rotate
    float angle = sin(twinkle * 2.0);
    float cosA = cos(angle), sinA = sin(angle);
    mat2 rotationMat = mat2(
         cosA, sinA,
        -sinA, cosA
    );
    rUV = rotationMat * rUV;

    rUV = rUV * 0.5 + 0.5;

    texCoord = rUV;
    vertexColor = texture(StarColorSampler, vec2(Color.g, Color.g) * 30.0) * Color.a;
    fanciness = Color.r;
}