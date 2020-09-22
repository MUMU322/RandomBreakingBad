package com.example.randombreakingbad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Main activity manage our first page
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        final Button bouton = findViewById(R.id.button); //Button to get a random character

        final ImageView photo = findViewById(R.id.imageView); //Picture of the character
        final TextView nom = findViewById(R.id.champNom); //Name of the character
        final TextView surnom = findViewById(R.id.champSurnom); //Nickname of the character
        final TextView metier = findViewById(R.id.champMetier); //Jobs of the character
        final TextView saisons = findViewById(R.id.champSaisons); //Seasons he was on the show
        final TextView headnom = findViewById(R.id.textView1);
        final TextView headsurnom = findViewById(R.id.textView2);
        final TextView headmetier = findViewById(R.id.textView3);
        final TextView headsaison = findViewById(R.id.textView4);
//        final ConstraintLayout layoutout = findViewById(R.id.layout1);

        photo.setVisibility(View.INVISIBLE);
        nom.setVisibility(View.INVISIBLE);
        surnom.setVisibility(View.INVISIBLE);
        metier.setVisibility(View.INVISIBLE);
        saisons.setVisibility(View.INVISIBLE);
        headnom.setVisibility(View.INVISIBLE);
        headsurnom.setVisibility(View.INVISIBLE);
        headmetier.setVisibility(View.INVISIBLE);
        headsaison.setVisibility(View.INVISIBLE);

        final ProgressBar progress = findViewById(R.id.progressBar1); //Nickname of the character

        //call randomCharacter a first time on launch
        randomCharacter();

        //Listener on the button to get a new random character
        bouton.setOnClickListener(v ->{

            //layoutout.setVisibility(View.INVISIBLE);
            photo.setVisibility(View.INVISIBLE);
            nom.setVisibility(View.INVISIBLE);
            surnom.setVisibility(View.INVISIBLE);
            metier.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);
            saisons.setVisibility(View.INVISIBLE);
            headnom.setVisibility(View.INVISIBLE);
            headsurnom.setVisibility(View.INVISIBLE);
            headmetier.setVisibility(View.INVISIBLE);
            headsaison.setVisibility(View.INVISIBLE);
         randomCharacter();
        });

    }

    /**
     * Method to fetch a random character from the API and give it to the updateUI methode as a JSONObject
     */
    private void randomCharacter() {


        //OkHttp3 request
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.breakingbadapi.com/api/character/random")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                e.printStackTrace();
            }

            /**
             * Place to manipulate the result from the response.
             * @param call
             * @param response response of the request (with header and body...)
             * @throws IOException
             */
            @Override
            public void onResponse(@NotNull Call call, final Response response) throws IOException {
                //we check that the response is sucessful, that she have code in 200..300
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String res = response.body().string();
                    try {
                        //first we create a JSONArray to read it because this is not a JSONObject from the start
                        JSONArray arrayRes = new JSONArray(res);
                        //from the JSONArray we create a JSONObject
                        JSONObject objectRes = new JSONObject(arrayRes.getString(0));
                        Log.i("2", arrayRes.toString());
                        //update the UI with the character data
                        updateUI(objectRes);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Method to update the character UI to the new recieved character
     * @param objectRes a character informations
     */
    private void updateUI(JSONObject objectRes){
        //We define some variables to access our views
        final ImageView photo = findViewById(R.id.imageView); //Picture of the character
        final TextView nom = findViewById(R.id.champNom); //Name of the character
        final TextView surnom = findViewById(R.id.champSurnom); //Nickname of the character
        final TextView metier = findViewById(R.id.champMetier); //Jobs of the character
        final TextView saisons = findViewById(R.id.champSaisons); //Seasons he was on the show
        final ProgressBar progress = findViewById(R.id.progressBar1); //Nickname of the character
        final TextView headnom = findViewById(R.id.textView1);
        final TextView headsurnom = findViewById(R.id.textView2);
        final TextView headmetier = findViewById(R.id.textView3);
        final TextView headsaison = findViewById(R.id.textView4);
        //Necessary as glide.into() only run on main thread
        runOnUiThread(() -> {
            try {
                //Load the image from the link given at the "img" URL into our image view
                Glide.with(getApplicationContext())
                        .load(objectRes.getString("img"))
                        .into(photo);
                //Load the name
                nom.setText(objectRes.getString("name"));

                //Load the nickname
                surnom.setText(objectRes.getString("nickname"));

                //Load the Jobs
                //As the job are listed in a JSONArray within the occupation field we get each one in a loop to avoid undesirable character
                JSONArray tabmetier = objectRes.getJSONArray("occupation");
                String textMetier = "";
                for (int i = 0; i < tabmetier.length(); i++) {
                    //we only put comma if there is multiple jobs
                    if (i > 0) {
                        textMetier += ", " + tabmetier.getString(i);
                    }
                    else{
                        textMetier += tabmetier.getString(i);
                    }
                }
                metier.setText(textMetier);

                //Load the Seasons
                String textSeason = objectRes.getString("appearance");
                //Another way to remove undesirable character ([] and ,) here with regex and replaceAll
                textSeason = textSeason.replaceAll("[^0-9]", "  ");
                saisons.setText(textSeason);

                photo.setVisibility(View.VISIBLE);
                nom.setVisibility(View.VISIBLE);
                surnom.setVisibility(View.VISIBLE);
                metier.setVisibility(View.VISIBLE);
                saisons.setVisibility(View.VISIBLE);
                headnom.setVisibility(View.VISIBLE);
                headsurnom.setVisibility(View.VISIBLE);
                headmetier.setVisibility(View.VISIBLE);
                headsaison.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }
}

