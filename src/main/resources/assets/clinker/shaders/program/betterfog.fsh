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

float hash(float p) { p = fract(p * 0.011); p *= p + 7.5; p *= p + p; return fract(p); }
float hash(vec2 p) {vec3 p3 = fract(vec3(p.xyx) * 0.13); p3 += dot(p3, p3.yzx + 3.333); return fract((p3.x + p3.y) * p3.z); }
float noise(vec3 x) {
    const vec3 step = vec3(110, 241, 171);

    vec3 i = floor(x);
    vec3 f = fract(x);

    // For performance, compute the base input to a 1D hash from the integer part of the argument and the
    // incremental change to the 1D based on the 3D -> 1D wrapping
    float n = dot(i, step);

    vec3 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix( hash(n + dot(step, vec3(0, 0, 0))), hash(n + dot(step, vec3(1, 0, 0))), u.x),
    mix( hash(n + dot(step, vec3(0, 1, 0))), hash(n + dot(step, vec3(1, 1, 0))), u.x), u.y),
    mix(mix( hash(n + dot(step, vec3(0, 0, 1))), hash(n + dot(step, vec3(1, 0, 1))), u.x),
    mix( hash(n + dot(step, vec3(0, 1, 1))), hash(n + dot(step, vec3(1, 1, 1))), u.x), u.y), u.z);
}

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

float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}
float sampleDensity(vec3 position) {
    vec3 samplePosition = floor(position * 2.0) / 2.0;
    float nDistoriton = noise(samplePosition * 0.2 + GameTime * -0.01);
    float n = (noise(samplePosition * 0.3 + GameTime * 0.02 + nDistoriton * 3.0) + 1.0) / 2.0;
    n *= n * n * n * n * n * n * 3.0;
    float v = max(min(map(samplePosition.y, 384.0, 400.0, 1.0, 0.0), 1.0), 0.0);
    v *= max(min(map(samplePosition.y, 280.0, 310.0, 0.0, 1.0), 1.0), 0.0);
    n += v * v;
    n /= 2.0;
    return v * v * n;
}
vec3 sampleCloudColor(vec3 position, float density) {
    float v = max(min(map(position.y, 160.0, 220.0, 1.0, 0.0), 1.0), 0.0);
    return mix(vec3(84.0 / 255.0, 70.0 / 255.0, 57.0 / 255.0), vec3(43.0 / 255.0, 27.0 / 255.0, 53.0 / 255.0), 0.0); //replace with sample from cloud map
}


void main() {
    float aspectRatio = ScreenSize.x / ScreenSize.y;
    float depth = texture(DiffuseDepthSampler, texCoord).x;
    float distanceFromCamera = distToCameraDistance(depth, NearPlaneDistance, FarPlaneDistance, texCoord, FOV, aspectRatio);
    vec2 rayOffset = texCoord * 2.0 - 1.0;
    vec3 ray = normalize(getPointOnNearPlane(rayOffset, NearPlaneDistance, FarPlaneDistance, FOV, aspectRatio, CameraForward, CameraLeft, CameraUp));

    distanceFromCamera *= 0.0005;
    vec3 wavelengths = vec3(700, 530, 440);
    float scatteringStrength = 20.0;
    vec3 p4 = 400.0 / wavelengths;
    vec3 scatteringCoefficients = p4 * p4 * p4 * p4 * scatteringStrength;
    vec3 transmittance = 1 - exp(-distanceFromCamera * scatteringCoefficients);

    vec4 sceneColor = texture(DiffuseSampler, texCoord);
    vec3 color = sceneColor.rgb * exp(-distanceFromCamera * scatteringStrength * 0.25) + transmittance;

    fragColor = vec4(color, 1.0);

}
