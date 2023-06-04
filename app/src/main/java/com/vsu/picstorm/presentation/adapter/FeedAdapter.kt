package com.vsu.picstorm.presentation.adapter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.PublicationItemBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.domain.model.Publication
import com.vsu.picstorm.domain.model.enums.ReactionType
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.viewmodel.FeedViewModel
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FeedAdapter constructor(
    private val feedViewModel: FeedViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val tokenStorage: TokenStorage,
    private val navController: NavController,
    private val context: Context
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    private var publications: MutableList<Publication> = emptyList<Publication>().toMutableList()
    private var accessToken: String? = null
    private var userId: Long? = null
    private var isAdmin: Boolean = false
    var isLoading: Boolean = false
    private var viewHolders: MutableMap<Long, FeedAdapter.FeedViewHolder> =
        emptyMap<Long, FeedAdapter.FeedViewHolder>().toMutableMap()

    init {
        observeToken()
        observeReactRes()
        observeBanRes()
        observeDeleteRes()
    }

    inner class FeedViewHolder(
        binding: PublicationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val avatarIv = binding.avatarImageView
        val nameButton = binding.nameButton
        val deleteButton = binding.deleteButton
        val publicationIv = binding.publicationImageView
        val rateUpButton = binding.rateUpButton
        val rateDownButton = binding.rateDownButton
        val rateTv = binding.rateTextView
        val dateTv = binding.dateTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = PublicationItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val data = publications[position]
        viewHolders[data.id] = holder
        lifecycleOwner.lifecycleScope.launch {
            feedViewModel.getAvatar(data.ownerId).collect { result ->
                if (result.status == ApiStatus.SUCCESS) {
                    holder.avatarIv.setImageBitmap(result.data)
                }
            }
        }
        lifecycleOwner.lifecycleScope.launch {
            feedViewModel.getPublicationPhoto(data.id).collect { result ->
                if (result.status == ApiStatus.SUCCESS) {
                    holder.publicationIv.setImageBitmap(result.data)
                }
            }
        }
        with(holder) {
            dateTv.text = reformatDate(data.date)
            rateTv.text = data.rating.toString()

            nameButton.text = data.ownerNickname
            nameButton.setOnClickListener {
                val bundle = Bundle()
                bundle.putLong("id", data.ownerId)
                navController.navigate(R.id.profileFragment, bundle)
            }

            if (isAdmin || userId == data.ownerId) {
                deleteButton.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
            }
            deleteButton.setOnClickListener {
                if (userId == data.ownerId) {
                    feedViewModel.deletePublication(accessToken, data.id)
                } else {
                    feedViewModel.banPublication(accessToken, data.id)
                }
            }

            if (userId != null) {
                when (data.reactionType) {
                    ReactionType.LIKE -> {
                        rateUpButton.drawable.setTint(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.picStorm_violet,
                                null
                            )
                        )
                        rateDownButton.drawable.setTint(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.black,
                                null
                            )
                        )
                        rateTv.setTextColor(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.picStorm_violet,
                                null
                            )
                        )
                    }
                    ReactionType.DISLIKE -> {
                        rateUpButton.drawable.setTint(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.black,
                                null
                            )
                        )
                        rateDownButton.drawable.setTint(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.picStorm_violet,
                                null
                            )
                        )
                        rateTv.setTextColor(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.picStorm_violet,
                                null
                            )
                        )
                    }
                    else -> {
                        rateUpButton.drawable.setTint(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.black,
                                null
                            )
                        )
                        rateDownButton.drawable.setTint(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.black,
                                null
                            )
                        )
                        rateTv.setTextColor(
                            ResourcesCompat.getColor(
                                context.resources,
                                R.color.black,
                                null
                            )
                        )
                    }
                }
                rateUpButton.setOnClickListener {
                    if (data.reactionType == ReactionType.LIKE) {
                        feedViewModel.reactPublication(accessToken, data.id, null)
                    } else {
                        feedViewModel.reactPublication(accessToken, data.id, ReactionType.LIKE)
                    }
                }

                rateDownButton.setOnClickListener {
                    if (data.reactionType == ReactionType.DISLIKE) {
                        feedViewModel.reactPublication(accessToken, data.id, null)
                    } else {
                        feedViewModel.reactPublication(accessToken, data.id, ReactionType.DISLIKE)
                    }
                }
            } else {
                rateUpButton.drawable.setTint(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.picStorm_grey,
                        null
                    )
                )
                rateDownButton.drawable.setTint(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.picStorm_grey,
                        null
                    )
                )
                rateTv.setTextColor(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.picStorm_grey,
                        null
                    )
                )
            }
        }
    }

    override fun onViewRecycled(holder: FeedViewHolder) {
        super.onViewRecycled(holder)
        holder.publicationIv.drawable?.toBitmapOrNull()?.recycle()
        holder.avatarIv.drawable?.toBitmapOrNull()?.recycle()
        holder.publicationIv.setImageBitmap(null)
        holder.avatarIv.setImageBitmap(null)
    }

    override fun getItemCount(): Int = publications.size

    fun reformatDate(date: Instant): String {
        val time = date.atZone(Clock.systemDefaultZone().zone)
        val timeCurr = ZonedDateTime.now()
        var formatter: DateTimeFormatter =
            if (time.year == timeCurr.year && time.dayOfYear == timeCurr.dayOfYear) {
                DateTimeFormatter.ofPattern("HH:mm")
            } else if (time.year == timeCurr.year) {
                DateTimeFormatter.ofPattern("dd MM")
            } else {
                DateTimeFormatter.ofPattern("yyyy")
            }
        formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatter.withLocale(context.resources.configuration.locales.get(0))
        } else {
            formatter.withLocale(context.resources.configuration.locale)
        }
        return formatter.format(time)
    }

    fun clear() {
        viewHolders.clear()
        val size = publications.size
        publications = emptyList<Publication>().toMutableList()
        notifyItemRangeRemoved(0, size)
    }

    fun update(data: List<Publication>) {
        publications.addAll(data)
        notifyDataSetChanged()
    }

    fun observeToken() {
        tokenStorage.token.observe(lifecycleOwner) { token ->
            if (token.accessToken != "null") {
                accessToken = token.accessToken
                val jwt = JWT(accessToken!!)
                userId = jwt.getClaim("id").asLong()
                val authorities = jwt.getClaim("authorities").asList(String::class.java)
                if (authorities.contains("BAN_PUBLICATION_AUTHORITY")) {
                    isAdmin = true
                }
            } else {
                accessToken = null
            }
        }
    }

    fun observeBanRes() {
        feedViewModel.banResult.observe(lifecycleOwner) { result ->
            when (result.second.status) {
                ApiStatus.SUCCESS -> {
                    deleteById(result.first)
                }
                ApiStatus.ERROR -> {
                }
                ApiStatus.LOADING -> {
                }
            }
        }
    }

    fun observeDeleteRes() {
        feedViewModel.deleteResult.observe(lifecycleOwner) { result ->
            when (result.second.status) {
                ApiStatus.SUCCESS -> {
                    deleteById(result.first)
                }
                ApiStatus.ERROR -> {
                }
                ApiStatus.LOADING -> {
                }
            }
        }
    }

    fun deleteById(publicationId: Long) {
        viewHolders.remove(publicationId)
        publications.filter { publication -> publicationId != publication.id }
        var i = 0
        for (p in publications.withIndex()) {
            if (p.value.id == publicationId) {
                i = p.index
            }
        }
        publications.removeAt(i)
        notifyItemRemoved(i)
    }

    fun observeReactRes() {
        feedViewModel.reactResult.observe(lifecycleOwner) { result ->
            val holder: FeedAdapter.FeedViewHolder? = viewHolders[result.first]
            holder?.let {
                val data = publications.find { publication -> publication.id == result.first }!!
                when (result.second.status) {
                    ApiStatus.SUCCESS -> {
                        with(holder) {
                            rateUpButton.isClickable = true
                            rateDownButton.isClickable = true
                            when (result.second.data) {
                                null -> {
                                    rateUpButton.drawable.setTint(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.black,
                                            null
                                        )
                                    )
                                    rateDownButton.drawable.setTint(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.black,
                                            null
                                        )
                                    )
                                    rateTv.setTextColor(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.black,
                                            null
                                        )
                                    )
                                    if (data.reactionType == ReactionType.LIKE) {
                                        data.rating -= 1
                                    } else {
                                        data.rating += 1
                                    }
                                }
                                ReactionType.LIKE -> {
                                    rateUpButton.drawable.setTint(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.picStorm_violet,
                                            null
                                        )
                                    )
                                    rateDownButton.drawable.setTint(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.black,
                                            null
                                        )
                                    )
                                    rateTv.setTextColor(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.picStorm_violet,
                                            null
                                        )
                                    )
                                    if (data.reactionType == ReactionType.DISLIKE) {
                                        data.rating += 2
                                    } else {
                                        data.rating += 1
                                    }
                                }
                                else -> {
                                    rateUpButton.drawable.setTint(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.black,
                                            null
                                        )
                                    )
                                    rateDownButton.drawable.setTint(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.picStorm_violet,
                                            null
                                        )
                                    )
                                    rateTv.setTextColor(
                                        ResourcesCompat.getColor(
                                            context.resources,
                                            R.color.picStorm_violet,
                                            null
                                        )
                                    )
                                    if (data.reactionType == ReactionType.LIKE) {
                                        data.rating -= 2
                                    } else {
                                        data.rating -= 1
                                    }
                                }
                            }
                            rateTv.text = data.rating.toString()
                        }
                        data.reactionType = result.second.data
                    }
                    ApiStatus.ERROR -> {
                    }
                    ApiStatus.LOADING -> {
                        holder.rateUpButton.isClickable = false
                        holder.rateDownButton.isClickable = false
                    }
                }
            }
        }
    }
}