#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 30) out;

in VS_OUT {
    int waterId;
    vec3 worldColorModifier;
} gs_in[];

uniform mat4 g_viewProjection;

const vec4 up = vec4(0, 1, 0, 0);
const vec4 right = vec4(1, 0, 0, 0);
const vec4 back = vec4(0, 0, 1, 0);

out vec3 color;

void main() {
    color = gs_in[0].worldColorModifier;
    vec4 position = gl_in[0].gl_Position;

    const float waterLevel = 3.5;
    const float flooredWaterLevel = floor(waterLevel);
    const float ceiledWaterLevel = ceil(waterLevel);

    //if(position.y + 1 <= flooredWaterLevel)
    //    return;

    int waterId = gs_in[0].waterId;
    bool shouldRenderXPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderXMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderZPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderZMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderYPlus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;
    bool shouldRenderYMinus = (waterId & 1) > 0 ? true : false;
    waterId = waterId >> 1;

    color = vec3(log2(gs_in[0].waterId) / 32, 0, 0);

    if((gs_in[0].waterId & (1<<30)) != 0)
        color = vec3(0, 0, 1);


    if(shouldRenderXMinus) {
        float height = position.y + 1 > waterLevel ? waterLevel - position.y : 1.0f;
        vec4 _up = height * up;
        // TODO: do color stuff
        gl_Position = g_viewProjection * position;
        EmitVertex();
        gl_Position = g_viewProjection * (position + _up);
        EmitVertex();
        gl_Position = g_viewProjection * (position + back);
        EmitVertex();
        EndPrimitive();

        gl_Position = g_viewProjection * (position + _up);
        EmitVertex();
        gl_Position = g_viewProjection * (position + _up + back);
        EmitVertex();
        gl_Position = g_viewProjection * (position + back);
        EmitVertex();
        EndPrimitive();
    }


    gl_Position = g_viewProjection * (position + up);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + right);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + back);
    EmitVertex();
    EndPrimitive();

    gl_Position = g_viewProjection * (position + up + back);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + right + back);
    EmitVertex();
    gl_Position = g_viewProjection * (position + up + right);
    EmitVertex();
    EndPrimitive();


}