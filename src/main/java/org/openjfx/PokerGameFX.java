package org.openjfx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
import java.util.Arrays;
import java.util.Random;

/*
    Sinead Cahill

    This Class 'PokerGameFx' is the driver class for a poker game. In this class the GUI is created and user actions are set.
    There are two instances of classes created and called here; CardDeck and HandRanking.

    Methods:
        + Start(Stage primaryStage) -> Creates GUI elements and sets the button actions, and then places them all in the stage
        - hideCards(HBox user) -> creates face down cards
        - showCards(String[] cardHand, HBox user) -> creates card hands as visible card buttons
        - isCardDisabled(boolean type, HBox user) -> enables or disables card buttons
        - openRound(String winningHand) -> check either player can open round and display matching text
        - isRoundOpen(String[] cardHand) -> returns true or false if either player has qualifying hand to open round
        - cardSelected(Button cardPicked) -> adds selected card to temp array
        - changeCards(String[] cardHand, HBox user) -> changes cards in both players hand
        - placeBet(TextField playerBet) -> checks players bet is valid and adds to pot
        - updateTokenPot(VBox tokenVBox) -> updates the pot, dealer and player tokens when required
        - getWinner(String[] player, String[] dealer, HBox newWinner) -> displays the winning hand
 */

public class PokerGameFX extends Application {
    //Creating instances of CardDeck and HandRanking Classes
    private CardDeck deck;
    private HandRanking hand;

    private String[] playersHand, dealersHand, tempSelectedCards;
    private int cardWidth, cardHeight, noOfSelectedCards, playerTokens, dealerTokens, potTokens;

    private HBox playersHBox, dealersHBox;
    private VBox startGameNotice, openRoundNotice, shuffleDeckNotice, betNotice, winnerNotice;

/*
    The below constructor contains all initialised global variables needed in this class
*/
    public PokerGameFX(){
        //Initialising Classes
        deck = new CardDeck();
        hand = new HandRanking();

        //Initialising Variable & Panes
        playersHBox = new HBox(5);
        dealersHBox = new HBox(5);

        cardWidth = 140; //used in hideCards & showCards functions
        cardHeight = 180; //used in hideCards & showCards functions
        noOfSelectedCards = 0; //used in cardSelected and changeCards functions
        playerTokens = 10; //used throughout class
        dealerTokens = 10; //used throughout class
        potTokens = 0; //used throughout class

        //This is set to length 4 so that the user cannot change more than 4 of their cards -> used in methods cardSelected and changeCard
        tempSelectedCards = new String[4];
    }

/*
    This is the start function that sets the stage, scene and nodes. This is where the GUI is created and actions set. All other methods in this class are called here
 */
    public void start(Stage primaryStage) {
        StackPane allElements = new StackPane();

        deck.newDeck(); //creating a new deck
        //dealing out 2 hands (player and dealer)
        playersHand = deck.getCardHand();
        //System.out.println("player: " + Arrays.toString(playersHand));
        dealersHand = deck.getCardHand();
        //System.out.println("dealer: " + Arrays.toString(dealersHand));


    //The following code contains all Elements to the Left of the Table (Green Area)
        //Creating Left Background
        Rectangle tableLeftbg = new Rectangle(0,0,770,700);
        tableLeftbg.setFill(Color.GREEN);
        tableLeftbg.setArcHeight(20);
        tableLeftbg.setArcWidth(20);

        dealersHBox.setAlignment(Pos.CENTER);
        hideCards(dealersHBox); //The dealers cards are hidden from the player

        playersHBox.setAlignment(Pos.CENTER);
        showCards(playersHand, playersHBox); //The player is shown their cards

        //The Message Notice background -> Displays messages to the user throughout the game
        Rectangle messageBox = new Rectangle(400, 100);
        messageBox.setFill(Color.WHITE);
        messageBox.setStroke(Color.BLACK);
        messageBox.setArcHeight(10);
        messageBox.setArcWidth(10);


        //Message Notice Elements to Start New Game
        Text newGameText = new Text("Start New Game");
        Button startBtn = new Button("Start Game");

        startGameNotice = new VBox(10);
        startGameNotice.setAlignment(Pos.CENTER);
        startGameNotice.getChildren().addAll(newGameText, startBtn);
        startGameNotice.setVisible(false);  //Visibility is set to false until required


        //Message Notice Elements to Reshuffle Deck -> if neither player or dealer have qualifying hand this is displayed
        Button shuffleDeckBtn = new Button("Reshuffle");
        Text newHand = new Text("Get New Hand");

        shuffleDeckNotice = new VBox(10);
        shuffleDeckNotice.setAlignment(Pos.CENTER);
        shuffleDeckNotice.getChildren().addAll(newHand, shuffleDeckBtn);
        shuffleDeckNotice.setVisible(false);  //Visibility is set to false until required


        //Message Notice Elements to Open/Play Round
        Button openRoundBtn = new Button("Open");
        Button foldBtn = new Button("Fold");
        Button playRoundBtn = new Button("Play");

        HBox roundBtns = new HBox(5);
        roundBtns.setAlignment(Pos.CENTER);
        roundBtns.getChildren().addAll(openRoundBtn, foldBtn);

        openRoundNotice = new VBox(10);
        openRoundNotice.setAlignment(Pos.CENTER);
        String bestHand = hand.compareHands(playersHand, dealersHand); //returns the winner of the 2 hands at first look
        openRound(bestHand); //This function checks if either player can open a round & if they have enough tokens

        if(bestHand.equalsIgnoreCase("Dealer")){ //if the dealer has the best hand(and can open) the "Open" button is replaced with the "Play" button
            roundBtns.getChildren().remove(0);
            roundBtns.getChildren().add(0, playRoundBtn);
        }

        openRoundNotice.getChildren().add(roundBtns);


        //Message Notice Elements so Player can Change Cards
        Text changeCardsText = new Text("Select the Cards you want to Change");
        changeCardsText.setTextAlignment(TextAlignment.CENTER);
        Button doneBtn = new Button(("Done"));

        VBox changeCardNotice = new VBox(10);
        changeCardNotice.setAlignment(Pos.CENTER);
        changeCardNotice.getChildren().addAll(changeCardsText, doneBtn);
        changeCardNotice.setVisible(false); //Visibility is set to false until required


        //Message Notice Elements so the Player can Place a Bet
        Text enterBet = new Text("Place a Bet (0-3 tokens)");
        enterBet.setTextAlignment(TextAlignment.CENTER);

        TextField betValue = new TextField(); //Allows user to input a bet value
        Button betBtn = new Button("Place Bet");

        HBox betAmount = new HBox(5);
        betAmount.setAlignment(Pos.CENTER);
        betAmount.getChildren().addAll(enterBet, betValue);

        betNotice = new VBox(5);
        betNotice.setAlignment(Pos.CENTER);
        betNotice.getChildren().addAll(betAmount, betBtn);
        betNotice.setVisible(false); //Visibility is set to false until required


        //Message Notice Elements to reveal the Winner of the round
        HBox winnerHBox = new HBox(5);
        getWinner(playersHand, dealersHand, winnerHBox); //both hands are passed in to the method and updates the winnerHBox with the winning player
        Button nextRoundBtn = new Button("Next Round");

        winnerNotice = new VBox(10);
        winnerNotice.setAlignment(Pos.CENTER);
        winnerNotice.getChildren().addAll(winnerHBox, nextRoundBtn);
        winnerNotice.setVisible(false); //Visibility is set to false until required


        //This code places all the message notice elements over the Message Box Shape
        StackPane messageNotices = new StackPane();
        messageNotices.setAlignment(Pos.CENTER);
        messageNotices.getChildren().addAll(messageBox, openRoundNotice, shuffleDeckNotice, changeCardNotice, betNotice, winnerNotice, startGameNotice);

        //Adding all Elements to the Left Container (card hands and message notices)
        VBox tableLeftContainer = new VBox(50);
        tableLeftContainer.setAlignment(Pos.CENTER);
        tableLeftContainer.getChildren().addAll(dealersHBox, messageNotices, playersHBox);


    //The following code contains all Elements to the Right of the Table (White Area)
        Rectangle tableRightbg = new Rectangle(800,0,225,700);
        tableRightbg.setFill(Color.WHITE);
        tableRightbg.setArcHeight(20);
        tableRightbg.setArcWidth(20);

        VBox updateTokens = new VBox(50);
        updateTokenPot(updateTokens);  //the player, dealer and pot token values are updated when the method is called

        Button quitGameBtn = new Button("Quit Game");

        BorderPane tableRightContainer = new BorderPane();
        BorderPane.setAlignment(quitGameBtn, Pos.BOTTOM_RIGHT);
        tableRightContainer.setStyle("-fx-padding: 0 10 20 10;");
        tableRightContainer.setBottom(quitGameBtn);
        tableRightContainer.setCenter(updateTokens);


    //Button Actions are set here
        //Open Round: Removes 1 token from each player and adds to pot. The open round notice is hidden and the change cards message appears. The cards are now clickable
        openRoundBtn.setOnAction(e -> { playerTokens--; dealerTokens--; potTokens += 2; updateTokenPot(updateTokens); changeCardNotice.setVisible(true); openRoundNotice.setVisible(false);
                                        isCardDisabled(false, playersHBox);});

        //Play Round: Removes 1 token from the player since dealer opened. The open round notice is hidden and the change cards message appears. The cards are now clickable
        playRoundBtn.setOnAction(e -> { playerTokens--; potTokens++; updateTokenPot(updateTokens); changeCardNotice.setVisible(true); openRoundNotice.setVisible(false); isCardDisabled(false, playersHBox);});

        //Fold: The open round notice is hidden and the player can reshuffle the deck. Until the player chooses to reshuffle their cards are hidden. If the dealer opened that round his token is returned
        foldBtn.setOnAction(e -> { openRoundNotice.setVisible(false); shuffleDeckNotice.setVisible(true); hideCards(playersHBox);
                                    if(bestHand.equalsIgnoreCase("Dealer") && isRoundOpen(dealersHand)==true){
                                        dealerTokens++;
                                        potTokens--;
                                        updateTokenPot(updateTokens);} });

        //Shuffle Deck: The game begins again (a new round)
        shuffleDeckBtn.setOnAction(e -> start(primaryStage));

        //Done: The player and dealers hands are updated with new cards. The change card notice is hidden and the bet notice appears. The cards are now disabled and winning hand is gotten
        doneBtn.setOnAction(e -> { changeCardNotice.setVisible(false); changeCards(playersHand, playersHBox); changeCards(dealersHand, dealersHBox); isCardDisabled(true, playersHBox);
                                   betNotice.setVisible(true); getWinner(playersHand, dealersHand, winnerHBox);});

        //Bet: The bet notice is hidden the place bet method is called. The dealer, player and pot tokens are updated according to the bet made
        betBtn.setOnAction(e -> { betNotice.setVisible(false); placeBet(betValue); updateTokenPot(updateTokens); });

        //Next Round: the game begins again at the top of the start method. The winner notice is hidden and the selected cards are reset to 0
        nextRoundBtn.setOnAction(e -> { winnerNotice.setVisible(false); noOfSelectedCards = 0; start(primaryStage);});

        //Quit Game: Both players cards are hidden and the start game notice is visible (all other notices are hidden and all variable values are reset)
        quitGameBtn.setOnAction(e -> { hideCards(playersHBox); hideCards(dealersHBox); startGameNotice.setVisible(true); shuffleDeckNotice.setVisible(false); betNotice.setVisible(false);
                                       changeCardNotice.setVisible(false); openRoundNotice.setVisible(false); winnerNotice.setVisible(false); noOfSelectedCards = 0;});

        //Start: Starts the game again, from the beginning
        startBtn.setOnAction(e -> {playerTokens = 10; dealerTokens = 10; potTokens = 0; start(primaryStage);});



    //Here All Table Elements are combined (Left and Right)
        //Black Background
        Rectangle background = new Rectangle(0,0,1000,700);
        background.setFill(Color.BLACK);

        HBox tableLayout = new HBox(5);
        tableLayout.getChildren().addAll(background, new StackPane(tableLeftbg, tableLeftContainer), new StackPane(tableRightbg, tableRightContainer));

        //Adding Table and Black Background together
        allElements.getChildren().addAll(background, tableLayout);

        // Creating the scene and place it in the stage
        Scene scene = new Scene(allElements,1000, 700);
        primaryStage.setTitle("Poker Game"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
        primaryStage.setResizable(false); //User can't resize
    }


/*
    This function creates the face down cards
    It is used when the user ends/quits a game, and is used to hide the dealers cards until the end of a round
*/
    private void hideCards(HBox user) {
        user.getChildren().clear();

        Rectangle cardHidden;

        //5 rectangles are created and added to the player or dealer HBox
        for(int i=0; i<5; i++){
            cardHidden = new Rectangle(cardWidth, cardHeight);
            cardHidden.setArcWidth(20);
            cardHidden.setArcHeight(20);
            cardHidden.setFill(Color.BLACK);
            Text text = new Text("?");
            text.setFill(Color.WHITE);
            text.setStyle("-fx-font: 70 Serif");

            user.getChildren().add(new StackPane(cardHidden, text));
        }
    }

/*
    This function displays the players cards at the beginning of each round and displays the dealers cards at the end of each round.
    The players cards are created as buttons so they can be changed
*/
    private void showCards(String[] cardHand, HBox user){
        user.getChildren().clear();

        for(int i=0; i<cardHand.length; i++){
            Button card = new Button(cardHand[i]);
            card.setPrefHeight(cardHeight);
            card.setPrefWidth(cardWidth);
            card.setStyle("-fx-opacity: 1; -fx-background-color: white; -fx-background-radius: 10; -fx-border-width: 1 1 1 1; -fx-border-radius: 10; -fx-font: 14 Serif");
            card.setTextFill(Color.BLACK);

            //each card if selected calls the cardSelected method
            card.setOnAction(e -> cardSelected(card));

            //each card is disabled until needed
            card.setDisable(true);
            user.getChildren().add(card);
        }
     }

/*
    This function enables or disables the card buttons in either the player or dealer HBox (at this time just playerHBox)
*/
     private void isCardDisabled(Boolean type, HBox user){
         //Go through each child of the HBox and set disable to true or false (they are all buttons)
         for (Node button : user.getChildren()){
             button.setDisable(type);
         }

     }

 /*
    This function checks that if the dealer or player can open the round successfully. Depending on the the player with the better qualifying hand, different actions are completed
  */
     private void openRound(String winningHand){
         Text playRound = new Text("");

         //checks both players have enough tokens to continue
         if(dealerTokens > 0 && playerTokens > 0) {

             //If player has higher hand or both hands draw, the player chooses if they want to open the round
             if (winningHand.equalsIgnoreCase("Player") || winningHand.equalsIgnoreCase("Draw")) {
                 //if the player has a qualifying hand they chose to open or fold
                 if (isRoundOpen(playersHand)) {
                     playRound = new Text("Do you want to Open Round?");
                 } else {
                     //if the player does not have a qualifying hand they can reshuffle the deck
                     openRoundNotice.setVisible(false);
                     shuffleDeckNotice.setVisible(true);
                 }
             } else if (winningHand.equalsIgnoreCase("Dealer")) {
                 //if the dealer has a qualifying hand it opens the round and puts 1 token in the pot.
                 if (isRoundOpen(dealersHand)) {
                     //Add 1 token from dealers tokens to the pot
                     dealerTokens--;
                     potTokens++;

                     //Player is asked if they want to Play the round or Fold
                     playRound = new Text("Dealer Opened the Round\nDo you want to Play?");
                     playRound.setTextAlignment(TextAlignment.CENTER);

                 } else {
                     //if the dealer does not have a qualifying hand the player must reshuffle the deck
                     openRoundNotice.setVisible(false);
                     shuffleDeckNotice.setVisible(true);
                 }
             } else {
                 //if neither hand is the winning hand, the player must reshuffle the deck
                 openRoundNotice.setVisible(false);
                 shuffleDeckNotice.setVisible(true);

             }
         }else{
             //if the dealer or player has no tokens left, the game ends
             Text winner;
             if(dealerTokens == 0) {
                 //JOptionPane.showMessageDialog(null, "YOU WIN THE POKER GAME!\nDealer has no more Tokens to Bet");
                 winner = new Text("PLAYER WINS!");
             }else{
                 //JOptionPane.showMessageDialog(null, "YOU LOSE THE GAME!\nYou have no more Tokens to Bet");
                 winner = new Text("DEALER WINS!");
             }
             //player can choose to start another game
             openRoundNotice.setVisible(false);
             startGameNotice.setVisible(true);
             startGameNotice.getChildren().remove(0);
             startGameNotice.getChildren().add(0, winner);
             hideCards(playersHBox);
         }

         //Adds corresponding text and buttons to the player with the better qualifying hand
         openRoundNotice.getChildren().add(0, playRound);
     }

/*
  This method returns a boolean true or false if the passed in card hand can open a round
*/
    private boolean isRoundOpen(String[] cardHand) {
        Boolean validOpenRound;
        int pairValue;
        //get the card hand ranking from the handRanking class
        int usersCards = hand.getRanking(cardHand);

        //if the hand consists of more than 1 pair, the player or dealer can open the round
        if (usersCards > 1) {
            validOpenRound = true;
        } else if (usersCards == 0) { //if the hand has no matches open round is false
            validOpenRound = false;
        } else {
            //if the hand has 1 pair we need to check is it a pair of jacks or higher
            pairValue = hand.returnIndex(hand.pairTriValue(cardHand));
            //Jack = 11, Ace = 1
            if (pairValue >= 11 || pairValue == 1) {
                validOpenRound = true;
            } else {
                validOpenRound = false;
            }
        }

        return validOpenRound;
    }

/*
    This function styles the card button that is selected by the user, to indicate it has been selected.
    Each selected card is then added to a list so that it can be changed when the player is finished selecting
 */
    private void cardSelected(Button cardPicked){
        String cardToString = cardPicked.toString();
        //Remove unnecessary text in string so that only the card value/face is left
        String cardName = cardToString.substring(cardToString.indexOf("'")+1,cardToString.lastIndexOf("'"));
        boolean alreadySelected = false;

        try {
            if(noOfSelectedCards >= 1) { // if it is not the first card selected
                for(int i = 0; i < tempSelectedCards.length; i++) { //temp array of size 4
                    //check if the selected card is already in the temp array -> if it is revert the selection(remove 1 from noOfSelectedCards and change card style)
                    if (cardName.equalsIgnoreCase(tempSelectedCards[i])) {
                        alreadySelected = true;
                        noOfSelectedCards--;
                        cardPicked.setStyle("-fx-opacity: 1; -fx-background-color: white; -fx-background-radius: 10; -fx-border-width: 1 1 1 1; -fx-border-radius: 10; -fx-font: 14 Serif");

                        //remove the card from the list
                        for(int j=i; j< tempSelectedCards.length; j++) {
                            if(j == 3){ //if it is the last card change to null
                                tempSelectedCards[j] = "null";
                            }else {
                                tempSelectedCards[j] = tempSelectedCards[j + 1];
                            }
                        }
                    }
                }
            }
            //if the card selected is not in the list, add it to the list and change the style
            if(alreadySelected == false){
                tempSelectedCards[noOfSelectedCards] = cardName;
                noOfSelectedCards++;
                cardPicked.setStyle("-fx-opacity: 1; -fx-background-color: grey; -fx-background-radius: 10; -fx-border-width: 1 1 1 1; -fx-border-radius: 10; -fx-font: 14 Serif");
            }
        }
        catch (ArrayIndexOutOfBoundsException anError) { //if the user selects more than 4 cards to change
            JOptionPane.showMessageDialog(null,  "Only 4 cards can be changed");
        }
    }

/*
    This function changes the cards that the user has selected to change. The cards are sent in to the changeCard method in the class CardDeck
 */
    private void changeCards(String[] cardHand, HBox user){
        //create an array the same length as the number of selected cards
        String[] cardsToChange = new String[noOfSelectedCards];

        //if the dealer has 1 pair  or lower
        if(user.equals(dealersHBox) && hand.getRanking(dealersHand) < 2) {
            //If dealer does not have the winning hand, change cards (change same number as player)
            String winningHand = hand.compareHands(playersHand, dealersHand);

            if(!winningHand.equalsIgnoreCase("dealer")) {
                for (int i = 0; i < noOfSelectedCards; i++) {
                    cardsToChange[i] = dealersHand[i];
                }
                //the card hand is updated
                cardHand = deck.changeCard(cardHand, cardsToChange);
            }
        } else if (user.equals(playersHBox)) {
            for (int i = 0; i < cardsToChange.length; i++) {
                //Selected cards are added to the cardsToChange array
                cardsToChange[i] = tempSelectedCards[i];
            }
            //card hand is updated
            cardHand = deck.changeCard(cardHand, cardsToChange);
            //the updated hand is shown to the player
            showCards(cardHand, user);
        }
    }

/*
    This function checks the users bet amount. It checks that it is a number between 0-3 and that the user has enough tokens to bet with
*/
    private void placeBet(TextField playerBet){
        try{
            //if the user does not enter a value or the value is greater than 3 tokens
            if(playerBet.getText().isEmpty() || Integer.parseInt(playerBet.getText()) > 3){
                JOptionPane.showMessageDialog(null, "Please enter value between 0-3");
                betNotice.setVisible(true);
                playerBet.clear();

                //if the player has less tokens than what they are betting
            }else if(playerTokens < Integer.parseInt(playerBet.getText())){
                JOptionPane.showMessageDialog(null, "Not Enough Tokens to Bet \nYour Tokens: " + playerTokens);
                betNotice.setVisible(true);
                playerBet.clear();
            }else{
                //if correct, remove the number of tokens bet from the players tokens and add it to the pot
                playerTokens = playerTokens - Integer.parseInt(playerBet.getText());
                potTokens += Integer.parseInt(playerBet.getText());

                //if the player bets 0 tokens or the dealer hand has more than 1 pair or the dealer has the winning hand don't fold
                if(Integer.parseInt(playerBet.getText()) == 0 ||hand.getRanking(dealersHand) > 1 || hand.compareHands(playersHand, dealersHand).equalsIgnoreCase("Dealer")){
                    //if the dealer has enough tokens remove them and add them to the pot
                    if(dealerTokens >= Integer.parseInt(playerBet.getText())) {
                        dealerTokens = dealerTokens - Integer.parseInt(playerBet.getText());
                        potTokens += Integer.parseInt(playerBet.getText());

                    //if the dealer has less tokens than the player has bet, they go all in
                    }else if (dealerTokens < Integer.parseInt(playerBet.getText())){
                        Text allIn = new Text("Dealer went All In");
                        winnerNotice.getChildren().add(0, allIn);
                        potTokens += dealerTokens;
                        dealerTokens = 0;
                    }

                    //The player is then shown the dealers cards
                    showCards(dealersHand, dealersHBox);

                }else{ //if the dealer has 1 pair or less and has the loosing hand
                    Random randNum = new Random();
                    //Random Number between 0-2
                    int seeOrFold = randNum.nextInt(3);

                    //if the random number = 1 then the dealer folds
                    if(seeOrFold == 1) {
                        winnerNotice.getChildren().remove(0);
                        Text dealerFolds = new Text("Dealer Folds!\nYou Win");
                        dealerFolds.setTextAlignment(TextAlignment.CENTER);
                        winnerNotice.getChildren().add(0, dealerFolds);
                    }else{ //if the random number = 0 or 2 the dealer see's the bet
                        if (dealerTokens < Integer.parseInt(playerBet.getText())) {
                            Text allIn = new Text("Dealer went All In");
                            winnerNotice.getChildren().add(0, allIn);
                            potTokens += dealerTokens;
                            dealerTokens = 0;
                        }else {
                            dealerTokens = dealerTokens - Integer.parseInt(playerBet.getText());
                            potTokens += Integer.parseInt(playerBet.getText());
                        }
                        showCards(dealersHand, dealersHBox);
                    }
                }

                //the winner notice appears, with the winning hand
                winnerNotice.setVisible(true);

            }
        }
        //if the player enters anything other than a number
        catch(NumberFormatException anError){
            JOptionPane.showMessageDialog(null, "Please enter a numeric value between 0-3");
            betNotice.setVisible(true);
            playerBet.clear();
        }
    }


/*
    This function updates all tokens (Player, Dealer and Pot)
 */
    private void updateTokenPot(VBox tokenVBox){
        //creating the token pot background, text and container
        Text potTitle = new Text("Tokens in the Pot");
        potTitle.setStyle("-fx-font: 25 Serif");
        Text potTotal = new Text(potTokens + " Token(s)");

        Rectangle potBG = new Rectangle(200, 200);
        potBG.setStroke(Color.BLACK);
        potBG.setFill(Color.ORANGE);

        VBox potContainer = new VBox();
        potContainer.setAlignment(Pos.CENTER);
        potContainer.getChildren().addAll(potTitle, new StackPane(potBG, potTotal)); //combines the text and background

        tokenVBox.getChildren().clear(); //removes old token values

        Text dealersTokenAmount = new Text("Dealers Tokens: " + dealerTokens);
        Text playerTokenAmount = new Text("Your Tokens: " + playerTokens);

        //if the winner notice is visible then divide up the pot tokens accordingly
        if(winnerNotice.isVisible()) {
            String winningPlayer = hand.compareHands(playersHand, dealersHand);
            //if the player wins, they get all the tokens in the pot
            if (winningPlayer.equalsIgnoreCase("Player")) {
                playerTokens += potTokens;

                //if the dealer wins, they get all the tokens in the pot
            } else if (winningPlayer.equalsIgnoreCase("Dealer")) {
                dealerTokens += potTokens;
            } else {
                //if it is a draw or no one wins then the pot is split
                playerTokens += potTokens / 2;
                dealerTokens += potTokens / 2;
            }
            //set the pot tokens back to 0
            potTokens = 0;
        }

        tokenVBox.getChildren().addAll(dealersTokenAmount, potContainer, playerTokenAmount);
        tokenVBox.setAlignment(Pos.CENTER);
    }


/*
    This function compares the 2 hands and gets the winner.
 */
    private void getWinner(String[] player, String[] dealer, HBox newWinner) {
        newWinner.getChildren().clear();
        Text winner;

        String winningPlayer = hand.compareHands(player, dealer); //compareHands method (HandRanking class) returns the winning hand

        //Depending on the winner the appropriate text is displayed
        if (winningPlayer.equalsIgnoreCase("Player")) {
            winner = new Text( "You Win! -> " + hand.showHandType(player));
        } else if (winningPlayer.equalsIgnoreCase("Dealer")) {
            winner = new Text (winningPlayer + " Wins! -> " + hand.showHandType(dealer));
        } else if (winningPlayer.equalsIgnoreCase("Draw")) {
            winner = new Text ("Draw!");
        } else {
            winner = new Text("No Winner!");
        }

        //The HBox is updated with the new winning player
        newWinner.getChildren().add(winner);
        newWinner.setAlignment(Pos.CENTER);
        winner.setTextAlignment(TextAlignment.CENTER);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
