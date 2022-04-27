package com.kadabengaran.storyapp

import com.kadabengaran.storyapp.service.database.StoryEntity
import com.kadabengaran.storyapp.service.model.*

object DummyDatas {

    fun generateDummyStoriesEntity(): List<StoryEntity> {
        val storiesList = ArrayList<StoryEntity>()
        for (i in 0..10) {
            val story = StoryEntity(
                "story_id_$i",
                "John Doe $i",
                "description $i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                .69,
                .69,
            )
            storiesList.add(story)
        }
        return storiesList
    }

    fun generateDummyStories(): List<StoryItem> {
        val newsList = ArrayList<StoryItem>()
        for (i in 0..10) {
            val story = StoryItem(
                "story_id_$i",
                "John Doe $i",
                "description $i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                .69,
                .69,
            )
            newsList.add(story)
        }
        return newsList
    }

    fun generateDummyStoryResponse(): ResponseStory {
        val newsList = ArrayList<StoryItem>()
        for (i in 0..10) {
            val story = StoryItem(
                "story_id_$i",
                "John Doe $i",
                "description $i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                .69,
                .69,
            )
            newsList.add(story)
        }
        return ResponseStory(false, newsList)
    }
    fun generateDummyUserEntity() =  LoginResult(
            "12342069",
            "John Doe",
            "214428oib42uyeeob87c23t472q38742qd9t897cvqf8vt98q27d8329q5"
    )

fun generateDummyPostResponse() =  FileUploadResponse(
            false,
            "Success"
        )

}