package com.chibuleo.sfcmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListaCuenta extends ArrayAdapter {

    private ArrayList datos;
    private Context contexto;

    public ListaCuenta(Context contexto, ArrayList datos) {
        super(contexto,R.layout.lyl_registro_cuenta , datos);
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
            listItemView = inflater.inflate(R.layout.lyl_registro_cuenta,parent,false);
        }

        //Obteniendo instancias de los text views
        TextView codigo = (TextView)listItemView.findViewById(R.id.lblCodigo);
        TextView tipoCuenta = (TextView)listItemView.findViewById(R.id.lblTipoCuenta);
        TextView saldo = (TextView)listItemView.findViewById(R.id.lblSaldo);
        TextView estado = (TextView)listItemView.findViewById(R.id.lblEstado);

        //Obteniendo instancia de la Tarea en la posición actual
        Utilitarios.CuentaLigth item = (Utilitarios.CuentaLigth) getItem(position);

        codigo.setText(item.getCodigo());
        tipoCuenta.setText(item.getTipoCuenta());
        saldo.setText(item.getSaldo());
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