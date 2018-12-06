package br.ufc.ubicomp.promocity.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.ufc.ubicomp.promocity.R;
import br.ufc.ubicomp.promocity.model.Usuario;
import br.ufc.ubicomp.promocity.view.TodosUsuariosActivity;

public class TodosUsuariosAdapter extends ArrayAdapter<Usuario> {

    private Context context;
    private ArrayList<Usuario> lista;
    private AlertDialog alerta;

    private Usuario itemPosicaoAux;

    public static String conteudoJson;

    public TodosUsuariosAdapter(Context context, ArrayList<Usuario> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = lista;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Usuario itemPosicao = this.lista.get(position);
        itemPosicaoAux = new Usuario();

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_lista_todos_usuarios,null);
        final View layout = convertView;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        TextView textViewNome = (TextView) convertView.findViewById(R.id.tvNome);
        textViewNome.setText(itemPosicao.getNome());

        TextView textViewDescricao = (TextView) convertView.findViewById(R.id.tvDescricao);
        textViewDescricao.setText("");

        TextView textViewCodigo = (TextView) convertView.findViewById(R.id.tvCodigo);
        textViewCodigo.setText(itemPosicao.getEmail());

        Button buttonAdicionar = (Button)convertView.findViewById(R.id.buttonAdicionar);
        buttonAdicionar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                itemPosicaoAux.setId(itemPosicao.getId());
                new adicionarUsuarioComoAmigo().execute();
            }
        });

        return convertView;

    }

    void kill_activity()
    {
        ((Activity) this.context).finish();
    }


    public void adicionarAmigoJSON(){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = "http://18.207.202.131:8082/promocity/users/"+TodosUsuariosActivity.usuarioLogin.getId()+"/add/friend/"+itemPosicaoAux.getId();

        // Request a string response from the provided URL.
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
               // Display the first 500 characters of the response string.
               String conteudo = "";

                    try {
                        int id = jsonObject.getInt("id");
                        conteudo = jsonObject.getString("conteudo");

                        conteudoJson = ""+conteudo;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Não conseguimos nos conectar ao servidor", Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(objectRequest);
    }

    private class adicionarUsuarioComoAmigo extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPostExecute(String conteudo) {
            if(conteudoJson==""){
                Toast.makeText(context, "Não foi possível adicionar o usuário", Toast.LENGTH_SHORT).show();
                kill_activity();
            } else {
                Toast.makeText(context, ""+conteudoJson, Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Adicionando Usuário aos Amigos...");
            dialog.show();
        }

        @Override
        protected String doInBackground(final String... args) {
            boolean resultado;

            String conteudo="";
            adicionarAmigoJSON();

            //tempo de execuçao do jsonParser
            try {
                // Simulate network access.
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return conteudo;
        }

    }



}
