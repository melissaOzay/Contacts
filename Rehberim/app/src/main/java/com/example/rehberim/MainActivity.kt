package com.example.rehberim


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.service.autofill.Dataset
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.Function as Function1001


class MainActivity : AppCompatActivity() {

    private val gson = Gson()
    private var list: ArrayList<DataClass> = arrayListOf()

    val adapter = RehberAdapter(list)
    lateinit var recyclerView: RecyclerView
    lateinit var textm: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textm = findViewById<TextView>(R.id.textView2)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewText)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter


        if (list.isNullOrEmpty() && fetchArrayList().isNullOrEmpty()) {
            textm.text = "Please add someone"

        } else {
            textm.text = " "

        }


        if (fetchArrayList().isNotEmpty()) {
            adapter.setData(fetchArrayList())
            adapter.notifyDataSetChanged()
        }





        findViewById<FloatingActionButton>(R.id.plusButton).setOnClickListener {


            val intent = Intent(this, RecordActivity::class.java)
            startForResult.launch(intent)


        }


        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val alert = AlertDialog.Builder(this@MainActivity)
                    alert.setTitle("Delete Contacts")
                    alert.setMessage("Are you sure")
                    alert.setPositiveButton("yes") { dialog, id ->
                        recyclerView.adapter!!.notifyItemRemoved(position)
                        if (list.isNotEmpty()) {
                            list.removeAt(position)
                        }

                        var newList = fetchArrayList()

                        newList.removeAt(position)
                        val prefs: SharedPreferences =
                            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        prefs.edit(commit = true) {
                            clear()
                            adapter?.notifyDataSetChanged()
                        }
                        for (dataclass in newList) {
                            saveObjectToArrayList(dataclass)

                            adapter?.notifyDataSetChanged()
                        }

                        Toast.makeText(this@MainActivity, "\n" +
                                "Data deleted", Toast.LENGTH_LONG)
                            .show()
                        adapter.setData(newList)
                        adapter?.notifyDataSetChanged()
                        if (newList.isEmpty()) {
                            textm.text = "Please add someone"
                        }
                    }


                    alert.setNegativeButton("no") { dialog, id ->
                        Toast.makeText(
                            this@MainActivity,
                            "Data not deleted",
                            Toast.LENGTH_LONG
                        ).show()
                        recyclerView.adapter!!.notifyItemRangeChanged(0, list.size)
                        dialog.cancel()

                    }
                    val build = alert.create()
                    build.show()

                }


            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }

    var startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 1) {
                val data: Intent? = result.data
                val kullaniciAdi = data?.getStringExtra("kullanici_adi")
                val soyad = data?.getStringExtra("kullanici_soyadi")
                val telefon = data?.getStringExtra("kullanici_telefon")
                var RehberList = DataClass(kullaniciAdi, soyad, telefon)

                if (kullaniciAdi.equals("") || soyad.equals("") || telefon.equals("")) {
                    val err = "Data cannot be entered blank."
                    Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, RecordActivity::class.java)
                    startActivity(intent)
                } else {
                    list.add(RehberList)
                    saveObjectToArrayList(RehberList)
                    adapter.notifyDataSetChanged()

                    if ((adapter.setData(fetchArrayList())) == null) {
                        textm.text = "Please add someone"
                    } else {
                        textm.text = " "
                    }
                }


            }

        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        var menuItem = menu!!.findItem(R.id.action_search)
        var searchView = menuItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        //search kısmını toolbarda uzun olarak gösteriyor
        searchView.imeOptions = (EditorInfo.IME_ACTION_DONE)
        //klavyede ki search kısmını iptal ediyor ve tik iconu nu koyuyor.

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var searchList = java.util.ArrayList<DataClass>()
                //search kısmına yazılan ismi al
                var newList = fetchArrayList()
                for (dataclass in newList) {
                    if (dataclass.name!!.contains(newText!!)) {
                        //contains:içeriyor mu anlamında
                        searchList.add(dataclass)
                    }
                }
                adapter.setData(searchList)
                adapter.notifyDataSetChanged()
                //RecordAdapter da ki setData ya gidiyor.


                return false
            }

        })
        return true


    }

    fun saveObjectToArrayList(yourObject: DataClass) {
        var prefs: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            ?: error("err")
        val bookmarks = fetchArrayList()
        bookmarks.add(0, yourObject)
        val prefsEditor = prefs.edit()
        val json = gson.toJson(bookmarks)
        prefsEditor.putString("your_key", json)
        prefsEditor.apply()
        prefsEditor.remove(bookmarks.toString())
        prefsEditor.clear()
        adapter.setData(bookmarks)
        adapter.notifyDataSetChanged()
    }

    fun fetchArrayList(): ArrayList<DataClass> {
        var prefs: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            ?: error("err")
        val yourArrayList: ArrayList<DataClass>
        val json = prefs.getString("your_key", "")
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit().remove("your_key")
            .apply()

        yourArrayList = when {
            json.isNullOrEmpty() -> ArrayList()
            else -> gson.fromJson(json, object : TypeToken<List<DataClass>>() {}.type)
        }

        return yourArrayList
    }


}









