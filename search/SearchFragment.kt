package com.smarttransport.its.monitoringums.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.smarttransport.its.monitoringums.data.model.Crosswalk
import com.smarttransport.its.monitoringums.R
import com.smarttransport.its.monitoringums.crosswalk.crosswalkedit.SegmentShowType
import com.smarttransport.its.monitoringums.custom.SelectorSearch
import com.smarttransport.its.monitoringums.databinding.FragmentSearchBinding
import com.smarttransport.its.monitoringums.utils.IconUtils
import kotlinx.android.synthetic.main.search_bar.view.*


class SearchFragment : Fragment(), SearchContract.SearchView {
    override fun bottomSheetUpdateStateSegment(type: SegmentShowType) {
        bottomSheet.showLoadingSegment(type)
    }

    private lateinit var presenter : SearchContract.SearchPresenter
    var adapter : CrosswalkAdapter? = null

    lateinit var binding: FragmentSearchBinding
    var parameters: FilterParameters = FilterParameters()
    private var iconUtils: IconUtils? = null

    lateinit var bottomSheet: FilterBottomSheet

    override fun getViewContext(): Context? {
        return context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        binding.executePendingBindings()

        iconUtils = getViewContext()?.let { IconUtils(it) }

        binding.searchToolbar.iv_search_back.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.searchToolbar.iv_search_settings.setOnClickListener {
            binding.fSearchFilterBs.setState(FilterBottomSheet.STATE.OPEN)
        }

        adapter = CrosswalkAdapter{
            item ->
                val mIntent = Intent()

                mIntent.putExtra(getString(R.string.INTENT_SEARCH), item)

                activity?.setResult(Activity.RESULT_OK, mIntent)
                activity?.finish()

        }

        binding.fSearchRv.layoutManager = LinearLayoutManager(activity)
        binding.fSearchRv.adapter = adapter

        bottomSheet = binding.fSearchFilterBs
        bottomSheet.setBehavior(BottomSheetBehavior.from(binding.fSearchFilterBs))

        bottomSheet.addListenerRoad(View.OnClickListener {
            val selectorSearch = SelectorSearch(presenter.getRoads()){
                parameters.roadId = it.getId()
                parameters.roadName = it.getTitle()
                presenter.loadSegments(it.getId())
                bottomSheet.updateFields(parameters)
            }
            selectorSearch.show(childFragmentManager,selectorSearch.tag)
        })


        bottomSheet.addListenerRegion(View.OnClickListener {
                val selectorSearch = SelectorSearch(presenter.getRegions()){
                parameters.regionId = it.getId()
                parameters.regionName = it.getTitle()
                bottomSheet.updateFields(parameters)
            }
            selectorSearch.show(childFragmentManager,selectorSearch.tag)
        })

        bottomSheet.addListenerSegment(View.OnClickListener {
            val selectorSearch = SelectorSearch(presenter.getSegments()){
                parameters.segmentId = it.getId()
                parameters.segmentName = it.getTitle()
                bottomSheet.updateFields(parameters)
            }
            selectorSearch.show(childFragmentManager, selectorSearch.tag)
        })

        bottomSheet.addListenerRoadCancel(View.OnClickListener {
            parameters.roadName = ""
            parameters.roadId = null
            bottomSheet.updateFields(parameters)
        })

        bottomSheet.addListenerRegionCancel(View.OnClickListener {
            parameters.regionName = ""
            parameters.regionId = null
            bottomSheet.updateFields(parameters)
        })

        bottomSheet.addListenerSegmentCancel(View.OnClickListener {
            parameters.segmentName = ""
            parameters.segmentId = null
            bottomSheet.updateFields(parameters)
        })

        bottomSheet.addListenerBtnOk(View.OnClickListener {
            adapter?.filtered(parameters)
            viewResult()
        })

        bottomSheet.showLoadingSegment(SegmentShowType.NOT_SHOW)


        binding.searchToolbar.search_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                parameters.textSearch = s.toString()
                adapter?.filtered(parameters)
                viewResult()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SearchPresenter(this)
        presenter.loadRegions()
        presenter.loadRoads()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadCrosswalks()

    }

    override fun viewCrosswalks(crosswalks : List<Crosswalk>) {
        adapter?.setData(crosswalks)
        //recyclerView?.adapter = adapter
        viewResult()
    }

    override fun showError(text: String, callback: (() -> Unit)?) {
        showProgress(false)
        bottomSheet.setState(FilterBottomSheet.STATE.CLOSED)
        val snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(context?.getString(R.string.repeat)) { callback?.invoke() }
        snackbar.show()
    }

    override fun showProgress(isShow: Boolean) {
        binding.viewModel?.isLoading?.set(isShow)
    }

    fun viewResult(){
        binding.viewModel?.isError?.set(adapter?.filteredList?.size == 0)
        updateIconFilter()
        bottomSheet.setState(FilterBottomSheet.STATE.CLOSED)
    }

    private fun updateIconFilter(){
        if (parameters.regionId != null || parameters.roadId != null){
            binding.searchToolbar.iv_search_settings.
                        setImageBitmap(iconUtils?.getIconBitmap(IconUtils.ICON.FILTERSELECTED)
                    )
        }else{
            binding.searchToolbar.iv_search_settings.
                        setImageBitmap(iconUtils?.getIconBitmap(IconUtils.ICON.FILTER)
                    )
        }
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}
