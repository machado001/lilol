package com.machado001.lilol.rotation.model.repository

import com.machado001.lilol.rotation.model.dto.DataDragonDto
import com.machado001.lilol.rotation.model.dto.RotationsDto

/**
 * Interface for managing game data related to League of Legends champions and rotations.
 * This interface combines functionality from RotationRepository and DataDragonRepository into one source.
 * It provides methods for retrieving information about supported languages, the current game version,
 * accessing DataDragon resources, and fetching rotation details.
 */
interface ChampionsManager {
    /**
     * Retrieves a list of supported languages for League of Legends data.
     * @return A list of supported language codes.
     */
    suspend fun getSupportedLanguages(): List<String>

    /**
     * Retrieves the current version of League of Legends data.
     * @return The current game version.
     */
    suspend fun getCurrentVersion(): String

    /**
     * Retrieves DataDragon resources for a specific language.
     * @param language The language for which to retrieve DataDragon data.
     * @return A DataDragonDto object containing game data resources for the specified language.
     */
    suspend fun getDataDragon(language: String): DataDragonDto

    /**
     * Retrieves information about the champion rotations in League of Legends.
     * @return A RotationsDto object containing details about the champion rotations.
     */
    suspend fun getRotations(): RotationsDto
}

