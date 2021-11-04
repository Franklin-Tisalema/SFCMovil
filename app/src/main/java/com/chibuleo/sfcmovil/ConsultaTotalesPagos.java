package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;
import com.chibuleo.sfcmovil.model.Recibo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.UUID;

public class ConsultaTotalesPagos extends AppCompatActivity {

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    private ProgressDialog pDialog;
    private Parametros parametro = new Parametros();
    private Utilitarios utilitario;
    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Recibo recib; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    /*
    private String usuario;
    private String fecha;
    private Boolean esLocal;
    private Boolean enLinea;
    private String nombreImpresora;
    */
    private ImageView logo;

    //String datosRecibo;
    private TextView lblUsuario;
    private TextView lblFecha;
    private TextView txtNumero;
    private TextView txtValor;
    private TextView txtNumeroI;
    private TextView txtValorI;
    private TextView txtNumeroO;
    private TextView txtValorO;
    private TextView txtEstado;
    //private String datosEnvioImpresora;
    private Button btnImprimeCobrosTotales;

    String numero="0";
    String valor="0 USD";
    String numeroI="0";
    String valorI="0 USD";
    String numeroO="0";
    String valorO="0 USD";
   /* @Override
    public void onPause()   {
        super.onPause();
        ConsultaTotalesPagos.this.finish();
    }
    @Override
    public void onStop()   {
        super.onStop();
        ConsultaTotalesPagos.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(ConsultaTotalesPagos.this,Distribuidor.class);
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
        setContentView(R.layout.activity_consulta_totales_pagos);
        distriModel = new DistribuidorM();
        impresora = new Impresora();
        recib = new Recibo();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);

        btnImprimeCobrosTotales =  (Button) findViewById(R.id.btnImprimeCobrosTotales);
        btnImprimeCobrosTotales.setEnabled(true);

        txtNumero = (TextView) findViewById(R.id.txtNumero);
        txtValor= (TextView) findViewById(R.id.txtValor);
        txtNumeroI = (TextView) findViewById(R.id.txtNumeroI);
        txtValorI= (TextView) findViewById(R.id.txtValorI);
        txtNumeroO = (TextView) findViewById(R.id.txtNumeroO);
        txtValorO= (TextView) findViewById(R.id.txtValorO);

        logo = (ImageView) findViewById(R.id.imageView);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(ConsultaTotalesPagos.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", ConsultaTotalesPagos.this);
            Intent principal = new Intent(ConsultaTotalesPagos.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(ConsultaTotalesPagos.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null){
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));

            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());

            if(!distriModel.getLinea()) {
                logo.setImageResource(R.mipmap.logooff);
            }

            try {
                pDialog = new ProgressDialog(ConsultaTotalesPagos.this);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setCancelable(true);
                pDialog.setMax(100);
                pDialog.setProgress(0);

                CargarTotales tarea = new CargarTotales();
                tarea.execute();
                pDialog = ProgressDialog.show(ConsultaTotalesPagos.this, "Por favor espere", "Cargando Totales", true, false);
                btnImprimeCobrosTotales.setEnabled(true);

            } catch (Exception error) {
                btnImprimeCobrosTotales.setEnabled(false);
                utilitario.MostrarMensaje(error.getMessage(), ConsultaTotalesPagos.this);
            }
        }
        else {
            btnImprimeCobrosTotales.setEnabled(false);
            utilitario.MostrarMensaje("No existen datos", getApplicationContext());
        }

        btnImprimeCobrosTotales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pDialog = new ProgressDialog(getApplicationContext());
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.setCancelable(true);
                    pDialog.setMax(100);
                    pDialog.setProgress(0);

                    //datosEnvioImpresora="";
                    DatosRecibo tareaImprime = new DatosRecibo();
                    tareaImprime.execute();
                    pDialog = ProgressDialog.show(ConsultaTotalesPagos.this, "Por favor espere", "Imprimiendo...", true, false);
                } catch (Exception error) {
                    utilitario.MostrarMensaje(error.getMessage(), ConsultaTotalesPagos.this);
                }

            }
        });

    }

    private Boolean MostrarTotales()
    {
        Boolean respuesta= false;
        try
        {
            BaseDatos baseDatos = new BaseDatos(ConsultaTotalesPagos.this,BaseDatos.DB_NAME,null,BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getReadableDatabase();

            String script = "select  ifnull(COUNT(*),0) as CUANTOS, ifnull(SUM(CAST(valor as REAL)),0) as VALOR from TransaccionesMovil where estado='I'";

            Cursor tabla = db.rawQuery(script, null);
            if (tabla.moveToFirst())
            {
                do {
                    numeroI = tabla.getString(tabla.getColumnIndex("CUANTOS"));
                    valorI = tabla.getString(tabla.getColumnIndex("VALOR")) + " USD";
                } while (tabla.moveToNext());
            }

            script = "select ifnull(COUNT(*),0) as CUANTOS, ifnull(SUM(CAST(valor as REAL)),0) as VALOR from TransaccionesMovil where estado='O'";

            tabla = db.rawQuery(script,null);
            if (tabla.moveToFirst())
            {
                do {
                    numeroO = tabla.getString(tabla.getColumnIndex("CUANTOS"));
                    valorO = tabla.getString(tabla.getColumnIndex("VALOR")) + " USD";
                } while (tabla.moveToNext());
            }

            script = "select ifnull(COUNT(*),0) as CUANTOS, ifnull(SUM(CAST(valor as REAL)),0) as VALOR from TransaccionesMovil";

            tabla = db.rawQuery(script,null);
            if (tabla.moveToFirst())
            {
                do {
                    numero = tabla.getString(tabla.getColumnIndex("CUANTOS"));
                    valor = tabla.getString(tabla.getColumnIndex("VALOR")) + " USD";
                } while (tabla.moveToNext());
            }
            db.close();
            respuesta=true;
        }
        catch(Exception error)
        {
            respuesta =  false;
        }
        return respuesta;
    }

    private class DatosRecibo extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean respuesta = true;
            Calendar calendario = new GregorianCalendar();
            String horaDispositivo="";
            String minutosDispositivo="";
            horaDispositivo = Integer.toString(calendario.get(Calendar.HOUR_OF_DAY));
            minutosDispositivo = Integer.toString(calendario.get(Calendar.MINUTE));
            try
            {
                recib.setDatosRecibo(
                        "       COAC CHIBULEO LTDA." +
                        "\n\r" +
                        "COMPROBANTE DE RECAUDACION TOTAL" +
                        "\n\r" +
                        " RECAUDADOR: " + distriModel.getUsuario().toUpperCase() +
                        "\n\r" +
                        " FECHA: " + distriModel.getFecha() + "  " + horaDispositivo + ":" + minutosDispositivo +
                        "\n\r" +
                        " TOTAL TRANSACCIONES: " + numero +
                        "\n\r" +
                        " MONTO TOTAL: " + valor +
                        "\n\r\n\r\n\r" +
                        "       -------------------" +
                        "\n\r" +
                        "             FIRMA" +
                        "\n\r\n\r\n\r");

                    respuesta=true;
            }
            catch (Exception e)
            {
                respuesta=false;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (!result) {
                utilitario.MostrarMensaje("Error al Generar Recibo de Totales Recaudados", ConsultaTotalesPagos.this);
            }
            else
            {
                // open bluetooth connection
                if(BuscarImpresora(impresora.getNombreImpresora()))
                {
                    try {
                        AbrirConeccionBluetooth();

                        // send data typed by the user to be printed
                        if(ImprimirRecibo(recib.getDatosRecibo()))
                        {
                            utilitario.MostrarMensaje("Comprobante Impreso con Éxito",ConsultaTotalesPagos.this);
                        }
                        else
                        {
                            utilitario.MostrarMensaje("Error al Imprimir Comprobante",ConsultaTotalesPagos.this);
                        }
                    } catch (IOException e) {
                        utilitario.MostrarMensaje("Error al Imprimir Comprobante " + e.getMessage(),ConsultaTotalesPagos.this);
                    }

                    try {
                        CerrarConexion();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }
    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarTotales extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = false;
            try
            {
                if(MostrarTotales()) {
                    respuesta = true;
                }
                else
                {
                    respuesta=false;
                }
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
                utilitario.MostrarMensaje("Se produjo un error al obtener el listado de transacciones",
                        ConsultaTotalesPagos.this);
            }
            else
            {
                txtNumeroI.setText(numeroI);
                txtValorI.setText(valorI);
                txtNumeroO.setText(numeroO);
                txtValorO.setText(valorO);
                txtNumero.setText(numero);
                txtValor.setText(valor);

                utilitario.MostrarMensajeCorto("Datos Obtenidos con Éxito", ConsultaTotalesPagos.this);
            }
        }
    }
    // This will find a bluetooth printer device
    public Boolean BuscarImpresora(String unNombreImpresora) {
        Boolean resp = false;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                throw new Exception("Adaptador Bluetooth no Habilitado");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().trim().toUpperCase().equals(unNombreImpresora.trim().toUpperCase())) {
                        mmDevice = device;
                        resp=true;
                        break;
                    }
                }
            }
            if(mmDevice==null)
                throw new Exception("Impresora no Encontrada");
        } catch (Exception e) {
            utilitario.MostrarMensaje(e.getMessage(), ConsultaTotalesPagos.this);
            resp=false;

        }
        return resp;
    }

    // Tries to open a connection to the bluetooth printer device
    public void AbrirConeccionBluetooth() throws IOException {
        Boolean resp = false;
        try {
            // Standard SerialPortService ID
            mmOutputStream=null;
            mmInputStream=null;
            if (mmDevice != null) {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();

                mmSocket.connect();

                Thread.sleep(1000);
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                beginListenForData();
                resp = true;
                //("Bluetooth Opened");
            } else {
                resp = false;
            }
        } catch (NullPointerException e) {
            utilitario.MostrarMensaje("Abrir Conexion " +  e.getMessage(), ConsultaTotalesPagos.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Abrir Conexion " + e.getMessage(),ConsultaTotalesPagos.this);
        }
    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[2048];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                impresora.setDatosEnvioImpresora(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (Exception e) {
            utilitario.MostrarMensaje("Begin Data  " + e.getMessage(), ConsultaTotalesPagos.this);

        }
    }

    // Close the connection to bluetooth printer.
    void CerrarConexion() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.flush();
            mmOutputStream.close();
            //mmInputStream.reset();
            mmInputStream.close();
            mmSocket.close();
        } catch (NullPointerException e) {
            utilitario.MostrarMensaje("Cerrar Conexion  " +  e.getMessage(), ConsultaTotalesPagos.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Cerrar Conexion " + e.getMessage(),ConsultaTotalesPagos.this);

        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    public Boolean ImprimirRecibo(String unDatoRecibo) {
        Boolean resp = false;
        try {

            // the text typed by the user
            String msg = unDatoRecibo;

            mmOutputStream.write(msg.getBytes());
            mmOutputStream.flush();

            // tell the user data were sent
            resp = true;
        } catch (Exception e) {
            resp=false;
            utilitario.MostrarMensaje(e.getMessage(), ConsultaTotalesPagos.this);
        }
        return resp;
    }
}
