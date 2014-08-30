package kpi.ua.auttaa.sections;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import kpi.ua.auttaa.NavDrawerActivity;
import kpi.ua.auttaa.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AuttaaMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AuttaaMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AuttaaMapFragment extends Fragment {
    public static final String IS_MOVE_TO_COORDS_REQUIRED = "AuttaaMapFragment.IS_MOVE_TO_COORDS_REQUIRED";
    private OnFragmentInteractionListener mListener;


    private GoogleMap map;
    private boolean isMovementRequired;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static AuttaaMapFragment newInstance(boolean isMovetoCoordsRequired) {
        AuttaaMapFragment fragment = new AuttaaMapFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_MOVE_TO_COORDS_REQUIRED, isMovetoCoordsRequired);
        fragment.setArguments(args);
        return fragment;
    }
    public AuttaaMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(IS_MOVE_TO_COORDS_REQUIRED)) {
            Bundle args = getArguments();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setZoomControlsEnabled(false);

        if (isMovementRequired) {
            moveToLocation();
            isMovementRequired = false;
        }

        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) ((NavDrawerActivity) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onSignalize();
        public void onInfoRequest(long eventId);
    }

    void moveToLocation() {
        Location myLocation  = map.getMyLocation();
        if(myLocation!=null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            Log.i("APPLICATION", " : " + dLatitude);
            Log.i("APPLICATION"," : "+dLongitude);
//            map.addMarker(new MarkerOptions().position(
//                    new LatLng(dLatitude, dLongitude)).title("My Location").icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmap("pointer_icon.png"))));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 8));

        }
        else
        {
            Toast.makeText(getActivity(), "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }

    }

}
