package com.example.busto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter (private val fullList: ArrayList<Item>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>(), Filterable {

    var filteredList = ArrayList<Item>()

    init {
        filteredList = fullList
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.busName)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.search_row, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = filteredList[position].name
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                var searchString = p0.toString()
                if(searchString.isEmpty()) {
                    filteredList = fullList
                } else {
                    val results = ArrayList<Item>()
                    for (row in fullList) {
                        if (row.name.toLowerCase(Locale.ROOT).contains(searchString)) {
                            results.add(row)
                        }
                    }
                    filteredList = results
                }
                var filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredList = p1?.values as ArrayList<Item>
                notifyDataSetChanged()
            }
        }
    }
}