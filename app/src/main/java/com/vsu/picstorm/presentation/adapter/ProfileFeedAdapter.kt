package com.vsu.picstorm.presentation.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.ProfileItemBinding
import com.vsu.picstorm.domain.model.Publication
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.util.PixelConverter
import com.vsu.picstorm.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFeedAdapter constructor(
    private val profileViewModel: ProfileViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val navController: NavController,
): RecyclerView.Adapter<ProfileFeedAdapter.ProfileFeedViewHolder>() {

    inner class ProfileFeedViewHolder(
        binding: ProfileItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageView
    }

    private var publications: MutableList<Publication> = emptyList<Publication>().toMutableList()
    private var viewHolders: MutableSet<ProfileFeedAdapter.ProfileFeedViewHolder> = emptySet<ProfileFeedAdapter.ProfileFeedViewHolder>().toMutableSet()
    private lateinit var recyclerView: RecyclerView
    var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFeedAdapter.ProfileFeedViewHolder {
        val binding = ProfileItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProfileFeedViewHolder(binding)
    }

    override fun getItemCount(): Int = publications.size


    override fun onBindViewHolder(holder: ProfileFeedAdapter.ProfileFeedViewHolder, position: Int) {
        val data = publications[position]
        viewHolders.add(holder)
        lifecycleOwner.lifecycleScope.launch {
            val width = (recyclerView.width - PixelConverter.fromDP(context, 32)) / 3
            profileViewModel.getPublicationPhoto(data.id, width).collect { result ->
                if (result.status == ApiStatus.SUCCESS) {
                    holder.imageView.setImageBitmap(result.data)
                }
            }
        }
        holder.imageView.setOnClickListener{
            val bundle = Bundle()
            bundle.putLong("userId", data.ownerId)
            bundle.putInt("position", position)
            bundle.putString("publications", Gson().toJson(publications))
            navController.navigate(R.id.action_profileFragment_to_userFeedFragment, bundle)
        }
    }

    override fun onViewRecycled(holder: ProfileFeedAdapter.ProfileFeedViewHolder) {
        super.onViewRecycled(holder)
        recycleViewHolder(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun recycleAll() {
        for (holder in viewHolders) {
            recycleViewHolder(holder)
        }
    }

    private fun recycleViewHolder(holder: ProfileFeedAdapter.ProfileFeedViewHolder) {
        holder.imageView.drawable?.toBitmapOrNull()?.recycle()
        holder.imageView.setImageBitmap(null)
    }

    fun clear() {
        viewHolders.clear()
        val size = publications.size
        publications = emptyList<Publication>().toMutableList()
        notifyItemRangeRemoved(0, size)
    }

    fun add(data: List<Publication>) {
        val index = publications.size
        publications.addAll(data)
        notifyItemRangeInserted(index, data.size)
    }
}