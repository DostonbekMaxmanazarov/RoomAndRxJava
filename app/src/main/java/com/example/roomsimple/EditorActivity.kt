package com.example.roomsimple

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.example.roomsimple.source.room.AppDatabase
import com.example.roomsimple.source.room.entity.StudentData
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity() {

    private var isSaveEnable = false
    private var isUpdate = false
    private var studentId: Long = -1

    private var colorTrue: Int? = null
    private var colorFalse: Int? = null

    private var cd = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true);
        }

        title = ""
        colorFalse = resources.getColor(R.color.colorFalse)
        colorTrue = resources.getColor(R.color.colorTrue)

        setUpValidate()
        loadFromintent()
    }

    private fun loadFromintent() {
        studentId = intent.getLongExtra(KEY_STUDENT_ID, -1)
        isUpdate = studentId > 0
        if (isUpdate) {
            AppDatabase.getDatabase(this)
                .studentDao()
                .loadStudentById(studentId)
                .observe(this, Observer {
                    it?.apply {
                        et_name.setText(name)
                        et_age.setText("$age")
                        et_email.setText("$email")
                    }
                })
        }
    }


    private fun setUpValidate() {
        val d = Observable.combineLatest(
            et_name.textChanges().map { it.isNotEmpty() },
            et_email.textChanges().map { it.isNotEmpty() },
            et_age.textChanges().map { it.isNotEmpty() },
            Function3 { name, email, age ->
                name && email && age

            }
        ).doOnNext {
            isSaveEnable = it
        }.subscribe({ invalidateOptionsMenu() }, { it.message })

        cd.add(d)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return if (item.itemId == R.id.save) {
            if (isUpdate) {
                updateStudent()
            } else {
                insertNewItem()
            }
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun insertNewItem() {
        val name = et_name.text.toString()
        val age = et_age.text.toString().toInt()
        val email = et_email.text.toString()

        val student = StudentData(name, age, email)

        Single.fromCallable {
            AppDatabase.getDatabase(this)
                .studentDao()
                .insert(student)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { finish() })
            .let { cd.add(it) }
    }

    private fun updateStudent() {

        val name = et_name.text.toString()
        val age = et_age.text.toString().toInt()
        val email = et_email.text.toString()

        val student = StudentData(name, age, email,studentId)
        Single.fromCallable {
            AppDatabase.getDatabase(this)
                .studentDao()
                .update(student)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer { finish() })
            .let { cd.add(it) }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menu = menu?.getItem(0)
        menu?.isEnabled = isSaveEnable
        val color: Int? = if (isSaveEnable) {
            colorTrue
        } else {
            colorFalse
        }
        menu?.icon?.setTint(color!!)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        cd.clear()
    }

}
