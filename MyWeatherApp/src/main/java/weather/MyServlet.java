package weather;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
//@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public MyServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String inputData = request.getParameter("city");
//		System.out.println(inputData);
//		doGet(request, response);
		
//		API setup and get city from the form input
		String apiKey =  "a66030a74d3efb1ed367423518349b89";
		String city = request.getParameter("city");
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
		
//		API Integration
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
//		Reading the data from network
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
//		Want to store the data
		StringBuilder responseContent = new StringBuilder();
		
//		To take input from the reader
		Scanner scanner = new Scanner(reader);
		
		while(scanner.hasNext()) {
			responseContent.append(scanner.nextLine());
		}
		
		scanner.close();

		
//		TypeCasting = parsing the data into the JSON
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
		System.out.println(jsonObject);
//		System.out.println(responseContent);
		
//		date and time
		long dateTimeStamp = jsonObject.get("dt").getAsLong() * 1000;
		String date = new Date(dateTimeStamp).toString(); // object type casted in toString
		
//		temperature
		double tempKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble(); // so, here from the main object, we are taking value of temp in the form of double
		int tempCelsius = (int) (tempKelvin - 273.15);
		
//		humidity
		int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt(); // so, here from the main object, we are taking value of humidity in the form of int
		
//		wind speed
		double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble(); // so, here from the wind object, we are taking value of speed in the form of double
		
//		weather condition
		String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString(); // so, here i am taking the whole array weather, and from the 1st index, getting main as jsonObject, and storing it in the form of String
		
//		set the data as request attribute (for sending to JSP page)
		request.setAttribute("date", date);
		request.setAttribute("city", city);
		request.setAttribute("temperature", tempCelsius);
		request.setAttribute("weatherCondition", weatherCondition);
		request.setAttribute("humidity", humidity);
		request.setAttribute("windSpeed", windSpeed);
		request.setAttribute("weatherData", responseContent.toString());
		
		connection.disconnect();
		
//		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
//		dispatcher.forward(request, response);
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
