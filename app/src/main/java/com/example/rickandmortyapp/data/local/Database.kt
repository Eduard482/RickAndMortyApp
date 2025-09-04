package com.example.rickandmortyapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val location: String
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val characterId: Int
)

@Entity(
    tableName = "episodes_seen",
    primaryKeys = ["characterId", "episode"]
)
data class EpisodeSeenEntity(
    val characterId: Int,
    val episode: String
)

@Entity(
    tableName = "episodes",
    primaryKeys = ["characterId", "url"]
)
data class EpisodeEntity(
    val characterId: Int,
    val url: String,
    val name: String
)

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAll(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CharacterEntity)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE characterId = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun toggle(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE characterId = :id")
    suspend fun remove(id: Int)
}

@Dao
interface EpisodeSeenDao {
    @Query("SELECT episode FROM episodes_seen WHERE characterId = :id")
    fun getForCharacter(id: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(entity: EpisodeSeenEntity)
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE characterId = :id")
    suspend fun getEpisodesForCharacter(id: Int): List<EpisodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<EpisodeEntity>)
}

@Database(
    entities = [CharacterEntity::class, FavoriteEntity::class, EpisodeSeenEntity::class,
        EpisodeEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun episodeSeenDao(): EpisodeSeenDao
    abstract fun episodeDao(): EpisodeDao
}