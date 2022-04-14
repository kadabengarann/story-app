package com.kadabengaran.storyapp.view.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.kadabengaran.storyapp.ViewModelFactory
import com.kadabengaran.storyapp.databinding.FragmentHomeBinding
import com.kadabengaran.storyapp.databinding.ItemStoriesBinding
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.model.StoryItem
import com.kadabengaran.storyapp.view.ListStoryAdapter
import com.kadabengaran.storyapp.view.PreferenceViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var preferenceViewModel: PreferenceViewModel

    private val factory by lazy {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val homeViewModel: HomeViewModel by viewModels {
        factory
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val storyAdapter: ListStoryAdapter by lazy {
        ListStoryAdapter(
            mutableListOf()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupViewModel()
        Log.d("HomeFragment", "onCreateView: create")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
//        setupView()
//        setupAction()
        Log.d("HomeFragment", "onViewCreated: create")
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("HomeFragment", "onDestroyView: destroyy")
    }

    private fun setupViewModel() {
        preferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
        preferenceViewModel.getUser().observe(viewLifecycleOwner) { user ->
            homeViewModel.setToken(user.token)
        }
        if (!homeViewModel.fetched) {
            homeViewModel.tokenSession.observe(viewLifecycleOwner){
                if (!it.isNullOrEmpty())
                    homeViewModel.getStories()
            }

        }
        Log.d(TAG, "setupViewModel: ${homeViewModel.fetched}")
    }
    private fun observeData() {
        homeViewModel.listStory.observe(viewLifecycleOwner) {
            processData(it)
        }
    }
    private fun processData( result: Result<List<StoryItem>>){
        if (result != null) {
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val storyList = result.data
                    setStories(storyList)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error)
                }
            }
        }
    }
    private fun showError(error: String) {
        binding.grError.visibility = View.VISIBLE
        binding.tvError.text = error
        binding.btnError.setOnClickListener {
            binding.grError.visibility = View.GONE
            homeViewModel.getStories()
        }
        Log.d(TAG, "proce_showError: $error")

    }

    private fun setStories(storyList: List<StoryItem>) {
        if (storyList.isEmpty()) {
            Log.d(TAG, "procc_setStories : NO DATA")
        }

        val listUser = ArrayList<StoryItem>()
        for (user in storyList) {
            listUser.add(user)
        }
        binding.rvStories.visibility = View.VISIBLE
        storyAdapter.setData(storyList)
        storyAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryItem, binding: ItemStoriesBinding) {
                val toDetailCategoryFragment =
                    HomeFragmentDirections.actionNavigationHomeToDetailFragment(
                        data
                    )
                val extras = FragmentNavigatorExtras(
//                    imageView to data.id,
                    binding.cardItem to "card"+data.id,
                )
//                toDetailCategoryFragment.stock = 7
                view?.findNavController()?.navigate(toDetailCategoryFragment,extras)

                Log.d(TAG, "procc_setStories: Cliked data $data")
            }
        })    }

    private fun showLoading(b: Boolean) {
        Log.d(TAG, "procc_setStories: LOADING.....")
        binding.progressBar.visibility = if (b) View.VISIBLE else View.GONE
    }
}