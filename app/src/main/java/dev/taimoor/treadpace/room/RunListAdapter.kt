package dev.taimoor.treadpace.room

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import dev.taimoor.treadpace.homeFragment.HomeFragmentDirections
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.Util
import dev.taimoor.treadpace.databinding.RecyclerviewItemBinding
import dev.taimoor.treadpace.databinding.RunLayoutBinding
import dev.taimoor.treadpace.settings.UnitSetting
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
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


        holder.itemView.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToPostRunFragment(current)
                .setSavingRun(false)

            it.findNavController().navigate(action)
        }


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
            binding.dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
            binding.decimalFormatter = DecimalFormat("0.00")

            val pref = PreferenceManager.getDefaultSharedPreferences(this.itemView.context).getString("units",  "mi")
            val unitSetting : UnitSetting = UnitSetting.valueOf(pref as String)
            binding.unit = unitSetting
        }
    }
}