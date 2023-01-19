package com.example.busto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StopsList : AppCompatActivity() {

    private lateinit var items: ArrayList<String>
    private lateinit var database: DatabaseReference
    private lateinit var rcList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_list)
        database = Firebase.database.reference
        rcList  = findViewById(R.id.stoplist)
        items = arrayListOf()

        database.child("lines").child("0").get().addOnSuccessListener {
            if (it.value != null) {
                val itemsFromDb = it.value as HashMap<String, Any>
                items.clear()
                val stops= itemsFromDb.getValue("stops") as ArrayList<String>
                for (i in 0..stops.size-1) {
                    var foo = stops[i]
                    items.add(foo)

                    rcList.adapter?.notifyDataSetChanged()
                }
            }
            rcList.layoutManager = LinearLayoutManager(this)
            rcList.adapter = SampleAdapter(items)
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
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent (this@StopsList, Search::class.java).apply {
                    putExtra("query", query)
                }
                startActivity(intent)
                return true
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)

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
        R.id.Logout -> {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            true
        }else -> {
            super.onOptionsItemSelected(item)
        }
    }
}