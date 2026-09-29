package com.practicum.playlistmaker.data

import java.text.SimpleDateFormat
import java.util.Locale
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.dto.TrackDTO

object TrackMapper {

    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun toDomain(dto: TrackDTO): Track = Track(
        trackId = dto.trackId,
        trackName = dto.trackName,
        artistName = dto.artistName,
        trackTime = dateFormat.format(dto.trackTimeMillis),
        artworkUrl100 = dto.artworkUrl100,
        collectionName = dto.collectionName,
        releaseDate = dto.releaseDate,
        primaryGenreName = dto.primaryGenreName,
        country = dto.country,
        previewUrl = dto.previewUrl
    )

    fun toDto(track: Track): TrackDTO = TrackDTO(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTimeMillis = parseTime(track.trackTime),
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl
    )

    private fun parseTime(time: String): Long {
        val parts = time.split(":")
        val m = parts.getOrNull(0)?.toLongOrNull() ?: 0L
        val s = parts.getOrNull(1)?.toLongOrNull() ?: 0L
        return (m * 60 + s) * 1000L
    }
}