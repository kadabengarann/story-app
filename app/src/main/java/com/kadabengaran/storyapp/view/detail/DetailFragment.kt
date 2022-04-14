package com.kadabengaran.storyapp.view.detail

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.kadabengaran.storyapp.R
import com.kadabengaran.storyapp.databinding.FragmentDetailBinding
import com.kadabengaran.storyapp.databinding.FragmentHomeBinding
import com.kadabengaran.storyapp.utils.loadImage
import com.kadabengaran.storyapp.utils.withDateFormat

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//        postponeEnterTransition()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.title_detail_story)
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStory = DetailFragmentArgs.fromBundle(arguments as Bundle).story
//        val dataDescription = DetailFragmentArgs.fromBundle(arguments as Bundle).stock

        binding.apply {
            tvUsername.text = dataStory.name
            tvItemDate.text = getString(R.string.dateFormat, dataStory.createdAt.withDateFormat())
            tvStoryDescription.text = dataStory.description
            imgStory.loadImage(dataStory.photoUrl)
//            imgStory.transitionName = dataStory.id
            cardContainer.transitionName = "card"+dataStory.id
        }
//        binding.tvCategoryDescription.text = "Stock : $dataDescription"

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                activity?.onBackPressed()
        }
        return true
    }


}