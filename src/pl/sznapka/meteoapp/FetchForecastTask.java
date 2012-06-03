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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class FetchForecastTask extends AsyncTask<City, Void, ChoosenForecast> {

	protected ProgressDialog progressDialog;
	protected WeakReference<Activity> parent;
	protected File cacheDir;
	protected ArrayList<String> types;
	
	public FetchForecastTask(Activity parent, File cacheDir, ArrayList<String> types) {
		
		super();
		this.parent = new WeakReference<Activity>(parent);
		this.cacheDir = cacheDir;
		this.types = types;
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
		} catch (FetcherException e) {
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			e.printStackTrace();			
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
		
		if (parent.get().getClass() == MeteoappActivity.class) {
			Intent intent = new Intent(parent.get(), ForecastActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("choosenForecast", choosenForecast);
	     	intent.putExtras(bundle);
			progressDialog.dismiss();
			parent.get().startActivity(intent);
		} else if (parent.get().getClass() == ForecastActivity.class) {
			progressDialog.dismiss();
			((ForecastActivity) parent.get()).displayForecast(choosenForecast);
		}
	}
}
