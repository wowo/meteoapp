package pl.sznapka.meteoapp;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import pl.sznapka.meteo.fetcher.FetcherException;
import pl.sznapka.meteo.fetcher.ForecastFetcher;
import pl.sznapka.meteo.http.HttpClient;
import pl.sznapka.meteo.image.ImageProcessingException;
import pl.sznapka.meteo.image.Processor;
import pl.sznapka.meteo.valueobject.City;
import pl.sznapka.meteo.valueobject.Forecast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class FetchForecastTask extends AsyncTask<City, Void, ChoosenForecast> {

	protected ProgressDialog progressDialog;
	protected WeakReference<Activity> parent;
	protected File cacheDir;
	protected ArrayList<String> types;
	protected CacheManager cache;
	protected Exception exception;
	
	public FetchForecastTask(Activity parent, File cacheDir, ArrayList<String> types, CacheManager cache) {
		
		super();
		this.parent = new WeakReference<Activity>(parent);
		this.cacheDir = cacheDir;
		this.types = types;
		this.cache = cache;
	}
	
	@Override
	protected ChoosenForecast doInBackground(City... cities) {

		try {
			ForecastFetcher fetcher = new ForecastFetcher(cities[0], new HttpClient(), cacheDir);
			ArrayList<Forecast> result = fetcher.fetch();
			Forecast current = result.get(0);	
			Processor processor = new Processor();
			HashMap<String, String> diagrams = processor.extractDiagrams(current, types);

			ArrayList<String> choosenDiagrams = new ArrayList<String>();
			for (String key : types) {
				choosenDiagrams.add(diagrams.get(key));
			}
			
			return new ChoosenForecast(current, choosenDiagrams, types);
		} catch (Exception e) {
			e.printStackTrace();
			exception = e;
		}
		
		return null;
	}
	
	protected void onPreExecute() {

		super.onPreExecute();
		progressDialog = new ProgressDialog(parent.get());
		progressDialog.setTitle("Pobieram");
		progressDialog.setMessage("Proszę czekać, pobieranie prognozy");
		progressDialog.show();
	}
	
	protected void onPostExecute(ChoosenForecast choosenForecast) {
	
		if (exception == null) {
			cache.storeObjectInCache("forecast", choosenForecast);
			if (parent.get().getClass() == MeteoappActivity.class) {
				Intent intent = new Intent(parent.get(), ForecastActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("choosenForecast", choosenForecast);
		     	intent.putExtras(bundle);
				parent.get().startActivity(intent);
			} else if (parent.get().getClass() == ForecastActivity.class) {
				((ForecastActivity) parent.get()).displayForecast(choosenForecast);
			}
		} else {
			new ExceptionHandler(parent.get(), exception).handle();
		}
		progressDialog.dismiss();
	}
}
