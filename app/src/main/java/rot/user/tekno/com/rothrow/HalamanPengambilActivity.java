package rot.user.tekno.com.rothrow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rot.user.tekno.com.rothrow.model.ListPengambil;
import rot.user.tekno.com.rothrow.util.Constant;

public class HalamanPengambilActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    float zoomLevel = 16.0f;
    private String email;
    private String namaUser;
    private String token;
    private String url;

    Gson gson;
    TextView txtNamaUser;
    TextView txtEmailUser;
    TextView nm;
    TextView jnsSmph;
    TextView hrg;
    TextView mdSmph;
    RelativeLayout tlTampil;
    private SharedPreferences.Editor editor;
    List<ListPengambil> list_data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_pengambil);
        gson = new Gson();
        tlTampil = (RelativeLayout) findViewById(R.id.layoutPopup);
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        //Log.d("token", token);

        txtNamaUser = (TextView) findViewById(R.id.et_nama_user);
        //txtNamaUser.setText(namaUser);

        txtEmailUser = (TextView) findViewById(R.id.et_email_user);
        //txtEmailUser.setText(email);

        nm = (TextView) findViewById(R.id.tv_nama_pembuang);
        jnsSmph = (TextView) findViewById(R.id.tv_jns_sampah);
        hrg = (TextView) findViewById(R.id.tv_harga);
        mdSmph = (TextView) findViewById(R.id.tv_mode_sampah);

        editor = sharedPrefs.edit();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getDataPosisi();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.halaman_pengambil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(HalamanPengambilActivity.this, HalamanUtamaActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(HalamanPengambilActivity.this, ProfilePengambilActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            logout();
            Intent intent = new Intent(HalamanPengambilActivity.this, Login.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(){
        editor.putString(Constant.KEY_SHAREDPREFS_USER_DATA, null);
        editor.putString(Constant.KEY_SHAREDPREFS_LOGIN_STATUS, "0");
        editor.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Jakarta and move the camera
        LatLng jakarta = new LatLng(-6.252884, 106.8469404);
        mMap.addMarker(new MarkerOptions().position(jakarta).title("Marker in Jakarta"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(jakarta));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jakarta, zoomLevel));
    }

    public void getDataPosisi(){
        url = Constant.ENDPOINT_GET_ORDER;
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        Log.d("token", token);
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
                    ListPengambil dataBaru = list_data.get(x);
                    tlTampil.setVisibility(View.VISIBLE);
                    nm.setText(dataBaru.getNama().toString(), TextView.BufferType.EDITABLE);
                    jnsSmph.setText(dataBaru.getJenisSp().toString(), TextView.BufferType.EDITABLE);
                    hrg.setText(dataBaru.getHarga().toString(), TextView.BufferType.EDITABLE);
                    mdSmph.setText(dataBaru.getModePb().toString(), TextView.BufferType.EDITABLE);
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
}
