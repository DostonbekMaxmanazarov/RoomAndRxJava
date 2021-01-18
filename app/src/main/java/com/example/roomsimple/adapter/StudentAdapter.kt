package com.example.roomsimple.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.roomsimple.R
import com.example.roomsimple.source.room.entity.StudentData
import com.example.rxroom.listener.OnClickStudentListener
import kotlinx.android.synthetic.main.item_student.view.*

class StudentAdapter(context: Context, private val lists: MutableList<StudentData>) :
    RecyclerView.Adapter<StudentAdapter.VH>() {

    private val inflater by lazy { LayoutInflater.from(context) }
    private val onItemClick: OnClickStudentListener = context as OnClickStudentListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = inflater.inflate(R.layout.item_student, parent, false)
        return VH(v, onItemClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val position = lists[position]
        holder.onBind(position)
    }

    override fun getItemCount() = lists.size

    fun addStudent(student: StudentData) {
        lists.add(student)
        notifyDataSetChanged()
    }

    fun updateStudents(students: MutableList<StudentData>) {
        lists.clear()
        lists.addAll(students)
        notifyDataSetChanged()
    }

    fun getItemByPosition(pos: Int): StudentData {
        return lists[pos]
    }

    fun removeItByPosition(pos: Int) {
        lists.removeAt(pos)
        notifyDataSetChanged()
    }

    class VH(
        item: View, onItemClick: OnClickStudentListener
    ) : RecyclerView.ViewHolder(item) {

        private var lastStudent: StudentData? = null

        init {
            itemView.setOnClickListener {
                onItemClick.onClickStudentListener(lastStudent!!)
            }
        }

        fun onBind(student: StudentData) {
            lastStudent = student
            itemView.tv_name.text = student.name
            itemView.tv_age.text = student.age.toString()
            itemView.tv_email.text = student.email
        }

    }
}