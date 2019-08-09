package be.magnias.stahb.injection.module

import be.magnias.stahb.network.ServiceInterceptor
import be.magnias.stahb.network.StahbApi
import be.magnias.stahb.persistence.UserService
import be.magnias.stahb.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about network
 */
@Module
// Safe here as we are dealing with a Dagger 2 module
@Suppress("unused")
class NetworkModule {
    /**
     * Provides the Stahb service implementation.
     * @param retrofit the Retrofit object used to instantiate the service
     * @return the Post service implementation.
     */
    @Provides
    internal fun providePostApi(retrofit: Retrofit): StahbApi {
        return retrofit.create(StahbApi::class.java)
    }

    /**
     * Provides the Retrofit object.
     * @return the Retrofit object
     */
    @Provides
    @Singleton
    internal fun provideRetrofitInterface(): Retrofit {
        var client = OkHttpClient.Builder()
            .addInterceptor(ServiceInterceptor())
            //.readTimeout(45,TimeUnit.SECONDS)
            //.writeTimeout(45,TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Provides
    @Singleton
    internal fun provideUserService(): UserService {
        return UserService()
    }
}