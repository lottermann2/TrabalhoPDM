package com.example.trabalho_pdm.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.trabalho_pdm.R;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class CadastroUsuarioActivity extends AppCompatActivity {
    public static final String TITULO_APPBAR = "Novo Usuario";
    private EditText txtEmail;
    private EditText txtSenha;
    private EditText txtNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        setTitle(TITULO_APPBAR);
        txtEmail = findViewById(R.id.activity_cadastro_email);
        txtSenha = findViewById(R.id.activity_cadastro_senha);
        txtNome = findViewById(R.id.activity_cadastro_nome);

        gerenciarCadastro();
    }

    private void gerenciarCadastro() {
        Button botaoCadastrar = findViewById(R.id.activity_cadastro_botao_cadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtNome.toString() == "" || txtEmail.toString() == "" || txtSenha.toString() == "") {
                    Toast.makeText(CadastroUsuarioActivity.this, "Todos os campos devem ser preenchidos!",
                            Toast.LENGTH_LONG).show();
                } else {
                    abrirCadastroUsuario();
                }
            }
        });
    }

    private void abrirCadastroUsuario() {
        NovoUsuario novoUsuario = new NovoUsuario();
        String email = txtEmail.getText().toString();
        String senha = txtSenha.getText().toString();
        String nome = txtNome.getText().toString();

        novoUsuario.execute("https://viacep.com.br/ws/96815040/json", email, senha, nome);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public class NovoUsuario extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("POST");

                ContentValues dto = new ContentValues();
                dto.put("email", strings[1]);
                dto.put("senha", strings[2]);
                dto.put("nome", strings[3]);

                OutputStream out = conexao.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(getFormData(dto));
                writer.flush();

                int status = conexao.getResponseCode();

                if (status == 200) {
                    InputStream stream = new BufferedInputStream(conexao.getInputStream());
                    BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();
                    String str = "";
                    while ((str = buff.readLine()) != null) {
                        builder.append(str);
                    }
                    conexao.disconnect();

                    return builder.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CadastroUsuarioActivity.this);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String dto) {
            super.onPostExecute(dto);
            dialog.dismiss();
            finish();
        }
    }

    private String getFormData(ContentValues dto) {
        try {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Object> entry : dto.valueSet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }

                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
