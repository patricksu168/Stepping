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
private ArrayList<String> moves = new ArrayList<String>();
private ArrayList<String> TheWayBack = new ArrayList<String>();
private boolean GoldHasBeenFound = false;

   public char get_action( char view[][] ) {
	   if(GoldHasBeenFound){
		   return the_way_back_home();
	   }
	   char[][] usable_map = map_scanner(view);
	   
	   //Just for now;
	   return 'a';
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
   
   public char move_to_char_transfer(String move){
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
	   char NextMove = move_to_char_transfer(TheWayBack.get(TheWayBack.size() - 1));
	   TheWayBack.remove(TheWayBack.size() - 1);
	   return NextMove;
   }
}
