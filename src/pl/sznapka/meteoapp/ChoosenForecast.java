package pl.sznapka.meteoapp;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

import pl.sznapka.meteo.valueobject.City;
import pl.sznapka.meteo.valueobject.Forecast;

public class ChoosenForecast implements Serializable {
	
	private static final long serialVersionUID = 448162750751778567L;
	
	public Forecast forecast;
	public ArrayList<String> diagramsPaths;
	public ArrayList<String> types;
	
	public ChoosenForecast(Forecast forecast, ArrayList<String> diagramsPaths, ArrayList<String> types) {
		
		this.forecast = forecast;
		this.diagramsPaths = diagramsPaths;
		this.types = types;
	}
}
