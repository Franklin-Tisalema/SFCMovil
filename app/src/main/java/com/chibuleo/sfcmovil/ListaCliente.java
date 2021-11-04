package com.chibuleo.sfcmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chibuleo.sfcmovil.R;
import com.chibuleo.sfcmovil.Utilitarios;

import java.util.ArrayList;

public class ListaCliente extends ArrayAdapter {

    private ArrayList datos;
    private Context contexto;

    public ListaCliente(Context contexto, ArrayList datos) {
        super(contexto, R.layout.lyl_lista_clientes , datos);
        this.contexto =  contexto;
        this.datos = datos;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con two_line_list_item.xml
            listItemView = inflater.inflate(R.layout.lyl_lista_clientes,parent,false);
        }

        //Obteniendo instancias de los text views
        TextView numero = (TextView)listItemView.findViewById(R.id.txtNumeroCliente);
        TextView telefono = (TextView)listItemView.findViewById(R.id.txtNumeroTelefono);
        TextView diasMora = (TextView)listItemView.findViewById(R.id.txtDiasMora);
        TextView nombre = (TextView)listItemView.findViewById(R.id.txtNombre);
        TextView direccion = (TextView)listItemView.findViewById(R.id.txtDireccion);
        TextView saldoAhorros = (TextView)listItemView.findViewById(R.id.txtSaldoAhorros);
        TextView garante1 = (TextView)listItemView.findViewById(R.id.txtGarante1);
        TextView garante2 = (TextView)listItemView.findViewById(R.id.txtGarante2);

        //Obteniendo instancia de la Tarea en la posición actual
        Utilitarios.ClienteLigth item = (Utilitarios.ClienteLigth) getItem(position);

        numero.setText(item.getNumeroCliente());
        telefono.setText(item.getTelefono());
        diasMora.setText(item.getDiasMora());
        nombre.setText(item.getNombre());
        direccion.setText(item.getDireccion());
        saldoAhorros.setText(item.getSaldoAhorros());
        garante1.setText(item.getGarante1());
        garante2.setText(item.getGarante2());

        //Devolver al ListView la fila creada
        return listItemView;
    }

    @Override
    public Object getItem(int posicion) {
        return datos.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }
}