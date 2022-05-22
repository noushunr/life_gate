package com.lifegate.app.ui.fragments.pricy_policy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.CoachesTypeApi
import com.lifegate.app.data.network.responses.PolicyApi
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.ui.fragments.coach.list.CoachesListener
import com.lifegate.app.utils.*
import timber.log.Timber

class PolicyViewModel(private val repository: UserRepository) : ViewModel() {
    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    private var mutablePolicyData = MutableLiveData<PolicyApi.PolicyData>()
    val livePolicyData : LiveData<PolicyApi.PolicyData>
        get() = mutablePolicyData
    var isFirstTime = true
    fun fetchPolicyData() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.policyData()

                checkPolicyDataResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : PolicyApi.PolicyResponse = fromJson(error.message.toString())

                    checkPolicyDataResponse(response)
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

    private fun checkPolicyDataResponse(response :PolicyApi.PolicyResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()


        if (data != null  && status != null && status) {
            listener?.onSuccess()

        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        mutablePolicyData?.value = data!!

    }
}