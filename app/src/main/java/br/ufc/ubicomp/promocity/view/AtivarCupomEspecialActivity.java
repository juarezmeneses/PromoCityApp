package br.ufc.ubicomp.promocity.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.ufc.ubicomp.promocity.utils.AtivarCupomEspecialAdapter;
import br.ufc.ubicomp.promocity.R;
import br.ufc.ubicomp.promocity.model.Cupom;
import br.ufc.ubicomp.promocity.model.Usuario;

public class AtivarCupomEspecialActivity extends AppCompatActivity {

    public static Usuario usuarioLogin;

    public static ArrayList<Usuario> listaMeusAmigosJson;
    private ListView listViewMeusAmigos;

    public static boolean flagAtivar = false;

    public static Cupom cupom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ativar_cupom_especial);

        listaMeusAmigosJson = new ArrayList<>();
        this.listViewMeusAmigos = (ListView) findViewById(R.id.listViewMeusAmigos);

        usuarioLogin = new Usuario();
        cupom = new Cupom();

        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){

                usuarioLogin.setId(bundle.getInt("id"));
                usuarioLogin.setNome(bundle.getString("nome"));
                usuarioLogin.setEmail(bundle.getString("email"));
                flagAtivar=bundle.getBoolean("flagAtivar");
                cupom.setId(bundle.getInt("idCupom"));
                cupom.setIdStore(bundle.getInt("idStore"));

            }
        }

        parserJSON();
    }

    private class buscarMeusAmigos extends AsyncTask<String, Void, ArrayList<Usuario>> {

        private ProgressDialog dialog;
        @Override
        protected void onPostExecute(ArrayList<Usuario> lista) {
            listViewMeusAmigos.setAdapter(new AtivarCupomEspecialAdapter(AtivarCupomEspecialActivity.this, lista));

            if(lista.isEmpty()){
                Toast.makeText(AtivarCupomEspecialActivity.this, "Você ainda não tem amigos", Toast.LENGTH_SHORT).show();
                kill_activity();
            }

            dialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AtivarCupomEspecialActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("Buscando Amigos Para Ativação do Cupom...");
            dialog.show();

        }

        @Override
        protected ArrayList<Usuario> doInBackground(final String... args) {
            boolean resultado;

            ArrayList<Usuario> lista = new ArrayList<>();

            //tempo de execuçao do jsonParser
            try {
                // Simulate network access.
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Usuario usuarioAtual:listaMeusAmigosJson
                    ) {
                lista.add(usuarioAtual);
            }

            return lista;
        }

    }

    void kill_activity()
    {
        ((Activity) this).finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new AtivarCupomEspecialActivity.buscarMeusAmigos().execute();
    }

    public void parserJSON(){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.207.202.131:8082/promocity/users/"+usuarioLogin.getId()+"/list/friends";

        // Request a string response from the provided URL.
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                // Display the first 500 characters of the response string.
                String name = "", address = "", city = "", state = "";
                double latitude = 100, longitude = 100, radius = 100;
                String latS = "", lonS = "";
                int promoId;

                String description = "";
                long fromData = 100, toData = 100;

                if (jsonArray != null) {
                    try {

                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject amigo = jsonArray.getJSONObject(k);
                            int amigoId = amigo.getInt("id");
                            String amigoNome = amigo.getString("username");
                            String email = amigo.getString("email");

                            Usuario novoAmigo = new Usuario();
                            novoAmigo.setId(amigoId);
                            novoAmigo.setNome(amigoNome);
                            novoAmigo.setEmail(email);
                            AtivarCupomEspecialActivity.listaMeusAmigosJson.add(novoAmigo);

                        }


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
}
