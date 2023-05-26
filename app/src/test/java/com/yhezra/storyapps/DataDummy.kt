package com.yhezra.storyapps

import com.yhezra.storyapps.data.local.entity.StoryEntity

object DataDummy {

    fun generateDummyStory(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                i.toString(),
                "name + $i",
                "description $i",
                "photo url $i",
                "created at $i",
                i.toDouble(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}