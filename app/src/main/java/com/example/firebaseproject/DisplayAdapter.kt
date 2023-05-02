package com.example.firebaseproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DisplayAdapter(var studentList : ArrayList<StudentModelClass>): RecyclerView.Adapter<DisplayAdapter.MyViewHolder>() {
    class MyViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {
        var txtname : TextView = itemview.findViewById(R.id.txtname)
        var txtcourse : TextView = itemview.findViewById(R.id.txtcourse)
        var txtaddress : TextView = itemview.findViewById(R.id.txtaddress)
        var txtfees : TextView = itemview.findViewById(R.id.txtfees)
        var txtid : TextView = itemview.findViewById(R.id.txtid)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.displayrecorditemfile,parent,false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
     return studentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.txtname.setText(studentList[position].name)
            holder.txtcourse.setText(studentList[position].course)
            holder.txtaddress.setText(studentList[position].address)
            holder.txtfees.setText(studentList[position].fees)
            holder.txtid.setText(studentList[position].id)
    }
}
