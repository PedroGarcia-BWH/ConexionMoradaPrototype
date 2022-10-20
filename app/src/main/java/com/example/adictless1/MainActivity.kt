package com.example.adictless1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registrar=findViewById<Button>(R.id.register)
        registrar.setOnClickListener {
            val intento1 = Intent(this, RegisterActivity1::class.java)
            startActivity(intento1)
        }
    }
}