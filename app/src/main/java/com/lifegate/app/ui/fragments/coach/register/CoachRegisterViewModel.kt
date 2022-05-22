package com.lifegate.app.ui.fragments.coach.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.CoachesTypeApi
import com.lifegate.app.data.network.responses.CountriesApi
import com.lifegate.app.data.network.responses.SignUpApi
import com.lifegate.app.data.repositories.CoachRepository
import com.lifegate.app.ui.fragments.coach.list.CoachesListener
import com.lifegate.app.utils.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

class CoachRegisterViewModel(private val repository: CoachRepository) : ViewModel() {
    // TODO: Implement the ViewModel
    var listener: NetworkListener? = null
    var coachListener: CoachesListener? = null
    var errorMessage: String = ""
    val appContext by lazy {
        repository.appContext
    }

    var coachTypeList : MutableList<CoachesTypeApi.CoachesType> =  mutableListOf()

    var coachName: String? = null
    var email: String? = null
    var phone: String? = null
    var civilid: String? = null
    var type :  String? = null
    var country :  String? = null
    var isCoachRegistration = false
    var files :MutableList<MultipartBody.Part> = mutableListOf()
    var filePaths : MutableList<String> = mutableListOf()


    private val mutableCountry = MutableLiveData<MutableList<CountriesApi.CountriesData>>()
    private val mutableCoachType = MutableLiveData<MutableList<CoachesTypeApi.CoachesType>>()
    val liveCountry : LiveData<MutableList<CountriesApi.CountriesData>>
        get() = mutableCountry
    val liveCoachType : LiveData<MutableList<CoachesTypeApi.CoachesType>>
        get() = mutableCoachType

    fun fetchCoachType() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

//        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCoachesType()

                checkCoachTypeResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachesTypeApi.CoachesTypeResponse = fromJson(error.message.toString())

                    checkCoachTypeResponse(response)
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

    private fun checkCoachTypeResponse(response: CoachesTypeApi.CoachesTypeResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        coachTypeList = mutableListOf()

        if (data != null  && status != null && status) {
            coachTypeList = data
//            listener?.onSuccess()
//            coachListener?.onCoachesType()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        mutableCoachType.value = coachTypeList

        println(response.toString())

    }

    fun fetchCountry() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userAllCountries()

                checkCountryResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CountriesApi.CountriesResponse = fromJson(error.message.toString())

                    checkCountryResponse(response)
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

    private fun checkCountryResponse(response: CountriesApi.CountriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableCountry.value = data!!
//            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

//        println(response.toString())
    }


    fun register(){
        val mName = coachName
        val mEmail = email
        val mPhone = phone
        val mCountry = country
        val mType = type
        val mCivilid = civilid
        if (mEmail.isNullOrEmpty() || mName.isNullOrEmpty() ||
            mPhone.isNullOrEmpty() || mCountry.isNullOrEmpty() ||
            mType.isNullOrEmpty() || mCivilid.isNullOrEmpty()) {
            errorMessage = appContext.getLocaleStringResource(R.string.all_field_mandate)
            listener?.onFailure()
            return
        }

        if (!mEmail.isValidEmail()) {
            errorMessage = appContext.getLocaleStringResource(R.string.enter_valid_email)
            listener?.onFailure()
            return
        }

        if (mPhone.length < 8) {
            errorMessage = appContext.getLocaleStringResource(R.string.enter_valid_phone)
            listener?.onFailure()
            return
        }


        if (mCountry?.equals("null")) {
            errorMessage = appContext.getLocaleStringResource(R.string.city_mandatory)
            listener?.onFailure()
            return
        }

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }
        listener?.onStarted()

        CoRoutines.main {
            try {
                isCoachRegistration = true
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)

                builder.addFormDataPart("coachname", mName)
                builder.addFormDataPart("country", mCountry)
                builder.addFormDataPart("email", mEmail)
                builder.addFormDataPart("phone", mPhone)
                builder.addFormDataPart("civilid", mCivilid)
                builder.addFormDataPart("coachtype", mType)
                if (filePaths.isNotEmpty()) {
                    for (i in filePaths) {
                        val file = File(i)
                        val requestBody: RequestBody =
                            RequestBody.create(MediaType.parse("image/jpeg"), file)
                        builder.addFormDataPart("attachment[]", file.name, requestBody)
                    }
                }
                val requestBody = builder.build()
                val response = repository.registerCoach(
                    requestBody
                )

                checkSignUpResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : SignUpApi.SignUpResponse = fromJson(error.message.toString())

                    checkSignUpResponse(response)
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

    private fun checkSignUpResponse(response: SignUpApi.SignUpResponse) {

        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (status!!) {
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

//        println(response.toString())
    }
}