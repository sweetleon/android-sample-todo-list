package com.example.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;
import com.example.network.SabreClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
                final View spinner = view.findViewById(android.R.id.progress);
                spinner.setVisibility(View.VISIBLE);
                TextView origin = (TextView) view.findViewById(R.id.travel_dept_city);
                TextView dest = (TextView) view.findViewById(R.id.travel_dest_city);
                TextView deptDate = (TextView) view.findViewById(R.id.travel_dept_date);
                TextView rtnDate = (TextView) view.findViewById(R.id.travel_return_date);


                Gson gson = new GsonBuilder().create();

                RequestInterceptor requestInterceptor = new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addHeader("Authorization", "Bearer T1RLAQLVuanq1RQaDGZrh4cinqqvqMOPdBDUCW47itJHg50DkDQsckzlAACgrBCgNX0VtkTWjgFQtRyjGD113CZ/NR3RFdOJp7zHmZhTQ7IGUVXGFqw+1Wq/eSl7Bc9tr9HvoR8Z4HxP9KQ3k4MgxGsyV6Xkb8oFFOMi7+kDIbvMNfBvu9B2QQps9NL+GlpnJ2shYeOoDGhiA22qGX9reVwjL7en+31rJOZex9MHNrPczVzGvGE+Of2rY1OmQwnBl7Jh1jUke3Rlw8n+CA**");
                        request.addHeader("X-Originating-Ip", "207.114.222.38");
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
                sabreClient.searchFlights(origin.getText().toString(), dest.getText().toString(), deptDate.getText().toString(), rtnDate.getText().toString(), new Callback<Map>() {
                    @Override
                    public void success(Map flightSearchResponse, Response response) {
                        Log.d(getClass().getName(), flightSearchResponse.toString());
                        spinner.setVisibility(View.GONE);

                        showResults(flightSearchResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(getClass().getName(), error.toString());
                    }
                });
            }
        });
    }

    private void showResults(Map flightSearchResponse) {
        View view = getView();
        final ListView searchResults = (ListView) view.findViewById(android.R.id.list);
        ArrayAdapter<Map> adapter = new ArrayAdapter<Map>(getActivity(), R.layout.todo_flight_details) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.todo_flight_details, parent, false);
                final Map pricedItinerary = getItem(position);


                TextView price = (TextView) view.findViewById(R.id.price);
                price.setText(getPrice(pricedItinerary));
                TextView airline = (TextView) view.findViewById(R.id.airline);
                airline.setText(getAirline(pricedItinerary));
                TextView times = (TextView) view.findViewById(R.id.times);
                times.setText(getTimeRanges(pricedItinerary));
                TextView stops = (TextView) view.findViewById(R.id.stops);
                stops.setText(getActivity().getString(R.string.number_of_stops, getNumberOfStops(pricedItinerary)));

                return view;
            }

            private int getNumberOfStops(Map pricedItinerary) {
                Map<String,Map> airItinerary = (Map<String, Map>) pricedItinerary.get("AirItinerary");
                Map<String,Object> originDestinationOptions = (Map<String, Object>) airItinerary.get("OriginDestinationOptions");
                List<Map> originDestinationOption = (List<Map>) originDestinationOptions.get("OriginDestinationOption");
                List<Map> flightSegments = (List<Map>) originDestinationOption.get(0).get("FlightSegment");
                return flightSegments.size()-1;
            }

            private String getPrice(Map pricedItinerary) {
                Map<String,Map> airItineraryPricingInfo = (Map<String, Map>) pricedItinerary.get("AirItineraryPricingInfo");
                Map<String,Object> itinTotalFare = (Map<String, Object>) airItineraryPricingInfo.get("ItinTotalFare");
                Map<String,Object> totalFare = (Map<String, Object>) itinTotalFare.get("TotalFare");
                return totalFare.get("Amount").toString();
            }

            private String getTimeRanges(Map pricedItinerary) {
                Map<String,Map> airItinerary = (Map<String, Map>) pricedItinerary.get("AirItinerary");
                Map<String,Object> originDestinationOptions = (Map<String, Object>) airItinerary.get("OriginDestinationOptions");
                List<Map> originDestinationOption = (List<Map>) originDestinationOptions.get("OriginDestinationOption");
                Map departing = originDestinationOption.get(0);
                Map returning = originDestinationOption.get(1);
                return getTimeRangeForJourney(departing) + "\n" + getTimeRangeForJourney(returning);
            }

            private String getTimeRangeForJourney(Map journey) {
                List<Map> flightSegments = (List<Map>) journey.get("FlightSegment");
                Map firstSegment = flightSegments.get(0);
                Map lastSegment = flightSegments.get(flightSegments.size()-1);
                return getTimeFromDateString(firstSegment.get("DepartureDateTime")) + " - " + getTimeFromDateString(lastSegment.get("ArrivalDateTime"));
            }

            private String getTimeFromDateString(Object dateString) {
                String s = dateString.toString();
                return s.substring(s.indexOf("T") + 1, s.length() - 3);
            }

            private String getAirline(Map pricedItinerary) {
                return ((Map<String, Map>) pricedItinerary.get("TPA_Extensions")).get("ValidatingCarrier").get("Code").toString();
            }
        };
        adapter.addAll((Collection<Map>) flightSearchResponse.get("PricedItineraries"));
        searchResults.setAdapter(adapter);
    }
}
