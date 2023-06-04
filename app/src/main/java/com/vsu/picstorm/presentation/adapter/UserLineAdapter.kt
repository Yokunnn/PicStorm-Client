package com.vsu.picstorm.presentation.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.SearchItemBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.viewmodel.UserLineViewModel
import kotlinx.coroutines.launch

class UserLineAdapter constructor(
    private val userLineViewModel: UserLineViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val tokenStorage: TokenStorage,
    private val navController: NavController
) : RecyclerView.Adapter<UserLineAdapter.SearchViewHolder>() {

    private var items: MutableList<UserLine> = emptyList<UserLine>().toMutableList()
    private var accessToken: String? = null
    var isLoading: Boolean = false
    private var viewHolders: MutableMap<Long, SearchViewHolder> =
        emptyMap<Long, SearchViewHolder>().toMutableMap()

    init {
        observeToken()
        observeSubRes()
    }

    inner class SearchViewHolder(
        binding: SearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView = binding.imageView
        val nameButton = binding.nameButton
        val subButton = binding.subButton
        val unsubButton = binding.unsubButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = SearchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val data = items[position]
        viewHolders[data.id] = holder
        lifecycleOwner.lifecycleScope.launch {
            userLineViewModel.getAvatar(data.id).collect { result ->
                if (result.status == ApiStatus.SUCCESS) {
                    holder.imageView.setImageBitmap(result.data)
                }
            }
        }
        with(holder) {
            nameButton.text = data.nickname
            nameButton.setOnClickListener {
                val bundle = Bundle()
                bundle.putLong("id", data.id)
                navController.navigate(R.id.profileFragment, bundle)
            }

            unsubButton.setOnClickListener {
                userLineViewModel.changeSubscribe(accessToken, data.id)
            }
            subButton.setOnClickListener {
                userLineViewModel.changeSubscribe(accessToken, data.id)
            }
            when (data.subscribed) {
                true -> {
                    unsubButton.visibility = View.VISIBLE
                }
                false -> {
                    subButton.visibility = View.VISIBLE
                }
                else -> {
                    unsubButton.visibility = View.GONE
                    subButton.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun clear() {
        viewHolders.clear()
        items = emptyList<UserLine>().toMutableList()
    }

    fun update(data: List<UserLine>) {
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun observeToken() {
        tokenStorage.token.observe(lifecycleOwner) { token ->
            accessToken = if (token.accessToken != "null") {
                token.accessToken
            } else {
                null
            }
        }
    }

    fun observeSubRes() {
        userLineViewModel.subResult.observe(lifecycleOwner) { result ->
            val holder: SearchViewHolder? = viewHolders[result.first]
            holder?.let {
                val data = items.find { user -> user.id == result.first }!!
                when (result.second.status) {
                    ApiStatus.SUCCESS -> {
                        with(holder) {
                            if (result.second.data == null) {
                                unsubButton.visibility = View.GONE
                                subButton.visibility = View.VISIBLE
                            } else {
                                subButton.visibility = View.GONE
                                unsubButton.visibility = View.VISIBLE
                            }
                            unsubButton.isClickable = true
                            subButton.isClickable = true
                        }
                        data.subscribed = result.second.data != null
                    }
                    ApiStatus.ERROR -> {
                    }
                    ApiStatus.LOADING -> {
                        with(holder) {
                            unsubButton.isClickable = false
                            subButton.isClickable = false
                        }
                    }
                }
            }
        }
    }
}