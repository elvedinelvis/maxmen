package maxmen.maxmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//import android.content.BroadcastReceiver;


public class UpdaterService extends Service {

    Updater updater;
    //BroadcastReceiver broadcaster;
    Intent intent;

    //If you want to use a real bus, use: BUS = "Ericsson$100021"
    //But only on weekdays (not weekends!!!) during 06:00-18:00 GMT+1
    final private String BUS = "Ericsson$Vin_Num_001", SIGNAL = "Ericsson$GPS2";
    static final public String BROADCAST_ACTION = "SendGpsLocation";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updater = new Updater();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {

        if (!updater.isRunning()) {
            updater.start();
            updater.isRunning = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();

        if (updater.isRunning) {
            updater.interrupt();
            updater.isRunning = false;
            updater = null;
        }

    }

    /**
     * Thread which gets and broadcasts the gps coordinates for the bus every 5 seconds
     */
    class Updater extends Thread {

        public boolean isRunning = false;
        public long DELAY = 5000; //5 seconds delay

        @Override
        public void run() {
            super.run();

            isRunning = true;
            while (isRunning) {
                try {
                    //*Calls getSignalData which fetches our signal
                    //from the ElectriCity API*
                    //Sends broadcast
                    sendGPScoords(findLatAndLong(getSignalData(BUS,SIGNAL)));
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isRunning = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    isRunning = false;
                } catch (StringIndexOutOfBoundsException e){
                    //When bus doesn't send any gps data
                    e.printStackTrace();
                    isRunning = false;
                }
            }
        }

        public boolean isRunning() {
            return this.isRunning;
        }

    }

    /**
     * Help-method which broadcasts gps coordinates, used in thread
     */
    public void sendGPScoords(String[] coords) {
        intent.putExtra("latitude", coords[0]);
        intent.putExtra("longitude", coords[1]);
        sendBroadcast(intent);
    }

    /**
     * Reads data from signals of the ElectriCity API
     * @param bus,signal  Strings for the names of the bus and signal
     * @return String  Returns the data gathered from the signal as a long string
     */
    public String getSignalData(String bus, String signal) throws IOException {
        long t2 = System.currentTimeMillis();
        long t1 = t2 - (1000 * 120);

        StringBuffer response = new StringBuffer();
        //The key for the ElectriCity API (BASE64 encoded)
        String key = "Z3JwMzQ6eWNGUmJjQjR5Tw==";
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=" + bus + "&sensorSpec="
                + signal + "&t1=" + t1 + "&t2=" + t2;

        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + key);

        //Used for debugging
        int responseCode = con.getResponseCode();
        String responseMsg = con.getResponseMessage();
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);
        //System.out.println("Response Message : " + responseMsg);

        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return(response.toString());
    }

    /**
     * Filters the lat & lng coordinates from the Ericsson$GPS2 signal of the API
     * @param s  The gathered data string from the signal
     * @return String[]  String array with latitude and longitude
     */
    public String[] findLatAndLong(String s){
        int index1 = s.indexOf("Latitude2_Value") + 50;
        int index2 = index1 + 15;
        int index3 = s.indexOf("Longitude2_Value") + 50;
        int index4 = index3 + 15;

        //Regex doesn't include dashes for negative lat/long yet <--- IMPORTANT!!!
        return (s.substring(index1,index2).replaceAll("[^\\d.]", "") +
                " " + s.substring(index3,index4).replaceAll("[^\\d.]", "")).split("\\s");
    }

}
