package com.example.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.api.ApiService
import com.example.storyapp.response.Story
import com.example.storyapp.response.StoryResponse
import java.lang.Exception

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, Story>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getForStories(data.token,position, params.loadSize)
            LoadResult.Page(
                data = responseData.execute().body()?.storyList!!,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.execute().body()?.storyList!!.isNullOrEmpty()) null else position + 1
            )
        }catch (exception : Exception){
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}