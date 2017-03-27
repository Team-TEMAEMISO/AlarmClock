package temaemiso.jp.weatheralarm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Created by y-kawase on 2017/03/21.
 */

public class SwitchAdapter extends SimpleAdapter {

    int switchId;
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public SwitchAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,int switchId) {
        super(context, data, resource, from, to);
        this.switchId = switchId;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);

        Switch switch1 = (Switch)view.findViewById(switchId);
        switch1.setTag(position);

        final ListView list =  (ListView) parent;
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AdapterView.OnItemClickListener listener = list.getOnItemClickListener();
                    long id = getItemId(position);
                    listener.onItemClick((AdapterView<?>)parent,buttonView,position,id);
                }
            }
        });

        return view;
    }
}
