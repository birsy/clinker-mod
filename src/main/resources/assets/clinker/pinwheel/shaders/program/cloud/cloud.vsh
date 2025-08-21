
layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 NormalMat;

out vec3 position;
out vec4 vertexColor;
out vec2 uv;
out float dist;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
   
    position = Position;
    vertexColor = Color;
    uv = UV0;
    dist = length((ModelViewMat * vec4(Position, 1.0)).xyz);
}


















