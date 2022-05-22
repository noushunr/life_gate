package com.lifegate.app.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.*
import com.lifegate.app.data.repositories.HomeRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var homeListener: HomeListener? = null
    var errorMessage: String = ""
    var isFirst = true

    fun getLoginStatus() = repository.getLoginStatus()

    fun getUserName() = repository.getUserName()

    fun getUserPic() = repository.getUserPic()

    fun getUserCityName() = repository.getUserCityName()

    fun getAdsCoachList() = repository.getAdsCoachList()

    fun getAdsHome() = repository.getAdsHome()

    fun getAdsCalories() = repository.getAdsCalories()

    var tutorialImg = ""
    var tutorialVideoUrl = ""
    var tutorialTxt = ""
    var homeSlideList = mutableListOf<SlideModel>()
    var homeFeaturedCoachList = mutableListOf<HomeFeaturedCoachesApi.HomeFeaturedCoaches>()
    var homeFeaturedPlanList = mutableListOf<HomeFeaturedPlansApi.HomeFeaturedPlans>()
    var homeBannerList = mutableListOf<SlideModel>()

    private val mutableSlideList = MutableLiveData<MutableList<SlideModel>>()
    private val mutableCoachList = MutableLiveData<MutableList<HomeFeaturedCoachesApi.HomeFeaturedCoaches>>()
    private val mutablePlanList = MutableLiveData<MutableList<HomeFeaturedPlansApi.HomeFeaturedPlans>>()
    private val mutableBannerList = MutableLiveData<MutableList<SlideModel>>()
    private val mutableTutorial = MutableLiveData<String>()
    private val mutableBurn = MutableLiveData<BurnCaloriesTodayApi.BurnCaloriesData>()
    private val mutableConsumed = MutableLiveData<ConsumedCaloriesTodayApi.ConsumedCaloriesData>()

    val liveSlideList : LiveData<MutableList<SlideModel>>
        get() = mutableSlideList
    val liveCoachList : LiveData<MutableList<HomeFeaturedCoachesApi.HomeFeaturedCoaches>>
        get() = mutableCoachList
    val livePlanList : LiveData<MutableList<HomeFeaturedPlansApi.HomeFeaturedPlans>>
        get() = mutablePlanList
    val liveBannerList : LiveData<MutableList<SlideModel>>
        get() = mutableBannerList
    val liveTutorial : LiveData<String>
        get() = mutableTutorial
    val liveBurn : LiveData<BurnCaloriesTodayApi.BurnCaloriesData>
        get() = mutableBurn
    val liveConsumed : LiveData<ConsumedCaloriesTodayApi.ConsumedCaloriesData>
        get() = mutableConsumed

    var myPlanList = mutableListOf<MyPlanApi.MyPlanData>()

    private val mutableMyPlan = MutableLiveData<MutableList<MyPlanApi.MyPlanData>>()

    val liveMyPlan : LiveData<MutableList<MyPlanApi.MyPlanData>>
        get() = mutableMyPlan

    /*init {
        fetchTutorialSlider()
    }*/

    fun fetchTutorialSlider() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userHomeTutorialSliders()

                checkTutorialSliderResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HomeTutorialSlidersApi.HomeTutorialSlidersResponse = fromJson(error.message.toString())

                    checkTutorialSliderResponse(response)
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

    private fun checkTutorialSliderResponse(response : HomeTutorialSlidersApi.HomeTutorialSlidersResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            val tutorialVideos = data.tutorialvideos
            if (tutorialVideos != null && tutorialVideos.size > 0) {
                tutorialImg = tutorialVideos[0].tutorial_video_thumb.toString()
                tutorialTxt = tutorialVideos[0].tutorialvideo_title.toString()
                tutorialVideoUrl = tutorialVideos[0].tutorialvideo_video.toString()
                mutableTutorial.value = tutorialImg
                homeListener?.onTutorialVideo()
            }
            val sliders = data.sliders
            if (sliders != null && sliders.size > 0) {
                setSliders(sliders)
            }
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
        fetchFeaturedCoach()
    }

    private fun setSliders(banner: MutableList<HomeTutorialSlidersApi.HomeSliders>) {
        if (banner.isEmpty()) {
            return
        }
        val slideList = mutableListOf<SlideModel>()
        for (list in banner) {
            slideList.add(SlideModel(list.homeslider_pic, list.homeslider_title))
        }
        homeSlideList = slideList
        mutableSlideList.value = slideList
        homeListener?.onSliders()
    }

    fun fetchFeaturedCoach() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userHomeFeaturedCoaches()

                checkFeaturedCoachResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HomeFeaturedCoachesApi.HomeFeaturedCoachesResponse = fromJson(error.message.toString())

                    checkFeaturedCoachResponse(response)
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

    private fun checkFeaturedCoachResponse(response : HomeFeaturedCoachesApi.HomeFeaturedCoachesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            if (data.isNotEmpty()) {
                homeFeaturedCoachList = data
            } else {
                homeFeaturedCoachList = mutableListOf()
            }
            mutableCoachList.value = homeFeaturedCoachList
            homeListener?.onFeaturedCoach()
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
        fetchFeaturedPlan()
//        fetchHomeBanner()
    }

    fun fetchHomeBanner() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userHomeBanners()

                checkHomeBannerResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HomeBannersApi.HomeBannersResponse = fromJson(error.message.toString())

                    checkHomeBannerResponse(response)
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

    private fun checkHomeBannerResponse(response : HomeBannersApi.HomeBannersResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            if (data.isNotEmpty()) {
//                setBanner(data)
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
            if (list?.ads_page.equals("home",ignoreCase = true))
            slideList.add(SlideModel(list.ads_pic))
        }
        homeBannerList = slideList
        mutableBannerList.value = slideList
        homeListener?.onBanner()
    }

    fun fetchFeaturedPlan() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userHomeFeaturedPlans()

                checkFeaturedPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HomeFeaturedPlansApi.HomeFeaturedPlansResponse = fromJson(error.message.toString())

                    checkFeaturedPlanResponse(response)
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

    private fun checkFeaturedPlanResponse(response : HomeFeaturedPlansApi.HomeFeaturedPlansResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            if (data.isNotEmpty()) {
                homeFeaturedPlanList = mutableListOf()
                homeFeaturedPlanList = data
            } else {
                homeFeaturedPlanList = mutableListOf()
            }
            mutablePlanList.value = homeFeaturedPlanList
            homeListener?.onFeaturedPlan()
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())

        if (getLoginStatus()) {
            fetchMyPlan()
        } else {
            fetchAds()
        }
    }

    fun fetchMyPlan() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyTodayPlan()

                checkPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyPlanApi.MyPlanResponse = fromJson(error.message.toString())

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

    private fun checkPlanResponse(response : MyPlanApi.MyPlanResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            myPlanList = mutableListOf()
            myPlanList = data

            mutableMyPlan.value = data!!
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
                repository.updatePrefAds(data)
               setBanner(data)
            }
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
        if (getLoginStatus()) {
            fetchConsumed()
        }
    }

    fun fetchConsumed() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyConsumedCaloriesToday()

                checkConsumedResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : ConsumedCaloriesTodayApi.ConsumedCaloriesResponse = fromJson(error.message.toString())

                    checkConsumedResponse(response)
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

    private fun checkConsumedResponse(response : ConsumedCaloriesTodayApi.ConsumedCaloriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableConsumed.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        //println(response.toString())

        fetchBurned()
    }

    private fun fetchBurned() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyBurnCaloriesToday()

                checkBurnedResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : BurnCaloriesTodayApi.BurnCaloriesResponse = fromJson(error.message.toString())

                    checkBurnedResponse(response)
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

    private fun checkBurnedResponse(response : BurnCaloriesTodayApi.BurnCaloriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableBurn.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        //println(response.toString())

    }
}