package dev.taimoor.treadpace.room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.taimoor.treadpace.R

//class RunListAdapter internal constructor(context: Context) :
//    RecyclerView.Adapter<RunListAdapter.RunViewHolder>(){
//
//    private val inflater: LayoutInflater = LayoutInflater.from(context)
//    private var runs = emptyList<RunEntity>() // Cached copy of words
//
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
//        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
//        return RunViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
//        val current = runs[position]
//        // set text views here
//        holder.text = current.word
//    }
//
//    internal fun setRuns(runs: List<RunEntity>) {
//        this.runs = runs
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount() = runs.size
//
//    inner class RunViewHolder(itemView: View): RecyclerView.ViewHolder{
//        lateinit var binding: RunLayoutBinding
//    }
//}