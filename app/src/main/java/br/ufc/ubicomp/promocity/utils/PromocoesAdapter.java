package br.ufc.ubicomp.promocity.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufc.ubicomp.promocity.R;
import br.ufc.ubicomp.promocity.model.Promocao;
import br.ufc.ubicomp.promocity.view.DetalhePromocaoActivity;

public class PromocoesAdapter extends ArrayAdapter<Promocao> {

    private Context context;
    private ArrayList<Promocao> lista;
    private AlertDialog alerta;

    public PromocoesAdapter(Context context, ArrayList<Promocao> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = lista;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Promocao itemPosicao = this.lista.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_lista_promocoes,null);
        final View layout = convertView;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        TextView textViewNome = (TextView) convertView.findViewById(R.id.tvNome);
        textViewNome.setText(itemPosicao.getNome());

        TextView textViewDescricao = (TextView) convertView.findViewById(R.id.tvDescricao);
        textViewDescricao.setText("");

        Button buttonVisualizar = (Button)convertView.findViewById(R.id.buttonVisualizar);
        buttonVisualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean flag = true;

                Intent intent = new Intent(context, DetalhePromocaoActivity.class);
                intent.putExtra("flag", flag);
                intent.putExtra("id",itemPosicao.getId());
                intent.putExtra("nome",itemPosicao.getNome());
                intent.putExtra("descricao",itemPosicao.getDescricao());
                intent.putExtra("idEstabelecimento",itemPosicao.getIdEstabelecimento());

                context.startActivity(intent);
            }
        });
        return convertView;

    }

    void kill_activity()
    {
        ((Activity) this.context).finish();
    }

}
