package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinDao {

    // Profile queries
    @Query("SELECT * FROM skin_profile WHERE id = 1 LIMIT 1")
    fun getSkinProfileFlow(): Flow<SkinProfile?>

    @Query("SELECT * FROM skin_profile WHERE id = 1 LIMIT 1")
    suspend fun getSkinProfileDirect(): SkinProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkinProfile(profile: SkinProfile)

    @Query("DELETE FROM skin_profile WHERE id = 1")
    suspend fun deleteSkinProfile()

    // Diary queries
    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    fun getAllDiaryEntriesFlow(): Flow<List<DiaryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(entry: DiaryEntry)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteDiaryEntryById(id: Int)

    // Recommendation queries
    @Query("SELECT * FROM skin_type_recommendations WHERE skinType = :skinType LIMIT 1")
    suspend fun getRecommendationForSkinType(skinType: String): SkinTypeRecommendation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(recommendation: SkinTypeRecommendation)

    @Query("DELETE FROM skin_type_recommendations")
    suspend fun clearRecommendations()

    // Inventory queries
    @Query("SELECT * FROM inventory_items ORDER BY openedDate DESC")
    fun getAllInventoryItemsFlow(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem)

    @Query("DELETE FROM inventory_items WHERE id = :id")
    suspend fun deleteInventoryItemById(id: Int)

    @Query("DELETE FROM inventory_items")
    suspend fun clearInventory()
}
