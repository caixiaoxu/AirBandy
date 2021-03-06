uniform mat4 u_MVMatrix;
uniform mat4 u_IT_MVMatrix;
uniform mat4 u_MVPMatrix;

uniform vec3 u_VectorToLight;
uniform vec4 u_PointLightPositions[3];
uniform vec3 u_PointLightColors[3];

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 v_Color;

vec3 materizlColor;
vec4 eyeSpacePosition;
vec3 eyeSpaceNormal;

vec3 getAmbientLighting();
vec3 getDirectionalLighting();
vec3 getPointLighting();

void main() {
    materizlColor = mix(vec3(0.180, 0.467, 0.153), vec3(0.660, 0.670, 0.680), a_Position.y);
    eyeSpacePosition = u_MVMatrix * a_Position;
    eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

    v_Color = getAmbientLighting();
    v_Color += getDirectionalLighting();
    v_Color += getPointLighting();

    gl_Position = u_MVPMatrix * a_Position;

    //    v_Color = mix(vec3(0.180, 0.467, 0.153), vec3(0.660, 0.670, 0.680), a_Position.y);
    //
    //    vec3 scaleNormal = a_Normal;
    //    scaleNormal.y *=10.0;
    //    scaleNormal = normalize(scaleNormal);
    //
    //    float diffuse = max(dot(scaleNormal, u_VectorToLight), 0.0);
    //    diffuse *=0.3;
    //    v_Color *= diffuse;
    //
    //    float ambient = 0.1;
    //    v_Color += ambient;
    //
    //    gl_Position = u_Matrix * vec4(a_Position, 1.0);
}

vec3 getAmbientLighting(){
    return materizlColor * 0.1;
}

vec3 getDirectionalLighting(){
    return materizlColor * 0.3 * max(dot(eyeSpaceNormal, u_VectorToLight), 0.0);
}
vec3 getPointLighting(){
    vec3 lightingSum = vec3(0.0);
    for (int i = 0; i< 3; i++){
        vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);
        float distance = length(toPointLight);
        toPointLight = normalize(toPointLight);

        float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);
        lightingSum += (materizlColor * u_PointLightColors[i] * 5.0 * cosine)/distance;
    }
    return lightingSum;
}
