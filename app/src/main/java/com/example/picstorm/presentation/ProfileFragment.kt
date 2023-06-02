package com.example.picstorm.presentation

import android.app.Activity
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.example.picstorm.R
import com.example.picstorm.databinding.FragmentProfileBinding
import com.example.picstorm.domain.TokenStorage
import com.example.picstorm.domain.model.enums.UserRole
import com.example.picstorm.util.ApiStatus
import com.example.picstorm.util.PixelConverter
import com.example.picstorm.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage
    private var startedInit = false
    private var viewerRole = UserRole.UNAUTHORIZED
    private var choosingAvatar = false
    private var viewerId: Long? = null
    private var accessToken: String? = null

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
        return binding.root
    }

    fun observeProfile() {
        profileViewModel.profileResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS -> {
                    val profile = result.data!!
                    binding.nicknameTv.text = profile.name
                    binding.avatarImageView.setImageBitmap(profile.avatar)
                    binding.subscriptionsTv.text =
                        "${resources.getString(R.string.subscriptions_label)} ${profile.subscriptions}"
                    binding.subscribersTv.text =
                        "${resources.getString(R.string.subscribers_label)} ${profile.subscribers}"
                    if (viewerId != profile.userId) {
                        binding.banBtn.setOnClickListener{
                            profileViewModel.banUser(accessToken, profile.userId)
                        }
                        binding.adminBtn.setOnClickListener{
                            profileViewModel.changeAdmin(accessToken, profile.userId)
                        }
                        observeChangeAdminResult()
                        observeBanUserResult()
                        refreshActionButtons(profile.role)
                    } else {
                        binding.logoutBtn.visibility = View.VISIBLE
                        binding.logoutBtn.setOnClickListener {
                            logout()
                        }
                        binding.photoLoadBtn.visibility = View.VISIBLE
                        binding.photoLoadBtn.setOnClickListener {
                            showPhotoChooser(false)
                        }
                        binding.avatarImageView.setOnClickListener{
                           showPhotoChooser(true)
                        }
                    }
                    if (profile.subscribed == true) {
                        binding.unsubBtn.visibility = View.VISIBLE
                    } else if (profile.subscribed == false) {
                        binding.subBtn.visibility = View.VISIBLE
                    }
                }

                ApiStatus.ERROR -> {
                }

                ApiStatus.LOADING -> {
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
            } else if (userRole == UserRole.ORDINARY) {
                binding.adminBtn.text = resources.getString(R.string.makeAdmin)
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

    fun observeChangeAdminResult() {
        profileViewModel.changeAdminResult.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                ApiStatus.SUCCESS ->  {
                    binding.adminBtn.isClickable = true
                    refreshActionButtons(result.data!!)
                }
                ApiStatus.ERROR ->   {
                }
                ApiStatus.LOADING ->  {
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
        initBottomNav()

        binding.subscriptionsTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_feedFragment)
        }
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
            profileViewModel.uploadPhoto(accessToken, bitmap)
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
                        profileViewModel.getProfile(accessToken, viewerId!!)
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
            } else if (!startedInit) {
                getAnotherUserProfile()
            }
        }
    }

    fun getAnotherUserProfile() {
        if (requireArguments().containsKey("id")) {
            val userId = requireArguments().getLong("id")
            profileViewModel.getProfile(accessToken, userId)
            startedInit = true
        }
    }

    fun initBottomNav() {
        binding.bottomNav.binding.imageList.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_feedFragment)
        }
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_searchFragment)
        }
    }
}