package com.example.windy

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.windy.api.WeatherApi
import com.example.windy.api.WeatherResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var textTemperature: TextView
    private lateinit var textWindSpeed: TextView
    private val apiKey: String = BuildConfig.WINDY_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar os elementos de interface
        textTemperature = findViewById(R.id.textTemperature)
        textWindSpeed = findViewById(R.id.textWindSpeed)

        // Inicializar o mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Exemplo de chamada à API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.windy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)
        val call = weatherApi.getWeather(37.7749, -122.4194, "mJFL27gd9YG25bPFsxn025FK0dMgIidF")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherData: WeatherResponse? = response.body()

                    // Log para verificar os dados recebidos
                    Log.d("MainActivity", "WeatherData received: $weatherData")

                    // Atualizar as informações de tempo na interface
                    weatherData?.let {
                        val temperatureText = getString(R.string.temperature_format, it.temperature)
                        Log.d("MainActivity", "Temperature: ${it.temperature} °C")
                        //val windSpeedText = getString(R.string.wind_speed_format, it.windSpeed)
                        //textWindSpeed.text = windSpeedText

                        textTemperature.text = temperatureText
                    }
                } else {
                    // Log para verificar a resposta não bem-sucedida
                    Log.e("MainActivity", "Failed to get weather data. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Lidar com falha na chamada à API
                t.printStackTrace()

                // Log para verificar a falha
                Log.e("MainActivity", "Failed to get weather data. Error: ${t.message}")
            }
        })
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        // Configurar um marcador no mapa
        val location = LatLng(37.7749, -122.4194) // Coordenadas de São Francisco
        googleMap.addMarker(MarkerOptions().position(location).title("Marker in San Francisco"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }
}
