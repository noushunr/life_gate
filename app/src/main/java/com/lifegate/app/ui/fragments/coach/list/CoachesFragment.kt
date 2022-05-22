package com.lifegate.app.ui.fragments.coach.list

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.lifegate.app.R
import com.lifegate.app.data.model.SlideModel
import com.lifegate.app.data.network.responses.CountriesApi
import com.lifegate.app.databinding.CoachesFragmentBinding
import com.lifegate.app.ui.fragments.home.HomeFragmentDirections
import com.lifegate.app.utils.*
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class CoachesFragment : Fragment(), KodeinAware, NetworkListener, CoachesListener,SliderAdapter.SliderClickListener  {

    companion object {
        fun newInstance() = CoachesFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: CoachesViewModel
    private lateinit var navController: NavController
    private lateinit var binding: CoachesFragmentBinding
    private val factory: CoachesViewModelFactory by instance()

    private lateinit var coachTypeAdapter: CoachesTypeAdapter
    private lateinit var coachesAdapter: CoachesAdapter
    private lateinit var coachCountryAdapter: CoachesCountryAdapter

    private lateinit var coachTypeMenu: PopupMenu
    private lateinit var countryMenu: PopupMenu
    private lateinit var serviceMenu: PopupMenu
    private lateinit var cityMenu: PopupMenu
    private lateinit var coachServicesMenu: PopupMenu

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(CoachesViewModel::class.java)
        binding = CoachesFragmentBinding.inflate(inflater, container, false)
        //binding.viewmodel = viewModel

        viewModel.listener = this
        //viewModel.coachListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        coachTypeAdapter = CoachesTypeAdapter {
            println(it.coach_type_id)
            viewModel.type = it.coach_type_id
            viewModel.action = "sort"
            viewModel.fetchCoachList()
        }

        coachesAdapter = CoachesAdapter {
            println(it.coach_id)
            goToNextFrag(it.coach_id.toString())
        }
        coachCountryAdapter = CoachesCountryAdapter {
            viewModel.country = it?.coachCountry
            viewModel.action = "sort"
            viewModel.fetchCoachList()
            binding.countryNameTxt.text = it?.name
            binding?.rlTransparent?.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.coachesFragTypeRecyclerview.adapter = coachTypeAdapter
        binding.coachesFragListRecyclerview.adapter = coachesAdapter
        binding.bottomSheet.countryRecyclerview.adapter = coachCountryAdapter
        binding.bottomSheet.countryRecyclerview!!.addItemDecoration(
            DividerItemDecoration(
                binding.bottomSheet.countryRecyclerview!!.context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.coachesFragSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetCountryLayout)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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
        initView()

        if (viewModel.isFirstTime) {
            viewModel.fetchCoachType()
            viewModel.isFirstTime = !viewModel.isFirstTime
        }
    }

    private fun initView() {
        coachTypeMenu = PopupMenu(requireContext(), binding.filter.coachType)
        countryMenu = PopupMenu(requireContext(), binding.filter.coachCountry)
        cityMenu = PopupMenu(requireContext(), binding.filter.coachState)
//        serviceMenu = PopupMenu(requireContext(), binding.filter.coachType)
        coachServicesMenu = PopupMenu(requireContext(), binding.filter.coachServices)

        val coachTypeListMap = mutableMapOf<String, String>()
        viewModel.liveCoachType.observe(viewLifecycleOwner, Observer { itemList ->
            coachTypeAdapter.submitList(viewModel.coachTypeList)
            coachTypeMenu.menu.clear()

            val modelList = arrayListOf<CharSequence>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.type.toString())
                    coachTypeListMap[data.type.toString()] = data.coach_type_id.toString()
                    coachTypeMenu.menu.add(data.type.toString())
                }
            }
        })
        coachTypeMenu.setOnMenuItemClickListener { item ->
            coachTypeListMap[item?.title]?.let { modelId ->
                viewModel.type = modelId
            }

            binding.filter?.tvCoachType.text = item?.title
            Timber.e(item?.title.toString())
            true
        }

        val modelListMap = mutableMapOf<String, String>()
        viewModel.liveCoachCountry.observe(viewLifecycleOwner, Observer { itemList ->
            countryMenu.menu.clear()
            val modelList = arrayListOf<String>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.name.toString())
                    modelListMap[data.name.toString()] = data.coachCountry.toString()
                    countryMenu.menu.add(data.name.toString())
                }
            }
            var countriesData = CountriesApi.CountriesData(null,null,"World Wide")
            itemList?.add(0,countriesData)
            coachCountryAdapter.submitList(itemList)

        })
//        viewModel.liveCountry.observe(viewLifecycleOwner, Observer { itemList ->
//            countryMenu.menu.clear()
//            val modelList = arrayListOf<String>()
//            itemList?.let {
//                itemList.forEach { data ->
//                    modelList.add(data.name.toString())
//                    modelListMap[data.name.toString()] = data.id.toString()
//                    countryMenu.menu.add(data.name.toString())
//                }
//            }
//        })
        countryMenu.setOnMenuItemClickListener { item ->
            modelListMap[item?.title]?.let { modelId ->
                viewModel.country = modelId
                cityMenu.menu.clear()
            }
            viewModel?.fetchCity(viewModel.country!!)
//            viewModel.action = "sort"
//            viewModel.fetchCoachList()
//            binding.filter.filterOverlay.visibility = View.GONE
            binding.filter?.tvCountry.text = item?.title
            binding.filter?.tvState.text = getString(R.string.select_state)
            true
        }

        val modelListCityMap = mutableMapOf<String, String>()
        viewModel.liveCity.observe(viewLifecycleOwner, Observer { itemList ->
            cityMenu.menu.clear()
            val modelList = arrayListOf<String>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.name.toString())
                    modelListCityMap[data.name.toString()] = data.id.toString()
                    cityMenu.menu.add(data.name.toString())
                }
            }
        })
        cityMenu.setOnMenuItemClickListener { item ->
            modelListCityMap[item?.title]?.let { modelId ->
                viewModel.city = modelId
            }

            binding.filter?.tvState.text = item?.title
            Timber.e(item?.title.toString())
            true
        }

        val modelListServicesMap = mutableMapOf<String, String>()
        viewModel.liveServices.observe(viewLifecycleOwner, Observer { itemList ->
            coachServicesMenu.menu.clear()
            val modelList = arrayListOf<String>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.service.toString())
                    modelListServicesMap[data.service.toString()] = data.serviceId.toString()
                    coachServicesMenu.menu.add(data.service.toString())
                }
            }
        })
        coachServicesMenu.setOnMenuItemClickListener { item ->
            modelListServicesMap[item?.title]?.let { modelId ->
                viewModel.service = modelId
            }

            binding.filter?.tvServices.text = item?.title
            Timber.e(item?.title.toString())
            true
        }

        viewModel.liveCoachList.observe(viewLifecycleOwner, Observer { item ->
            coachesAdapter.submitList(viewModel.coachesList)
            if (item != null && item.size > 0) {
                hideProgress()
            }
        })
        viewModel.liveBannerList.observe(viewLifecycleOwner, Observer { item ->
            val adapter = SliderAdapter(viewModel.coachBannerList, requireContext())
            binding.coachBannerSliderView.also {
                it.stopAutoCycle()
                it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                it.setIndicatorAnimation(IndicatorAnimationType.WORM)
                it.indicatorSelectedColor = Color.WHITE
                it.indicatorUnselectedColor = Color.GRAY
                it.scrollTimeInSec = 3
                it.setSliderAdapter(adapter)
                it.isAutoCycle = true
                it.startAutoCycle()
                it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            }
        })

        binding.coachFragFilterCard.setOnClickListener {
            binding.filter.filterOverlay.visibility = View.VISIBLE
        }

        binding.filter.closeImg.setOnClickListener {
            binding.filter.filterOverlay.visibility = View.GONE
        }

        binding.filter.coachType.setOnClickListener {
            coachTypeMenu.show()
        }

        binding.filter.coachCountry.setOnClickListener {
            countryMenu.show()
        }
        binding.filter.coachState.setOnClickListener {
            cityMenu.show()
        }
        binding?.filter?.coachServices?.setOnClickListener {
            coachServicesMenu.show()
        }
        binding?.filter?.search?.setOnClickListener {
            viewModel.action = "filter"
            binding.filter.filterOverlay.visibility = View.GONE
            viewModel.fetchCoachList()
        }

        binding.filter.clearTxt.setOnClickListener {
            viewModel.search= null
            viewModel.type = null
            viewModel.city = null
            viewModel.country = null
            viewModel.action = null
            viewModel.service = null
            viewModel.page = null
            viewModel.per_page = null
            cityMenu?.menu.clear()
            binding.filter?.tvCoachType.text = getString(R.string.select_coach_type)
            binding.filter?.tvCountry.text = getString(R.string.select_country)
            binding.filter?.tvState.text = getString(R.string.select_state)
            binding.filter?.tvServices.text = getString(R.string.select_services)
            binding.countryNameTxt.text = "World Wide"
            binding.coachesFragSearch.text = null
            viewModel.fetchCoachList()
            binding.filter.filterOverlay.visibility = View.GONE
        }


        binding.countryCard.setOnClickListener {
            //binding.filter.filterOverlay.visibility = View.VISIBLE
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                binding?.rlTransparent?.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                binding?.rlTransparent?.visibility = View.GONE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
//            countryMenu.show()
        }
        binding?.rlTransparent?.setOnClickListener {
            binding?.rlTransparent?.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun goToNextFrag(coachId : String) {
        val action = CoachesFragmentDirections.actionCoachesFragmentToCoachDetailFragment(coachId)
        navController.safeNavigate(action)
    }

    private fun performSearch() {
        val search = binding.coachesFragSearch.text.toString()
        if (search.isEmpty() || search.isEmpty() || search.equals("null")) {
            view?.context?.toast("Enter a coach name to search")
        } else {
            viewModel.search = search
            viewModel.fetchCoachList()
        }
    }

    override fun onStarted() {
        hideKeyboard()
        showProgress()
    }

    override fun onSuccess() {
        hideProgress()
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

    override fun onCoachesType() {
        coachTypeAdapter.submitList(viewModel.coachTypeList)
    }

    override fun onCoachesList() {
        coachesAdapter.submitList(viewModel.coachesList)
    }

    override fun onSliderClick(item: SlideModel) {
        goToSliderActivity()
    }

    private fun goToSliderActivity() {
        val action = CoachesFragmentDirections.actionCoachesFragmentToSliderActivity(
            Gson().toJson(viewModel.coachBannerList)
        )
        navController.safeNavigate(action)
    }
}