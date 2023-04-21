package com.kl3jvi.animity.data.mapper

import com.kl3jvi.animity.NotificationsQuery
import com.kl3jvi.animity.data.model.ui_models.AniListMedia
import com.kl3jvi.animity.data.model.ui_models.FuzzyDate
import com.kl3jvi.animity.data.model.ui_models.Genre
import com.kl3jvi.animity.data.model.ui_models.MediaCoverImage
import com.kl3jvi.animity.data.model.ui_models.MediaTitle
import com.kl3jvi.animity.data.model.ui_models.Notification
import com.kl3jvi.animity.data.model.ui_models.NotificationData
import com.kl3jvi.animity.data.model.ui_models.User
import com.kl3jvi.animity.data.model.ui_models.UserAvatar

fun NotificationsQuery.Data.convert(): NotificationData {
    val airingNotifications = mutableListOf<Notification>()
    val followingNotifications = mutableListOf<Notification>()
    val likeNotification = mutableListOf<Notification>()
    val messageNotifications = mutableListOf<Notification>()

    page?.notifications?.forEach { notification ->
        when {
            notification?.onAiringNotification != null -> {
                airingNotifications.add(
                    Notification(
                        id = notification.onAiringNotification.id,
                        episode = notification.onAiringNotification.episode,
                        contexts = notification.onAiringNotification.contexts,
                        media = notification.onAiringNotification.media.convert()
                    )
                )
            }

            notification?.onFollowingNotification != null -> {
                followingNotifications.add(
                    Notification(
                        id = notification.onFollowingNotification.id,
                        episode = null,
                        user = User(
                            id = notification.onFollowingNotification.user?.id ?: 0,
                            name = notification.onFollowingNotification.user?.name.orEmpty(),
                            avatar = UserAvatar(
                                notification.onFollowingNotification.user?.avatar?.large.orEmpty(),
                                notification.onFollowingNotification.user?.avatar?.medium.orEmpty()
                            )
                        ),
                        contexts = listOf(notification.onFollowingNotification.context)
                    )
                )
            }

            notification?.onActivityLikeNotification != null -> {
                likeNotification.add(
                    Notification(
                        id = notification.onActivityLikeNotification.id,
                        episode = null,
                        user = User(
                            id = notification.onActivityLikeNotification.user?.id ?: 0,
                            name = notification.onActivityLikeNotification.user?.name.orEmpty(),
                            avatar = UserAvatar(
                                notification.onActivityLikeNotification.user?.avatar?.large.orEmpty(),
                                notification.onActivityLikeNotification.user?.avatar?.medium.orEmpty()
                            )
                        ),
                        contexts = listOf(notification.onActivityLikeNotification.context)
                    )
                )
            }

            notification?.onActivityMessageNotification != null -> {
                likeNotification.add(
                    Notification(
                        id = notification.onActivityMessageNotification.id,
                        episode = null,
                        user = User(
                            id = notification.onActivityMessageNotification.user?.id ?: 0,
                            name = notification.onActivityMessageNotification.user?.name.orEmpty(),
                            avatar = UserAvatar(
                                notification.onActivityMessageNotification.user?.avatar?.large.orEmpty(),
                                notification.onActivityMessageNotification.user?.avatar?.medium.orEmpty()
                            )
                        ),
                        contexts = listOf(notification.onActivityMessageNotification.context)
                    )
                )
            }
        }
    }

    return NotificationData(
        airingNotifications = airingNotifications,
        followingNotifications = followingNotifications,
        likeNotification = likeNotification,
        messageNotifications = messageNotifications
    )
}

private fun NotificationsQuery.Media?.convert(): AniListMedia {
    return AniListMedia(
        idAniList = this?.homeMedia?.id ?: 0,
        idMal = this?.homeMedia?.idMal,
        title = MediaTitle(userPreferred = this?.homeMedia?.title?.userPreferred.orEmpty()),
        type = this?.homeMedia?.type,
        format = this?.homeMedia?.format,
        isFavourite = this?.homeMedia?.isFavourite ?: false,
        streamingEpisode = this?.homeMedia?.streamingEpisodes?.mapNotNull { it.convert() },
        nextAiringEpisode = this?.homeMedia?.nextAiringEpisode?.airingAt,
        status = this?.homeMedia?.status,
        description = this?.homeMedia?.description.orEmpty(),
        startDate = if (this?.homeMedia?.startDate?.year != null) {
            FuzzyDate(this.homeMedia.startDate.year, this.homeMedia.startDate.month, this.homeMedia.startDate.day)
        } else {
            null
        },
        coverImage = MediaCoverImage(
            this?.homeMedia?.coverImage?.extraLarge.orEmpty(),
            this?.homeMedia?.coverImage?.large.orEmpty(),
            this?.homeMedia?.coverImage?.medium.orEmpty()
        ),
        bannerImage = this?.homeMedia?.bannerImage.orEmpty(),
        genres = this?.homeMedia?.genres?.mapNotNull { Genre(name = it.orEmpty()) } ?: emptyList(),
        averageScore = this?.homeMedia?.averageScore ?: 0,
        favourites = this?.homeMedia?.favourites ?: 0
    )
}
