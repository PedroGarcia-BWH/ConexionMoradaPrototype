package es.uca.conexionmorada;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.conexionmorada.R;

public class HolderMensaje extends RecyclerView.ViewHolder
{
    public HolderMensaje(View itemView)
    {
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nickname);
        mensaje = (TextView) itemView.findViewById(R.id.cuerpoMensaje);
        hora = (TextView) itemView.findViewById(R.id.horaMensaje);
        fotoMensajePerfil = (ImageView) itemView.findViewById(R.id.imagenMensajeA);
    }

    private TextView nombre;
    private TextView mensaje;
    private TextView hora;
    private ImageView fotoMensajePerfil;

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public ImageView getFotoMensajePerfil() {
        return fotoMensajePerfil;
    }

    public void setFotoMensajePerfil(ImageView fotoMensajePerfil) {
        this.fotoMensajePerfil = fotoMensajePerfil;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }
}
