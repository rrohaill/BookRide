package rohail.bookride.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import rohail.bookride.R;
import rohail.bookride.activities.BookRideActivity;
import rohail.bookride.models.BookingModel;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private BookingModel values;
    private Context context;
    private ArrayList<BookingModel> itemModelArrayList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(Context context, BookingModel itemModelArrayList) {
        values = itemModelArrayList;
        this.context = context;
    }

    public ListAdapter(Context context, ArrayList<BookingModel> itemModelArrayList) {
        this.itemModelArrayList = itemModelArrayList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (context instanceof BookRideActivity) {
            setMainView(holder, position);
        } else {
            setHistoryView(holder, position);
        }

    }

    private void setHistoryView(ViewHolder holder, int position) {
        holder.txtLeft.setText(itemModelArrayList.get(position).getDropLocation());
        holder.txtRight.setText(itemModelArrayList.get(position).getPickupTime());
    }

    private void setMainView(ViewHolder holder, int position) {

        switch (position) {
            case 0:
                holder.txtLeft.setText("Name");
                holder.txtRight.setText(values.getName());
                break;
            case 1:
                holder.txtLeft.setText("Contact");
                holder.txtRight.setText(values.getContact());
                break;
            case 2:
                holder.txtLeft.setText("Pick up location");
                holder.txtRight.setText(values.getPickupLocation());
                break;
            case 3:
                holder.txtLeft.setText("Drop off location");
                holder.txtRight.setText(values.getDropLocation());
                break;
            case 4:
                holder.txtLeft.setText("Pick up time");
                holder.txtRight.setText(values.getPickupTime());
                break;
            case 5:
                holder.txtLeft.setText("Car");
                holder.txtRight.setText(values.getCarType());
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (context instanceof BookRideActivity)
            return 6;
        else return itemModelArrayList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtLeft;
        public TextView txtRight;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtLeft = v.findViewById(R.id.txt_left);
            txtRight = v.findViewById(R.id.txt_right);
        }
    }

}
