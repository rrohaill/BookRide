package rohail.bookride.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.ArrayList;

import rohail.bookride.R;
import rohail.bookride.adapters.ListAdapter;
import rohail.bookride.database.DatabaseAdapter;
import rohail.bookride.models.BookingModel;

/**
 * A History fragment containing a recycler view.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BookingModel> bookingModelList;
    private DatabaseAdapter dataBaseAdapter;
    private View rootView;

    public HistoryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_history, container, false);
        dataBaseAdapter = new DatabaseAdapter(getContext());
        fetchHistory();
        initAdapter();
        return rootView;
    }

    private void fetchHistory() {
        dataBaseAdapter = dataBaseAdapter.open();
        try {
            bookingModelList = dataBaseAdapter.selectAllProducts();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dataBaseAdapter.close();
    }

    private void initAdapter() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        if (bookingModelList == null) {
            bookingModelList = new ArrayList<>();
        }
        mAdapter = new ListAdapter(getContext(), bookingModelList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchHistory();
        initAdapter();
    }
}
