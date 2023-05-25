package com.yhezra.storyapps.data.remote.utils.story

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yhezra.storyapps.data.remote.response.StoryResponse
import com.yhezra.storyapps.data.remote.retrofit.ApiService

class PagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, StoryResponse>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStories(token, page, params.loadSize)
            LoadResult.Page(
                data = responseData.listStoryResponse,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (responseData.listStoryResponse.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}