package rot.user.tekno.com.rothrow;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rot.user.tekno.com.rothrow.util.Constant;

public class HalamanUtamaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    private GoogleMap mMap;
    float zoomLevel = 16.0f;
    private String email;
    private String namaUser;
    private String token;
    public  String idus;
    public double lt;
    public double lg;
    EditText etHarga;
    TextInputEditText spMode;
    TextView txtNamaUser;
    TextView txtEmailUser;
    LinearLayout tlTampil;
    Button btnSave;
    private SharedPreferences.Editor editor;
    TextInputEditText spjenis;
    CharSequence jenis[] = {
            "Sampah Kering",
            "Sampah Basah"
    };

    CharSequence modeSampah[] = {
            "Sharing Fee",
            "Harga Sudah Ditentukan",
            "Pembuang Menentukan Harga",
            "Free"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        spjenis = (TextInputEditText)findViewById(R.id.et_pilih_jns_sampah);
        spMode = (TextInputEditText)findViewById(R.id.et_pilih_modesampah);
        etHarga = (EditText) findViewById(R.id.JnsSampahTxt);
        btnSave = (Button) findViewById(R.id.btn_order);

        tlTampil = (LinearLayout) findViewById(R.id.layoutPU);
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String getUserData = sharedPrefs.getString(Constant.KEY_SHAREDPREFS_USER_DATA, null);
        String getToken = sharedPrefs.getString(Constant.KEY_SHAREDPREFS_TOKEN, null);
        if (getUserData != null) {
            JSONObject json = null;
            try {
                json = new JSONObject(getUserData);
                idus = json.getString("id");
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

        txtNamaUser = (TextView) headerView.findViewById(R.id.txt_nav_home);
        txtNamaUser.setText(namaUser, TextView.BufferType.EDITABLE);

        txtEmailUser = (TextView) headerView.findViewById(R.id.txt_nav_email_home);
        txtEmailUser.setText(email, TextView.BufferType.EDITABLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final SearchView search = (SearchView) findViewById(R.id.svCari);
        //String cr = search.getQuery().toString();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = query.toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(HalamanUtamaActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    lt = address.getLatitude();
                    lg = address.getLongitude();
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(query));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            tlTampil.setVisibility(View.VISIBLE);
                            return true;
                        }
                    });
                    spjenis.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etHarga.setVisibility(View.VISIBLE);
                            spMode.setVisibility(View.VISIBLE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(HalamanUtamaActivity.this);
                            builder.setTitle("-- Pilih Jenis Sampah --");
                            builder.setItems(jenis, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    spjenis.setText(jenis[i]);
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                    spMode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(HalamanUtamaActivity.this);
                            builder.setTitle("-- Pilih Mode Pembuangan --");
                            builder.setItems(modeSampah, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    spMode.setText(modeSampah[i]);
                                    if(modeSampah[1] == "Free"){
                                        etHarga.setText("0", TextView.BufferType.EDITABLE);
                                    }
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            tlTampil.setVisibility(View.GONE);
                            spMode.setVisibility(View.GONE);
                            etHarga.setVisibility(View.GONE);
                        }
                    });
                }
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(HalamanUtamaActivity.this,"Masuk sini",Toast.LENGTH_LONG).show();
                        String urlHal = Constant.ENDPOINT_INSERT_ORDER;
                        Log.d("link ni: ", urlHal);
                        StringRequest req = new StringRequest(Request.Method.POST, urlHal, successListener(), errListener()){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id", idus);
                                params.put("name", namaUser);
                                params.put("jenis", spjenis.getText().toString());
                                params.put("mode", spMode.getText().toString());
                                params.put("alamat", search.getQuery().toString());
                                params.put("lat", String.valueOf(lt));
                                params.put("lang", String.valueOf(lg));
                                params.put("harga", etHarga.getText().toString());
                                params.put("status", "Waiting");

                                return params;
                            }
                        };

                        AppsController.getInstance().addToRequestQueue(req);
//                        Intent intent = new Intent(HalamanUtamaActivity.this, HalamanUtamaActivity.class);
//                        startActivity(intent);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spMode.setVisibility(View.GONE);
                etHarga.setVisibility(View.GONE);
                tlTampil.setVisibility(View.GONE);
            }
        });

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
        getMenuInflater().inflate(R.menu.halaman_utama, menu);
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
            Intent intent = new Intent(HalamanUtamaActivity.this, HalamanUtamaActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(HalamanUtamaActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            logout();
            Intent intent = new Intent(HalamanUtamaActivity.this, Login.class);
            startActivity(intent);
        } else if(id == R.id.nav_history){
            Intent intent = new Intent(HalamanUtamaActivity.this, HistoryActivity.class);
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

    private Response.ErrorListener errListener() {
        return new Response.ErrorListener() {
            @Override
            public  void onErrorResponse(VolleyError error) {
                Log.e("Error", "Login");
                Log.e("Error", String.valueOf(error));
                Toast.makeText(HalamanUtamaActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        };
    }

    private Response.Listener<String> successListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d("response", response);
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");
                    String status = json.getString("status");
                    Log.d("message", message);
                    if (status.equals("200"))
                    {
                        Toast.makeText(HalamanUtamaActivity.this,"Order Sukses",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(HalamanUtamaActivity.this, HalamanUtamaActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
