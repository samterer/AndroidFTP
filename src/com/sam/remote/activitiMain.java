package com.sam.remote;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


import com.sam.remote.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;

public class activitiMain extends Activity {

	  private String sDir = "/public_html";
	  private String array_spinner[];
	  ScrollView sv;
	 
	 
		private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //loadLogs();
    }
	private void loadLogs() {
		//radBtn.addView(null);
		new processTask().execute(null);
	}
    private class processTask extends AsyncTask<String, Void, Void>{
    	private ProgressDialog Dialog = new ProgressDialog(activitiMain.this);
    	 private TextView log; 
    	 private  Spinner s;
    	protected void onPreExecute() {
    		Dialog.setMessage("Loading...");
    		Dialog.show();
     	}
		@Override
		protected Void doInBackground(String... arg0) {
			FTPClient client = new FTPClient();
	        try {
	        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	   		String ipaddr = myPref.getString("etServer", "");
	   		String port= myPref.getString("port", "21");
	   		String logFile= myPref.getString("logfname", "error_log");
	   		String filePath= myPref.getString("path", "/public_html");
	   		String uname= myPref.getString("uname", "");
	   		String pass= myPref.getString("pass", "");
	            client.connect(ipaddr,21);
	            client.enterLocalPassiveMode();
	            boolean login = client.login(uname, pass);
	            client.changeWorkingDirectory(filePath);
	            System.out.println(client.printWorkingDirectory());
	          
	            BufferedReader reader = null;
	            String line = null;
	            sv = new ScrollView(activitiMain.this);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
					sv.setLayoutParams(LP_FF);
					 LinearLayout loglay  =  new LinearLayout(activitiMain.this);
					 loglay.setOrientation( LinearLayout.VERTICAL ); 
	         //FTPFile[] ftpFiles = client.listFiles();
					 FTPFile[] ftpFiles = client.listFiles();
					 ArrayList<String> name = new ArrayList<String>();
					 
/*						         ArrayAdapter <CharSequence> adapter =
	        	  new ArrayAdapter <CharSequence> (getBaseContext(), android.R.layout.simple_spinner_item );
	        	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	
	        	 adapter.add("Select File");*/      
	        	//s = new (Spinner)findViewById(R.id.Spinner01);
	        	s = new Spinner(activitiMain.this);
	        	for (int i = 0; i < ftpFiles.length; i++) {	
	        		String fname =ftpFiles[i].getName();
	        		Log.i("FTP", "File " +i +" : "+fname);
						   name.add(fname);
						    long length = ftpFiles[i].getSize();
						    //adapter2.add(ftpFiles[i].getName());
						    //String readableLength = FileUtils.byteCountToDisplaySize( length );
						    ///System.out.println( name + ":\t\t" + readableLength );  
						}
	        	//String [] strArray =null;
	        	//strArray.toArray(name);
	        	String [] files = name.toArray(new String[name.size()]);
	        	ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,files);  
	             adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	             s.setAdapter(adapter2);  
	             loglay.addView(s);
	        	
	         Log.i("Files", ftpFiles.toString());
	            for (FTPFile ftpFile : ftpFiles) {
	            	String fileName = ftpFile.getName(); 
	            	if (fileName.equals(logFile)){
	            		 try {
	                         InputStream stream = client.retrieveFileStream(fileName);                             
	                         reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
	                         //line = reader.readLine();
	                        
	         				
	                         while ((line = reader.readLine())!= null && line.trim().length()>0) {
	                        	    //System.out.println(line);
	                        	 log = new TextView(activitiMain.this);
	                        	    String trail = line; 
	                        	    log.setTextColor(Color.GREEN);                                                                                                                                                                                                                                                                                                                                 
	                                log.setPadding(10, 5, 0, 5);
	                                log.setText(trail);                                                                                                                                                                                                                                                                                                                                   
	                                loglay.addView(log);    
	                        	}
	                         sv.addView(loglay);
	                     } finally {
	                         if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
	                     }
	            	}
	            }
	               client.logout();
	            
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                client.disconnect();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
			return null;
		}
		protected void onPostExecute(Void unused) {
    		Dialog.dismiss();
    		setContentView(sv);
    		}
    }
    public class PCTailListener extends TailerListenerAdapter {
        public void handle(String line) {
            System.out.println(line);
        } 
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.mainmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){            	
           switch (item.getItemId()) {
        case R.id.refresh:            	
        	loadLogs();
        	return true;
        case R.id.settings:
        	Intent settingsActivity = new Intent(this, Preferences.class);
        	startActivityForResult(settingsActivity,0);
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}