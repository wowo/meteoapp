package pl.sznapka.meteoapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import pl.sznapka.meteo.fetcher.CityFetcher;
import pl.sznapka.meteo.fetcher.FetcherException;
import pl.sznapka.meteo.fetcher.StateFetcher;
import pl.sznapka.meteo.http.HttpClient;
import pl.sznapka.meteo.valueobject.City;
import pl.sznapka.meteo.valueobject.Forecast;
import pl.sznapka.meteo.valueobject.State;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MeteoappActivity extends Activity {

	public ArrayList<State> states;
	public HashMap<String, ArrayList<City>> cities;
	public ArrayList<Integer> checkboxes;
	ProgressDialog dialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        
        cities = loadCitiesFromCache();
        dialog = new ProgressDialog(this);
        dialog.setTitle("Pobieram");
        
        ((Spinner)findViewById(R.id.city)).setEnabled(false);
        cities = loadCitiesFromCache();
        
        ArrayList<String> items = new ArrayList<String>();
        items.add("Wybierz województwo");
        states = (new StateFetcher()).fetch();
        for (State state : states) {
			items.add(state.name);
		}
        
        Spinner statesSpinner = (Spinner)findViewById(R.id.state);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item, items);
        statesSpinner.setAdapter(adapter);
        statesSpinner.setOnItemSelectedListener(new StatesSpinnerListener());

        attachCheckboxListeners();
        findViewById(R.id.buttonShowMeteo).setOnClickListener(
        		new ShowMeteoButtonListener());
        
    }
    
    protected HashMap<String, ArrayList<City>> loadCitiesFromCache() {
    	
    	HashMap<String, ArrayList<City>> cities = null;
    	File cacheFile = getCacheFile("cities"); 
    	if (cacheFile.exists()) {
	    	FileInputStream fis = null;
	    	ObjectInputStream ois = null;
	    	try {
	    		fis = new FileInputStream(cacheFile);
	    		ois = new ObjectInputStream(fis);
	    		cities = (HashMap<String, ArrayList<City>>) ois.readObject();
	    		System.out.println("Got cities from cache: " + cities.keySet().toString());
	    	} catch (Exception e) {
	    	} finally {
	    		try {
	    			if (fis != null) fis.close();
	    			if (ois != null) ois.close();
	    		} catch (Exception e) {
	    		}
	    	}
    	} else {
    		cities = new HashMap<String, ArrayList<City>>();
    		System.out.println("Cities does not exists in cache");
    	}
    	
    	return cities;
    }
    
    /**
     * Stores in cache
     * 
     * @param cities
     */
    protected void storeCitiesInCache(HashMap<String, ArrayList<City>> cities) {
    	
    	FileOutputStream fos = null;
    	ObjectOutputStream oos = null;
    	
    	try {
    		fos = new FileOutputStream(getCacheFile("cities"));
    		oos = new ObjectOutputStream(fos);
    		oos.writeObject(cities);
    	} catch (Exception e) {
    	} finally {
    		try {
    			if (oos != null) oos.close();
    			if (fos != null) fos.close();
    		} catch (Exception e) {
			}
    	}
    }
    
    protected File getCacheFile(String target) {

    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    		System.out.println("Cache dir in SD card");
    		File dataDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/meteoApp");
    		dataDir.mkdir();
    		
    		return new File(dataDir.getAbsolutePath() + "/" + target + ".dat");
    	} else {
    		System.out.println("Cache dir in data");
    		
    		return new File(getCacheDir().getAbsoluteFile() + "/" + target + ".dat");
    	}
	}

	/**
     * Attaches checkboxes listener
     */
    protected void attachCheckboxListeners() {
    	
    	checkboxes = new ArrayList<Integer>();
    	checkboxes.add(R.id.checkBoxTemperature);
    	checkboxes.add(R.id.checkBoxRain);
    	checkboxes.add(R.id.checkBoxPressure);
    	checkboxes.add(R.id.checkBoxWind);
    	checkboxes.add(R.id.checkBoxVisibility);
    	checkboxes.add(R.id.checkBoxClouds);
    	for (Integer id : checkboxes) {
			CheckBox checkbox = (CheckBox)findViewById(id);
			checkbox.setOnCheckedChangeListener(new ChecboxListener());
		}
    }
    
    protected void setButtonEnabledIfAllCriteriaSatisfied(boolean initial) {
		
    	boolean allCriteriaSatisfied = initial;
		if (!initial) {
			for (Integer id : checkboxes) {
				CheckBox checkbox = (CheckBox)findViewById(id);
				if (checkbox.isChecked()) {
					allCriteriaSatisfied = true;
					break;
				}
			}
		}
		Spinner city = (Spinner)findViewById(R.id.city);
		allCriteriaSatisfied = allCriteriaSatisfied & (city.getSelectedItemPosition()
				!= Spinner.INVALID_POSITION);
		findViewById(R.id.buttonShowMeteo).setEnabled(allCriteriaSatisfied);    	
    }

	private class ChecboxListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			setButtonEnabledIfAllCriteriaSatisfied(isChecked);
		}
	}
    
	private class StatesSpinnerListener implements OnItemSelectedListener {
    	
    	State state;
    	
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

			if (pos > 0) {
				state = states.get(pos - 1);
				System.out.println("Selected state: " + state.name);
		        ((Spinner)findViewById(R.id.city)).setEnabled(false);
				new FetchCitiesTask().execute(state);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private class ShowMeteoButtonListener implements OnClickListener {

		@Override
		public void onClick(View button) {
			
			State state = states.get(((Spinner)findViewById(R.id.state)).getSelectedItemPosition() - 1);
			ArrayList<City> citiesInState = cities.get(state.name);
			City city = citiesInState.get(((Spinner)findViewById(R.id.city)).getSelectedItemPosition());
			
			new FetchForecastTask(MeteoappActivity.this, getCacheDir(), getSelectedTypes()).execute(city);
		}
		
		protected ArrayList<String> getSelectedTypes() {
			
			ArrayList<String> types = new ArrayList<String>();
			if (((CheckBox)findViewById(R.id.checkBoxTemperature)).isChecked()) {
				types.add(Forecast.TEMPERATURE);
			}
			if (((CheckBox)findViewById(R.id.checkBoxRain)).isChecked()) {
				types.add(Forecast.RAIN);
			}
			if (((CheckBox)findViewById(R.id.checkBoxPressure)).isChecked()) {
				types.add(Forecast.PRESSURE);
			}
			if (((CheckBox)findViewById(R.id.checkBoxWind)).isChecked()) {
				types.add(Forecast.WIND);
			}
			if (((CheckBox)findViewById(R.id.checkBoxVisibility)).isChecked()) {
				types.add(Forecast.VISIBILITY);
			}
			if (((CheckBox)findViewById(R.id.checkBoxClouds)).isChecked()) {
				types.add(Forecast.CLOUDS);
			}
			
			return types;
		}
		
	}
	
	private class FetchCitiesTask extends AsyncTask<State, Void, ArrayList<City>> {

		protected ProgressDialog progressDialog;
		
		@Override
		protected ArrayList<City> doInBackground(State... states) {

			State state = states[0];
			try {
				if (!cities.containsKey(state.name)) {
					System.out.println("State: " + state.name + " not found in cache");
					CityFetcher fetcher = new CityFetcher(state, new HttpClient());
					cities.put(state.name, fetcher.fetch());
					storeCitiesInCache(cities);
				} else {
					System.out.println("Retrieving " + state.name + " from cache");
				}
				return cities.get(state.name);
			} catch (FetcherException e) {
				System.out.println("Exception: " + e.getMessage());
			}
			return null;
		}
		
		protected void onPreExecute() {

			super.onPreExecute();
			progressDialog = new ProgressDialog(MeteoappActivity.this);
			progressDialog.setTitle("Pobieram");
			progressDialog.setMessage("Proszę czekać, pobieranie listy miast");
			progressDialog.show();
		}
		
		protected void onPostExecute(ArrayList<City> cities) {

	        ArrayList<String> items = new ArrayList<String>();
	        for (City city : cities) {
				items.add(city.name);
			}
	        Spinner citiesSpinner = (Spinner)findViewById(R.id.city);
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MeteoappActivity.this,
	        		android.R.layout.simple_spinner_item, items);
	        citiesSpinner.setAdapter(adapter);
	        ((Spinner)findViewById(R.id.city)).setEnabled(true);
	        setButtonEnabledIfAllCriteriaSatisfied(false);
	        
	        progressDialog.dismiss();
		}
	}
}