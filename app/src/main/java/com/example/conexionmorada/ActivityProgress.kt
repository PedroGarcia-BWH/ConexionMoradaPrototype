package com.example.conexionmorada

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import android.R.id
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.example.conexionmorada.Controlador.ObtenerExperiencia
import com.example.conexionmorada.Controlador.Progress

import com.github.mikephil.charting.charts.BarChart
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_progress.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class ActivityProgress : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    companion object {
        private var TAG = "ProgressBar"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        auth = Firebase.auth
        val user = auth.currentUser

        val Btn_Redes = findViewById<Button>(R.id.RedesButton)
        val Btn_Apuestas = findViewById<Button>(R.id.ApuestasButton)
        val Btn_Videojuegos = findViewById<Button>(R.id.VideojuegosButton)
        val Btn_Confirmar = findViewById<Button>(R.id.addStatButton2)
        var Horas = findViewById<EditText>(R.id.addTimeText)

        val doc_ref = user?.let { db.collection("users").document(it.uid) }
        doc_ref!!.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(Progress.TAG, "Datos Recibidos desde la Base de Datos")
                    val data_user = document.data
                    val adicciones = data_user?.get("type").toString()

                    if (!adicciones.contains("RedesSociales")) {
                        Btn_Redes.isEnabled = false
                        Btn_Redes.isClickable = false
                        Btn_Redes.setBackgroundColor(ContextCompat.getColor(textView.context, android.R.color.darker_gray))
                    } else {
                        Btn_Redes?.setOnClickListener(){
                            val StatsRedes = data_user?.get("stats_socialmedia") as ArrayList<Double>
                            setBarChart(StatsRedes, barChart)
                            Btn_Confirmar?.setOnClickListener()
                            {
                                val check = Horas.text.toString()

                                if (check.isDigitsOnly() && check != ""){
                                    if (check.toDouble() <= 24.0){
                                        val HorasRedes = check.toDouble()
                                        val horaBBDD = data_user.get("last_login") as Timestamp
                                        val n_dia = horaBBDD.toDate().day
                                        when(n_dia){
                                            0 -> {
                                                StatsRedes[6] = HorasRedes  // Domingo
                                                db.collection("users").document(user.uid).update("stats_socialmedia", StatsRedes)
                                                Toast.makeText(baseContext, "Uso de Redes Sociales Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            1 -> {
                                                StatsRedes[0] = HorasRedes  // Lunes
                                                db.collection("users").document(user.uid).update("stats_socialmedia", StatsRedes)
                                                Toast.makeText(baseContext, "Uso de Redes Sociales Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            2 -> {
                                                StatsRedes[1] = HorasRedes  // Martes
                                                db.collection("users").document(user.uid).update("stats_socialmedia", StatsRedes)
                                                Toast.makeText(baseContext, "Uso de Redes Sociales Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            3 -> {
                                                StatsRedes[2] = HorasRedes  // Miercoles
                                                db.collection("users").document(user.uid).update("stats_socialmedia", StatsRedes)
                                                Toast.makeText(baseContext, "Uso de Redes Sociales Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            4 -> {
                                                StatsRedes[3] = HorasRedes  // Jueves
                                                db.collection("users").document(user.uid).update("stats_socialmedia", StatsRedes)
                                                Toast.makeText(baseContext, "Uso de Redes Sociales Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            5 -> {
                                                StatsRedes[4] = HorasRedes  // Viernes
                                                db.collection("users").document(user.uid).update("stats_socialmedia", StatsRedes)
                                                Toast.makeText(baseContext, "Uso de Redes Sociales Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            6 -> {
                                                StatsRedes[5] = HorasRedes  // Sabado
                                                db.collection("users").document(user.uid).update("stats_socialmedia", StatsRedes)
                                                Toast.makeText(baseContext, "Uso de Redes Sociales Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {
                                                Toast.makeText(baseContext, "Se ha producido un error desconocido", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        setBarChart(StatsRedes, barChart)

                                        //COMPRABACION DE SI ES LA PRIMERA VEZ QUE SE REALIZA EN EL DIA
                                        val fechaT = Timestamp.now()

                                        //Setteamos a 0 las horas para solo tener en cuenta el dia
                                        val fecha = fechaT.toLocalDateTime()

                                        doc_ref.get()
                                            .addOnSuccessListener { document ->
                                                if (document.data != null) {
                                                    Log.d(Progress.TAG, "Datos Recibidos desde la Base de Datos")
                                                    val data_user = document.data
                                                    val ultima_fechaD = data_user?.get("last_hours_progress") as Timestamp   // Obtengo fecha de la base de datos

                                                    val ultima_fecha = ultima_fechaD.toLocalDateTime()

                                                    Log.d("Fecha - Base de datos", ultima_fecha.toString())
                                                    Log.d("Fecha - Actual", fecha.toString())

                                                    if(fecha.dayOfMonth > ultima_fecha.dayOfMonth){
                                                        var cont_stats = data_user.get("cont_award_stats").toString().toInt()  // Obtengo contador estadisticas diario
                                                        val level_db = data_user.get("level").toString().toFloat()    // Obtengo nivel de la base de datos
                                                        val newLevel = ObtenerExperiencia(40,level_db,0.5f) // Calculo el nuevo nivel
                                                        val database = db.collection("users").document(user.uid)    // Obtengo el documento de la base de datos
                                                        database.update("level", newLevel)  // Se guarda el nuevo nivel en la base de datos
                                                        database.update("last_hours_progress", Timestamp.now())  // Se guarda la fecha de realizacion de la encuesta en la base de datos
                                                        if(fecha.dayOfMonth == ultima_fecha.plusDays(1).dayOfMonth){
                                                            cont_stats += 1 // Incremento en uno el contador
                                                            db.collection("users").document(user.uid)   // y se actualiza el campo en la BBDD
                                                                .update("cont_award_stats", cont_stats)
                                                        }

                                                    } else {
                                                        val database = db.collection("users").document(user.uid)    // Obtengo el documento de la base de datos
                                                        database.update("last_hours_progress", Timestamp.now())  // Se guarda la fecha de realizacion de la encuesta en la base de datos
                                                    }

                                                    val builder = AlertDialog.Builder(this)
                                                    builder.setTitle("Registro de Horas")
                                                    builder.setMessage("¿Deseas registrar otra estadística?")
                                                    builder.setCancelable(true)

                                                    builder.setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                                                        Toast.makeText(this, "Regresando al Menu Principal", Toast.LENGTH_SHORT).show()
                                                        val login = Intent(applicationContext, Login::class.java)
                                                        startActivity(login)
                                                        finish()
                                                    })

                                                    builder.setPositiveButton("Si", DialogInterface.OnClickListener{ dialog, which ->
                                                    })
                                                    val alertDialog = builder.create()
                                                    alertDialog.show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(this, "Dato Introducido No Permitido", Toast.LENGTH_SHORT).show()  // Muestro un mensaje por pantalla
                                    }
                                } else {
                                    Toast.makeText(this, "Dato Introducido No Permitido", Toast.LENGTH_SHORT).show()  // Muestro un mensaje por pantalla
                                }
                            }
                        }
                    }

                    if (!adicciones.contains("Apuestas")) {
                        Btn_Apuestas.isEnabled = false
                        Btn_Apuestas.isClickable = false
                        Btn_Apuestas.setBackgroundColor(ContextCompat.getColor(textView.context, android.R.color.darker_gray))
                    } else {
                        Btn_Apuestas?.setOnClickListener(){
                            val StatsAdicciones = data_user?.get("stats_bets") as ArrayList<Double>
                            setBarChart(StatsAdicciones, barChart)
                            Btn_Confirmar?.setOnClickListener()
                            {
                                val check = Horas.text.toString()

                                if (check.isDigitsOnly() && check != ""){
                                    if (check.toDouble() <= 24.0){
                                        val HorasAdicciones = check.toDouble()
                                        val horaBBDD = data_user.get("last_login") as Timestamp
                                        val n_dia = horaBBDD.toDate().day
                                        when(n_dia){
                                            0 -> {
                                                StatsAdicciones[6] = HorasAdicciones  // Domingo
                                                db.collection("users").document(user.uid).update("stats_bets", StatsAdicciones)
                                                Toast.makeText(baseContext, "Uso de Apuestas Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            1 -> {
                                                StatsAdicciones[0] = HorasAdicciones  // Lunes
                                                db.collection("users").document(user.uid).update("stats_bets", StatsAdicciones)
                                                Toast.makeText(baseContext, "Uso de Apuestas Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            2 -> {
                                                StatsAdicciones[1] = HorasAdicciones  // Martes
                                                db.collection("users").document(user.uid).update("stats_bets", StatsAdicciones)
                                                Toast.makeText(baseContext, "Uso de Apuestas Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            3 -> {
                                                StatsAdicciones[2] = HorasAdicciones  // Miercoles
                                                db.collection("users").document(user.uid).update("stats_bets", StatsAdicciones)
                                                Toast.makeText(baseContext, "Uso de Apuestas Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            4 -> {
                                                StatsAdicciones[3] = HorasAdicciones  // Jueves
                                                db.collection("users").document(user.uid).update("stats_bets", StatsAdicciones)
                                                Toast.makeText(baseContext, "Uso de Apuestas Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            5 -> {
                                                StatsAdicciones[4] = HorasAdicciones  // Viernes
                                                db.collection("users").document(user.uid).update("stats_bets", StatsAdicciones)
                                                Toast.makeText(baseContext, "Uso de Apuestas Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            6 -> {
                                                StatsAdicciones[5] = HorasAdicciones  // Sabado
                                                db.collection("users").document(user.uid).update("stats_bets", StatsAdicciones)
                                                Toast.makeText(baseContext, "Uso de Apuestas Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {
                                                Toast.makeText(baseContext, "Se ha producido un error desconocido", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        setBarChart(StatsAdicciones, barChart)

                                        //COMPRABACION DE SI ES LA PRIMERA VEZ QUE SE REALIZA EN EL DIA
                                        val fechaT = Timestamp.now()

                                        //Setteamos a 0 las horas para solo tener en cuenta el dia
                                        val fecha = fechaT.toLocalDateTime()

                                        doc_ref.get()
                                            .addOnSuccessListener { document ->
                                                if (document.data != null) {
                                                    Log.d(Progress.TAG, "Datos Recibidos desde la Base de Datos")
                                                    val data_user = document.data
                                                    val ultima_fechaD = data_user?.get("last_hours_progress") as Timestamp   // Obtengo fecha de la base de datos

                                                    val ultima_fecha = ultima_fechaD.toLocalDateTime()

                                                    Log.d("Fecha - Base de datos", ultima_fecha.toString())
                                                    Log.d("Fecha - Actual", fecha.toString())

                                                    if(fecha.dayOfMonth > ultima_fecha.dayOfMonth){
                                                        var cont_stats = data_user.get("cont_award_stats").toString().toInt()  // Obtengo contador estadisticas diario
                                                        val level_db = data_user.get("level").toString().toFloat()    // Obtengo nivel de la base de datos
                                                        val newLevel = ObtenerExperiencia(40,level_db,0.5f) // Calculo el nuevo nivel
                                                        val database = db.collection("users").document(user.uid)    // Obtengo el documento de la base de datos
                                                        database.update("level", newLevel)  // Se guarda el nuevo nivel en la base de datos
                                                        database.update("last_hours_progress", Timestamp.now())  // Se guarda la fecha de realizacion de la encuesta en la base de datos
                                                        if(fecha.dayOfMonth == ultima_fecha.plusDays(1).dayOfMonth){
                                                            cont_stats += 1 // Incremento en uno el contador
                                                            val awards = data_user?.get("awards") as java.util.ArrayList<Int>
                                                            if(cont_stats >= 5){
                                                                awards[1] = 1
                                                            }
                                                            if(cont_stats >= 10){
                                                                awards[1] = 2
                                                            }
                                                            if(cont_stats >= 25){
                                                                awards[1] = 3
                                                            }
                                                            if(cont_stats >= 50){
                                                                awards[1] = 4
                                                            }
                                                            if(cont_stats >= 75){
                                                                awards[1] = 5
                                                            }
                                                            if(cont_stats >= 100){
                                                                awards[1] = 6
                                                            }
                                                            db.collection("users").document(user.uid)   // y se actualiza el campo en la BBDD
                                                                .update("awards", awards)
                                                            db.collection("users").document(user.uid)   // y se actualiza el campo en la BBDD
                                                                .update("cont_award_stats", cont_stats)
                                                        }
                                                    } else {
                                                        val database = db.collection("users").document(user.uid)    // Obtengo el documento de la base de datos
                                                        database.update("last_hours_progress", Timestamp.now())  // Se guarda la fecha de realizacion de la encuesta en la base de datos
                                                    }

                                                    val builder = AlertDialog.Builder(this)
                                                    builder.setTitle("Registro de Horas")
                                                    builder.setMessage("¿Deseas registrar otra estadística?")
                                                    builder.setCancelable(true)

                                                    builder.setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                                                        Toast.makeText(this, "Regresando al Menu Principal", Toast.LENGTH_SHORT).show()
                                                        val login = Intent(applicationContext, Login::class.java)
                                                        startActivity(login)
                                                        finish()
                                                    })

                                                    builder.setPositiveButton("Si", DialogInterface.OnClickListener{ dialog, which ->
                                                    })
                                                    val alertDialog = builder.create()
                                                    alertDialog.show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(this, "Dato Introducido No Permitido", Toast.LENGTH_SHORT).show()  // Muestro un mensaje por pantalla
                                    }
                                } else {
                                    Toast.makeText(this, "Dato Introducido No Permitido", Toast.LENGTH_SHORT).show()  // Muestro un mensaje por pantalla
                                }
                            }
                        }
                    }

                    if (!adicciones.contains("Videojuegos")) {
                        Btn_Videojuegos.isEnabled = false
                        Btn_Videojuegos.isClickable = false
                        Btn_Videojuegos.setBackgroundColor(ContextCompat.getColor(textView.context, android.R.color.darker_gray))
                    } else {
                        Btn_Videojuegos?.setOnClickListener(){
                            val StatsVideojuegos = data_user?.get("stats_videogames") as ArrayList<Double>
                            setBarChart(StatsVideojuegos, barChart)
                            Btn_Confirmar?.setOnClickListener()
                            {
                                val check = Horas.text.toString()

                                if (check.isDigitsOnly() && check != ""){
                                    if (check.toDouble() <= 24.0){
                                        val HorasVideojuegos = check.toDouble()
                                        val horaBBDD = data_user.get("last_login") as Timestamp
                                        val n_dia = horaBBDD.toDate().day
                                        when(n_dia){
                                            0 -> {
                                                StatsVideojuegos[6] = HorasVideojuegos  // Domingo
                                                db.collection("users").document(user.uid).update("stats_videogames", StatsVideojuegos)
                                                Toast.makeText(baseContext, "Uso de Videojuegos Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            1 -> {
                                                StatsVideojuegos[0] = HorasVideojuegos  // Lunes
                                                db.collection("users").document(user.uid).update("stats_videogames", StatsVideojuegos)
                                                Toast.makeText(baseContext, "Uso de Videojuegos Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            2 -> {
                                                StatsVideojuegos[1] = HorasVideojuegos  // Martes
                                                db.collection("users").document(user.uid).update("stats_videogames", StatsVideojuegos)
                                                Toast.makeText(baseContext, "Uso de Videojuegos Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            3 -> {
                                                StatsVideojuegos[2] = HorasVideojuegos  // Miercoles
                                                db.collection("users").document(user.uid).update("stats_videogames", StatsVideojuegos)
                                                Toast.makeText(baseContext, "Uso de Videojuegos Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            4 -> {
                                                StatsVideojuegos[3] = HorasVideojuegos  // Jueves
                                                db.collection("users").document(user.uid).update("stats_videogames", StatsVideojuegos)
                                                Toast.makeText(baseContext, "Uso de Videojuegos Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            5 -> {
                                                StatsVideojuegos[4] = HorasVideojuegos  // Viernes
                                                db.collection("users").document(user.uid).update("stats_videogames", StatsVideojuegos)
                                                Toast.makeText(baseContext, "Uso de Videojuegos Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            6 -> {
                                                StatsVideojuegos[5] = HorasVideojuegos  // Sabado
                                                db.collection("users").document(user.uid).update("stats_videogames", StatsVideojuegos)
                                                Toast.makeText(baseContext, "Uso de Videojuegos Actualizado", Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {
                                                Toast.makeText(baseContext, "Se ha producido un error desconocido", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        setBarChart(StatsVideojuegos, barChart)

                                        //COMPRABACION DE SI ES LA PRIMERA VEZ QUE SE REALIZA EN EL DIA
                                        val fechaT = Timestamp.now()

                                        //Setteamos a 0 las horas para solo tener en cuenta el dia
                                        val fecha = fechaT.toLocalDateTime()

                                        doc_ref.get()
                                            .addOnSuccessListener { document ->
                                                if (document.data != null) {
                                                    Log.d(Progress.TAG, "Datos Recibidos desde la Base de Datos")
                                                    val data_user = document.data
                                                    val ultima_fechaD = data_user?.get("last_hours_progress") as Timestamp   // Obtengo fecha de la base de datos

                                                    val ultima_fecha = ultima_fechaD.toLocalDateTime()

                                                    Log.d("Fecha - Base de datos", ultima_fecha.toString())
                                                    Log.d("Fecha - Actual", fecha.toString())

                                                    if(fecha.dayOfMonth > ultima_fecha.dayOfMonth){
                                                        var cont_stats = data_user.get("cont_award_stats").toString().toInt()  // Obtengo contador estadisticas diario
                                                        val level_db = data_user.get("level").toString().toFloat()    // Obtengo nivel de la base de datos
                                                        val newLevel = ObtenerExperiencia(40,level_db,0.5f) // Calculo el nuevo nivel
                                                        val database = db.collection("users").document(user.uid)    // Obtengo el documento de la base de datos
                                                        database.update("level", newLevel)  // Se guarda el nuevo nivel en la base de datos
                                                        database.update("last_hours_progress", Timestamp.now())  // Se guarda la fecha de realizacion de la encuesta en la base de datos
                                                        if(fecha.dayOfMonth == ultima_fecha.plusDays(1).dayOfMonth){
                                                            cont_stats += 1 // Incremento en uno el contador
                                                            db.collection("users").document(user.uid)   // y se actualiza el campo en la BBDD
                                                                .update("cont_award_stats", cont_stats)
                                                        }
                                                    } else {
                                                        val database = db.collection("users").document(user.uid)    // Obtengo el documento de la base de datos
                                                        database.update("last_hours_progress", Timestamp.now())  // Se guarda la fecha de realizacion de la encuesta en la base de datos
                                                    }

                                                    val builder = AlertDialog.Builder(this)
                                                    builder.setTitle("Registro de Horas")
                                                    builder.setMessage("¿Deseas registrar otra estadística?")
                                                    builder.setCancelable(true)

                                                    builder.setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                                                        Toast.makeText(this, "Regresando al Menu Principal", Toast.LENGTH_SHORT).show()
                                                        val login = Intent(applicationContext, Login::class.java)
                                                        startActivity(login)
                                                        finish()
                                                    })

                                                    builder.setPositiveButton("Si", DialogInterface.OnClickListener{ dialog, which ->
                                                    })
                                                    val alertDialog = builder.create()
                                                    alertDialog.show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(this, "Dato Introducido No Permitido", Toast.LENGTH_SHORT).show()  // Muestro un mensaje por pantalla
                                    }
                                } else {
                                    Toast.makeText(this, "Dato Introducido No Permitido", Toast.LENGTH_SHORT).show()  // Muestro un mensaje por pantalla
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun setBarChart(values: ArrayList<Double>, barChart: BarChart) {
        val entries = ArrayList<BarEntry>()

        for (value in values) {
            entries.add(BarEntry(value.toFloat(), values.indexOf(value)))
        }

        /*entries.add(BarEntry(8f, 0))
    entries.add(BarEntry(2f, 1))
    entries.add(BarEntry(5f, 2))
    entries.add(BarEntry(20f, 3))
    entries.add(BarEntry(15f, 4))
    entries.add(BarEntry(19f, 5))
    entries.add(BarEntry(5f, 6))
    */
        val barDataSet = BarDataSet(entries, "Horas")

        val labels = ArrayList<String>()
        labels.add("Lun")
        labels.add("Mar")
        labels.add("Mie")
        labels.add("Jue")
        labels.add("Vie")
        labels.add("Sab")
        labels.add("Dom")
        val data = BarData(labels, barDataSet)

        barChart.data = data // set the data and list of lables into chart

        barChart.setDescription("Tiempo de uso semanal")  // set the description

        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        barDataSet.color = R.color.blueProgress

        barChart.animateY(5000)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun Timestamp.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()) = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(seconds * 1000 + nanoseconds / 1000000), zone)
}