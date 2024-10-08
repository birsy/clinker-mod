#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform float TwinkleTime;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord0;
out vec4 vertexColor;

float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    float twinkle = TwinkleTime * ((Color.b * 2.0) - 1.0);
    float uvRotation = sin(twinkle);

    vec2 rotatedUV = UV0;
    rotatedUV = (rotatedUV * 2.0) - vec2(1.0);
    rotatedUV *= mix(mix(0.7, 1.3, (sin(twinkle*0.9 + Color.b) + 1.0) / 2.0), 1.0, Color.b);
    rotatedUV = vec2(rotatedUV.x * cos(uvRotation) - rotatedUV.y * sin(uvRotation),
                     rotatedUV.y * cos(uvRotation) + rotatedUV.x * sin(uvRotation));
    rotatedUV = (rotatedUV + vec2(1.0)) * 0.5;


    texCoord0 = rotatedUV;
    vertexColor = vec4(Color.rga, 1.0F);
}
