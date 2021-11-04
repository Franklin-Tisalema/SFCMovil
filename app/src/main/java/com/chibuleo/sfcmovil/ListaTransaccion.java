package com.chibuleo.sfcmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListaTransaccion extends ArrayAdapter {
    private ArrayList datos;
    private Context contexto;

    public ListaTransaccion(Context contexto, ArrayList datos) {
        super(contexto,R.layout.lyl_lista_transaccion_movil, datos);
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
            listItemView = inflater.inflate(R.layout.lyl_lista_transaccion_movil,parent,false);
        }

        //Obteniendo instancias de los text views
        TextView cliente = (TextView)listItemView.findViewById(R.id.lblCliente);
        TextView nombre = (TextView)listItemView.findViewById(R.id.lblNombre);
        TextView codigo = (TextView)listItemView.findViewById(R.id.lblCodigo);
        TextView producto = (TextView)listItemView.findViewById(R.id.lblProducto);
        TextView valor = (TextView)listItemView.findViewById(R.id.lblValor);
        TextView documento = (TextView)listItemView.findViewById(R.id.lblDocumento);
        TextView fecha = (TextView)listItemView.findViewById(R.id.lblFecha);
        TextView estado = (TextView)listItemView.findViewById(R.id.lblEstado);
        TextView tipoTransaccion = (TextView)listItemView.findViewById(R.id.lblTipoTransaccion);

        //Obteniendo instancia de la Tarea en la posición actual
        ConsultaTransacciones.Transaccion item = (ConsultaTransacciones.Transaccion) getItem(position);

        cliente.setText(item.getCliente());
        nombre.setText(item.getNombre());
        codigo.setText(item.getCodigo());
        producto.setText(item.getProducto());
        valor.setText(item.getValor());
        documento.setText(item.getDocumento());
        fecha.setText(item.getFecha());
        estado.setText(item.getEstado());
        tipoTransaccion.setText(item.getTipoTransaccion());

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
