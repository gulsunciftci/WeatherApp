package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //İlk olarak, bir API'den hava durumu bilgilerini almamız gerektiğinden, API'nin URL'sine bir http isteğinde bulunacağız. Bunun için API keyini giriyoruz.
    val CITY: String = "erzurum,tr"
    val API: String = "7b27e9a4e02ed71c6e2bb5cc8b0b1dfb" //API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()

    }
//AsyncTask’ ı database işlemleri(SqlLite vb) için veri çekme, internet üzerinden indirme(download) yapma
// veya Web Servis aracılığı ile veri parse etme işlemlerinde kullanabiliriz.

    inner class weatherTask() : AsyncTask<String, Void, String>() {

        //onPreExecute: Arka plan işlemi başlamadan önce ön yüzde değiştirilmesi istenen değişkenlerin (ProgressBar gibi animasyonlar)
        // ve AsyncTask içinde gerekli değişkenlerin değer ataması yapılır.

        override fun onPreExecute() {
            super.onPreExecute()

            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        //doInBackground: Arka planda yapılması istenen işlem burada gerçekleşir.
        // Bu metod içinde yapılan işlemler ön yüzde kullanıcının uygulamayı kullanmasını kesinlikle etkilemez.
        // Eğer buradaki işlemler sonucunda ana akışa bir değişken gönderilmesi gerekiyorsa return metodu ile bu değişken onPostExecute metoduna paslanabilir.

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        //onPostExecute: doInBackground metodu tamamlandıktan sonra işlemlerin sonucu bu metoda değişken ile gönderilir.
        // Buradaki işlemler ana akışı etkiler ve herhangi bir hataya sebep olmaz.
        // Arka plandaki işlemden gelen bir veri ön yüzde gösterilmek isteniyorsa bu metod içinde gösterim işlemi yapılabilir.


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {

                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")


                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity


                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}