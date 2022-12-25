#version 150

uniform mat4 ProjMat;
uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D LightSampler;
uniform vec3 ColorModulator;
uniform vec3 ChunkOffset;
uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;

uniform sampler2D ChunkLightSampler;

uniform vec3 CameraPosition;
uniform vec3 CameraForward;
uniform vec3 CameraLeft;
uniform vec3 CameraUp;
uniform float NearPlaneDistance;
uniform float FarPlaneDistance;
uniform float FOV;
uniform vec2 ScreenSize;

in vec2 texCoord;
out vec4 fragColor;

float linearizeDepth(float d,float zNear,float zFar){
    float z_n = 2.0 * d - 1.0;
    return 2.0 * zNear * zFar / (zFar + zNear - z_n * (zFar - zNear));
}

//magic numbers, copied from camera code...
vec3 getPointOnNearPlane(vec2 rayOffset, float nearPlaneDistance, float farPlaneDistance, float fov, float aspectRatio, vec3 forward, vec3 left, vec3 up) {
    float d1 = tan((fov * (3.141592 / 180.0)) / 2.0) * nearPlaneDistance;
    float d2 = d1 * aspectRatio;
    return (forward * nearPlaneDistance) + (up * rayOffset.y * d1) + (left * -rayOffset.x * d2);
}
//stolen from fufo code
//credit shBLOCK i think
float distToCameraDistance(float depth, float near, float far, vec2 texCoord, float fov, float aspectRatio) {
    return length(vec3(1.0, (2.0 * texCoord - 1.) * vec2(aspectRatio, 1.0) * tan((fov * (3.141592 / 180.0)) / 2.0)) * linearizeDepth(depth, near, far));
}

struct Position { vec3 localSpace; vec3 worldSpace;  };

vec3 getPixelRay(vec2 textureCoord) {
    float aspectRatio = ScreenSize.x / ScreenSize.y;
    vec2 rayOffset = textureCoord * 2.0 - 1.0;
    return normalize(getPointOnNearPlane(rayOffset, NearPlaneDistance, FarPlaneDistance, FOV, aspectRatio, CameraForward, CameraLeft, CameraUp));
}

Position getPixelPosition(vec2 textureCoord) {
    vec3 ray = getPixelRay(textureCoord);
    float depth = texture(DiffuseDepthSampler, textureCoord).x;
    float aspectRatio = ScreenSize.x / ScreenSize.y;
    float distanceFromCamera = distToCameraDistance(depth, NearPlaneDistance, FarPlaneDistance, textureCoord, FOV, aspectRatio);

    vec3 localSpace = ray * distanceFromCamera;
    vec3 worldSpace = localSpace + CameraPosition;

    return Position(localSpace, worldSpace);
}


float smin( float a, float b, float k ) {
    float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
    return mix( b, a, h ) - k*h*(1.0-h);
}

vec3 getLightColor(vec3 pixelWorldSpacePos, vec3 pixelWorldSpaceNormal, vec3 lightPos, vec3 lightColor, float lightIntensity, float ambientIntensity, float maxLightIntensity, bool hasFalloff) {
    // dot product of normal and light direction so light only appears if the surface is facing it
    float light = dot(normalize(lightPos - pixelWorldSpacePos), pixelWorldSpaceNormal);
    if (light < 0.0) light = 0.0;
    light *= lightIntensity;
    light += ambientIntensity;
    if (hasFalloff) { float distanceFromLight = length(lightPos -pixelWorldSpacePos); light /= distanceFromLight * distanceFromLight; }
    light = smin(light, maxLightIntensity, 6.0);
    return lightColor * max(0, light);
}

vec3 getDirectionalLightColor(vec3 pixelWorldSpacePos, vec3 pixelWorldSpaceNormal, vec3 lightDirection, vec3 lightColor, float lightIntensity, float ambientIntensity) {
    // dot product of normal and light direction so light only appears if the surface is facing it
    float light = dot(normalize(lightDirection), pixelWorldSpaceNormal);
    if (light < 0.0) light = 0.0;
    light *= lightIntensity;
    light += ambientIntensity;
    return lightColor * max(0, light);
}

vec3 normalFromThreePoints(vec3 a, vec3 b, vec3 c, vec3 cameraRay) {
    vec3 dir = cross(b - a, c - a);
    vec3 norm = normalize(dir);
    if (dot(cameraRay, norm) > 0.0) norm *= -1.0;
    return norm;
}

vec3 normalFromFourPoints(vec3 a, vec3 b, vec3 c, vec3 d, vec3 cameraRay) {
    vec3 normalA = normalFromThreePoints(a, b, c, cameraRay);
    vec3 normalB = normalFromThreePoints(d, c, b, cameraRay);
    return normalize((normalA + normalB) * 0.5);
}


void main() {
    float offset = 1.0;
    float pixelX = texCoord.x * ScreenSize.x;
    float pixelXd = pixelX + offset;
    float pixelY = texCoord.y * ScreenSize.y;
    float pixelYd = pixelY + offset;

    vec4 coords = vec4(pixelX, pixelY, pixelXd, pixelYd) / vec4(ScreenSize, ScreenSize);
    Position pixelPos = getPixelPosition(coords.xy);

    vec3 a = pixelPos.localSpace;
    vec3 b = getPixelPosition(coords.xw).localSpace;
    vec3 c = getPixelPosition(coords.zy).localSpace;
    vec3 d = getPixelPosition(coords.zw).localSpace;

    vec3 normal = normalFromFourPoints(a, b, c, d, getPixelRay(coords.xy));

    vec3 color = (normal + 1.0) / 2.0;

    vec3 sceneColor = texture(DiffuseSampler, texCoord).xyz;

    //vec3 light1 = getLightColor(pixelPos.worldSpace, normal, CameraPosition, vec3(1.0, 96.0F / 255.0, 0.0F), 16.0, 3.0, 5.0, true);
    vec3 light2 = getLightColor(pixelPos.worldSpace, normal, vec3(4205.0, -4.0, -8729.0), vec3(96.0F / 255.0, 96.0F / 255.0, 1.0F), 16.0, 3.0, 5.0, true);
    //vec3 light2 = getDirectionalLightColor(pixelPos.worldSpace, normal, vec3(0.5, 1.0, 0.5), vec3(1.0, 1.0, 1.0), 1.0, 0.8);
    //Multiply by the s
    vec3 totalLightEffect = light2 * sceneColor;
    fragColor = vec4(sceneColor + totalLightEffect, 1.0);
}
