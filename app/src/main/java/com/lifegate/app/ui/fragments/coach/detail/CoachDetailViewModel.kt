package com.lifegate.app.ui.fragments.coach.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.CoachDetailsApi
import com.lifegate.app.data.network.responses.CoachRatingApi
import com.lifegate.app.data.repositories.CoachRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class CoachDetailViewModel(
    private val repository: CoachRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var coachListener: CoachDetailListener? = null
    var errorMessage: String = ""

    fun getLoginStatus() = repository.getLoginStatus()

    var coachId = ""
    var comment = ""
    var rating = ""

    var homeSlideList = mutableListOf<SlideModel>()
    var coachName : String? = null
    var coachType : String? = null
    var coachCity : String? = null
    var coachClub : String? = null
    var coachExp : String? = null
    var coachRating : Float = 0f
    var coachServiceList = mutableListOf<CoachDetailsApi.CoachServices>()
    var coachAwardList = mutableListOf<CoachDetailsApi.CoachAward>()
    var coachPlanList = mutableListOf<CoachDetailsApi.CoachPlan>()
    var coachReviewsList = mutableListOf<CoachRatingApi.CoachRating>()

    private val mutableCoachData = MutableLiveData<CoachDetailsApi.CoachDetails>()

    val liveCoachData : LiveData<CoachDetailsApi.CoachDetails>
        get() = mutableCoachData

    fun fetchCoachDetail() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCoachDetail(coachId)

                checkCoachResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachDetailsApi.CoachDetailsResponse = fromJson(error.message.toString())

                    checkCoachResponse(response)
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

    private fun checkCoachResponse(response : CoachDetailsApi.CoachDetailsResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            listener?.onSuccess()
            coachListener?.onCoachDetail()

            val banner = data.coach_attachments
            if (banner != null) {
                setSliders(banner)
            }

            val coachName1 = StringBuilder()
            if (data.coach_fname != null) {
                coachName1.append(data.coach_fname).append(" ")
            }
            if (data.coach_lname != null) {
                coachName1.append(data.coach_lname)
            }
            coachName = coachName1.toString()

            coachType = data.coach_type_name
            coachCity = data.city_name
            coachClub = data.coach_club_name
            coachExp = data.coach_experience + " Yrs"
            val rate = data.coach_rating
            coachRating = rate?.toFloat() ?: 0f

            val serviceList = data.coach_services
            coachServiceList = if (!serviceList.isNullOrEmpty()) {
                serviceList
            } else {
                mutableListOf()
            }

            val awardList = data.coach_awards
            coachAwardList = if (!awardList.isNullOrEmpty()) {
                awardList
            } else {
                mutableListOf()
            }

            val planList = data.coach_plans
            coachPlanList = if (!planList.isNullOrEmpty()) {
                planList
            } else {
                mutableListOf()
            }

            mutableCoachData.value = data!!

        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())

        fetchCoachRating()
    }

    private fun setSliders(banner: MutableList<CoachDetailsApi.CoachAttachment>) {
        if (banner.isEmpty()) {
            return
        }
        val slideList = mutableListOf<SlideModel>()
        for (list in banner) {
            slideList.add(SlideModel(list.attachment))
        }

        homeSlideList = slideList
    }

    fun fetchCoachRating() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCoachRatingList(coachId)

                checkCoachRating(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachRatingApi.CoachRatingListResponse = fromJson(error.message.toString())

                    checkCoachRating(response)
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

    private fun checkCoachRating(response : CoachRatingApi.CoachRatingListResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        coachReviewsList = mutableListOf()

        if (data != null  && status != null && status) {
            coachReviewsList = data
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        coachListener?.onReviewList()

        println(response.toString())
    }

    fun addCoachRating() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCoachRatingAdd(coachId, comment, rating)

                checkAddCoachRating(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachRatingApi.CoachRatingAddResponse = fromJson(error.message.toString())

                    checkAddCoachRating(response)
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

    private fun checkAddCoachRating(response : CoachRatingApi.CoachRatingAddResponse) {

        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        listener?.onFailure()

        println(response.toString())
    }

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

}