package com.example.myscheduler

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.text.format.DateFormat
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleEditActivity : AppCompatActivity() {
    private lateinit var realm: Realm //Realmクラスのプロパティの用意

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        realm = Realm.getDefaultInstance() //Realmインスタンスの取得

        val scheduleId = intent?.getLongExtra("schedule_id", -1L)//更新処理
        if (scheduleId != -1L) { //idが-1だったら新規登録
            val schedule = realm.where<Schedule>()
                .equalTo("id", scheduleId).findFirst()
            dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.date))
            titleEdit.setText(schedule?.title)
            detailEdit.setText(schedule?.detail)
            delete.visibility = View.VISIBLE
        } else {
            delete.visibility = View.INVISIBLE
        }


        delete.setOnClickListener { view: View ->
            realm.executeTransaction { db: Realm ->
                db.where<Schedule>().equalTo("id", scheduleId)
                    ?.findFirst()
                    ?.deleteFromRealm()
            }
            Snackbar.make(view, "削除しました", Snackbar.LENGTH_SHORT)
                .setAction("戻る") { finish() }
                .setActionTextColor(Color.YELLOW)
                .show()

            save.setOnClickListener { view: View ->
                when (scheduleId) {
                    -1L -> {
                        realm.executeTransaction { db: Realm ->
                            val maxId = db.where<Schedule>().max("id")
                            val nextId = (maxId?.toLong() ?: 0L) + 1
                            val schedule = db.createObject<Schedule>(nextId) //データを1行追加
                            val date =
                                dateEdit.text.toString().toDate("yyyy/MM/dd") //scheduleオブジェクトに値を設定
                            if (date != null) schedule.date = date
                            schedule.title = titleEdit.text.toString()
                            schedule.detail = detailEdit.text.toString()
                        }
                        Snackbar.make(view, "追加しました", Snackbar.LENGTH_SHORT)
                            .setAction("戻る") { finish() }
                            .setActionTextColor(Color.YELLOW)
                            .show()
                    }
                    else -> {
                        realm.executeTransaction { db: Realm ->
                            val schedule = db.where<Schedule>()
                                .equalTo("id", scheduleId).findFirst()
                            val date = dateEdit.text.toString()
                                .toDate("yyyy/MM/dd")
                            if (date != null) schedule?.date = date
                            schedule?.title = titleEdit.text.toString()
                            schedule?.detail = detailEdit.text.toString()

                        }
                        Snackbar.make(view, "更新しました", Snackbar.LENGTH_SHORT)
                            .setAction("戻る") { finish() }
                            .setActionTextColor(Color.YELLOW)
                            .show()
                    }
                }
            }
        }
    }

    override fun onDestroy() { //閉じる
        super.onDestroy()
        realm.close()
    }

    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        return try {  //文字列が意図しない形式の可能性
            SimpleDateFormat(pattern).parse(this)
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: ParseException) {
            return null
        }
    }
}



