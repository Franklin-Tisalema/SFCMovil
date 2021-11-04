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

public class ConsultaCuenta extends AppCompatActivity {

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
    private String codigoCuenta;
     */

    private ImageView logo;

    private CheckBox cbConCedula;
    private CheckBox cbEsParaDeposito;

    private ListView lstvCuentas;
    ArrayAdapter adaptador;

    ArrayList<Utilitarios.CuentaLigth> listaCuentas = new ArrayList<Utilitarios.CuentaLigth>();

    /*public void onResume()
    {
        super.onResume();
        ConsultaCuenta.this.finish();
        Intent activityPrincipal = new Intent(ConsultaCuenta.this,MainActivity.class);
        startActivity(activityPrincipal);

    }
    @Override
    public void onPause()   {
        super.onPause();
        ConsultaCuenta.this.finish();
    }
    @Override
    public void onStop()   {
        super.onStop();
        ConsultaCuenta.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(ConsultaCuenta.this,Distribuidor.class);
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
        setContentView(R.layout.activity_consulta_cuenta);
        cliente = new Cliente();
        impresora = new Impresora();
        distriModel = new DistribuidorM();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);//prevent phone from

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        btnConsultar =  (Button) findViewById(R.id.btnConsultar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        txtNumeroCliente = (EditText) findViewById(R.id.txtNumeroCliente);

        lstvCuentas = (ListView) findViewById(R.id.lstvCuentas);
        lblNombreCliente = (TextView) findViewById(R.id.lblNombreCliente);

        logo = (ImageView) findViewById(R.id.imageView);

        cbConCedula = (CheckBox) findViewById(R.id.cbConCedula);
        cbEsParaDeposito = (CheckBox) findViewById(R.id.cbDeposito);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(ConsultaCuenta.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", ConsultaCuenta.this);
            Intent principal = new Intent(ConsultaCuenta.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(ConsultaCuenta.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null){
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));
            cliente.setNumeroCliente(datos.getInt("numeroCliente"));

            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());
            if(cliente.getNumeroCliente()!=0)
                txtNumeroCliente.setText(String.valueOf(cliente.getNumeroCliente()));
            if(!distriModel.getLinea())
                logo.setImageResource(R.mipmap.logooff);
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
                            //utilitario.MostrarMensaje(error.getMessage(),ConsultaCuenta.this);
                        }

                        if (!Utilitarios.EstaVacio(txtNumeroCliente)) {

                            if(cbConCedula.isChecked())
                            {
                                parametro.DefinirMetodo("ListaCuentasClienteCedulaLigth", distriModel.getLocal(),ConsultaCuenta.this);
                                cliente.setCedulaCliente(txtNumeroCliente.getText().toString());
                            }
                            else {
                                parametro.DefinirMetodo("ListaCuentasClienteLigth", distriModel.getLocal(),ConsultaCuenta.this);
                                cliente.setNumeroCliente(Integer.parseInt(txtNumeroCliente.getText().toString()));
                            }
                            pDialog = new ProgressDialog(ConsultaCuenta.this);
                            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pDialog.setCancelable(true);
                            pDialog.setMax(100);
                            pDialog.setProgress(0);

                            CargarListaCuentas tarea = new CargarListaCuentas();
                            tarea.execute();
                            pDialog = ProgressDialog.show(ConsultaCuenta.this, "Por favor espere", "Cargando Lista de Cuentas Activas", true, false);
                        }
                        else {
                            utilitario.MostrarMensaje("Ingrese los datos solicitados", ConsultaCuenta.this);
                        }
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaCuenta.this);
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
                        //utilitario.MostrarMensaje(error.getMessage(),ConsultaCuenta.this);
                    }
                }
            });

            lstvCuentas.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Utilitarios.CuentaLigth elegido = (Utilitarios.CuentaLigth) parent.getItemAtPosition(position);
                    cliente.setCodigoCuenta(elegido.getCodigo());
                    //utilitario.MostrarMensaje("Selecciono el cuenta " + elegido.getPagare(),getApplicationContext());
                    if(cbEsParaDeposito.isChecked()) {
                        Intent depositoCuenta = new Intent(ConsultaCuenta.this, DepositoCuenta.class);
                        depositoCuenta.putExtra("usuario", distriModel.getUsuario());
                        depositoCuenta.putExtra("codigo", elegido.getCodigo());
                        depositoCuenta.putExtra("fecha", distriModel.getFecha());
                        depositoCuenta.putExtra("esLocal", distriModel.getLocal());
                        depositoCuenta.putExtra("enLinea", distriModel.getLinea());
                        depositoCuenta.putExtra("numeroCliente", Integer.toString(cliente.getNumeroCliente()));
                        depositoCuenta.putExtra("impresora", impresora.getNombreImpresora());
                        finish();
                        startActivity(depositoCuenta);
                    }
                    else
                    {
                        //envia la propiedad del objeto
                        Intent consultaTransaccionesCuenta = new Intent(ConsultaCuenta.this, ConsultaTransaccionesCuenta.class);
                        consultaTransaccionesCuenta.putExtra("usuario", distriModel.getUsuario());
                        consultaTransaccionesCuenta.putExtra("codigo", elegido.getCodigo());
                        consultaTransaccionesCuenta.putExtra("fecha", distriModel.getFecha());
                        consultaTransaccionesCuenta.putExtra("esLocal", distriModel.getLocal());
                        consultaTransaccionesCuenta.putExtra("enLinea", distriModel.getLinea());
                        consultaTransaccionesCuenta.putExtra("numeroCliente", Integer.toString(cliente.getNumeroCliente()));
                        consultaTransaccionesCuenta.putExtra("impresora", impresora.getNombreImpresora());
                        consultaTransaccionesCuenta.putExtra("nombreCliente", cliente.getNombreCliente());
                        consultaTransaccionesCuenta.putExtra("codigoCuenta", cliente.getCodigoCuenta());
                        consultaTransaccionesCuenta.putExtra("producto", elegido.getTipoCuenta());
                        finish();
                        startActivity(consultaTransaccionesCuenta);

                    }

                }
            });
        }
        else {
            utilitario.MostrarMensaje("No existen datos", getApplicationContext());
        }
    }


    public ArrayList<Utilitarios.CuentaLigth> ListaCuentasCliente(){

        ArrayList<Utilitarios.CuentaLigth> lista = new ArrayList<Utilitarios.CuentaLigth>();
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

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlCuentas());

            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                int cuantos = resSoap.getPropertyCount();

                for (int i = 0; i < cuantos; i++) {
                    SoapPrimitive res = (SoapPrimitive) resSoap.getProperty(i);
                    String[] camposCuenta = res.toString().split("_sc_");
                    cliente.setNombreCliente(camposCuenta[0]);
                    cliente.setNumeroCliente(Integer.parseInt(camposCuenta[5]));
                    lista.add(new Utilitarios.CuentaLigth(camposCuenta[1], camposCuenta[2], camposCuenta[3], camposCuenta[4], camposCuenta[5], camposCuenta[6]));
                }
            } catch (Exception e) {
                lista.add(new Utilitarios.CuentaLigth(e.getMessage(), "", "", "","",""));
            }
        }
        return lista;
    }

    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarListaCuentas extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean respuesta = false;
            try
            {
                if(ListaCuentasCliente().size()>0) {
                    listaCuentas = ListaCuentasCliente();
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
            if (!result)
                utilitario.MostrarMensaje("Se produjo un error al obtener el listado de Cuentas", ConsultaCuenta.this);
            else
            {
                if(listaCuentas.size()>0) {
                    lblNombreCliente = (TextView) findViewById(R.id.lblNombreCliente);
                    lblNombreCliente.setText("Nombre: " + cliente.getNombreCliente());
                    lstvCuentas = (ListView) findViewById(R.id.lstvCuentas);
                    adaptador = new ListaCuenta(ConsultaCuenta.this, listaCuentas);
                    lstvCuentas.setAdapter(adaptador);
                }
                else {
                    utilitario.MostrarMensaje("Cliente no tiene cuentas activas",ConsultaCuenta.this);
                }
            }
        }
    }

}



