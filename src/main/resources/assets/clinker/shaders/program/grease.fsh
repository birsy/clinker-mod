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
uniform vec3 LightPosition;

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

const mat3 m = mat3( 0.00,  0.80,  0.60,
-0.80,  0.36, -0.48,
-0.60, -0.48,  0.64 );

vec3 hash( vec3 p ) {
    p = vec3( dot(p,vec3(127.1,311.7, 74.7)),
    dot(p,vec3(269.5,183.3,246.1)),
    dot(p,vec3(113.5,271.9,124.6)));

    return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}
float noise( in vec3 p ) {
    //p = p * m;
    vec3 i = floor( p );
    vec3 f = fract( p );

    vec3 u = f*f*(3.0-2.0*f);

    return mix( mix( mix( dot( hash( i + vec3(0.0,0.0,0.0) ), f - vec3(0.0,0.0,0.0) ),
    dot( hash( i + vec3(1.0,0.0,0.0) ), f - vec3(1.0,0.0,0.0) ), u.x),
    mix( dot( hash( i + vec3(0.0,1.0,0.0) ), f - vec3(0.0,1.0,0.0) ),
    dot( hash( i + vec3(1.0,1.0,0.0) ), f - vec3(1.0,1.0,0.0) ), u.x), u.y),
    mix( mix( dot( hash( i + vec3(0.0,0.0,1.0) ), f - vec3(0.0,0.0,1.0) ),
    dot( hash( i + vec3(1.0,0.0,1.0) ), f - vec3(1.0,0.0,1.0) ), u.x),
    mix( dot( hash( i + vec3(0.0,1.0,1.0) ), f - vec3(0.0,1.0,1.0) ),
    dot( hash( i + vec3(1.0,1.0,1.0) ), f - vec3(1.0,1.0,1.0) ), u.x), u.y), u.z );
}
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}
float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}
float wave(float x) {
    float w = mod(x, 1.0);
    float w1 = map(w, 0.5, 1.0, 0.0, 1.0);
    float w2 = map(w, 0.0, 0.5, 1.0, 0.0);
    return max(w1, w2);
}

void main() {
    vec3 sceneColor = texture(DiffuseSampler, texCoord).xyz;

    float offset = 2.0;
    float pixelX = texCoord.x * ScreenSize.x;
    float pixelXd = pixelX + offset;
    float pixelY = texCoord.y * ScreenSize.y;
    float pixelYd = pixelY + offset;

    vec4 coords = vec4(pixelX, pixelY, pixelXd, pixelYd) / vec4(ScreenSize, ScreenSize);
    Position pixelPos = getPixelPosition(coords.xy);

    vec3 noiseCoords = vec3(floor((pixelPos.worldSpace + 0.5) * 16.0) / 16.0) * 0.5 + vec3(GameTime * 0.001, GameTime * 0.005, GameTime * 0.001);
    float n = noise(noiseCoords);

    float l = 1.0 - fract(n * 1.5 + noiseCoords.y * 0.5);
    float l1 = map(l, 0.0, 0.1, 1.0, 0.0);
    float l2 = map(l, 0.1, 1.0, 0.0, 1.0);
    l = max(l1, l2);
    l = smoothstep(0.0, 1.0, l);

    float w = wave(n * 5.0 + (sceneColor.r + sceneColor.g + sceneColor.b) * 0.2);
    vec3 col = hsv2rgb(vec3(w, 1.0, 1.0));

    fragColor = vec4(sceneColor + (col * l) * 0.45 * sceneColor, 1.0);
}
