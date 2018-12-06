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
import br.ufc.ubicomp.promocity.model.Cupom;
import br.ufc.ubicomp.promocity.view.DetalheCupomActivity;
import br.ufc.ubicomp.promocity.view.MainActivity;

public class CuponsDaPromocaoAdapter extends ArrayAdapter<Cupom> {

    private Context context;
    private ArrayList<Cupom> lista;
    private AlertDialog alerta;

    public CuponsDaPromocaoAdapter(Context context, ArrayList<Cupom> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = lista;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Cupom itemPosicao = this.lista.get(position);

        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_lista_meus_cupons,null);
        final View layout = convertView;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

        TextView textViewNome = (TextView) convertView.findViewById(R.id.tvNome);
        textViewNome.setText(itemPosicao.getNome());

        TextView textViewDescricao = (TextView) convertView.findViewById(R.id.tvDescricao);
        textViewDescricao.setText("");

        TextView textViewCodigo = (TextView) convertView.findViewById(R.id.tvCodigo);
        textViewCodigo.setText(itemPosicao.getCodigo());

        Button buttonVisualizar = (Button)convertView.findViewById(R.id.buttonVisualizar);
        buttonVisualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean flag = true;

                Intent intent = new Intent(context, DetalheCupomActivity.class);
                intent.putExtra("flag", flag);
                intent.putExtra("idCupom",itemPosicao.getId());
                intent.putExtra("nomeCupom",itemPosicao.getNome());
                intent.putExtra("descricao",itemPosicao.getDescricao());
                intent.putExtra("codigo",itemPosicao.getCodigo());
                intent.putExtra("flagAtivar",false);
                intent.putExtra("id", MainActivity.usuarioLogin.getId());
                intent.putExtra("email", MainActivity.usuarioLogin.getEmail());
                intent.putExtra("nome", MainActivity.usuarioLogin.getNome());

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
