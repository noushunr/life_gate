package com.lifegate.app.ui.fragments.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.PostPurchaseApi
import com.lifegate.app.data.network.responses.PurchaseApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.ui.fragments.myplan.detail.PlanDetailListener
import com.lifegate.app.utils.*
import timber.log.Timber

class CheckoutViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var planListener: PlanDetailListener? = null
    var errorMessage: String = ""

    var planId: String = ""
    var planName: String? = ""
    var planStartDate: String? = ""
    var planEndDate: String? = ""
    var planPrice: String? = ""
    var planServicePrice: String? = ""
    var planDiscount: String? = ""
    var planTotalPrice: String? = ""

    var paySecret: String = ""
    var publishKey: String = ""
    var purchaseId: String = ""
    var payIntent: String = ""

    var voucherCode: String? = null

    var isPay = false

    private val mutablePurchaseData = MutableLiveData<PurchaseApi.PurchaseData>()

    val livePurchase : LiveData<PurchaseApi.PurchaseData>
        get() = mutablePurchaseData

    fun fetchPurchaseDetail() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        isPay = false

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userPlanPurchase(planId, voucherCode)

                checkPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : PurchaseApi.PurchaseResponse = fromJson(error.message.toString())

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

    private fun checkPlanResponse(response : PurchaseApi.PurchaseResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            planPrice = "$" + data.purchase_plan_price
            planServicePrice = "$" + data.purchase_extra_price
            planTotalPrice = "$" + data.purchase_total_price
            planDiscount = data.purchase_promocode
            planStartDate = setDayMonthDate(data.purchase_start_date.toString())
            planEndDate = setDayMonthDate(data.purchase_exp_date.toString())

            paySecret = data.paymentIntents?.client_secret.toString()
            publishKey = data.publishableKey.toString()
            purchaseId = data.curr_purchase_id.toString()
            payIntent = data.paymentIntents?.id.toString()

            mutablePurchaseData.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    fun postPurchasePayment() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        isPay = true

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userPurchasePostPayment(
                    PostPurchaseApi.PostPurchaseData(purchaseId, PostPurchaseApi.PostPurchaseIntent(payIntent, paySecret))
                )

                checkPurchasePaymentResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : PostPurchaseApi.PostPurchaseResponse = fromJson(error.message.toString())

                    checkPurchasePaymentResponse(response)
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

    private fun checkPurchasePaymentResponse(response : PostPurchaseApi.PostPurchaseResponse) {

        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (status != null && status) {

            listener?.onSuccess()
        } else {
            listener?.onFailure()
        }
    }

}