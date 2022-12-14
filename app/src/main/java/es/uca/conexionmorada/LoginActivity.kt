package es.uca.conexionmorada

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    private lateinit var logEmail: TextView
    private lateinit var logPassword: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        logEmail = findViewById(R.id.email)
        logPassword = findViewById(R.id.password)
        val logLogin = findViewById<Button>(R.id.login)
        val progressBar = findViewById<ProgressBar>(R.id.progress)

        logLogin.setOnClickListener(View.OnClickListener {
            val email: String
            val password: String

            email = logEmail.getText().toString()
            password = logPassword.getText().toString()
            if (email != "" && password != "") {
                progressBar.setVisibility(View.VISIBLE)
                InicioSesion(email,password)
                progressBar.setVisibility(View.GONE)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Todos los campos son requeridos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val registro =findViewById<TextView>(R.id.register)
        registro.setOnClickListener {
            val registerAct = Intent(this, RegisterActivity::class.java)
            startActivity(registerAct)
        }

    }
    companion object{
        private const val TAG = "EmailPassword"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun InicioSesion(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    val username = user?.email

                    if(!user!!.isEmailVerified){
                        Toast.makeText(
                            baseContext, "Email introducido no verificado. Revise su correo electrónico",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(baseContext, "Inicio de Sesión Correcto",
                            Toast.LENGTH_SHORT).show()

                        val user = auth.currentUser
                        val doc_ref = user?.let { db.collection("users").document(it.uid) }
                        doc_ref!!.get()
                            .addOnSuccessListener { document ->
                                if (document.data != null) {
                                    val data_user= document.data
                                    val date_last_login = data_user?.get("last_login") as Timestamp
                                    var cont_logins = data_user?.get("cont_award_login").toString().toInt()

                                    val fecha_bbdd = date_last_login.toLocalDateTime()
                                    val fecha_actual = Timestamp.now().toLocalDateTime()

                                    //Normalizar fecha
                                    var fechaActXP = date_last_login.toDate()
                                    var fechaBbddXP = Timestamp.now().toDate()

                                    fechaActXP.hours = 0
                                    fechaActXP.minutes = 0
                                    fechaActXP.seconds = 0
                                    fechaBbddXP.hours = 0
                                    fechaBbddXP.minutes = 0
                                    fechaBbddXP.seconds = 0

                                    Log.d("Actual XP",fechaActXP.toString())
                                    Log.d("BBDD XP",fechaBbddXP.toString())
                                    Log.d("XP Condicion",fechaBbddXP.before(fechaActXP).toString())

                                    //Otorgar experiencia
                                    if(fechaBbddXP.before(fechaActXP)){
                                        val level_db = data_user.get("level").toString().toFloat()    // Obtengo nivel de la base de datos
                                        //val newLevel = ObtenerExperiencia(20,level_db,2.5f) // Calculo el nuevo nivel
                                        val database = db.collection("users").document(user.uid)    // Obtengo el documento de la base de datos
                                        //database.update("level", newLevel)  // Se guarda el nuevo nivel en la base de datos
                                        val xp = 20 + level_db * 2.5
                                        Toast.makeText(baseContext, "Has conseguido " + xp.toInt().toString() + "xp",
                                            Toast.LENGTH_SHORT).show()
                                    }

                                    // Inicio Sesion Dias Consecutivos --> Incrementa en 1 el contador y lo guarda en la BBDD
                                    if(fecha_actual.isAfter(fecha_bbdd) && fecha_actual.dayOfMonth == fecha_bbdd.plusDays(1).dayOfMonth){
                                        cont_logins += 1
                                        val awards = data_user?.get("awards") as ArrayList<Int>
                                        if(cont_logins == 5){
                                            awards[0] = 1
                                        }
                                        if(cont_logins >= 10){
                                            awards[0] = 2
                                        }
                                        if(cont_logins >= 25){
                                            awards[0] = 3
                                        }
                                        if(cont_logins >= 50){
                                            awards[0] = 4
                                        }
                                        if(cont_logins >= 75){
                                            awards[0] = 5
                                        }
                                        if(cont_logins >= 100){
                                            awards[0] = 6
                                        }
                                        db.collection("users").document(user.uid)   // y se actualiza el campo en la BBDD
                                            .update("awards", awards)
                                        db.collection("users").document(user.uid)
                                            .update("cont_award_login", cont_logins)
                                    }
                                    else{
                                        //Si los dias no son consecutivos entonces se reinicia el contador
                                        cont_logins = 1
                                        db.collection("users").document(user.uid)
                                            .update("cont_award_login", cont_logins)
                                    }
                                }
                                db.collection("users").document(user.uid)
                                    .update("last_login", Timestamp.now())

                                val login = Intent(applicationContext, Login::class.java)
                                startActivity(login)
                                logEmail.setText("")    // Borramos el email para el logout
                                logPassword.setText("") // Borramos la contraseña para el logout
                            }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Correo o Password Incorrecto",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Timestamp.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()) = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(seconds * 1000 + nanoseconds / 1000000), zone)

    private fun updateUI(user: FirebaseUser?) {
    }
    private fun reload() {}
}