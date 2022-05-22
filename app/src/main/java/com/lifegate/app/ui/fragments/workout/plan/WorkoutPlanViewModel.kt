package com.lifegate.app.ui.fragments.workout.plan

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.EquipmentListApi
import com.lifegate.app.data.network.responses.HomeAdsApi
import com.lifegate.app.data.network.responses.MyWorkoutPlanApi
import com.lifegate.app.data.network.responses.WorkoutPlanBlocksApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

class WorkoutPlanViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""
    var isFirst = true

    fun getLoginStatus() = repository.getLoginStatus()

    var planId: String = ""
    var purchaseId: String = ""

    var totalTime: String = "0"
    var caloriesBurned: String = "0"

    var weight = ""
    var notes = ""

    var myPlanList = mutableListOf<MyWorkoutPlanApi.MyWorkoutPlanMainTitle>()

    lateinit var itemCopy: MyWorkoutPlanApi.MyWorkoutPlanSubTitle

    private val mutableMyPlan = MutableLiveData<MutableList<MyWorkoutPlanApi.MyWorkoutPlanMainTitle>>()
    private val mutableEquipment = MutableLiveData<MutableList<EquipmentListApi.EquipmentData>>()
    var equipmentList = MutableLiveData<MutableList<EquipmentListApi.EquipmentData>>()
    private val mutableBannerList = MutableLiveData<MutableList<SlideModel>>()
    var bannerList = mutableListOf<SlideModel>()

    val liveMyPlan : LiveData<MutableList<MyWorkoutPlanApi.MyWorkoutPlanMainTitle>>
        get() = mutableMyPlan
    val liveEquipment : LiveData<MutableList<EquipmentListApi.EquipmentData>>
        get() = mutableEquipment
    val liveEquipmentList : LiveData<MutableList<EquipmentListApi.EquipmentData>>
        get() = equipmentList
    val liveBannerList : LiveData<MutableList<SlideModel>>
        get() = mutableBannerList

    var videoFile: File? = null
    var mimeType = ""
    var displayName = ""
    var selectedImageUri: Uri? = null

    var firstTime = true

    fun fetchMyWorkoutPlan() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyWorkoutPlan(planId, purchaseId)

                checkWorkoutPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyWorkoutPlanApi.MyWorkoutPlanResponse = fromJson(error.message.toString())

                    checkWorkoutPlanResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    private fun checkWorkoutPlanResponse(response : MyWorkoutPlanApi.MyWorkoutPlanResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        val list = data?.generation?.current_set?.mywplanmaintitle

        errorMessage = message.toString()

        if (data != null  && status != null && status && !list.isNullOrEmpty()) {
            myPlanList = list!!
            mutableMyPlan.value = list!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())

        if (firstTime) {
            fetchPlanBlocks()
        }
    }

    fun fetchPlanBlocks() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyWorkoutPlanBlock(planId, purchaseId)

                checkPlanBlocksResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : WorkoutPlanBlocksApi.WorkoutPlanBlocksResponse = fromJson(error.message.toString())

                    checkPlanBlocksResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    private fun checkPlanBlocksResponse(response : WorkoutPlanBlocksApi.WorkoutPlanBlocksResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            val time = data.total_time
            val cal = data.calories
            val equip = data.equipments

            if (time != null) {
                totalTime = time
            }
            if (cal != null) {
                caloriesBurned = cal
            }
//            if (equip != null) {
//                mutableEquipment.value = equip!![0]
//                equipmentList.value = equip!![0]
//            }
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()


        }
        fetchEquipments()
        println(response.toString())

        //fetchEquipments()
    }

    fun updateWorkoutPlanStatus(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userUpdateStatusWorkoutPlan(
                    planId, purchaseId, item.workoutsublitle_id.toString(), "1"
                )

                checkWorkoutPlanStatusResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyWorkoutPlanApi.MyWorkoutPlanResponse = fromJson(error.message.toString())

                    checkWorkoutPlanStatusResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    fun uploadWorkoutPlanVideo() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val plan = RequestBody.create(MediaType.parse("multipart/form-data"), planId)
                val purchase = RequestBody.create(MediaType.parse("multipart/form-data"), purchaseId)
                val subtitleId = try {
                    val fId = itemCopy.workoutsublitle_id.toString()
                    RequestBody.create(MediaType.parse("multipart/form-data"), fId)
                } catch (e: Exception) {
                    null
                }

                val mFront = videoFile
                var videoReqBody : RequestBody? = null
                val vidFile = when (mFront) {
                    null -> null
                    else -> {
                        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mFront)
                        videoReqBody = requestFile
                        MultipartBody.Part.createFormData("video", mFront.name, requestFile)
                    }
                }

                val response = repository.userVideoUploadWorkoutPlan(
                    plan, purchase, subtitleId, vidFile
                )

                /*val response = repository.userVideoUploadWorkoutPlanV2(
                    planId, purchaseId, itemCopy.workoutsublitle_id.toString(), videoReqBody
                )*/

                /*val parcelFileDescriptor =
                    appContext.contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return@main

                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val file = File(appContext.cacheDir, displayName)
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)

                val body = UploadRequestBody(file, "video", null)
                val part = MultipartBody.Part.createFormData("video", file.name, body)

                val response = repository.userVideoUploadWorkoutPlan(
                    plan, purchase, subtitleId, part
                )*/

                checkWorkoutPlanStatusResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyWorkoutPlanApi.MyWorkoutPlanResponse = fromJson(error.message.toString())

                    checkWorkoutPlanStatusResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    fun updateWorkoutPlanWeight(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userWorkoutAddWeight(
                    planId, purchaseId, item.workoutsublitle_id.toString(), weight
                )

                checkWorkoutPlanStatusResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyWorkoutPlanApi.MyWorkoutPlanResponse = fromJson(error.message.toString())

                    checkWorkoutPlanStatusResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    fun updateWorkoutPlanNotes(item: MyWorkoutPlanApi.MyWorkoutPlanSubTitle) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userWorkoutAddNotes(
                    planId, purchaseId, item.workoutsublitle_id.toString(), notes
                )

                checkWorkoutPlanStatusResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyWorkoutPlanApi.MyWorkoutPlanResponse = fromJson(error.message.toString())

                    checkWorkoutPlanStatusResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    private fun checkWorkoutPlanStatusResponse(response : MyWorkoutPlanApi.MyWorkoutPlanResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            listener?.onSuccess()
            listener?.onFailure()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        firstTime = false

        fetchMyWorkoutPlan()

        println(response.toString())
    }

    fun fetchEquipments() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userEquipmentList()

                checkEquipmentsResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : EquipmentListApi.EquipmentListResponse = fromJson(error.message.toString())

                    checkEquipmentsResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    private fun checkEquipmentsResponse(response : EquipmentListApi.EquipmentListResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableEquipment.value = data!!
            equipmentList.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
        fetchAds()
    }

    fun fetchAds() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userAds()

                checkAdsResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HomeAdsApi.HomeAdsResponse = fromJson(error.message.toString())

                    checkAdsResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                listener?.onFailure()
            }
        }

    }

    private fun checkAdsResponse(response : HomeAdsApi.HomeAdsResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            if (data.isNotEmpty()) {
                setBanner(data)
            }
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())

    }

    private fun setBanner(banner: MutableList<HomeAdsApi.HomeAds>) {
        if (banner.isEmpty()) {
            return
        }
        val slideList = mutableListOf<SlideModel>()
        for (list in banner) {
            if (list?.ads_page.equals("my nutrition plan",ignoreCase = true))
                slideList.add(SlideModel(list.ads_pic))
        }
        bannerList = slideList
        mutableBannerList.value = slideList
    }
}