package com.kl3jvi.animity.view.fragments.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.request.CachePolicy

import com.kl3jvi.animity.databinding.FragmentDetailsBinding
import com.kl3jvi.animity.model.network.ApiHelper
import com.kl3jvi.animity.model.network.RetrofitBuilder
import com.kl3jvi.animity.utils.Constants
import com.kl3jvi.animity.utils.Status
import com.kl3jvi.animity.view.adapters.CustomEpisodeAdapter


class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels {
        DetailsViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
    }
    private lateinit var episodeAdapter: CustomEpisodeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DetailsFragmentArgs by navArgs()
        val randomNum = (0..4).random()

        args.animeDetails.let { animeInfo ->
            binding.appBarImage.load(Constants.DETAILS_BACKGROUND[randomNum]){
                crossfade(true)
                diskCachePolicy(CachePolicy.ENABLED)
            }

            binding.icon.load(animeInfo.imageUrl){
                crossfade(true)
                diskCachePolicy(CachePolicy.ENABLED)
            }

            binding.animeTitleDetail.text = animeInfo.title

            animeInfo.categoryUrl?.let { url ->
                fetchAnimeInfo(url)
            }
        }

        binding.episodeListRv.layoutManager = GridLayoutManager(requireContext(), 2)
        episodeAdapter = CustomEpisodeAdapter(this)
        binding.episodeListRv.adapter = episodeAdapter


    }


    private fun fetchEpisodeList(id: String, endEpisode: String, alias: String) {
        viewModel.fetchEpisodeList(id, endEpisode, alias)
            .observe(viewLifecycleOwner, { episodeListResponse ->
                episodeListResponse.data?.let { episodeList ->
                    episodeAdapter.getEpisodeInfo(episodeList)
                }
            })
    }


    private fun fetchAnimeInfo(url: String) {
        viewModel.fetchAnimeInfo(url)
        viewModel.fetchAnimeInfo(url).observe(viewLifecycleOwner, { res ->
            res?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { info ->
                            binding.expandTextView.text = info.plotSummary
                            binding.releaseDate.text = info.releasedTime
                            binding.status.text = info.status
                            binding.type.text = info.type

                            // Call the other observer for fetching episodes list
                            fetchEpisodeList(info.id, info.endEpisode, info.alias)

                            binding.loadingDetails.visibility = View.GONE
                            binding.expandTextView.visibility = View.VISIBLE
                            binding.releaseDate.visibility = View.VISIBLE
                            binding.status.visibility = View.VISIBLE
                            binding.type.visibility = View.VISIBLE
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireActivity(), res.message, Toast.LENGTH_LONG)
                            .show()
                    }
                    Status.LOADING -> {
                        binding.loadingDetails.visibility = View.VISIBLE

                        binding.expandTextView.visibility = View.GONE
                        binding.releaseDate.visibility = View.GONE
                        binding.status.visibility = View.GONE
                        binding.type.visibility = View.GONE

                    }
                }
            }
        })
    }

}