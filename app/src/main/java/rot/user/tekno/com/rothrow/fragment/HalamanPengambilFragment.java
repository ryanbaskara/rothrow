package rot.user.tekno.com.rothrow.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.esafirm.imagepicker.features.ImagePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rot.user.tekno.com.rothrow.AppsController;
import rot.user.tekno.com.rothrow.R;
import rot.user.tekno.com.rothrow.model.ListPengambil;
import rot.user.tekno.com.rothrow.util.Constant;
import rot.user.tekno.com.rothrow.util.MLocation;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class HalamanPengambilFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    float zoomLevel = 16.0f;
    private String email;
    private String namaUser;
    private String token;
    private String url;

    Gson gson;
    TextView nm;
    TextView jnsSmph;
    TextView hrg;
    TextView mdSmph;
    Button btnOrder;
    RelativeLayout tlTampil;
    private SharedPreferences.Editor editor;
    List<ListPengambil> list_data = new ArrayList<>();
    View view;
    Location myLocation;

    public HalamanPengambilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_halaman_pengambil, container, false);
        gson = new Gson();
        myLocation = MLocation.getLocation(getContext());
        tlTampil = (RelativeLayout) view.findViewById(R.id.layoutPopup);
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String getUserData = sharedPrefs.getString(Constant.KEY_SHAREDPREFS_USER_DATA, null);
        String getToken = sharedPrefs.getString(Constant.KEY_SHAREDPREFS_TOKEN, null);
        if (getUserData != null) {
            JSONObject json = null;
            try {
                json = new JSONObject(getUserData);
                namaUser = json.getString("ro_nama_pengguna");
                email = json.getString("ro_email");
                //Log.d("id adalah: ",email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (getToken != null) {
            token = getToken;
        }

        nm = (TextView) view.findViewById(R.id.tv_nama_pembuang);
        jnsSmph = (TextView) view.findViewById(R.id.tv_jns_sampah);
        hrg = (TextView) view.findViewById(R.id.tv_harga);
        mdSmph = (TextView) view.findViewById(R.id.tv_mode_sampah);
        btnOrder = (Button) view.findViewById(R.id.btnOrder);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getDataPosisi();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Jakarta and move the camera
        LatLng jakarta = new LatLng(-6.252884, 106.8469404);
        mMap.addMarker(new MarkerOptions().position(jakarta).title("Marker in Jakarta"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jakarta, zoomLevel));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta));
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void getDataPosisi(){
        url = Constant.ENDPOINT_GET_ORDER;
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String getLocalData = sharedPrefs.getString(Constant.KEY_SHAREDPREFS_TOKEN, null);
        if (getLocalData!=null) {
            token = getLocalData;
        }
        StringRequest req = new StringRequest(Request.Method.GET, url, successListener(), errListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        AppsController.getInstance().addToRequestQueue(req);
    }

    private Response.ErrorListener errListener() {
        return new Response.ErrorListener() {
            @Override
            public  void onErrorResponse(VolleyError error) {
                Log.e("Error", "Error get view booking");
                Log.e("Error", String.valueOf(error));
            }
        };
    }
    private Response.Listener<String> successListener(){
        return new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Log.d("respone",response);
                try{
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("status") == 200) {
                        String jsonOutput = json.getString("data");
                        Type listType = new TypeToken<List<ListPengambil>>(){}.getType();
                        list_data = (List<ListPengambil>) gson.fromJson(jsonOutput, listType);

                    } else {
                        //callback.onFinish(false, "failed");
                    }
                    putMarker();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void putMarker(){
        mMap.clear();
        Log.d("List data: ",list_data.toString());
        for(int i = 0; i<list_data.size(); i++){
            LatLng newMarker = new LatLng(list_data.get(i).getLat(), list_data.get(i).getLang());
            mMap.addMarker(new MarkerOptions().position(newMarker).title(list_data.get(i).getAlamat())).setTag(i);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    int x = (int) marker.getTag();
                    final ListPengambil dataBaru = list_data.get(x);
                    tlTampil.setVisibility(View.VISIBLE);
                    nm.setText(dataBaru.getNama().toString(), TextView.BufferType.EDITABLE);
                    jnsSmph.setText(dataBaru.getJenisSp().toString(), TextView.BufferType.EDITABLE);
                    hrg.setText(String.valueOf(dataBaru.getHarga()), TextView.BufferType.EDITABLE);
                    mdSmph.setText(dataBaru.getModePb().toString(), TextView.BufferType.EDITABLE);
                    btnOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myLocation = MLocation.getLocation(getContext());
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr="+
                                            String.valueOf(myLocation.getLatitude())+","+String.valueOf(myLocation.getLongitude())+
                                            "&daddr="+String.valueOf(dataBaru.getLat())+","+String.valueOf(dataBaru.getLang())));
                            startActivity(intent);
//                            getFotoKtp(12);
                        }
                    });

                    return true;
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    tlTampil.setVisibility(View.GONE);
                }
            });
        }
    }

    private void getFotoKtp(int i) {
        ImagePicker.create(this) // Activity or Fragment
                .single().showCamera(true)
                .start(i);
    }

    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && null != data) {

            File file = new File(ImagePicker.getImages(data).get(0).getPath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_gambar);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            imageView.setImageBitmap(rotatedBitmap);
            imageView.setMaxHeight(bitmap.getHeight());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0,0,0,0);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();

            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("base", encodedImage);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
