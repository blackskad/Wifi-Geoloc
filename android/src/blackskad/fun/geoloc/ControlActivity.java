package blackskad.fun.geoloc;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;

public class ControlActivity extends ListActivity {
  
  private TrackingService tracker;
  
  private ArrayAdapter adapter;
  
  private BroadcastReceiver receiver = new TrackingUpdateReceiver();
  
  private ServiceConnection connection = new ServiceConnection() {
    
    public void onServiceConnected(ComponentName cn, IBinder service) {
      tracker = ((TrackingService.LocalBinder) service).getService();
    }
    
    public void onServiceDisconnected(ComponentName cn) {
      tracker = null;
    }
  };

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // start & bind to the data providere service
    bindService(new Intent("blackskad.fun.geoloc.TrackingService"), connection, Context.BIND_AUTO_CREATE);
    
    registerReceiver(receiver, new IntentFilter(ControlActivity.TrackingUpdateReceiver.class.getName()));
    
    adapter = new WifiLocationAdapter(this);
    setListAdapter(adapter);
    
    setContentView(R.layout.main);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
    unbindService(connection);
  }

  public class TrackingUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context cntxt, Intent intent) {
      adapter.add(tracker.get());
    }
  }
}
