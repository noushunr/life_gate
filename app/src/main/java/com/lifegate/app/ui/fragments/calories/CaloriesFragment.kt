package com.lifegate.app.ui.fragments.calories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.lifegate.app.data.network.responses.FoodMealListApi
import com.lifegate.app.databinding.CaloriesFragmentBinding
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class CaloriesFragment : Fragment(), KodeinAware, NetworkListener,
    FoodAdapter.FoodClickListener {

    companion object {
        fun newInstance() = CaloriesFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: CaloriesViewModel
    private lateinit var navController: NavController
    private lateinit var binding: CaloriesFragmentBinding
    private val factory: CaloriesViewModelFactory by instance()
    private var burned = 0
    private var consumed = 0
    private var burnedTarget = 0
    private var consumedTarget = 0

    private var userConsumed = 0

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var adapter: FoodAdapter
    private var dialogNew: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(CaloriesViewModel::class.java)
        binding = CaloriesFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        androidx.core.view.ViewCompat.setNestedScrollingEnabled(
            binding.bottomSheet.foodMenuRecyclerview,
            true
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomSheet.bottomSheetFoodMenuLayout)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
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
        binding.overlay.close.setOnClickListener {
            //binding.overlay.goalsOverlay.visibility = View.GONE
            showGoalsUpdateDialog()
        }

        binding.history.setOnClickListener {
            goToAllHistory()
        }

        adapter = FoodAdapter(this)

        binding.bottomSheet.foodMenuRecyclerview.adapter = adapter

        if (viewModel.isFirst && viewModel.getLoginStatus()) {
            viewModel.isFirst = false
            viewModel.fetchConsumed()
        }

        initView()
    }

    private fun initView() {

        viewModel.liveBurn.observe(viewLifecycleOwner, Observer { item ->
            binding.caloriesHeader.burningGoal.text = item.burning_goal + " Cal"
            binding.caloriesBurnedToday.text = "I Burned ${item.actual_burned} Calories"

            binding.overlay.burningGoal.setText(item.burning_goal) //item.burning_goal + " Cal"

            viewModel.burnGoal = item.burning_goal.toString()

            try {
                burned = item.actual_burned!!.toInt()
                burnedTarget = item.burning_goal!!.toInt()
                setCalToday()
            } catch (e: Exception) {

            }
        })

        viewModel.liveConsumed.observe(viewLifecycleOwner, Observer { item ->
            binding.caloriesHeader.consumeGoal.text = item.consume_goal + " Cal"
            if (item.proteins_today != null && item.proteins_today!!.isNotEmpty()) {
                var proteins = item.proteins_today?.toDouble()
                val proteinsIn = String.format("%.2f", proteins)
                binding.caloriesHeader.proteins.text = proteinsIn + " g"
            }
            if (item.carbs_today != null && item.carbs_today!!.isNotEmpty()) {
                var carbs = item.carbs_today?.toDouble()
                val carbsIn = String.format("%.2f", carbs)
                binding.caloriesHeader.carbs.text = carbsIn + " g"
            }
            if (item.fat_today != null && item.fat_today!!.isNotEmpty()) {
                var fat = item.fat_today?.toDouble()
                val fatIn = String.format("%.2f", fat)
                binding.caloriesHeader.fats.text = fatIn + " g"
            }

            binding.caloriesEatToday.text = "I Consumed ${item.consumed_today} Calories"

            binding.overlay.consumeGoal.setText(item.consume_goal) //= item.consume_goal + " Cal"
            binding.overlay.proteins.setText(item.proteins_goal)
            binding.overlay.carbs.setText(item.carbs_goal)
            binding.overlay.fats.setText(item.fat_goal)

            viewModel.consumeGoal = item.consume_goal.toString()
            viewModel.proteinGoal = item.proteins_goal.toString()
            viewModel.carbGoal = item.carbs_goal.toString()
            viewModel.fatGoal = item.fat_goal.toString()

            try {
                consumed = item.consumed_today!!.toInt()
                consumedTarget = item.consume_goal!!.toInt()
                setCalToday()
            } catch (e: Exception) {

            }
            if (item != null) {
                binding.goal.setOnClickListener {
                    binding.overlay.goalsOverlay.visibility = View.VISIBLE
                }
            }

        })

        binding.caloriesHeader.consumedBtn.setOnClickListener {
            //binding.consmed.overlay.visibility = View.VISIBLE
            showBottomSheet()
        }
        binding.caloriesHeader.burnedBtn.setOnClickListener {
            binding.burned.overlay.visibility = View.VISIBLE
        }
        binding.consmed.close.setOnClickListener {
            binding.consmed.overlay.visibility = View.GONE
        }
        binding.consmed.cancel.setOnClickListener {
            binding.consmed.overlay.visibility = View.GONE
        }
        binding.burned.close.setOnClickListener {
            binding.burned.overlay.visibility = View.GONE
        }

        binding.consmed.submitsBtn.setOnClickListener {
            if (viewModel.getLoginStatus()) {
                val caloriesTxt = binding.consmed.caloriesTxt.text
                val carbsTxt = binding.consmed.carbsTxt.text
                val proteinsTxt = binding.consmed.proteinsTxt.text
                val fatsTxt = binding.consmed.fatsTxt.text
                viewModel.addBurnedConsumed(
                    null,
                    caloriesTxt.toString(),
                    proteinsTxt.toString(),
                    carbsTxt.toString(),
                    fatsTxt.toString()
                )
            } else {
                view?.context?.toast("Please login to continue")
            }
            binding.consmed.overlay.visibility = View.GONE
        }

        binding.burned.submitBtn.setOnClickListener {
            if (viewModel.getLoginStatus()) {
                val valueTxt = binding.burned.valueTxt.text
                if (valueTxt.isNullOrBlank() || valueTxt.isNullOrEmpty()) {
                    view?.context?.toast("Value cant be blank")
                } else {
                    viewModel.addBurnedConsumed(valueTxt.toString(), null, null, null, null)
                    binding.burned.valueTxt.setText(null)
                }
            } else {
                view?.context?.toast("Please login to continue")
            }
            binding.burned.overlay.visibility = View.GONE
        }

        viewModel.liveFood.observe(viewLifecycleOwner, Observer { item ->
            //Timber.e("${item.size}")
            adapter.submitList(mutableListOf())
            if (!item.isNullOrEmpty())
                adapter.submitList(item)
        })

        binding.bottomSheet.submit.setOnClickListener {
            hideKeyboard()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding?.rlTransparent?.visibility = View.GONE
            //viewModel.addBurnedConsumed(null, "$userConsumed", null, null, null)
            viewModel.checkFoodListUpdate()
        }
        binding?.rlTransparent?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding?.rlTransparent?.visibility = View.GONE
        }

        binding.bottomSheet.other.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding?.rlTransparent?.visibility = View.GONE
            binding.consmed.overlay.visibility = View.VISIBLE
        }
    }

    private fun showGoalsUpdateDialog() {
        hideKeyboard()

        val title = "Goal"
        val message = "Do you want to save changes?"
        dialogNew = AlertDialog.Builder(requireContext())
            .setPositiveButton(resources.getText(android.R.string.ok)) { dialog1, _ ->
                updateGoal()
                dialog1.dismiss()
            }
            .setNegativeButton(resources.getText(android.R.string.cancel)) { dialog1, _ ->
                binding.overlay.goalsOverlay.visibility = View.GONE
                dialog1.dismiss()
            }
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .create()

        dialogNew?.show()

    }

    private fun updateGoal() {
        hideKeyboard()

        val consumeGoal = binding.overlay.consumeGoal.text.toString()
        val burningGoal = binding.overlay.burningGoal.text.toString()
        val proteins = binding.overlay.proteins.text.toString()
        val carbs = binding.overlay.carbs.text.toString()
        val fats = binding.overlay.fats.text.toString()

        viewModel.consumeGoal = consumeGoal
        viewModel.burnGoal = burningGoal
        viewModel.proteinGoal = proteins
        viewModel.carbGoal = carbs
        viewModel.fatGoal = fats

        viewModel.addBurnedConsumed(null, null, null, null, null)

    }

    private fun showBottomSheet() {

        adapter.submitList(viewModel.mealFoodList)

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            binding?.rlTransparent?.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            binding?.rlTransparent?.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setCalToday() {
        val total = consumed - burned
        binding.caloriesTotalTodayTxt.text = "Total : $total Cal"
        try {
            val consumedProgress = (consumed * 100) / consumedTarget
            val burnedProgress = (burned * 100) / burnedTarget
            binding.caloriesHeader.consumedProgress.progress = consumedProgress.toFloat()
            binding.caloriesHeader.burnedProgress.progress = burnedProgress.toFloat()

            binding.overlay.consumedProgress.progress = consumedProgress.toFloat()
            binding.overlay.burnedProgress.progress = burnedProgress.toFloat()
        } catch (e: Exception) {

        }
    }

    private fun goToAllHistory() {
        val action = CaloriesFragmentDirections.actionCaloriesFragmentToHistoryFragment()
        navController.safeNavigate(action)
    }

    override fun onResume() {
        super.onResume()
        loadImage(binding.adsImg, viewModel.getAdsCalories())
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
        //view?.context?.toast(viewModel.errorMessage)
        loadImage(binding.adsImg, viewModel.getAdsCalories())
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

    override fun onSelectFoodClick(item: FoodMealListApi.FoodMealListData) {
        //Timber.e("select")
        viewModel.updateMealList(item)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        try {
            userConsumed += item.groceries_calory!!.toInt()
        } catch (e: Exception) {

        }

        Timber.e("$userConsumed")
    }

    override fun onUnSelectFoodClick(item: FoodMealListApi.FoodMealListData) {
        //Timber.e("un-select")
        viewModel.updateMealList(item)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        try {
            userConsumed -= item.groceries_calory!!.toInt()
        } catch (e: Exception) {

        }

        Timber.e("$userConsumed")
    }

    override fun onUpdateQty(item: FoodMealListApi.FoodMealListData, qty: String) {
        viewModel.updateMealList(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding?.rlTransparent?.visibility = View.GONE
        dialogNew?.dismiss()
    }

}