package com.example.fragment;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.example.R;
import com.example.network.SabreClient;
import com.example.test.MyTestRunner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.util.ActivityController;
import retrofit.Callback;
import retrofit.RestAdapter;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(MyTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 18)
public class TravelSearchInputFragmentTest {
    private ActivityController<FakeFragmentActivity> activityController;
    private FakeFragmentActivity activity;
    private TravelSearchInputFragment fragment;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(FakeFragmentActivity.class).create().resume().visible();
        activity = activityController.get();
        fragment = new TravelSearchInputFragment();
        activity.getFragmentManager().beginTransaction().add(fragment, "abc").commit();
    }

    @Test
    public void fragmentCanBeCreated() throws Exception {
        assertThat(fragment).isNotNull();
    }

    @Test
    public void testShowFields() throws Exception {
        View dept_date = fragment.getView().findViewById(R.id.travel_dept_date);
        assertThat(dept_date).isNotNull();


        View return_date = fragment.getView().findViewById(R.id.travel_return_date);
        assertThat(return_date).isNotNull();


        View dept_city = fragment.getView().findViewById(R.id.travel_dept_city);
        assertThat(dept_city).isNotNull();


        View dest_city = fragment.getView().findViewById(R.id.travel_dest_city);
        assertThat(dest_city).isNotNull();

        View go_button = fragment.getView().findViewById(R.id.travel_go_button);
        assertThat(go_button).isNotNull();
    }

    @Test
    public void pressingButton_showsSpinner() throws Exception {
        assertThat(fragment.getView().findViewById(android.R.id.progress).getVisibility()).isEqualTo(View.GONE);
        fragment.getView().findViewById(R.id.travel_go_button).performClick();
        assertThat(fragment.getView().findViewById(android.R.id.progress).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    @Config(shadows = {MockingRestAdapterBuilder.class})
    public void whenApiReturnsData_hidesSpinner() throws Exception {
        fragment.getView().findViewById(R.id.travel_go_button).performClick();

        ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(MockingRestAdapterBuilder.latestSabreClient).searchFlights(anyString(), anyString(), anyString(), anyString(), callbackArgumentCaptor.capture());
        Map flightData = new GsonBuilder().create().fromJson(getJsonData(), Map.class);
        callbackArgumentCaptor.getValue().success(flightData, null);

        assertThat(fragment.getView().findViewById(android.R.id.progress).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    @Config(shadows = {MockingRestAdapterBuilder.class})
    public void testDisplayFlightInfo() throws Exception {
        fragment.getView().findViewById(R.id.travel_go_button).performClick();

        ArgumentCaptor<Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(MockingRestAdapterBuilder.latestSabreClient).searchFlights(anyString(), anyString(), anyString(), anyString(), callbackArgumentCaptor.capture());
        Map flightData = new GsonBuilder().create().fromJson(getJsonData(), Map.class);
        callbackArgumentCaptor.getValue().success(flightData, null);

        ListView results = (ListView) fragment.getView().findViewById(android.R.id.list);
        shadowOf(results).populateItems();

        View firstRow = results.getChildAt(0);

        TextView price = (TextView) firstRow.findViewById(R.id.price);
        assertThat(price.getText()).isEqualTo("347.20");

        TextView airline = (TextView) firstRow.findViewById(R.id.airline);
        assertThat(airline.getText()).isEqualTo("SY");

        TextView times = (TextView) firstRow.findViewById(R.id.times);

        assertThat(times.getText()).isEqualTo("07:15 - 12:05\n23:55 - 10:35");

        TextView stops = (TextView) firstRow.findViewById(R.id.stops);
        assertThat(stops.getText()).isEqualTo("1 stop(s)");
    }

    private String getJsonData() {

        return "{\n" +
                "  \"PricedItineraries\": [\n" +
                "    {\n" +
                "      \"AirItinerary\": {\n" +
                "        \"OriginDestinationOptions\": {\n" +
                "          \"OriginDestinationOption\": [\n" +
                "            {\n" +
                "              \"FlightSegment\": [\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"JFK\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 180,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"O\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-01T07:15:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-01T09:15:00\",\n" +
                "                  \"FlightNumber\": 240,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 240,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -4\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"LAX\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -7\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 225,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"I\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-01T10:20:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-01T12:05:00\",\n" +
                "                  \"FlightNumber\": 423,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 423,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  }\n" +
                "                }\n" +
                "              ],\n" +
                "              \"ElapsedTime\": 470\n" +
                "            },\n" +
                "            {\n" +
                "              \"FlightSegment\": [\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"LAX\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 202,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"O\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-10T23:55:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-11T05:17:00\",\n" +
                "                  \"FlightNumber\": 430,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 430,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -7\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"JFK\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -4\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 155,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"I\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-11T07:00:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-11T10:35:00\",\n" +
                "                  \"FlightNumber\": 243,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 243,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  }\n" +
                "                }\n" +
                "              ],\n" +
                "              \"ElapsedTime\": 460\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"DirectionInd\": \"Return\"\n" +
                "      },\n" +
                "      \"TPA_Extensions\": {\n" +
                "        \"ValidatingCarrier\": {\n" +
                "          \"Code\": \"SY\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"SequenceNumber\": 1,\n" +
                "      \"AirItineraryPricingInfo\": {\n" +
                "        \"PTC_FareBreakdowns\": {\n" +
                "          \"PTC_FareBreakdown\": {\n" +
                "            \"FareBasisCodes\": {\n" +
                "              \"FareBasisCode\": [\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"JFK\",\n" +
                "                  \"ArrivalAirportCode\": \"MSP\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"MSP\",\n" +
                "                  \"AvailabilityBreak\": true,\n" +
                "                  \"ArrivalAirportCode\": \"LAX\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"LAX\",\n" +
                "                  \"ArrivalAirportCode\": \"MSP\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"MSP\",\n" +
                "                  \"AvailabilityBreak\": true,\n" +
                "                  \"ArrivalAirportCode\": \"JFK\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            \"PassengerTypeQuantity\": {\n" +
                "              \"Quantity\": 1,\n" +
                "              \"Code\": \"ADT\"\n" +
                "            },\n" +
                "            \"PassengerFare\": {\n" +
                "              \"FareConstruction\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"DecimalPlaces\": 2,\n" +
                "                \"Amount\": 280.94\n" +
                "              },\n" +
                "              \"TotalFare\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"Amount\": \"347.20\"\n" +
                "              },\n" +
                "              \"Taxes\": {\n" +
                "                \"TotalTax\": {\n" +
                "                  \"CurrencyCode\": \"USD\",\n" +
                "                  \"DecimalPlaces\": 2,\n" +
                "                  \"Amount\": 66.26\n" +
                "                },\n" +
                "                \"Tax\": [\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"US1\",\n" +
                "                    \"Amount\": 21.06\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"ZP\",\n" +
                "                    \"Amount\": \"16.00\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"AY\",\n" +
                "                    \"Amount\": \"11.20\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"XF\",\n" +
                "                    \"Amount\": \"18.00\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              },\n" +
                "              \"BaseFare\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"Amount\": 280.94\n" +
                "              },\n" +
                "              \"EquivFare\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"DecimalPlaces\": 2,\n" +
                "                \"Amount\": 280.94\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"FareInfos\": {\n" +
                "          \"FareInfo\": [\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"TPA_Extensions\": {\n" +
                "          \"DivideInParty\": {\n" +
                "            \"Indicator\": false\n" +
                "          }\n" +
                "        },\n" +
                "        \"ItinTotalFare\": {\n" +
                "          \"FareConstruction\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": 280.94\n" +
                "          },\n" +
                "          \"TotalFare\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": \"347.20\"\n" +
                "          },\n" +
                "          \"Taxes\": {\n" +
                "            \"Tax\": [\n" +
                "              {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"DecimalPlaces\": 2,\n" +
                "                \"TaxCode\": \"TOTALTAX\",\n" +
                "                \"Amount\": 66.26\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          \"BaseFare\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": 280.94\n" +
                "          },\n" +
                "          \"EquivFare\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": 280.94\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"TicketingInfo\": {\n" +
                "        \"TicketType\": \"eTicket\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"AirItinerary\": {\n" +
                "        \"OriginDestinationOptions\": {\n" +
                "          \"OriginDestinationOption\": [\n" +
                "            {\n" +
                "              \"FlightSegment\": [\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"JFK\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 180,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"O\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-01T07:15:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-01T09:15:00\",\n" +
                "                  \"FlightNumber\": 240,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 240,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -4\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"LAX\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -7\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 225,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"I\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-01T10:20:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-01T12:05:00\",\n" +
                "                  \"FlightNumber\": 423,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 423,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  }\n" +
                "                }\n" +
                "              ],\n" +
                "              \"ElapsedTime\": 470\n" +
                "            },\n" +
                "            {\n" +
                "              \"FlightSegment\": [\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"LAX\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 202,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"O\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-10T13:00:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-10T18:22:00\",\n" +
                "                  \"FlightNumber\": 424,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 424,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -7\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"DepartureAirport\": {\n" +
                "                    \"LocationCode\": \"MSP\"\n" +
                "                  },\n" +
                "                  \"ArrivalAirport\": {\n" +
                "                    \"LocationCode\": \"JFK\"\n" +
                "                  },\n" +
                "                  \"MarketingAirline\": {\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"ArrivalTimeZone\": {\n" +
                "                    \"GMTOffset\": -4\n" +
                "                  },\n" +
                "                  \"TPA_Extensions\": {\n" +
                "                    \"eTicket\": {\n" +
                "                      \"Ind\": true\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"StopQuantity\": 0,\n" +
                "                  \"ElapsedTime\": 170,\n" +
                "                  \"ResBookDesigCode\": \"L\",\n" +
                "                  \"MarriageGrp\": \"I\",\n" +
                "                  \"Equipment\": {\n" +
                "                    \"AirEquipType\": 738\n" +
                "                  },\n" +
                "                  \"DepartureDateTime\": \"2015-08-10T19:25:00\",\n" +
                "                  \"ArrivalDateTime\": \"2015-08-10T23:15:00\",\n" +
                "                  \"FlightNumber\": 249,\n" +
                "                  \"OperatingAirline\": {\n" +
                "                    \"FlightNumber\": 249,\n" +
                "                    \"Code\": \"SY\"\n" +
                "                  },\n" +
                "                  \"DepartureTimeZone\": {\n" +
                "                    \"GMTOffset\": -5\n" +
                "                  }\n" +
                "                }\n" +
                "              ],\n" +
                "              \"ElapsedTime\": 435\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"DirectionInd\": \"Return\"\n" +
                "      },\n" +
                "      \"TPA_Extensions\": {\n" +
                "        \"ValidatingCarrier\": {\n" +
                "          \"Code\": \"SY\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"SequenceNumber\": 2,\n" +
                "      \"AirItineraryPricingInfo\": {\n" +
                "        \"PTC_FareBreakdowns\": {\n" +
                "          \"PTC_FareBreakdown\": {\n" +
                "            \"FareBasisCodes\": {\n" +
                "              \"FareBasisCode\": [\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"JFK\",\n" +
                "                  \"ArrivalAirportCode\": \"MSP\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"MSP\",\n" +
                "                  \"AvailabilityBreak\": true,\n" +
                "                  \"ArrivalAirportCode\": \"LAX\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"LAX\",\n" +
                "                  \"ArrivalAirportCode\": \"MSP\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"BookingCode\": \"L\",\n" +
                "                  \"DepartureAirportCode\": \"MSP\",\n" +
                "                  \"AvailabilityBreak\": true,\n" +
                "                  \"ArrivalAirportCode\": \"JFK\",\n" +
                "                  \"content\": \"LT21\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            \"PassengerTypeQuantity\": {\n" +
                "              \"Quantity\": 1,\n" +
                "              \"Code\": \"ADT\"\n" +
                "            },\n" +
                "            \"PassengerFare\": {\n" +
                "              \"FareConstruction\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"DecimalPlaces\": 2,\n" +
                "                \"Amount\": 280.94\n" +
                "              },\n" +
                "              \"TotalFare\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"Amount\": \"347.20\"\n" +
                "              },\n" +
                "              \"Taxes\": {\n" +
                "                \"TotalTax\": {\n" +
                "                  \"CurrencyCode\": \"USD\",\n" +
                "                  \"DecimalPlaces\": 2,\n" +
                "                  \"Amount\": 66.26\n" +
                "                },\n" +
                "                \"Tax\": [\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"US1\",\n" +
                "                    \"Amount\": 21.06\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"ZP\",\n" +
                "                    \"Amount\": \"16.00\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"AY\",\n" +
                "                    \"Amount\": \"11.20\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"CurrencyCode\": \"USD\",\n" +
                "                    \"DecimalPlaces\": 2,\n" +
                "                    \"TaxCode\": \"XF\",\n" +
                "                    \"Amount\": \"18.00\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              },\n" +
                "              \"BaseFare\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"Amount\": 280.94\n" +
                "              },\n" +
                "              \"EquivFare\": {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"DecimalPlaces\": 2,\n" +
                "                \"Amount\": 280.94\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"FareInfos\": {\n" +
                "          \"FareInfo\": [\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"TPA_Extensions\": {\n" +
                "                \"Cabin\": {\n" +
                "                  \"Cabin\": \"Y\"\n" +
                "                },\n" +
                "                \"SeatsRemaining\": {\n" +
                "                  \"BelowMin\": false,\n" +
                "                  \"Number\": 4\n" +
                "                }\n" +
                "              },\n" +
                "              \"FareReference\": \"L\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"TPA_Extensions\": {\n" +
                "          \"DivideInParty\": {\n" +
                "            \"Indicator\": false\n" +
                "          }\n" +
                "        },\n" +
                "        \"ItinTotalFare\": {\n" +
                "          \"FareConstruction\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": 280.94\n" +
                "          },\n" +
                "          \"TotalFare\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": \"347.20\"\n" +
                "          },\n" +
                "          \"Taxes\": {\n" +
                "            \"Tax\": [\n" +
                "              {\n" +
                "                \"CurrencyCode\": \"USD\",\n" +
                "                \"DecimalPlaces\": 2,\n" +
                "                \"TaxCode\": \"TOTALTAX\",\n" +
                "                \"Amount\": 66.26\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          \"BaseFare\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": 280.94\n" +
                "          },\n" +
                "          \"EquivFare\": {\n" +
                "            \"CurrencyCode\": \"USD\",\n" +
                "            \"DecimalPlaces\": 2,\n" +
                "            \"Amount\": 280.94\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"TicketingInfo\": {\n" +
                "        \"TicketType\": \"eTicket\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"ReturnDateTime\": \"2015-08-10\",\n" +
                "  \"DepartureDateTime\": \"2015-08-01\",\n" +
                "  \"DestinationLocation\": \"LAX\",\n" +
                "  \"OriginLocation\": \"JFK\",\n" +
                "  \"Links\": [\n" +
                "    {\n" +
                "      \"rel\": \"self\",\n" +
                "      \"href\": \"https://api.test.sabre.com/v1/shop/flights?origin=JFK&destination=LAX&departuredate=2015-08-01&returndate=2015-08-10&onlineitinerariesonly=N&limit=10&offset=1&eticketsonly=N&sortby=totalfare&order=asc&sortby2=departuretime&order2=asc&pointofsalecountry=US\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"rel\": \"linkTemplate\",\n" +
                "      \"href\": \"https://api.test.sabre.com/v1/shop/flights?origin=<origin>&destination=<destination>&departuredate=<departuredate>&returndate=<returndate>&offset=<offset>&limit=<limit>&sortby=<sortby>&order=<order>&sortby2=<sortby2>&order2=<order2>&minfare=<minfare>&maxfare=<maxfare>&includedcarriers=<includedcarriers>&excludedcarriers=<excludedcarriers>&outboundflightstops=<outboundflightstops>&inboundflightstops=<inboundflightstops>&outboundstopduration=<outboundstopduration>&inboundstopduration=<inboundstopduration>&outbounddeparturewindow=<outbounddeparturewindow>&outboundarrivalwindow=<outboundarrivalwindow>&inbounddeparturewindow=<inbounddeparturewindow>&inboundarrivalwindow=<inboundarrivalwindow>&onlineitinerariesonly=<onlineitinerariesonly>&eticketsonly=<eticketsonly>&includedconnectpoints=<includedconnectpoints>&excludedconnectpoints=<excludedconnectpoints>&pointofsalecountry=<pointofsalecountry>&passengercount=<passengercount>\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    public static class FakeFragmentActivity extends FragmentActivity {
    }

    @Implements(RestAdapter.Builder.class)
    public static class MockingRestAdapterBuilder {
        private static SabreClient latestSabreClient;

        @Implementation
        public RestAdapter build() {
            latestSabreClient = Mockito.mock(SabreClient.class, CALLS_REAL_METHODS);
            RestAdapter restAdapter = mock(RestAdapter.class);
            when(restAdapter.create(SabreClient.class)).thenReturn(latestSabreClient);
            return restAdapter;
        }
    }
}