package utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hti.R;

public class ItemRideAdapter extends ArrayAdapter<String>{
	
	private final Context context;
	private final String[] Ids;
    private final int rowResourceId;

    public ItemRideAdapter(Context context, int textViewResourceId, String[] objects) {

        super(context, textViewResourceId);

        this.context = context;
        this.Ids = objects;
        this.rowResourceId = textViewResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(rowResourceId, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewRideRow);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewRideRow);

        ItemRide item = new ItemRide(Ids[position], R.drawable.icon_list_ride);
        
        textView.setText(item.getItemRideText());
        textView.setTextColor(Color.BLACK);
        imageView.setImageResource(item.getItemRideIconFile());
        return rowView;

    }
    
    @Override
    public int getCount() {
        return Ids.length;
    }
    
    @Override
    public String getItem(int position) {
        return Ids[position];
    }
}
