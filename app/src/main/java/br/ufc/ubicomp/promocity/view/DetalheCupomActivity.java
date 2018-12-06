package br.ufc.ubicomp.promocity.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.ufc.ubicomp.promocity.R;
import br.ufc.ubicomp.promocity.model.Cupom;
import br.ufc.ubicomp.promocity.model.Usuario;

public class DetalheCupomActivity extends AppCompatActivity {

    private boolean flagAtivar = false;

    private Usuario usuarioLogin;

    private Cupom cupom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_cupom);

        usuarioLogin = new Usuario();
        cupom = new Cupom();

        Button buttonAtivarCupom = (Button) findViewById(R.id.buttonAtivarCupom);
        buttonAtivarCupom.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){

                cupom.setId(bundle.getInt("idCupom"));

                TextView textViewNome = (TextView) findViewById(R.id.tvNome);
                textViewNome.setText(bundle.getString("nomeCupom"));

                TextView textViewDescricao = (TextView) findViewById(R.id.tvDescricao);
                textViewDescricao.setText("");

                TextView textViewCodigo = (TextView) findViewById(R.id.tvCodigo);
                textViewCodigo.setText(bundle.getString("codigo"));

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.qrcode);

                flagAtivar = bundle.getBoolean("flagAtivar");

                if(flagAtivar){
                    usuarioLogin.setNome(bundle.getString("nome"));
                    usuarioLogin.setEmail(bundle.getString("email"));
                    usuarioLogin.setId(bundle.getInt("id"));
                    buttonAtivarCupom.setVisibility(View.VISIBLE);

                    cupom.setId(bundle.getInt("idCupom"));
                    cupom.setIdStore(bundle.getInt("idStore"));

                }


            }
        }

        buttonAtivarCupom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DetalheCupomActivity.this, AtivarCupomEspecialActivity.class);
                intent.putExtra("id", usuarioLogin.getId());
                intent.putExtra("nome", usuarioLogin.getNome());
                intent.putExtra("email", usuarioLogin.getEmail());
                intent.putExtra("flagAtivar", flagAtivar);
                intent.putExtra("idCupom", cupom.getId());
                intent.putExtra("idStore", cupom.getIdStore());

                startActivity(intent);

            }
        });

    }
}
