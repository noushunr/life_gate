package com.lifegate.app.ui.fragments.myplan.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.CoachRatingApi
import com.lifegate.app.data.network.responses.PlanDetailsApi
import com.lifegate.app.data.network.responses.PlanRatingApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber
import java.math.RoundingMode
import java.text.DecimalFormat

class PlanDetailViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var planListener: PlanDetailListener? = null
    var errorMessage: String = ""

    var planId = ""
    var comment = ""
    var rating = ""
    var coachId = ""

    var planSlideList = mutableListOf<SlideModel>()
    var planName : String? = null
    var planType : String? = null
    var planDesc : String? = null
    var planRule : String? = null
    var planDuration : String? = null
    var planPrice : String? = null
    var planTotalPrice : String? = null
    var planExtraCost : String? = null
    var planRating : Float = 0f
    var planRules : Array<String> = arrayOf()
    var planReviewsList = mutableListOf<PlanRatingApi.PlanRating>()

    var planPic : String? = null

    private val mutablePlanData = MutableLiveData<PlanDetailsApi.PlanDetails>()

    val livePlanData : LiveData<PlanDetailsApi.PlanDetails>
        get() = mutablePlanData

    fun fetchPlanDetail() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userPlanDetail(planId)

                checkPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : PlanDetailsApi.PlanDetailsResponse = fromJson(error.message.toString())

                    checkPlanResponse(response)
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

    private fun checkPlanResponse(response : PlanDetailsApi.PlanDetailsResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            val banner = data.plan_image
            if (banner != null) {
                setSliders(banner)
            }

            planName = data.plan_name
            planType = data.plantype_name
            planDesc = data.plan_desc
            planRule = data.plan_rule
            planRules = planRule?.split("&nbsp;<br>")?.toTypedArray()!!

            planDuration = data.plan_duration
            planPrice = "$" + data.plan_cost
            planTotalPrice = "$" + data.plan_total
            planExtraCost = "$${String.format("%.2f",(data.plan_total?.toDouble()?.minus(data.plan_cost?.toDouble()!!)!!))}"
            coachId = data?.plan_coach!!

            val rate = data.plan_avg_rating
            planRating = rate?.toFloat() ?: 0f

            mutablePlanData.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())

        fetchPlanRating()
    }

    private fun setSliders(banner: MutableList<PlanDetailsApi.PlanImage>) {
        if (banner.isEmpty()) {
            return
        }
        val slideList = mutableListOf<SlideModel>()
        var flag = true
        for (list in banner) {
            slideList.add(SlideModel(list.attachment))
            if (flag && list.type.equals(KEY_ATTACHMENT_TYPE_IMG)) {
                flag = false
                planPic = list.attachment
            }
        }

        planSlideList = slideList
    }

    fun fetchPlanRating() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userPlanRatingList(planId)

                checkPlanRating(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : PlanRatingApi.PlanRatingListResponse = fromJson(error.message.toString())

                    checkPlanRating(response)
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

    private fun checkPlanRating(response : PlanRatingApi.PlanRatingListResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        planReviewsList = mutableListOf()

        if (data != null  && status != null && status) {

            planReviewsList = data

            listener?.onSuccess()
            planListener?.onReviewList()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    fun addPlanRating() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userPlanRatingAdd(planId, comment, rating)

                checkAddPlanRating(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : PlanRatingApi.PlanRatingAddResponse = fromJson(error.message.toString())

                    checkAddPlanRating(response)
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

    private fun checkAddPlanRating(response : PlanRatingApi.PlanRatingAddResponse) {

        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        listener?.onFailure()

        println(response.toString())
    }

    fun getLoginStatus() = repository.getLoginStatus()

    fun addCoachMessage(
        message: String
    ) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCoachMessageAdd(coachId, message)

                checkAddCoachMessage(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachRatingApi.CoachRatingAddResponse = fromJson(error.message.toString())

                    checkAddCoachMessage(response)
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

    private fun checkAddCoachMessage(response : CoachRatingApi.CoachRatingAddResponse) {

        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        listener?.onFailure()

        println(response.toString())
    }
    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }
}