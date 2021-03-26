import java.util.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;

class Lux{

	private static Random rand = new Random();
    private static String[] playerCoord = new String[3];
    private static Scanner console = new Scanner(System.in);
 
    private static String strSize; //dimensions of playable map
    private static boolean confirmedSize = false;
    private static int floorCount = 1;
    private static int fuel;
    private static int fuelRoC = 1; //fuel rate of consumption; how fast it burns
    private static int fuelConstant;
    private static int visibility;
	
    public static void giveIntro(){
    	System.out.println("\nYou awake in an ancient ruin, your only "+
    	"possessions being an oil lantern, very little fuel, and a "+
    	"burning desire to make your way out.");
    	
    	System.out.println("Controls:\n'W' to go up\n'S' to go down\n"+
    	"'A' to go left\n"+"'D' to go right\n'X' is the stairs "+
    	"leading to the next level\n'F' is extra lantern fuel - "+
    	"be sure to pick it up! Darkness here means death!\n"+
    	"The rest is up to you. Good luck!");
    }
    		
    		
    //everything about map-making: get size, generate blank, & furnish
    public static String[][] getMap(){
    	if (confirmedSize == false){
    		boolean validInput = false;
    		while (!validInput){
    			validInput = getValidInput();
    		}
    	}
    	String[][] map = genMap(genGrid(strSize));
    	String[][] keyCoords = spawnSpecialPoints(map);
    	return fillMap(map, keyCoords);
    }
    
    public static boolean getValidInput(){
    		System.out.print("Please enter two positive"+
    		" numbers for the grid dimensions(larger than 4x4):\nw h: ");
    		strSize = console.nextLine();
    	
    		int sepSpot = strSize.indexOf(" ");
    		if (sepSpot == -1){
    			return false;
    		}
    		int strLen = strSize.length();
    	
    		String numList = "0123456789";
    	
    		String str1 = strSize.substring(0, sepSpot);
    		for (int q = 0; q<str1.length(); q++){
    			if (numList.indexOf(str1.charAt(q) ) == -1){
    				return false;
    			}
    		}
    		int rowLength = Integer.parseInt(str1);
    		
    	
    		String str2 = strSize.substring(sepSpot+1, strLen);
    		for (int q = 0; q<str2.length(); q++){
    			if (numList.indexOf(str2.charAt(q) ) == -1){
    				return false;
    			}
    		}
    		int layerHeight = Integer.parseInt(str2);
    		
    			
    		confirmedSize = true;
    	return true;
    }
    	
    //convert user's input string nums into map grid
    public static String[][] genGrid(String strSize){
    	int sepSpot = strSize.indexOf(" ");
    	int strLen = strSize.length();
    	
    	String str1 = strSize.substring(0, sepSpot);
    	int rowLength = Integer.parseInt(str1);
    	if (rowLength < 5){rowLength = 5;}
    	String str2 = strSize.substring(sepSpot+1, strLen);
    	int layerHeight = Integer.parseInt(str2);
    	if (layerHeight < 5){layerHeight = 5;}
    	
    	String[][] map = new String[layerHeight+2][]; //+2 b/c top/bottom walls
    	for (int q = 0; q < layerHeight+2; q++){
    		String[] curRow = new String[rowLength+2]; //make subarrays "x" long
    		map[q]=curRow;
    	}
    	return map;
    }
    
    //convert a blank 2d plane into an empty dungeon room
    public static String[][] genMap(String[][] map){  	
    	for (int y = 0; y <= map.length-1; y++){ //for each row
    		for (int x = 0; x < map[0].length; x++){ //for each coord per row
    			if ( y==0 || y==(map.length-1)){
    				map[y][x] = "-"; //create top/bottom walls
    			}
    			else if (x==0 || x==map[0].length-1){
    				map[y][x] = "|";
    			}
    			else{
    				map[y][x] = ".";
    			}
    		}
    	}
    	return map;
    }
    
    //create key gameplay points (spawn, exit, fuel for lamp)
    public static String[][] spawnSpecialPoints(String[][] map){
    	playerCoord = genRandCoord(map);
    	playerCoord[2] = "P";
    	
    	String[] exitCoord = genRandCoord(map);
    	exitCoord = checkOverlap(playerCoord, exitCoord, map);
    	exitCoord[2] = "X";
    	
    	String[] fuelCoord = genRandCoord(map);
    	fuelCoord = checkOverlap(playerCoord, fuelCoord, map);
    	fuelCoord = checkOverlap(exitCoord, fuelCoord, map);
    	fuelCoord[2] = "F";
    	
    	String[][] keyCoords = {playerCoord, exitCoord, fuelCoord};
    	return keyCoords;
    }
    
    //fills a blank dungeon room with interactable core game features
    public static String[][] fillMap(String[][] map, String[][] coords){
    	for (int q=0; q<coords.length; q++){
    		int x = Integer.parseInt(coords[q][0]);
    		int y = Integer.parseInt(coords[q][1]);
    		map[y][x] = coords[q][2];
    	}
    	return map;
    }
    
    //ensures a new arr doesn't overlap with another ("held") arr
    public static String[] checkOverlap(String[] heldArr, String[] newArr,
     String[][] map){   
    	boolean coordsOverlap = (heldArr[0] == newArr[0] && 
    	 heldArr[1] == newArr[1]);
    	
    	while (coordsOverlap){
    		newArr = genRandCoord(map);
    		coordsOverlap = (heldArr[0] == newArr[0] && 
    		 heldArr[1] == newArr[1]);
    	}
    	return newArr;
    }
    
    //randomly generates a (x,y) coordinate
    public static String[] genRandCoord(String[][] map){
    	int xRand = rand.nextInt(map[0].length-2)+1; //weird #'s bc cant spawn
    	int yRand = rand.nextInt(map.length-2)+1; //in either L/R wall
    	String[] randCoord = new String[3]; //3rd spot for map sprite
    	randCoord[0]=Integer.toString(xRand);
    	randCoord[1]=Integer.toString(yRand);
    	return randCoord;
    }
    
    public static void printOutput(String str, String file){
    	try{
    		PrintWriter typer = new PrintWriter(new FileWriter("lastMap.txt", true));
    		typer.print(str);
    		typer.close();
    	}
    	catch(Exception e) {
    		e.getStackTrace();
    	}
    }
    
    public static void printMap(String[][] map){
    	for (int y = 0; y < map.length; y++){
    		System.out.println();
    		for (int x = 0; x < map[0].length; x++){
    			System.out.print(map[y][x]);
    		}
    	}
    }
    
    public static void clearOutput(String file){
    	try{
    		PrintWriter eraser = new PrintWriter(file);
    		eraser.close();
    	}
    	catch(Exception e) {
    		e.getStackTrace();
    	}
    }  		
    		
    public static void saveMap(String[][] map, String file){
    	clearOutput(file);
    	for (int y = 0; y < map.length; y++){
    		for (int x = 0; x < map[0].length; x++){
    			printOutput(map[y][x], file);
    		}
    		printOutput(" \n", file);
    	}
    }
    	
    public static void updateVisibility(){
    	visibility = fuel/5;
    	if (visibility > 3){
    		visibility = 3;
    	}
    	else if (visibility < 1){
    		visibility = 1;
    	}
    }
    
    public static void playGame(String[][] baseMap){
    	boolean playing = true;
    	fuelConstant = baseMap.length + baseMap[0].length - 4;
    	fuel += fuelConstant;
    	updateVisibility();
    	//System.out.println("visibility ="+visibility);
    	giveIntro();
    	renderFog(baseMap);
    	
    	while (playing == true){
    		if(fuel>0){
    				fuel -= fuelRoC;
    				updateVisibility();
    				System.out.println("\nFuel remaining: "+fuel);
    				System.out.println("Current floor: "+floorCount);
    				String playerMove = getPlayerMove();
    				if (floorCount != 14){
    					baseMap = updateMap(baseMap, playerMove);
    					renderFog(baseMap);
    					saveMap(baseMap, "lastMap.txt");
    				}
    				else if (floorCount == 14){
    					System.out.println("\nCongratulations - you've made "+
    					 "it out! You win!");
    					playing = false;
    				}
    		}
    		else if(fuel<=0){
    			System.out.println("\nDarkness envelops you as the dim light in"+
    			 " your lantern finally fades out. Your story ends here - "+
    			 "GAME OVER.");
    			playing = false;
    		}
    	}
    }
    
    public static boolean withinRange(int y, int x){
    	int yPlayer = Integer.parseInt(playerCoord[1]);
    	int xPlayer = Integer.parseInt(playerCoord[0]);
    	int yDelta = Math.abs(y-yPlayer); int xDelta = Math.abs(x-xPlayer);
    	
    	if (yDelta <= visibility){
    		if (xDelta <= visibility){
    			return true;
    		}
    	}
    	return false;
    }
    
    public static void renderFog(String[][] baseMap){
    	String[][] playMap = new String[baseMap.length][baseMap[0].length];
    	for (int y = 0; y<baseMap.length;y++){
    		for (int x = 0; x<baseMap[0].length; x++){
    			if (!withinRange(y, x)){
    				playMap[y][x]="?";
    			}
    			else{
    				String sprite = baseMap[y][x];
    				playMap[y][x] = sprite;
    			}
    		}
    	}
    	printMap(playMap);
    }
    
    //return valid player move
    public static String getPlayerMove(){
    	System.out.print("Enter your move (WASD): ");
    	String playerMove = console.nextLine();
    	while ( ("WASD".indexOf(playerMove.toUpperCase()) )==-1 ||
    			  playerMove.equals("")){
    		System.out.println("Only WASD can be used to move.");
    		playerMove = console.nextLine();
    	}
    	return playerMove.toUpperCase();
    }
    
    //update player's location with new, valid move
    public static void movePlayer(String playerMove, String[][] map){
    	int newPos;
    	if (playerMove.equals("W")){
    		newPos = Integer.parseInt(playerCoord[1])-1;
    		if(isSpaceOpen(newPos,"y", map)){
    			playerCoord[1] = Integer.toString(newPos);
    		}
    	}
    	else if (playerMove.equals("S")){
    		newPos = Integer.parseInt(playerCoord[1])+1;
    		if(isSpaceOpen(newPos,"y", map)){
    			playerCoord[1] = Integer.toString(newPos);
    		}
    	}
    	else if (playerMove.equals("D")){
    		newPos = Integer.parseInt(playerCoord[0])+1;
    		if(isSpaceOpen(newPos,"x", map)){
    			playerCoord[0] = Integer.toString(newPos);
    		}
    	}
    	else if (playerMove.equals("A")){
    		newPos = Integer.parseInt(playerCoord[0])-1;
    		if(isSpaceOpen(newPos,"x", map)){
    			playerCoord[0] = Integer.toString(newPos);
    		}
    	}
    }

    public static boolean isSpaceOpen(int newPos, String direction,
    								  String[][] map){
    	int y = Integer.parseInt(playerCoord[1]);
    	int x = Integer.parseInt(playerCoord[0]);
    	if (direction == "y"){
    		if (map[newPos][x] == "-"){
    			System.out.println("You bump into a wall, doing nothing but burning precious fuel.");
    			return false;
    		}
    	}
    	else if (direction == "x"){
    		if (map[y][newPos] == "|"){
    			System.out.println("You bump into a wall, doing nothing but burning precious fuel.");
    			return false;
    		}
    	}
    	return true;
    }
    
	//updates the clear overall map
    public static String[][] updateMap(String[][] map, String playerMove){
    	map[Integer.parseInt(playerCoord[1])]
    	 [Integer.parseInt(playerCoord[0])] = ".";
    	movePlayer(playerMove, map);
    	int y = Integer.parseInt(playerCoord[1]);
    	int x = Integer.parseInt(playerCoord[0]);
    	
    	if (map[y][x] == "F"){
 			int xtraFuel = (fuelConstant/2+rand.nextInt(5)-2);
    		System.out.println("You find "+xtraFuel+" extra fuel for your lantern!");
    		fuel+=fuelConstant;
    	}
    			
    	if (map[y][x] == "X"){
    		System.out.print("\nFloor #"+floorCount+" completed - "+
    		 (13-floorCount)+" floors left!");
    		floorCount++;
    		map = getMap();
    	}
    	else{
    		map[y][x] = "P";
    	}
    	return map;
    }   
    
    public static String[][] mapSelector(){
    	System.out.println("\nWelcome to Lux - a low-res roguelike "+
    		"built on the latest cutting-edge technology... from the early 2000's."+
    		"\nWould you like to: \n1) play from the last map, or \n2) play with a "+
    		"new randomly-generated map?");
    	String[][] defaultMap = new String[1][1];
    	String mapInput = console.nextLine();
    	System.out.println();
    	String numList = "12";
    	boolean validInput = false;
    	int intInput = 0;
    	
    	while (validInput == false){
    		if (mapInput.length() == 1){
    				if (numList.indexOf(mapInput.charAt(0) ) == 0 ||
    					numList.indexOf(mapInput.charAt(0) ) == 1 ){
    						validInput = true;
    						intInput = Integer.parseInt(mapInput);
    				}
    			}
    	}
    	
    	if (intInput == 1){
    		System.out.println("Please enter the input map's file location (with 2 \\'s): ");
    		String filePath = console.nextLine();
    		try{
    			String[][] map = importMap(filePath);
    		}
    		catch (Exception e){
    			System.out.println("Couldn't find the file location. Creating new map.\n");
    			String[][] map = getMap();
    			return map;
    		}
    	}
    		
    	if (intInput == 2){
    		String[][] map = getMap();
    		return map;
    	}
    	return defaultMap;
    }
    
    public static String[][] importMap(String filepath) throws FileNotFoundException{
    	File file = new File(filepath);
    	Scanner fileReader = new Scanner(file);
    	String fileLine = "";
    	while (fileReader.hasNextLine()){
    		fileLine += fileReader.nextLine()+" ";
    	}

    	String[][] map = makeImportedGrid(fileLine);
    	map = arrangeMap(fileLine, map);
    	return map;
    }
    
    public static String[][] arrangeMap(String fileLine, String[][] map){
    	int lineCount = 0; //amnt of rows (y)
    	int rowCount = 0; //length of rows
    	for (int q = 0; q<fileLine.length(); q++){ //for every char in file
    		if (fileLine.charAt(q) == (' ')){
    			for (int w = 0; w<q; w++){ //for every char until the space
    				System.out.println(fileLine.charAt(w));
    				map[lineCount][w] = String.valueOf(fileLine.charAt(w));
    			}
    			lineCount++;
    		}
    	}
    	printMap(map);
    	return map;
    }
    			
    public static String[][] makeImportedGrid(String fileLine){
    	int lineCount = 0;
    	int rowCount = 0;
    	for (int q = 0; q<fileLine.length(); q++){
    		if (fileLine.charAt(q) == (' ')){
    			lineCount++;
    			rowCount = 0; //only need length of last row; all are =
    		}
    		else{
    			rowCount++;
    		}
    	}
    	String[][] map = new String[lineCount][rowCount];
    	return map;
    }
    
    public static void main(String[] args)  {  //TODO: fix X not spawning, load map
    	String[][] map = mapSelector();
    	saveMap(map, "lastMap.txt");
    	playGame(map);
    }
}