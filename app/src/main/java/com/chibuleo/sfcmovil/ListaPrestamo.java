package com.chibuleo.sfcmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListaPrestamo extends ArrayAdapter {

    private ArrayList datos;
    private Context contexto;

    public ListaPrestamo(Context contexto, ArrayList datos) {
        super(contexto,R.layout.lyl_registro_prestamo , datos);
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
            listItemView = inflater.inflate(R.layout.lyl_registro_prestamo,parent,false);
        }

        //Obteniendo instancias de los text views
        TextView pagare = (TextView)listItemView.findViewById(R.id.lblPagare);
        TextView tipoPrestamo = (TextView)listItemView.findViewById(R.id.lblTipoPrestamo);
        TextView monto = (TextView)listItemView.findViewById(R.id.lblMonto);
        TextView estado = (TextView)listItemView.findViewById(R.id.lblEstado);

        //Obteniendo instancia de la Tarea en la posición actual
        Utilitarios.PrestamoLigth item = (Utilitarios.PrestamoLigth) getItem(position);

        pagare.setText(item.getPagare());
        tipoPrestamo.setText(item.getTipoPestamo());
        monto.setText(item.getMonto());
        estado.setText(item.getEstado());

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