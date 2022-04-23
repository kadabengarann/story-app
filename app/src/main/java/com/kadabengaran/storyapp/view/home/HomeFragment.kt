package com.kadabengaran.storyapp.view.home

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialFadeThrough
import com.kadabengaran.storyapp.databinding.FragmentHomeBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.database.StoryEntity
import com.kadabengaran.storyapp.service.model.StoryItem
import com.kadabengaran.storyapp.view.ListStoryAdapter
import com.kadabengaran.storyapp.view.PreferenceViewModel
import com.kadabengaran.storyapp.view.ViewModelFactory
import com.kadabengaran.storyapp.view.detail.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var preferenceViewModel: PreferenceViewModel

    private val binding get() = _binding!!

    private val factory by lazy {
        ViewModelFactory.getInstance(requireContext())
    }
    private val homeViewModel: HomeViewModel by viewModels {
        factory
    }
    private val storyAdapter: ListStoryAdapter by lazy {
        ListStoryAdapter()
    }

    private var reFetch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reFetch = HomeFragmentArgs.fromBundle(arguments as Bundle).reFetch
        setupViewModel()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
        observeData()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TOTTTTTTT", "setupViewModel: SI DESTROYYY")

        _binding = null
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        preferenceViewModel.getUser().observe(viewLifecycleOwner) { user ->
            homeViewModel.setToken(user.token)
        }
        if (reFetch){
            homeViewModel.refresh()
        }
        homeViewModel.tokenSession.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    homeViewModel.getStories().observe(viewLifecycleOwner){
                        Log.d("TAGAGAGAG", "setupViewModel: GETSTORYYYYYY")
                        storyAdapter.submitData(lifecycle, it)
                        binding.rvStories.visibility = View.VISIBLE
                    }
                }
        }
        binding.rvStories.scrollToPosition(0)
        showLoading(true)
    }

    private fun observeData() {
        storyAdapter.addLoadStateListener { loadState ->
            Log.d("TAGAAA", "observeData loadstatetete: ${loadState.source.refresh }")
            showLoading(loadState.source.refresh is LoadState.Loading)
            binding.grError.isVisible = loadState.source.refresh is LoadState.Error

            showError(loadState)
        }
        storyAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryEntity, cardItem: CardView) {
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY, data)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireContext() as Activity,
                        Pair(cardItem, "parentCard")
                    )
                requireContext().startActivity(intent, optionsCompat.toBundle())
                Log.d(TAG, "setStories: Cliked data $data")
            }
        })
    }

    private fun showError(error: CombinedLoadStates) {
       /* error.let {
            binding.tvError.text = it.error
        }*/
        binding.btnError.setOnClickListener {
            binding.grError.visibility = View.GONE
            storyAdapter.retry()
        }
    }
    private fun showLoading(b: Boolean) {
        binding.progressBar.visibility = if (b) View.VISIBLE else View.GONE
    }
}