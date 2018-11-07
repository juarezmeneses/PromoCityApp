package br.ufc.ubicomp.promocity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetalheCupomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_cupom);



        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){

                TextView textViewNome = (TextView) findViewById(R.id.tvNome);
                textViewNome.setText(bundle.getString("nome"));

                TextView textViewDescricao = (TextView) findViewById(R.id.tvDescricao);
                textViewDescricao.setText(bundle.getString("descricao"));

                TextView textViewCodigo = (TextView) findViewById(R.id.tvCodigo);
                textViewCodigo.setText(bundle.getString("codigo"));

            }
        }


    }
}
