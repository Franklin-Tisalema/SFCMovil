package com.chibuleo.sfcmovil;

/**
 * Created by Ertzil on 05/03/2018.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;
    private Utilitarios.DatosComunes datosComunes;
    private Utilitarios utilitario;


    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }


    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject,Utilitarios.DatosComunes datosComunes) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.datosComunes = datosComunes;
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Error de Autenticación de huellas dactilares\n" + errString, false);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Autenticando con huella dactilar\n" + helpString, false);
    }


    @Override
    public void onAuthenticationFailed() {
        this.update("Fallo al autenticar con la huella dactilar.", false);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Éxito al autenticar con la huella dactilar.", true);
    }


    public void update(String e, Boolean success){
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.errorText);
        textView.setText(e);
        if(success){
            textView.setTextColor(ContextCompat.getColor(context,R.color.successText));
            //Valida caducidad de la sesion
            utilitario.RegistroUltimoAcceso(context);
            Intent distribuidor = new Intent(((Activity)context),Distribuidor.class);
            distribuidor.putExtra("usuario", this.datosComunes.getUsuarioComun());
            distribuidor.putExtra("fecha", this.datosComunes.getFechaComun());
            distribuidor.putExtra("esLocal", this.datosComunes.getEsLocalComun());
            distribuidor.putExtra("enLinea",this.datosComunes.getEnLineaComun());
            distribuidor.putExtra("impresora", this.datosComunes.getNombreImpresoraComun());
            ((Activity) context).finish();
            context.startActivity(distribuidor);

        }
    }
}
