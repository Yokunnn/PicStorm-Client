package com.vsu.picstorm.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentSubBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.presentation.adapter.UserLineAdapter
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.viewmodel.SubViewModel
import com.vsu.picstorm.viewmodel.UserLineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubFragment : Fragment() {

    private lateinit var binding: FragmentSubBinding
    private lateinit var tokenStorage: TokenStorage
    private val subViewModel: SubViewModel by viewModels()
    private val userLineViewModel: UserLineViewModel by viewModels()
    private val pageSize: Int = 20
    private var lastPage = 0
    private var accessToken: String? = null
    private var userId: Long = 0
    private var areSubscriptions: Boolean = false
    private lateinit var usersAdapter: UserLineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())
        usersAdapter = UserLineAdapter(userLineViewModel, viewLifecycleOwner, tokenStorage, findNavController())
        userId = requireArguments().getLong("id")
        areSubscriptions = requireArguments().getBoolean("areSubscriptions")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initLabel()
        initBackButton()
        observeSubs()
        observeToken()
    }

    private fun initBackButton() {
        binding.exitImageButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putLong("id", userId)
            findNavController().navigate(R.id.action_subFragment_to_profileFragment, bundle)
        }
    }

    fun observeSubs() {
        subViewModel.subsResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    val data = result.data!!
                    usersAdapter.update(data)
                }
                ApiStatus.ERROR ->   {
                }
                ApiStatus.LOADING ->  {
                }
            }
        }
    }

    fun initLabel() {
        if (areSubscriptions) {
            binding.textView.text = resources.getText(R.string.subscriptions_title_label)
        } else {
            binding.textView.text = resources.getText(R.string.subscribers_title_label)
        }
    }

    fun initBottomNav(isAuthorised: Boolean) {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_feedFragment)
        }
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_subFragment_to_searchFragment)
        }
        if (isAuthorised) {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_subFragment_to_loginFragment)
            }
        } else {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_subFragment_to_profileFragment)
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
                    usersAdapter.isLoading = true
                    if (areSubscriptions) {
                        subViewModel.getSubscriptions(accessToken, userId, itemsCount / pageSize, pageSize)
                    } else {
                        subViewModel.getSubscribers(accessToken, userId, itemsCount / pageSize, pageSize)
                    }
                    usersAdapter.isLoading = false
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
                    accessToken = token.accessToken
                }
            } else {
                initBottomNav(false)
                accessToken = null
            }
            if (areSubscriptions) {
                subViewModel.getSubscriptions(accessToken, userId, lastPage, pageSize)
            } else {
                subViewModel.getSubscribers(accessToken, userId, lastPage, pageSize)
            }

        }
    }
}