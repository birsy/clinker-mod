#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;
in uint BoneIndex;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform mat3 NormalMatt;

uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

uniform ivec2 OverlayCoordinates;
uniform ivec2 LightmapCoordinates;

uniform mat4 BoneTransforms[2];

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

void main() {
    vec3 poseSpacePosition = (BoneTransforms[BoneIndex] * vec4(Position, 1.0)).xyz;
    gl_Position = ProjMat * ModelViewMat * vec4(poseSpacePosition, 1.0);
    normal = vec4(NormalMatt * Normal, 0.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * poseSpacePosition, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, normal.xyz, Color);
    lightMapColor = texelFetch(Sampler2, LightmapCoordinates, 0);
    overlayColor = texelFetch(Sampler1, OverlayCoordinates, 0);
    texCoord0 = UV0;
}
