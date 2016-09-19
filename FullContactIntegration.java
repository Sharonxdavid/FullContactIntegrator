import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FullContactIntegration {

	public static String[] emailAddress;
	
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
	
	public static void main(String[] args) throws IOException {
		parseInputFile(readFromInputFile());
		for (int i = 0; i < emailAddress.length; i++) {
			System.out.println(emailAddress[i]);
		}

	}

}
