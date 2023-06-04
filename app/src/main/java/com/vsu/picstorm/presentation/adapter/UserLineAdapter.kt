package com.vsu.picstorm.presentation.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.SearchItemBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.domain.model.UserLine
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.util.PixelConverter
import com.vsu.picstorm.viewmodel.UserLineViewModel
import kotlinx.coroutines.launch

class UserLineAdapter constructor(
    private val userLineViewModel: UserLineViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val tokenStorage: TokenStorage,
    private val navController: NavController,
    private val context: Context
) : RecyclerView.Adapter<UserLineAdapter.UserViewHolder>() {

    private var items: MutableList<UserLine> = emptyList<UserLine>().toMutableList()
    private var accessToken: String? = null
    var isLoading: Boolean = false
    private var viewHolders: MutableMap<Long, UserViewHolder> =
        emptyMap<Long, UserViewHolder>().toMutableMap()

    init {
        observeToken()
        observeSubRes()
    }

    inner class UserViewHolder(
        binding: SearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val imageView = binding.imageView
        val nameButton = binding.nameButton
        val subButton = binding.subButton
        val unsubButton = binding.unsubButton
        val imageCard = binding.imageCard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = SearchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val data = items[position]
        viewHolders[data.id] = holder
        lifecycleOwner.lifecycleScope.launch {
            val width = PixelConverter.fromDP(context, holder.imageCard.layoutParams.width)
            userLineViewModel.getAvatar(data.id, width).collect { result ->
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
        val size = items.size
        items = emptyList<UserLine>().toMutableList()
        notifyItemRangeRemoved(0, size)
    }

    fun update(data: List<UserLine>) {
        val index = items.size
        items.addAll(data)
        notifyItemRangeInserted(index, data.size)
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
            val holder: UserViewHolder? = viewHolders[result.first]
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

    override fun onViewRecycled(holder: UserLineAdapter.UserViewHolder) {
        super.onViewRecycled(holder)
        recycleViewHolder(holder)
    }

    fun recycleAll() {
        for (holder in viewHolders.values) {
            recycleViewHolder(holder)
        }
    }

    fun recycleViewHolder(holder: UserLineAdapter.UserViewHolder) {
        holder.imageView.drawable?.toBitmapOrNull()?.recycle()
        holder.imageView.setImageBitmap(null)
    }
}