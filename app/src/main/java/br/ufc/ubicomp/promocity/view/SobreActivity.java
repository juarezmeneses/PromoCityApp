package br.ufc.ubicomp.promocity.view;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import br.ufc.ubicomp.promocity.R;

public class SobreActivity extends AppCompatActivity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        mWebView = (WebView) findViewById(R.id.webview);

        String text = "<html><body>"
                + "<p align=\"justify\">"
                + getString(R.string.descricao_sistema)
                + "</p> "
                + "</body></html>";

        mWebView.loadData(text, "text/html; charset=utf-8", "utf-8");

        TextView textViewNome = (TextView) findViewById(R.id.tvNome);
        textViewNome.setText("Aplicativo: " + getString(R.string.app_name));

        TextView textViewVersao = (TextView) findViewById(R.id.tvVersao);
        textViewVersao.setText("Versão: "+version);

    }
}
