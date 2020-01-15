package com.assignment.assesments.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.assignment.assesments.R
import com.assignment.assesments.adapter.PostsListAdapter
import com.assignment.assesments.listeners.PaginationScrollListener
import com.assignment.assesments.models.Posts
import com.assignment.assesments.network.Service.Companion.BASE_URL
import com.assignment.assesments.network.Service.Companion.GET_STORY_BY_DATE
import com.assignment.assesments.views.ProgressDialog
import kotlinx.android.synthetic.main.activity_posts_list.*
import org.json.JSONArray
import org.json.JSONObject


class PostsListActivity : AppCompatActivity() {

    private val TAG = PostsListActivity::class.java.name
    private var recyclerAdapter: PostsListAdapter? = null
    private lateinit var progressDialog: ProgressDialog
    var isLoading = false
    var isLastPage = false
    var totalPages = 0
    private var totalRecords=0
    private val pageStart = 1
    var currentPage: Int = pageStart
    private var postsList: ArrayList<Posts> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_list)
        progressDialog = ProgressDialog(this)

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_posts.layoutManager = layoutManager
        rv_posts.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        rv_posts.adapter = recyclerAdapter

        val pageListener = object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                getRecordsFromServer(currentPage)
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        }
        rv_posts.addOnScrollListener(pageListener)
        getRecordsFromServer(pageStart)
    }

    fun getRecordsFromServer(page: Int) {
        if(page==pageStart){
            progressDialog.showDialog()
        }
        val queue = Volley.newRequestQueue(this)
        val requestUrl = BASE_URL + GET_STORY_BY_DATE + page

        val jsonArrayRequest = JsonObjectRequest(Request.Method.GET, requestUrl, null,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "Api Success $response");parseNetworkResponse(response, page)
            },
            Response.ErrorListener { error ->
                Log.d(TAG, "Api Failure $error")
                progressDialog.hideDialog()
                Toast.makeText(this,"Server Error , Please try again later",Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonArrayRequest)
    }

    private fun parseNetworkResponse(response: JSONObject, page: Int) {
        if(page==pageStart){
            progressDialog.hideDialog()
        }
        totalPages = response.getInt("nbPages")
        totalRecords += response.getInt("hitsPerPage")
        val recordArray: JSONArray = response.getJSONArray("hits")
        val recordsList: ArrayList<Posts> = ArrayList()
        for (i in 0 until recordArray.length()) {
            val item = recordArray.getJSONObject((i))
            val modelObj = Posts(
                item.getString("created_at"),
                item.getString("title")
            )
            recordsList.add(modelObj)
        }
        setAdapter(recordsList, page)
        setNavBarTitle(totalRecords)
    }

    @SuppressLint("ShowToast")
    fun setAdapter(list: ArrayList<Posts>, page: Int) {

        if (page == pageStart) {
            postsList.clear()
            postsList.addAll(list)
            if (currentPage <= totalPages) recyclerAdapter?.addLoadingFooter() else isLastPage =
                true
        } else {
            recyclerAdapter?.removeLoadingFooter()
            isLoading = false
            postsList.addAll(postsList.size, list)
            if (currentPage != totalPages) recyclerAdapter?.addLoadingFooter() else isLastPage =
                true
        }
        if (page != pageStart && recyclerAdapter != null) {
            recyclerAdapter!!.notifyDataSetChanged()
        } else {
            recyclerAdapter = PostsListAdapter(this, postsList)
            rv_posts.adapter = recyclerAdapter
        }

    }

     private fun setNavBarTitle(total:Int){
         val actionBar = supportActionBar
         actionBar!!.title = "Total Posts: $total"
     }
}
