package dev.taimoor.treadpace.room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.databinding.RecyclerviewItemBinding
import dev.taimoor.treadpace.databinding.RunLayoutBinding
import java.time.format.DateTimeFormatter

class RunListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<RunListAdapter.RunViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var runs = emptyList<RunEntity>() // Cached copy of words



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val recyclerviewItemBinding = RecyclerviewItemBinding.inflate(inflater, parent, false)
        return RunViewHolder(recyclerviewItemBinding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val current = runs[position]
        holder.bind(current)
    }

    internal fun setRuns(runs: List<RunEntity>) {
        this.runs = runs
        notifyDataSetChanged()
    }

    override fun getItemCount() = runs.size

    inner class RunViewHolder(val binding: RecyclerviewItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(run: RunEntity){
            binding.run = run
            binding.formatter = DateTimeFormatter.ISO_LOCAL_DATE
        }
    }
}