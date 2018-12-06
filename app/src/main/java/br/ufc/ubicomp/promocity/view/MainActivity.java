package br.ufc.ubicomp.promocity.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import br.ufc.ubicomp.promocity.R;
import br.ufc.ubicomp.promocity.model.Cupom;
import br.ufc.ubicomp.promocity.model.Estabelecimento;
import br.ufc.ubicomp.promocity.model.Promocao;
import br.ufc.ubicomp.promocity.model.Usuario;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    public final static SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
    public final static String noite = "18:00";
    public final static String madrugada = "04:00";

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    ArrayList<Estabelecimento> listaDeEstabelecimentos;

    public static ArrayList<Estabelecimento> listaDeEstabelecimentosJson;

    public static ArrayList<Promocao> listaDePromocoesColetadasJson;

    public static boolean carregouJson = false;

    public static Usuario usuarioLogin;

    private AlertDialog alerta;

    private Boolean moveuMapa = false;

    FloatingActionButton fab, fab1, fab2, fab3;
    boolean isFABOpen=false;

    private long lastBackPressTime = 0;
    private Toast toast;

    public SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        carregouJson = false;

        //listaDeEstabelecimentosJson = new ArrayList<>();

        listaDePromocoesColetadasJson = new ArrayList<>();

        usuarioLogin = new Usuario();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                usuarioLogin.setId(bundle.getInt("id"));
                usuarioLogin.setNome(bundle.getString("nome"));
                usuarioLogin.setEmail(bundle.getString("email"));
                usuarioLogin.setLatitude(0.0);
                usuarioLogin.setLongetude(0.0);

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Maps
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkGps(MainActivity.this);

        //show keyhash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        //parserJSON();

        //verifica periodicamente se o usuário colotou cupons
        coletarCupons();

        //setup
        setupEstatico();

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                carregouJson=false;////////////////////////////////////////////////////
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        //new MainActivity.buscarPromocoes().execute();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PromocoesActivity.class);
                intent.putExtra("id", usuarioLogin.getId());
                intent.putExtra("nome", usuarioLogin.getNome());
                intent.putExtra("email", usuarioLogin.getEmail());
                startActivity(intent);

                closeFABMenu();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MeusCuponsActivity.class);
                intent.putExtra("id", usuarioLogin.getId());
                intent.putExtra("nome", usuarioLogin.getNome());
                intent.putExtra("email", usuarioLogin.getEmail());
                startActivity(intent);

                closeFABMenu();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AmigosActivity.class);
                intent.putExtra("id", usuarioLogin.getId());
                intent.putExtra("nome", usuarioLogin.getNome());
                intent.putExtra("email", usuarioLogin.getEmail());
                startActivity(intent);

                closeFABMenu();
            }
        });

    }

    private void showFABMenu(){
        isFABOpen=true;
        fab.animate().rotation(45);
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab.animate().rotation(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        carregouJson=false;
        listaDeEstabelecimentosJson = new ArrayList<>();

        new MainActivity.buscarPromocoes().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_locais) {
            // Handle the camera action
        } else if (id == R.id.nav_promocoes) {
            Intent intent = new Intent(this, PromocoesActivity.class);
            intent.putExtra("id", usuarioLogin.getId());
            intent.putExtra("nome", usuarioLogin.getNome());
            intent.putExtra("email", usuarioLogin.getEmail());
            startActivity(intent);

        } else if (id == R.id.nav_cupons) {
            Intent intent = new Intent(this, MeusCuponsActivity.class);
            intent.putExtra("id", usuarioLogin.getId());
            intent.putExtra("nome", usuarioLogin.getNome());
            intent.putExtra("email", usuarioLogin.getEmail());
            startActivity(intent);

        } else if (id == R.id.nav_configuracoes) {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_sobre) {
            Intent intent = new Intent(this, SobreActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_amigos) {
            Intent intent = new Intent(this, AmigosActivity.class);
            intent.putExtra("id", usuarioLogin.getId());
            intent.putExtra("nome", usuarioLogin.getNome());
            intent.putExtra("email", usuarioLogin.getEmail());
            startActivity(intent);

        } else if (id == R.id.nav_sair) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    public void makeUseOfNewLocation(Location location) {

        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        usuarioLogin.setLatitude((double) myLocation.latitude);
        usuarioLogin.setLongetude((double) myLocation.longitude);
        mMap.setMyLocationEnabled(true);

        if(!moveuMapa){
            moveuMapa = true;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
        }

        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(20.0f);

        Log.i("Lista Lojas Json: ", "" + listaDeEstabelecimentosJson.size()+ " " + carregouJson +" "+mMap);

        if (carregouJson != true) {
            carregouJson = true;

            for (Estabelecimento atual : listaDeEstabelecimentosJson) {
                LatLng posicao = new LatLng(atual.getLatitude(), atual.getLongetude());
                mMap.addMarker(new MarkerOptions().position(posicao).title(atual.getNome()).snippet("Promoções: " + atual.getListaDePromocoes().size()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            }

        }

        Log.i("Coletou Cupons Lista: ", "" + listaDePromocoesColetadasJson.size());
        if (listaDePromocoesColetadasJson.size() > 0) {
            for (Promocao promocaoColetada : listaDePromocoesColetadasJson)
                for (Cupom cupomColetado : promocaoColetada.getListaDeCupons()) {
                    Log.i("Entrou", "" + listaDePromocoesColetadasJson.size());
                    //Cria o gerador do AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    //define o titulo
                    builder.setTitle("Você coletou cupons");
                    //define a mensagem
                    builder.setMessage("" + promocaoColetada.getNome() + ": " + cupomColetado.getNome());
                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();
                    //alerta.dismiss();

                }
            listaDePromocoesColetadasJson.clear();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        /*int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(MainActivity.this.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if(locationMode == LOCATION_MODE_HIGH_ACCURACY) {
            //request location updates
        } else { //redirect user to settings page
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }*/


        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(sydney).title("Ative o GPS e Defina o Modo de Localização para:").snippet("ALTA PRECISÃO!!!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(14.0f);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        //adicionando os estabelecimentos ao mapa como marcadores
        for (Estabelecimento atual:listaDeEstabelecimentos) {
            LatLng posicao = new LatLng(atual.getLatitude(), atual.getLongetude());
            mMap.addMarker(new MarkerOptions().position(posicao).title(atual.getNome()).snippet("Promoções: "+atual.getListaDePromocoes().size()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        }

        //adicionando os estabelecimentos do json ao mapa como marcadores
        if(listaDeEstabelecimentosJson.size()>0){
            for (Estabelecimento atual:MainActivity.listaDeEstabelecimentosJson) {
                LatLng posicao = new LatLng(atual.getLatitude(), atual.getLongetude());
                mMap.addMarker(new MarkerOptions().position(posicao).title(atual.getNome()).snippet("Promoções: "+atual.getListaDePromocoes().size()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            }
        }

        DateFormat df = new SimpleDateFormat("HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        if(checkIfNight(date)) {
            try {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.mapanoite));

                if (!success) {
                    Log.e("Mapa noite", "Falhou ao aplicar estilo.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("Mapa noite", "Estilo não encontrado. Error: ", e);
            }

        }

    }


    public void checkGps(MainActivity view){
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!gpsEnabled || provider.length()==0) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            Dialog dialog = new AlertDialog.Builder(this)
                    .setMessage("Seu GPS está desligado!!! Ative-o e defina o método de localização para ALTA PRECISÃO ")
                    .setPositiveButton("Ligar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .create();

            dialog.show();
        }
    }

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    public static boolean checkIfNight(String time) {
        try {
            Date present = parser.parse(time);
            Date closed = parser.parse(noite);
            Date closed2 = parser.parse(madrugada);
            if (present.after(closed) || present.before(closed2)) {
                return true;
            }
        } catch (ParseException e) {
            // Invalid date was entered
        }
        return false;
    }

    public void parserJSON(){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.207.202.131:8082/promocity/stores/";

        // Request a string response from the provided URL.
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                // Display the first 500 characters of the response string.
                String name="", address="", city="", state="";
                double latitude = 100, longitude = 100;
                String latS="", lonS="";

                String description = "";
                long fromData=100, toData=100;

                for (int i=0; i<jsonArray.length() ;i++){
                    try {
                        JSONObject estabelecimento = jsonArray.getJSONObject(i);
                        int id = estabelecimento.getInt("id");
                        name = estabelecimento.getString("name");
                        address = estabelecimento.getString("address");
                        city = estabelecimento.getString("city");
                        state = estabelecimento.getString("state");


                        latitude = estabelecimento.getDouble("latitude");
                        longitude = estabelecimento.getDouble("longitude");

                        Estabelecimento novoEstabelecimento = new Estabelecimento(id, latitude, longitude, name, address);

                        JSONArray promotionList = estabelecimento.getJSONArray("promotionList");
                        for (int j=0; j<promotionList.length() ;j++){
                            JSONObject promocao = promotionList.getJSONObject(j);
                            int promoId = promocao.getInt("id");
                            description = promocao.getString("description");
                            fromData = promocao.getLong("fromDate");
                            toData = promocao.getLong("toDate");

                            Promocao novaPromocao = new Promocao(promoId, description, description, id);
                            novoEstabelecimento.addPromocao(novaPromocao);

                        }
                        MainActivity.listaDeEstabelecimentosJson.add(novoEstabelecimento);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Não conseguimos nos conectar ao servidor", Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(arrayRequest);
    }

    public void coletarCupons(){
        int delay = 30000;   // delay de 30 seg.
        int interval = 30000;  // intervalo de 30 seg.
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        sendMyLocationJson();
                        //Toast.makeText(getApplicationContext(), "Checando Cupons nas Proximidades...", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }, delay, interval);
    }

    public void setupEstatico(){
        Estabelecimento americanas = new Estabelecimento(1, -3.745181, -38.512447, "Lojas Americanas", "Preço baixo e as melhores ofertas de smartphones, notebooks, TV LED, geladeiras, móveis, tablets e mais na Americanas.com. Aproveite!");
        Estabelecimento extra = new Estabelecimento(2, -3.745856, -38.514948, "Extra Supermecados", "Leve uma vida mais família no Extra.com.br. Móveis, eletrônicos, celulares, notebooks e tudo o que você precisa para estar e se conectar com quem você ama.");
        Estabelecimento casasBahia = new Estabelecimento(3, -3.734353, -38.566027, "Casas Bahia", "Caro Freguês, encontre tudo que você precisa e faça sua compra com segurança nas Casas Bahia." );
        Estabelecimento cantinaDaQuimica = new Estabelecimento(4, -3.746442, -38.576870, "Cantina da Química", "");

        Promocao tvsAmericanas = new Promocao(1, "TVs Americanas", "Todas as TVs com até 50% de desconto",1);
        Promocao eletrodomesticosAmericanas = new Promocao(2, "Eletrodomesticos Americanas", "Todos os Eletrodomesticos com até 40% de desconto",1);

        Promocao tvsExtra = new Promocao(3, "TVs Extra", "Todas as TVs com até 30% de desconto",2);
        Promocao eletrodomesticosExtra = new Promocao(4, "Eletrodomesticos Extra", "Todos os Eletrodomesticos com até 20% de desconto",2);

        Promocao moveisCasasBahia = new Promocao(5, "Móveis Casas Bahia", "Todos os Móveis com até 25% de desconto",3);

        Promocao alimentacaoCantina = new Promocao(6, "Comidas e Bebidas Cantina da Química", "Comidas e Bebidas com até 50% de desconto",4);

        Cupom tv50Americanas = new Cupom(1, "TV 50 LG", "TV 50 LG COM 15% DE DESCONTO", "@*!2332");
        Cupom geladeiraAmericanas = new Cupom(2, "Geladeira Brastemp", "Geladeira Brastemp COM 10% DE DESCONTO", "@*!1900");

        Cupom tv47Extra = new Cupom(3, "TV 47 Philco", "TV 47 Philco COM 20% DE DESCONTO", "@*!3323");
        Cupom fogaoExtra = new Cupom(4, "Fogão Esmaltec", "Fogão Esmaltec COM 25% DE DESCONTO", "@*!5535");

        Cupom camaBoxCasasBahia = new Cupom(5, "Cama Box","Cama Box Casal Queen Size COM 10% DE DESCONTO", "@*!9299");

        Cupom salgadosCantina = new Cupom(6, "Salgados","Salgados COM 25% DE DESCONTO", "@*!7279");
        Cupom sucosCantina = new Cupom(7, "Sucos","Sucos COM 50% DE DESCONTO", "@*!8878");
        Cupom fatiaBoloCantina = new Cupom(8, "Fatias de Bolo","Fatias de Bolo COM 20% DE DESCONTO", "@*!8338");


        tvsAmericanas.addCupom(tv50Americanas);
        eletrodomesticosAmericanas.addCupom(geladeiraAmericanas);

        americanas.addPromocao(tvsAmericanas);
        americanas.addPromocao(eletrodomesticosAmericanas);

        tvsExtra.addCupom(tv47Extra);
        eletrodomesticosExtra.addCupom(fogaoExtra);

        extra.addPromocao(tvsExtra);
        extra.addPromocao(eletrodomesticosExtra);

        moveisCasasBahia.addCupom(camaBoxCasasBahia);

        casasBahia.addPromocao(moveisCasasBahia);

        alimentacaoCantina.addCupom(salgadosCantina);
        alimentacaoCantina.addCupom(sucosCantina);
        alimentacaoCantina.addCupom(fatiaBoloCantina);

        cantinaDaQuimica.addPromocao(alimentacaoCantina);

        listaDeEstabelecimentos = new ArrayList<>();

        listaDeEstabelecimentos.add(americanas);
        listaDeEstabelecimentos.add(extra);
        listaDeEstabelecimentos.add(casasBahia);
        listaDeEstabelecimentos.add(cantinaDaQuimica);

    }


    private class buscarPromocoes extends AsyncTask<String, Void, ArrayList<Promocao>> {

        private ProgressDialog dialog;
        @Override
        protected void onPostExecute(ArrayList<Promocao> lista) {

            if(lista.isEmpty()){
                Toast.makeText(MainActivity.this, "Não Existem Lojas Cadastradas", Toast.LENGTH_SHORT).show();
                //kill_activity();
            }
            dialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("Carregando Estabelecimentos...");
            dialog.show();
        }

        @Override
        protected ArrayList<Promocao> doInBackground(final String... args) {
            boolean resultado;
            parserJSON();

            ArrayList<Promocao> lista = new ArrayList<>();

            //tempo de execuçao do jsonParser
            try {
                // Simulate network access.
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Estabelecimento estabelecimentoAtual:MainActivity.listaDeEstabelecimentosJson
                    ) {
                for (Promocao promocaoAtual:estabelecimentoAtual.getListaDePromocoes()
                        ) {
                    lista.add(promocaoAtual);
                }
            }
            return lista;
        }
    }


    public void sendMyLocationJson(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.207.202.131:8082/promocity/users/"+usuarioLogin.getId()+"/monitoring/location/"+usuarioLogin.getLatitude()+"/"+usuarioLogin.getLongetude();

        // Request a string response from the provided URL.
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                // Display the first 500 characters of the response string.
                String name="", address="", city="", state="";
                double latitude = 100, longitude = 100;
                String latS="", lonS="";

                String description = "";
                long fromData=100, toData=100;
                String cupomQrCode = "";
                double discount = 100;

                //JSONObject resultado = jsonArray.getJSONObject();
                if (jsonArray != null) {
                    try {
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject promocao = jsonArray.getJSONObject(j);

                            int promoId = promocao.getInt("idPromotion");
                            description = promocao.getString("descriptionPromotion");
                            fromData = promocao.getLong("fromDate");
                            toData = promocao.getLong("toDate");
                            int storeId = promocao.getInt("idStore");

                            Promocao novaPromocao = new Promocao(promoId, description, description, storeId);

                            int cupomId = promocao.getInt("idCoupon");
                            String cupomDescription = promocao.getString("descriptionCoupon");

                            cupomQrCode = promocao.getString("qrCode");

                            Cupom novoCupom = new Cupom(cupomId, cupomDescription, cupomDescription, cupomQrCode);
                            novaPromocao.addCupom(novoCupom);

                            MainActivity.listaDePromocoesColetadasJson.add(novaPromocao);
                            }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Não conseguimos nos conectar ao servidor1", Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(arrayRequest);
    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Pressione o Botão Voltar Novamente se Deseja Deslogar do Aplicativo.", Toast.LENGTH_SHORT);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            super.onBackPressed();
            finish();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        carregouJson = false;
        outState.putBoolean("carregouJson", carregouJson); // Saving the Variable theWord
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        carregouJson = savedInstanceState.getBoolean("carregouJson"); // Restoring theWord
    }

}
