package com.example.rehberim


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class RecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val name = findViewById<TextView>(R.id.nameRecord)
        val surname = findViewById<TextView>(R.id.SurnameRecord)
        val number = findViewById<TextView>(R.id.numberRecord)


        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Create Contact"

        }


        findViewById<Button>(R.id.buttonRecord).setOnClickListener {
            val intent = Intent()

            intent.putExtra("kullanici_adi", name.text.toString())

            intent.putExtra("kullanici_soyadi", surname.text.toString())

            intent.putExtra("kullanici_telefon", number.text.toString())


            setResult(1, intent)

            onBackPressed()

        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true


    }

}