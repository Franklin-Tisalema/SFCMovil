package com.chibuleo.sfcmovil;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
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

public class ConsultaTransacciones extends AppCompatActivity {

    ProgressDialog pDialog;
    Parametros parametro = new Parametros();
    Utilitarios utilitario;

    private TextView lblUsuario;
    private TextView lblFecha;
    private Button btnConsultar;

    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Recibo recib; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Cliente cliente; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Recibo.Transaccion trans;
    /*
    private String usuario;
    private String fecha;
    private Boolean esLocal;
    private Boolean enLinea;
    private String nombreImpresora;
    private String nombreRecibo;
    private String documentoRecibo;
    private String datosRecibo;
    private String montoRecibo;
    private String codigoRecibo;
    private String estadoPago;
    private String tipoTransaccion;
    private String producto;
    private String numeroCliente;
    private String datosEnvioImpresora;
    */

    private ImageView logo;

    private RadioButton rbEnLinea;
    private RadioButton rbDesconectado;
    private RadioButton rbTodos;

    private ListView lstvTransacciones;
    ArrayAdapter adaptador;

    private String opcion;

    ArrayList<Transaccion> listaTransacciones = new ArrayList<Transaccion>();
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

   /* @Override
    public void onPause()   {
        super.onPause();
        ConsultaTransacciones.this.finish();
    }
    @Override
    public void onStop()   {
        super.onStop();
        ConsultaTransacciones.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(ConsultaTransacciones.this,Distribuidor.class);
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
        setContentView(R.layout.activity_consulta_transacciones);
        distriModel = new DistribuidorM();
        impresora = new Impresora();
        recib = new Recibo();
        trans = recib.new Transaccion();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        btnConsultar =  (Button) findViewById(R.id.btnConsultar);

        lstvTransacciones = (ListView) findViewById(R.id.lstvTransacciones);

        logo = (ImageView) findViewById(R.id.imageView);

        rbEnLinea = (RadioButton) findViewById(R.id.rbEnLinea);
        rbDesconectado = (RadioButton) findViewById(R.id.rbDesconectado);
        rbTodos = (RadioButton) findViewById(R.id.rbTodos);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(ConsultaTransacciones.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", ConsultaTransacciones.this);
            Intent principal = new Intent(ConsultaTransacciones.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(ConsultaTransacciones.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null){
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));

            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());

            if(!distriModel.getLinea())
                logo.setImageResource(R.mipmap.logooff);
            btnConsultar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        try {
                            adaptador.clear();
                        }
                        catch (Exception error){
                            //utilitario.MostrarMensaje(error.getMessage(),ConsultaPrestamo.this);
                        }

                        if(rbEnLinea.isChecked()) opcion = " where estado='I'";
                        if(rbDesconectado.isChecked()) opcion = " where estado='O'";
                        if(rbTodos.isChecked())  opcion = " ";

                        pDialog = new ProgressDialog(ConsultaTransacciones.this);
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.setCancelable(true);
                        pDialog.setMax(100);
                        pDialog.setProgress(0);

                        CargarListaTransacciones tarea = new CargarListaTransacciones();
                        tarea.execute();
                        pDialog = ProgressDialog.show(ConsultaTransacciones.this, "Por favor espere", "Cargando Lista de Transacciones Realizadas", true, false);
                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaTransacciones.this);
                    }
                }
            });

            lstvTransacciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ConsultaTransacciones.Transaccion elegido = (ConsultaTransacciones.Transaccion) parent.getItemAtPosition(position);

                    recib.setNombreRecibo(elegido.getNombre());
                    recib.setDocumentoRecibo(elegido.getDocumento());
                    recib.setMontoRecibo(elegido.getValor());
                    recib.setCodigoRecibo(elegido.getCodigo());
                    recib.setEstadoPago(elegido.getEstado());
                    trans.setTipoTransaccion(elegido.getTipoTransaccion());
                    recib.setProducto(elegido.getProducto());
                    cliente.setNumeroCliente(Integer.parseInt(elegido.getCliente()));

                    //utilitario.MostrarMensaje("Selecciono el prestamo " + elegido.getPagare(),getApplicationContext());
                    /*Intent pagoPrestamo = new Intent(ConsultaPagos.this,PagoPrestamo.class);
                    pagoPrestamo.putExtra("usuario",usuario);
                    pagoPrestamo.putExtra("pagare", elegido.getPagare());
                    pagoPrestamo.putExtra("fecha", fecha);
                    pagoPrestamo.putExtra("esLocal", esLocal);

                    startActivity(pagoPrestamo);*/

                    try {
                            if(trans.getTipoTransaccion().trim().toUpperCase().equals("P")) {
                                if ((distriModel.getLinea() && recib.getEstadoPago().trim().toUpperCase().equals("I")) || recib.getEstadoPago().trim().toUpperCase().equals("O")) {
                                    parametro.DefinirMetodo("DatosImprimeReciboPrestamo", distriModel.getLocal(),ConsultaTransacciones.this);

                                    pDialog = new ProgressDialog(getApplicationContext());
                                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    pDialog.setCancelable(true);
                                    pDialog.setMax(100);
                                    pDialog.setProgress(0);

                                    ImprimeRecibo tareaImprime = new ImprimeRecibo();
                                    tareaImprime.execute();
                                    pDialog = ProgressDialog.show(ConsultaTransacciones.this, "Por favor espere", "Imprimiendo...", true, false);
                                } else {
                                    utilitario.MostrarMensaje("Solo se puede reimprimir cuando esta en línea", ConsultaTransacciones.this);
                                }
                            }
                            else
                            {
                                parametro.DefinirMetodo("DatosImprimeReciboCuenta", distriModel.getLocal(),getApplicationContext());

                                pDialog = new ProgressDialog(getApplicationContext());
                                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                pDialog.setCancelable(true);
                                pDialog.setMax(100);
                                pDialog.setProgress(0);

                                ImprimeRecibo tareaImprime = new ImprimeRecibo();
                                tareaImprime.execute();
                                pDialog = ProgressDialog.show(ConsultaTransacciones.this, "Por favor espere", "Imprimiendo...", true, false);
                            }


                    } catch (Exception error) {
                        utilitario.MostrarMensaje(error.getMessage(), ConsultaTransacciones.this);
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


    public ArrayList<Transaccion> ListaTransacciones(){

        ArrayList<Transaccion> lista = new ArrayList<Transaccion>();

        try
        {
            BaseDatos baseDatos = new BaseDatos(ConsultaTransacciones.this,BaseDatos.DB_NAME,null,BaseDatos.v_db);
            SQLiteDatabase db = baseDatos.getReadableDatabase();

            String script = "select cliente, nombre, codigo, producto, valor, documento, fecha, estado, tipoTransaccion from TransaccionesMovil " + opcion;

            Cursor tabla = db.rawQuery(script,null);
                if (tabla.moveToFirst())
                {
                    do {
                        String cliente = tabla.getString(tabla.getColumnIndex("cliente"));
                        String nombre = tabla.getString(tabla.getColumnIndex("nombre"));
                        String codigo = tabla.getString(tabla.getColumnIndex("codigo"));
                        String producto = tabla.getString(tabla.getColumnIndex("producto"));
                        String valor = tabla.getString(tabla.getColumnIndex("valor"));
                        String documento = tabla.getString(tabla.getColumnIndex("documento"));
                        String fecha = tabla.getString(tabla.getColumnIndex("fecha"));
                        String estado = tabla.getString(tabla.getColumnIndex("estado"));
                        String tipoTransaccion = tabla.getString(tabla.getColumnIndex("tipoTransaccion"));
                        lista.add(new Transaccion(cliente,nombre,codigo,producto,valor,documento,fecha,estado,tipoTransaccion));
                    } while (tabla.moveToNext());
                }
             db.close();
         }
        catch (Exception e)
        {
            lista.add(new Transaccion(e.getMessage(),"","","","","","","",""));
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
                    listaTransacciones = ListaTransacciones();
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
                utilitario.MostrarMensaje("Se produjo un error al obtener el listado de transacciones", ConsultaTransacciones.this);
            }
            else
            {
                if(listaTransacciones.size()>0) {
                    lstvTransacciones = (ListView) findViewById(R.id.lstvTransacciones);
                    adaptador = new ListaTransaccion(ConsultaTransacciones.this, listaTransacciones);
                    lstvTransacciones.setAdapter(adaptador);
                }
                else {
                    utilitario.MostrarMensaje("No existen transacciones registradas",ConsultaTransacciones.this);
                }
            }
        }
    }

    public static class Transaccion{
        private String cliente;
        private String nombre;
        private String codigo;
        private String producto;
        private String valor;
        private String documento;
        private String fecha;
        private String estado;
        private String tipoTransaccion;

        public String getCliente() { return this.cliente; }
        public String getNombre() { return this.nombre; }
        public String getDocumento() { return this.documento; }
        public String getFecha() { return this.fecha; }
        public String getCodigo() { return this.codigo; }
        public String getProducto()
        {
            return this.producto;
        }
        public String getValor()
        {
            return this.valor;
        }
        public String getEstado() {return this.estado; }
        public String getTipoTransaccion() {return this.tipoTransaccion; }

        Transaccion(String unCliente, String unNombre, String unCodigo, String unProducto, String unValor, String unDocumento,
             String unaFecha, String unEstado, String unTipoTransaccion){
            this.cliente = unCliente;
            this.nombre = unNombre;
            this.codigo = unCodigo;
            this.producto = unProducto;
            this.valor = unValor;
            this.documento = unDocumento;
            this.fecha = unaFecha;
            this.estado = unEstado;
            this.tipoTransaccion=unTipoTransaccion;
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
        if(trans.getTipoTransaccion().trim().toUpperCase().equals("P"))//Para imprimir recibos de Prestamos
        {

            if (recib.getEstadoPago().trim().toUpperCase().equals("I")) {
                SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
                request.addProperty("unNumeroPagare", recib.getCodigoRecibo());
                request.addProperty("unCodigoUsuario", distriModel.getUsuario());

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;

                envelope.setOutputSoapObject(request);

                HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlPrestamos());

                try {
                    SSLConnection.allowAllSSL();
                    transporte.call(parametro.getSoapAction(), envelope);
                    SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta


                    recib.setDatosRecibo("       COAC CHIBULEO LTDA." +
                            "\n\r" +
                            "        PAGO DE PRESTAMO" +
                            "\n\r" +
                            " NOMBRE: " + recib.getNombreRecibo() +
                            "\n\r" +
                            " PAGARÉ: " + recib.getCodigoRecibo() +
                            "\n\r" +
                            " MONTO: " + recib.getMontoRecibo() + " USD" +
                            "\n\r" +
                            " USUARIO: " + distriModel.getUsuario().toUpperCase() +
                            "\n\r" +
                            " DOCUMENTO: " + recib.getDocumentoRecibo() +
                            "\n\r" +
                            " FECHA: " + distriModel.getFecha() + "  " + horaDispositivo + ":" + minutosDispositivo + //Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                            "\n\r" +
                            " P. EFECTIVO: " + recib.getMontoRecibo() + " USD" +
                            "\n\r\n\r" +
                            " SALDO ACTUAL: " + resSoap.getProperty(0).toString() + " USD" +
                            "\n\r" +
                            " F. PROX. PAGO: " + resSoap.getProperty(2).toString() +
                            "\n\r" +
                            " V. PROX. PAGO: " + resSoap.getProperty(1).toString() + " USD" +
                            "\n\r\n\r\n\r" +
                            "       GRACIAS POR SU PAGO" +
                            "\n\r\n\r\n\r");

                    impresora.setNombreImpresora(resSoap.getProperty(3).toString());
                    respuesta = true;
                } catch (Exception error) {
                    respuesta = false;
                }
            } else {
                recib.setNombreRecibo("       COAC CHIBULEO LTDA." +
                        "\n\r" +
                        "        PAGO DE PRESTAMO" +
                        "\n\r" +
                        " NOMBRE: " + recib.getNombreRecibo() +
                        "\n\r" +
                        " PAGARÉ: " + recib.getCodigoRecibo() +
                        "\n\r" +
                        " MONTO: " + recib.getMontoRecibo() + " USD" +
                        "\n\r" +
                        " USUARIO: " + distriModel.getUsuario().toUpperCase() +
                        "\n\r" +
                        " DOCUMENTO: " + recib.getDocumentoRecibo() +
                        "\n\r" +
                        " FECHA: " + distriModel.getFecha() + "  " + horaDispositivo + ":" + minutosDispositivo + //+ Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                        "\n\r" +
                        " P. EFECTIVO: " + recib.getMontoRecibo() + " USD" +
                        "\n\r\n\r\n\r" +
                        "       GRACIAS POR SU PAGO" +
                        "\n\r\n\r\n\r");

                respuesta = true;
            }
        }//Para imprimir recibos de Prestamos
        else //Para imprimir recibos de Depositos
        {
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoCuenta", recib.getCodigoRecibo());
            request.addProperty("unNumeroDocumento", recib.getDocumentoRecibo());
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
                        " DOCUMENTO: " + recib.getDocumentoRecibo() +
                        "\n\r" +
                        " FECHA: " + distriModel.getFecha() + "  " + horaDispositivo + ":" + minutosDispositivo + //Integer.toString(calendario.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendario.get(Calendar.MINUTE)) +
                        "\n\r" +
                        " VALOR: " + recib.getMontoRecibo() + " USD" +
                        "\n\r" +
                        " SALDO: " + resSoap.getProperty(0).toString() + " USD" +
                        "\n\r\n\r\n\r" +
                        "     ----------------------" +
                        "\n\r" +
                        "       FIRMA DEL CLIENTE" +
                        "\n\r\n\r" +
                        "    GRACIAS POR SU DEPOSITO" +
                        "\n\r\n\r\n\r");

                impresora.setNombreImpresora(resSoap.getProperty(2).toString());

                respuesta = true;
            } catch (Exception error) {
                respuesta = false;
            }
        }//Para imprimir recibos de Depositos
        return respuesta;
    }

    private class ImprimeRecibo extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean respuesta = true;
            try
            {
                if(!ObtieneDatosRecibo()) respuesta=false;
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
            if (!result)  utilitario.MostrarMensaje("Error al imprimir recibo", ConsultaTransacciones.this);
            else
            {
                // open bluetooth connection
                if(BuscarImpresora(impresora.getNombreImpresora()))
                {
                    try {
                        AbrirConeccionBluetooth();

                        // send data typed by the user to be printed
                        if(ImprimirRecibo(recib.getDatosRecibo())) utilitario.MostrarMensaje("Recibo Impreso con Éxito",ConsultaTransacciones.this);
                        else utilitario.MostrarMensaje("Transacción Exitosa sin Impresión de Recibo",ConsultaTransacciones.this);
                    } catch (IOException e) {
                        utilitario.MostrarMensaje("Error al Imprimir Recibo " + e.getMessage(),ConsultaTransacciones.this);
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
            utilitario.MostrarMensaje(e.getMessage(), ConsultaTransacciones.this);
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
            utilitario.MostrarMensaje("Abrir Conexion " +  e.getMessage(), ConsultaTransacciones.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Abrir Conexion " + e.getMessage(),ConsultaTransacciones.this);
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
            utilitario.MostrarMensaje("Begin Data " + e.getMessage(), ConsultaTransacciones.this);

        }
    }

    void CerrarConexion() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.flush();
            mmOutputStream.close();
            //mmInputStream.reset();
            mmInputStream.close();
            mmSocket.close();
        } catch (NullPointerException e) {
            utilitario.MostrarMensaje("Cerrar Conexion  " +  e.getMessage(), ConsultaTransacciones.this);

        } catch (Exception e) {
            utilitario.MostrarMensaje("Cerrar Conexion " + e.getMessage(),ConsultaTransacciones.this);

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
            utilitario.MostrarMensaje(" Imprimir Recibo " + e.getMessage(), ConsultaTransacciones.this);
        }
        return resp;
    }

    // Close the connection to bluetooth printer.

}
