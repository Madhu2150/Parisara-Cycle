// data/remote/GeminiService.kt
package com.parisara.cycle.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {

    // ✅ Read key directly without BuildConfig
    private val geminiApiKey: String by lazy {
        try {
            val clazz = Class.forName("com.parisara.cycle.BuildConfig")
            val field = clazz.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey    = geminiApiKey.ifBlank { "placeholder" },
            generationConfig = generationConfig {
                temperature     = 0.7f
                maxOutputTokens = 512
            }
        )
    }

    suspend fun generateRouteSummary(
        distanceKm: Double,
        durationMinutes: Int,
        hazardsOnRoute: Int,
        startName: String,
        endName: String
    ): Result<String> = runCatching {
        val prompt = """
            You are a cycling safety assistant for Indian towns.
            Generate a friendly safety summary (3-4 sentences) for:
            
            Route: $startName → $endName
            Distance: ${distanceKm}km | Duration: ~${durationMinutes} min
            Nearby hazards reported: $hazardsOnRoute
            CO₂ saved: ${(distanceKm * 120).toInt()}g
            
            Include one safety tip and encouragement.
            Keep under 80 words.
        """.trimIndent()

        model.generateContent(prompt).text
            ?: "Stay safe and enjoy your green commute! 🚴"
    }

    suspend fun generateSafetyTip(
        hazardCategory: String,
        location: String
    ): Result<String> = runCatching {
        val prompt = """
            Give a 2-sentence cycling safety tip for approaching 
            a $hazardCategory in an Indian town near $location.
        """.trimIndent()
        model.generateContent(prompt).text
            ?: "Proceed with caution in this area."
    }
}