package com.example.tugas_roommm.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.Tugas_Roomm.database.Notes
import com.example.Tugas_Roomm.database.NotesDao
import com.example.Tugas_Roomm.database.NotesRoomDatabase
import com.example.tugas_roommm.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mNotesDao: NotesDao
    private lateinit var executorService: ExecutorService
    private val REQUEST_CODE_SECOND_ACTIVITY = 1

    companion object {
        const val REQUEST_CODE_UPDATE_ACTIVITY = 2
    }


    private val listNotes = mutableListOf<Notes>() // Tambahkan list untuk menyimpan data yang akan ditampilkan
    private lateinit var rvAdapater: RvAdapater // Deklarasikan adapter di sini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        executorService = Executors.newSingleThreadExecutor()
        val db = NotesRoomDatabase.getDatabase(this)
        mNotesDao = db!!.notesDao()!!
        setContentView(binding.root)

        rvAdapater = RvAdapater(listNotes,
            onItemClick = { item ->
                executorService.execute {
                    // Inside onItemClick in your notesAdapter
                    val position = listNotes.indexOf(item)
                    val selectedNote = listNotes[position] // Assuming position is the clicked item position
                    val intent = Intent(this@MainActivity, SecondActivity::class.java)
                    intent.putExtra("SELECTED_NOTE", selectedNote)
                    startActivityForResult(intent, REQUEST_CODE_UPDATE_ACTIVITY)
                }
            },
            onItemLongClick = { item ->
                delete(item)
            }
        )

        binding.recyclerView.adapter = rvAdapater // Set adapter ke RecyclerView di sini

        binding.btnnn.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY) // Ubah ke startActivityForResult
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SECOND_ACTIVITY && resultCode == Activity.RESULT_OK) {

        }
    }

    private fun getAll() {
        mNotesDao.getAllNotes.removeObservers(this) // Hapus observer sebelum menambahkan yang baru
        mNotesDao.getAllNotes.observe(this) { notes ->
            listNotes.clear()
            listNotes.addAll(notes)
            rvAdapater.notifyDataSetChanged()
        }
    }

    private fun delete(notes: Notes){
        executorService.execute { mNotesDao.deleteNotes(notes) }
    }

    override fun onResume(){
        super.onResume()
        getAll()
    }
}