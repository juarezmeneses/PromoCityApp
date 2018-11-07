package br.ufc.ubicomp.promocity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PromocoesActivity extends AppCompatActivity {

    ListView lv;
    private ListView listViewPromocoes;

    ArrayList<Estabelecimento> listaDeEstabelecimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocoes);

        this.listViewPromocoes = (ListView) findViewById(R.id.listViewPromocoes);

        //setup

        Estabelecimento americanas = new Estabelecimento(1, -3.745181, -38.512447, "Lojas Americanas", "Preço baixo e as melhores ofertas de smartphones, notebooks, TV LED, geladeiras, móveis, tablets e mais na Americanas.com. Aproveite!");
        Estabelecimento extra = new Estabelecimento(2, -3.745856, -38.514948, "Extra Supermecados", "Leve uma vida mais família no Extra.com.br. Móveis, eletrônicos, celulares, notebooks e tudo o que você precisa para estar e se conectar com quem você ama.");
        Estabelecimento casasBahia = new Estabelecimento(3, -3.734353, -38.566027, "Casas Bahia", "Caro Freguês, encontre tudo que você precisa e faça sua compra com segurança nas Casas Bahia." );
        Estabelecimento cantinaDaQuimica = new Estabelecimento(4, -3.746442, -38.576870, "Cantina da Química", "");

        Promocao tvsAmericanas = new Promocao(1, "TVs Americanas", "Todas as TVs com até 50% de desconto");
        Promocao eletrodomesticosAmericanas = new Promocao(2, "Eletrodomesticos Americanas", "Todos os Eletrodomesticos com até 40% de desconto");

        Promocao tvsExtra = new Promocao(3, "TVs Extra", "Todas as TVs com até 30% de desconto");
        Promocao eletrodomesticosExtra = new Promocao(4, "Eletrodomesticos Extra", "Todos os Eletrodomesticos com até 20% de desconto");

        Promocao moveisCasasBahia = new Promocao(5, "Móveis Casas Bahia", "Todos os Móveis com até 25% de desconto");

        Promocao alimentacaoCantina = new Promocao(6, "Comidas e Bebidas Cantina da Química", "Comidas e Bebidas com até 50% de desconto");

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

        /////////////
    }

    private class buscarPromocoes extends AsyncTask<String, Void, ArrayList<Promocao>> {

        private ProgressDialog dialog;
        @Override
        protected void onPostExecute(ArrayList<Promocao> lista) {
            listViewPromocoes.setAdapter(new PromocoesAdapter(PromocoesActivity.this, lista));

            if(lista.isEmpty()){
                Toast.makeText(PromocoesActivity.this, "Não Existem Promocoes Cadastrados", Toast.LENGTH_SHORT).show();
                kill_activity();
            }

            dialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(PromocoesActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage("Buscando Promocoes...");
            dialog.show();

        }

        @Override
        protected ArrayList<Promocao> doInBackground(final String... args) {
            boolean resultado;

            //PromocaoDAO dao = new PromocaoDAO();
            ArrayList<Promocao> lista = new ArrayList<>();

            for (Estabelecimento estabelecimentoAtual:listaDeEstabelecimentos
                 ) {
                for (Promocao promocaoAtual:estabelecimentoAtual.getListaDePromocoes()
                     ) {
                    lista.add(promocaoAtual);
                }
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

        new buscarPromocoes().execute();
    }

}
