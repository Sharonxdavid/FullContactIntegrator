import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class FullContactIntegration {

	public static String[] emailAddress;
	public static String apiKey = "68e63d17dd554d79";
	public static Stats stats = new Stats();
	public static HashMap<String, String> dictionary;
	
	/**
	 * read input file and store in a stringbuilder
	 */
	public static StringBuilder readFromInputFile() throws IOException{
		StringBuilder fileContent = new StringBuilder();
		Scanner scanner = new Scanner( System.in );
		System.out.println("Welcom, please insert input file name");
		String inFile = scanner.next();
		
		BufferedReader br = new BufferedReader(new FileReader(inFile));
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
	
	/**
	 * send http reqest and receive specific contact information
	 * @param name
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String getContactDetails(String name) throws UnsupportedEncodingException, IOException{
		URL url = new URL("https://api.fullcontact.com/v2/person.json?email=" + name);
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("X-FullContact-APIKey", apiKey);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return (response.toString());
		
	}
	
	
	/**
	 * parse json (http response) to params
	 * @param jsonAsString
	 * @return
	 */
	public static String parseResponse(String jsonAsString){
		StringBuilder res = new StringBuilder();
		
		String age = "";
		JsonObject jsonObject = new JsonParser().parse(jsonAsString).getAsJsonObject();
		double likeli = 0;
		String fullName = "";
		String gender = "";
		String city = "";
		String country = "";
		
		try{
		 likeli = jsonObject.get("likelihood").getAsDouble();
		 fullName = jsonObject.get("contactInfo").getAsJsonObject().get("fullName").getAsString();
		 gender = jsonObject.get("demographics").getAsJsonObject().get("gender").getAsString();
		 city = jsonObject.get("demographics").getAsJsonObject().get("locationDeduced").getAsJsonObject().get("city").getAsJsonObject().get("name").getAsString();
		 country = jsonObject.get("demographics").getAsJsonObject().get("locationDeduced").getAsJsonObject().get("county").getAsJsonObject().get("name").getAsString();
		 age = jsonObject.get("demographics").getAsJsonObject().get("age").getAsString();
		 
		}catch (Exception e){
			//System.out.println("not found");
		}
		
		if(!(fullName.isEmpty())){
			res.append(fullName);
		}
		if (!(age == null)){
			res.append(", " + age);
		}
		if (!(gender.isEmpty())){
			res.append(", " + gender);
			if(gender.equalsIgnoreCase("female")){
				stats.setFemaleCounter(stats.getFemaleCounter() + 1);
				stats.setFemaleAccuracyAvg((stats.getFemaleAccuracyAvg() + likeli) / stats.getFemaleCounter());
			}
			if(gender.equalsIgnoreCase("male")){
				stats.setMaleCounter(stats.getMaleCounter() + 1);
				stats.setMaleAccuracyAvg((stats.getMaleAccuracyAvg() + likeli) / stats.getMaleCounter());
			}
			
		}
		if (!(city.isEmpty())){
			res.append(", " + city);
		}
		if (!(country.isEmpty())){
			res.append(", " + country);
		}
		return res.toString();
	}
	
	
	
	public static void main(String[] args) throws IOException {
		dictionary = new HashMap<>();
		
		
		parseInputFile(readFromInputFile());
		String jsonRes;
		
		System.out.println("*******************************");
		System.out.println("      Here are the results:");
		System.out.println("*******************************");
		
		
		//check if user is in cache
		for (int i = 0; i < emailAddress.length; i++) {
			if(dictionary.containsKey(emailAddress[i])){
				continue;
			}
			else{
				jsonRes = getContactDetails(emailAddress[i]);
				dictionary.put(emailAddress[i], parseResponse(jsonRes));
			}
		}
		
		//print full list
		System.out.println("Here is the full list:");
		for (String string : dictionary.values()) {
			System.out.println(string);
		}
		
		System.out.println("---------------------");
		System.out.println("Out of " + dictionary.size() + " employees:");
		System.out.println(stats.getFemaleCounter() + " are females (at least " + stats.getFemaleAccuracyAvg() + " accuracy)" );
		System.out.println(stats.getMaleCounter() + " are males (at least " + stats.getMaleAccuracyAvg() + " accuracy)" );
	}
}
