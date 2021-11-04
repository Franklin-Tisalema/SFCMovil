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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chibuleo.sfcmovil.model.Cliente;
import com.chibuleo.sfcmovil.model.DistribuidorM;
import com.chibuleo.sfcmovil.model.Impresora;
import com.chibuleo.sfcmovil.model.Pago;
import com.chibuleo.sfcmovil.model.Recibo;
import com.chibuleo.sfcmovil.model.ValorxPagar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CotizadorDPF extends AppCompatActivity {

    private Utilitarios utilitario;
    private ProgressDialog pDialog;
    private Parametros parametro = new Parametros();

    private Button btnObtenerTasa;
    private Button btnCalcular;
    private Button btnLimpiar;

    private EditText txtMonto;
    private EditText txtPlazo;
    private EditText txtTasa;
    private EditText txtSobreTasa;
    private EditText txtInteres;
    private EditText txtRetencion;
    private EditText txtNetoARecibir;
    private TextView lblInteres;
    private TextView lblRetencion;
    private TextView lblNetoARecibir;
    private CheckBox chkEsIFI;
    private CheckBox chkEsPeriodico;


    private TextView lblUsuario;
    private TextView lblFecha;

    private ImageView logo;

    private DistribuidorM distriModel; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Impresora impresora; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Pago pago; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    private Pago.PagoL pagoL; //una clase modelo en donde se puede llamar los atributos del objeto que se vaya a crear
    /*
    private String usuario;
    private String fecha;
    private Boolean esLocal;
    private Boolean enLinea;
    private String nombreImpresora;

    private String tipoPagoInteres;
    private String monto;
    private String plazo;
    private String tasa;
    private String sobretasa;
    private String interes;
    private String retencion;
    private String netoARecibir;
    private String esIFI;
    private Float montoL;
    private Float tasaL;
    private Float plazoL;
    private Float sobreTasaL;
    private Float interesL;
    private Float retencionL;
    private Float netoARecibirL;
    private Float porcentajeRetiene;
    private boolean seRetiene;
     */

    /*@Override
    public void onPause() {
        super.onPause();
        CotizadorDPF.this.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        CotizadorDPF.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent distribuidor = new Intent(CotizadorDPF.this,Distribuidor.class);
        distribuidor.putExtra("usuario",distriModel.getUsuario());
        distribuidor.putExtra("fecha", distriModel.getFecha());
        distribuidor.putExtra("esLocal", distriModel.getLocal());
        distribuidor.putExtra("enLinea", distriModel.getLinea());
        distribuidor.putExtra("impresora", impresora.getNombreImpresora());

        finish();
        startActivity(distribuidor);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizadordpf);
        distriModel = new DistribuidorM();
        impresora = new Impresora();
        pago = new Pago();
        pagoL = pago.new PagoL();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lblUsuario = (TextView) findViewById(R.id.lblUsuario);
        lblFecha = (TextView) findViewById(R.id.lblFecha);
        logo = (ImageView) findViewById(R.id.imageView);

        btnCalcular = (Button) findViewById(R.id.btnCalcular);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        btnObtenerTasa = (Button) findViewById(R.id.btnObtenerTasa);
        txtMonto = (EditText) findViewById(R.id.txtMonto);
        txtPlazo = (EditText) findViewById(R.id.txtPlazo);
        txtTasa = (EditText) findViewById(R.id.txtTasa);
        txtSobreTasa = (EditText) findViewById(R.id.txtSobreTasa);
        txtInteres = (EditText) findViewById(R.id.txtInteres);
        txtRetencion = (EditText) findViewById(R.id.txtRetencion);
        txtNetoARecibir = (EditText) findViewById(R.id.txtNetoARecibir);
        lblInteres = (TextView) findViewById(R.id.lblInteres);
        lblRetencion = (TextView) findViewById(R.id.lblRetencion);
        lblNetoARecibir = (TextView) findViewById(R.id.lblNetoARecibir);
        chkEsIFI = (CheckBox) findViewById(R.id.chkEsIFI);
        chkEsPeriodico = (CheckBox) findViewById(R.id.chkPeriodico);
        //Valida caducidad de la sesion
        if(!utilitario.ValidaCaducidadSesion(CotizadorDPF.this))
        {
            utilitario.MostrarMensaje("Su sesion ha expirado, por favor ingrese nuevamente", CotizadorDPF.this);
            Intent principal = new Intent(CotizadorDPF.this, MainActivity.class);
            finish();
            startActivity(principal);
        }
        utilitario.RegistroUltimoAcceso(CotizadorDPF.this);
        Bundle datos = this.getIntent().getExtras();
        if(datos!=null) {
            distriModel.setUsuario(datos.getString("usuario"));
            distriModel.setFecha(datos.getString("fecha"));
            distriModel.setLocal(datos.getBoolean("esLocal"));
            distriModel.setLinea(datos.getBoolean("enLinea"));
            impresora.setNombreImpresora(datos.getString("impresora"));

            if(!distriModel.getLinea()) {
               logo.setImageResource(R.mipmap.logooff);
                txtTasa.setEnabled(true);
                //txtTasa.setText("0.0");
                btnObtenerTasa.setEnabled(false);
            }
            else {
                txtTasa.setEnabled(false);
                //txtTasa.setText("0.0");
                btnObtenerTasa.setEnabled(true);
            }
            /*
            monto ="";
            plazo="";
            tasa="";
            sobretasa="";
            tasa="";
            netoARecibir="";
            retencion="";
            interes="";
            tipoPagoInteres="001";
             */

            chkEsIFI.setChecked(false);
            chkEsPeriodico.setChecked(false);
            lblNetoARecibir.setVisibility(View.INVISIBLE);
            lblInteres.setVisibility(View.INVISIBLE);
            lblRetencion.setVisibility(View.INVISIBLE);
            txtInteres.setVisibility(View.INVISIBLE);
            txtRetencion.setVisibility(View.INVISIBLE);
            txtNetoARecibir.setVisibility(View.INVISIBLE);

            txtMonto.requestFocus();

            //txtSobreTasa.setText("0");

            txtNetoARecibir.setEnabled(false);
            txtRetencion.setEnabled(false);
            txtInteres.setEnabled(false);

            lblUsuario.setText(distriModel.getUsuario().toUpperCase());
            lblFecha.setText(distriModel.getFecha());

            btnLimpiar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chkEsIFI.setChecked(false);
                    chkEsPeriodico.setChecked(false);
                    lblNetoARecibir.setVisibility(View.INVISIBLE);
                    lblInteres.setVisibility(View.INVISIBLE);
                    lblRetencion.setVisibility(View.INVISIBLE);
                    txtInteres.setVisibility(View.INVISIBLE);
                    txtRetencion.setVisibility(View.INVISIBLE);
                    txtNetoARecibir.setVisibility(View.INVISIBLE);

                    txtMonto.setText("");
                    txtPlazo.setText("");
                    txtSobreTasa.setText("");
                    txtTasa.setText("");
                    txtInteres.setText("");
                    txtRetencion.setText("");
                    txtNetoARecibir.setText("");

                    txtMonto.requestFocus();

                    //txtSobreTasa.setText("0");

                    txtNetoARecibir.setEnabled(false);
                    txtRetencion.setEnabled(false);
                    txtInteres.setEnabled(false);
                }
            });

             btnObtenerTasa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        pago.setMonto(txtMonto.getText().toString());
                        pago.setPlazo(txtPlazo.getText().toString());

                        if(pago.getMonto()!="" && pago.getPlazo()!="") {
                            if(chkEsPeriodico.isChecked()) pago.setTipoPagoInteres("002");
                            else pago.setTipoPagoInteres("001");
                            if (distriModel.getLinea()) {
                                pDialog = new ProgressDialog(getApplicationContext());
                                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                pDialog.setCancelable(true);
                                pDialog.setMax(100);
                                pDialog.setProgress(0);

                                ObtenerTasa tarea = new ObtenerTasa();
                                tarea.execute();
                                pDialog = ProgressDialog.show(CotizadorDPF.this, "Por favor espere", "Obteniendo tasa de interés", true, false);
                            } else {
                                txtTasa.setEnabled(true);
                            }
                        }
                        else
                        {
                            utilitario.MostrarMensaje("Ingrese monto y plazo", CotizadorDPF.this);
                        }

                    }
                    catch(Exception error){
                        utilitario.MostrarMensaje(error.getMessage(), CotizadorDPF.this);
                    }

                }
            });

            btnCalcular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        pago.setMonto(txtMonto.getText().toString());
                        pago.setPlazo(txtPlazo.getText().toString());
                        pago.setTasa(txtTasa.getText().toString());
                        pago.setSobretasa(txtSobreTasa.getText().toString());
                        if(pago.getSobretasa().trim()=="") pago.setSobretasa("0");
                        String esIFI = chkEsIFI.isChecked() ? "1":"0";
                        pago.setEsIFI(esIFI);
                        if(pago.getMonto().trim()!="" && pago.getPlazo().trim()!="" &&
                                pago.getTasa().trim()!=""&& pago.getSobretasa().trim()!="")
                        {
                            if(chkEsPeriodico.isChecked()) pago.setTipoPagoInteres("002");
                            else pago.setTipoPagoInteres("001");
                            if (distriModel.getLinea()) {
                                pDialog = new ProgressDialog(getApplicationContext());
                                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                pDialog.setCancelable(true);
                                pDialog.setMax(100);
                                pDialog.setProgress(0);

                                ObtenerDatos tarea = new ObtenerDatos();
                                tarea.execute();
                                pDialog = ProgressDialog.show(CotizadorDPF.this, "Por favor espere", "Calculando valores", true, false);
                            } else {
                                txtTasa.setEnabled(true);
                                pagoL.setTasaL(Float.parseFloat(txtTasa.getText().toString()));
                                pagoL.setSobreTasaL(Float.parseFloat(txtSobreTasa.getText().toString()));
                                pagoL.setMontoL(Float.parseFloat(txtMonto.getText().toString()));
                                pagoL.setPlazoL(Float.parseFloat(txtPlazo.getText().toString()));
                                if(chkEsPeriodico.isChecked() || pagoL.getPlazoL()<360) {
                                    pagoL.setSeRetiene(true);
                                    if(chkEsIFI.isChecked()) pagoL.setPorcentajeRetiene(Float.parseFloat("1"));
                                    else pagoL.setPorcentajeRetiene(Float.parseFloat("2"));
                                }
                                else pagoL.setSeRetiene(false);
                                float interL = (pagoL.getMontoL()*(pagoL.getTasaL()+pagoL.getSobreTasaL())*pagoL.getPlazoL())/36500;
                                pagoL.setInteresL(interL);
                                float retencionL = 0.0F, netoARecibirL = 0.0F;
                                //interesL=(montoL*(tasaL+sobreTasaL)*plazoL)/36500;
                                if(!pagoL.isSeRetiene()) {
                                    netoARecibirL = pagoL.getMontoL() + pagoL.getInteresL();
                                    pagoL.setNetoARecibirL(netoARecibirL);
                                    //netoARecibirL = montoL + interesL;
                                    retencionL=Float.parseFloat("0.0");
                                    pagoL.setRetencionL(retencionL);
                                }
                                else{
                                    retencionL = pagoL.getInteresL()*pagoL.getPorcentajeRetiene()/100;
                                    //retencionL =  interesL*porcentajeRetiene/100;
                                    pagoL.setRetencionL(retencionL);
                                    netoARecibirL = pagoL.getMontoL() + pagoL.getInteresL()-retencionL;
                                    //netoARecibirL = montoL + interesL-retencionL;
                                    pagoL.setNetoARecibirL(netoARecibirL);
                                }

                                lblNetoARecibir.setVisibility(View.VISIBLE);
                                lblInteres.setVisibility(View.VISIBLE);
                                lblRetencion.setVisibility(View.VISIBLE);
                                txtInteres.setVisibility(View.VISIBLE);
                                txtRetencion.setVisibility(View.VISIBLE);
                                txtNetoARecibir.setVisibility(View.VISIBLE);

                                txtInteres.setText(String.format("%.2f",pagoL.getInteresL()));
                                txtRetencion.setText(String.format("%.2f",retencionL));
                                txtNetoARecibir.setText(String.format("%.2f",netoARecibirL));

                            }
                        }
                        else utilitario.MostrarMensaje("Ingrese monto, plazo, tasa y sobre tasa para poder calcular", CotizadorDPF.this);
                    }
                    catch(Exception error){
                        utilitario.MostrarMensaje(error.getMessage(), CotizadorDPF.this);
                    }

                }
            });
        }
        else {
            utilitario.MostrarMensaje("Error al obtener datos", CotizadorDPF.this);
            btnCalcular.setEnabled(false);
            btnObtenerTasa.setEnabled(false);
        }
    }
     private Boolean ObtenerTasaPorMontoPlazo(){
        Boolean respuesta = false;
        BaseDatos baseDatos = new BaseDatos(CotizadorDPF.this, BaseDatos.DB_NAME, null, BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String elSql="";
        if(distriModel.getLinea()) {
            parametro.DefinirMetodo("TasaInteres", distriModel.getLocal(),getApplicationContext());
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoUsuario", distriModel.getUsuario());
            request.addProperty("monto", pago.getMonto());
            request.addProperty("plazoDias", pago.getPlazo());
            request.addProperty("codigoTipoDeposito", pago.getTipoPagoInteres());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlDPF());
            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta
                Utilitarios.DatosCotizadorDPF respuestaCotizador = new Utilitarios.DatosCotizadorDPF(resSoap);
                pago.setTasa(respuestaCotizador.getInteres());
                if(pago.getTasa()!="0") respuesta=true;
            } catch (Exception e) {
                respuesta=false;
            }
        }
         db.close();
         pago.setTasa(pago.getTasa().trim().replace(",","."));
         return  respuesta;
    }

    private class ObtenerTasa extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean respuesta = true;
            try {
                if (!ObtenerTasaPorMontoPlazo())  respuesta = false;
            } catch (Exception e) {
                respuesta = false;
            }
            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            try {
                if (!result)
                    utilitario.MostrarMensaje("No se pudo obtener tasa de interés con los datos ingresados", CotizadorDPF.this);
                else {
                    txtTasa.setText(pago.getTasa());
                    utilitario.MostrarMensaje("Tasa de interes " + pago.getTasa() + " %", CotizadorDPF.this);
                }
            } catch (Exception error) {
                utilitario.MostrarMensaje(error.getMessage(), CotizadorDPF.this);
            }
        }
    }

    private Boolean ObtenerDatosCotizadorDPF(){
        Boolean respuesta = false;
        BaseDatos baseDatos = new BaseDatos(CotizadorDPF.this, BaseDatos.DB_NAME, null, BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String elSql="";
        if(distriModel.getLinea()) {
            parametro.DefinirMetodo("ValoresCotizadorDPF", distriModel.getLocal(),getApplicationContext());
            SoapObject request = new SoapObject(parametro.getNamespace(), parametro.getMethodName());
            request.addProperty("unCodigoUsuario", distriModel.getUsuario());
            request.addProperty("tasa", pago.getTasa().replace(".",","));
            request.addProperty("variacionTasa", pago.getSobretasa().replace(".",","));
            request.addProperty("plazoDias", pago.getPlazo());
            request.addProperty("monto", pago.getMonto().replace(".",","));
            request.addProperty("esInstitucionFinanciera", pago.getEsIFI());
            request.addProperty("codigoTipoDeposito", pago.getTipoPagoInteres());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(parametro.getUrlDPF());

            try {
                SSLConnection.allowAllSSL();
                transporte.call(parametro.getSoapAction(), envelope);
                SoapObject resSoap = (SoapObject) envelope.getResponse(); //obtiene respuesta

                Utilitarios.DatosCotizadorDPF respuestaCotizador = new Utilitarios.DatosCotizadorDPF(resSoap);

                pago.setInteres(respuestaCotizador.getInteres());
                pago.setRetencion(respuestaCotizador.getRetencion());
                pago.setNetoARecibir(respuestaCotizador.getNetoARecibir());
                respuesta=true;

            } catch (Exception e) {
                respuesta=false;
            }
        }
        db.close();
        return  respuesta;
    }

    private class ObtenerDatos extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean respuesta = true;
            try {
                if (!ObtenerDatosCotizadorDPF()) {
                    respuesta = false;
                }
            } catch (Exception e) {
                respuesta = false;
            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            try {
                if (!result) {
                    utilitario.MostrarMensaje("No se pudo obtener respuesta con los datos ingresados", CotizadorDPF.this);
                } else {
                    utilitario.MostrarMensaje("Valores Calculados con Éxito", CotizadorDPF.this);
                    lblNetoARecibir.setVisibility(View.VISIBLE);
                    lblInteres.setVisibility(View.VISIBLE);
                    lblRetencion.setVisibility(View.VISIBLE);
                    txtInteres.setVisibility(View.VISIBLE);
                    txtRetencion.setVisibility(View.VISIBLE);
                    txtNetoARecibir.setVisibility(View.VISIBLE);
                    txtInteres.setText(pago.getInteres());
                    txtRetencion.setText(pago.getRetencion());
                    txtNetoARecibir.setText(pago.getNetoARecibir());
                }
            } catch (Exception error) {
                utilitario.MostrarMensaje(error.getMessage(), CotizadorDPF.this);
            }
        }
    }



}
