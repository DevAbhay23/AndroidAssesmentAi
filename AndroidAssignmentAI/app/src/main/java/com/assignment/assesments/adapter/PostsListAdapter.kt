package com.assignment.assesments.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.assignment.assesments.R
import com.assignment.assesments.models.Posts
import kotlinx.android.synthetic.main.recycl_item_layout.view.*


class PostsListAdapter internal constructor(
    mContext: Context,
    mPostsList: ArrayList<Posts>
) : RecyclerView.Adapter<ViewHolder>() {

    private var context: Context = mContext
    var postsList: ArrayList<Posts> = mPostsList
    private val itemConst = 0
    private val loadingConst = 1
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var viewHolder: ViewHolder? = null
        val inflater: LayoutInflater = LayoutInflater.from(context)

        when (viewType) {
            itemConst -> {
                val view1: View = inflater.inflate(R.layout.recycl_item_layout, parent, false)
                viewHolder = PostsListVH(view1)
            }
            loadingConst -> {
                val view2: View =
                    inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = PostsListVH(view2)
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int =
        postsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            itemConst -> {
                val postHolder: PostsListVH = holder as PostsListVH
                postHolder.itemView.tag = position
                postHolder.bindItems()
            }
            loadingConst -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == postsList.size - 1 && isLoadingAdded) loadingConst else itemConst
    }

    private fun add(listItem: Posts) {
        postsList.add(listItem)
        notifyItemInserted(postsList.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Posts())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = postsList.size - 1
        val post: Posts? = getItem(position)
        if (post != null) {
            postsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): Posts? {
        return postsList[position]
    }


    inner class PostsListVH(itemView: View) : ViewHolder(itemView) {

        fun bindItems() {
            val position = adapterPosition
            val postsItems = postsList[position]
            itemView.text_title.text = postsItems.title
            itemView.text_createdAt.text = postsItems.created_at
        }

    }
}
