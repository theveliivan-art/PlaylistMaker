package com.practicum.playlistmaker.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaBinding
import com.practicum.playlistmaker.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MediaFragment : Fragment() {
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var track: Track
    private lateinit var viewModel: MediaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = requireArguments().getParcelable<Track>(EXTRA_TRACK)
            ?: run {
                findNavController().navigateUp()
                return
            }
        viewModel = getViewModel { parametersOf(track) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка «Назад» в тулбаре трека — та же навигация, что и системный Back
        binding.mediaButtonBack.setOnClickListener { findNavController().navigateUp() }


        val radiusInPx = resources.getDimensionPixelSize(R.dimen.media_track_image_radius)
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(radiusInPx))
            .placeholder(R.drawable.ic_placeholder_312)
            .error(R.drawable.ic_placeholder_312)

        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .apply(requestOptions)
            .into(binding.mediaTrackImage)

        binding.mediaTrackName.text = track.trackName
        binding.mediaTrackArtist.text = track.artistName
        binding.mediaDataDuration.text = track.trackTime
        binding.mediaDataGenre.text = track.primaryGenreName
        binding.mediaDataCountry.text = track.country

        if (track.collectionName.isEmpty()) {
            binding.mediaInfAlbum.isVisible = false
            binding.mediaDataAlbum.isVisible = false
        } else {
            binding.mediaDataAlbum.text = track.collectionName
            binding.mediaInfAlbum.isVisible = true
            binding.mediaDataAlbum.isVisible = true
        }

        if (track.releaseDate.isEmpty()) {
            binding.mediaInfYear.isVisible = false
            binding.mediaDataYear.isVisible = false
        } else {
            binding.mediaDataYear.text = extractYear(track.releaseDate)
            binding.mediaInfYear.isVisible = true
            binding.mediaDataYear.isVisible = true
        }

        binding.buttonPlay.isVisible = true
        binding.buttonPause.isVisible = false
        binding.buttonPlay.isEnabled = false

        binding.buttonPlay.setOnClickListener { viewModel.onPlayClicked() }
        binding.buttonPause.setOnClickListener { viewModel.onPauseClicked() }

        viewModel.state.observe(this) { state ->
            binding.mediaTrackDuration.text = state.currentTime
            binding.buttonPlay.isVisible = !state.isPlaying
            binding.buttonPlay.isEnabled = state.isPlayEnabled
            binding.buttonPause.isVisible = state.isPlaying
        }

    }

    private fun extractYear(releaseDate: String): String? =
        releaseDate.substringBefore("-").takeIf { it.length == 4 }

    override fun onPause() {
        super.onPause()
        if (::viewModel.isInitialized) viewModel.onScreenPaused()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val EXTRA_TRACK = "track"
    }

}