package vic.software.androidpokedex;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Detalles extends Activity {

	private TextView tvnombre;
	private TextView tvaltura;
	private TextView tvpeso;
	private TextView tvratio;
	private TextView tvdescri;
	private ImageView ivfoto;
	private ImageView bsonido;
	private MediaPlayer sonido;
	private boolean rulando=false;
	private class Hilo1 extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			
			rulando=false;
			super.onPostExecute(result);
		}
		
    	
    }
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles);
        tvnombre=(TextView)findViewById(R.id.tvNombre);
        tvaltura=(TextView)findViewById(R.id.tvAltura);
        tvpeso=(TextView)findViewById(R.id.tvPeso);
        tvratio=(TextView)findViewById(R.id.tvRatio);
        tvdescri=(TextView)findViewById(R.id.tvDescri);
        ivfoto=(ImageView)findViewById(R.id.ivFoto);
        bsonido=(ImageView)findViewById(R.id.bSonido);
        bsonido.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (rulando) return;
				dalequesuene();
				
			}
		});
        Bundle bundle=getIntent().getExtras();
        String nombre=bundle.getString("nombre");
        tvnombre.setText(nombre);
        Double altura=bundle.getDouble("altura");
        tvaltura.setText(altura+" metros.");	
        Double peso=bundle.getDouble("peso");
        tvpeso.setText(peso+" Kilos.");
        int ratio=bundle.getInt("ratio");
        tvratio.setText(ratio+"");
        String descri=bundle.getString("descripcion");
        tvdescri.setText(descri);
        long id=bundle.getLong("id");
        int codigo=getResources().getIdentifier("p"+id, "drawable", getPackageName());
        ivfoto.setImageResource(codigo);
        int codigos=getResources().getIdentifier("s"+id, "raw", getPackageName());
        sonido=MediaPlayer.create(this, codigos);
        dalequesuene();
    }

	protected void dalequesuene() {
		rulando=true;
		sonido.start();
		new Hilo1().execute();
		
	}

	
}
