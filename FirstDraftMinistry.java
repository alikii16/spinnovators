//This will be our first draft
//This class aims to read data from the Ministry of Environment & Energy from a JSON file (e.g. budget, transfers, fixed assets, targets, etc.), Display the data, Allow you to change values ​​(e.g. update the budget or a target) and then Save the changes back to the JSON file.

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class FirstDraftMinistry {

	private static final String MAIN_FILE = "json.file"; //Final variable in order to describe our file's name 
	private static Gson gson = new Gson();//Gson library from Google! 
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in); //Reads user's key

