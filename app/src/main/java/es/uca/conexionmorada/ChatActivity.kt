package es.uca.conexionmorada

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import java.time.Instant

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var nomTema = getIntent().getExtras()?.getString("nombreTema").toString()
        var db = Firebase.firestore
        var auth = Firebase.auth

        tituloTema.text = nomTema

        rvmensajes.layoutManager = LinearLayoutManager(this)
        rvmensajes.adapter = MessageAdapter()
        var enviar = findViewById<FloatingActionButton>(R.id.sendMesage)
        enviar.setOnClickListener() {
            var find = findViewById<EditText>(R.id.Mensage)
            var mensajeEnviar = find.getText().toString()
            var nicknameEnviar = "Prueba"
            Mensage.setText(null)
            var id = auth.currentUser
            var aux = id?.uid.toString()
            val doc_ref = id.let { db.collection("users").document(aux) }
            doc_ref.get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        //Log.d(Home.TAG, "Datos Recibidos desde la Base de Datos")
                        var data_user = document.data
                        nicknameEnviar = data_user?.get("username").toString()
                        var hora = Instant.now().toString()
                        var message = Mensaje( mensajeEnviar,nicknameEnviar, hora)

                        db.collection("foro").document(nomTema).collection("mensajes").document().set(message)
                    }
                }
            }
           db.collection("foro").document(nomTema).collection("mensajes").orderBy("hora",Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { messages ->
               var listMessages = messages.toObjects(Mensaje::class.java)
                (rvmensajes.adapter as MessageAdapter).setData(listMessages)
            }
               .addOnFailureListener { exception ->
                   Log.d(TAG, "get failed with ", exception)
               }

         db.collection("foro").document(nomTema).collection("mensajes").orderBy("hora",Query.Direction.ASCENDING)
            .addSnapshotListener { messages, error ->
                if(error == null){
                    messages?.let {
                     val listMessages = it.toObjects(Mensaje::class.java)
                        (rvmensajes.adapter as MessageAdapter).setData(listMessages)
                    }
                }
            }
    }
        companion object{
            private const val TAG = "Mensaje"
        }

}