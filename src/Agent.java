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
	
	private LinkedList<Character> moves = new LinkedList<Character>();;


	   public char get_action(char view[][]) {
		   int x=2; int y=2;
		   boolean found = false;
		   if(GoldHasBeenFound){
			   return the_way_back_home();
		   }
		   char[][] useable_map = map_scanner(view);
		   if(ExplorationState){
			   ExplorationState = this.explore_map(useable_map);
		   }
		   if (found){
			   return moves.poll();
		   }
		   if(HasTarget){
			   int i=0; int j=0; int count = 0;
			   LinkedList<Character> tmp2 = new LinkedList<Character>();
			   int[][] visit = new int[5][5];
			   for (i=0; i<5; i++){
				   for (j=0; j<5;j++){
					   visit[i][j] = 0;
				   }
			   }
			   int[] tar = targets.get(0);
			   int horizontal = tar[0];
			   int vertical = tar[1];
			   if (isLegal(view,x+1,y)&&visit[x+1][y]==0) moves.push('R');
			   if (isLegal(view,x-1,y)&&visit[x-1][y]==0) moves.push('L');
			   if (isLegal(view,x,y+1)&&visit[x][y+1]==0) moves.push('F');
			   if (isLegal(view,x,y-1)&&visit[x][y-1]==0) moves.push('B');
			   while (x!=horizontal && y!=vertical){
				   char tmp = tmp2.poll();
				   count = 0;
				   switch (tmp){
				   	   case 'L': x=x-1;
				   	   			 break;
				   	   case 'R': x=x+1;
				   	   	         break;
				   	   case 'F': y=y+1;
				   	   			 break;
				   	   case 'B': y=y-1;
				   	             break;
				   }
				   if (isLegal(view,x+1,y)&&visit[x+1][y]==0){ 
					   moves.push('R'); tmp2.push('R'); count++;   
				   }else if (isLegal(view,x-1,y)&&visit[x-1][y]==0) {
					   moves.push('L'); tmp2.push('L');count++;
				   }else if (isLegal(view,x,y+1)&&visit[x][y+1]==0) {
					   moves.push('F'); tmp2.push('F');count++;
			       }else if (isLegal(view,x,y-1)&&visit[x][y-1]==0) {
			    	   moves.push('B'); tmp2.push('B');count++;
			       }
				   if (count == 0){
					   found=false;
					   break;
				   }
			   } 
		   }
		   
		   return take_random_step(useable_map);
	   }
	   
	   private boolean isLegal(char view[][], int x, int y){
		   if (view[x][y] == ' '){
			   return true;
		   }else{
			   if (view[x][y] == '~' && this.HasStepStone) return true;
			   else if (view[x][y] == 'T' && this.HasAxe) return true;
			   else if (view[x][y] == '-' && this.HasKey) return true;
		   }
		   return false; 
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
