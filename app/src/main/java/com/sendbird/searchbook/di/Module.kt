package com.sendbird.searchbook.di

import com.sendbird.searchbook.api.BookAPI
import com.sendbird.searchbook.model.DataModel
import com.sendbird.searchbook.model.DataModelImp
import com.sendbird.searchbook.setting.Constants.Companion.SERVER_URL_FULL
import com.sendbird.searchbook.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val retrofit = module {
    single<BookAPI> {
        HttpLoggingInterceptor().let { httpLoggingInterceptor ->
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            Retrofit.Builder()
                .baseUrl(SERVER_URL_FULL)
                .client(OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookAPI::class.java)
        }
    }
}

val netModel = module {
    factory<DataModel> {
        DataModelImp(get())
    }
}

val viewModel = module {
    viewModel { MainViewModel(get()) }
}

val allModule = listOf(retrofit, netModel, viewModel)