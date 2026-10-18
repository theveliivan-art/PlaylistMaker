package com.practicum.playlistmaker.ui.mediateka

import androidx.fragment.app.Fragment
import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding

class FavoriteFragment: Fragment() {
    companion object {

        fun newInstance() = FavoriteFragment().apply {
            arguments = Bundle()
        }
    }
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            favImageEmpty.visibility = View.VISIBLE
            favTextEmpty.visibility = View.VISIBLE
        }
    }
}