package pl.sznapka.meteoapp;

import java.util.ArrayList;
import java.util.HashMap;

import pl.sznapka.image.ImageProcessingException;
import pl.sznapka.image.Processor;
import pl.sznapka.meteo.fetcher.FetcherException;
import pl.sznapka.meteo.fetcher.ForecastFetcher;
import pl.sznapka.meteo.fetcher.HttpClient;
import pl.sznapka.meteo.valueobject.City;
import android.app.Activity;
import android.os.Bundle;

public class MeteoappActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
			}
			System.out.println("End");
		} catch (FetcherException e) {
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			e.printStackTrace();
			
		}
        setContentView(R.layout.main);
    }
}