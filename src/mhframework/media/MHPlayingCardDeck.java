package mhframework.media;
import java.util.ArrayList;
import java.util.Random;

/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHPlayingCardDeck
{
	public static final int NUMBER_OF_CARDS = 52;

	private final ArrayList<MHPlayingCard> deck; // array of Card objects
    private final Random randomNumbers; // random number generator


   public MHPlayingCardDeck()
   {
      deck = new ArrayList<MHPlayingCard>(NUMBER_OF_CARDS);

      randomNumbers = new Random(); // create random number generator

      reset();

   } // end DeckOfCards constructor


   public void reset()
   {
       deck.clear();

       // populate deck with Card objects
       for ( int count = 0; count < NUMBER_OF_CARDS; count++ )
       {
           final MHPlayingCard card = new MHPlayingCard(count);
           card.setImageGroup(card.getImageGroup());
           addCard(card);
       }
   }

   // shuffle deck of Cards with one-pass algorithm
   public void shuffle()
   {
      // for each Card, pick another random Card and swap them
      for ( int first = 0; first < deck.size()-1; first++ )
      {
         // select a random number between 0 and 51
         final int second =  randomNumbers.nextInt( deck.size()-1 );

         // swap current Card with randomly selected Card
         //System.out.println("Swapping " + deck.get(first) + " with " + deck.get(second));
         final MHPlayingCard temp = deck.remove(first);
         deck.add(first, deck.remove(second));
         deck.add(second, temp);
      } // end for
   } // end method shuffle


   // deal one Card
   public MHPlayingCard dealCard()
   {
      // determine whether Cards remain to be dealt
      if (deck.size() > 0 )
         return deck.remove(0);

      return null; // return null to indicate that all Cards were dealt
   }

   public int size()
   {
	   return deck.size();
   }

   public void addCard(final MHPlayingCard card)
   {
       deck.add(card);
   }


   public boolean isEmpty()
   {
       return deck.size() == 0;
   }
}
