layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

out vec4 vertColor;
out vec2 texCoord;

void main() {
    gl_Position = vec4(Position, 1.0);
    vertColor = Color;
    texCoord = UV0;
}







