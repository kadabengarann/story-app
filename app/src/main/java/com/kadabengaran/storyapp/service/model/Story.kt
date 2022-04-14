package com.kadabengaran.storyapp.service.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
): Parcelable
