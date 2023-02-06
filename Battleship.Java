// This is a simple 2 player console based battleship game

// Imports
import java.util.Scanner;
import java.util.ArrayList;

class Battleship {

  static Scanner keyboard = new Scanner(System.in);

  // Constants
  static final int EMPTY = 0;
  static final int SHIP = 1;
  static final int MISS = 2;
  static final int HIT = 3;
  static final char LEFT = 'L';
  static final char RIGHT = 'R';
  static final char DOWN = 'D';
  static final char UP = 'U';
  static final char CHANGESHIP = 'C';
  static final int CARRIER = 5;
  static final int BATTLESHIP = 4;
  static final int CRUISER = 3;
  static final int SUBMARINE = 3;
  static final int DESTROYER = 2;
  static final String TEXT_RESET = "\u001B[0m";
  static final String TEXT_BLACK = "\u001B[30m";
  static final String TEXT_RED = "\u001B[31m";
  static final String TEXT_GREEN = "\u001B[32m";
  static final String TEXT_YELLOW = "\u001B[33m";
  static final String TEXT_BLUE = "\u001B[34m";
  static final String TEXT_PURPLE = "\u001B[35m";
  static final String TEXT_CYAN = "\u001B[36m";
  static final String TEXT_WHITE = "\u001B[37m";

  // Classes
  static class Coordinate {
    int row;
    int col;
  }

  // Creation of a blank board
  public static int[][] initializeBoard (int size) {
    int [][] board = new int[size][size];
    for (int r = 0; r < size; r++) {
        for (int c = 0; c < size; c++) {
            board[r][c] = EMPTY;
        }
    }
    return board;
  }

  // Individual drawing of each cell on the board.
  public static void drawCell(int value, String colour) {
    String c = "_";
    if (value == HIT) { // cell with the value hit
       c = "H";
       System.out.printf(colour + "| %s ", TEXT_PURPLE + c);
    } else if (value == SHIP) { // cell with the value ship
      c = "S";
      System.out.printf(colour + "| %s ", TEXT_GREEN + c);
    } else if (value == MISS) { // cell with the value miss
      c = "M";
      System.out.printf(colour + "| %s ", TEXT_WHITE + c);
    } else { //cell with the value empty
       System.out.printf(colour + "| %s ", colour + c);
    }
  }

  // Clears console
  public static void clearConsole() {
    System.out.println("\033[H\033[2J"); 
  }

  // Draws board after changes by drawCell have been made
  public static void drawBoard(int[][] board, String colour) {
    int n = 0;
    System.out.print("\n");
    System.out.print("    ");
    // Prints numbers across the top of the board
    for(int m = 0; m < board.length; m++){
      System.out.print(m + "   ");
    }
    System.out.print("\n");
    System.out.print("0 ");
    // Draws board
    for (int i = 0; i < board.length; i++) {
      n++;
      
      for (int j = 0; j < board.length; j++) {
        drawCell(board[i][j], colour);  
      }
      if (n < 7) {
        // Draws numbers on the left side of the board
        System.out.print(colour + "|\n\n" + TEXT_RESET + n + " ");
      }
    }
    System.out.print(colour + "|\n\n\n" + TEXT_RESET);
  }

  // Boards for the offence phase
  public static void drawPlayerBoards(int[][] boardD, int[][] boardO, String colourD, String colourO, String name) {
    clearConsole();
    System.out.println("Offence Phase: " + name + "\n");
    System.out.println("Defense board:\n");
    // Player defence board
    drawBoard(boardD, colourD);
    System.out.println("Offense board:\n");
    // Player offence board
    drawBoard(boardO, colourO);
  }
  
  // Checking if coordinates are in the array bounds
  public static boolean checkIfInBounds(int board[][], int row, int col) {
    if (0 <= row && row < board.length && 0 <= col && col < board.length) {
      return true;
    }
    return false;
  }

  // Check if coordinate is already of the value miss or hit
  public static boolean checkIfTakenO(int board[][], int row, int col) {
    if (board[row][col] == MISS || board[row][col] == HIT) {
      return false;
    }
    return true;
  }

  // Placing the first coordinate of a ship
  public static Coordinate placeFirstShipCoordinate(int[][] board, int ship, String colour) {
    Coordinate coordinate = new Coordinate();
    boolean coordinateWasEntered = false;
    do {
      drawBoard(board, colour);
      System.out.println(TEXT_RESET + "\nEnter coordinates for your ship of length " + ship);
      // Receive user input for coordinates
      System.out.println("\nEnter row coordinate.");
      coordinate.row = keyboard.nextInt();
      System.out.println("Enter column coordinate.");
      coordinate.col = keyboard.nextInt();
      // Check if user inputed coordinates are valid
      if (!checkIfInBounds(board, coordinate.row, coordinate.col)) {
        System.out.println("\nInvalid coordinates. Please choose again.\n");
        // Check if user input coordinate is taken
      } else if (board[coordinate.row][coordinate.col] == SHIP) {
        System.out.println("\nCoordinate is used. Please choose again.\n");
      } else {
        // Finalize the coordinate placement
        board[coordinate.row][coordinate.col] = SHIP;
        coordinateWasEntered = true;  
      }
    } while (!coordinateWasEntered);
    return coordinate;
  }

  // Move placement for offence phase
  public static int placeMove (int [][] boardO, int[][] boardD) {
    Coordinate coordinate = new Coordinate();
    do {
      System.out.println("Enter your move's row coordinate.");
      coordinate.row = keyboard.nextInt();
      System.out.println("Enter your move's column coordinate.");
      coordinate.col = keyboard.nextInt();      
      if (!checkIfInBounds(boardD, coordinate.row, coordinate.col)) { // is coordinate out of bounds
        System.out.println("\nInvalid coordinates. Please choose again.\n");
        
      } else if (!checkIfTakenO(boardD, coordinate.row, coordinate.col)) { // is coordinate taken
          System.out.println("\nCoordinate is used. Please choose again.\n");
      } else {
          if (boardD[coordinate.row][coordinate.col] == SHIP) {
            boardO[coordinate.row][coordinate.col] = HIT;
            boardD[coordinate.row][coordinate.col] = HIT;
            return HIT;
          } else {
            boardO[coordinate.row][coordinate.col] = MISS;
            boardD[coordinate.row][coordinate.col] = MISS;
            return MISS;
          }
        }
    } while (true);
  }

  // Find available placements
  public static ArrayList<Character> getValidPlacements(int[][] board, int row, int col, int ship) {
    ArrayList<Character> placements = new ArrayList<Character>();
    boolean placementAvailabilityU = true;
    boolean placementAvailabilityD = true;
    boolean placementAvailabilityR = true;
    boolean placementAvailabilityL = true;
    
    // Check for up
    if (row - ship >= -1) {
      for (int i = 1; i < ship; i++) {
        if (board[row - i][col] == SHIP) {
          placementAvailabilityU = false;
        }
      }
      if (placementAvailabilityU == true) {
        placements.add(UP);
      }
    }

    // Check for down
    if (row + ship <= board.length) {
      for (int i = 1; i < ship; i++) {
        if (board[row + i][col] == SHIP) {
          placementAvailabilityD = false;
        }
      }
      if (placementAvailabilityD == true) {
        placements.add(DOWN);
      }    
    }

    // Check for right
    if (col + ship <= board.length) {
      for (int i = 1; i < ship; i++) {
        if (board[row][col + i] == SHIP) {
          placementAvailabilityR = false;
        }
      }
      if (placementAvailabilityR == true) {
        placements.add(RIGHT);
      }
    }

    // Check for left
    if (col - ship >= -1) {
      for (int i = 1; i < ship; i++) {
       if (board[row][col - i] == SHIP) { 
         placementAvailabilityL = false;
        }
      }
      if (placementAvailabilityL == true) {
        placements.add(LEFT);
      }    
    }

    return placements;
  }

  // Fill out correct coordinate placements for rest of ship
  public static void placeRestOfShip(char userDirectionChoice, int row, int col, int [][] board, int shipLength) {
    if (userDirectionChoice == LEFT) {
      for (int i = 0; i < shipLength; i++) {        
        board[row][col - i] = SHIP;
      }
    } else if (userDirectionChoice == RIGHT) {
      for (int i = 0; i < shipLength; i++) {        
        board[row][col + i] = SHIP;
      }
    } else if (userDirectionChoice == DOWN) {
      for (int i = 0; i < shipLength; i++) {
        board[row + i][col] = SHIP;
      }
    } else if (userDirectionChoice == UP) {
      for (int i = 0; i < shipLength; i++) {
        board[row - i][col] = SHIP;
      }
    }
  }
  
  // Combining the first coordinate placement with the rest of the ship placement
  public static void placeShip(int board[][], int ship, String colour) {
    boolean shipWasPlaced = false;
    do {
      Coordinate coordinate = placeFirstShipCoordinate(board, ship, colour);
      drawBoard(board, colour);
      ArrayList<Character> validPlacements = getValidPlacements(board, coordinate.row, coordinate.col, ship);
      System.out.println("Please choose one of the avaliable directional placements:" + validPlacements + ". Press any other key to replace the ship.");
      char placement = keyboard.next().toUpperCase().charAt(0);
      if (validPlacements.contains(placement)) {
        placeRestOfShip(placement, coordinate.row, coordinate.col, board, ship);
        shipWasPlaced = true;
      } else {
        board[coordinate.row][coordinate.col] = EMPTY;
      }
    } while (!shipWasPlaced);
  }

  // Ship placement for each type of ship
  public static void placeShips(int board[][], String player, String colour) {
    clearConsole();
    System.out.println("Ship Placement Phase: " + player);
    placeShip(board, CARRIER, colour);
    placeShip(board, BATTLESHIP, colour);
    placeShip(board, SUBMARINE, colour);
    placeShip(board, CRUISER, colour);
    placeShip(board, DESTROYER, colour);
  }

  public static void main(String[] args) {

    boolean keepPlaying = false;
    
    do {
    
      // Player1 name, hitcounter and boards
      String name1 = "player1";
      int hitCounter1 = 0;
      int [][] offensiveBoard1 = initializeBoard(7);
      int [][] defensiveBoard1 = initializeBoard(7);

      // Player2 name, hitcounter and boards
      String name2 = "player2";
      int hitCounter2 = 0;
      int [][] offensiveBoard2 = initializeBoard(7);
      int [][] defensiveBoard2 = initializeBoard(7);
      
      // Intro message and player name inputs
      System.out.print("\nWelcome to battleship. This is a two person game. Each player will have 7x7 square grid (labeled from 0 to 6) for which they can place their ships. You have five ships, one with a length of five squares (the Carrier), one with a length of 4 (the Main), two with a length of 3 (the Cruiser and the Submarine) and one with a length of 2 (the Destroyer). Your ships cannot overlap on the grid. Ship positions cannot be changed once the game starts. Each player takes turns choosing a square of the opponents grid to shoot at. They score a hit on a ship if they hit a square with a ship on it. The first player to destroy the other player's fleet wins which means to get 17 hits. Player 1 will have a blue defensive board and red offensive board and Player 2 will have a red defensive board and a blue offensive board.");
      System.out.print("\n\nPlayer 1 enter your name. ");
      name1 = keyboard.next();
      System.out.println("\nWelcome " + name1 + "\n\n");

      System.out.print("Player 2 enter your name. ");
      name2 = keyboard.next();
      System.out.println("\nWelcome " + name2);
      clearConsole();
    
      // Player1 ship placement
      placeShips(defensiveBoard1, name1, TEXT_BLUE);

      // Player2 ship placement
      placeShips(defensiveBoard2, name2, TEXT_RED);

      int currentPlayer = 10;
      // total number of cells used by one player's ships
      int numberOfHitsToWin = 17;
   
      // Offence phase
      while (hitCounter1 < numberOfHitsToWin && hitCounter2 < numberOfHitsToWin) {
        if (currentPlayer == 10) {
          drawPlayerBoards(defensiveBoard1, offensiveBoard1, TEXT_BLUE, TEXT_RED, name1);
          if (placeMove(offensiveBoard1, defensiveBoard2) == HIT) {
            hitCounter1++;
          }
          currentPlayer = 11;
        } else {
          drawPlayerBoards(defensiveBoard2, offensiveBoard2, TEXT_RED, TEXT_BLUE, name2);
          if (placeMove(offensiveBoard2, defensiveBoard1) == HIT) {
          hitCounter2++;
          }
          currentPlayer = 10;
        }
      }
      clearConsole();
      
      // Victory message
      String winner = name1;
      if (hitCounter2 == numberOfHitsToWin) {
        winner = name2;
      }
      System.out.println(winner + " won!");
      System.out.println("\nTo play again enter 1");
      int userInput = keyboard.nextInt();
      if (userInput == 1) {
        keepPlaying = true;
      } else {
        keepPlaying = false;
      }    
    } while (keepPlaying == true);
    
    keyboard.close();
  }
}
