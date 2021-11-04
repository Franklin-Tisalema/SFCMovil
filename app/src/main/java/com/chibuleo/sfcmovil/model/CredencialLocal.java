package com.chibuleo.sfcmovil.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import com.chibuleo.sfcmovil.BaseDatos;

/**
 * Created by sistemas on 22/01/2016.
 */
public class CredencialLocal {

    private String usuario;
    private String contrasenia;
    private String fecha;
    private String nombreImpresora;

    public void setUsuario(String elUsuario) { this.usuario = elUsuario; }
    public String getUsuario() { return this.usuario; }
    public void setContrasenia(String laContrasenia) { this.contrasenia = laContrasenia; }
    public String getContrasenia() {return this.contrasenia; }
    public void setFecha(String unaFecha) { this.fecha = unaFecha; }
    public String getFecha() { return this.fecha; }
    public void setNombreImpresora(String unNombreImpresora) { this.nombreImpresora = unNombreImpresora; }
    public String getNombreImpresora() { return this.nombreImpresora; }

    public CredencialLocal(String unUsuario, String unaContrasenia, String unaFecha, String unNombreImpresora){
        this.usuario=unUsuario;
        this.contrasenia=unaContrasenia;
        this.fecha = unaFecha;
        this.nombreImpresora = unNombreImpresora;
    }

    public CredencialLocal(){
        this.usuario="";
        this.contrasenia="";
        this.fecha = "";
        this.nombreImpresora ="";
    }

    public Boolean GuardarCredencial(Context unContexto) {
        Boolean respuesta = false;
        try {

            if (this.usuario.equals("") || this.contrasenia.equals("")) {
                respuesta = false;
            }
            else
            {
                BaseDatos baseDatos = new BaseDatos(unContexto, BaseDatos.DB_NAME, null, BaseDatos.v_db);
                SQLiteDatabase db = baseDatos.getWritableDatabase();

                String elSql = "";

                elSql = "delete from CredencialesLocales";
                db.execSQL(elSql);

                elSql = "insert into CredencialesLocales( usuario, contrasenia, fecha, nombreImpresora ) " +
                        " values ('" + this.usuario + "','" + this.contrasenia + "','" + this.fecha + "','" + this.nombreImpresora + "')";
                db.execSQL(elSql);

                db.close();

                respuesta = true;
            }

        } catch (Exception error) {
            respuesta = false;
        }
        return respuesta;
    }

    public void LeerCredenciales(Context unContexto)
    {
        BaseDatos baseDatos = new BaseDatos(unContexto,BaseDatos.DB_NAME,null,BaseDatos.v_db);
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        Cursor tabla = db.rawQuery("select usuario, contrasenia , fecha, nombreImpresora from CredencialesLocales",null);

        try
        {
            if (tabla.moveToFirst())
            {
                do {
                    this.usuario = tabla.getString(tabla.getColumnIndex("usuario"));
                    this.contrasenia = tabla.getString(tabla.getColumnIndex("contrasenia"));
                    this.fecha = tabla.getString(tabla.getColumnIndex("fecha"));
                    this.nombreImpresora = tabla.getString(tabla.getColumnIndex("nombreImpresora"));
                } while (tabla.moveToNext());
            }
        }
        catch (Exception ex)
        {
        }
        db.close();
    }
}
