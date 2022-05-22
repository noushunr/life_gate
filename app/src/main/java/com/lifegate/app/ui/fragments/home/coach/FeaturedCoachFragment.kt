package com.lifegate.app.ui.fragments.home.coach

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.HomeFeaturedCoachesApi
import com.lifegate.app.databinding.FeaturedCoachFragmentBinding
import com.lifegate.app.generated.callback.OnClickListener
import com.lifegate.app.ui.fragments.coach.list.CoachesAdapter
import com.lifegate.app.ui.fragments.coach.list.CoachesListener
import com.lifegate.app.ui.fragments.coach.list.CoachesTypeAdapter
import com.lifegate.app.ui.fragments.coach.list.CoachesViewModelFactory
import com.lifegate.app.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import timber.log.Timber

class FeaturedCoachFragment : Fragment(), KodeinAware, NetworkListener {

    companion object {
        fun newInstance() = FeaturedCoachFragment()
    }

    override val kodein by kodein()

    private lateinit var viewModel: FeaturedCoachViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FeaturedCoachFragmentBinding
    private val factory: FeaturedCoachViewModelFactory by instance()

    private lateinit var coachTypeAdapter: CoachesTypeAdapter
    private lateinit var coachAdapter: FeaturedCoachAdapter
    private lateinit var coachesAdapter: CoachesAdapter

    private lateinit var coachTypeMenu: PopupMenu
    private lateinit var countryMenu: PopupMenu
    private lateinit var serviceMenu: PopupMenu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(FeaturedCoachViewModel::class.java)
        binding = FeaturedCoachFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        viewModel.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        coachAdapter = FeaturedCoachAdapter {
            println(it.coach_id)
            goToCoachDetail(it.coach_id.toString())
        }

        binding.featuredCoachRecyclerview.adapter = coachAdapter

        val safeArgs: FeaturedCoachFragmentArgs by navArgs()
        val list : MutableList<HomeFeaturedCoachesApi.HomeFeaturedCoaches> = fromJson(safeArgs.json)

        coachAdapter.submitList(list)

        coachTypeAdapter = CoachesTypeAdapter {
            println(it.coach_type_id)
            viewModel.type = it.coach_type_id
            viewModel.action = "sort"
            viewModel.fetchCoachList()
        }

        coachesAdapter = CoachesAdapter {
            println(it.coach_id)
            goToCoachDetail(it.coach_id.toString())
        }

        binding.coachesFragTypeRecyclerview.adapter = coachTypeAdapter
        binding.coachesFragListRecyclerview.adapter = coachesAdapter

        binding.coachesFragSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        initView()

        if (viewModel.isFirstTime) {
            viewModel.fetchCoachType()
            viewModel.isFirstTime = !viewModel.isFirstTime
        }
    }

    private fun initView() {
        coachTypeMenu = PopupMenu(requireContext(), binding.filter.coachType)
        countryMenu = PopupMenu(requireContext(), binding.filter.coachCountry)
        serviceMenu = PopupMenu(requireContext(), binding.filter.coachType)

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
            viewModel.action = "sort"
            viewModel.fetchCoachList()
            binding.filter.filterOverlay.visibility = View.GONE
            Timber.e(item?.title.toString())
            true
        }

        val modelListMap = mutableMapOf<String, String>()
        viewModel.liveCountry.observe(viewLifecycleOwner, Observer { itemList ->
            countryMenu.menu.clear()
            val modelList = arrayListOf<String>()
            itemList?.let {
                itemList.forEach { data ->
                    modelList.add(data.name.toString())
                    modelListMap[data.name.toString()] = data.id.toString()
                    countryMenu.menu.add(data.name.toString())
                }
            }
        })
        countryMenu.setOnMenuItemClickListener { item ->
            modelListMap[item?.title]?.let { modelId ->
                viewModel.city = modelId
            }
            viewModel.action = "sort"
            viewModel.fetchCoachList()
            binding.filter.filterOverlay.visibility = View.GONE
            binding.countryNameTxt.text = item?.title
            Timber.e(item?.title.toString())
            true
        }


        viewModel.liveCoachList.observe(viewLifecycleOwner, Observer { item ->
            coachesAdapter.submitList(viewModel.coachesList)
            if (item != null && item.size > 0) {
                binding.featuredCoachRecyclerview.visibility = View.GONE
                hideProgress()
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

        binding.filter.clearTxt.setOnClickListener {
            viewModel.search= null
            viewModel.type = null
            viewModel.city = null
            viewModel.action = null
            viewModel.service = null
            viewModel.page = null
            viewModel.per_page = null
            binding.countryNameTxt.text = "All"
            binding.coachesFragSearch.text = null
            viewModel.fetchCoachList()
            binding.filter.filterOverlay.visibility = View.GONE
        }

        binding.countryCard.setOnClickListener {
            //binding.filter.filterOverlay.visibility = View.VISIBLE
            countryMenu.show()
        }
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

    private fun goToCoachDetail(id: String) {
        val action = FeaturedCoachFragmentDirections.actionFeaturedCoachFragmentToCoachDetailFragment(
            id
        )
        navController.safeNavigate(action)
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

}