package com.example.roomsimple

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomsimple.adapter.StudentAdapter
import com.example.roomsimple.source.room.AppDatabase
import com.example.roomsimple.source.room.entity.StudentData
import com.example.rxroom.listener.OnClickStudentListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

const val KEY_STUDENT_ID = "KEY_STUDENT_ID"

class MainActivity : AppCompatActivity(), OnClickStudentListener {

    private val adapter by lazy { StudentAdapter(this, mutableListOf()) }
    private var cd = CompositeDisposable()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val color = ContextCompat.getColor(this, R.color.statusColor)
        window.statusBarColor = color
        fab.setOnClickListener {
            startEditorActivty()
        }

        setUpRv()
        loadInitialStudents()

    }

    private fun startEditorActivty(id: Long? = null) {
        Intent(this, EditorActivity::class.java)
            .apply {
                putExtra(KEY_STUDENT_ID, id)
                startActivity(this)
            }
    }

    private fun loadInitialStudents() {
        AppDatabase
            .getDatabase(this)
            .studentDao()
            .loadStudents()
            .observe(this, Observer {
                adapter.updateStudents(it)
            })


    }

    private fun setUpRv() {
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        val swipeListener = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val pos = viewHolder.adapterPosition
                val it = adapter.getItemByPosition(pos)

                Single.fromCallable {
                    AppDatabase.getDatabase(this@MainActivity)
                        .studentDao().deleteStudent(it)
                }.map { it > 0 }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer {
                        if (it) adapter.removeItByPosition(pos)
                    })
                    .let {
                        cd.add(it)
                    }
            }
        }
        val touchHelper = ItemTouchHelper(swipeListener)
        touchHelper.attachToRecyclerView(recyclerview)
    }

    override fun onClickStudentListener(student: StudentData) = startEditorActivty(student.id)
}