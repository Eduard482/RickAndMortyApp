package com.example.rickandmortyapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmortyapp.data.local.AppDatabase
import com.example.rickandmortyapp.data.local.CharacterEntity
import com.example.rickandmortyapp.data.local.EpisodeEntity
import com.example.rickandmortyapp.data.local.EpisodeSeenEntity
import com.example.rickandmortyapp.data.local.FavoriteEntity
import com.example.rickandmortyapp.data.mappers.toDomain
import com.example.rickandmortyapp.data.remote.RickAndMortyApi
import com.example.rickandmortyapp.domain.Character
import com.example.rickandmortyapp.domain.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterPagingSource(
    private val api: RickAndMortyApi,
    private val db: AppDatabase,
    private val name: String?,
    private val status: String?,
    private val species: String?
) : PagingSource<Int, Character>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val page = params.key ?: 1
            val res = api.getCharacters(page, name, status, species)
            val characters = res.results.map {
                val domain = Character(
                    id = it.id,
                    name = it.name,
                    status = it.status,
                    species = it.species,
                    gender = it.gender,
                    image = it.image,
                    location = it.location.name,
                    episodes = it.episode
                )
                // cache basic info
                db.characterDao().upsert(
                    CharacterEntity(
                        id = it.id,
                        name = it.name,
                        status = it.status,
                        species = it.species,
                        gender = it.gender,
                        image = it.image,
                        location = it.location.name
                    )
                )
                domain
            }
            LoadResult.Page(
                data = characters,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (res.info.next == null) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}

class CharacterRepositoryImpl(
    private val api: RickAndMortyApi,
    private val db: AppDatabase
) : CharacterRepository {
    override fun getCharactersPaged(
        name: String?,
        status: String?,
        species: String?
    ): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { CharacterPagingSource(api, db, name, status, species) }
        ).flow
    }

    override suspend fun getCharacter(id: Int): Character {
        val cached = db.characterDao().getById(id)
        val cachedEpisodes = db.episodeDao().getEpisodesForCharacter(id)

        return if (cached != null && cachedEpisodes.isNotEmpty()) {
            // ✅ usar cache (personaje + episodios)
            Character(
                id = cached.id,
                name = cached.name,
                status = cached.status,
                species = cached.species,
                gender = cached.gender,
                image = cached.image,
                location = cached.location,
                episodes = cachedEpisodes.map { it.name }  // 👈 ahora sí
            )
        } else {
            // ✅ pedir a la API si no hay cache o no hay episodios guardados
            val dto = api.getCharacter(id)

            // Guardar personaje
            db.characterDao().upsert(
                CharacterEntity(
                    id = dto.id,
                    name = dto.name,
                    status = dto.status,
                    species = dto.species,
                    gender = dto.gender,
                    image = dto.image,
                    location = dto.location.name
                )
            )

            // Guardar episodios (solo número o URL)
            val episodes = dto.episode.map { epUrl ->
                EpisodeEntity(
                    characterId = dto.id,
                    url = epUrl,
                    name = epUrl.substringAfterLast("/") // 👈 Ej: "23"
                )
            }
            db.episodeDao().upsertAll(episodes)

            Character(
                id = dto.id,
                name = dto.name,
                status = dto.status,
                species = dto.species,
                gender = dto.gender,
                image = dto.image,
                location = dto.location.name,
                episodes = episodes.map { it.name }
            )
        }
    }
    override fun getFavorites(): Flow<List<Character>> {
        return db.favoriteDao().getAll()
            .map { favs ->
                favs.mapNotNull { fav ->
                    db.characterDao().getById(fav.characterId)?.toDomain()
                }
            }
    }

    override suspend fun toggleFavorite(id: Int) {
        val isFav = db.favoriteDao().isFavorite(id)
        if (isFav) db.favoriteDao().remove(id)
        else db.favoriteDao().toggle(FavoriteEntity(id))
    }

    override suspend fun isFavorite(id: Int): Boolean = db.favoriteDao().isFavorite(id)
    override fun episodesSeen(id: Int): Flow<List<String>> {
        // Devuelve en tiempo real los episodios que el usuario marcó como vistos
        return db.episodeSeenDao().getForCharacter(id)
    }

    override suspend fun markEpisodeSeen(id: Int, episode: String) {
        // Guarda el episodio como visto en la tabla episodes_seen
        db.episodeSeenDao().add(EpisodeSeenEntity(id, episode))
    }
}