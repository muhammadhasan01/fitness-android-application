package com.k310.fitness.ui.historyList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.R
import com.k310.fitness.db.training.Training

class HistoryAdapter(private val mHistory: List<Training>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val descriptionTextView: TextView = itemView.findViewById<TextView>(R.id.description)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val historyView = inflater.inflate(R.layout.item_history, parent, false)
        // Return a new holder instance
        return ViewHolder(historyView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val history: Training = mHistory.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.descriptionTextView
        textView.text = history.toString()
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mHistory.size
    }
}