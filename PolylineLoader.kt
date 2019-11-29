package ru.smarttransport.citytransport.navi

import common.RepositoryFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

interface PolylineLoaderListener{
    fun onLoadCountPage(countPage : Int)
    fun onNextPage(currentPage: Int)
    fun onComplete()
    fun onError(textError : String, callback: () -> Unit)
    fun onStart()
}

//Надо добавить проверку актуальности данных
class PolylineLoader(private val listener: PolylineLoaderListener) {

    private var countPageDisposable : Disposable? = null
    private var currentPageDisposable: Disposable? = null
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()
    private val navigationRepository = RepositoryFactory.getNavigationRepository()
    private var countPage : Int? = null
    private var currentPageLoad : Int? = null

    fun stop(){
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }

    private fun getCountPage(){
        listener.onStart()
        countPageDisposable =
            navigationRepository.getCountPolylinePage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                    .doFinally{
                        countPageDisposable?.dispose()
                        compositeDisposable.remove(countPageDisposable!!)}
                    .subscribe(
                            {
                                countPage = it
                                if (countPage != null) {
                                    currentPageLoad = 1
                                    listener.onLoadCountPage(countPage!!)
                                    loadPolylines()
                                } else {
                                    listener.onError("NullPointer: CountPage is null"){getCountPage()}
                                }
                            },
                            {
                                listener.onError("Error Load CountPage" + it.message){getCountPage()}
                            }
                    )
        compositeDisposable.add(countPageDisposable!!)
    }

    private fun loadPolylines(){
        if (currentPageLoad == null){
            listener.onError("NullPointer CurrentPage is null", {getCountPage()})
        } else {
            listener.onStart()
            currentPageDisposable = navigationRepository.getPolylineForPage(currentPageLoad!!)
                    .doFinally {
                        currentPageDisposable?.dispose()
                        compositeDisposable.remove(currentPageDisposable!!)
                    }
                    .subscribeOn(Schedulers.io())
                    .map {
                        navigationRepository.savePolylines(it.data)
                        currentPageLoad = currentPageLoad!! + 1
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (currentPageLoad!! > countPage!!){
                            listener.onComplete()
                        } else {
                            listener.onNextPage(currentPageLoad!!)
                            loadPolylines()
                        }
                    },{
                        listener.onError("Error load Polyline" + it.message, {loadPolylines()})
                    })
            compositeDisposable.add(currentPageDisposable!!)
        }
    }

    fun start(){
        getCountPage()
    }

    fun getActual(){}

}

object PolylineLoaderStatic {

    private val loader = PolylineLoader(object : PolylineLoaderListener{
        override fun onStart() {
            error = null
            isLoad = true
        }

        override fun onLoadCountPage(countPage: Int) {
            countLoad = countPage
        }

        override fun onNextPage(currentPage: Int) {
           currentLoad = currentPage
        }

        override fun onComplete() {
            isLoad = false
        }

        override fun onError(textError: String, callback: () -> Unit) {
            error = Pair(textError, callback)
            isLoad = false
        }
    })

    var isLoad = false
        private set

    var countLoad : Int = 0
    var currentLoad : Int = 0

    var isNeedUpdate: Boolean
        get() {
            return !RepositoryFactory.getNavigationRepository().isActualPolylines()
        }
        private set(value) {}

    private var error : Pair<String, ()->Unit>? = null

    var isError: Boolean
        get() {
           return error != null
        }
        private set(value) {

        }

    fun startLoad(){
        loader.start()
    }

    fun clear(){
        loader.stop()
    }

    fun getError(): Pair<String, ()->Unit> {
        if (error != null) {
            return error!!
        } else {
            return Pair("Nothing", { startLoad()})
        }
    }
}