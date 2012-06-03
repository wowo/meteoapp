package pl.sznapka.meteoapp;
import android.app.AlertDialog;
import android.content.Context;


public class ExceptionHandler {
	
	Context context;
	Exception exception;
	
	public ExceptionHandler(Context context, java.lang.Exception exception) {
		
		this.context = context;
		this.exception = exception;
	}
	
	public void handle() {
		
		new AlertDialog.Builder(context)
		.setTitle("Wystąpił błąd")
		.setCancelable(true)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(exception.getMessage())
		.setPositiveButton("OK", null)
		.create()
		.show();
	}
}
