package pl.sznapka.meteoapp;

import java.util.ArrayList;
import java.util.HashMap;

import pl.sznapka.meteo.image.ImageProcessingException;
import pl.sznapka.meteo.image.Processor;
import pl.sznapka.meteo.fetcher.FetcherException;
import pl.sznapka.meteo.fetcher.ForecastFetcher;
import pl.sznapka.meteo.http.HttpClient;
import pl.sznapka.meteo.valueobject.City;
import pl.sznapka.meteo.valueobject.Forecast;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MeteoappActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  

		try {
			System.out.println("Start");
	
			ForecastFetcher fetcher = new ForecastFetcher(new City(797, "Orzesze"), new HttpClient());
			ArrayList<Forecast> result = fetcher.fetch();
			Forecast current = result.get(0);
			System.out.println("Fetched img: " + current.path);
	
			Processor processor = new Processor();
			HashMap<String, String> diagrams = processor.extractDiagrams(current);
			for (String key : diagrams.keySet()) {
				System.out.println(key + ":\t" + diagrams.get(key));
				Bitmap bmp = BitmapFactory.decodeFile(diagrams.get(key));
				ImageView image = new ImageView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Processor.DIAGRAM_HEIGHT);
				image.setLayoutParams(params);
				image.setImageBitmap(bmp);
				((LinearLayout)findViewById(R.id.diagramsLayout)).addView(image);
			}
			System.out.println("End");
		} catch (FetcherException e) {
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			e.printStackTrace();			
		}
    }
}