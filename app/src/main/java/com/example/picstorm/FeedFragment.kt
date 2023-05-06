package com.example.picstorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.picstorm.databinding.FragmentFeedBinding
import com.skydoves.powerspinner.PowerSpinnerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var feedSpinner: PowerSpinnerView
    private lateinit var filterDateSpinner: PowerSpinnerView
    private lateinit var filterRatingSpinner: PowerSpinnerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        feedSpinner = binding.feedSpinner
        filterDateSpinner = binding.filterDateSpinner
        filterRatingSpinner = binding.filterRatingSpinner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPhotoLoadBtn()

        initBottomNav()

        initFeedSpinner()
        initFilterDateSpinner()
        initFilterRatingSpinner()
    }

    fun initPhotoLoadBtn(){
        binding.photoLoadBtn.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
        }
    }

    fun initBottomNav(){
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_searchFragment)
        }
        binding.bottomNav.binding.imageUser.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
        }
    }

    fun initFeedSpinner() {
        feedSpinner.setItems(R.array.feedSpinnerPersonal)
        feedSpinner.setHint(R.string.global)
        feedSpinner.setOnClickListener {
            feedSpinner.setBackgroundResource(R.drawable.feed_spinner_openup_shape)
            feedSpinner.showOrDismiss()
        }
        feedSpinner.setOnSpinnerDismissListener {
            feedSpinner.setBackgroundResource(R.drawable.feed_spinner_shape)
        }
        feedSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (newItem == R.string.global.toString()) {
                feedSpinner.setItems(R.array.feedSpinnerPersonal)
            }
            if (newItem == R.string.personal.toString()) {
                feedSpinner.setItems(R.array.feedSpinnerGlobal)
            }
            feedSpinner.setBackgroundResource(R.drawable.feed_spinner_shape)
        }
    }

    fun initFilterDateSpinner(){
        filterDateSpinner.setItems(R.array.filterDateSpinner)
        filterDateSpinner.setHint(R.string.dateFilter)
        filterDateSpinner.setOnClickListener{
            filterDateSpinner.setBackgroundResource(R.drawable.filter_spinner_openup_shape)
            filterDateSpinner.showOrDismiss()
        }
        filterDateSpinner.setOnSpinnerDismissListener {
            filterDateSpinner.setBackgroundResource(R.drawable.filter_spinner_shape)
        }
        filterDateSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            filterDateSpinner.setBackgroundResource(R.drawable.filter_spinner_shape)
        }
    }

    fun initFilterRatingSpinner(){
        filterRatingSpinner.setItems(R.array.filterRatingSpinner)
        filterRatingSpinner.setHint(R.string.ratingFilter)
        filterRatingSpinner.setOnClickListener{
            filterRatingSpinner.setBackgroundResource(R.drawable.filter_spinner_openup_shape)
            filterRatingSpinner.showOrDismiss()
        }
        filterRatingSpinner.setOnSpinnerDismissListener {
            filterRatingSpinner.setBackgroundResource(R.drawable.filter_spinner_shape)
        }
        filterRatingSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            filterRatingSpinner.setBackgroundResource(R.drawable.filter_spinner_shape)
        }
    }
}