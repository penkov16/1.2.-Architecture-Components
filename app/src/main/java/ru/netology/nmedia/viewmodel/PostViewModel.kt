package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject



private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    authorId = 0,
    likedByMe = false,
    likes = 0,
    published = "",
    newer =0,
    attachment = Attachment (
        url = "http://10.0.2.2:9999/media/d7dff806-4456-4e35-a6a1-9f2278c5d639.png",
        type = AttachmentType.IMAGE
    )
)


@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    auth: AppAuth,
) : ViewModel() {
val data: Flow<PagingData<Post>>  =repository.data
        .cachedIn(viewModelScope)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val noPhoto = PhotoModel()
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo
    private val _error = SingleLiveEvent<ErrorModel>()
    val error: LiveData<ErrorModel>
        get() = _error

    init {

        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            // repository.stream.cachedIn(viewModelScope).
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
//            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when(_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(post: Post){
        edited.value = post

    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            repository.unlikeById(id)


        } catch (e: Exception) {
            _error.postValue(ErrorModel(ErrorType.AppError, ActionType.Like, e.message ?: ""))
        }
    }


    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.RemoveById, e.message ?: ""
                )
            )
        }

    }

    fun countMessegePost ()= viewModelScope.launch {
        try {
            repository.countMessegePost()
        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.CountMessegePost, e.message ?: ""
                )
            )
        }
    }


    fun unCountNewer()= viewModelScope.launch {

        try {
            repository.unCountNewer()
        } catch (e: Exception) {

            _error.postValue(
                ErrorModel(
                    ErrorType.NetworkError,
                    ActionType.UnCountMessegePost, e.message ?: ""
                )
            )
        }
    }

}


