package com.smarttransport.its.monitoringums.search


import android.util.Log
import com.smarttransport.its.monitoringums.R
import com.smarttransport.its.monitoringums.crosswalk.crosswalkedit.SegmentShowType
import com.smarttransport.its.monitoringums.data.Repository
import com.smarttransport.its.monitoringums.data.model.Region
import com.smarttransport.its.monitoringums.data.model.Road
import com.smarttransport.its.monitoringums.data.model.Segment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchPresenter(var view: SearchContract.SearchView?) : SearchContract.SearchPresenter {

    private val disposables = CompositeDisposable()

    override fun getSegments(): ArrayList<Segment> {
        return mSegments
    }

    override fun getRegions(): ArrayList<Region> {
        return mRegions
    }

    override fun getRoads(): ArrayList<Road> {
        return mRoads
    }

    private var mRoads = ArrayList<Road>()
    private var mSegments = ArrayList<Segment>()
    private var mRegions = ArrayList<Region>()

    override fun loadRoads(){
        disposables.add(Repository().getRoads()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it != null){
                                mRoads = ArrayList(it)
                            }
                        },
                        {
                            view?.showError("Ошибка при загрузке дорог"){loadRoads()}
                        }
                ))

    }

    override fun loadRegions(){
        disposables.add(Repository().getRegions()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it != null){
                                mRegions = ArrayList(it)
                            }
                        },
                        {
                            view?.showError("Ошибка при загрузке районов"){loadRegions()}

                        }
                ))

    }

    override fun loadSegments(roadId: Int){
        view?.bottomSheetUpdateStateSegment(SegmentShowType.LOAD)
        disposables.add(Repository().getSegments(roadId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it != null){
                                mSegments = ArrayList(it)
                            }
                            view?.bottomSheetUpdateStateSegment(SegmentShowType.SHOW)
                        },
                        {
                            view?.showError("Ошибка при загрузке участков дорог"){loadSegments(roadId)}
                            view?.bottomSheetUpdateStateSegment(SegmentShowType.SHOW)
                        }
                ))
    }

    override fun unsubscribe() {
        disposables.dispose()
        disposables.clear()
    }

    override fun loadCrosswalks() {
        view?.showProgress(true)
        disposables.add(Repository().getCrosswalks()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            res ->
                                view?.viewCrosswalks(res)

                        },
                        {
                            error ->
                                Log.d("ERROR", error.message)
                            view?.getViewContext()?.getString(R.string.error_load_data)?.let { view?.showError(it){loadCrosswalks()} }
                        },
                        {
                            view?.showProgress(false)
                        }
                ))
    }
}