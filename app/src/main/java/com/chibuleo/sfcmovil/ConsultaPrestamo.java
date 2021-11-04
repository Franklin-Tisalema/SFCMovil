package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.Cliente;
import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class ConsultaPrestamo extends AppCompatActivity {

    ProgressDialog pDialog;
    Parametros parametro = new Parametros();
    Utilitarios utilitario;

    private TextView lblUsuario;
    private TextView lblFecha;
    private Button btnConsultar;
    private Button btnLimpiar;
    private EditText txtNumeroCliente;
    private TextView lblNombreCliente;

    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Cliente cliente; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    /*
    private String usuario;
    private String fecha;
    private Boolean esLocal;
    private Boolean enLinea;
    private String  nombreImpresora;
    private int numeroCliente;
    private String cedulaCliente;
    private String nombreCliente;

     */

    private ImageView logo;

    private CheckBox cbConCedula;
    private CheckBox cbParaPago;

    private ListView lstvPrestamos;
    ArrayAdapter adaptador;

    ArrayList<Utilitarios.PrestamoLigth> listaPrestamos = new ArrayList<Utilitarios.PrestamoLigth>();

    /*public void onResume()
    {
        super.onResume();
        ConsultaPrestamo.this.finish();
        Intent activityPrincipal = new Intent(ConsultaPrestamo.this,MainActivity.class);
        startActivity(activityPrincipal);

    }
    @Override
    public void onPause()   {
        super.onPause();
        ConsultaPrestamo.this.finish();
    }
    @Override
    public void onStop()   {
        super.onStop();
        ConsultaPrestamo.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(ConsultaPrestamo.this,Distribuidor.class);
        distribuidor.putExtra("usuario",distriModel.getUsuario());
        distribuidor.putExtra("fecha", distriModel.getFecha());
        distribuidor.putExtra("esLocal", distriModel.getLocal());
        distribuidor.putExtra("enLinea",distriModel.getLinea());
        distribuidor.putExtra("impresora",impresora.getNombreImpresora());

        finish();
        startActivity(distribuidor);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_prestamo);
        distriModel = new DistribuidorM();
        cliente = new Cliente();
        impresora = new Impresora();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);//prevent phone from

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        btnConsultar =  (Button) findViewById(R.id.btnConsultar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        txtNumeroCliente = (EditText) findViewById(R.id.txtNumeroCliente);
        cbParaPago = (CheckBox) findViewById(R.id.cbPago);

        lstvPrestamos = (ListView) findViewById(R.id.lstvPrestamos);
        lblNombreCliente = (TextView) findViewById(R.id.lblNombreCliente);

        logo = (ImageView) findViewById(R.id.imageView);

        cbConCedula = (CheckBox) findViewById(R.id.cbConCedula);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(ConsultaPrestamo.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", ConsultaPrestamo.this);
            Intent principal = new Intent(ConsultaPrestamo.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(ConsultaPrestamo.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null){
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora")); ;
            cliente.setNumeroCliente(datos.getInt("numeroCliente"));

            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());

            if(cliente.getNumeroCliente()!=0 && cliente.getNumeroCliente()!=1)
                txtNumeroCliente.setText(String.valueOf(cliente.getNumeroCliente()));

            if(!distriModel.getLinea()) {
                logo.setImageResource(R.mipmap.logooff);
            }

            cbConCedula.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cbConCedula.isChecked()) txtNumeroCliente.setHint("Ej: 1801234259");
                    else txtNumeroCliente.setHint("Ej: 12345");
                }
            });

            btnConsultar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        try {
                            adaptador.clear();
                            lblNombreCliente.setText("");
                        }
                        catch (Exception error){
                            lblNombreCliente.setText("");
                            //utilitario.MostrarMensaje(error.getMessage(),ConsultaPrestamo.this);
                        }

                        if (!Utilitarios.EstaVacio(txtNumeroCliente)) {

                            if(cbConCedula.isChecked())
                            {
                                parametro.DefinirMetodo("ListaPrestamosClienteCedulaLigth", distriModel.getLocal(),ConsultaPrestamo.this);
                                cliente.setCedulaCliente(txtNumeroCliente.getText().toString());
                            }
                            else {
                                parametro.DefinirMetodo("ListaPrestamosClienteLigth", distriModel.getLocal(),ConsultaPrestamo.this);
                                cliente.setNumeroCliente(Integer.parseInt(txtNumeroCliente.getText().toString()));
                            }
                            pDialog = new ProgressDialog(ConsultaPrestamo.this);
                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pDialog.setCancelable(true);
                            pDialog.setMax(100);
                            pDialog.setProgress(0);

                            CargarListaPrestamos tarea = new CargarListaPrestamos();
                            tarea.execute();
                            pDialog = ProgressDialog.show(ConsultaPrestamo.this, "Por favor espere", "Cargando Lista de Préstamos Vigentes", true, false);
                        }
                        else {
                            utilitario.MostrarMensaje("Ingrese los datos solicitados", ConsultaPrestamo.this);
                        }
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaPrestamo.this);
                    }
                }
            });

            btnLimpiar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        adaptador.clear();
                        txtNumeroCliente.setText("");
                        lblNombreCliente.setText("");
                        txtNumeroCliente.requestFocus();
                        cbConCedula.setChecked(false);
                    }
                    catch (Exception error){
                        txtNumeroCliente.setText("");
                        lblNombreCliente.setText("");
                        txtNumeroCliente.requestFocus();
                        cbConCedula.setChecked(false);
                        //utilitario.MostrarMensaje(error.getMessage(),ConsultaPrestamo.this);
                    }
                }
            });

            lstvPrestamos.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Utilitarios.PrestamoLigth elegido = (Utilitarios.PrestamoLigth) parent.getItemAtPosition(position);
                    if(cbParaPago.isChecked()) {
                    //utilitario.MostrarMensaje("Selecciono el prestamo " + elegido.getPagare(),getApplicationContext());
                    Intent pagoPrestamo = new Intent(ConsultaPrestamo.this,PagoPrestamo.class);
                    pagoPrestamo.putExtra("usuario",distriModel.getUsuario());
                    pagoPrestamo.putExtra("pagare", elegido.getPagare());
                    pagoPrestamo.putExtra("fecha", distriModel.getFecha());
                    pagoPrestamo.putExtra("esLocal", distriModel.getLocal());
                    pagoPrestamo.putExtra("enLinea", distriModel.getLinea());
                    pagoPrestamo.putExtra("numeroCliente", Integer.toString(cliente.getNumeroCliente()));
                    pagoPrestamo.putExtra("impresora",impresora.getNombreImpresora());

                    finish();
                    startActivity(pagoPrestamo);

                }
                    else
                {
                    Intent consultaTransaccionesPrestamo = new Intent(ConsultaPrestamo.this, ConsultaTransaccionesPrestamo.class);
                    consultaTransaccionesPrestamo.putExtra("usuario", distriModel.getUsuario());
                    consultaTransaccionesPrestamo.putExtra("fecha", distriModel.getFecha());
                    consultaTransaccionesPrestamo.putExtra("esLocal", distriModel.getLocal());
                    consultaTransaccionesPrestamo.putExtra("enLinea", distriModel.getLinea());
                    consultaTransaccionesPrestamo.putExtra("numeroCliente", Integer.toString(cliente.getNumeroCliente()));
                    consultaTransaccionesPrestamo.putExtra("impresora", impresora.getNombreImpresora());
                    consultaTransaccionesPrestamo.putExtra("nombreCliente", cliente.getNombreCliente());
                    consultaTransaccionesPrestamo.putExtra("pagare", elegido.getPagare());
                    consultaTransaccionesPrestamo.putExtra("prestamo", elegido.getTipoPestamo());
                    consultaTransaccionesPrestamo.putExtra("monto", elegido.getMonto());
                    finish();
                    startActivity(consultaTransaccionesPrestamo);

                }

            }
            });
        }
        else {
            utilitario.MostrarMensaje("No existen datos", getApplicationContext());
        }
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    public ArrayList<Utilitarios.PrestamoLigth> ListaPrestamosCliente(){

        ArrayList<Utilitarios.PrestamoLigth> lista = new ArrayList<Utilitarios.PrestamoLigth>();
        if(distriModel.getLinea()) {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            if (!cbConCedula.isChecked()) {
                request.addProperty("unNumeroCliente", cliente.getNumeroCliente());
                request.addProperty("unCodigoUsuario", distriModel.getUsuario());
            } else {
                request.addProperty("unaCedula", cliente.getCedulaCliente());
                request.addProperty("unCodigoUsuario", distriModel.getUsuario());
            }
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                int cuantos = resSoap.getPropertyCount();

                for (int i = 0; i < cuantos; i++) {
                    SoapPrimitive res = (SoapPrimitive) resSoap.getProperty(i);
                    String[] camposPrestamo = res.toString().split("_sc_");
                    cliente.setNombreCliente(camposPrestamo[0]);
                    cliente.setNumeroCliente(Integer.parseInt(camposPrestamo[5]));
                    lista.add(new Utilitarios.PrestamoLigth(camposPrestamo[1], camposPrestamo[2], camposPrestamo[3], camposPrestamo[4], camposPrestamo[5]));
                }
            } catch (Exception e) {
                lista.add(new Utilitarios.PrestamoLigth(e.getMessage(), "", "", "",""));
            }
        }
        else {
            String condicion ="";
            if (cbConCedula.isChecked()) {
                condicion = " codigo = '" + cliente.getCedulaCliente() + "'";
            } else {
                condicion = " numeroCliente = '" + String.valueOf(cliente.getNumeroCliente()) + "'";
            }
            BaseDatos baseDatos = new BaseDatos(ConsultaPrestamo.this,BaseDatos.DB_NAME,null,BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getReadableDatabase();

            Cursor tabla = db.rawQuery("select numeroPagare, numeroCliente, nombreCliente, tipoCredito, " +
                    "  montoCredito, estado from DatosPrestamosClientes where  "+ condicion ,null);
            try
            {
                if (tabla.moveToFirst())
                {
                    do {

                        cliente.setNombreCliente(tabla.getString(tabla.getColumnIndex("nombreCliente")));
                        cliente.setNumeroCliente(Integer.parseInt(tabla.getString(tabla.getColumnIndex("numeroCliente"))));
                        lista.add(new Utilitarios.PrestamoLigth(tabla.getString(tabla.getColumnIndex("numeroPagare")),tabla.getString(tabla.getColumnIndex("tipoCredito")),
                                tabla.getString(tabla.getColumnIndex("montoCredito")),tabla.getString(tabla.getColumnIndex("estado")),tabla.getString(tabla.getColumnIndex("numeroCliente"))));

                    } while (tabla.moveToNext());
                }
             }
            catch (Exception ex)
            {
                lista.add(new Utilitarios.PrestamoLigth(ex.getMessage(), "", "", "",""));
            }
            db.close();

        }

        return lista;
    }

    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarListaPrestamos extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean respuesta = false;
            try
            {
                if(ListaPrestamosCliente().size()>0) {
                    listaPrestamos = ListaPrestamosCliente();
                    respuesta = true;
                }
                else respuesta=false;
            }
            catch (Exception e)
            {
                respuesta=false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (!result) {
                utilitario.MostrarMensaje("Se produjo un error al obtener el listado de Préstamos", ConsultaPrestamo.this);
            }
            else
            {
                if(listaPrestamos.size()>0) {
                    lblNombreCliente = (TextView) findViewById(R.id.lblNombreCliente);
                    lblNombreCliente.setText("Nombre: " + cliente.getNombreCliente());
                    lstvPrestamos = (ListView) findViewById(R.id.lstvPrestamos);
                    adaptador = new ListaPrestamo(ConsultaPrestamo.this, listaPrestamos);
                    lstvPrestamos.setAdapter(adaptador);
                }
                else {
                    utilitario.MostrarMensaje("Cliente no tiene préstamos vigentes",ConsultaPrestamo.this);
                }
            }
        }
    }

}



