#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform float TwinkleTime;
uniform float Rotation;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord0;
out vec4 vertexColor;

void main() {
    // rotate the position
    float rot = ((Color.r*2.0) - 1.0) * Rotation;
    vec3 pos = Position;
    pos = vec3(pos.x * cos(rot) - pos.z * sin(rot),
               pos.y,
               pos.z * cos(rot) + pos.x * sin(rot));

    pos.y += sin(Rotation * 5.0 * ((Color.b * 2.0) - 1.0)) * 50.0F;

    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    // brightness and fancy are packed into a single color channel. so extract that.
    float brightness = Color.a * 2.0;
    float fancy = 0.0;
    if (Color.a > 0.5) {
        brightness = (Color.a - 0.5) * 2.0;
        fancy = 1.0F;
    }

    // twinkly...
    float twinkle = TwinkleTime * ((Color.b * 2.0) - 1.0);
    float uvRotation = sin(twinkle);

    vec2 rotatedUV = UV0;
    rotatedUV = (rotatedUV * 2.0) - vec2(1.0);
    rotatedUV *= mix(mix(0.7, 1.3, (sin(twinkle*0.9 + Color.b) + 1.0) / 2.0), 1.0, Color.b);
    rotatedUV = vec2(rotatedUV.x * cos(uvRotation) - rotatedUV.y * sin(uvRotation),
                     rotatedUV.y * cos(uvRotation) + rotatedUV.x * sin(uvRotation));
    rotatedUV = (rotatedUV + vec2(1.0)) * 0.5;
    texCoord0 = rotatedUV;

    vertexColor = vec4(brightness, Color.g, fancy, 1.0F);
}
