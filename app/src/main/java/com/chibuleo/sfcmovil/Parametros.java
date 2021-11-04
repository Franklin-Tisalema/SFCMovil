package com.chibuleo.sfcmovil;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;

import java.util.Date;


public class Parametros {
    private final String namespace = "http://www.chibuleo.com";
    private final String versionApp = "7";
    private String urlSeguridad="";
    private String urlPrestamos="";
    private String urlCuentas="";
    private String urlDPF="";
    private String soapAction = "";
    private String methodName = "";
    private String servidorAppExterno="enlinea.chibuleo.com";
    private String servidorAppInterno="hubtransaccion.chibuleo.local";
    private String uriServidor="";

    public String getNamespace() { return this.namespace; }
    public String getVersionApp() { return this.versionApp; }
    public String getUrlSeguridad()
    {
        return this.urlSeguridad;
    }
    public String getUrlPrestamos()
    {
        return this.urlPrestamos;
    }
    public String getUrlCuentas()
    {
        return this.urlCuentas;
    }
    public String getUrlDPF() { return this.urlDPF; }
    public String getSoapAction()
    {
        return this.soapAction;
    }
    public String getMethodName()
    {
        return this.methodName;
    }

    public Parametros(){}

    public void DefinirMetodo(String unMethodName,Boolean esLocal, Context contexto)
    {
        this.methodName = unMethodName;
        this.soapAction =  namespace + "/" + unMethodName;
        /*if(esLocal) {
            this.urlSeguridad = "https://192.168.61.6/WSMobileManager/wsSeguridad.asmx";        //"http://10.0.2.2:1473/ServicioClientes.asmx";
            this.urlPrestamos = "https://192.168.61.6/WSMobileManager/wsPrestamos.asmx";
            this.urlCuentas = "https://192.168.61.6/WSMobileManager/wsCuentas.asmx";
            this.urlDPF = "https://192.168.61.6/WSMobileManager/wsDepositosPlazoFijo.asmx";

        }
        else {
            this.urlSeguridad = "https://190.95.194.26/WSMobileManager/wsSeguridad.asmx";        //"http://10.0.2.2:1473/ServicioClientes.asmx";
            this.urlPrestamos = "https://190.95.194.26/WSMobileManager/wsPrestamos.asmx";
            this.urlCuentas = "https://190.95.194.26/WSMobileManager/wsCuentas.asmx";
            this.urlDPF = "https://190.95.194.26/WSMobileManager/wsDepositosPlazoFijo.asmx";
        }*/

            this.LeerURIServidor(esLocal,contexto);

            this.urlSeguridad = uriServidor + "/WSMobileManager/wsSeguridad.asmx";        //"http://10.0.2.2:1473/ServicioClientes.asmx";
            this.urlPrestamos = uriServidor + "/WSMobileManager/wsPrestamos.asmx";
            this.urlCuentas = uriServidor + "/WSMobileManager/wsCuentas.asmx";
            this.urlDPF = uriServidor + "/WSMobileManager/wsDepositosPlazoFijo.asmx";
    }

    private void LeerURIServidor(Boolean esLocal, Context contexto)
    {
        BaseDatos baseDatos = new BaseDatos(contexto,BaseDatos.DB_NAME,null,BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        String servidor = "";
        if (esLocal)
            servidor = "select servidorInterno from ServidorApp";
        else
            servidor = "select servidorExterno from ServidorApp";

        Cursor tabla = db.rawQuery(servidor,null);

        uriServidor="";
        try
        {
            if (tabla.moveToFirst())
            {
                do {
                    if (esLocal)
                        uriServidor = tabla.getString(tabla.getColumnIndex("servidorInterno"));
                    else
                        uriServidor = tabla.getString(tabla.getColumnIndex("servidorExterno"));

                } while (tabla.moveToNext());
            }
        }
        catch (Exception ex)
        {
            if (esLocal)
                uriServidor = servidorAppInterno;
            else
                uriServidor = servidorAppExterno;
        }
        db.close();
    }



}
