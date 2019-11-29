package com.smarttransport.its.monitoringums.search

import android.content.Context
import com.smarttransport.its.monitoringums.crosswalk.crosswalkedit.SegmentShowType
import com.smarttransport.its.monitoringums.data.model.Crosswalk
import com.smarttransport.its.monitoringums.data.model.Region
import com.smarttransport.its.monitoringums.data.model.Road
import com.smarttransport.its.monitoringums.data.model.Segment

interface SearchContract {
    interface SearchView {
        fun viewCrosswalks(crosswalks : List<Crosswalk>)
        fun showError(text: String, callback:(()->Unit)?)
        fun showProgress(isShow: Boolean)
        fun getViewContext():Context?
        fun bottomSheetUpdateStateSegment(type: SegmentShowType)
    }

    interface SearchPresenter{
        fun loadCrosswalks()
        fun unsubscribe()
        fun loadRoads()
        fun loadRegions()
        fun loadSegments(roadId: Int)
        fun getRegions(): ArrayList<Region>
        fun getRoads(): ArrayList<Road>
        fun getSegments(): ArrayList<Segment>
    }
}