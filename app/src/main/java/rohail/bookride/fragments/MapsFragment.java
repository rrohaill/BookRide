package rohail.bookride.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rohail.bookride.R;
import rohail.bookride.activities.BookRideActivity;
import rohail.bookride.adapters.FusedLocationService;
import rohail.bookride.models.BookingModel;
import rohail.bookride.utils.PopupDialogs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, FusedLocationService.LocationChangedInterface {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private AutoCompleteTextView etPickup;
    private AutoCompleteTextView etDrop;
    private FusedLocationService fusedLocationService;
    private View fragmentView;
    private Location location;
    private String latitude, longitude;
    private Place pickupPlace, dropPlace;
    private PlaceAutocompleteFragment pickupAutocompleteFragment;
    private PlaceAutocompleteFragment dropAutocompleteFragment;
    private Button btnSedan, btnHatchBack, btnSUV;
    private BookingModel bookingModel;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapsFragment.
     */
    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_maps, container, false);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
        } else {
            initView(fragmentView);
        }
        return fragmentView;
    }

    private void initView(View view) {
        bookingModel = new BookingModel();

        mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        pickupAutocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.pickup_autocomplete_fragment);
        pickupAutocompleteFragment.setHint(getString(R.string.pickup_search));
        dropAutocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.drop_autocomplete_fragment);
        dropAutocompleteFragment.setHint(getString(R.string.drop_search));

        setAutoCompleteListeners();

        btnSedan = view.findViewById(R.id.btn_sedan);
        btnHatchBack = view.findViewById(R.id.btn_hatchback);
        btnSUV = view.findViewById(R.id.btn_suv);

        btnSedan.setOnClickListener(this);
        btnHatchBack.setOnClickListener(this);
        btnSUV.setOnClickListener(this);

    }

    private void setAutoCompleteListeners() {
        pickupAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                pickupPlace = place;
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                        title(place.getAddress().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

                if (dropPlace != null) {
                    getDirections();
                }
            }

            @Override
            public void onError(Status status) {

            }
        });

        dropAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                dropPlace = place;
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                        title(place.getAddress().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

                if (pickupPlace != null) {
                    getDirections();
                }
            }

            @Override
            public void onError(Status status) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationService = new FusedLocationService(this, getContext(), getActivity());
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    final Address address = getAddress();
                    pickupAutocompleteFragment.setText(address.getSubLocality() + "");
                    setPickupPlace(address);
                    return true;
                }
            });
        }
    }

    /*
    get address from lat lng
     */
    private Address getAddress() {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            ioException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            illegalArgumentException.printStackTrace();
        }

        return addresses.get(0);
    }

    /*
    Location permission
     */
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                PopupDialogs.createAlertDialog("You need to allow ACCESS to device location", "Permission Required", getContext(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_PERMISSIONS);
                    }
                });
                return;
            }
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        } else {
            mMap.setMyLocationEnabled(true);
            initView(fragmentView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView(fragmentView);
                } else {
                    Toast.makeText(getContext(), "LOCATION permissions not granted", Toast.LENGTH_LONG).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_sedan:
                btnSedan.setBackgroundResource(R.drawable.button_left_round_pressed);
                btnHatchBack.setBackgroundResource(R.drawable.button_centre);
                btnSUV.setBackgroundResource(R.drawable.button_right_round);

                bookingModel.setCarType(btnSedan.getText().toString());
                break;
            case R.id.btn_hatchback:
                btnHatchBack.setBackgroundResource(R.drawable.button_centre_pressed);
                btnSedan.setBackgroundResource(R.drawable.button_left_round);
                btnSUV.setBackgroundResource(R.drawable.button_right_round);

                bookingModel.setCarType(btnHatchBack.getText().toString());
                break;
            case R.id.btn_suv:
                btnSUV.setBackgroundResource(R.drawable.button_right_round_pressed);
                btnHatchBack.setBackgroundResource(R.drawable.button_centre);
                btnSedan.setBackgroundResource(R.drawable.button_left_round);

                bookingModel.setCarType(btnSUV.getText().toString());
                break;
            default:
                break;
        }

        populateModel();
        Intent intent = new Intent(getContext(), BookRideActivity.class);
        intent.putExtra(getString(R.string.booking_model), bookingModel);
        startActivity(intent);
    }

    private void populateModel() {
        bookingModel.setContact("+48-729221614");
        bookingModel.setDropLocation(dropPlace.getName().toString());
        bookingModel.setName("Rohail Ahmad");
        bookingModel.setPickupLocation(pickupPlace.getName().toString());
        bookingModel.setPickupTime(getCurrentTime());
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        String strDate = calendar.getTime().toString();
        return strDate;
    }

    /*
    calls when location changes
     */
    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        this.location = location;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                14.0f));

    }

    /*
    Draw line on maps
     */
    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void getDirections() {

        DateTime now = new DateTime();
        try {
            DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin((String) pickupPlace.getAddress())
                    .destination((String) dropPlace.getAddress()).departureTime(now)
                    .await();
            addMarkersToMap(result, mMap);
            addPolyline(result, mMap);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupPlace.getLatLng(),
                    14.0f));

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,
                results.routes[0].legs[0].startLocation.lng)).
                title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,
                results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].startAddress).
                snippet(getEndLocationTitle(results)));
    }

    public void setPickupPlace(final Address address) {
        pickupPlace = new Place() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public List<Integer> getPlaceTypes() {
                return null;
            }

            @Nullable
            @Override
            public CharSequence getAddress() {
                return address.getAddressLine(0);
            }

            @Override
            public Locale getLocale() {
                return address.getLocale();
            }

            @Override
            public CharSequence getName() {
                return address.getSubLocality();
            }

            @Override
            public LatLng getLatLng() {
                return new LatLng(address.getLatitude(), address.getLongitude());
            }

            @Nullable
            @Override
            public LatLngBounds getViewport() {
                return null;
            }

            @Nullable
            @Override
            public Uri getWebsiteUri() {
                return null;
            }

            @Nullable
            @Override
            public CharSequence getPhoneNumber() {
                return null;
            }

            @Override
            public float getRating() {
                return 0;
            }

            @Override
            public int getPriceLevel() {
                return 0;
            }

            @Nullable
            @Override
            public CharSequence getAttributions() {
                return null;
            }

            @Override
            public Place freeze() {
                return null;
            }

            @Override
            public boolean isDataValid() {
                return false;
            }
        };
    }
}
