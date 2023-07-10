package mp.app.calonex.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function8
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.landlord.model.ApiAllModel
import mp.app.calonex.landlord.response.*

class FeaturesService : IntentService("BackgroundIntentService") {

    private val LOG_TAG = "BackgroundIntentService"


    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "onCreate")
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(LOG_TAG, "onHandleIntent")

        getAllProperties()
    }

    @SuppressLint("CheckResult")
    private fun getAllProperties() {
        var allPropertyService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)

        var type1 = allPropertyService.getBuildingTypeList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        var type2 = allPropertyService.getBuildingFeatureList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        var type3 = allPropertyService.getParkingTypeList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        var type4 = allPropertyService.getBathroomTypeList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        var type5 = allPropertyService.getUnitFeatureList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        var type6 = allPropertyService.getUnitTypeList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        var type7 = allPropertyService.getUnitUtilities().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        var type8 = allPropertyService.getPropertyExpensesList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        val subscribe: Any = Observable.zip(
            type1,
            type2,
            type3,
            type4,
            type5,
            type6,
            type7,
            type8,
            Function8 { t1: BuildingTypeListResponse, t2: BuildingFeatureListResponse,
                        t3: ParkingTypeListResponse, t4: BathroomTypeListResponse,
                        t5: UnitFeatureListResponse, t6: UnitTypeListResponse,
                        t7: UnitUtilitiesResponse, t8: PropertyExpensesListResponse ->
                ApiAllModel(t1, t2, t3, t4, t5, t6, t7, t8)
            })
            .subscribe(
                { t: ApiAllModel ->
                    allModel = t
                    stopSelf()
                },

                { e ->
                    Log.d("TAG", e.toString())
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()

                    stopSelf()
                })
    }

    companion object {
        var allModel: ApiAllModel? = null

    }
}