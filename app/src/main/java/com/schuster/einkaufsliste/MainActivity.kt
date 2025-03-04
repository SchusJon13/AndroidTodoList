package com.schuster.einkaufsliste

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.schuster.einkaufsliste.databinding.ActivityMainBinding
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import android.util.Log

public var ShoppingItem: Int = 0
public var ShoppingName: String = ""

class MainActivity : AppCompatActivity() {
    fun saveArrayList(context: Context, arrayList: ArrayList<String>, filename: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            ObjectOutputStream(it).writeObject(arrayList)
        }
    }

    // Loading the ArrayList
    fun loadArrayList(context: Context, filename: String): ArrayList<String>? {
        return try {
            context.openFileInput(filename).use {
                ObjectInputStream(it).readObject() as ArrayList<String>
            }
        } catch (e: Exception) {
            null // Handle exceptions appropriately
        }
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var lvTodoList: ListView
    private lateinit var fab: FloatingActionButton
    private lateinit var shoppingItems: ArrayList<String>
    private lateinit var itemAdapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lvTodoList = findViewById(R.id.lvTodoList)
        fab = findViewById(R.id.floatingActionButton)
        shoppingItems = ArrayList()

        try {
            shoppingItems = loadArrayList(this, "shoppingItems.txt") ?: ArrayList()
         } catch (e: Exception) {
             Log.e("MainActivity", "Error loading shopping items", e)
         }

        itemAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shoppingItems)
        lvTodoList.adapter = itemAdapter

        lvTodoList.onItemLongClickListener = OnItemLongClickListener { arg0, arg1, pos, id ->
            shoppingItems.removeAt(pos)
            itemAdapter.notifyDataSetChanged()
            Toast.makeText(applicationContext, "Element gelöscht", Toast.LENGTH_SHORT).show()
            //Delete Items in Group
            var NoNe = ArrayList<String>()
            saveArrayList(this, NoNe, "$pos.txt")
            true
        }
        lvTodoList.setOnItemClickListener { parent, view, position, id ->
            //Neue Activity öffnen
            val intent = Intent(this, Second::class.java)
            startActivity(intent)

            ShoppingItem = position
            ShoppingName = shoppingItems[position]
        }
        fab.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Hinzufügen")

            var input = EditText(this)
            input.hint = "Text eingeben"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                shoppingItems.add(input.text.toString())
            }

            builder.setNegativeButton("Abbrechen") { _, _ ->
                Toast.makeText(applicationContext, "Abgebrochen", Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }



    }
    override fun onPause() {
        super.onPause()
        saveArrayList(this, shoppingItems, "shoppingItems.txt")
    }
}