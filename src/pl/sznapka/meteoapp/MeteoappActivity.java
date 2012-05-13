package pl.sznapka.meteoapp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import pl.sznapka.image.ImageProcessingException;
import pl.sznapka.image.Processor;
import pl.sznapka.meteo.fetcher.FetcherException;
import pl.sznapka.meteo.fetcher.ForecastFetcher;
import pl.sznapka.meteo.fetcher.HttpClient;
import pl.sznapka.meteo.valueobject.City;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

public class MeteoappActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  

		try {
			System.out.println("Start");
	
			ForecastFetcher fetcher = new ForecastFetcher(new City(797, "Orzesze"), new HttpClient());
			ArrayList<String> result = (ArrayList<String>)fetcher.fetch();
			String path = result.get(0);
			System.out.println("Fetched img: " + path);
	
			Processor processor = new Processor();
			HashMap<String, String> diagrams = processor.extractDiagrams(path, path.substring(0, path.length() - 4) + "-");
			for (String key : diagrams.keySet()) {
				System.out.println(key + ":\t" + diagrams.get(key));
				Bitmap bmp = BitmapFactory.decodeFile(diagrams.get(key));
				ImageView image = new ImageView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				params.setMargins(0, 0, 0, 0);
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