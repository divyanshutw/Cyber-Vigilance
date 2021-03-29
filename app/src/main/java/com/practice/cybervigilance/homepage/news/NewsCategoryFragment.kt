package com.practice.cybervigilance.homepage.news

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.practice.cybervigilance.AppNetworkStatus
import com.practice.cybervigilance.R
import com.practice.cybervigilance.databinding.FragmentNewsCategoryBinding


class NewsCategoryFragment : Fragment(),NewsAdapter.OnCompleteClickListener {

    private lateinit var recyclerAdapter: NewsAdapter
    private lateinit var binding: FragmentNewsCategoryBinding
    private lateinit var viewModel:NewsViewModel

    private lateinit var category:String

    private lateinit var firestore:FirebaseFirestore

    private var page=0
    private var newsList=ArrayList<News?>()
    private var favList=ArrayList<String?>()

    private var lastVisibleDocument:DocumentSnapshot?=null

    private lateinit var preferences:SharedPreferences
    private lateinit var uid:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_news_category,container,false)

        viewModel=ViewModelProvider(this).get(NewsViewModel::class.java)

        preferences=requireActivity().getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)
        uid=preferences.getString("uid","")!!

        category=requireArguments().getString("category","")

        firestore= FirebaseFirestore.getInstance()

        recyclerAdapter = NewsAdapter(ArrayList<News?>(), favList,this, requireActivity())
        binding.recyclerView.adapter = recyclerAdapter


        initScrollListener()

        getFavList()

        return binding.root;
    }

    private fun getFavList() {
        firestore.collection("Users").document(uid).get()
            .addOnSuccessListener {
                if(it.get("liked")!=null)
                favList=it.get("liked") as ArrayList<String?>
                loadData()
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(),getString(R.string.unable_to_load), Toast.LENGTH_LONG).show()
                viewModel.isLoadingDialogVisible.value=false
            }
    }

    private fun loadData() {
        if(AppNetworkStatus.getInstance(requireContext()).isOnline)
        {
            viewModel.isLoadingDialogVisible.value=true
            showLoadingDialog()
            firestore.collection("Data").document("News").collection(category)
                .orderBy("datePosted", Query.Direction.DESCENDING).limit(8).get()
                .addOnSuccessListener {

                    for(snap:DocumentSnapshot in it.documents)
                    {
                        Log.d("div","NewsCategoryFragment L67 $snap")
                        newsList.add(News(snap.getString("category"),
                            snap.getString("type"),
                            snap.getString("imageUrl"),
                            snap.getString("title"),
                            snap.getString("description"),
                            snap.getString("videoUrl"),
                            snap.getString("contributedBy"),
                            snap.getLong("datePosted"),
                            snap.getLong("shares"),
                            snap.getLong("likes"),
                            snap.id
                        ))
                    }
//                    newsList.addAll(newsList)
//                    newsList.addAll(newsList)
//                    newsList.addAll(newsList)
                    if(newsList.size>0)
                        lastVisibleDocument= it.documents[it.size()-1]
                    if(newsList.isNotEmpty()) {
                        recyclerAdapter.allNewsList = newsList
                        recyclerAdapter.notifyDataSetChanged()
                    }
                    viewModel.isLoadingDialogVisible.value=false
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(),getString(R.string.unable_to_load), Toast.LENGTH_LONG).show()
                    viewModel.isLoadingDialogVisible.value=false
                }
        }
        else {
            val snackbar = Snackbar.make(binding.layout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }

    private fun initScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!viewModel.isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() ==newsList.size - 1) {
                        //bottom of list!
                        if (AppNetworkStatus.getInstance(requireContext()).isOnline) {
                            viewModel.isLoading = true
                            loadMore()
                        } else {
                            //showInternetNotConnectedDialog()
                            val snackbar = Snackbar.make(binding.layout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                            snackbar.show()
                        }

                    }
                }
            }
        })
    }

    private fun loadMore() {
        viewModel.isLoadingDialogVisible.value=true
        showLoadingDialog()
        //newsList.add(null)
        //newsList.size.minus(1).let { recyclerAdapter.notifyItemInserted(it) }
        firestore.collection("Data").document("News").collection(category)
            .orderBy("datePosted", Query.Direction.DESCENDING).startAfter(lastVisibleDocument).limit(8).get()
            .addOnSuccessListener {

                for(snap:QueryDocumentSnapshot in it)
                {
                    Log.d("div","NewsCategoryFragment L67 $snap")
                    newsList.add(News(snap.getString("category"),
                        snap.getString("type"),
                        snap.getString("imageUrl"),
                        snap.getString("title"),
                        snap.getString("description"),
                        snap.getString("videoUrl"),
                        snap.getString("contributedBy"),
                        snap.getLong("datePosted"),
                        snap.getLong("shares"),
                        snap.getLong("likes"),
                        snap.id
                    ))
                }

//                Handler().postDelayed(Runnable {
//                    //_blogsList.value?.size?.minus(1)?.let { _blogsList.value?.removeAt(it) }
//                    newsList.removeAt(newsList.size-1)
//                    recyclerAdapter.notifyItemRemoved(newsList.size-1)
//
//                    recyclerAdapter.notifyDataSetChanged()
//                    viewModel.isLoading = false
//                }, 2000)

                if(newsList.isNotEmpty()) {
                    recyclerAdapter.allNewsList = newsList
                    recyclerAdapter.notifyDataSetChanged()
                }
                lastVisibleDocument= it.documents[it.size()-1]
                viewModel.isLoadingDialogVisible.value=false
                viewModel.isLoading=false
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(),getString(R.string.unable_to_load), Toast.LENGTH_LONG).show()
                viewModel.isLoadingDialogVisible.value=false
                viewModel.isLoading=false
            }
    }

    private fun showLoadingDialog() {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        viewModel.isLoadingDialogVisible.observe(viewLifecycleOwner, Observer {
            if(!it)
            {
                dialog.dismiss()
            }
        })
        val handler= Handler()
        handler.postDelayed(Runnable{
            dialog.dismiss()
        },R.string.loadingDuarationInMillis.toLong())

    }

    override fun onClickNews(position: Int) {
        if(newsList[position]!=null && newsList[position]!!.type=="video")
        {
            Log.d("div","NewsCategoryFragment L237 ${newsList[position]!!.videoUrl}")
            val intent=Intent(requireActivity(),FullNewsVideoActivity::class.java)
            intent.putExtra("videoUrl",newsList[position]!!.videoUrl)
            intent.putExtra("title",newsList[position]!!.title)
            startActivity(intent)
        }
        else if(newsList[position]!=null)
        {
            val intent=Intent(requireActivity(),FullNewsArticleActivity::class.java)
            intent.putExtra("imageUrl",newsList[position]!!.imageUrl)
            intent.putExtra("title",newsList[position]!!.title)
            intent.putExtra("description",newsList[position]!!.description)
            newsList[position]!!.datePosted?.let { intent.putExtra("datePosted", it) }
            intent.putExtra("contributedBy",newsList[position]!!.contributedBy)
            startActivity(intent)
        }
    }

    override fun onClickLike(adapterPosition: Int) {
        if (newsList[adapterPosition] != null) {
            if (favList.contains(newsList[adapterPosition]!!.id))
                favList.remove(newsList[adapterPosition]!!.id);
            else
                favList.add(newsList[adapterPosition]!!.id)
            firestore.collection("Users").document(uid).update("liked",favList)
            recyclerAdapter.favList=favList
            //recyclerAdapter.notifyDataSetChanged()
        }
        Log.d("div","NewsCategoryFragment L263 ${newsList[adapterPosition]}")
    }

    override fun onClickShare(adapterPosition: Int) {
        val intent= Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Cyber Vigilance");
        intent.putExtra(
            Intent.EXTRA_TEXT, "${newsList[adapterPosition]?.title}.\nDownload the Cyber Vigilance app now")
        startActivity(Intent.createChooser(intent, "Share via"));
    }


}