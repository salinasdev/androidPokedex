package vic.software.androidpokedex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Pokedex extends Activity implements OnClickListener {
	
	private LinearLayout listaPokemon;
	private ImageView bbuscar;
	private EditText bcaja;
	private Intent intent;
	private long id;
	private AdaptadorBD miBD;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listaPokemon=(LinearLayout)findViewById(R.id.LYLista);
        bbuscar=(ImageView)findViewById(R.id.imageView1);
        bcaja=(EditText)findViewById(R.id.cjBuscar);
        intent=new Intent(this, Detalles.class);
    	miBD = new AdaptadorBD(this);
    	if (sacapokemon(1)==null)
    		importar();
        buscar("");
        bbuscar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				buscar(bcaja.getText()+"");
				
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        
    }
    
    private void buscar(String p){
    	if(listaPokemon.getChildCount()>0){
    		listaPokemon.removeAllViews();
    	}
    	
    	miBD.abrirR();
    	Cursor c=miBD.buscarPokemon(p);
    	if (c.moveToFirst()){
        	for(int i=0;i<c.getCount();i++,c.moveToNext()){
        		ver(c);
        	}
        }
        miBD.cerrar();

    }
    private void ver(Cursor c) {
    	id=c.getLong(0);
    	mensaje(c.getLong(0)+": "+c.getString(1));
	}

	private void mensaje(String msg) {
        TextView textview=new TextView(getApplicationContext());
        textview.setText(msg);
        textview.setTextSize(30);
        textview.setTag("p"+id);
        textview.setOnClickListener(this);
        listaPokemon.addView(textview);
	}

    private void importar() {
		try {
	    	miBD.abrirW();
	    	//miBD.insertarPokemon();
	    	miBD.cerrar();
			String destPath = "/data/data/" + getPackageName()
					+ "/databases/S2DAMBD";
			File f = new File(destPath);
			if (f.exists()) {
				f.delete();
			}
			CopyDB(getBaseContext().getAssets().open("DBPOK"),
						new FileOutputStream(destPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        
	}

	public void CopyDB(InputStream origen, OutputStream destino)
			throws IOException {
		// copiar 1K bytes
		byte[] buffer = new byte[1024];
		int length;
		while ((length = origen.read(buffer)) > 0) {
			destino.write(buffer, 0, length);
		}
		origen.close();
		destino.close();
	}

	public void onClick(View v) {
		String mitag=(String) v.getTag();
		int miid=Integer.parseInt(mitag.substring(1));
		Pokemon p=sacapokemon(miid);
		intent.putExtra("id", p.getId());
		intent.putExtra("nombre", p.getNombre());
		intent.putExtra("altura", p.getAltura());
		intent.putExtra("peso", p.getPeso());
		intent.putExtra("ratio", p.getRatio());
		intent.putExtra("descripcion", p.getDescri());
		startActivity(intent);
		
		
	}

	private Pokemon sacapokemon(int miid) {
		AdaptadorBD miBD = new AdaptadorBD(this);
		Pokemon p=new Pokemon();
    	miBD.abrirR();
    	Cursor c=miBD.buscarDetallesPoke(miid);
    	if (c.moveToFirst()){
        	p.setId(c.getLong(0));
        	p.setNombre(c.getString(1));
        	p.setAltura(c.getDouble(2));
        	p.setPeso(c.getDouble(3));
        	p.setRatio(c.getInt(4));
        	p.setDescri(c.getString(5));
        	return p;
        }
        miBD.cerrar();
		return null;
	}	


}
