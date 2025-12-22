package com.waytube.app.preferences.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.waytube.app.preferences.domain.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import java.io.InputStream
import java.io.OutputStream

class DataStorePreferencesRepository(context: Context) : PreferencesRepository {
    private val store = context.dataStore

    override val searchHistory: Flow<List<String>> = store.data.map { it.searchHistory }

    override suspend fun saveSearch(query: String) {
        store.updateData { preferences ->
            preferences.copy(
                searchHistory = preferences.searchHistory
                    .toMutableList()
                    .apply {
                        remove(query)
                        add(0, query)
                    }
                    .take(20)
            )
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
private data class UserPreferences(
    @ProtoNumber(1)
    val searchHistory: List<String> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
private val Context.dataStore by dataStore(
    fileName = "user_preferences.pb",
    serializer = object : Serializer<UserPreferences> {
        override val defaultValue: UserPreferences = UserPreferences()

        override suspend fun readFrom(input: InputStream): UserPreferences {
            try {
                return ProtoBuf.decodeFromByteArray(
                    UserPreferences.serializer(),
                    input.readBytes()
                )
            } catch (exception: SerializationException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
            val byteArray = ProtoBuf.encodeToByteArray(UserPreferences.serializer(), t)
            output.write(byteArray)
        }
    }
)
