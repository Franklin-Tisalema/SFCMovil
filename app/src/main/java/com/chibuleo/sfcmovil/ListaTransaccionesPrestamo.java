package com.chibuleo.sfcmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListaTransaccionesPrestamo extends ArrayAdapter {

    private ArrayList datos;
    private Context contexto;

    public ListaTransaccionesPrestamo(Context contexto, ArrayList datos) {
        super(contexto,R.layout.lyl_lista_transaccion_prestamo, datos);
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
            listItemView = inflater.inflate(R.layout.lyl_lista_transaccion_prestamo,parent,false);
        }

        //Obteniendo instancias de los text views
        TextView fecha = (TextView)listItemView.findViewById(R.id.lblFecha);
        TextView documento = (TextView)listItemView.findViewById(R.id.lblDocumento);
        TextView valor = (TextView)listItemView.findViewById(R.id.lblValor);
        TextView saldo = (TextView)listItemView.findViewById(R.id.lblSaldo);
        TextView usuario = (TextView)listItemView.findViewById(R.id.lblUsuario);
        TextView oficina = (TextView)listItemView.findViewById(R.id.lblOficina);
        TextView transaccion = (TextView)listItemView.findViewById(R.id.lblTipoTransaccion);

        //Obteniendo instancia de la Tarea en la posición actual
        Utilitarios.TransaccionPrestamo item = (Utilitarios.TransaccionPrestamo) getItem(position);

        fecha.setText(item.getFecha());
        documento.setText(item.getDocumento());
        valor.setText(item.getValor());
        saldo.setText(item.getSaldo());
        usuario.setText(item.getUsuario());
        oficina.setText(item.getOficina());
        transaccion.setText(item.getTransaccion());

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