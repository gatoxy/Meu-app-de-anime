package com.kl3jvi.animity.ui.activities.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.kl3jvi.animity.data.model.ui_models.Content
import com.kl3jvi.animity.domain.repositories.activity_repositories.PlayerRepository
import com.kl3jvi.animity.persistence.EpisodeDao
import com.kl3jvi.animity.utils.Result
import com.kl3jvi.animity.utils.asResult
import com.kl3jvi.animity.utils.logError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val episodeDao: EpisodeDao,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    var episodeUrl = MutableStateFlow("")
    var videoUrlLiveData = MutableStateFlow<Result<List<String>>>(Result.Loading)
    private var _playBackPosition = MutableLiveData<Long>()

    init {
        getEpisodeUrl()
    }

    /**
     * It creates a flow that emits the current position of the exoPlayer every second.
     *
     * @param exoPlayer ExoPlayer? - The ExoPlayer instance
     */
    fun audioProgress(exoPlayer: ExoPlayer?) = flow {
        exoPlayer?.currentPosition?.let {
            while (it < 200000) {
                emit(exoPlayer.currentPosition)
                delay(1000)
            }
        }
    }.flowOn(Dispatchers.IO + viewModelScope.coroutineContext)

    private fun getEpisodeUrl() {
        viewModelScope.launch(ioDispatcher) {
            episodeUrl.collectLatest { url ->
                val episodeUrl =
                    playerRepository.fetchEpisodeMediaUrl(url = url.orEmpty()).asResult()
                        .flatMapLatest { episodeInfo ->
                            when (episodeInfo) {
                                is Result.Error -> {
                                    logError(episodeInfo.exception)
                                    emptyFlow()
                                }
                                is Result.Loading -> {
                                    emptyFlow()
                                }
                                is Result.Success -> {
                                    val id = Regex("id=([^&]+)").find(
                                        episodeInfo.data.vidCdnUrl.orEmpty()
                                    )?.value?.removePrefix("id=")
                                    playerRepository.fetchEncryptedAjaxUrl(
                                        url = episodeInfo.data.vidCdnUrl.orEmpty(),
                                        id = id.orEmpty()
                                    )
                                }
                            }
                        }.flatMapLatest {
                            playerRepository.fetchM3u8Url(url = it)
                        }.asResult()

                episodeUrl.collect {
                    videoUrlLiveData.value = it
                }
            }
        }
    }

    fun insertOrUpdate(content: Content) {
        viewModelScope.launch(ioDispatcher) {
            if (episodeDao.isEpisodeOnDatabase(content.episodeUrl) && content.watchedDuration > 0) {
                episodeDao.updateEpisode(content)
            } else {
                episodeDao.insertEpisode(content)
            }
        }
    }

    /**
     * It's getting the playback position of the episode from the database and posting it to the
     * _playBackPosition
     *
     * @param episodeUrl The url of the episode that is being played.
     * @return The _playBackPosition is being returned.
     */
    fun getPlaybackPosition(episodeUrl: String): LiveData<Long> {
        viewModelScope.launch(ioDispatcher) {
            if (episodeDao.isEpisodeOnDatabase(episodeUrl)) {
                episodeDao.getEpisodeContent(episodeUrl).collectLatest {
                    _playBackPosition.postValue(it.watchedDuration)
                }
            }
        }
        return _playBackPosition
    }
}
