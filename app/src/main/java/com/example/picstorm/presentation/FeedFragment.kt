package com.example.picstorm.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentFeedBinding
import com.example.picstorm.presentation.adapter.FeedAdapter
import com.skydoves.powerspinner.PowerSpinnerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var feedSpinner: PowerSpinnerView
    private lateinit var filterDateSpinner: PowerSpinnerView
    private lateinit var filterRatingSpinner: PowerSpinnerView

    private val feedAdapter = FeedAdapter()

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

        initRecyclerView()

        initFeedSpinner()
        initFilterDateSpinner()
        initFilterRatingSpinner()
    }

    fun initRecyclerView() {
        binding.feedRv.adapter = feedAdapter
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
        with(feedSpinner){
            setItems(R.array.feedSpinnerPersonal)
            setHint(R.string.global)
            setOnClickListener {
                setBackgroundResource(R.drawable.feed_spinner_openup_shape)
                showOrDismiss()
            }
            setOnSpinnerDismissListener {
                setBackgroundResource(R.drawable.feed_spinner_shape)
            }
            setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
                if (newItem == "Общая") {
                    setItems(R.array.feedSpinnerPersonal)
                }
                if (newItem == "Личная") {
                    setItems(R.array.feedSpinnerGlobal)
                }
                setBackgroundResource(R.drawable.feed_spinner_shape)
            }
        }
    }

    fun initFilterDateSpinner(){
        with(filterDateSpinner){
            setItems(R.array.filterDateSpinner)
            setHint(R.string.dateFilter)
            setOnClickListener{
                setBackgroundResource(R.drawable.filter_spinner_openup_shape)
                showOrDismiss()
            }
            setOnSpinnerDismissListener {
                setBackgroundResource(R.drawable.filter_spinner_shape)
            }
            setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
                setBackgroundResource(R.drawable.filter_spinner_shape)
            }
        }
    }

    fun initFilterRatingSpinner(){
        with(filterRatingSpinner){
            setItems(R.array.filterRatingSpinner)
            setHint(R.string.ratingFilter)
            setOnClickListener{
                setBackgroundResource(R.drawable.filter_spinner_openup_shape)
                showOrDismiss()
            }
            setOnSpinnerDismissListener {
                setBackgroundResource(R.drawable.filter_spinner_shape)
            }
            setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
                setBackgroundResource(R.drawable.filter_spinner_shape)
            }
        }
    }
}