package pl.sznapka.meteoapp;

import pl.sznapka.meteo.image.Processor;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ForecastActivity extends Activity {
	
	protected static final String CAPTION_PREFIX = "Prognoza pogody dla ";
	protected ChoosenForecast choosenForecast;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
        choosenForecast = (ChoosenForecast) getIntent().getExtras().getSerializable("choosenForecast");
        displayForecast(choosenForecast);
    }
    
    public void displayForecast(ChoosenForecast choosenForecast) {

		System.out.println("Showing forecast activity for: " + choosenForecast.forecast.city.name);
		((TextView)findViewById(R.id.textForecastCaption)).setText(CAPTION_PREFIX + choosenForecast.forecast.city.name);
		
		((LinearLayout)findViewById(R.id.diagramsLayout)).removeAllViewsInLayout();
		for (String diagramPath : choosenForecast.diagramsPaths) {
			ImageView image = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Processor.DIAGRAM_HEIGHT);
			image.setLayoutParams(params);
			image.setImageBitmap(BitmapFactory.decodeFile(diagramPath));
			((LinearLayout)findViewById(R.id.diagramsLayout)).addView(image);
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	getMenuInflater().inflate(R.layout.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.refresh:
	    		new FetchForecastTask(ForecastActivity.this, getCacheDir(),
	    				choosenForecast.types,new CacheManager(getCacheDir())).execute(choosenForecast.forecast.city);
	    		return true;
	    	case R.id.form:
	    		finish();
	    		return true;
	    	case R.id.about:
	    		TextView view = new TextView(this);
	    		SpannableString msg = new SpannableString("Wykonanie: Wojciech Sznapka\n" +
	    				"Dane pobierane są ze strony new.meteo.pl\n\n" +
	    				"Strona autora: sznapka.pl\n\n" +
	    				"Kontakt: android@sznapka.pl");
	    		Linkify.addLinks(msg, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
	    		view.setText(msg);
	    		view.setMovementMethod(LinkMovementMethod.getInstance());
	    		
	    		new AlertDialog.Builder(this)
	    		.setTitle("O programie")
	    		.setCancelable(true)
	    		.setIcon(android.R.drawable.ic_dialog_info)
	    		.setView(view)
	    		.setPositiveButton("OK", null)
	    		.create()
	    		.show();
	    		return true;
    		default:
    	    	return super.onOptionsItemSelected(item);
    	}
    }
}
