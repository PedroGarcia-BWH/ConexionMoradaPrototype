package es.uca.conexionmorada

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth

        val user = auth.currentUser

        val doc_ref = user?.let { db.collection("users").document(it.uid) }
        doc_ref!!.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(TAG, "Datos Recibidos desde la Base de Datos")
                    val data_user = document.data
                    val email = data_user?.get("email").toString()
                    val encryptedPassword = data_user?.get("password").toString()
                    val password = decrypt("-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVm",encryptedPassword)

                    val credential = EmailAuthProvider
                            .getCredential(email, password!!)

                    user.reauthenticate(credential)
                        .addOnCompleteListener { Log.d(TAG, "Usuario Reautenticado.")}
                        } else {
                    Log.d(TAG, "No existe dicho documento en la Base de Datos")
                }

                val changeEmail = findViewById<TextView>(R.id.emailChange)
                val changeUsername = findViewById<TextView>(R.id.usernameChange)
                val changePassword = findViewById<TextView>(R.id.passwordChange)
                val changePasswordConfirm = findViewById<TextView>(R.id.passwordChangeConfirm)
                val changeConfirm = findViewById<Button>(R.id.confirmChange)

                changeConfirm.setOnClickListener(View.OnClickListener {
                    val email: String
                    val username: String
                    val password: String
                    val confirmPassword: String
                    var cont: Int = 0

                    email = changeEmail.getText().toString()
                    username = changeUsername.getText().toString()
                    password = changePassword.getText().toString()
                    confirmPassword = changePasswordConfirm.getText().toString()

                    if (email == "" && username == "" && password == "" && confirmPassword == "") {
                        Toast.makeText(
                            applicationContext,
                            "No se ha modificado ningun parametro del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Comprobacion Email Ya Existe en la Database
                        val query1 = db.collection("users").whereEqualTo("email", email).get()
                            .addOnCompleteListener { query ->
                                if (!query.result!!.isEmpty) {
                                    Log.d(TAG, "No se ha actualizado el correo del usuario.")
                                    Toast.makeText(
                                        baseContext, "El Correo introducido ya existe en la Base de Datos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Comprobacion Username Ya Existe en la Database
                                    val query2 = db.collection("users").whereEqualTo("username", username).get()
                                        .addOnCompleteListener { query ->
                                            if (!query.result!!.isEmpty) {
                                                Log.d(TAG, "No se ha actualizado el username.")
                                                Toast.makeText(
                                                    baseContext,
                                                    "El Nombre de Usuario introducido ya existe en la Base de Datos",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                val key = "-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVm"
                                                val encryptedPassword: String = encrypt(password, key)!!

                                                val userDocRef = user.let { db.collection("users").document(it.uid) }
                                                userDocRef.get()
                                                    .addOnSuccessListener { document ->
                                                        if (document.data != null) {
                                                            Log.d(TAG,"Datos Recibidos desde la Base de Datos")
                                                            val data_user = document.data
                                                            val databasePassword = data_user?.get("password").toString()
                                                            if (username.length < 3){
                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "El username debe contener al menos 3 caracteres",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            } else {
                                                                if(password != "" && confirmPassword != "" && (password.length < 8 || confirmPassword.length < 8)){
                                                                    Toast.makeText(
                                                                        applicationContext,
                                                                        "La Contraseña debe contener al menos 8 caracteres",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                } else {
                                                                    if (password != confirmPassword) {
                                                                        Toast.makeText(
                                                                            applicationContext,
                                                                            "Las Contraseñas introducidas no son Iguales",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    } else{
                                                                        if (databasePassword == encryptedPassword){
                                                                            Toast.makeText(
                                                                                baseContext,
                                                                                "La Contraseña introducida es la actual Contraseña del Usuario",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        } else {
                                                                            CambioEmail(email)
                                                                            CambioUsername(username)
                                                                            CambioPassword(password,encryptedPassword)
                                                                            FunReturn()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                            }
                                        }
                                }
                            }
                    }
                })
            }
    }

    companion object {
        private var TAG = "ChangeData"
    }

    private fun CambioEmail(email: String){
        if (email != "") {
            user!!.updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        db.collection("users").document(user.uid)
                            .update("email", email)
                        Log.d(TAG, "Email del Usuario Actualizado.")
                        Toast.makeText(
                            baseContext, "Email del Usuario Actualizado",
                            Toast.LENGTH_SHORT
                        ).show()

                        user.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "Email de verificacion.")
                                    Toast.makeText(
                                        baseContext, "Revisa el nuevo correo electronico para verificar el correo",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }
        }
    }

    private fun CambioUsername(username: String){
        if (username != ""){
            db.collection("users").document(user!!.uid)
                .update("username", username)
            Log.d(TAG, "Username Actualizado.")
            Toast.makeText(
                baseContext, "Username del Usuario Actualizado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun CambioPassword(password: String, encryptedPassword: String){
        if (password != "") {
            user!!.updatePassword(password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        db.collection("users").document(user.uid)
                            .update("password", encryptedPassword)
                        Log.d(TAG, "Password del Usuario Actualizado.")
                        Toast.makeText(
                            baseContext, "Password del Usuario Actualizado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun FunReturn() {
        val home = Intent(applicationContext, Login::class.java)
        startActivity(home)
        finish()
    }

    private fun encrypt(strToEncrypt: String, secret_key: String): String? {
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
                    Base64.encode(cipherText)
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

    private fun decrypt(key: String, strToDecrypt: String?): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = org.bouncycastle.util.encoders.Base64
                .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
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