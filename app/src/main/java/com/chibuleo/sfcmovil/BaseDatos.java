package com.chibuleo.sfcmovil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class BaseDatos extends SQLiteOpenHelper
{
    // public static String DB_PATH = "/data/data/com.example.aplicacionSqLite3/databases/";
    public static String DB_PATH = "/data/data/com.chibuleo.sfcmovil/databases/";
    public static String DB_NAME = "DBSFCMovil";
    private final Context contexto;
    public static int v_db =5;


    //private String sqlEdicion="alter table Telefono add column fecha TEXT";


    public BaseDatos(Context contexto,String nombre,SQLiteDatabase.CursorFactory factory,int version)
    {
        super(contexto,nombre,factory,version);
        this.contexto=contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try {
            String sqlCreacion = "";
            sqlCreacion = "create table CredencialesLocales ( usuario TEXT , contrasenia TEXT , fecha TEXT, nombreImpresora TEXT )";
            db.execSQL(sqlCreacion);
            sqlCreacion = "create table TransaccionesMovil(cliente TEXT , nombre TEXT , codigo TEXT , producto TEXT , " +
                    " valor TEXT , documento TEXT, fecha TEXT , estado TEXT, tipoTransaccion TEXT)";
            db.execSQL(sqlCreacion);
            sqlCreacion = "create table DatosPrestamosClientes(numeroPagare TEXT, numeroCliente TEXT, nombreCliente TEXT, tipoCredito TEXT, codigo TEXT," +
                    "  montoCredito TEXT , saldoPrestamo TEXT, saldoAtrasado TEXT,totalInteres TEXT, totalMora TEXT, totalOtros TEXT," +
                    "  fechaUltimoPago TEXT, valorCuota TEXT, totalAPagar TEXT, valorAlDia TEXT, paraCancelar TEXT, valorProximoPago TEXT, " +
                    "  fechaProximoPago TEXT, estado TEXT, numeroTelefono TEXT, direccion TEXT, diasMora TEXT, comentario TEXT, garante1 TEXT, garante2 TEXT, saldoAhorros TEXT)";
            db.execSQL(sqlCreacion);
            sqlCreacion = "create table Telefono ( numeroTelefono TEXT )";
            db.execSQL(sqlCreacion);
            sqlCreacion = "create table ServidorApp ( servidorInterno TEXT, servidorExterno TEXT )";
            db.execSQL(sqlCreacion);
            sqlCreacion = "create table ValidaSesion(fechaHoraUltimoAcceso TEXT)";
            db.execSQL(sqlCreacion);

        }
        catch (Exception error) {
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        //db.execSQL("DROP TABLE IF EXISTS Telefono");
        //db.execSQL("DROP TABLE IF EXISTS ServidorApp");
        db.execSQL("DROP TABLE IF EXISTS CredencialesLocales");
        db.execSQL("DROP TABLE IF EXISTS TransaccionesMovil");
        db.execSQL("DROP TABLE IF EXISTS DatosPrestamosClientes");

        // Create tables again
        onCreate(db);

    }
}