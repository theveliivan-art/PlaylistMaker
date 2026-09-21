package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.network.ITunesSearchAPI
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.data.dto.TracksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksRepositoryImpl(private val api: ITunesSearchAPI):TracksRepository {

    override fun searchTracks(
        term: String,
        onSuccess: (List<Track>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        api.search(term).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty()
                    onSuccess(results.map(TrackMapper::toDomain))
                } else {
                    onError(Throwable("HTTP ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                onError(t)
            }
        })
    }
}