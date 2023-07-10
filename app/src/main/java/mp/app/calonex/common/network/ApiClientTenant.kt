package mp.app.calonex.common.network

import android.content.Context
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClientTenant {

    // PROD BASE URL
    //private val BASE_URL: String = "https://apigateway.calonex.com/api/tenant-service/"
    // UAT BASE URL
    private val BASE_URL:String ="https://uat-apigateway.calonex.com/api/tenant-service/"
    // DEV BASE URL
    //private val BASE_URL: String = "https://dev-apigateway.calonex.com/api/tenant-service/"


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