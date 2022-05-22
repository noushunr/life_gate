package com.lifegate.app.ui.fragments.checkout

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lifegate.app.MainApplication
import com.lifegate.app.R
import com.lifegate.app.databinding.CheckoutFragmentBinding
import com.lifegate.app.utils.*
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class CheckoutFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = CheckoutFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: CheckoutViewModel
    private lateinit var navController: NavController
    private lateinit var binding: CheckoutFragmentBinding
    private val factory: CheckoutViewModelFactory by instance()

    private lateinit var paymentSheet : PaymentSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(CheckoutViewModel::class.java)
        binding = CheckoutFragmentBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        val safeArgs: CheckoutFragmentArgs by navArgs()
        viewModel.planId = safeArgs.plan
        viewModel.planName = safeArgs.name

        //viewModel.fetchPurchaseDetail()

        binding.checkoutPlanNameTxt.text = viewModel.planName
        loadImage(binding.checkoutPlanImg, safeArgs.pic)
        binding.checkoutPlanDurationTxt.text = safeArgs.duration
        binding.checkoutPlanPriceTxt.text = safeArgs.price
        binding.checkoutServicePriceTxt.text = safeArgs.extra
        binding.checkoutTotalPriceTxt.text = safeArgs.price

        try {
            val price = safeArgs.price.toString().toFloat()
            val extra = safeArgs.extra.toString().toFloat()
            val total = price + extra
            binding.checkoutTotalPriceTxt.text = "$ $total"
        } catch (e: Exception) {

        }

        initView()
    }

    private fun initView() {

        viewModel.livePurchase.observe(viewLifecycleOwner, Observer { item ->

            binding.checkoutPlanNameTxt.text = viewModel.planName
            binding.checkoutPlanStartDateTxt.text = viewModel.planStartDate
            binding.checkoutPlanEndDateTxt.text = viewModel.planEndDate
            binding.checkoutPlanPriceTxt.text = viewModel.planPrice
            binding.checkoutServicePriceTxt.text = viewModel.planServicePrice
            //binding.checkoutCouponCodeTxt.setText(viewModel.planDiscount)
            binding.checkoutTotalPriceTxt.text = viewModel.planTotalPrice
            binding.checkoutPlanDurationTxt.visibility = View.GONE
            binding.checkoutPlanDateLayout.visibility = View.VISIBLE

        })

        /*binding.checkoutPayBtn.setOnClickListener {
            prepareCheckout { customerConfig, clientSecret ->
                paymentSheet.presentWithPaymentIntent(
                    clientSecret,
                    PaymentSheet.Configuration(
                        merchantDisplayName = "ATR",
                        customer = customerConfig,
                        googlePay = null,
                        allowsDelayedPaymentMethods = false
                    )
                )
            }
        }*/

        binding.checkoutPayBtn.setOnClickListener {
            //initPayment()
            checkTos()
        }

    }

    private fun checkTos() {
        if (binding.tosCheck.isChecked) {
            viewModel.fetchPurchaseDetail()
        } else {
            view?.context?.toast(viewModel.appContext.getLocaleStringResource(R.string.accept_all_the_rules_and_regulations))
        }
    }

    private fun initPayment() {

        PaymentConfiguration.init(
            MainApplication.appContext,
            viewModel.publishKey
        )

        val configuration = PaymentSheet.Configuration(viewModel.appContext.getLocaleStringResource(
            R.string.app_name))

        paymentSheet.presentWithPaymentIntent(viewModel.paySecret.toString(), configuration)


    }

    private fun prepareCheckout(
        onSuccess: (PaymentSheet.CustomerConfiguration?, String) -> Unit
    ) {
        /*PaymentConfiguration.init(requireContext(), "pst_test_YWNjdF8xMDMyRDgyZVp2S1lsbzJDLHNZYnFjSzhsbWNOZnZCVTFpWmFMV21ITFhpMlpUOU8_00D47cxdEo")

        onSuccess(
            null,
            "pi_3KC5HR2eZvKYlo2C1JwnP9KD_secret_ukXVeef9EoEXkD2MG51aPBQDU"
        )*/
        PaymentConfiguration.init(requireContext(), "pk_test_51BTUDGJAJfZb9HEBwDg86TN1KNprHjkfipXmEDMb0gSCassK5T3ZfxsAbcgKVmAIXF7oZ6ItlZZbXO6idTHE67IM007EwQ4uN3")

        onSuccess(
            null,
            "pi_3KCHYM2eZvKYlo2C1wW4KhAY_secret_rP8DE2Ojym10Viy1QJjhV5ALd"
        )
    }

    private fun onPaymentSheetResult(
        paymentResult: PaymentSheetResult
    ) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                Timber.e("Payment complete!")
                viewModel.postPurchasePayment()
            }
            is PaymentSheetResult.Canceled -> {
                Timber.e("Payment canceled!")
                view?.context?.toast("Payment canceled!")
            }
            is PaymentSheetResult.Failed -> {
                Timber.e(paymentResult.error)
                view?.context?.toast("Payment failed!")
                //showAlert("Payment failed", paymentResult.error.localizedMessage)
            }

        }
    }

    private fun goToNextFrag() {
        val action = CheckoutFragmentDirections.actionCheckoutFragmentToHomeFragment()
        navController.safeNavigate(action)
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        if (viewModel.isPay) {
            goToNextFrag()
        } else {
            initPayment()
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

}