package org.openjfx;

/*
    Sinead Cahill

    This Class 'HandRanking' ranks a card hand and compares 2 hands.
    It extends the 'CheckHand' class and inherits all the public CheckHand methods

    Methods:
        + getRanking(String[] cardHand) -> returns in integer value depending on its type. The higher the value the higher the ranking
        + showHandType(String[] cardHand) -> returns the hand match type as a string
        + compareHands(String[] player, String[] dealer)  -> compares 2 arrays and returns the winner

 */

public class HandRanking extends CheckHand{

    private String winner, cardType;

/*
    The below constructor contains all initialised variables needed in this class
*/
    public HandRanking() {
        //initialising variables
        winner = "";
        cardType = "";
    }

/*
    This method returns the rank value of a hand. This higher the ranking the better the hand.
    The hand is checked for each hand type, starting with the best
 */
    public int getRanking(String[] cardHand) {

        if(isRoyalFlush(cardHand)) {
            cardType = "Royal Flush";
            return 9;
        }else if(isStraight(cardHand) && isFlush(cardHand)) { //Straight Flush
            cardType = "Straight Flush";
            return 8;
        }else if(isFourOfAKind(cardHand)) {
            cardType = "Four of a Kind (" + pairTriValue(cardHand) + "'s)";
            return 7;
        }else if(isFlush(cardHand)) {
            cardType = "Flush of " + flushSuit(cardHand) + "'s";
            return 6;
        }else if(isStraight(cardHand)) {
            cardType = "Straight";
            return 5;
        }else if(isFullHouse(cardHand)){
            cardType = "Full House (Three " + returnFace(fullHouseValues(cardHand,"Triplet")) + "'s, Pair of " + returnFace(fullHouseValues(cardHand,"Pair")) + "'s)";
            return 4;
        }else if(isTriplet(cardHand)) {
            cardType = "Three of a Kind (" + pairTriValue(cardHand) + "'s)";
            return 3;
        }else if(isPair(cardHand) && numOfPairs(cardHand)==2){ //2 Pairs
            cardType = "Two Pairs (Pair of " + returnFace(doublePairValue(cardHand, "max")) + "'s, Pair of " + returnFace(doublePairValue(cardHand, "min")) + "'s)";
            return 2;
        }else if(isPair(cardHand) && numOfPairs(cardHand)==1){ //1 Pair
            cardType = "Pair of " + pairTriValue(cardHand) + "'s";
            return 1;
        }else{
            cardType = "";
            return 0;
        }
    }


/*
    This method returns the hand type in string form. It is called when the winning hand is being shown to the user
 */
    public String showHandType(String[] cardHand){
        getRanking(cardHand);
        return cardType;
    }


/*
    This method compares 2 card hands and returns the winner (Player, Dealer, Draw or No Winner)
 */
    public String compareHands(String[] player, String[] dealer) {
        int min = -1, max = -1;
        //Get the ranking of both hands
        int playerRank = getRanking(player);
        int dealerRank = getRanking(dealer);


        if(playerRank == 0 && dealerRank == 0){
            winner = "No Winner";
        }else if(playerRank > dealerRank){
            winner = "Player";
        }else if(playerRank < dealerRank){
            winner = "Dealer";
        }else { //If both have same ranking
            //if players hand is 4 of a kind, 3 of a kind or 1 pair (dealer will have the same ranking)
            if (playerRank == 7 || playerRank == 3 || playerRank == 1) {
                //set player and dealer to max and min
                min = returnIndex(pairTriValue(player));
                max = returnIndex(pairTriValue(dealer));
            }

            if (playerRank == 2 && dealerRank == 2) {
                //The highest pair values are first selected to be compared against each other
                min = doublePairValue(player, "max");
                max = doublePairValue(dealer, "max");

                //If the highest pair values both match, compare the second pair against each other
                if (min == max) {
                    min = doublePairValue(player, "min");
                    max = doublePairValue(dealer, "min");
                }
            } else if (playerRank == 4 && dealerRank == 4) {
                min = fullHouseValues(player, "Triplet");
                max = fullHouseValues(dealer, "Triplet");

                if (min == max) {
                    min = fullHouseValues(player, "Pair");
                    max = fullHouseValues(dealer, "Pair");
                }
            } else if (playerRank == 8 && dealerRank == 8 || playerRank == 5 && dealerRank == 5) {
                min = sumOfStraight(player);
                max = sumOfStraight(dealer);
            }

            //Taking into account Ace will be the highest card in a pair/triplet/4 of a kind ect
            if (min == 1) {
                min = 14;
            } else if (max == 1) {
                max = 14;
            }

            //Min is set as player, Max is set at Dealer.
            if (max > min) {
                winner = "Dealer";
            } else if (max < min) {
                winner = "Player";
            } else {
                winner = "Draw";
            }
        }
        return winner;
    }
}

