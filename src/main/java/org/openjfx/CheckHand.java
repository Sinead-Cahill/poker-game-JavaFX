package org.openjfx;

import java.util.Arrays;

/*
    Sinead Cahill

    This Class 'CheckHand' was created previously for the Card Game lab project. The class 'HandRanking' extends this class and has been updated since the previous project
    however still keeps its existing interface so as not to break it.
    This class checks the a card hands' type, whether it has a pair, 2 pairs, 3 of a kind, 4 of a kind, flush, straight or royal flush.
    This class also returns the values or suit for each type.

    Methods:
        - checkCards(String[] cardHand, String type) -> This method is the main method in the class. It takes in a string 'type' (pair, flush etc) and counts the number of matches their are for that card hand.

        + isPair (String[] cardHand) -> returns true or false if hand has a pair
        + numOfPairs(String[] cardHand) -> returns the number of pairs in a hand (1 or 2)
        + isTriplet (String[] cardHand) -> returns true or false if hand has 3 of a kind
        + isFourOfAKind (String[] cardHand) -> returns true or false if hand has 4 of a kind
        + pairTriValue(String[] cardHand, String type) -> returns value of card for a pair, 3 of a kind and 4 of a kind (Ace, 2, 3 etc) [can't rename as is used in other project]
        + doublePairValue(String[] cardHand, String rank) -> returns the values of 2 different pairs in a hand

        + isFullHouse(String[] cardHand) -> returns true or false if hand has a full house
        + fullHouseValues(String[] cardHand, String type) -> returns the values of the full house cards

        + isFlush (String[] cardHand) -> returns true or false if hand is a flush
        + flushSuit(String[] cardHand) -> returns the flush suit

        + isStraight(String[] cardHand) -> returns true or false if hand is a straight
        + sumOfStraight (String[] cardHand) -> returns the sum of the straight hand cards

        + isRoyalFlush(String[] cardHand) -> returns true or false if hand is royal flush

        - sortHand(String[] cardHand) -> sort the order of the cards by their value

        + returnIndex(String card)-> return the index of a card [if jack, queen, king or ace]
        + returnFace(int card)-> return name of card [1 = Ace etc]
 */

public class CheckHand {

    private int matches, value, pairValue1, pairValue2;
    private String suit;
    private int[] newOrder; //used in checkCards and sortHand methods

/*
    The below constructor contains all initialised variables needed in this class
*/
    public CheckHand() {
        //Initialising Variables
        value = -1; //used throughout the class
        suit = ""; //used in checkCards and flushSuit methods
        pairValue1 = -1; //used in checkCards, pairTriValue and doublePairValue methods
        pairValue2 = -1; //used in checkCards and doublePairValue methods

    }

    /*
        This checkCards method is the main method for checking a card hands type (Pair, 3-of-a-Kind etc.)
        A card hand is passed through with a string that indicates the type of hand we are checking for.
        A variable called matches counts the number of matches per type which is then used in other methods.
        The card value or suit is also recorded here.

    */
    private void checkCards(String[] cardHand, String type) {
        String value1 = "", value2 = "", suit2 = ""; //used to temporarily hold strings

        matches = 0; //Matches is set to 0 every time the method is called on so it starts a new count

        //if checking for a flush it looks at the last word of each card (the suit) and then compares each one to the others
        if(type.equals("Flush")) {
            suit = cardHand[0].substring(cardHand[0].lastIndexOf(" ")+1);

            for (int i = 1; i < cardHand.length; i++) {
                suit2 = cardHand[i].substring(cardHand[i].lastIndexOf(" ") + 1);

                //if the two suits being compared match, add one to variable matches
                if (suit.equalsIgnoreCase(suit2)) {
                    matches++;
                }else{
                    //if any 2 suits don't match it can't be a flush so we don't need to continue the check, and stop here
                    continue;
                }
            }
        }else if(type.equals("Straight")) {
        //if checking for a straight it looks at the value of each card and checks they are in order with a difference of +/- 1
            //The card hand is passed through the sortHand method -> the hand is sorted in numerical order depending on each cards value
            sortHand(cardHand);

            for(int i=0; i<newOrder.length-1; i++) {
                //if the card on the left-1 equals the card to the right, add one to the variable matches
                if(newOrder[i+1]-1 ==newOrder[i]) {
                    matches++;;
                }
            }
        }else {
            //If checking for Pairs, 3-of-a-Kind, 4-of-a-Kind, it looks at the value of each card
            for(int i=0; i<cardHand.length; i++) {
                value1 = cardHand[i].substring(0, cardHand[i].indexOf(" "));

                for(int j=0; j<cardHand.length; j++) {
                    //Does not compare the same two cards against each other in the array
                    if(j == i) {
                        continue;
                    }
                    value2 = cardHand[j].substring(0, cardHand[j].indexOf(" "));

                    //if the value at the start of the card matches another, add one to the variable matches
                    //1 pair = 2 matches; 2 pairs = 4 matches; 3-of-a-Kind = 6 matches; 4-of-a-Kind = 12 matches -> these are the number of matches each type should get if true
                    if(value1.equalsIgnoreCase(value2)) {
                        if (type.equals("Pair")) {
                            matches++;
                            //Here we are saving the pair value which is used when comparing cards
                            if(matches == 1){
                                pairValue1 = returnIndex(value1);
                            }
                            //If the match count reaches 3, there are 2 pairs in the hand and save the pair value
                            if(matches == 3){
                                pairValue2 = returnIndex(value1);
                            }
                            //This will catch if the pair values are marked the same and change it over to the other pair value
                            if(matches == 4){
                                if(pairValue1 == pairValue2){
                                    pairValue2 = returnIndex(value1);
                                }else{
                                    continue;
                                }
                            }

                        } else if (type.equals("Triplet")){
                            //if comparing the 4th card in the hand against the other cards & there are < 2 matches, it can't be 3-of-a-Kind so stop checking
                            if(i >= 3 && matches < 2) {
                                continue;
                            }else {
                                matches++;
                                value = returnIndex(value1);
                            }
                        } else if (type.equals("Four of a Kind")){
                            //if comparing the 3rd card against the other cards & the match count is < 3, it can't be 4-of-a-Kind so stop checking
                            if(i >= 2 && matches < 3) {
                                continue;
                            }else {
                                matches++;
                                value = returnIndex(value1);
                            }
                        }
                    }
                }
            }
        }


    }

/*
    This 'isPair' method checks if the card hand has a pair and returns true or false.
*/
    public boolean isPair (String[] cardHand) {
        boolean hasPair = false;

        //The hand it passed through checkCards and counts the number of matches
        checkCards(cardHand, "Pair");

        //if there are 2 matches then there is one pair or if there are 4 matches then there are 2 pairs
        if(matches == 2 || matches == 4) {
            hasPair=true;
        }

        return hasPair;
    }

/*
    This 'numOfPairs' method returns the number of pairs in a hand. It is used in the HandType class when ranking the hand types
*/
    public int numOfPairs(String[] cardHand){
        int numberOfPairs = 0;

        if(isPair(cardHand)) {
            //if there are 2 matches then there is one pair
            if (matches == 2) {
                numberOfPairs = 1;
            } else if (matches == 4) { //if there are 4 matches then there are 2 pairs
                numberOfPairs = 2;
            }
        }

        return numberOfPairs;
    }

/*
    This 'isTriplet' method checks if the card hand has 3 of the same value cards and returns true or false.
*/
    public boolean isTriplet (String[] cardHand) {
        boolean hasTriplet = false;

        //The hand it passed through checkCards and counts the number of matches
        checkCards(cardHand, "Triplet");

        //if there are 6 matches then there is 3 of a Kind
        if(matches == 6) {
            hasTriplet=true;
        }

        return hasTriplet;
    }

/*
    This 'isFourOfAKind' method checks if the card hand has 4 of the same value cards and returns true or false.
*/
    public boolean isFourOfAKind (String[] cardHand) {
        boolean hasFourOfAKind = false;

        //The hand it passed through checkCards and counts the number of matches
        checkCards(cardHand, "Four of a Kind");

        //if there are 12 matches then there is are 4 of the same value cards
        if(matches == 12) {
            hasFourOfAKind=true;
        }

        return hasFourOfAKind;
    }

/*
    I can't rename this method as it is used in the other project under this name but for this project this 'pairTriValue' method is being used to return the card value of 1 pair, 3-of-a-kind and 4-of-a-kind types.
    (In the previous project it is used to return the pair and 3-of-a-kind string values) -> for future purposes I would rename this to something more suitable and it would return  int
*/
    public String pairTriValue(String[] cardHand) { //Needed to return string to keep other project working

        if(isFourOfAKind(cardHand)) {
            return returnFace(value);
        }else if(isTriplet(cardHand)){
            return returnFace(value);
        }else if(isPair(cardHand)){
            return returnFace(pairValue1);
        }
        return "";
    }

/*
    This 'doublePairValue' method returns the value of a highest or lowest pair if there are 2 pairs in the hand.
*/
    public int doublePairValue(String[] cardHand, String rank) {
        //The hand is passed through checkCards and sets the card values
        isPair(cardHand);

        //I am passing in the 2 pair and returning their pairs (mainly if they are Ace, King, Queen or Jack).
        int min = pairValue1;
        int max = pairValue2;
        int temp = -1; //use to temporarily hold int value

        //Here I am taking into account that a pair of Ace's is the highest card if drawn. Its value is 1 so it would be considered the min value so I am overriding this in the next few lines of code
        //I am also checking if the min and max values should be swapped so that they are correct
        if (min == 1) {
            temp = max;
            max = 14;
            min = temp;
        } else if (max == 1) {
            max = 14;
        }else if (max < min) {
            temp = max;
            max = min;
            min = temp;
        }

        if(rank.equalsIgnoreCase("max")){
            value = max;
        }else if(rank.equalsIgnoreCase("min")){
            value = min;
        }

        return value;
    }

/*
    This 'isFullHouse' method checks if the card hand has a pair and triplet and returns true or false.
*/
    public boolean isFullHouse(String[] cardHand){
        boolean hasFullHouse = false;

        //the hand is passed into the method isTriplet (could also pass through method 'isPair'), which will count the number of matches
        isTriplet(cardHand);

        //if there are 8 matches then there is a pair and a triplet
        if(matches == 8){
            hasFullHouse = true;
        }

        return hasFullHouse;
    }

/*
    This 'fullHouseValues' method returns the value of the type requested (triplet or pair)
*/
    public int fullHouseValues(String[] cardHand, String type){
        //hand is sorted be its number value in order
        sortHand(cardHand);

        //if looking for the triplet value, the center card will be one of the three (as long as the card hand is 5 cards in size)
        if(type == "Triplet"){
            value = newOrder[2];
        }else{
            //if looking for the pair value, it checks is the card to the left of the center card less than or equal to it
            //if < then the first 2 cards are the pair; if = then the last 2 cards are the pair
            if(newOrder[2] > newOrder[1]) {
                value = newOrder[0];
            }else{
                value = newOrder[4];
            }
        }
        return value;
    }

/*
    This 'isFlush' method checks if the card hand has all the same suit and returns true or false.
*/
    public boolean isFlush (String[] cardHand) {
        boolean hasFlush = false;

        //The hand it passed through checkCards and counts the number of matches
        checkCards(cardHand, "Flush");

        //if there are 4 matches then it is a flush
        if(matches == 4) {
            hasFlush=true;
        }

        return hasFlush;
    }

/*
    This 'flushSuit' method returns the flush suit
*/
    public String flushSuit(String[] cardHand) {
        //The hand is passed through isFlush and suit type set
        isFlush(cardHand);
        return suit;
    }

/*
    This 'isStraight' method checks if the card hand is a straight and returns true or false.
*/
    public boolean isStraight(String[] cardHand) {
        boolean hasStraight=false;

        //The hand it passed through checkCards and counts the number of matches
        checkCards(cardHand, "Straight");

        //if the matches = 4, then it is a straight
        if(matches==4) {
            hasStraight=true;
        }
        return hasStraight;
    }

/*
    This 'sumOfStraight' method adds up all the integer values of the hand (used when comparing 2 straights)
*/
    public int sumOfStraight (String[] cardHand){
        int sum = 0;

        sortHand(cardHand);

        for(int i=0; i< newOrder.length; i++){
            sum += newOrder[i];
        }

        return sum;
    }

/*
    This 'isRoyalFlush' method checks if the card hand is a high straight and a flush, and returns true or false.
*/
    public boolean isRoyalFlush(String[] cardHand){
        boolean hasRoyalFlush = false;

        //The hand it passed through isFlush method, and if true it is then passed through isStraight method
        if(isFlush(cardHand)){
            isStraight(cardHand);

            //if the new ordered int array matches the highStraight array then its a royal flush
            if(newOrder[4] == 14) { //Ace = 14 only when high straight
                hasRoyalFlush = true;
            }
        }
        return hasRoyalFlush;
    }

/*
    This 'sortHand' method creates a new int array and sorts the card hand in ascending numerical order and returns this new hand order
*/
    private void sortHand(String[] cardHand) {
        //Ace has index value of 1 unless it is with the 4 highest cards in deck -> Ten(10), Jack(11), Queen(12), King(13), Ace(14) -> array is used as a check
        final int[] highStraight = new int[] {10, 11, 12, 13, 14};
        String cardValue; // temporarily holds string
        int index;

        newOrder = new int[cardHand.length];

        for(int i=0; i<cardHand.length; i++) {
            cardValue = cardHand[i].substring(0, cardHand[i].indexOf(" "));
            //if the card value is ace, king, queen or jack we need to set its index
            //else returns string value as integer
            index = returnIndex(cardValue);

            newOrder[i] = index; //add value to new array
        }
        Arrays.sort(newOrder); //Sort in order by number
        //Change the value of Ace from 1 to 14 if the card hand contains 10, Jack, Queen, King and Ace -> high straight
        if(newOrder[0] == 1 && newOrder[1] == 10 && newOrder[2] == 11 && newOrder[3] == 12 && newOrder[4] == 13){
            newOrder = highStraight;
        }

    }

/*
    This 'returnIndex' method returns the value of each card as an integer
*/
    public int returnIndex(String card){
        int index;

        if(card.equalsIgnoreCase("King")) {
            index = 13;
        }else if(card.equalsIgnoreCase("Queen")) {
            index = 12;
        }else if(card.equalsIgnoreCase("Jack")) {
            index = 11;
        }else if(card.equalsIgnoreCase("Ace")) {
            index = 1;
        }else {
            //convert the string value to integer
            index = Integer.parseInt(card);
        }
        return index;
    }

/*
    This 'returnFace' method returns the string name or number of each card. Used mainly for Jack, Queen, King and Ace
*/
    public String returnFace(int card){
        String face;

        if(card == 13) {
            face = "King";
        }else if(card == 12) {
            face = "Queen";
        }else if(card == 11) {
            face = "Jack";
        }else if(card == 1 || card == 14) {
            face = "Ace";
        }else {
            //convert the integer to string
            face = Integer.toString(card);
        }
        return face;
    }
}