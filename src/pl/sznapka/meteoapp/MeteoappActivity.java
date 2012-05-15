package pl.sznapka.meteoapp;

import java.util.ArrayList;
import java.util.HashMap;

import pl.sznapka.meteo.fetcher.CityFetcher;
import pl.sznapka.meteo.fetcher.FetcherException;
import pl.sznapka.meteo.fetcher.StateFetcher;
import pl.sznapka.meteo.http.HttpClient;
import pl.sznapka.meteo.valueobject.City;
import pl.sznapka.meteo.valueobject.State;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class MeteoappActivity extends Activity {

	protected ArrayList<State> states;
	protected HashMap<State, ArrayList<City>> cities;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        
        ((Spinner)findViewById(R.id.city)).setEnabled(false);
        cities = new HashMap<State, ArrayList<City>>();
        
        ArrayList<String> items = new ArrayList<String>();
        items.add("Wybierz województwo");
        states = (new StateFetcher()).fetch();
        for (State state : states) {
			items.add(state.name);
		}
        
        Spinner statesSpinner = (Spinner)findViewById(R.id.state);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        statesSpinner.setAdapter(adapter);
        
        statesSpinner.setOnItemSelectedListener(new StatesSpinnerListener());
    }
    
    public class StatesSpinnerListener implements OnItemSelectedListener {
    	
    	State state;
    	ProgressDialog dialog;
    	
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

			if (pos > 0) {
				state = states.get(pos - 1);
				System.out.println("Selected state: " + state.name);
				
		        ((Spinner)findViewById(R.id.city)).setEnabled(false);
				dialog = ProgressDialog.show(view.getContext(), "Pobieram", "Proszę czekać, pobieranie listy miast dla województwa " + state.name, true, false);
				new Thread(new Runnable() {					
					@Override
					public void run() {
						
						try {
							CityFetcher fetcher = new CityFetcher(state, new HttpClient());
							if (!cities.containsKey(state)) {
								cities.put(state, fetcher.fetch());
							}
					        dialog.dismiss();
					        runOnUiThread(new Runnable() {
								public void run() {
							        ArrayList<String> items = new ArrayList<String>();
							        for (City city : cities.get(state)) {
										items.add(city.name);
									}
							        Spinner citiesSpinner = (Spinner)findViewById(R.id.city);
							        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MeteoappActivity.this, android.R.layout.simple_spinner_item, items);
							        citiesSpinner.setAdapter(adapter);
							        ((Spinner)findViewById(R.id.city)).setEnabled(true);									
								}
							});
						} catch (FetcherException e) {
							e.printStackTrace();
							Toast.makeText(getParent(), "Error: " + e.getMessage(), 1000);
						}
					}
				}).start();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}
}