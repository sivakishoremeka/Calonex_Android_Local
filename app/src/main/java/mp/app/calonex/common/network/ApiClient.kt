package mp.app.calonex.common.network

import android.content.Context
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    //DEV BASE URL
    //private val BASE_URL: String = "http://13d9aea0ba85.ngrok.io/api/"
    //private val BASE_URL: String = "https://uat-apigateway.calonex.com/api/"
    //PROD BASE URL
    //private val BASE_URL: String = "http://ec2-54-67-28-34.us-west-1.compute.amazonaws.com:1026/api/"
    //private val BASE_URL: String = "https://staging-api.calonex.com/"
    //private val BASE_URL: String = "https://api.calonex.com/"
    //private val BASE_URL: String = "https://364d-202-142-118-163.ngrok.io/api/" // https://364d-202-142-118-163.ngrok.io/

    private val SOCIAL_BASE_URL: String = "https://api.calonex.com/"
    private val BASE_URL: String = "https://api.calonex.com/api/"
    private val STRIPEPIITOKENURL:String="https://api.stripe.com/v1/tokens"
//    private val BASE_URL: String = "	https://staging-api.calonex.com/api/"

    //private val BASE_URL: String = "	http://192.168.1.193:1026/api/"
    //private val BASE_URL: String = "https://staging-api.calonex.com/api/"
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

    private fun provideRestAdapterForStripeKey(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpProduction.getOkHttpClient(mContext, true))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun provideRestAdapterSocial(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SOCIAL_BASE_URL)
            .client(OkHttpSocialService.getOkHttpClient(mContext, true))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun <S> provideService(serviceClass: Class<S>): S {
        return provideRestAdapter().create(serviceClass)
    }

    fun <S> provideServiceSocial(serviceClass: Class<S>): S {
        return provideRestAdapterSocial().create(serviceClass)
    }
}