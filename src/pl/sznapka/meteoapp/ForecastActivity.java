package pl.sznapka.meteoapp;

import pl.sznapka.meteo.image.Processor;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ForecastActivity extends Activity {
	
	public static final String CAPTION_PREFIX = "Prognoza pogody dla ";
	protected ChoosenForecast choosenForecast;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  

		choosenForecast = (ChoosenForecast) this.getIntent().getExtras().getSerializable("choosenForecast");
		System.out.println("Showing forecast activity for: " + choosenForecast.forecast.city.name);
		((TextView)findViewById(R.id.textForecastCaption)).setText(CAPTION_PREFIX + choosenForecast.forecast.city.name);
		
		for (String diagramPath : choosenForecast.diagramsPaths) {
			ImageView image = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Processor.DIAGRAM_HEIGHT);
			image.setLayoutParams(params);
			image.setImageBitmap(BitmapFactory.decodeFile(diagramPath));
			((LinearLayout)findViewById(R.id.diagramsLayout)).addView(image);
		}
    }
}
