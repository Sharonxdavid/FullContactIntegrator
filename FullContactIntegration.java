import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class FullContactIntegration {

	public static String[] emailAddress;
	public static String apiKey = "68e63d17dd554d79";
	
	//read input file and store in a stringbuilder
	public static StringBuilder readFromInputFile() throws IOException{
		StringBuilder fileContent = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader("addresses.txt"));
		while (br.ready()) {
			fileContent.append(br.readLine() + "\r\n");
		}
		return fileContent;
	}
	
	public static void parseInputFile(StringBuilder sb){
		if(sb == null){
			throw new NullPointerException("Input file is null");
		}
		
		//split sb by \r\n
		emailAddress = sb.toString().split("\r\n");
	}
	
	//send http reqest and receive specific contact information
	public static String getContactDetails(String name) throws UnsupportedEncodingException, IOException{
		URL url = new URL("https://api.fullcontact.com/v2/person.json?email=" + name);
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("X-FullContact-APIKey", apiKey);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		return (response.toString());
		
	}
	
	
	//parse json (http response) to HashMap
	public static void parseResponse(String jsonAsString){
		int age = 0;
		JsonObject jsonObject = new JsonParser().parse(jsonAsString).getAsJsonObject();
		String likeli = "";
		String fullName = "";
		//age = jsonObject.get("demographics").getAsJsonObject().get("age").getAsInt();
		String gender = "";
		String city = "";
		String country = "";
		
		try{
		 likeli = jsonObject.get("likelihood").getAsString();
		 fullName = jsonObject.get("contactInfo").getAsJsonObject().get("fullName").getAsString();
		//age = jsonObject.get("demographics").getAsJsonObject().get("age").getAsInt();
		 gender = jsonObject.get("demographics").getAsJsonObject().get("gender").getAsString();
		 city = jsonObject.get("demographics").getAsJsonObject().get("locationDeduced").getAsJsonObject().get("city").getAsJsonObject().get("name").getAsString();
		 country = jsonObject.get("demographics").getAsJsonObject().get("locationDeduced").getAsJsonObject().get("county").getAsJsonObject().get("name").getAsString();
		}catch (Exception e){
			System.out.println("not found");
		}
		
//		if(!(likeli == null)){
//			System.out.print(likeli + ", ");
//		}
		if(!(fullName == null)){
			System.out.print(fullName+ ", ");
		}
		if (!(age == 0)){
			System.out.print(age+ ", ");
		}
		if (!(gender == null)){
			System.out.print(gender+ ", ");
		}
		if (!(city == null)){
			System.out.print(city + " ");
		}
		if (!(country == null)){
			System.out.println(country);
		}
		
		
		//get name, age, gender, company, address(city, street and number, country)
//		System.out.println(jsonObject.get("contactInfo").getAsString());
//		System.out.println(jsonObject.get("status").getAsString());
//		System.out.println(jsonObject.get("status").getAsString());
//		System.out.println(jsonObject.get("status").getAsString());
//		System.out.println(jsonObject.get("status").getAsString());
		
	}
	
	public static void main(String[] args) throws IOException {
		
		parseInputFile(readFromInputFile());
		String jsonRes;
		
		System.out.println("*******************************");
		System.out.println("Welcome! Here are the results:");
		System.out.println("*******************************");
		System.out.println("Out of " + emailAddress.length + " employees:");
		
		
		System.out.println("Here is the full list:");
		for (int i = 0; i < emailAddress.length; i++) {
			System.out.println(emailAddress[i]);
//			jsonRes = getContactDetails("bart@fullcontact.com");
			jsonRes = getContactDetails(emailAddress[i]);
			System.out.println("***");
			parseResponse(jsonRes);
		}

	}

}
