package com.example.imagegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText inputText;
    MaterialButton generatebtn;
    ProgressBar progressBar;
    ImageView imageView;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputText=findViewById(R.id.input_text);
        generatebtn=findViewById(R.id.generate_btn);
        progressBar=findViewById(R.id.progressbar);
        imageView=findViewById(R.id.image_view);

        generatebtn.setOnClickListener((v)->{
            String text=inputText.getText().toString().trim();
            if (text.isEmpty())
            {
                inputText.setError("Text can't empty");
                return;
            }
            callAPI(text);
                });
    }
    void callAPI(String text)
    {
        setInProgress(true);
        JSONObject jsonBody=new JSONObject();
        try {
            jsonBody.put("prompt",text);
            jsonBody.put("size","256x256");

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(jsonBody.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(),"Failed to generate image",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    String imageUrl=jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    loadImage(imageUrl);
                    setInProgress(false);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            generatebtn.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            generatebtn.setVisibility(View.VISIBLE);
        }
    }
    void loadImage(String url)
    {
       runOnUiThread(()->{
           Picasso.get().load(url).into(imageView);
       });
    }
}

