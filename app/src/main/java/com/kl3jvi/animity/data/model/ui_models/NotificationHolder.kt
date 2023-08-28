package com.kl3jvi.animity.data.model.ui_models

data class NotificationData(
    val airingNotifications: List<Notification>,
    val followingNotifications: List<Notification>,
    val likeNotification: List<Notification>,
    val messageNotifications: List<Notification>,
)
