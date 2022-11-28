package es.uca.conexionmorada

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.util.*
import android.util.Base64
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

       auth = Firebase.auth

        val regEmail = findViewById<TextView>(R.id.emailRegister)
        val regUsername = findViewById<TextView>(R.id.nickNameRegister)
        val regPassword = findViewById<TextView>(R.id.passwordRegister)
        val regConfPassword = findViewById<TextView>(R.id.passwordConfirm)
        val regRegistro = findViewById<Button>(R.id.confirmRegister)
        val regRedes = findViewById<CheckBox>(R.id.redesSociales)
        val regApuestas = findViewById<CheckBox>(R.id.Apuestas)
        val regVideojuegos = findViewById<CheckBox>(R.id.Videojuegos)
        val regNose = findViewById<CheckBox>(R.id.NoSabe)
        val progressBar = findViewById<ProgressBar>(R.id.reg_progress)

        regRegistro.setOnClickListener(View.OnClickListener {
            val email: String
            val username: String
            val password: String
            val confirmPassword: String
            var type: String = ""
            val level: Float = 1.0F

            email = regEmail.getText().toString()
            username = regUsername.getText().toString()
            password = regPassword.getText().toString()
            confirmPassword = regConfPassword.getText().toString()

            if (email != "" && username != "" && password != "" && (regRedes.isChecked == true ||
                        regApuestas.isChecked == true || regVideojuegos.isChecked == true ||
                        regNose.isChecked == true)) {
                if (username.length < 3){
                    Toast.makeText(
                        applicationContext,
                        "El username debe contener al menos 3 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (password.length >= 8) {
                        if (regNose.isChecked == true)
                            type += "NoProcede "
                        if (regRedes.isChecked == true)
                            type += "RedesSociales "
                        if (regApuestas.isChecked == true)
                            type += "Apuestas "
                        if (regVideojuegos.isChecked == true)
                            type += "Videojuegos"

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "El password debe contener al menos 8 caracteres",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (password != confirmPassword){
                        Toast.makeText(
                            applicationContext,
                            "Los passwords introducidos no son iguales",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        progressBar.setVisibility(View.VISIBLE)
                        val key: String = "-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVm"
                        val encryptedPassword : String? = encrypt(password,key)
                        crearCuenta(email, username, password, type, level, encryptedPassword)
                        progressBar.setVisibility(View.GONE)
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Todos los campos son requeridos",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

        val tCondition = findViewById<TextView>(R.id.textConditions)
        tCondition.setOnClickListener {
            val condAct = Intent(this, ConditionsActivity::class.java)
            startActivity(condAct)
        }


        /* Intercambio de bloqueos entre checkbox */
        val noResponse = findViewById<CheckBox>(R.id.NoSabe)

        noResponse.setOnCheckedChangeListener { buttonView, isChecked ->
            regRedes.isEnabled = !isChecked
            regApuestas.isEnabled = !isChecked
            regVideojuegos.isEnabled = !isChecked
        }

        regRedes.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regApuestas.isChecked && !regVideojuegos.isChecked
        }
        regApuestas.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regRedes.isChecked && !regVideojuegos.isChecked
        }
        regVideojuegos.setOnCheckedChangeListener { buttonView, isChecked ->
            noResponse.isEnabled = !isChecked && !regApuestas.isChecked && !regRedes.isChecked
        }
    }

    companion object{
        private var TAG = "EmailPassword"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun crearCuenta (email: String, username: String, password: String, type: String, level: Float, encryptedPassword: String?){
        val query = db.collection("users").whereEqualTo("username", username).get()
            .addOnCompleteListener(this) {query ->
                if (query.result!!.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                Toast.makeText(
                                    baseContext, "Registro Correcto",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUI(user)

                                val arrayRedes = arrayListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
                                val arrayApuestas = arrayListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
                                val arrayVideojuegos = arrayListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)

                                val logros = arrayListOf(-1,-1,-1)
                                val logro_login = 1
                                val logro_estadistica = 1

                                val current = Timestamp.now()

                                val usuario = hashMapOf(
                                    "email" to email,
                                    "username" to username,
                                    "password" to encryptedPassword,
                                    "type" to type,
                                    "level" to level,
                                    "last_login" to current,
                                    "last_survey" to current,
                                    "last_hours_progress" to current,
                                    "stats_socialmedia" to arrayRedes,
                                    "stats_bets" to arrayApuestas,
                                    "stats_videogames" to arrayVideojuegos,
                                    "awards" to logros,
                                    "cont_award_login" to logro_login,
                                    "cont_award_stats" to logro_estadistica
                                )

                                TAG = "DocSnippets"
                                 if (user != null) {
                                    db.collection("users").document(user.uid)
                                        .set(usuario)
                                        .addOnSuccessListener {
                                            Log.d(
                                                TAG,
                                                "Documento escrito de forma exitosa"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                TAG,
                                                "Error al escribir el documento",
                                                e
                                            )
                                        }
                                }
                                user!!.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "Email de verificacion.")
                                            Toast.makeText(
                                                baseContext, "Revisa el correo electronico para verificar el correo",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }


                                val registro = Intent(applicationContext, LoginActivity::class.java)
                                startActivity(registro)
                                finish()
                                // Sign in success, update UI with the signed-in user's information

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Fallo de Registro",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUI(null)
                            }
                        }
                } else {
                    Toast.makeText(
                        baseContext, "El usuario ya existe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }
    fun encrypt(strToEncrypt: String, secret_key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secret_key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(
                    Base64.encode(cipherText, Base64.DEFAULT)
                )
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }
}