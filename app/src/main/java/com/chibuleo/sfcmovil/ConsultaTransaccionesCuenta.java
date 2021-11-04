package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.Cliente;
import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;
import com.chibuleo.sfcmovil.model.Recibo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.UUID;

public class ConsultaTransaccionesCuenta extends AppCompatActivity {

    ProgressDialog pDialog;
    Parametros parametro = new Parametros();
    Utilitarios utilitario;

    private TextView lblUsuario;
    private TextView lblFecha;
    private TextView txtNombre;
    private TextView txtNumeroCliente;
    private TextView txtCodigo;
    private TextView txtCuenta;
    private String datosEnvioImpresora;
    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Recibo recib; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Recibo.Transaccion trans;
    private Cliente cliente; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    /*
    private String usuario;
    private String fecha;
    private Boolean esLocal;
    private Boolean enLinea;
    private String documentoTransaccion;
    private String valorTransaccion;
    private String codigoCuenta;
    private String producto;
    private String numeroCliente;

     */

    private ImageView logo;

    private ListView lstvTransaccionesCuenta;
    ArrayAdapter adaptador;

    private String opcion;

    ArrayList<Utilitarios.TransaccionCuenta> listaTransaccionesCuenta = new ArrayList<Utilitarios.TransaccionCuenta>();
    /*
    String nombreImpresora;
    String datosRecibo;
    String nombreRecibo;
    */

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

   /* @Override
    public void onPause()   {
        super.onPause();
        ConsultaTransaccionesCuenta.this.finish();
    }
    @Override
    public void onStop()   {
        super.onStop();
        ConsultaTransaccionesCuenta.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(ConsultaTransaccionesCuenta.this,ConsultaCuenta.class);
        distribuidor.putExtra("usuario", distriModel.getUsuario());
        distribuidor.putExtra("fecha", distriModel.getFecha());
        distribuidor.putExtra("esLocal", distriModel.getLocal());
        distribuidor.putExtra("enLinea", distriModel.getLinea());
        distribuidor.putExtra("impresora",impresora.getNombreImpresora());

        finish();
        startActivity(distribuidor);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_transacciones_cuenta);
        cliente = new Cliente();
        recib = new Recibo();
        impresora = new Impresora();
        trans = recib.new Transaccion();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        txtCodigo = (TextView) findViewById(R.id.txtCodigo);
        txtNombre = (TextView) findViewById(R.id.txtNombre);
        txtNumeroCliente = (TextView) findViewById(R.id.txtNumeroCliente);
        txtCuenta = (TextView) findViewById(R.id.txtCuenta);



        lstvTransaccionesCuenta = (ListView) findViewById(R.id.lstvTransaccionesCuenta);

        logo = (ImageView) findViewById(R.id.imageView);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(ConsultaTransaccionesCuenta.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", ConsultaTransaccionesCuenta.this);
            Intent principal = new Intent(ConsultaTransaccionesCuenta.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(ConsultaTransaccionesCuenta.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null){
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));
            recib.setNombreRecibo(datos.getString("nombreCliente"));
            cliente.setCodigoCuenta(datos.getString("codigoCuenta"));
            recib.setProducto(datos.getString("producto"));
            cliente.setNumeroCliente(datos.getInt("numeroCliente"));
            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());

            txtNombre.setText(recib.getNombreRecibo());
            txtCodigo.setText(cliente.getCodigoCuenta());
            txtNumeroCliente.setText(cliente.getNumeroCliente());
            txtCuenta.setText(recib.getProducto());

            if(!distriModel.getLinea()) {
                logo.setImageResource(R.mipmap.logooff);
            }

                    try {
                        try {
                            adaptador.clear();
                        }
                        catch (Exception error){
                            //utilitario.MostrarMensaje(error.getMessage(),ConsultaPrestamo.this);
                        }

                        parametro.DefinirMetodo("ListaTransaccionesCuenta",distriModel.getLocal(),getApplicationContext());
                        pDialog = new ProgressDialog(getApplicationContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.setCancelable(true);
                        pDialog.setMax(100);
                        pDialog.setProgress(0);

                        CargarListaTransacciones tarea = new CargarListaTransacciones();
                        tarea.execute();
                        pDialog = ProgressDialog.show(ConsultaTransaccionesCuenta.this, "Por favor espere", "Cargando Lista de Transacciones", true, false);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaTransaccionesCuenta.this);
                    }


            lstvTransaccionesCuenta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Utilitarios.TransaccionCuenta elegido = (Utilitarios.TransaccionCuenta) parent.getItemAtPosition(position);

                    //fechaTransaccion = elegido.getFecha();
                    trans.setDocumentoTransaccion(elegido.getDocumento());
                    trans.setValorTransaccion(elegido.getValor());
                    //saldoTransaccion = elegido.getSaldo();
                    //usuario = elegido.getUsuario();
                    //tipoTransaccion = elegido.getTransaccion();
                    //oficinaTransaccion = elegido.getOficina();
                    datosEnvioImpresora="";
                    try {
                        parametro.DefinirMetodo("DatosImprimeReciboCuenta",distriModel.getLocal(),getApplicationContext());

                        pDialog = new ProgressDialog(getApplicationContext());
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.setCancelable(true);
                        pDialog.setMax(100);
                        pDialog.setProgress(0);

                        ImprimeRecibo tareaImprime = new ImprimeRecibo();
                        tareaImprime.execute();
                        pDialog = ProgressDialog.show(ConsultaTransaccionesCuenta.this, "Por favor espere", "Imprimiendo...", true, false);
                     } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaTransaccionesCuenta.this);
                    }


                }
            });
        }
        else {
            utilitario.MostrarMensaje("No existen datos", getApplicationContext());
        }
     }


    public ArrayList<Utilitarios.TransaccionCuenta> ListaTransacciones(){

        ArrayList<Utilitarios.TransaccionCuenta> lista = new ArrayList<Utilitarios.TransaccionCuenta>();

        Boolean respuesta = false;

        if(distriModel.getLinea()) {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoCuenta", cliente.getCodigoCuenta());
            request.addProperty("unCodigoUsuario", distriModel.getUsuario());


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlCuentas());

            try {
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                int cuantos = resSoap.getPropertyCount();
                for (int i = 0; i < cuantos; i++) {

                    SoapObject dato = (SoapObject) resSoap.getProperty(i);
                    lista.add( new Utilitarios.TransaccionCuenta(dato.getProperty(0).toString(),
                            dato.getProperty(1).toString(),
                            dato.getProperty(2).toString(),
                            dato.getProperty(3).toString(),
                            dato.getProperty(4).toString(),
                            dato.getProperty(5).toString(),
                            dato.getProperty(6).toString()));



                }
                //SoapPrimitive res = (SoapPrimitive) resSoap.getProperty(0);
                respuesta = true;
            } catch (Exception error) {
                respuesta = false;
                lista.add(new Utilitarios.TransaccionCuenta(error.getMessage(),"","","","","",""));
            }
        }
         return lista;
    }

    //Tarea Asíncrona para llamar al WS de login en segundo plano
    private class CargarListaTransacciones extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = false;
            try
            {
                if(ListaTransacciones().size()>0) {
                    listaTransaccionesCuenta = ListaTransacciones();
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
                utilitario.MostrarMensaje("Se produjo un error al obtener el listado de transacciones", ConsultaTransaccionesCuenta.this);
            }
            else
            {
                if(listaTransaccionesCuenta.size()>0) {
                    lstvTransaccionesCuenta = (ListView) findViewById(R.id.lstvTransaccionesCuenta);
                    adaptador = new ListaTransaccionesCuenta(ConsultaTransaccionesCuenta.this, listaTransaccionesCuenta);
                    lstvTransaccionesCuenta.setAdapter(adaptador);
                }
                else {
                    utilitario.MostrarMensaje("No existen transacciones registradas",ConsultaTransaccionesCuenta.this);
                }
            }
        }
    }

     private Boolean ObtieneDatosRecibo(){
        Boolean respuesta = false;
        Calendar calendario = new GregorianCalendar();
        String horaDispositivo="";
        String minutosDispositivo="";

        horaDispositivo = Integer.toString(calendario.get(Calendar.HOUR_OF_DAY));
        minutosDispositivo = Integer.toString(calendario.get(Calendar.MINUTE));

        if (horaDispositivo.trim().length()<2)
            horaDispositivo="0"+horaDispositivo;

        if (minutosDispositivo.trim().length()<2)
            minutosDispositivo="0"+minutosDispositivo;

            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoCuenta", cliente.getCodigoCuenta());
            request.addProperty("unNumeroDocumento", trans.getDocumentoTransaccion());
            request.addProperty("unCodigoUsuario", distriModel.getUsuario());


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlCuentas());

            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                recib.setDatosRecibo("       COAC CHIBULEO LTDA." +
                        "\n\r" +
                        "      DEPOSITO EN AHORROS" +
                        "\n\r" +
                        " NUMERO: " + cliente.getNumeroCliente() +
                        "\n\r" +
                        " ID: " + resSoap.getProperty(1).toString() +
                        "\n\r" +
                        " NOMBRE: " + recib.getNombreRecibo() +
                        "\n\r" +
                        " CUENTA: " + recib.getProducto() +
                        "\n\r" +
                        " USUARIO: " + distriModel.getUsuario().toUpperCase() +
                        "\n\r" +
                        " DOCUMENTO: " + trans.getDocumentoTransaccion() +
                        "\n\r" +
                        " FECHA: " + distriModel.getFecha() + "  " + horaDispositivo + ":" + minutosDispositivo + //Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                        "\n\r" +
                        " VALOR: " + trans.getValorTransaccion() + " USD" +
                        "\n\r" +
                        " SALDO: " + resSoap.getProperty(0).toString() + " USD" +
                        "\n\r\n\r\n\r" +
                        "     ----------------------" +
                        "\n\r" +
                        "       FIRMA DEL CLIENTE" +
                        "\n\r\n\r" +
                        "    COMPROBANTE REIMPRESO" +
                        "\n\r\n\r\n\r");

                //nombreImpresora = resSoap.getProperty(2).toString();

                respuesta = true;
            } catch (Exception error) {
                respuesta = false;
            }

        return respuesta;
    }

    private class ImprimeRecibo extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean respuesta = true;
            try
            {
                if(!ObtieneDatosRecibo())  respuesta=false;
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
                utilitario.MostrarMensaje("Error al imprimir recibo", ConsultaTransaccionesCuenta.this);
            }
            else
            {
                // open bluetooth connection
                if(BuscarImpresora(impresora.getNombreImpresora()))
                {
                    try {
                        AbrirConeccionBluetooth();

                        // send data typed by the user to be printed
                        if(ImprimirRecibo(recib.getDatosRecibo())) utilitario.MostrarMensaje("Recibo Impreso con Éxito",ConsultaTransaccionesCuenta.this);
                        else utilitario.MostrarMensaje("Transacción Exitosa sin Impresión de Recibo",ConsultaTransaccionesCuenta.this);
                    } catch (IOException e) {
                        utilitario.MostrarMensaje("Error al Imprimir Recibo " + e.getMessage(),ConsultaTransaccionesCuenta.this);
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
            utilitario.MostrarMensaje(e.getMessage(), ConsultaTransaccionesCuenta.this);
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
            utilitario.MostrarMensaje("Abrir Conexion " +  e.getMessage(), ConsultaTransaccionesCuenta.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Abrir Conexion " + e.getMessage(),ConsultaTransaccionesCuenta.this);
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
                                                datosEnvioImpresora = data;
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
            utilitario.MostrarMensaje("Begin Data  " + e.getMessage(), ConsultaTransaccionesCuenta.this);

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
            utilitario.MostrarMensaje("Cerrar Conexion  " +  e.getMessage(), ConsultaTransaccionesCuenta.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Cerrar Conexion " + e.getMessage(),ConsultaTransaccionesCuenta.this);

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
            utilitario.MostrarMensaje(e.getMessage(), ConsultaTransaccionesCuenta.this);
        }
        return resp;
    }

}
