package ru.netology.nmedia.activity
import Wallsevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLargePhotoBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject


@AndroidEntryPoint
class FragmentLargePhoto: Fragment() {


    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLargePhotoBinding.inflate(
            inflater,
            container,
            false
        )

        val post =  Post(
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
        val service = Wallsevice()

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest { posts ->
                posts.filter {it.id == post.id}?.let {
                    binding.like.isChecked = post.likedByMe
                        binding.like.text = "${service.zeroingOutLikes(post.likes.toLong())}"
                }
            }
        }
        binding.apply {

            post.attachment?.let {

                Log.d("MyLog", "${BuildConfig.BASE_URL}/media/${it.url}")

                Glide.with(photo)
                    .load(arguments?.getString("url"))
                    .timeout(10_000)
                    .into(photo)
            }

                arguments?.getString("likes")
                    ?.let(binding.like::setText)

                post.likedByMe = arguments?.getBoolean("likedByMeTrue") == true
                like.isChecked =  post.likedByMe

                binding.like.setOnClickListener {


                            if (post.likedByMe){
                                viewModel.unlikeById(post.id.toLong())
                            } else {
                                viewModel.likeById(post.id.toLong())
                            }
                        }

                binding.share.setOnClickListener {

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }


            viewModel.error.observe(viewLifecycleOwner) { error ->
                Snackbar.make(
                    binding.root,
                    "${getString(R.string.error_loading)}: ${error.message}",

                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    setAction(R.string.retry_loading) {
                        when (error.action) {

                            ActionType.Like -> viewModel.likeById(id.toLong())
                            ActionType.unlikeById -> viewModel.unlikeById(id.toLong())
                        }
                    }
                    show()
                }
            }

            }

        return binding.root
    }
}