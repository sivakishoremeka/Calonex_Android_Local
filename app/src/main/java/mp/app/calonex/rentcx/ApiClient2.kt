package mp.app.calonex.rentcx

import android.content.Context
import mp.app.calonex.common.network.OkHttpProduction
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient2 {

    //private val BASE_URL: String = "https://api.calonex.com/"

    private val BASE_URL: String = "https://api-rentcx.calonex.com/"

    private var mContext: Context

    constructor(context: Context) {
        this.mContext = context
    }


    private fun provideRestAdapter(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpProduction.getOkHttpClient(mContext, true))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun <S> provideService(serviceClass: Class<S>): S {
        return provideRestAdapter().create(serviceClass)
    }
}