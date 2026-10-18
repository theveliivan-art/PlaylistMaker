package com.practicum.playlistmaker.ui.mediateka

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding

class PlaylistFragment: Fragment() {
    companion object {

        fun newInstance() = PlaylistFragment().apply {
            arguments = Bundle()
        }
    }
    private val playListViewModel: PlaylistViewModel by viewModel()

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            playlistButtonAdd.visibility = View.VISIBLE
            playlistImageEmpty.visibility = View.VISIBLE
            playlistTextEmpty.visibility = View.VISIBLE
        }
    }
}