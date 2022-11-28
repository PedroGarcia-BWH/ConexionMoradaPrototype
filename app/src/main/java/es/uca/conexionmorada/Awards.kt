package es.uca.conexionmorada

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.conexionmorada.Controlador.Progress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_awards.*
import kotlinx.android.synthetic.main.activity_awards.view.*

class Awards : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    companion object {
        private var TAG = "ProgressBar"
    }
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_awards)

        //Lista con todos los imageView de los trofeos
        val imageList: MutableList<ImageView> = arrayListOf()
        imageList.add(findViewById<ImageView>(R.id.imageView12))
        imageList.add(findViewById<ImageView>(R.id.imageView8))
        imageList.add(findViewById<ImageView>(R.id.imageView10))
        for (id in 13 until 28) {
            imageList.add(
                findViewById<ImageView>(
                    resources.getIdentifier(
                        "imageView" + id,
                        "id",
                        packageName
                    )
                )
            )
        }

        //Creacion de lista trofeos
        var AwardList: MutableList<Award> = arrayListOf()
        AwardList.add(Award("Chat", 1, imageList[0]))
        AwardList.add(Award("Estadistica", 1, imageList[1]))
        AwardList.add(Award("XP", 1, imageList[2]))

        val categoria: Array<String> = arrayOf("Chat", "Estadistica", "XP")
        val nivel: Array<Int> = arrayOf(1, 2, 3, 4, 5, 6)
        var contador = 0
        var nivelIt = 0

        for (it in 0..17) {
            if (contador == 3) {
                contador = 0
                nivelIt++
            }
            AwardList.add(Award(categoria[contador], nivel[nivelIt], imageList[it]))
            contador++
        }

        //Descripcion de AwardList: Lista que contiene todos los logros disponibles
        // Primero = Chat, Segundo = Estadistica, Tercero = XP

        // 0, 1, 2 -> Bronces
        // 3, 4, 5 -> Plata
        // 6, 7, 8 -> Oro
        // 9, 10, 11 -> Diamante
        // 12, 13, 14 -> Platino
        // 15, 16, 17 -> Extra

        //Para desbloquear un trofeo -> AwardList[numTrofeo segun lo descrito arriba].desbloquear()


        auth = Firebase.auth
        val user = auth.currentUser

        val doc_ref = user?.let { db.collection("users").document(it.uid) }
        doc_ref!!.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(Progress.TAG, "Datos Recibidos desde la Base de Datos")
                    val data_user = document.data
                    val awards = data_user?.get("awards") as ArrayList<Int>
                    if(awards[0] >= 0){
                        AwardList[awards[0] * 3].desbloquear()
                        var desq = awards[0] * 3
                        while(desq >= 0){
                            AwardList[desq].desbloquear()
                            desq = desq - 3
                        }
                    }
                    if(awards[1] >= 0)
                        AwardList[awards[1] * 3 + 1].desbloquear()
                    var desq2 = awards[1] * 3 + 1
                    while(desq2 >= 0){
                        AwardList[desq2].desbloquear()
                        desq2 = desq2 - 3
                    }
                    if(awards[2] >= 0)
                        AwardList[awards[2] * 3 + 2].desbloquear()
                    var desq = awards[2] * 3 + 2
                    while(desq > 0){
                        Log.d("XP Logro", desq.toString())
                        AwardList[desq].desbloquear()
                        AwardList[desq].ImageId.alpha = 1F
                        desq = desq - 3
                    }
                }
            }
    }
}



class Award{
    val Categoria : String
    val Nivel : Int
    val ImageId: ImageView
    var Conseguido : Boolean

    constructor(Categoria: String, Nivel: Int, ImageId: ImageView, Conseguido : Boolean = false){
        this.Categoria = Categoria
        this.Nivel = Nivel
        this.ImageId = ImageId
        this.Conseguido = Conseguido
        if(Conseguido == false){
            ImageId.alpha = 0.5F
        }
    }

    fun desbloquear(){
        this.Conseguido = true
        this.ImageId.alpha = 1F
    }

    override fun toString(): String {
        return "Award(Categoria='$Categoria', Nivel=$Nivel, ImageId=$ImageId, Conseguido=$Conseguido)"
    }

}