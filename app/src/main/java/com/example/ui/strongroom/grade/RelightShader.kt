package com.example.ui.strongroom.grade

/**
 * STAGE 10 PRE-BUILD — RELIGHT AS A PRODUCTION SHADER (BUILD-SR-10).
 *
 * The AGSL (Android Graphics Shading Language) source the app runs on
 * API 33+ to execute [GradePipeline]'s law on the GPU: the content
 * shader's own luminance gradient, projected onto the state's light
 * direction, quantised to whole eighths, blended toward the state's
 * lit/shadow partner inks. The uniform contract below is the whole
 * interface — a surface sets the state's row (from
 * [GradePipeline.paramsFor], via [uniformsFor]) and renders; it does
 * not write shading logic of its own.
 *
 * Below API 33, and wherever a shader compile fails, the fallback IS
 * [GradeFieldRenderer] — the same law struck on the CPU. The shader
 * and the reference renderer are one law in two executions; the
 * tests pin the shader source to the law's load-bearing terms
 * (quantisation, partner uniforms, no white, no green) so the two
 * cannot drift silently.
 *
 * UNPROVEN-ON-DEVICE (G-7): nothing here claims how the OnePlus
 * compiles or runs AGSL — judged in the installable build at
 * Stage 10 composition, per AL-0063.
 */
object RelightShader {

    /** The uniform names the AGSL source declares — the contract. */
    val UNIFORMS: List<String> = listOf(
        "uLightDir", "uLitPartner", "uShadowPartner",
        "uLitCapEighths", "uSubtractionOnly", "uTexel",
    )

    /** AGSL source. Load-bearing terms, by name: `floor(... + 0.5)`
     *  over eighths is the quantisation (a continuous blend here
     *  would fork P0's law); the partner colours arrive ONLY as
     *  uniforms struck from named inks — the source names no colour
     *  literal of its own, so it cannot invent one. */
    const val AGSL_SOURCE: String = """
uniform shader content;
uniform float2 uLightDir;
uniform float3 uLitPartner;
uniform float3 uShadowPartner;
uniform float uLitCapEighths;
uniform float uSubtractionOnly;
uniform float2 uTexel;

float lum(float3 c) {
    float3 lin = pow((c + 0.055) / 1.055, float3(2.4));
    return 0.2126 * lin.r + 0.7152 * lin.g + 0.0722 * lin.b;
}

half4 main(float2 xy) {
    half4 centre = content.eval(xy);
    float gx = lum(content.eval(xy + float2(uTexel.x, 0.0)).rgb)
             - lum(content.eval(xy - float2(uTexel.x, 0.0)).rgb);
    float gy = lum(content.eval(xy + float2(0.0, uTexel.y)).rgb)
             - lum(content.eval(xy - float2(0.0, uTexel.y)).rgb);
    float g = clamp(gx * uLightDir.x + gy * uLightDir.y, -1.0, 1.0);
    float3 rgb = centre.rgb;
    if (g > 0.0 && uSubtractionOnly < 0.5) {
        float steps = floor(g * uLitCapEighths + 0.5);
        rgb = mix(rgb, uLitPartner, clamp(steps, 0.0, 8.0) / 8.0);
    } else if (g < 0.0) {
        float steps = floor(-g * 8.0 + 0.5);
        rgb = mix(rgb, uShadowPartner, clamp(steps, 0.0, 8.0) / 8.0);
    }
    return half4(rgb, centre.a);
}
"""

    /** One state's uniform row, struck from [GradePipeline]'s filed
     *  parameters + the surface's own partner inks for a mark class.
     *  Returns name → value as data so the Compose seam (Stage 10
     *  composition) sets uniforms without re-deriving the law. */
    fun uniformsFor(
        state: GradeState,
        litPartner: GradeColour,
        shadowPartner: GradeColour,
        texelX: Float,
        texelY: Float,
    ): Map<String, FloatArray> {
        val params = GradePipeline.paramsFor(state)
        fun rgb(c: GradeColour): FloatArray =
            floatArrayOf(c.r / 255f, c.g / 255f, c.b / 255f)
        return mapOf(
            "uLightDir" to floatArrayOf(params.lightDirectionX, params.lightDirectionY),
            "uLitPartner" to rgb(litPartner),
            "uShadowPartner" to rgb(shadowPartner),
            "uLitCapEighths" to floatArrayOf(params.maxLitEighths.toFloat()),
            "uSubtractionOnly" to floatArrayOf(if (params.subtractionOnly) 1f else 0f),
            "uTexel" to floatArrayOf(texelX, texelY),
        )
    }
}
