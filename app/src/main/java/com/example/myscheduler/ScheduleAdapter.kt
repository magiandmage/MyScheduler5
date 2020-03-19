package com.example.myscheduler

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class ScheduleAdapter (data: OrderedRealmCollection<Schedule>) : //総称型
    RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true){//自動更新

    private var listener: ((Long?)-> Unit)? = null

    fun setOnItemClickListener(listener:(Long?) -> Unit ){ //コールバック関数
        this.listener = listener //格納しておく
    }

    init {                      //イニシャライザ
        setHasStableIds(true)
    }

    class ViewHolder(cell: View) : RecyclerView.ViewHolder(cell) {
        val date: TextView = cell.findViewById(android.R.id.text1)
        val title: TextView = cell.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ScheduleAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_2,
                                    parent, false)
        return ViewHolder(view)
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ScheduleAdapter.ViewHolder, position: Int) { //データを取得し表示する
        val schedule: Schedule? = getItem(position)
        holder.date.text = DateFormat.format("yyyy/MM/dd", schedule?.date)
        holder.title.text = schedule?.title
        holder.itemView.setOnClickListener { //コールバック
            listener?.invoke(schedule?.id) //invokeメソッド
        }

        //To change body of created functions use File | Settings | File Templates.
    }
    override fun getItemId(position: Int): Long{
        return getItem(position)?.id ?: 0
    }

}