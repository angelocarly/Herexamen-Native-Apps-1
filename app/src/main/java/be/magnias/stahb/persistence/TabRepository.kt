package be.magnias.stahb.persistence

import be.magnias.stahb.App
import be.magnias.stahb.model.Tab
import be.magnias.stahb.model.TabDao
import be.magnias.stahb.network.StahbApi
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TabRepository(private val tabDao : TabDao)
{
    @Inject
    lateinit var stahbApi: StahbApi

    init {
        App.appComponent.inject(this)
    }

    fun getAllTabs(): Observable<List<Tab>> {
        return Observable.concatArrayEagerDelayError(
            loadTabsFromCache(),
            loadTabsFromApi()
        )
    }

    private fun loadTabsFromApi(): Observable<List<Tab>> {
        //Load tabs from network
        return stahbApi.getTabs()
            .onErrorResumeNext(Observable.empty())
            .doOnNext {
                tabDao.deleteAll()
                tabDao.insertAll(it)
                Logger.d("Dispatching tabs from API")
            }
            .subscribeOn(Schedulers.io())
    }

    private fun loadTabsFromCache() : Observable<List<Tab>> {
        return tabDao.getAllTabs().filter { it.isNotEmpty() }
            .toObservable()
            .doOnNext {
                Logger.d("Dispatching tabs from database")
            }
            .subscribeOn(Schedulers.io())
    }
}