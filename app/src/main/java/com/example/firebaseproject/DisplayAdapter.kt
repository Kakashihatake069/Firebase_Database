package com.example.firebaseproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DisplayAdapter(var context: Context,var onEdit : (StudentModelClass) -> Unit,var onDelete : (String) -> Unit): RecyclerView.Adapter<DisplayAdapter.MyViewHolder>() {
     var studentList = ArrayList<StudentModelClass>()


    class MyViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {
        var txtname : TextView = itemview.findViewById(R.id.txtname)
        var txtcourse : TextView = itemview.findViewById(R.id.txtcourse)
        var txtaddress : TextView = itemview.findViewById(R.id.txtaddress)
        var txtfees : TextView = itemview.findViewById(R.id.txtfees)
        var txtid : TextView = itemview.findViewById(R.id.txtid)
        var btnedit : AppCompatButton = itemview.findViewById(R.id.btnedit)
        var btndelete : AppCompatButton = itemview.findViewById(R.id.btndelete)
        var imgdisimage : ImageView = itemview.findViewById(R.id.imgdisimage)
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
            Glide.with(context).load(studentList[position].image).into(holder.imgdisimage)

        holder.btnedit.setOnClickListener {
            onEdit.invoke(studentList[position])
        }
        holder.btndelete.setOnClickListener {
            onDelete.invoke(studentList[position].id)
        }

    }
    fun updatelist(studentList : java.util.ArrayList<StudentModelClass>){
        this.studentList = ArrayList()
        this.studentList=studentList
        notifyDataSetChanged()
    }
}

