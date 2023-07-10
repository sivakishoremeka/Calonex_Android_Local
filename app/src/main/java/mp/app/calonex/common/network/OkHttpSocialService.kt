package mp.app.calonex.common.network

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class OkHttpSocialService {
    fun OkHttpSocialService() = Unit

    companion object getOkHttpClient {
        val DISK_CACHE_SIZE = 5 * 1024 * 1024 // 5MB
        fun getOkHttpClient(context: Context, debug: Boolean): OkHttpClient {
            // Install an HTTP cache in the context cache directory.
            val cacheDir = File(context.cacheDir, "http")
            val cache = Cache(cacheDir, DISK_CACHE_SIZE.toLong())

            val builder = OkHttpClient.Builder().cache(cache)
            builder.connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                //.addInterceptor(NetworkInterceptor(context))
                .addInterceptor { chain ->
                    // In login api, we can't have user access token
                    if (chain.request().url.toString().contains("login") ){
                        val newRequest = chain.request().newBuilder()
                            .build()
                        chain.proceed(newRequest)
                    }
                    // It's a multipart request, so content type should not be application/json
                    else if(chain.request().url.toString().contains("uploadMultipleImages") || chain.request().url.toString().contains("uploadFile")){
                        val newRequest = chain.request().newBuilder()
                            //.addHeader("Authorization", "bearer "+ accessToken)
                            .build()
                        chain.proceed(newRequest)
                    }
                    // It's NOT a multipart request, so content type should not be application/json
                    else {
                        val newRequest = chain.request().newBuilder()
                            //.addHeader("Authorization", "bearer "+ accessToken)
                            //.addHeader("Content-Type", "application/json")
                            .build()
                        chain.proceed(newRequest)
                    }

                }
            if (debug) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(loggingInterceptor)
            }
            return builder.build()
        }
    }
}