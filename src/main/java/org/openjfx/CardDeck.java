package org.openjfx;

import java.util.*;

/*
    Sinead Cahill

    This Class 'CardDeck' creates a deck of cards and a 5 card hand. It also changes cards in a hand with new cards.

    Methods:
        + newDeck() -> creates a deck of 52 cards
        + getCardHand() -> returns an array of 5 cards from the deck
        + changeCard(String[] cardHand, String[] cardsToChange) -> Changes selected cards in the cardHand with new ones from the deck
        - removeFromDeck() -> removes a card from the deck
 */

public class CardDeck {
    private String[] cardSuit, cardValue, deck;
    private int numberOfCards;
    private Random randNum;

/*
    The below constructor contains all initialised variables needed in this class
*/
    public CardDeck() {
        //Initialising Variables
        cardSuit = new String[]{"Hearts", "Diamonds", "Clubs", "Spades"};
        cardValue = new String[]{"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
        numberOfCards = cardSuit.length * cardValue.length; //52
        randNum = new Random();
    }

/*
    This function creates a new deck of cards and shuffles them
 */
    public void newDeck(){
        //create a deck of size 52
        deck = new String[numberOfCards];

        //Create Full Deck using Suit and Value Arrays
        for(int i=0; i < cardSuit.length; i++) {
            for(int j=0; j< cardValue.length; j++) {
                deck[cardValue.length*i + j]=cardValue[j] + " Of " + cardSuit[i];
            }
        }
        //Shuffle Deck
        for(int i=0; i<deck.length; i++){
            //pick a random index number between 0-51
            int randIndex = randNum.nextInt(numberOfCards);
            String temp = deck[randIndex];
            //Swapping over the values at two indexes
            deck[randIndex] = deck[i];
            deck[i] = temp;
        }
    }

/*
    This function returns an array of 5 different cards from the deck
 */
    public String[] getCardHand() {
        String[] cardHand = new String[5];
        //Cards are taken from top of the deck (last in array)
        //Add the card from the deck to the card hand & remove it from deck x5 (length of hand array)
        for(int i=0; i<cardHand.length; i++) {
            cardHand[i] = deck[deck.length-1];
            removeFromDeck();
        }

        return cardHand;
    }

/*
    This function changes cards in the card hand. The cards to change are passed in as an array. If a card in the cardHand matches one in the cardsToChange, it is replaced with a new card from the deck
 */
    public String[] changeCard(String[] cardHand, String[] cardsToChange){ ;

        for(int i=0; i<cardsToChange.length; i++){
            for(int j=0; j<cardHand.length; j++){
                if(cardsToChange[i].equals(cardHand[j])){
                    cardHand[j] = deck[deck.length-1];
                    removeFromDeck();
                }
            }
        }

        return cardHand;
    }

/*
    This function removes the last card in the deck array
 */
    private void removeFromDeck(){
        //Temp array created that is size 1 less than deck array (will be removing 1 card)
        String[] temp = new String[deck.length-1];

        //Discards the last card
        for (int i = 0; i < temp.length; i++) {
            temp[i] = deck[i];
        }
        //Updating the deck array with available cards left
        deck = temp;
    }
}