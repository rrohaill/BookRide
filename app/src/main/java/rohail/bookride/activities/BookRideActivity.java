package rohail.bookride.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import rohail.bookride.R;
import rohail.bookride.adapters.ListAdapter;
import rohail.bookride.database.DatabaseAdapter;
import rohail.bookride.models.BookingModel;

public class BookRideActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnCancel;
    private Button btnGo;
    private RecyclerView recyclerView;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private BookingModel bookingModel;
    private DatabaseAdapter dataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);

        bookingModel = (BookingModel) getIntent().getExtras().get(getString(R.string.booking_model));
        dataBaseAdapter = new DatabaseAdapter(this);
        initView();
        initAdapter();
    }

    private void initView() {
        btnCancel = findViewById(R.id.btn_cancel);
        btnGo = findViewById(R.id.btn_go);
        recyclerView = findViewById(R.id.recycler_view);

        btnCancel.setOnClickListener(this);
        btnGo.setOnClickListener(this);
    }

    private void initAdapter() {

        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListAdapter(this, bookingModel);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_go:
                saveData();
                Toast.makeText(this, "All done!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void saveData() {
        dataBaseAdapter = dataBaseAdapter.open();
        dataBaseAdapter.insertEntry(bookingModel.getName(), bookingModel.getContact(),
                bookingModel.getPickupLocation(), bookingModel.getDropLocation(),
                bookingModel.getPickupTime(), bookingModel.getCarType());
        dataBaseAdapter.close();
    }
}
