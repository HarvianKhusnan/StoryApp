package com.example.storyapp.data


import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.api.ApiService
import com.example.storyapp.dao.DatabaseStory
import com.example.storyapp.dao.RemoteKeysStory
import com.example.storyapp.response.Story

@OptIn(ExperimentalPagingApi::class)
class StoryRemote(
    private val databaseStory: DatabaseStory,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, Story>(){

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val keysRemote = getRemoteKeyNearPosition(state)
                keysRemote?.nextKey?.minus(1) ?: STARTING_PAGE
            }

            LoadType.PREPEND -> {
                val keysRemote = getRemoteFirstItem(state)
                val prevKey = keysRemote?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = keysRemote != null)
                prevKey
            }

            LoadType.APPEND -> {
                val keysRemote = getRemoteLastItem(state)
                val nextKey = keysRemote?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = keysRemote != null)
                nextKey
            }
        }

        return try{
            val respData = apiService.getForStories("Bearer $token", page as Int,state.config.pageSize).storyList

            val endofpage = respData.isEmpty()

            databaseStory.withTransaction {
                if (loadType == LoadType.REFRESH){
                    databaseStory.keysRemoteDao().remoteKeysDelete()
                    databaseStory.daoStories().deleteStory()
                }
                val keyPrev = if(page == 1) null else page -1
                val keyNext = if(endofpage) null else page +1
                val key = respData.map {
                    RemoteKeysStory(id = it.id, prevKey = keyPrev, nextKey = keyNext)
                }
                databaseStory.keysRemoteDao().insert(key)
                databaseStory.daoStories().insert(respData)
            }
            return MediatorResult.Success(endOfPaginationReached = endofpage)
        }catch (e : Exception){
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteLastItem(state: PagingState<Int, Story>) : RemoteKeysStory?{
        return state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.lastOrNull()?.let {
            data ->
            databaseStory.keysRemoteDao().idKeysRemote(data.id)
        }
    }

    private suspend fun getRemoteFirstItem(state: PagingState<Int, Story>) : RemoteKeysStory?{
        return state.pages.firstOrNull{it.data.isNotEmpty()}?.data?.firstOrNull()?.let {
            data ->
            databaseStory.keysRemoteDao().idKeysRemote(data.id)
        }
    }

    private suspend fun getRemoteKeyNearPosition(state: PagingState<Int, Story>): RemoteKeysStory?{
        return state.anchorPosition?.let { pos ->
            state.closestItemToPosition(pos)?.id?.let { id ->
                databaseStory.keysRemoteDao().idKeysRemote(id)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private companion object{
        const val STARTING_PAGE = 1
    }

}