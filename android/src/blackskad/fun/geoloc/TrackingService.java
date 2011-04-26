package blackskad.fun.geoloc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Thomas Meire
 */
public class TrackingService extends Service {

  private static final String TAG = "WifiGeolocTrackingService";

  private WifiManager wifiManager;

  private LocationManager locationManager;

  private LocationListener listener;

  private Location location;

  private BroadcastReceiver receiver;

  private final IBinder binder = new LocalBinder();

  @Override
  public void onCreate() {
    super.onCreate();
    wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if (wifiManager == null) {
      Log.e(TAG, "could not get wifi manager.");
      throw new NullPointerException("WifiManager is null!");
    }

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (locationManager == null) {
      Log.e(TAG, "could not get location manager.");
      throw new NullPointerException("LocationManager is null!");
    }

    listener = new GPSLocationListener();

    receiver = new WifiBroadcastReceiver();


    // register a listener for GPS location updates
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, listener);

    // register a broadcast receiver for new scan results
    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    registerReceiver(receiver, filter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
    locationManager.removeUpdates(listener);
  }

  private final Queue<WifiLocation> locations = new LinkedList<WifiLocation>();
  
  public WifiLocation get() {
    synchronized (locations) {
      return locations.poll();
    }
  }

  private void log(List<ScanResult> networks) {

    if (location != null) {
      for (ScanResult network : networks) {
        Log.i(TAG, "Found network " + network.BSSID + " at location " + location.toString());

        WifiLocation wifi = new WifiLocation();
        wifi.location = location;
        wifi.network = network;
        
        synchronized (locations) {
          locations.offer(wifi);
        }
        sendBroadcast(new Intent(ControlActivity.TrackingUpdateReceiver.class.getName()));
      }
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  public class LocalBinder extends Binder {

    public TrackingService getService() {
      return TrackingService.this;
    }
  }

  private class GPSLocationListener implements LocationListener {

    public void onLocationChanged(Location lctn) {
      location = lctn;
    }

    public void onStatusChanged(String string, int i, Bundle bundle) {
    }

    public void onProviderEnabled(String string) {
    }

    public void onProviderDisabled(String string) {
    }
  }

  private class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context cntxt, Intent intent) {
      if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
        log(wifiManager.getScanResults());
      }
    }
  }
}
