package com.lifegate.app.ui.fragments.workout.plan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.allanwang.kau.mediapicker.kauLaunchMediaPicker
import ca.allanwang.kau.mediapicker.kauOnMediaPickerResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.lifegate.app.VideoPickerActivityOverlay
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.MyWorkoutPlanApi
import com.lifegate.app.databinding.WorkoutPlanFragmentBinding
import com.lifegate.app.ui.fragments.coach.detail.CoachDetailFragmentDirections
import com.lifegate.app.utils.*
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.EasyImage


class WorkoutPlanFragment : Fragment(), KodeinAware, NetworkListener,
    WorkoutPlanSubAdapter.WorkoutClickListener,SliderAdapter.SliderClickListener {

    companion object {
        fun newInstance() = WorkoutPlanFragment()
        private const val PHOTOS_KEY = "easy_image_photos_list"
        private const val STATE_KEY = "easy_image_state"
        private const val CHOOSER_PERMISSIONS_REQUEST_CODE = 7459
        private const val GALLERY_REQUEST_CODE = 7502
        private const val DOCUMENTS_REQUEST_CODE = 7503
        private const val LEGACY_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 456
        private val LEGACY_WRITE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_MEDIA = 27
    }

    override val kodein by kodein()

    private lateinit var viewModel: WorkoutPlanViewModel
    private lateinit var navController: NavController
    private lateinit var binding: WorkoutPlanFragmentBinding
    private val factory: WorkoutPlanViewModelFactory by instance()

    private lateinit var adapter: WorkoutPlanAdapter

    private lateinit var easyImage: EasyImage

    private var timer: CountDownTimer?=null
    private var mCounter = 0
    private var totalCounter = 0
    private var totalTime = 0

    private lateinit var newItem: MyWorkoutPlanApi.MyWorkoutPlanSubTitle

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var equipmentAdapter: EquipmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(WorkoutPlanViewModel::class.java)
        binding = WorkoutPlanFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        androidx.core.view.ViewCompat.setNestedScrollingEnabled(
            binding.bottomSheet.equipmentRecyclerview,
            true
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetEquipmentLayout)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.rlTransparent.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.rlTransparent.visibility = View.GONE
                        bottomSheetBehavior.setPeekHeight(0)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        val safeArgs: WorkoutPlanFragmentArgs by navArgs()
        viewModel.planId = safeArgs.planId
        viewModel.purchaseId = safeArgs.purchaseId

        adapter = WorkoutPlanAdapter(this)

        binding.workoutMainRecyclerView.adapter = adapter

        binding.notes.close.setOnClickListener {
            binding.notes.overlay.visibility = View.GONE
        }

        binding.weight.close.setOnClickListener {
            binding.weight.overlay.visibility = View.GONE
        }

        if (viewModel.isFirst) {
            viewModel.fetchMyWorkoutPlan()
            viewModel.isFirst = false
        }

        binding.goToHistory.setOnClickListener {
            goToNextFrag()
        }

        binding.goToPlan.setOnClickListener {
            goToFlowChart()
        }

        equipmentAdapter = EquipmentAdapter {
            println(it.equipments_name)
        }

        binding.bottomSheet.equipmentRecyclerview.adapter = equipmentAdapter

        initView()

        if (isLegacyExternalStoragePermissionRequired()) {
            requestLegacyWriteExternalStoragePermission()
        }
    }

    private fun initView() {

        viewModel.liveMyPlan.observe(viewLifecycleOwner, Observer { item ->
            adapter.submitList(viewModel.myPlanList)
            if (item != null && item.size > 0) {
                hideProgress()
            }
        })

        viewModel.liveEquipment.observe(viewLifecycleOwner, Observer { item ->
            if (viewModel.totalTime.isNotEmpty()) {
                var timeInHr  = viewModel.totalTime.toDouble()?.div(60)
                if (timeInHr != 0.0) {
                    val time = String.format("%.2f", timeInHr)
                    binding.totalTimeTxt.text = "Total Time\n $time hr"
                }
            }
//            binding.totalTimeTxt.text = "Total Time\n${viewModel.totalTime} min"
            binding.calBurnedTxt.text = "Calories Burned \n${viewModel.caloriesBurned} cal"
        })
        viewModel?.liveEquipmentList?.observe(viewLifecycleOwner, {
            equipmentAdapter?.submitList(it)
        })

        viewModel.liveBannerList.observe(viewLifecycleOwner, Observer { item ->
            val adapter = SliderAdapter(item, requireContext(),this)
            binding.bannerSliderView.also {
                it.stopAutoCycle()
                it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                it.setIndicatorAnimation(IndicatorAnimationType.WORM)
                it.indicatorSelectedColor = Color.WHITE
                it.indicatorUnselectedColor = Color.GRAY
                it.scrollTimeInSec = 3
                it.setSliderAdapter(adapter)
                it.isAutoCycle = true
                it.startAutoCycle()
                it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            }
        })

        binding.performWorkout.close.setOnClickListener {
            timer?.cancel()
            binding.performWorkout.startBtn.text = "Start"
            binding.performWorkout.performWorkoutOverlay.visibility = View.GONE
        }

        binding.notes.submitBtn.setOnClickListener {
            if (viewModel.getLoginStatus()) {
                val valueTxt = binding.notes.valueTxt.text
                if (valueTxt.isNullOrBlank() || valueTxt.isNullOrEmpty()) {
                    view?.context?.toast("Value cant be blank")
                } else {
                    viewModel.notes = valueTxt.toString()
                    viewModel.updateWorkoutPlanNotes(newItem)
                }
            } else {
                view?.context?.toast("Please login to continue")
            }
            binding.notes.overlay.visibility = View.GONE
        }

        binding.weight.submitBtn.setOnClickListener {
            if (viewModel.getLoginStatus()) {
                val valueTxt = binding.weight.valueTxt.text
                if (valueTxt.isNullOrBlank() || valueTxt.isNullOrEmpty()) {
                    view?.context?.toast("Value cant be blank")
                } else {
                    viewModel.weight = valueTxt.toString()
                    viewModel.updateWorkoutPlanWeight(newItem)
                }
            } else {
                view?.context?.toast("Please login to continue")
            }
            binding.weight.overlay.visibility = View.GONE
        }

        binding.planEquipment.setOnClickListener {
            showBottomSheet()
        }
        binding?.rlTransparent?.setOnClickListener {
            binding?.rlTransparent?.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun showBottomSheet() {

//        equipmentAdapter.submitList(viewModel.equipmentList)

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            binding?.rlTransparent?.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            binding?.rlTransparent?.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setUpTimer() {

        val timeMilliSeconds  = (totalTime * 1000).toLong()

        if (timer!=null){
            timer?.cancel()
        }
        timer = object : CountDownTimer(timeMilliSeconds, 1000) {
            override fun onFinish() {
                val timeSec = totalTime
                val hour = (timeSec/3600)
                val min = ((timeSec - (hour * 3600))/60)
                val sec = (timeSec - (hour * 3600) - (min * 60))
                val timeMmSs = "%02d:%02d".format(min, sec)
                binding.performWorkout.startBtn.text = "Start"
                totalCounter--
                binding.performWorkout.timerTxt.text = timeMmSs
                binding.performWorkout.counterTxt.text = "$totalCounter"
                val progress = ((mCounter - totalCounter) * 100 ) / mCounter
                binding.performWorkout.counterProgress.progress = progress.toFloat()
                binding.performWorkout.timerProgress.progress = 0f
            }

            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished/1000
                val timeLapsed = totalTime - timeLeft
                try {
                    val hour = (timeLapsed/3600)
                    val min = ((timeLapsed - (hour * 3600))/60)
                    val sec = (timeLapsed - (hour * 3600) - (min * 60))
                    val timeMmSs = "%02d:%02d".format(min, sec)
                    binding.performWorkout.timerTxt.text = timeMmSs
                    val progress = ((timeLapsed) * 100 ) / totalTime
                    binding.performWorkout.timerProgress.progress = progress.toFloat()
                } catch (e: Exception) {
                    //println(e.message.toString())
                }
            }
        }
    }

    private fun goToNextFrag() {
        //val action = WorkoutPlanFragmentDirections.actionWorkoutPlanFragmentToHistoryFragment()
        val action = WorkoutPlanFragmentDirections.actionWorkoutPlanFragmentToWorkoutHistoryFragment(
            viewModel.planId, viewModel.purchaseId
        )
        navController.safeNavigate(action)
    }

    private fun goToFlowChart() {
        val action = WorkoutPlanFragmentDirections.actionWorkoutPlanFragmentToFlowChartFragment(
            viewModel.planId, viewModel.purchaseId
        )
        navController.safeNavigate(action)
    }

    private fun setupEasyImage(msg: String) {
        easyImage = EasyImage.Builder(requireContext())
            .setChooserTitle(msg)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .build()

        if (isLegacyExternalStoragePermissionRequired()) {
            requestLegacyWriteExternalStoragePermission()
        } else {
            //easyImage.openCameraForVideo(requireActivity())
            requireActivity().kauLaunchMediaPicker<VideoPickerActivityOverlay>(REQUEST_MEDIA)
        }
    }
    private fun isLegacyExternalStoragePermissionRequired(): Boolean {
        val permissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return Build.VERSION.SDK_INT < 29 && !permissionGranted
    }

    private fun requestLegacyWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            LEGACY_WRITE_PERMISSIONS,
            LEGACY_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun setupPerformWorkout(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        mCounter = 0
        totalCounter = 0
        totalTime = 0
        binding.performWorkout.totalTimeTxt.text = "Total Time\n" + item.workoutsublitle_time + " min"
        binding.performWorkout.counterTxt.text = item.workoutsublitle_sets
        binding.performWorkout.timerTxt.text = "00:00"
        binding.performWorkout.performWorkoutSubmitBtn.setOnClickListener(null)

        var count = 0
        try {
            val set = item.workoutsublitle_sets
            val time = item.workoutsublitle_time
            if (set != null && time != null) {
                count = set.toInt()
                mCounter = count
                val timeMin = time.toDouble().toInt()
                val timeSec = (timeMin * 60 )/count
                val hour = (timeSec/3600)
                val min = ((timeSec - (hour * 3600))/60)
                val sec = (timeSec - (hour * 3600) - (min * 60))
                val timeMmSs = "%02d:%02d".format(min, sec)
                totalCounter = count
                totalTime = timeSec?.toInt()
                binding.performWorkout.timerTxt.text = timeMmSs
                binding.performWorkout.startBtn.setOnClickListener{
                    setUpTimer()
                    val btnText = binding.performWorkout.startBtn.text.toString()
                    if (btnText.equals("Start", ignoreCase = true)) {
                        if (totalCounter > 0) {
                            timer?.start()
                            binding.performWorkout.startBtn.text = "Stop"
                        }
                    } else {

                        timer?.cancel()
                        binding.performWorkout.startBtn.text = "Start"
                        totalCounter--
                        binding.performWorkout.timerTxt.text = timeMmSs
                        binding.performWorkout.counterTxt.text = "$totalCounter"
                        val progress = ((count - totalCounter) * 100 ) / count
                        binding.performWorkout.counterProgress.progress = progress.toFloat()
                        binding.performWorkout.timerProgress.progress = 0f
                    }
                }

            }
        } catch (e: Exception) {

        }

        binding.performWorkout.performWorkoutSubmitBtn.setOnClickListener {
            timer?.cancel()
            binding.performWorkout.startBtn.text = "Start"
            if (totalCounter>0){
                totalCounter--
                binding.performWorkout.counterTxt.text = "$totalCounter"
                val progress = ((count - totalCounter) * 100 ) / count
                binding.performWorkout.counterProgress.progress = progress.toFloat()
            }
            viewModel.updateWorkoutPlanStatus(item)

        }
        binding.performWorkout.startBtn.text = "Start"
        binding.performWorkout.performWorkoutOverlay.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_MEDIA -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val list = requireActivity().kauOnMediaPickerResult(resultCode, data)
                    if (list.isNotEmpty()) {
                        println(list[0].displayName)
                        val pdfFile = FilesObj.pickedExistingPicture(requireContext(), list[0].uri)
                        println(pdfFile.absolutePath)
                        println(pdfFile.totalSpace)
                        println(pdfFile.name)
                        println(list[0].mimeType)

                        println(list[0].data)
                        viewModel.selectedImageUri = list[0].uri
                        viewModel.videoFile = pdfFile
                        viewModel.mimeType = list[0].mimeType
                        viewModel.displayName = list[0].displayName.toString()
                        viewModel.uploadWorkoutPlanVideo()
                    }
                }
            }
        }
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        //view?.context?.toast(viewModel.errorMessage)
        if (viewModel.totalTime.isNotEmpty()) {
            var timeInHr  = viewModel.totalTime.toDouble()?.div(60)
            if (timeInHr != 0.0) {
                val time = String.format("%.2f", timeInHr)
                binding.totalTimeTxt.text = "Total Time\n $time hr"
            }
        }
        binding.calBurnedTxt.text = "Calories\nBurned ${viewModel.caloriesBurned}cal"
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

    override fun onStatusClick(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        viewModel.updateWorkoutPlanStatus(item)
    }

    override fun onUploadClick(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        viewModel.itemCopy = item
        setupEasyImage("Select Video")
    }

    override fun onAddWeightClick(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        newItem = item
        binding.weight.overlay.visibility = View.VISIBLE

    }

    override fun onPerformWorkoutClick(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        setupPerformWorkout(item)
    }

    override fun onNotesClick(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        newItem = item
        binding.notes.overlay.visibility = View.VISIBLE
    }

    override fun onImageClick(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {
        var imageList = mutableListOf<SlideModel>()
        if (item.workoutsublitle_pics!=null && item.workoutsublitle_pics?.isNotEmpty()!!){
            if ( item.workoutsublitle_pics?.contains(",")!!){
                var result: List<String>? = item.workoutsublitle_pics?.split(",")?.map { it.trim() }

                if (result?.size!! >0){
                    for (i in result!!){
                        imageList?.add(SlideModel(PRO_IMG_BASE_URL + i, ""))
                    }
                }

            }else{
                imageList?.add(SlideModel(PRO_IMG_BASE_URL + item.workoutsublitle_pics, ""))
            }
        }
        val action = WorkoutPlanFragmentDirections.actionWorkoutPlanFragmentToSliderActivity(
            Gson().toJson(imageList)
        )
        navController.safeNavigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            timer?.cancel()
        } catch (e: Exception) {

        }
    }
    private fun goToSliderActivity() {
        val action = WorkoutPlanFragmentDirections.actionWorkoutPlanFragmentToSliderActivity(
            Gson().toJson(viewModel.bannerList)
        )
        navController.safeNavigate(action)
    }
    override fun onSliderClick(item: SlideModel) {
        goToSliderActivity()

    }

}