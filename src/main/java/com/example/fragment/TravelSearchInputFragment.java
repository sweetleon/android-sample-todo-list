package com.example.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.R;
import com.example.network.SabreClient;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.apache.http.client.HttpClient;
import org.w3c.dom.Text;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Date;

public class TravelSearchInputFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.todo_travel_input, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.travel_go_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
                TextView origin = (TextView) view.findViewById(R.id.travel_dept_city);
                TextView dest = (TextView) view.findViewById(R.id.travel_dest_city);
                TextView deptDate = (TextView) view.findViewById(R.id.travel_dept_date);
                TextView rtnDate = (TextView) view.findViewById(R.id.travel_return_date);



                Gson gson = new GsonBuilder()
//                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                        .registerTypeAdapter(Date.class, new DateTypeAdapter())
                        .create();

                RequestInterceptor requestInterceptor = new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addHeader("Authorization", "Bearer T1RLAQLVuanq1RQaDGZrh4cinqqvqMOPdBDUCW47itJHg50DkDQsckzlAACgrBCgNX0VtkTWjgFQtRyjGD113CZ/NR3RFdOJp7zHmZhTQ7IGUVXGFqw+1Wq/eSl7Bc9tr9HvoR8Z4HxP9KQ3k4MgxGsyV6Xkb8oFFOMi7+kDIbvMNfBvu9B2QQps9NL+GlpnJ2shYeOoDGhiA22qGX9reVwjL7en+31rJOZex9MHNrPczVzGvGE+Of2rY1OmQwnBl7Jh1jUke3Rlw8n+CA**");
                        request.addHeader("X-Originating-Ip","207.114.222.38");
                    }
                };

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("https://api.test.sabre.com")
                        .setConverter(new GsonConverter(gson))
                        .setRequestInterceptor(requestInterceptor)
                        .build();

                // TODO
                origin.setText("SFO");
                dest.setText("JFK");
                deptDate.setText("2015-08-01");
                rtnDate.setText("2015-08-11");

                SabreClient sabreClient = restAdapter.create(SabreClient.class);
                sabreClient.searchFlights(origin.getText().toString(), dest.getText().toString(), deptDate.getText().toString(), rtnDate.getText().toString(), new Callback<SabreClient.FlightSearchResponse>() {
                    @Override
                    public void success(SabreClient.FlightSearchResponse flightSearchResponse, Response response) {
                        Log.d(getClass().getName(), flightSearchResponse.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(getClass().getName(), error.toString());
                    }
                });

            }
        });
    }
}
