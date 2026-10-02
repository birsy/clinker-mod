#include veil:fog
#include veil:space_helper

uniform vec2 ScreenResolution;

uniform sampler2D Sampler0;
uniform sampler2D DiffuseDepthSampler;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in vec2 texCoord0;
in float vertexDistance;
in float angle;

out vec4 fragColor;

mat2 rotate2d(float a) {
    float c = cos(a);
    float s = sin(a);
    return mat2(c, -s, s, c);
}

void main() {
    mat2 rot = rotate2d(angle), irot = rotate2d(-angle);

    vec2 uv = floor(texCoord0 * 8.0) / 8.0;
    uv = ((uv * 2.0 - 1.0) * rot) * 0.5 + 0.5;

    vec3 normal = normalize(texture(Sampler0, uv).rgb * 2.0 - 1.0);
    vec3 normalX = vec3(vec2(1.0, 0.0) * irot, 0).xzy,
         normalY = vec3(0.0, 1.0, 0.0),
         normalZ = cross(normalX, normalY);
    vec2 transformedNormal = normalX.xz * normal.x + normalZ.xz * normal.y;
    float shading = dot(transformedNormal, -normalize(vec2(0.5, 0.5)));

    vec2 screenCoords = gl_FragCoord.xy / ScreenResolution;
    float sceneDistance = texelFetch(DiffuseDepthSampler, ivec2(gl_FragCoord.xy), 0).r;
    sceneDistance = length(screenToViewSpace(screenCoords, sceneDistance).xyz);
    float geometryDistance = length(screenToViewSpace(screenCoords, gl_FragCoord.z).xyz);
    float distanceThroughGeometry = sceneDistance - geometryDistance;
    if (distanceThroughGeometry > 0.1) discard;

    fragColor = vec4(vec3(step(0.0, shading)), abs(shading)) * vertexColor;
}


