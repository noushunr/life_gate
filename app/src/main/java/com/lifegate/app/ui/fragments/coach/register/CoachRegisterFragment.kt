package com.lifegate.app.ui.fragments.coach.register

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lifegate.app.R
import com.lifegate.app.databinding.CoachRegisterFragmentBinding
import com.lifegate.app.databinding.CoachesFragmentBinding
import com.lifegate.app.ui.fragments.coach.list.CoachesViewModelFactory
import com.lifegate.app.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber
import java.io.File

class CoachRegisterFragment : Fragment(), KodeinAware,NetworkListener {

    companion object {
        fun newInstance() = CoachRegisterFragment()
    }
    override val kodein by kodein()
    private lateinit var viewModel: CoachRegisterViewModel
    private lateinit var navController: NavController
    private val factory: CoachesRegisterViewModelFactory by instance()
    private lateinit var binding: CoachRegisterFragmentBinding

    private lateinit var coachTypeMenu: PopupMenu
    private lateinit var countryMenu: PopupMenu
    private val PERMISSION_REQUEST_CODE = 200
    val REQUEST_CODE = 100
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    var alUri : MutableList<Uri> = mutableListOf()
    var adapter :ImageUploadAdapter?=null
    private var mBottomSheetBehaviourImage: BottomSheetBehavior<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this,factory).get(CoachRegisterViewModel::class.java)
        viewModel.listener = this
        binding = CoachRegisterFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        initView()
        viewModel?.fetchCountry()
        viewModel?.fetchCoachType()

        adapter = ImageUploadAdapter(requireContext(),alUri)
        val layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL,
            false
        )
        binding?.rvImages!!.setHasFixedSize(true)
        binding?.rvImages!!.layoutManager = layoutManager
        binding?.rvImages!!.adapter = adapter
        lifecycleScope.launch(Dispatchers.Main) {
            delay(3000)
            //goToNextFrag()
        }
    }

    private fun initView() {
        coachTypeMenu = PopupMenu(requireContext(), binding.etType)
        countryMenu = PopupMenu(requireContext(), binding.etCountry)

        mBottomSheetBehaviourImage = BottomSheetBehavior.from(binding?.bottomSheet?.nestedScrollViewProfile)
        mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_HIDDEN
        binding?.etCountry?.setOnClickListener {
            countryMenu.show()
        }
        binding?.etType?.setOnClickListener {
            coachTypeMenu.show()
        }
        binding?.llUpload?.setOnClickListener {
            if (checkPermission()){
                initializeBottomSheetImage()
                binding?.rlTransparent!!.visibility = View.VISIBLE
                mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                requestPermission()
            }
        }
        binding?.bottomSheet?.ivCloseGallery!!.setOnClickListener {
            mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_HIDDEN

        }
        binding?.bottomSheet?.llCamera!!.setOnClickListener {
            mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_HIDDEN
            openCamera()
        }
        binding?.bottomSheet?.llGallery!!.setOnClickListener {
            mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_HIDDEN
            openGalleryForImage()
        }
        val coachTypeListMap = mutableMapOf<String, String>()
        viewModel.liveCoachType.observe(viewLifecycleOwner, Observer { itemList ->
//            coachTypeAdapter.submitList(viewModel.coachTypeList)
            coachTypeMenu.menu.clear()

            val modelList = arrayListOf<CharSequence>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.type.toString())
                    coachTypeListMap[data.type.toString()] = data.coach_type_id.toString()
                    coachTypeMenu.menu.add(data.type.toString())
                }
            }
        })
        coachTypeMenu.setOnMenuItemClickListener { item ->
            coachTypeListMap[item?.title]?.let { modelId ->
                viewModel.type = modelId
            }
            binding?.etType.setText( item?.title!!)
            true
        }

        val modelListMap = mutableMapOf<String, String>()
        viewModel.liveCountry.observe(viewLifecycleOwner, Observer { itemList ->
            hideProgress()
            countryMenu.menu.clear()
            val modelList = arrayListOf<String>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.name.toString())
                    modelListMap[data.name.toString()] = data.id.toString()
                    countryMenu.menu.add(data.name.toString())
                }
            }
        })
        countryMenu.setOnMenuItemClickListener { item ->
            modelListMap[item?.title]?.let { modelId ->
                viewModel.country = modelId

            }
            binding?.etCountry.setText( item?.title!!)
            true
        }
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
        if (viewModel?.isCoachRegistration)
            navController.safePopBackStack()
    }

    override fun onFailure() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
    }

    override fun onError() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
    }

    override fun onNoNetwork() {
        hideProgress()
        view?.context?.toast(viewModel.errorMessage)
    }

    // Function to check and request permission
    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result1 = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val result2 = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
            return  result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    initializeBottomSheetImage()
                    binding?.rlTransparent!!.visibility = View.VISIBLE
                    mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_EXPANDED

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        requireContext(),
                        "Permission denied to read your External storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    //region initialize bottom sheet for change password
    private fun initializeBottomSheetImage() {
        mBottomSheetBehaviourImage!!.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        binding?.layoutTransparent!!.visibility = View.VISIBLE
                        binding?.rlTransparent!!.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding?.rlTransparent!!.visibility = View.GONE
                        // update collapsed button text
//                        toggleBottomSheet.setText("Collapse BottomSheet")
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding?.rlTransparent!!.visibility = View.GONE

//                        mBottomSheetBehaviour?.state = STATE_COLLAPSED

                        mBottomSheetBehaviourImage?.peekHeight = 0
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
//                        Log.d("", "State Dragging");
                    }
                }
            }

        })
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
                val uriPathHelper = URIPathHelper()
                val filePath = uriPathHelper.getPath(requireContext(), data?.data!!)
                alUri.add(data?.data!!)
                if (filePath != null && filePath!!.isNotEmpty()) {
                    viewModel?.filePaths.add(filePath)!!
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
                val uriPathHelper = URIPathHelper()
                val filePath = uriPathHelper.getPath(requireContext(), image_uri!!)
                alUri.add(image_uri!!)
                if (filePath != null && filePath!!.isNotEmpty()) {
                    viewModel?.filePaths.add(filePath)!!

                }
            }

            adapter?.notifyDataSetChanged()

        } catch (e: Exception) {

        } catch (e: java.lang.Exception) {

        }

    }
}