
package ru.netology.nmedia.adapter

import Wallsevice
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.view.loadCircleCrop

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun pushPhoto (post: Post) {}

}
class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }



    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // FIXME: students will do in HW
       val post = getItem(position) ?: return
            holder.bind(post)
        }
    }


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    val service = Wallsevice()
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            like.isChecked = post.likedByMe
            like.text = "${service.zeroingOutLikes(post.likes.toLong())}"


            photo.setImageURI(Uri.parse( "${BuildConfig.BASE_URL}/attachment/моя_картинка.jpg"))

            photo.isVisible = post.attachment != null
            post.attachment?.let {
                Log.d("MyLog", "${BuildConfig.BASE_URL}/media/${it.url}")
                Glide.with(photo)
                    .load("${BuildConfig.BASE_URL}/media/${it.url}")
                    .timeout(10_000)
                    .into(photo)
            }

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {

                PopupMenu(it.context, it).apply {

                    inflate(R.menu.options_post)
                    // TODO: if we don't have other options, just remove dots

                    menu.setGroupVisible(R.id.owned, post.ownedByMe)

                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            photoContainer.setOnClickListener {
                onInteractionListener.pushPhoto(post)
            }

        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}



