package com.vsu.picstorm.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentDialogAlertBinding
import com.vsu.picstorm.databinding.FragmentDialogConfirmBinding
import com.vsu.picstorm.databinding.FragmentDialogPhotoLoadBinding
import com.vsu.picstorm.databinding.FragmentUserFeedBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.domain.model.enums.DateFilterType
import com.vsu.picstorm.domain.model.enums.SortFilterType
import com.vsu.picstorm.domain.model.enums.UserFilterType
import com.vsu.picstorm.presentation.adapter.FeedAdapter
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.util.DialogFactory
import com.vsu.picstorm.viewmodel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFeedFragment : Fragment() {

    private lateinit var binding: FragmentUserFeedBinding

    private lateinit var alertBinding: FragmentDialogAlertBinding
    private lateinit var alertRecycleBinding: FragmentDialogAlertBinding
    private lateinit var confirmBanBinding: FragmentDialogConfirmBinding
    private lateinit var confirmDeleteBinding: FragmentDialogConfirmBinding
    private lateinit var photoAlertBinding: FragmentDialogPhotoLoadBinding
    private lateinit var dialog: Dialog
    private lateinit var loadDialog: Dialog

    private lateinit var tokenStorage: TokenStorage
    private val feedViewModel: FeedViewModel by viewModels()
    private val pageSize = 1
    private var lastPage = 0
    private var accessToken: String? = null
    private var userId: Long = 0
    private lateinit var usersAdapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserFeedBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())

        alertBinding = FragmentDialogAlertBinding.inflate(inflater, container, false)
        alertRecycleBinding = FragmentDialogAlertBinding.inflate(inflater, container, false)
        confirmBanBinding = FragmentDialogConfirmBinding.inflate(inflater, container, false)
        confirmDeleteBinding = FragmentDialogConfirmBinding.inflate(inflater, container, false)
        photoAlertBinding = FragmentDialogPhotoLoadBinding.inflate(inflater, container, false)
        dialog = DialogFactory.createAlertDialog(requireContext(), alertBinding)
        loadDialog = DialogFactory.createPhotoLoadDialog(requireContext(), photoAlertBinding)
        usersAdapter = FeedAdapter(
            feedViewModel, viewLifecycleOwner, tokenStorage, findNavController(),
            requireContext(), alertRecycleBinding, confirmBanBinding, confirmDeleteBinding
        )

        userId = requireArguments().getLong("userId")
        lastPage = requireArguments().getInt("pubId")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initBackButton()

        observeToken()
    }

    private fun initBackButton() {
        binding.exitImageButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong("id", userId)
            findNavController().navigate(R.id.action_userFeedFragment_to_profileFragment, bundle)
        }
    }


    fun initBottomNav(isAuthorised: Boolean) {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_userFeedFragment_to_feedFragment)
        }
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_userFeedFragment_to_searchFragment)
        }
        if (isAuthorised) {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_userFeedFragment_to_loginFragment)
            }
        } else {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_userFeedFragment_to_profileFragment)
            }
        }
    }

    fun initRecyclerView() {
        binding.searchRv.setItemViewCacheSize(pageSize)
        binding.searchRv.layoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchRv.adapter = usersAdapter

        val layoutManager = binding.searchRv.layoutManager as LinearLayoutManager
        binding.searchRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemsCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!usersAdapter.isLoading && itemsCount == lastVisibleItem + 1 && itemsCount / pageSize == lastPage + 1) {
                    feedViewModel.getFeed(
                        accessToken,
                        DateFilterType.NONE,
                        SortFilterType.NONE,
                        UserFilterType.ALL,
                        userId,
                        lastPage,
                        pageSize
                    )
                    lastPage++
                }
            }
        })
    }

    fun observeToken() {
        tokenStorage.token.observe(viewLifecycleOwner) { token ->
            if (token.accessToken != "null") {
                val jwt = JWT(token.accessToken)
                if (jwt.isExpired(0)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        tokenStorage.deleteToken()
                    }
                } else {
                    initBottomNav(true)
                    if (jwt.getClaim("id").asLong() == userId) {
                        binding.textView.text = getString(R.string.myPublications)
                    } else {
                        binding.textView.text = getString(R.string.userPublications)
                    }
                    accessToken = token.accessToken
                }
            } else {
                initBottomNav(false)
                accessToken = null
            }
            feedViewModel.getFeed(
                accessToken,
                DateFilterType.NONE,
                SortFilterType.NONE,
                UserFilterType.ALL,
                userId,
                lastPage,
                pageSize
            )
        }
    }

//    fun observeFeed() {
//        feedViewModel.feedResult.observe(viewLifecycleOwner) { result ->
//            when (result.status) {
//                ApiStatus.SUCCESS -> {
//                    val data = result.data!!
//                    feedAdapter.update(data)
//                    binding.refreshLayout.isRefreshing = false
//                    feedAdapter.isLoading = false
//                }
//                ApiStatus.ERROR -> {
//                    feedAdapter.isLoading = false
//                    alertBinding.textView.text = result.message.toString()
//                    dialog.show()
//                }
//                ApiStatus.LOADING -> {
//                    feedAdapter.isLoading = true
//                }
//            }
//        }
//    }

    override fun onDestroyView() {
        usersAdapter.recycleAll()
        super.onDestroyView()
    }

}