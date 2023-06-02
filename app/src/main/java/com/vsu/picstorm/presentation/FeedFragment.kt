package com.vsu.picstorm.presentation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.vsu.picstorm.R
import com.vsu.picstorm.databinding.FragmentFeedBinding
import com.vsu.picstorm.domain.TokenStorage
import com.vsu.picstorm.presentation.adapter.FeedAdapter
import com.vsu.picstorm.viewmodel.FeedViewModel
import com.skydoves.powerspinner.PowerSpinnerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val feedViewModel: FeedViewModel by viewModels()
    private lateinit var tokenStorage: TokenStorage
    private lateinit var feedSpinner: PowerSpinnerView
    private lateinit var filterDateSpinner: PowerSpinnerView
    private lateinit var filterRatingSpinner: PowerSpinnerView

    private var accessToken: String? = null

    private val feedAdapter = FeedAdapter()
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
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        tokenStorage = TokenStorage(this.requireContext())

        feedSpinner = binding.feedSpinner
        filterDateSpinner = binding.filterDateSpinner
        filterRatingSpinner = binding.filterRatingSpinner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeToken()

        initRecyclerView()

        initFilterDateSpinner()
        initFilterRatingSpinner()
    }

    private fun observeToken() {
        tokenStorage.token.observe(viewLifecycleOwner) { token ->
            if (token.accessToken != "null") {
                val jwta = JWT(token.accessToken)
                if (jwta.isExpired(0)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        tokenStorage.deleteToken()
                    }
                } else {
                    accessToken = token.accessToken
                    val authorities = jwta.getClaim("authorities").asList(String::class.java)
                    if (authorities.contains("UPLOAD_AUTHORITY")) {
                        initPhotoLoadBtnAuthorized()
                        initBottomNav(true)
                        initFeedSpinner()
                    } else {
                        initPhotoLoadBtn()
                        initBottomNav(false)
                    }
                    Log.i(
                        "Access",
                        jwta.getClaim("authorities").asList(String::class.java).toString()
                    )
                    Log.i("Access", jwta.subject.toString())
                    Log.i("Access", jwta.expiresAt.toString())
                }
            } else {
                accessToken = null
                initPhotoLoadBtn()
                initBottomNav(false)
            }
        }
    }

    fun initRecyclerView() {
        binding.feedRv.adapter = feedAdapter
    }

    fun initPhotoLoadBtn() {
        binding.photoLoadBtn.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
        }
    }

    fun initPhotoLoadBtnAuthorized() {
        binding.photoLoadBtn.setOnClickListener {
            showFileChooser()
        }
    }

    fun showFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        photoLauncher.launch(intent)
    }

    fun uploadPhoto(uri: Uri) {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                activity?.contentResolver!!,
                uri
            )
            //imageView.setImageBitmap(bitmap)
        } else {
            val source = ImageDecoder.createSource(activity?.contentResolver!!, uri)
            ImageDecoder.decodeBitmap(source)
            //imageView.setImageBitmap(bitmap)
        }
        feedViewModel.loadPhoto(accessToken, bitmap)
    }

    fun initBottomNav(isAuthorised: Boolean) {
        binding.bottomNav.binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_searchFragment)
        }
        if (isAuthorised) {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_profileFragment)
            }
        } else {
            binding.bottomNav.binding.imageUser.setOnClickListener {
                findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
            }
        }

    }

    fun initFeedSpinner() {
        with(feedSpinner) {
            visibility = View.VISIBLE
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
                if (newItem == resources.getString(R.string.global)) {
                    setItems(R.array.feedSpinnerPersonal)
                }
                if (newItem == resources.getString(R.string.personal)) {
                    setItems(R.array.feedSpinnerGlobal)
                }
                setBackgroundResource(R.drawable.feed_spinner_shape)
            }
        }
    }

    fun initFilterDateSpinner() {
        with(filterDateSpinner) {
            setItems(R.array.filterDateSpinner)
            setHint(R.string.dateFilter)
            setOnClickListener {
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

    fun initFilterRatingSpinner() {
        with(filterRatingSpinner) {
            setItems(R.array.filterRatingSpinner)
            setHint(R.string.ratingFilter)
            setOnClickListener {
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

    override fun onStop() {
        filterDateSpinner.dismiss()
        filterRatingSpinner.dismiss()
        feedSpinner.dismiss()
        super.onStop()
    }
}