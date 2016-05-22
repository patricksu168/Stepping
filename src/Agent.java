/*********************************************
 *  Agent.java
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2016
*/

import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {

private boolean HasKey = false;
private boolean HasAxe = false;
private boolean HasStepStone = false;
private ArrayList<String> TheWayBack = new ArrayList<String>();
private ArrayList<int[]> targets = new ArrayList<int[]>();
private boolean HasTarget = false;
private boolean GoldHasBeenFound = false;
private boolean ExplorationState = true;
private int     TranslateStep = 0;

   public char get_action(char view[][]) {
	   if(GoldHasBeenFound){
		   return the_way_back_home();
	   }
	   char[][] useable_map = map_scanner(view);
	   if(ExplorationState){
		   explore_map(useable_map);
	   }
	   if(HasTarget){
		   int[] tar = targets.get(0);
		   int horizontal = tar[0];
		   int vertical = tar[1];
		   if(vertical == 0 && horizontal == 0){
			   targets.remove(0);
			   if(targets.get(0) != null){
				   tar = targets.get(0);
				   horizontal = tar[0];
				   vertical = tar[1];
			   }
		   }
		   if(horizontal != 0 ){
			   if(horizontal > 0){
				   if(legal_step("right", useable_map)) return take_action("right");
			   }else{
				   if(legal_step("left", useable_map)) return take_action("left");
			   }
		   }
		   else if(vertical != 0){
			   if(vertical > 0){
				   if(legal_step("up", useable_map)) return take_action("up");
			   }else{
				   if(legal_step("back", useable_map)) return take_action("back");
			   }
		   }
	   }
	   if(targets.get(0) == null){
		   HasTarget = false;
		   ExplorationState = true;
	   }
	   return take_random_step(useable_map);
   }
   

   void print_view( char view[][] )
   {
      int i,j;

      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }

   public static void main( String[] args )
   {
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      char   action   = 'F';
      int port;
      int ch;
      int i,j;
      
      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }

      try { // scan 5-by-5 wintow around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  }
               }
            }
            agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            action = agent.get_action( view );
            out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
   
   public boolean ifReachable(char block){
	   if(block == '-'){
		   if(HasKey){
			   return true;
		   }else{
			   return false;
		   }
	   }else if(block == 'T'){
		   if(HasAxe){
			   return true;
		   }else{
			   return false;
		   }
	   }
	   return true;
   }
   
   public char[][] map_scanner(char view[][]){
	   char[][] map = new char[5][5];
	   int x_coordinate = 0;;
	   int y_coordinate = 0;
	   while(y_coordinate < 5){
		   while(x_coordinate < 5){
			   char ref = view[x_coordinate][y_coordinate];
			   boolean block = ifReachable(ref);
			   char point;
			   if(block){
				   point = ' ';
			   }else{
				   point = 'x';
			   }
			   //Check if the block has an item upon it;
			   if(ref == 'g' || ref == 'O' || ref == 'a' || ref == 'k' || ref == '~'){
				   point = ref;
			   }
			   map[x_coordinate][y_coordinate] = point;
			   x_coordinate ++;;
		   }
		   y_coordinate ++;
	   }
	   return map;
   }
   
   public boolean explore_map(char view[][]){
	   int x = 0, y = 0;
	   boolean ifFoundSomething = false;
	   while(y < 5){
		   while(x < 5){
			   char ref = view[x][y];
			   if(ref == 'g' || ref == 'a' || ref == 'O' || ref == 'k'){
				   ExplorationState = false;
				   ifFoundSomething = true;
				   HasTarget = true;
			   }
			   int[] item = {x - 2, 2 - y};
			   targets.add(item); 
			   x ++;
		   }
		   y ++;
	   } 
	   return ifFoundSomething;
   }
   
   public boolean legal_step(String step, char view[][]){
	   //TODO
	   return true;
   }
   
   public char take_action(String move){
	   //TODO
	   return 'a';   
   }
   
   public char take_random_step(char view[][]){
	   //TODO
	   return 'a';
   }
   
   public char move_to_action_transfer(String move){
	   if(move.equals("up")){
		   return 'f';
	   }
	   if(move.equals("right")){
		   return 'r';
	   }else{
		   return 'l';
	   }
   }
   
   public char the_way_back_home(){
	   char NextMove = move_to_action_transfer(TheWayBack.get(TheWayBack.size() - 1));
	   TheWayBack.remove(TheWayBack.size() - 1);
	   return NextMove;
   }
}
