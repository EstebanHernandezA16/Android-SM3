package com.example.concesionario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class VehiculoActivity extends AppCompatActivity {

    ClsOpenHelper admin=new ClsOpenHelper(this,"Concesionario.db",null,1);
    //placa, marca, modelo

    EditText jetPlaca, jetMarca, jetModelo;
    String Placa, Marca, Modelo;

    long respuesta;
    byte sw;// en 1 es Si, en 0 es No.
    CheckBox jcbActivo;

    //findViewById(R.Var)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);
        getSupportActionBar().hide();

        jetPlaca = findViewById(R.id.etplaca);
        jetMarca = findViewById(R.id.etmarca);
        jetModelo = findViewById(R.id.etmodelo);
        jcbActivo = findViewById(R.id.cbactivoV);
        jetPlaca.requestFocus();
        //el sw es para controlar si registró o no (o = no, > a 0 si)
        sw=0;
    }//fin constructor

    public void GuardarVehiculo(View view){
        Placa= jetPlaca.getText().toString();
        Marca= jetMarca.getText().toString();
        Modelo= jetModelo.getText().toString();
            if(Placa.isEmpty() || Marca.isEmpty() || Modelo.isEmpty()){
                Toast.makeText(this, "Todos los datos del vehiculo son requerido", Toast.LENGTH_SHORT).show();
                jetPlaca.requestFocus();

      }else{
                SQLiteDatabase db = admin.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("Placa",Placa);
                registro.put("marca",Marca);
                registro.put("modelo",Modelo);
                if(sw == 0){
                    respuesta = db.insert("TblVehiculo","",registro);
                }else{
                    respuesta = db.update("TblVehiculo",registro, "Placa='"+Placa+"'", null);
                    sw=0;
                }
                if(respuesta>0){
                    Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }else{
                    Toast.makeText(this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }


        }//fin guardar

    public void ConsultarVehiculo(View view){
        Placa=jetPlaca.getText().toString();
        if(Placa.isEmpty()){
            Toast.makeText(this, "La Placa del vehiculo es necesaria para consultar", Toast.LENGTH_SHORT).show();
            jetPlaca.requestFocus();
        }else{
            SQLiteDatabase db = admin.getReadableDatabase();
        Cursor fila=db.rawQuery("select * from TblVehiculo where Placa='"+Placa+"'",null);
        if(fila.moveToNext()){
            sw=1;
            if(fila.getString(3).equals("Si")){
                jetPlaca.setText(fila.getString(0));
                jetMarca.setText(fila.getString(1));
                jetModelo.setText(fila.getString(2));

                jcbActivo.setChecked(true);
            }else{//entonces el registro esta anulado
                Toast.makeText(this, "Registro anulado, para verlo, debe activarlo", Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(this, "El registro ingresado no existe", Toast.LENGTH_SHORT).show();
        }


        db.close();
        }
    }//fin consultarV

        public void ActivarVehiculo(){

            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("Activo","Si");
            respuesta = db.update("TblVehiculo",registro,"Placa='"+Placa +"'", null);
            if(respuesta!=0){
                Toast.makeText(this, "Activado Exitoso del vehiculo con placa "+Placa, Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            }else{
                Toast.makeText(this, "No se logró activar el registro", Toast.LENGTH_SHORT).show();
            }
        db.close();


}//Fin activarVehiculo

    public void AnularVehiculo(){
        Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show();
        if(sw==1){
            sw=0;
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("activo","No");
            respuesta= db.update("TblVehiculo",registro,"Placa='"+Placa+"'",null);
            if(respuesta>0){
                Toast.makeText(this, "Anulado Exitoso del Vehiculo con placa "+Placa, Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            }else{
                Toast.makeText(this, "No se logró anular el registro", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
        else{
            Toast.makeText(this, "Debe consultar primero para anular", Toast.LENGTH_SHORT).show();
        }
    }





    public void Cancelar(View view){
        Limpiar_campos();
    }

    public void RegresarVehiculo(View view){//view view es para conectar con la vista
        Intent intmain = new Intent(this, MainActivity.class);
        startActivity(intmain);
    }

    private void Limpiar_campos(){
        jetPlaca.setText("");
        jetMarca.setText("");
        jetModelo.setText("");
        jcbActivo.setChecked(false);
        jetPlaca.requestFocus();
        sw=0;
    }

}