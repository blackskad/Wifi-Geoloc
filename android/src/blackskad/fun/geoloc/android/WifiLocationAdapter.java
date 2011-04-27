package blackskad.fun.geoloc.android;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 *
 * @author Thomas Meire
 */
public class WifiLocationAdapter extends ArrayAdapter<WifiLocation> {

  private final Activity context;

  public WifiLocationAdapter(Activity context) {
    super(context, R.layout.row);
    this.context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    WifiLocation wifi = getItem(position);

    LayoutInflater inflater = context.getLayoutInflater();
    View row = inflater.inflate(R.layout.row, null, true);
    TextView bssid = (TextView) row.findViewById(R.id.bssid);
    TextView latitude = (TextView) row.findViewById(R.id.latitude);
    TextView longitude = (TextView) row.findViewById(R.id.longitude);

    bssid.setText(wifi.network.BSSID);
    latitude.setText("" + wifi.location.getLatitude());
    longitude.setText("" + wifi.location.getLongitude());
    
    return row;
  }
}
