package com.smarttransport.its.monitoringums.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.smarttransport.its.monitoringums.R
import com.smarttransport.its.monitoringums.crosswalk.crosswalkedit.SegmentShowType

class FilterBottomSheet(context: Context, attributeSet: AttributeSet) : CoordinatorLayout(context, attributeSet) {

    enum class STATE {
        OPEN,
        CLOSED
    }

    private var etRoad: EditText
    private var etRegion: EditText
    private var etSegment: EditText
    private var tvSegmentLabel: TextView
    private var btnOk: Button
    private var ivRegionExit: ImageView
    private var ivRoadExit: ImageView
    private var ivSegmentExit: ImageView
    private var mProgressBarSegment: ProgressBar

    fun addListenerRoad(listener: OnClickListener){
        etRoad.setOnClickListener(listener)
    }

    fun addListenerRegion(listener: OnClickListener){
        etRegion.setOnClickListener(listener)
    }

    fun addListenerSegment(listener: OnClickListener){
        etSegment.setOnClickListener(listener)
    }

    fun addListenerRoadCancel(listener: OnClickListener){
        ivRoadExit.setOnClickListener(listener)
    }

    fun addListenerRegionCancel(listener: OnClickListener){
        ivRegionExit.setOnClickListener(listener)
    }

    fun addListenerSegmentCancel(listener: OnClickListener){
        ivSegmentExit.setOnClickListener(listener)
    }

    fun addListenerBtnOk(listener: OnClickListener){
        btnOk.setOnClickListener(listener)
    }

    fun showLoadingSegment(type: SegmentShowType) {
        when(type){
            SegmentShowType.NOT_SHOW -> {
                tvSegmentLabel.visibility = View.GONE
                etSegment.visibility = View.GONE
                ivSegmentExit.visibility = View.GONE
                mProgressBarSegment.visibility = View.GONE
            }
            SegmentShowType.SHOW -> {
                tvSegmentLabel.visibility = View.VISIBLE
                etSegment.visibility = View.VISIBLE
                ivSegmentExit.visibility = View.VISIBLE
                mProgressBarSegment.visibility = View.GONE
                etSegment.isEnabled = true
                setState(STATE.OPEN)
            }
            SegmentShowType.LOAD -> {
                tvSegmentLabel.visibility = View.VISIBLE
                etSegment.visibility = View.VISIBLE
                ivSegmentExit.visibility = View.GONE
                mProgressBarSegment.visibility = View.VISIBLE
                etSegment.isEnabled = false
            }
        }

    }

    private var mBehavior: BottomSheetBehavior<FilterBottomSheet>? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val root = inflater.inflate(R.layout.fragment_filter, this, true)
        etRegion = root.findViewById(R.id.et_filter_region)
        etRoad = root.findViewById(R.id.et_filter_road)
        etSegment = root.findViewById(R.id.et_filter_segment)
        tvSegmentLabel = root.findViewById(R.id.tv_filter_segment)
        btnOk = root.findViewById(R.id.btn_filter)
        ivRegionExit = root.findViewById(R.id.btn_filter_region_cancel)
        ivRoadExit = root.findViewById(R.id.btn_filter_road_cancel)
        ivSegmentExit = root.findViewById(R.id.btn_filter_segment_cancel)
        mProgressBarSegment = root.findViewById(R.id.pb_segment)

    }

    fun setBehavior(behavior: BottomSheetBehavior<FilterBottomSheet>){
        this.mBehavior = behavior
        this.mBehavior?.setHideable(true)
        this.mBehavior?.setPeekHeight(0)
        this.mBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
    }

    fun setState(state : STATE){
        this.post {
            mBehavior?.setHideable(true)
            mBehavior?.setPeekHeight(if (state == STATE.CLOSED) 0 else getAnchorHeight())
        }
        when(state){
            STATE.OPEN ->
                mBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            STATE.CLOSED ->
                mBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun getAnchorHeight(): Int {
        val h = this.height
        val scale = context.resources.displayMetrics.density
        return if (h == 0) { (h * scale + 0.5f).toInt()} else h
    }

    fun updateFields(parameters: FilterParameters){
        etRoad.setText(parameters.roadName)
        etRegion.setText(parameters.regionName)
        etSegment.setText(parameters.segmentName)

    }



}