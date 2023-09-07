package com.kl3jvi.animity.di

import android.content.Context
import android.net.ConnectivityManager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kl3jvi.animity.data.network.UpdateClient
import com.kl3jvi.animity.data.network.UpdateService
import com.kl3jvi.animity.data.network.anilist_service.AniListAuthService
import com.kl3jvi.animity.data.network.anilist_service.AniListGraphQlClient
import com.kl3jvi.animity.data.network.anime_service.base.ApiServiceSingleton
import com.kl3jvi.animity.data.network.anime_service.base.BaseClient
import com.kl3jvi.animity.data.network.anime_service.enime.EnimeClient
import com.kl3jvi.animity.data.network.anime_service.gogo.GogoAnimeApiClient
import com.kl3jvi.animity.data.network.interceptor.HeaderInterceptor
import com.kl3jvi.animity.domain.repositories.LoginRepository
import com.kl3jvi.animity.domain.repositories.PersistenceRepository
import com.kl3jvi.animity.parsers.BaseParser
import com.kl3jvi.animity.parsers.GoGoParser
import com.kl3jvi.animity.settings.Settings
import com.kl3jvi.animity.utils.Apollo
import com.kl3jvi.animity.utils.Constants.Companion.ANILIST_API_URL
import com.kl3jvi.animity.utils.RetrofitClient
import com.kl3jvi.animity.utils.setGenericDns
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    @Apollo
    fun provideOkHttpClient(
        localStorage: PersistenceRepository,
        loginRepository: LoginRepository,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor(loginRepository, localStorage))
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            },
        )
        .connectTimeout(40, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @RetrofitClient
    fun provideRetrofitOkHttpClient(
        settings: Settings,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            },
        )
        .setGenericDns(settings)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @RetrofitClient okHttpClient: OkHttpClient,
        @Named("base-url") url: String,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Named("base-url")
    fun provideBaseUrl(): String = "https://rickandmortyapi.com/"

    @Provides
    @Singleton
    fun provideApolloClient(
        @Apollo okHttpClient: OkHttpClient,
    ): ApolloClient = ApolloClient.Builder()
        .serverUrl(ANILIST_API_URL)
        .okHttpClient(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiServiceSingleton(
        @Named("base-url") baseUrlProvider: Provider<String>,
        @RetrofitClient okHttpClient: OkHttpClient,
        settings: Settings,
    ) = ApiServiceSingleton(baseUrlProvider, okHttpClient, settings)

    @Provides
    @Singleton
    fun provideAniListGraphQlClient(
        apolloClient: ApolloClient,
    ): AniListGraphQlClient = AniListGraphQlClient(apolloClient)

    @Singleton
    @Provides
    fun provideAniListAuthService(retrofit: Retrofit): AniListAuthService =
        retrofit.create(AniListAuthService::class.java)

    @Provides
    @Singleton
    fun provideChucker(
        @ApplicationContext context: Context,
    ): ChuckerInterceptor = ChuckerInterceptor.Builder(context)
        .collector(ChuckerCollector(context))
        .maxContentLength(250000L)
        .redactHeaders(emptySet())
        .alwaysReadResponseBody(false)
        .build()

    @Singleton
    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context,
    ): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Singleton
    @Provides
    fun provideFirebaseInstance(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    @IntoMap
    @StringKey("GOGO_ANIME")
    fun provideGogoAnimeApiClient(client: GogoAnimeApiClient): BaseClient = client

    @Provides
    @Singleton
    fun provideUpdateClient(service: UpdateService): UpdateClient = UpdateClient(service)

    @Provides
    @Singleton
    fun provideUpdateService(retrofit: Retrofit): UpdateService =
        retrofit.create(UpdateService::class.java)

    @Provides
    @Singleton
    @IntoMap
    @StringKey("ENIME")
    fun provideEnimeClient(client: EnimeClient): BaseClient = client

    @Provides
    @Singleton
    @IntoMap
    @StringKey("GOGO_ANIME_PARSER")
    fun provideGoGoAnimeParser(parser: GoGoParser): BaseParser = parser

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context,
    ) = FirebaseAnalytics.getInstance(context)
}
