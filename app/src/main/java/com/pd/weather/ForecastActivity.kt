package com.pd.weather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonReader
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.gson.Gson
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

class TemperatureVal(val Value : Int)
class Temperature(val Minimum: TemperatureVal, val Maximum : TemperatureVal)
class DailyForecast(val Date : Date , val Temperature : Temperature)
class Forecast (val DailyForecasts : List<DailyForecast>)

interface ForecastApi {
    @GET("forecasts/v1/daily/5day/{cityKey}?apikey=mGP6gvHA0g0vSSqG4W8t3KRqP6d00XqG")
    fun getForecast(@Path("cityKey") cityKey: Int): Call<Forecast>
}

class City (val Key : Int)
interface LocationApi {
    @GET("locations/v1/cities/search?apikey=mGP6gvHA0g0vSSqG4W8t3KRqP6d00XqG")
    fun listCities(@Query("q", encoded=true) q: String): Call<List<City>>
}

class ForecastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dataservice.accuweather.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var locationApi  = retrofit.create(LocationApi::class.java)

        var cityName = intent.extras.getString("cityName")

        if (cityName == null){
            cityName = "San Jose"
        }


        val forecastCallBack = object : Callback<Forecast> {
            override fun onResponse(call: Call<Forecast>, response: Response<Forecast>) {
                println("Recieved Response for Forecast API *************************")

                val dailyForecasts = response.body()?.DailyForecasts

                var forecasts = mutableListOf<String>()
                if(dailyForecasts != null) {
                    for (dailyForecast in dailyForecasts){
                        forecasts.add("${dailyForecast.Date.month+1}-${dailyForecast.Date.date}, ${dailyForecast.Temperature.Minimum.Value}," + " ${dailyForecast.Temperature.Maximum.Value}")
                    }
                }

                var listView = findViewById<ListView>(R.id.forecastListView)
                var adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, forecasts)

                listView.adapter = adapter

            }

            override fun onFailure(call: Call<Forecast>, t: Throwable) {
                println("Recieved Failure for Forecast API *************************, $t")

            }
        }


        val locationCallBack = object : Callback<List<City>>{
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                println("Recieved Response for Location API *************************")
                for (city in response.body()!!){
                    println(city.Key)
                }

                response.body()!![0].Key
                var forecastApi  = retrofit.create(ForecastApi::class.java)
                forecastApi.getForecast(response.body()!![0].Key).enqueue(forecastCallBack)
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable) {
                println("Received Failure ************************")
                println(t)
            }
        }
        locationApi.listCities(cityName).enqueue(locationCallBack)



    }
}
