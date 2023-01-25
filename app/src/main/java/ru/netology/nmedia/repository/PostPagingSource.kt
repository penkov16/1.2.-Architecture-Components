import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError


class PostPagingSource (
    private val service: ApiService,
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? {
        return null
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val result = when (params) {

                is LoadParams.Refresh -> service.getLatest(params.loadSize)
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )
                is LoadParams.Append -> service.getBefore(params.key, params.loadSize)
            }

            if (!result.isSuccessful) {
                throw ApiError(result.code(), result.message())
            }
            val body = result.body() ?: throw ApiError(
                result.code(),
                result.message(),
            )

            val nextKey = if (body.isEmpty()) null else body.last().id
            return LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}
