import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackJack {
    static class Card{
        int value;

        public Card(int value){
            this.value = value;
        }

        public String toString() {
            String symbol = getCardSymbol(value);
            int displayValue = value % 13;
            if (displayValue == 0) {
                displayValue = 13;
            }
            return symbol + " " + displayValue;
        }
    }

    // define card symbols
    private static String getCardSymbol(int value) {
        if (value >= 1 && value <= 13) {
            return "Heart";
        } else if (value >= 14 && value <= 26) {
            return "Tile";
        } else if (value >= 27 && value <= 39) {
            return "Club";
        } else {
            return "Spade";
        }
    }

    // build double-deck
    private static List<Card> buildDeck(){
        List<Card> deck = new ArrayList<>();
        // two deck
        for (int i = 0; i < 2; i++){
            for (int value = 1; value <= 52; value++){
                deck.add(new Card(value));
            }
        }
        // mixing operation
        Collections.shuffle(deck);
        return deck;
    }

    // calculate hand value
    private static int calculateHandValue(List<Card> hand) {
        int totalValue = 0;
        int acesCount = 0;

        // First add up the values of all cards and count the number of Aces
        for (Card card : hand) {
            int cardValue = getCardValue(card);
            if (cardValue == 1) { // Ace
                acesCount++;
                totalValue += 11; // first add as 11
            } else {
                totalValue += cardValue;
            }
        }

        // If there is more than one Ace in hand and the total value exceeds 21,
        // consider the value of each Ace as 1
        if (acesCount > 1) {
            totalValue -= 10 * (acesCount - 1);
            acesCount = 1;
        }

        // If the total value still exceeds 21 and there is an Ace,
        // consider the value of this Ace as 1
        if (totalValue > 21 && acesCount > 0) {
            totalValue -= 10;
            --acesCount;
        }

        return totalValue;
    }

    private static int getCardValue(Card card) {
        int rank = card.value % 13;
        if (rank == 0 || rank > 10) { // J, Q, K
            return 10;
        } else if (rank == 1) { // Ace
            return 1;
        } else {
            return rank;
        }
    }

    private static List<Integer> getDeckValues(List<Card> deck){
        List<Integer> values = new ArrayList<>();
        for (Card card : deck) {
            int cardValue = getCardValue(card);
            values.add(cardValue);
        }
        return values;
    }

    private static final double BET_AMOUNT = 1.0;
    private static double playerMoney = 10.0; // Initial money

    private static void playBlakcjack(List<Card> deck){
        List<Card> playerHand = new ArrayList<>();
        List<Card> dealerHand = new ArrayList<>();

        System.out.println("--- NEW TOUR ---");

        playerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));
        playerHand.add(deck.remove(0));
        dealerHand.add(deck.remove(0));

        System.out.println("Money in Case: " + playerMoney + "\n");

        boolean playerTurn = true;
        while (playerTurn){

            System.out.println("Dealer Hand: " + "[" + dealerHand.get(0) + ", Hidden" + "]" + " - Value: " + getCardValue(dealerHand.get(0))); // Show only the dealer's first card
            System.out.println("Player Hand: " + playerHand + " - Value: " + calculateHandValue(playerHand));

            if (calculateHandValue(playerHand) == 21){
                playerMoney += BET_AMOUNT * 1.5;
                System.out.println("\n\n");
                playerTurn = false;
            }
            else {
                if (shouldHit(playerHand, dealerHand.get(0))){
                    System.out.println("HIT");
                    playerHand.add(deck.remove(0));
                    if (calculateHandValue(playerHand) > 21){
                        System.out.println("Player Hand: " + playerHand + " - Value: " + calculateHandValue(playerHand));
                            System.out.println("!!! YOU LOST !!!");
                            playerMoney -= BET_AMOUNT;
                            System.out.println("\n\n");
                            return;
                        }
                    }
                    else {
                        System.out.println("STAND");
                        playerTurn = false;
                    }

            }
        }

            // The dealer's logic of playing
            while (calculateHandValue(dealerHand) < 17) {
                dealerHand.add(deck.remove(0));
            }


        System.out.println("Dealer Hand: " + dealerHand + " - Value: " + calculateHandValue(dealerHand));
        System.out.println("Player Hand: " + playerHand + " - Value: " + calculateHandValue(playerHand));

        // Determining the game outcome
        int playerValue = calculateHandValue(playerHand);
        int dealerValue = calculateHandValue(dealerHand);

        if (dealerValue > 21 || dealerValue < playerValue){
            System.out.println("!!! YOU WIN !!!");
            playerMoney += BET_AMOUNT;
            System.out.println("\n\n");
        } else if (dealerValue == playerValue) {
            System.out.println("!!! DRAW !!!");
            System.out.println("\n\n");
        } else {
            System.out.println("!!! YOU LOST !!!");
            playerMoney -= BET_AMOUNT;
            System.out.println("\n\n");
        }

    }


    private static boolean shouldHit(List<Card> playerHand, Card dealerCard) {
        int playerValue = calculateHandValue(playerHand);
        int dealerValue = getCardValue(dealerCard);

        boolean isSoftHand = isSoftHand(playerHand); // this control is checking the hand is soft or not

        // if player hand is soft (it means there is ace in hand and counts as 11)
        if (isSoftHand) {
            if (playerValue <= 17) {
                return true; // Hit for soft 17 or under
            } else if (playerValue == 18) {
                return dealerValue >= 9; // Hit if the dealer's card is 9, 10 or Ace
            } else {
                return false; // Soft stop for 19 or higher
            }
        }
        // If the player's hand is hard (it means there is no Ace in hand or Ace counts as 1)
        else {
            if (playerValue <= 11) {
                return true; // always hit for 11 or below
            } else if (playerValue == 12) {
                return dealerValue < 4 || dealerValue > 6; // Hit if the dealer's card is 2, 3, 7 or higher
            } else {
                return playerValue <= 16 && dealerValue > 6; // Hit if the player's hand is 16 or lower and the dealer's card is 7 or higher
            }
        }
    }


    private static boolean isSoftHand(List<Card> hand) {
        int totalValue = 0;
        boolean hasAce = false;

        for (Card card : hand) {
            int cardValue = getCardValue(card);
            if (cardValue == 1) { // there is ace
                hasAce = true;
                totalValue += 11;
            } else {
                totalValue += cardValue;
            }
        }

        if (hasAce && totalValue <= 21) {
            return true; // If the value of the hand does not exceed 21 and there is an Ace, it is a soft hand.
        }
        return false;
    }

    public static void main(String[] args) {
        List<Card> deck = buildDeck();


        while (true) {
            if (deck.size() < 7) {
                System.out.println("Not enough card. Game done!");
                System.out.println("FINAL MONEY: " + playerMoney + "$");
                break;
            }

            //System.out.println("Deck'deki kart değerleri: " + getDeckValues(deck)); // shows the cards in the deck
            playBlakcjack(deck);
        }
    }
}