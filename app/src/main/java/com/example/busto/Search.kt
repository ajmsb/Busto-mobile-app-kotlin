package com.example.busto

import android.content.Intent
import android.database.DataSetObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Search : AppCompatActivity() {

    private lateinit var items: ArrayList<Item>
    private lateinit var database: DatabaseReference
    private lateinit var rcList: RecyclerView
    private lateinit var adapter: SearchAdapter
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        database = Firebase.database.reference
        rcList  = findViewById(R.id.searchList)
        items = arrayListOf()
        adapter = SearchAdapter(items)
        rcList.adapter = adapter
        rcList.layoutManager = LinearLayoutManager(this)

        if (intent.hasExtra("query")) {
            searchQuery = intent.getStringExtra("query").toString()
            // adapter.filter.filter(searchQuery)
        }

       /* adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onChanged(){
                if ( searchQuery.length > 0 ){
                    adapter.filter.filter(searchQuery)
                }
            }
        })
        */

        database.child("lines").child("0").get().addOnSuccessListener {
            if (it.value != null) {
                val itemsFromDb = it.value as HashMap<String, Any>
                items.clear()
                val stops= itemsFromDb.getValue("stops") as ArrayList<String>
                for (i in 0..stops.size-1) {
                    var name = stops[i]
                    val item = Item(name)

                    if ( name .toLowerCase(Locale.ROOT).contains(intent.getStringExtra("query").toString())){
                        items.add(item)
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar, menu)
        var searchItem = menu?.findItem(R.id.search)
        var searchView = searchItem?.actionView as SearchView

        //getting rid of the magnifying glass
        val magId: Int = resources.getIdentifier("android:id/search_mag_icon", null, null)
        val magImage: ImageView = searchView!!.findViewById(magId)
        val searchViewGroup: ViewGroup = magImage.getParent() as ViewGroup
        searchViewGroup.removeView(magImage)

        searchView.queryHint = "Search..."
        searchView.isIconifiedByDefault = false

        val queryTextListener = object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)

        val actionExpandableListAdapter = object: MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                adapter.filter.filter("")
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }
        }
        searchItem.setOnActionExpandListener(actionExpandableListAdapter)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId)  {
        R.id.settings -> {
            val intent = Intent (this, userSettings::class.java)
            startActivity(intent)
            true
        }
        R.id.map -> {
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.login_icon -> {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.bus_stops -> {
            val intent = Intent (this, StopsList::class.java)
            startActivity(intent)
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }
}