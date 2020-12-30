package com.example.trabalho_pdm.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalho_pdm.R;
import com.example.trabalho_pdm.model.Usuario;

import org.json.JSONObject;

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

public class LoginActivity extends AppCompatActivity {
    private static final String TITULO_APPBAR = "Login";
    private EditText txtEmail;
    private EditText txtSenha;
    private Usuario usuario = new Usuario(0, "", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(TITULO_APPBAR);
        txtEmail = findViewById(R.id.activity_login_email);
        txtSenha = findViewById(R.id.activity_login_senha);

        gerenciarBotoes();
    }

    private void gerenciarBotoes() {
        Button botaoLogar = findViewById(R.id.activity_login_botao_entrar);
        TextView cadastroUsuario = findViewById(R.id.activity_login_cadastro_usuario);

        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gerenciamentoBotaoLogar();
            }
        });

        cadastroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCadastroDeUsuario();
            }
        });
    }

    private void gerenciamentoBotaoLogar() {
        GerenciarLogin novoLogin = new GerenciarLogin();
        String email = txtEmail.getText().toString();
        String senha = txtSenha.getText().toString();

        novoLogin.execute("https://viacep.com.br/ws/96815040/json", email, senha);
    }

    private void abrirCadastroDeUsuario() {
        startActivity(new Intent(this, CadastroUsuarioActivity.class));
    }

    private void abrirTelaPrincipal() {
        startActivity(new Intent(this, CadastroUsuarioActivity.class));
    }

//    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
//
//        ProgressDialog dialog;
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                URL url = new URL("https://viacep.com.br/ws/96815040/json");
//                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
//                conexao.setRequestMethod("GET");
//                int status = conexao.getResponseCode();
//
//                if (status == 200){
//                    InputStream stream = new BufferedInputStream(conexao.getInputStream());
//                    BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
//                    StringBuilder builder = new StringBuilder();
//                    String str = "";
//                    while ((str = buff.readLine()) != null) {
//                        Log.d("LOG", "------teste str -----");
//                        Log.d("LOG", str);
//                        Log.d("LOG", "------teste readLine -----");
//                        Log.d("LOG", buff.readLine());
//
//                        builder.append(str);
//                    }
//                    conexao.disconnect();
//                    return builder.toString();
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog = new ProgressDialog(LoginActivity.this);
//            dialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(String dto) {
//            super.onPostExecute(dto);
//            dialog.dismiss();
//
//            if (dto != null){
//                try{
//                    JSONObject obj = new JSONObject(dto);
//
//                }
//                catch (Exception ex){
//                    ex.printStackTrace();
//                }
//            }
//
//        }
//    }

    public class GerenciarLogin extends AsyncTask<String, Void, String> {

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
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String dto) {
            super.onPostExecute(dto);
            dialog.dismiss();

            if (dto != null) {
                try {
                    JSONObject obj = new JSONObject(dto);
//                    usuario.setIdUsuario(obj.getInt("id"));
//                    usuario.setNome(obj.getString("nome"));
//                    usuario.setEmail(obj.getString("email"));
                    Log.d("Teste", "" + usuario.getIdUsuario());
                    if (usuario.getIdUsuario() != 0) {
                        abrirTelaPrincipal();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                usuario.setIdUsuario(1);
                Log.d("Teste", "" + usuario.getIdUsuario());
                if (usuario.getIdUsuario() != 0) {
                    abrirTelaPrincipal();
                }
                Toast.makeText(LoginActivity.this, "E-mail ou senha não encontrados",
                        Toast.LENGTH_LONG).show();
            }
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
