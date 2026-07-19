struct VSOut
{
    float4 position : SV_POSITION;
    float2 uv       : TEXCOORD0;
};

VSOut mainVS(uint vertexID : SV_VertexID)
{
    VSOut o;
    float2 uv = float2((vertexID << 1) & 2, vertexID & 2);
    o.position = float4(uv * float2(2.0, -2.0) + float2(-1.0, 1.0), 0.0, 1.0);
    o.uv = float2(uv.x, 1.0 - uv.y);
    return o;
}

Texture2D sourceTexture : register(t0);
SamplerState pointSampler
{
    Filter = MIN_MAG_MIP_POINT;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

float4 mainPS(VSOut input) : SV_Target
{
    return sourceTexture.SampleLevel(pointSampler, input.uv, 0.0);
}