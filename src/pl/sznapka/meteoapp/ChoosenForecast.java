package pl.sznapka.meteoapp;

import java.io.Serializable;
import java.util.ArrayList;
import pl.sznapka.meteo.valueobject.Forecast;

/**
 * @author wowo
 * Forecast choosen by user
 */
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
