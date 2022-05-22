package com.lifegate.app.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.databinding.ProfileFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var navController: NavController
    private lateinit var binding: ProfileFragmentBinding
    private val factory: ProfileViewModelFactory by instance()
    private val PERMISSION_REQUEST_CODE = 200
    val REQUEST_CODE = 100
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    var alUri : MutableList<Uri> = mutableListOf()
    var alInjuryImages : MutableList<String> = mutableListOf()
    var alGoals : MutableList<String> = mutableListOf("Lose Weight","Gain Muscles", "Stay Active", "Increase Strength", "flexibility")
    var alFitness : MutableList<String> = mutableListOf("Beginner","Intermediate", "Advanced")
    var alDays : MutableList<String> = mutableListOf("Sunday","Monday", "Tuesday","Wednesday","Thursday", "Friday","Saturday")
    var alTimes : MutableList<String> = mutableListOf()
    var alSelectedTimes : MutableList<String> = mutableListOf()
    var alEquipmentImages : MutableList<String> = mutableListOf()
    var adapter : ImageAdapter?=null
    var equipmentsAdapter : ImageAdapter?=null
    var isAllergiFileUpload = false
    var goal = ""
    var fitness = ""
    private var mBottomSheetBehaviourImage: BottomSheetBehavior<*>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


        viewModel.fetchProfile()

        var calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE,0)
        val df = SimpleDateFormat("hh:mm aa")
        for (i in 1..48){

            alTimes.add(df.format(calendar.time))
            calendar.add(Calendar.MINUTE, 30);
        }
        initView()
    }

    private fun initView() {
        mBottomSheetBehaviourImage = BottomSheetBehavior.from(binding?.bottomSheet?.nestedScrollViewProfile)
        mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_HIDDEN
        adapter = ImageAdapter(alInjuryImages) {
            gotoSlider(alInjuryImages)
        }
        val layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL,
            false
        )
        binding?.rvInjuryImages!!.setHasFixedSize(true)
        binding?.rvInjuryImages!!.layoutManager = layoutManager
        binding?.rvInjuryImages!!.adapter = adapter

        equipmentsAdapter = ImageAdapter(alEquipmentImages) {
            gotoSlider(alEquipmentImages)
        }
        setArraySpinner(binding?.spinnerGoal,alGoals)
        setArraySpinner(binding?.spinnerFitness,alFitness)
        setArraySpinner(binding?.spinnerDays,alDays)
        setArraySpinner(binding?.spinnerTime,alTimes)
        val layoutManager1 = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL,
            false
        )
        binding?.rvEquipmentImages!!.setHasFixedSize(true)
        binding?.rvEquipmentImages!!.layoutManager = layoutManager1
        binding?.rvEquipmentImages!!.adapter = equipmentsAdapter
        binding?.llInjuriesUpload?.setOnClickListener {
            isAllergiFileUpload = true
            if (checkPermission()){
                initializeBottomSheetImage()
                binding?.rlTransparent!!.visibility = View.VISIBLE
                mBottomSheetBehaviourImage?.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                requestPermission()
            }
        }
        binding?.llEquipmentsUpload?.setOnClickListener {
            isAllergiFileUpload = false
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

        binding?.spinnerGoal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                goal = alGoals[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding?.spinnerFitness.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                fitness = alFitness[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding?.addBtn.setOnClickListener {
            var selectedDayTime = binding?.spinnerDays.selectedItem.toString() + " " + binding?.spinnerTime.selectedItem.toString()
            if (alSelectedTimes.contains(selectedDayTime)){
                Toast.makeText(context,"Already Added",Toast.LENGTH_LONG).show()
            }else{
                alSelectedTimes.add(selectedDayTime)
                var days =""
                alSelectedTimes.forEachIndexed { index, s ->
                    days += if (index < alSelectedTimes.size -1 )
                        "$s,"
                    else
                        s

                }
                binding?.profileAvailableDaysTxt?.setText(days)
            }


        }
        viewModel.liveData.observe(viewLifecycleOwner, Observer { item ->
            val profileData = item
            if (profileData != null) {
                binding.injuryUpload.visibility = View.GONE
                binding.equipmentUpload.visibility = View.GONE
                alUri.clear()
                viewModel?.filePaths.clear()
                viewModel?.filePathsEquipment.clear()
                alEquipmentImages?.clear()
                alInjuryImages?.clear()
                binding.profileNameTxt.setText(item.user_name)
                binding.profileEmailTxt.setText(item.user_mailid)
                binding.profilePhoneTxt.setText(item.user_phone)
                binding.profileCountryTxt.setText(item.user_country)
                binding.profileWeightTxt.setText(item.user_weight)
                binding.profileGenderTxt.setText(item.user_gender)
                binding.profileDobTxt.setText(item.user_dob)
                binding.profileInterestTxt.setText(item.user_interest)
                binding.profileGoalTxt.setText(item.user_goal)
                if (!item.user_goal.isNullOrEmpty()){
                    if (!item.user_goal.equals("nil",ignoreCase = true)){
                        binding?.spinnerGoal.setSelection(alGoals.indexOf(item.user_goal))
                        goal = item.user_goal!!
                    }
                }
                if (!item.user_fitness_level.isNullOrEmpty()){
                    if (!item.user_fitness_level.equals("nil",ignoreCase = true)){
                        binding?.spinnerFitness.setSelection(alFitness.indexOf(item.user_fitness_level))
                        fitness = item.user_fitness_level!!
                    }
                }
                binding.profileWeightGoalTxt.setText(item.user_weight_goal)
                binding.profileFitnessLevelTxt.setText(item.user_fitness_level)
                binding.profileAllergiesTxt.setText(item.user_allergies)
                binding.profileCoachNotesTxt.setText(item.user_notes)
                binding.profileAvailableDaysTxt.setText(item.user_available_days)
                if (item.user_available_days?.contains(",")!!){
                    alSelectedTimes = item.user_available_days!!.split(",").toMutableList()
                }else{
                    alSelectedTimes.add(item.user_available_days!!)
                }
                if (item.user_injuries!=null){
                    if (item.user_injuries?.isNotEmpty()!! ){
                        binding?.rvInjuryImages?.visibility = View.VISIBLE
                        if ( item?.user_injuries?.contains(",")!!){
                            var result: List<String>? = item?.user_injuries?.split(",")?.map { it.trim() }
                            if (result != null) {
                                alInjuryImages.addAll(result)
                            }
                        }else{
                            alInjuryImages.add( item?.user_injuries!!)
                        }
                    }
                }
                adapter?.notifyDataSetChanged()
                if (item.user_equipments!=null){
                    if (item.user_equipments?.isNotEmpty()!! ){
                        binding?.rvEquipmentImages?.visibility = View.VISIBLE
                        if ( item?.user_equipments?.contains(",")!!){
                            var result: List<String>? = item?.user_equipments?.split(",")?.map { it.trim() }
                            if (result != null) {
                                alEquipmentImages.addAll(result)
                            }
                        }else{
                            alEquipmentImages.add(item?.user_equipments!!)
                        }
                    }
                }
                equipmentsAdapter?.notifyDataSetChanged()
                loadCircleImage(binding.profileImg, PRO_IMG_BASE_URL + item.user_pic)
//                loadImage(binding.injuryImg, PRO_IMG_BASE_URL + item.user_injuries)
//                loadImage(binding.equipmentImg, PRO_IMG_BASE_URL + item.user_equipments)
            }
        })

        binding.profileEssentialInfoHeader.setOnClickListener {
            val visibility = binding.profileEssentialInfoContent.visibility
            if (visibility == View.VISIBLE) {
                binding.profileEssentialInfoContent.visibility = View.GONE
            } else {
                binding.profileEssentialInfoContent.visibility = View.VISIBLE
            }
        }

        binding.profileCoachInfoHeader.setOnClickListener {
            val visibility = binding.profileCoachInfoContent.visibility
            if (visibility == View.VISIBLE) {
                binding.profileCoachInfoContent.visibility = View.GONE
            } else {
                binding.profileCoachInfoContent.visibility = View.VISIBLE
            }
        }

        binding.profilePerformanceHeader.setOnClickListener {
            val visibility = binding.profilePerformanceContent.visibility
            if (visibility == View.VISIBLE) {
                binding.profilePerformanceContent.visibility = View.GONE
            } else {
                binding.profilePerformanceContent.visibility = View.VISIBLE
            }
        }

        binding.saveBtn.setOnClickListener {
            viewModel.updateProfile(
                binding.profileNameTxt.text.toString(),
                binding.profileEmailTxt.text.toString(),
                binding.profilePhoneTxt.text.toString(),
                binding.profileWeightTxt.text.toString(),
                binding.profileGenderTxt.text.toString(),
                binding.profileDobTxt.text.toString(),
                binding.profileInterestTxt.text.toString(),
                goal,
                binding.profileWeightGoalTxt.text.toString(),
                fitness,
                binding.profileAllergiesTxt.text.toString(),
                binding.profileCoachNotesTxt.text.toString(),
                binding.profileAvailableDaysTxt.text.toString()
            )
        }

        binding.chart.setTouchEnabled(false)
        binding.chart.setBackgroundColor(Color.rgb(255, 255, 255))
        binding.chart.description.isEnabled = false
        binding.chart.setDrawGridBackground(false)
        val xAxis: XAxis = binding.chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.disableGridDashedLine()
        xAxis.labelRotationAngle = -45F
        xAxis.setDrawGridLines(false)
//        xAxis.textColor = Color.WHITE
//        xAxis.textColor = Color.rgb(255, 192, 56)
//        xAxis.setCenterAxisLabels(true)
//        xAxis.granularity = 1f // one hour

        xAxis.valueFormatter = object : IAxisValueFormatter {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                val millis: Long = value.toLong()
                val label = mFormat.format(Date(millis))
                return label
            }
        }

//        binding.chart.xAxis.isEnabled = false
        binding.chart.axisRight.isEnabled = false
        var yAxis : YAxis = binding.chart.axisLeft
        yAxis.axisMaximum = 2000F
        yAxis.axisMinimum = 0F
        yAxis.labelCount = 4
        binding.chart.invalidate()

        binding.chart2.setTouchEnabled(false)
        binding.chart2.setBackgroundColor(Color.rgb(255, 255, 255))
        binding.chart2.description.isEnabled = false
        binding.chart2.setDrawGridBackground(false)
        val xAxis2: XAxis = binding.chart2.xAxis
        xAxis2.position = XAxis.XAxisPosition.BOTTOM
        xAxis2.textSize = 10f
        xAxis2.disableGridDashedLine()
        xAxis2.labelRotationAngle = -45F
        xAxis2.setDrawGridLines(false)
//        xAxis2.textColor = Color.WHITE
//        xAxis.textColor = Color.rgb(255, 192, 56)
//        xAxis.setCenterAxisLabels(true)
//        xAxis.granularity = 1f // one hour

        xAxis2.valueFormatter = object : IAxisValueFormatter {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                val millis: Long = value.toLong()
                val label = mFormat.format(Date(millis))
                return label
            }
        }
//        binding.chart2.xAxis.isEnabled = false
        binding.chart2.axisRight.isEnabled = false
        var yAxis2 : YAxis = binding.chart2.axisLeft
        yAxis2.axisMaximum = 1F
        yAxis2.axisMinimum = 0F
        yAxis2.labelCount = 1
        binding.chart2.invalidate()

        viewModel.liveGraph.observe(viewLifecycleOwner, Observer { itemList ->
            if (itemList != null && itemList.isNotEmpty()) {
                val endDate = itemList[0].log_date.toString()
                val startDate = itemList[itemList.size - 1].log_date.toString()
                val dateTimeLine = "${setDateMonth(startDate)} - ${setDateMonth(endDate)}"
                binding.burnedLogDateTxt.text = dateTimeLine
                binding.consumedLogDateTxt.text = dateTimeLine

                itemList?.reverse()
                val values1 = mutableListOf<Entry>()
                val values2 = mutableListOf<Entry>()
                for (i in 0 until itemList.size) {
                    try {
                        var burned = 0f
                        if(itemList[i].burned!=null){
                            burned = if (itemList[i].burned.equals("0"))
                                0f
                            else{
                                1f
                            }
                        }
                        var consumed = 0.0f
                        if(itemList[i].consumed!=null)
                            consumed = itemList[i].consumed.toString().toFloat()
                        var dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        var date = dateFormat.parse(itemList[i].log_date)
                        values1.add(Entry(date.time.toFloat(), burned))
                        values2.add(Entry(date.time.toFloat(), consumed))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
                val set1 = LineDataSet(values1, "Burned")
                set1.mode = LineDataSet.Mode.LINEAR
                set1.cubicIntensity = 0.2f
                set1.setDrawFilled(true)
                set1.setDrawCircles(false)
                set1.lineWidth = 1.8f
                set1.circleRadius = 4f
                set1.setCircleColor(Color.rgb(255, 0, 0))
                set1.highLightColor = Color.rgb(244, 128, 128)
                set1.color = Color.rgb(255, 0, 0)
                set1.fillColor = Color.rgb(255, 0, 0)
                set1.fillAlpha = 100
                set1.valueTextColor = Color.TRANSPARENT
                set1.setDrawHorizontalHighlightIndicator(false)

                val set2 = LineDataSet(values2, "Consumed")
                set2.mode = LineDataSet.Mode.LINEAR
                set2.cubicIntensity = 0.2f
                set2.setDrawFilled(true)
                set2.setDrawCircles(false)
                set2.lineWidth = 1.8f
                set2.circleRadius = 4f
                set2.setCircleColor(Color.rgb(55, 183, 254))
                set2.highLightColor = Color.rgb(154, 219, 254)
                set2.color = Color.rgb(55, 183, 254)
                set2.fillColor = Color.rgb(55, 183, 254)
                set2.fillAlpha = 100
                set2.valueTextColor = Color.TRANSPARENT
                set2.setDrawHorizontalHighlightIndicator(false)

                /*val lineData = mutableListOf<ILineDataSet>()
                lineData.add(set1)
                lineData.add(set2)
                val data = LineData(lineData)
                binding.chart.data = null
                binding.chart.data = data
                binding.chart.invalidate()*/

                val lineData = mutableListOf<ILineDataSet>()
                lineData.add(set1)
                val data = LineData(lineData)

                val lineData2 = mutableListOf<ILineDataSet>()
                lineData2.add(set2)
                val data2 = LineData(lineData2)

                binding.chart.data = null
                binding.chart.data = data2
                binding.chart.xAxis.labelCount = values1.size
                binding.chart.invalidate()

                binding.chart2.data = null
                binding.chart2.data = data
                binding.chart2.xAxis.labelCount = values1.size
                binding.chart2.invalidate()
            }
        })
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        if (viewModel.countryName.isNotBlank() && viewModel.countryName.isNotEmpty()) {
            binding.profileCountryTxt.setText(viewModel.countryName)
        } else {
            binding.profileCountryTxt.setText(viewModel.countryId)
        }
        //view?.context?.toast(viewModel.errorMessage)
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
                alUri.clear()
                viewModel?.filePaths.clear()
                viewModel?.filePathsEquipment.clear()
                val uriPathHelper = URIPathHelper()
                val filePath = uriPathHelper.getPath(requireContext(), data?.data!!)
                alUri.add(data?.data!!)

                if (isAllergiFileUpload) {
                    binding.injuryUpload.visibility = View.VISIBLE
                    binding.injuryUpload.setImageURI(data?.data)
                    if (filePath != null && filePath!!.isNotEmpty()) {
                        viewModel?.filePaths.add(filePath)!!

                    }
                } else {
                    binding.equipmentUpload.visibility = View.VISIBLE
                    binding.equipmentUpload.setImageURI(data?.data)
                    if (filePath != null && filePath!!.isNotEmpty()) {
                        viewModel?.filePathsEquipment.add(filePath)!!

                    }
                }

            } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
                alUri.clear()
                viewModel?.filePaths.clear()
                viewModel?.filePathsEquipment.clear()
                val uriPathHelper = URIPathHelper()
                val filePath = uriPathHelper.getPath(requireContext(), image_uri!!)

                alUri.add(image_uri!!)
                if (isAllergiFileUpload) {
                    binding.injuryUpload.visibility = View.VISIBLE
                    binding.injuryUpload.setImageURI(image_uri)
                    if (filePath != null && filePath!!.isNotEmpty()) {
                        viewModel?.filePaths.add(filePath)!!
                    }
                } else {
                    binding.equipmentUpload.visibility = View.VISIBLE
                    binding.equipmentUpload.setImageURI(image_uri)
                    if (filePath != null && filePath!!.isNotEmpty()) {
                        viewModel?.filePathsEquipment.add(filePath)!!
                    }
                }
            }
        } catch (e: Exception) {

        } catch (e: java.lang.Exception) {

        }

    }

    fun gotoSlider(images : List<String>){
        var imageList = mutableListOf<SlideModel>()
        for (i in images){
            imageList.add(SlideModel(PRO_IMG_BASE_URL + i,""))
        }
        val action = ProfileFragmentDirections.actionProfileFragmentToSliderActivity(
            Gson().toJson(imageList)
        )
        navController.safeNavigate(action)
    }
}