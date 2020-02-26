package dev.taimoor.treadpace.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import dev.taimoor.treadpace.R
import dev.taimoor.treadpace.databinding.SettingsItemBinding

class SettingsListAdapter(val context: Context, val dataSource: Array<Setting>)
    : BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: SettingsItemBinding
        if(convertView == null){
            binding = SettingsItemBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        }
        else{
            binding = convertView.tag as SettingsItemBinding
        }

        binding.setting = getItem(position) as Setting
        return binding.root
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }



}