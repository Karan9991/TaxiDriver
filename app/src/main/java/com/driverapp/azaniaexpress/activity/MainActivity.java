package com.driverapp.azaniaexpress.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.driverapp.azaniaexpress.R;
import com.driverapp.azaniaexpress.circleimage.CircleImageView;
import com.driverapp.azaniaexpress.controller.AppController;
import com.driverapp.azaniaexpress.fragment.FragmentProfile;
import com.driverapp.azaniaexpress.fragment.driver.FragmentDashboard;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideBookingConfirmedDriver;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideBookingNewDriver;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideBookingRejectedDriver;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideCanceledDriver;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideCompletedDriver;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideConfirmedDriver;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideNewDriver;
import com.driverapp.azaniaexpress.fragment.driver.FragmentRideOnRideDriver;
import com.driverapp.azaniaexpress.model.DrawerPojo;
import com.driverapp.azaniaexpress.model.M;
import com.driverapp.azaniaexpress.settings.AppConst;
import com.driverapp.azaniaexpress.settings.ConnectionDetector;
import com.driverapp.azaniaexpress.settings.PrefManager;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private RecyclerView mDrawerList;
    public static DrawerLayout mDrawerLayout;
    public static Context context;
    private static ConnectionDetector connectionDetector;
    DrawerAdapter drawerAdapter;
    public static ArrayList<DrawerPojo> list=new ArrayList<>();
    public static PrefManager prefManager;
    private FrameLayout drawer_conducteur;
    private LinearLayout drawer_user;
    public static TextView user_name, user_phone,statut_conducteur, balance;
    private SwitchCompat switch_statut;
    private static final int LOCATION_REQUEST_CODE = 101;
    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;
    public static Activity activity;
    private Boolean notification = false;
    public static CircleImageView user_photo;
    private static TextView save_note;
    private static TextView cancel_note;
    private static TextInputLayout intput_layout_comment;
    private static EditText input_edit_comment;
    private static RatingBar rate_conducteur;

    /** MAP **/
    public static GoogleMap mMap;
    public static Location currentLocation;

    /** GOOGLE API CLIENT **/
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    private LocationManager locationManager;
    private String provider;
    public static String amount,id_ride,position,id_driver,note_,img,comment;

    public static AlertDialog alertDialog;
    private static TextView title;
    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";

    private static TextView ok, time;
    private static ProgressBar progressBar2;
    private static CountDownTimer countDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        activity = this;
        connectionDetector=new ConnectionDetector(context);
        prefManager = new PrefManager(this);

        title = (TextView) findViewById(R.id.title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle objetbundle = this.getIntent().getExtras();
        String fragment_name = objetbundle.getString("fragment_name");

        if(!fragment_name.equals("")){
            notification = true;
            if(fragment_name.equals("ridenewrider"))
                selectItem(1);
            else if(fragment_name.equals("ridecompleted"))
                selectItem(4);
                /*else if(fragment_name.equals("ridecanceledrider"))
                    selectItem(1);*/
        }

        balance = (TextView) findViewById(R.id.balance);
        user_photo = (CircleImageView) findViewById(R.id.user_photo);
        switch_statut = (SwitchCompat) findViewById(R.id.switch_statut);
        user_name = (TextView) findViewById(R.id.user_name);
        user_phone = (TextView) findViewById(R.id.user_phone);
        statut_conducteur = (TextView) findViewById(R.id.statut_conducteur);
        drawer_conducteur = (FrameLayout) findViewById(R.id.drawer_conducteur);
        drawer_user = (LinearLayout) findViewById(R.id.drawer_user);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerLayout.setScrimColor(Color.GRAY);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
//        toggle.getDrawerArrowDrawable().setVehicleColor(getResources().getColor(R.color.white));
        toggle.syncState();

        NavigationView navigationViewLeft = (NavigationView) findViewById(R.id.nav_view);
//        int width = R.dimen.drawer_width;//getResources().getDisplayMetrics().widthPixels;
//        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationViewLeft.getLayoutParams();
//        params.width = width;
//        navigationViewLeft.setLayoutParams(params);
        mDrawerList = (RecyclerView) findViewById(R.id.rvdrawer);
        mDrawerList.setLayoutManager(new LinearLayoutManager(context));
        mDrawerList.setHasFixedSize(true);

        setDrawer();

        if (savedInstanceState == null) {
            if(fragment_name.equals(""))
                selectItem(0);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//            return;
        }

        if(!isLocationEnabled(context))
            showMessageEnabledGPS();

        balance.setVisibility(View.GONE);
        if (M.getStatutConducteur(context).equals("yes")) {
            switch_statut.setChecked(true);
            statut_conducteur.setText("enabled");
        } else {
            switch_statut.setChecked(false);
            statut_conducteur.setText("disabled");
        }

        switch_statut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switch_statut.isChecked()) {
                    M.showLoadingDialog(context);
                    new changerStatut().execute("yes");
                }else {
                    M.showLoadingDialog(context);
                    new changerStatut().execute("no");
                }
            }
        });

        // loading model cover using Glide library
        if(!M.getPhoto(context).equals("")) {
            // loading model cover using Glide library
            Glide.with(context).load(AppConst.Server_url + "images/app_user/" + M.getPhoto(context))
                    .skipMemoryCache(false)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                            user_photo.setImageDrawable(getResources().getDrawable(R.drawable.user_profile));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(user_photo);
        }else{
            user_photo.setImageDrawable(getResources().getDrawable(R.drawable.user_profile));
        }

        // Get the location manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (provider != null) {
            currentLocation = locationManager.getLastKnownLocation(provider);
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(context)
//                .enableAutoManage(getActivity(),0,this)
                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        updateFCM(M.getID(context));
//        new getWallet().execute();
    }

    public static void setTitle(String title_){
        title.setText(title_);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(context, PayPalService.class));
        currentLocation = null;
        super.onDestroy();
    }

    /** Récupération user wallet**/
    public class getWallet extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"get_wallet.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    balance.setText(M.getCurrency(context)+" "+msg.getString("amount"));
                                }else{

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_user", M.getID(context));
                    params.put("cat_user", M.getUserCategorie(context));
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //to add spacing between cards
            if (this != null) {

            }

        }

        @Override
        protected void onPreExecute() {

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
//        Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();

        if (currentLocation != null) {
            new setCurrentLocation().execute(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
        }
    }

    /** MAJ de la position d'un conducteur **/
    private class setCurrentLocation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"set_position.php";
            final String latitude = params[0];
            final String longitude = params[1];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_user", M.getID(context));
                    params.put("user_cat", M.getUserCategorie(context));
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //to add spacing between cards
            if (this != null) {

            }

        }

        @Override
        protected void onPreExecute() {

        }
    }

    /** Start COOGLE API Client **/
    @Override
    public void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /** Change driver status **/
    private class changerStatut extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"change_statut.php";
            final String online = params[0];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                String online = msg.getString("online");
                                if(etat.equals("1")){
                                    if(online.equals("yes")) {
                                        switch_statut.setChecked(true);
                                        statut_conducteur.setText("enabled");
                                        M.setStatutConducteur(online,context);
                                    }else {
                                        switch_statut.setChecked(false);
                                        statut_conducteur.setText("disabled");
                                        M.setStatutConducteur(online,context);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                    if(switch_statut.isChecked())
                        switch_statut.setChecked(false);
                    else
                        switch_statut.setChecked(true);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_driver", M.getID(context));
                    params.put("online", online);
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //to add spacing between cards
            if (this != null) {

            }

        }

        @Override
        protected void onPreExecute() {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if(!isLocationEnabled(context))
                    showMessageEnabledGPS();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showMessageEnabledGPSClient(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.this_service_requires_the_activation_of_the_gps))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void showMessageEnabledGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Activez le service GPS pour partager votre position avec les clients. Activez le GPS maintenant ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isLocationEnabled(Context context){
//        String locationProviders;
        boolean enabled = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            enabled = (mode != Settings.Secure.LOCATION_MODE_OFF);
        }else{
            LocationManager service = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            enabled =  service.isProviderEnabled(LocationManager.GPS_PROVIDER)||service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return enabled;
    }

    public void setDrawer() {
        mDrawerLayout.setFocusable(false);
        list.clear();
        drawer_conducteur.setVisibility(View.VISIBLE);
        switch_statut.setVisibility(View.VISIBLE);
        drawer_user.setVisibility(View.GONE);
        drawer_user.setVisibility(View.GONE);
        user_name.setText("");
        user_phone.setText("");

        list.add(new DrawerPojo(1, "", getString(R.string.item_dashboard), R.drawable.ic_dashboard));
        list.add(new DrawerPojo(2, "", getString(R.string.item_new), R.drawable.ic_new));
        list.add(new DrawerPojo(3, "", getString(R.string.item_confirmed), R.drawable.ic_confirm));
        list.add(new DrawerPojo(4, "", getString(R.string.item_onride), R.drawable.ic_completed));
        list.add(new DrawerPojo(5, "", getString(R.string.item_completed), R.drawable.ic_completed));
        list.add(new DrawerPojo(6, "", getString(R.string.item_rejected), R.drawable.ic_error));

        list.add(new DrawerPojo(10, "", getString(R.string.item_new_booking), R.drawable.ic_calendar));
        list.add(new DrawerPojo(11, "", getString(R.string.item_confirmed_booking), R.drawable.ic_calendar_check));
        list.add(new DrawerPojo(12, "", getString(R.string.item_rejected_booking), R.drawable.ic_calendar_cancel));
//            list.add(new DrawerPojo(13, "", getString(R.string.item_wallet), R.drawable.ic_wallet));
        list.add(new DrawerPojo(7, "", getString(R.string.item_profile), R.drawable.ic_profile_outline));
        list.add(new DrawerPojo(0, "", getString(R.string.item_logout), R.drawable.ic_logout_outline));
        list.add(new DrawerPojo(8, "", "divider", 0));
        list.add(new DrawerPojo(9, "", getString(R.string.item_help), 0));
        list.add(new DrawerPojo(13, "", getString(R.string.item_contact_us), R.drawable.ic_assistance_outline));

        drawerAdapter=new DrawerAdapter(list,context);
        mDrawerList.setAdapter(drawerAdapter);
    }

    public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<DrawerPojo> mDrawerItems;
        Context context;

        public DrawerAdapter(List<DrawerPojo> list, Context mcontext) {
            context = mcontext;
            mDrawerItems = list;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false);
            return new ViewHolderPosts(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
            final ViewHolderPosts holder = (ViewHolderPosts) holder1;
            DrawerPojo item = mDrawerItems.get(position);

            if(item.getmText().equals("divider")) {
                holder.line_divider.setVisibility(View.VISIBLE);
                holder.layout_item.setVisibility(View.GONE);
            }else {
                holder.line_divider.setVisibility(View.GONE);
                holder.layout_item.setVisibility(View.VISIBLE);
                holder.title.setText(item.getmText());
                holder.llrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectItem(position);
                    }
                });
            }

            if(item.getmIconRes() == 0) {
                holder.img.setVisibility(View.GONE);
            }else {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setImageDrawable(getResources().getDrawable(item.getmIconRes()));
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mDrawerItems.size();
        }

        public class ViewHolderPosts extends RecyclerView.ViewHolder {
            TextView title;
            LinearLayout llrow;
            ImageView img;
            View line_divider;
            LinearLayout layout_item;

            public ViewHolderPosts(View convertView) {
                super(convertView);
                llrow=(LinearLayout)convertView.findViewById(R.id.llrow);
                title = (TextView) convertView.findViewById(R.id.tvtitle);
                img = (ImageView) convertView.findViewById(R.id.img);
                line_divider = (View) convertView.findViewById(R.id.line_divider);
                layout_item = (LinearLayout) convertView.findViewById(R.id.layout_item);
            }
        }
    }

    public static void selectItem(int pos1){
        Fragment fragment = null;
        long pos=list.get(pos1).getmId();
        String item=list.get(pos1).getmText();
        String tag = "home";

        if(pos==1) {
            fragment = new FragmentDashboard();
            tag = "dashboard";
        }else if(pos==2){
            fragment=new FragmentRideNewDriver();
            tag = "new";
        }else if(pos==3){
            fragment=new FragmentRideConfirmedDriver();
            tag = "confirmed";
        }else if(pos==4){
            fragment=new FragmentRideOnRideDriver();
            tag = "on_ride";
        }else if(pos==5){
            fragment=new FragmentRideCompletedDriver();
            tag = "completed";
        }else if(pos==6){
            fragment=new FragmentRideCanceledDriver();
            tag = "canceled";
        }else if(pos==7){
            fragment=new FragmentProfile();
            tag = "profil";
        }else if(pos==10){
            fragment=new FragmentRideBookingNewDriver();
            tag = "new_book";
        }else if(pos==11){
            fragment=new FragmentRideBookingConfirmedDriver();
            tag = "confrimed_book";
        }else if(pos==12){
            fragment=new FragmentRideBookingRejectedDriver();
            tag = "rejected_book";
        }else if(pos==13){
            openBrowser(context, "https://codecanyon.net/item/taxi-cab-on-demand-taxi/25137864");
        }/*else if(pos==2){
                fragment=new FragmentMesCourses();
                tag = "mes_courses";
//            else if(pos==3)
//                fragment=new FragmentMessage();
            }else if(pos==4){
                fragment=new FragmentProfile();
                tag = "profile";
//        else if(pos==6)
//            openBrowser();
            }*/else if(pos==0){
            M.logOut(context);
            prefManager.setFirstTimeLaunch7(true);
            Intent mIntent = new Intent(context, LoginActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.finish();
            context.startActivity(mIntent);
        }

        if(fragment!=null) {
            fragmentManager = ((MainActivity)context).getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, tag);
            if(pos!=1)
                fragmentTransaction.addToBackStack(item);
            else{
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public static void openBrowser(final Context context, String url){
        if(!url.startsWith(HTTP)&& !url.startsWith(HTTPS)){
            url = HTTP+url;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(Intent.createChooser(intent, "Choisir un naviguateur"));
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }else {
            if(notification == true){
                selectItem(0);
                notification = false;
            }else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                if(fragmentTag==null)
                    finish();
                else {
//                    Toast.makeText(context, ""+fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
                    if(fragmentManager.getBackStackEntryCount()>=2){
                        String fragmentTag1 = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2).getName();
//                        Toast.makeText(context, ""+fragmentTag1, Toast.LENGTH_SHORT).show();
                        if(fragmentTag1==null)
                            selectItem(0);
                        else
                            super.onBackPressed();
                    }else
                        super.onBackPressed();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==401 && resultCode==RESULT_OK) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private static void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void updateFCM(final String user_id) {
        final String[] fcmid = {""};
        final String[] deviceid = { "" };
        if(AppConst.fcm_id==null){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
//                                Toast.makeText(Demarrage.this, "getInstanceId failed", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            fcmid[0] = token;
                            // Log and toast
                            deviceid[0] = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//                            Toast.makeText(Demarrage.this, ""+ fcmid[0] +" "+ deviceid[0], Toast.LENGTH_SHORT).show();
                            if(fcmid[0] !=null && fcmid[0].trim().length()>0 && deviceid[0] !=null && deviceid[0].trim().length()>0) {
                                new setUserFCM().execute(user_id, fcmid[0], deviceid[0]);
                            }
                        }
                    });
        }else{
            fcmid[0] = AppConst.fcm_id;
        }
    }

    /** Mettre à jour le token de l'utilisateur**/
    private class setUserFCM extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"update_fcm.php";
            final String user_id = params[0];
            final String fcmid = params[1];
            final String deviceid = params[2];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id",user_id);
                    params.put("fcm_id",fcmid);
                    params.put("device_id",deviceid);
                    params.put("user_cat",M.getUserCategorie(context));
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //to add spacing between cards
            if (this != null) {

            }

        }

        @Override
        protected void onPreExecute() {

        }
    }

    public static Fragment getCurrentFragment(){
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
        return currentFragment;
    }
}
