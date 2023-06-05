package com.vsu.picstorm.presentation

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.auth0.android.jwt.JWT
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentDialogAlertBinding
import com.vsu.picstorm.databinding.FragmentDialogConfirmBinding
import com.vsu.picstorm.databinding.FragmentDialogPhotoLoadBinding
import com.vsu.picstorm.databinding.FragmentProfileBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.domain.model.Profile
import com.vsu.picstorm.domain.model.enums.DateFilterType
import com.vsu.picstorm.domain.model.enums.SortFilterType
import com.vsu.picstorm.domain.model.enums.UserFilterType
import com.vsu.picstorm.domain.model.enums.UserRole
import com.vsu.picstorm.presentation.adapter.ProfileFeedAdapter
import com.vsu.picstorm.util.ApiStatus
import com.vsu.picstorm.util.DialogFactory
import com.vsu.picstorm.util.PixelConverter
import com.vsu.picstorm.viewmodel.ProfileViewModel
import com.yandex.metrica.YandexMetrica
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var alertBinding: FragmentDialogAlertBinding
    private lateinit var loadBinding: FragmentDialogPhotoLoadBinding
    private lateinit var confirmAdminBinding: FragmentDialogConfirmBinding
    private lateinit var confirmBanBinding: FragmentDialogConfirmBinding
    private lateinit var alertDialog: Dialog
    private lateinit var loadDialog: Dialog
    private lateinit var confirmAdminDialog: Dialog
    private lateinit var confirmBanDialog: Dialog
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage
    private var startedInit = false
    private var viewerRole = UserRole.UNAUTHORIZED
    private var choosingAvatar = false
    private var viewerId: Long? = null
    private var profileOwnerId : Long? = null
    private var accessToken: String? = null
    private lateinit var feedAdapter: ProfileFeedAdapter
    private val pageSize: Int = 12
    private var lastPage = 0


    private var photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                intent?.data?.let { uploadPhoto(it) }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())

        alertBinding = FragmentDialogAlertBinding.inflate(inflater, container, false)
        loadBinding = FragmentDialogPhotoLoadBinding.inflate(inflater, container, false)
        confirmAdminBinding = FragmentDialogConfirmBinding.inflate(inflater, container, false)
        confirmBanBinding = FragmentDialogConfirmBinding.inflate(inflater, container, false)
        alertDialog = DialogFactory.createAlertDialog(requireContext(), alertBinding)
        loadDialog = DialogFactory.createPhotoLoadDialog(requireContext(), loadBinding)
        confirmAdminDialog = DialogFactory.createConfirmDialog(requireContext(), confirmAdminBinding)
        confirmBanDialog = DialogFactory.createConfirmDialog(requireContext(), confirmBanBinding)
        feedAdapter = ProfileFeedAdapter(profileViewModel, viewLifecycleOwner, requireContext(), findNavController())

        return binding.root
    }

    fun observeAvatar() {
        profileViewModel.avatarResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS -> {
                    binding.avatarImageView.setImageBitmap(result.data)
                }
                ApiStatus.ERROR -> {}
                ApiStatus.LOADING -> {}
            }
        }
    }

    fun observeProfile() {
        profileViewModel.profileResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS -> {
                    val profile = result.data!!
                    binding.nicknameTv.text = profile.name
                    initSubLabels(profile)
                    if (viewerId != profile.userId) {
                        confirmBanBinding.textView.text = getString(R.string.confirmBan)
                        confirmBanBinding.buttonConfirm.setOnClickListener {
                            profileViewModel.banUser(accessToken, profile.userId)
                            confirmBanDialog.dismiss()
                        }
                        binding.banBtn.setOnClickListener{
                            confirmBanDialog.show()
                        }
                        confirmAdminBinding.buttonConfirm.setOnClickListener {
                            profileViewModel.changeAdmin(accessToken, profile.userId)
                            confirmAdminDialog.dismiss()
                        }
                        binding.adminBtn.setOnClickListener{
                            confirmAdminDialog.show()
                        }
                        observeChangeAdminResult()
                        observeBanUserResult()
                        refreshActionButtons(profile.role)
                    } else {
                        binding.logoutBtn.visibility = View.VISIBLE
                        binding.logoutBtn.setOnClickListener {
                            confirmAdminBinding.buttonConfirm.setOnClickListener {
                                confirmAdminDialog.dismiss()
                                logout()
                            }
                            confirmAdminBinding.textView.text = getString(R.string.confirmLogout)
                            confirmAdminDialog.show()
                        }
                        binding.photoLoadBtn.visibility = View.VISIBLE
                        binding.photoLoadBtn.setOnClickListener {
                            showPhotoChooser(false)
                        }
                        handlePhotoUpload()
                    }
                    if (profile.subscribed == true) {
                        binding.unsubBtn.visibility = View.VISIBLE
                    } else if (profile.subscribed == false) {
                        binding.subBtn.visibility = View.VISIBLE
                    }
                    binding.subBtn.setOnClickListener{
                        profileViewModel.changeSubscribe(accessToken, profile.userId)
                    }
                    binding.unsubBtn.setOnClickListener{
                        profileViewModel.changeSubscribe(accessToken, profile.userId)
                    }
                    observeSubResult()
                }

                ApiStatus.ERROR -> {
                }

                ApiStatus.LOADING -> {
                }
            }
        }
    }

    private fun initSubLabels(profile: Profile) {
        binding.subscriptionsTv.text =
            "${resources.getString(R.string.subscriptions_label)} ${profile.subscriptions}"
        binding.subscribersTv.text =
            "${resources.getString(R.string.subscribers_label)} ${profile.subscribers}"
        binding.subscriptionsTv.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong("id", profile.userId)
            bundle.putBoolean("areSubscriptions", true)
            findNavController().navigate(R.id.action_profileFragment_to_subFragment, bundle)
        }
        binding.subscribersTv.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong("id", profile.userId)
            bundle.putBoolean("areSubscriptions", false)
            findNavController().navigate(R.id.action_profileFragment_to_subFragment, bundle)
        }
    }

    fun observeSubResult() {
        profileViewModel.subResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    val prefix = resources.getString(R.string.subscriptions_label)
                    var subscribers = binding.subscribersTv.text.substring(prefix.length + 3).toInt()
                    if (result.data == null) {
                        binding.unsubBtn.visibility = View.GONE
                        binding.subBtn.visibility = View.VISIBLE
                        subscribers -= 1
                    } else {
                        binding.subBtn.visibility = View.GONE
                        binding.unsubBtn.visibility = View.VISIBLE
                        subscribers += 1
                    }
                    binding.unsubBtn.isClickable = true
                    binding.subBtn.isClickable = true
                    binding.subscribersTv.text = "${resources.getString(R.string.subscribers_label)} ${subscribers}"
                }
                ApiStatus.ERROR ->   {
                }
                ApiStatus.LOADING ->  {
                    binding.unsubBtn.isClickable = false
                    binding.subBtn.isClickable = false
                }
            }
        }
    }

    fun refreshActionButtons(userRole: UserRole) {
        var actionButtons = 0
        if (viewerRole == UserRole.SUPER_ADMIN) {
            binding.adminBtn.visibility = View.VISIBLE
            if (userRole == UserRole.ADMIN) {
                binding.adminBtn.text = resources.getString(R.string.demoteAdmin)
                confirmAdminBinding.textView.text = getString(R.string.confirmDemote)
            } else if (userRole == UserRole.ORDINARY) {
                binding.adminBtn.text = resources.getString(R.string.makeAdmin)
                confirmAdminBinding.textView.text = getString(R.string.confirmMakeAdmin)
            }
            actionButtons += 1
        } else {
            binding.adminBtn.visibility = View.GONE
        }
        if ((viewerRole == UserRole.ADMIN || viewerRole == UserRole.SUPER_ADMIN) &&
            userRole == UserRole.ORDINARY) {
            binding.banBtn.visibility = View.VISIBLE
            if (actionButtons == 0) {
                val layoutParams = binding.banBtn.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = PixelConverter.fromDP(requireContext(), 124)
                binding.banBtn.layoutParams = layoutParams
            }
            actionButtons += 1
        } else {
            binding.banBtn.visibility = View.GONE
        }
        if (actionButtons != 0 ) {
            val layoutParams =
                binding.cardView.layoutParams as ViewGroup.MarginLayoutParams
            val dpMargin = if (actionButtons == 2) {65} else {35}
            layoutParams.topMargin = PixelConverter.fromDP(requireContext(), dpMargin)
            binding.cardView.layoutParams = layoutParams
        }
    }

    fun handlePhotoUpload() {
        profileViewModel.uploadPhotoResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    refreshFeed()
                    alertBinding.textView.text = getString(R.string.photoWasLoaded)
                    alertDialog.show()
                }
                ApiStatus.LOADING ->  {

                }
                ApiStatus.ERROR ->  {
                    alertBinding.textView.text = result.message
                    alertDialog.show()
                }
            }
        }
        profileViewModel.uploadAvatarResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    val width = PixelConverter.fromDP(requireContext(), binding.imageCard.layoutParams.width)
                    profileViewModel.getAvatar(profileOwnerId!!, width)
                    alertBinding.textView.text = getString(R.string.avatarWasLoaded)
                    alertDialog.show()
                }
                ApiStatus.LOADING ->  {

                }
                ApiStatus.ERROR ->  {
                    alertBinding.textView.text = result.message
                    alertDialog.show()
                }
            }
        }
    }

    fun observeChangeAdminResult() {
        profileViewModel.changeAdminResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    binding.adminBtn.isClickable = true
                    binding.banBtn.isClickable = true
                    refreshActionButtons(result.data!!)
                }
                ApiStatus.ERROR ->   {
                }
                ApiStatus.LOADING ->  {
                    binding.adminBtn.isClickable = false
                    binding.banBtn.isClickable = false
                }
            }
        }
    }

    fun observeBanUserResult() {
        profileViewModel.banUserResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    findNavController().navigate(R.id.action_profileFragment_to_searchFragment)
                }
                ApiStatus.ERROR ->   {
                }
                ApiStatus.LOADING ->  {
                    binding.adminBtn.isClickable = false
                }
            }
        }
    }

    fun showPhotoChooser(choosingAvatar: Boolean) {
        this.choosingAvatar = choosingAvatar
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        photoLauncher.launch(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeToken()
        observeProfile()
        observeAvatar()
        initRecyclerView()
        observeFeed()
        setRefreshListener()
    }

    fun uploadPhoto(uri: Uri) {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                activity?.contentResolver!!,
                uri
            )
        } else {
            val source = ImageDecoder.createSource(activity?.contentResolver!!, uri)
            ImageDecoder.decodeBitmap(source)
        }
        if (choosingAvatar) {
            profileViewModel.uploadAvatar(accessToken, bitmap)
        } else {
            YandexMetrica.reportEvent(getString(R.string.event_choose_photo_in_profile))
            loadBinding.imageView.setImageBitmap(bitmap)
            loadBinding.buttonConfirm.setOnClickListener {
                profileViewModel.uploadPhoto(accessToken, bitmap)
                loadDialog.dismiss()
                YandexMetrica.reportEvent(getString(R.string.event_confirms_photo_in_profile))
            }
            loadDialog.show()
        }
    }

    fun logout() {
        lifecycleScope.launch {
            tokenStorage.deleteToken()
            findNavController().navigate(R.id.action_profileFragment_to_feedFragment)
        }
    }

    fun observeToken() {
        tokenStorage.token.observe(viewLifecycleOwner) { token ->
            if (token.accessToken != "null") {
                accessToken = token.accessToken
                val jwta = JWT(token.accessToken)
                viewerId = jwta.getClaim("id").asLong()
                if (!startedInit) {
                    getAnotherUserProfile()
                    if (!startedInit) {
                        profileOwnerId = viewerId
                        profileViewModel.getProfile(accessToken, viewerId!!)
                        profileViewModel.getFeed(
                            accessToken,
                            DateFilterType.NONE,
                            SortFilterType.NONE,
                            UserFilterType.SPECIFIED,
                            profileOwnerId,
                            0,
                            pageSize
                        )
                        val width = PixelConverter.fromDP(requireContext(), binding.imageCard.layoutParams.width)
                        profileViewModel.getAvatar(viewerId!!, width)
                        binding.avatarImageView.setOnClickListener{
                            showPhotoChooser(true)
                        }
                        YandexMetrica.reportEvent(getString(R.string.event_view_his_profile))
                        startedInit = true
                    }
                }
                val authorities = jwta.getClaim("authorities").asList(String::class.java)
                if (authorities.contains("MANAGE_ADMINS_AUTHORITY")) {
                    viewerRole = UserRole.SUPER_ADMIN
                } else if (authorities.contains("BAN_USER_AUTHORITY")) {
                    viewerRole = UserRole.ADMIN
                } else if (authorities.contains("UPLOAD_AUTHORITY")) {
                    viewerRole = UserRole.ORDINARY
                }
                initBottomNav(true)
            } else {
                if (!startedInit) {
                    getAnotherUserProfile()
                }
                initBottomNav(false)
            }

        }
    }

    fun getAnotherUserProfile() {
        if (requireArguments().containsKey("id")) {
            profileOwnerId = requireArguments().getLong("id")
            profileViewModel.getProfile(accessToken, profileOwnerId!!)
            profileViewModel.getFeed(
                accessToken,
                DateFilterType.NONE,
                SortFilterType.NONE,
                UserFilterType.SPECIFIED,
                profileOwnerId,
                0,
                pageSize
            )
            val width = PixelConverter.fromDP(requireContext(), binding.imageCard.layoutParams.width)
            profileViewModel.getAvatar(profileOwnerId!!, width)
            YandexMetrica.reportEvent(getString(R.string.event_view_another_profile))
            startedInit = true
        }
    }

    fun initBottomNav(isAuthorised: Boolean) {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_feedFragment)
        }
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_searchFragment)
        }
        if (isAuthorised) {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        } else {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }
    }

    fun initRecyclerView() {
        binding.photoRv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.photoRv.adapter = feedAdapter

        val layoutManager = binding.photoRv.layoutManager as StaggeredGridLayoutManager
        binding.photoRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemsCount = layoutManager.itemCount
                val positions =  layoutManager.findLastVisibleItemPositions(null)
                val lastVisibleItem = positions.max()
                if (!feedAdapter.isLoading && itemsCount == lastVisibleItem + 1 && itemsCount / pageSize == lastPage + 1) {
                    profileViewModel.getFeed(
                        accessToken,
                        DateFilterType.NONE,
                        SortFilterType.NONE,
                        UserFilterType.SPECIFIED,
                        profileOwnerId,
                        itemsCount / pageSize,
                        pageSize
                    )
                    lastPage++
                }
            }
        })
    }

    fun observeFeed() {
        profileViewModel.feedResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS -> {
                    val data = result.data!!
                    feedAdapter.add(data)
                    feedAdapter.isLoading = false
                    binding.refreshLayout.isRefreshing = false

                }
                ApiStatus.ERROR -> {
                    feedAdapter.isLoading = false
                    alertBinding.textView.text = result.message.toString()
                    alertDialog.show()
                }
                ApiStatus.LOADING -> {
                    feedAdapter.isLoading = true
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.avatarImageView.drawable?.toBitmapOrNull()?.recycle()
        binding.avatarImageView.setImageBitmap(null)
        feedAdapter.recycleAll()
        super.onDestroyView()
    }

    fun setRefreshListener() {
        binding.refreshLayout.setOnRefreshListener{
            refreshFeed()
        }
    }

    fun refreshFeed() {
        feedAdapter.clear()
        profileViewModel.getFeed(
            accessToken,
            DateFilterType.NONE,
            SortFilterType.NONE,
            UserFilterType.SPECIFIED,
            profileOwnerId,
            0,
            pageSize
        )
        lastPage = 0
    }
}