package com.voice.voicerecorder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.voice.voicerecorder.R
import com.voice.voicerecorder.data.Record
import com.voice.voicerecorder.databinding.VoiceItemBinding
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class VoiceRecordAdapter :
    ListAdapter<Record, VoiceRecordAdapter.MyViewHolder>(VoiceRecorderDiffUtilCallBack()) {

    inner class MyViewHolder(private val voiceItem: VoiceItemBinding) :
        RecyclerView.ViewHolder(voiceItem.root) {

        fun bind(file: Record, position: Int) {


            voiceItem.itemName.text = file.title


            val format = SimpleDateFormat("yyy/MMM/dd", Locale.ENGLISH)
            val f = File(file.filePath)
            val date = format.format(Date(f.lastModified()))

            voiceItem.itemDate.text = date


            voiceItem.voiceItemCard.setOnClickListener {

                onVoiceItemClickListener?.let { click ->
                    click(file, position)
                }

            }


        }


    }

    class VoiceRecorderDiffUtilCallBack : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.voice_item, parent, false)
        val binding: VoiceItemBinding = VoiceItemBinding.bind(view)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private var onVoiceItemClickListener: ((Record, Int) -> Unit)? = null

    fun setOnVoiceItemClickListener(listener: (Record, Int) -> Unit) {
        onVoiceItemClickListener = listener
    }


}