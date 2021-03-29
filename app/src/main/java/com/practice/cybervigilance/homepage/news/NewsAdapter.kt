package com.practice.cybervigilance.homepage.news

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.practice.cybervigilance.R
import kotlinx.android.synthetic.main.news_item_view.view.*


class NewsAdapter(
    var allNewsList: ArrayList<News?>,
    var favList: ArrayList<String?>,
    private val onCompleteClickListener: OnCompleteClickListener,
    var context: FragmentActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    init {
        Log.d("div", "NewsAdapter L32 $context")
    }

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==VIEW_TYPE_ITEM)
        {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.news_item_view, parent, false)
            return NewsAdapter.ItemViewHolder(view,favList,allNewsList, onCompleteClickListener, context)
        }
        else
        {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.progressbar_loading, parent, false)
            return NewsAdapter.LoadingViewHolder(view, onCompleteClickListener)
        }
    }

    override fun getItemCount(): Int {
        Log.d("div", "NewsAdapter L25 " + allNewsList.size)
        return allNewsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ItemViewHolder)
        {
            val card = allNewsList[position]
            if (card != null) {
                holder.bind(card)
            }
        }
        else if(holder is LoadingViewHolder)
        {
            showLoadingView(holder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (allNewsList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(
        itemView: View,
        val favList: ArrayList<String?>,
        allNewsList: ArrayList<News?>,
        onCompleteClickListener: OnCompleteClickListener,
        var context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onCompleteClickListener.onClickNews(adapterPosition)
                Log.d("div", "NewsAdapter L86 ${context.contentResolver}")
            }
            itemView.findViewById<ImageButton>(R.id.imageButton_like).setOnClickListener {

                Log.d("div","NewsAdapter $absoluteAdapterPosition")
                onCompleteClickListener.onClickLike(absoluteAdapterPosition)
                if(allNewsList[absoluteAdapterPosition]!=null && favList.contains(allNewsList[absoluteAdapterPosition]!!.id))
                    itemView.imageButton_like.setImageResource(R.drawable.ic_baseline_star_outline_24)
                else
                    itemView.imageButton_like.setImageResource(R.drawable.ic_baseline_star_24)
            }
            itemView.findViewById<ImageButton>(R.id.imageButton_share).setOnClickListener {
                onCompleteClickListener.onClickShare(absoluteAdapterPosition)
            }
        }

        fun bind(news:News) {
            itemView.textView.text = news.title.toString()
            bindImage(itemView.imageView, news.imageUrl)
            if(favList.contains(news.id))
                itemView.imageButton_like.setImageResource(R.drawable.ic_baseline_star_24)
        }
        fun bindImage(imgView: ImageView, imgUrl: String?) {
            imgUrl?.let {

                val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
                Glide.with(imgView.context)
                    .load(imgUri)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_baseline_play_circle_24)
                    )
                    .into(imgView)

            }
        }
    }

    private class LoadingViewHolder(
        itemView: View,
        onCompleteClickListener: OnCompleteClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    interface OnCompleteClickListener {
        fun onClickNews(position: Int)
        fun onClickLike(adapterPosition: Int)
        fun onClickShare(adapterPosition: Int)
    }
}

