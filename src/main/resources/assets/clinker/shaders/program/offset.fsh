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
uniform vec3 LightPosition;

in vec2 texCoord;
out vec4 fragColor;

// stuff for extracting the world space position from some camera parameters
// stolen from fufo code
// credit shBLOCK i think
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

float distToCameraDistance(float depth, float near, float far, vec2 texCoord, float fov, float aspectRatio) {
    return length(vec3(1.0, (2.0 * texCoord - 1.) * vec2(aspectRatio, 1.0) * tan((fov * (3.141592 / 180.0)) / 2.0)) * linearizeDepth(depth, near, far));
}
// end fancy world space extraction stuff


float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

struct BlackHoleSample{ vec3 offset; bool isHole; };

vec3 sampleOffset(vec3 position) {
    vec3 samplePosition = position;
    vec3 offset = samplePosition - LightPosition;
    float distance = length(offset) * 120.0;
    float intensity = 1.0 / distance;
    intensity *= 1.0 / length(samplePosition - CameraPosition);
    return normalize(offset) * intensity;
}

// raymarching!
BlackHoleSample sampleVolume(float distance, int steps, vec3 ray, vec3 worldPosition) {
    float maxDistance = min(distance, 128);
    float stepSize = (maxDistance / steps);
    vec3 rayPos = ray + worldPosition;
    vec3 rayOffset = normalize(ray) * stepSize;

    vec3 offsetSample = vec3(0.0);
    vec3 previousOffsetSample = vec3(0.0);
    vec3 totalOffset = vec3(0.0);
    int stepCount = 0;

    while (stepCount < steps) {
        rayOffset = normalize(ray) * (length(rayPos - LightPosition) - 0.25);
        if (length(worldPosition - (rayPos + rayOffset)) > distance) break;
        if (length(rayOffset) < 0.01) return BlackHoleSample(vec3(0.0), true);
        rayPos += rayOffset;
        stepCount++;
    }

    stepCount = 0;
    rayPos = ray + worldPosition;
    rayOffset = normalize(ray) * stepSize;
    while (stepCount < steps) {
        previousOffsetSample = offsetSample;
        offsetSample = sampleOffset(rayPos);

        // trapezoidal integration provides more accurate density estimates.
        totalOffset += 0.5 * (previousOffsetSample + offsetSample) * stepSize;

        rayPos += rayOffset;
        stepCount++;
    }

    return BlackHoleSample(totalOffset, false);
}

void main() {
    float aspectRatio = ScreenSize.x / ScreenSize.y;
    float depth = texture(DiffuseDepthSampler, texCoord).x;
    float distanceFromCamera = distToCameraDistance(depth, NearPlaneDistance, FarPlaneDistance, texCoord, FOV, aspectRatio);
    vec2 rayOffset = texCoord * 2.0 - 1.0;
    vec3 ray = normalize(getPointOnNearPlane(rayOffset, NearPlaneDistance, FarPlaneDistance, FOV, aspectRatio, CameraForward, CameraLeft, CameraUp));


    int steps = 128;

    BlackHoleSample s = sampleVolume(distanceFromCamera, steps, ray, CameraPosition);
    vec3 offset = s.offset;
    vec3 nCL = normalize(CameraLeft);
    vec3 nCU = normalize(CameraUp);
    vec2 ssOffset = vec2(dot(nCL, offset), dot(nCU, offset));
    vec2 totalOffset = ssOffset * vec2(aspectRatio, 1.0) * -5.0;

    fragColor = s.isHole ? vec4(0.0) : texture(DiffuseSampler, texCoord + totalOffset);
}
