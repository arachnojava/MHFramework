package mhframework.media;

import java.awt.Graphics2D;
import java.awt.Image;
import mhframework.MHActor;

/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHPlayingCard extends MHActor
{
	// Faces
	public static final int ACE   =  0;
	public static final int DEUCE =  1;
	public static final int THREE =  2;
	public static final int FOUR  =  3;
	public static final int FIVE  =  4;
	public static final int SIX   =  5;
	public static final int SEVEN =  6;
	public static final int EIGHT =  7;
	public static final int NINE  =  8;
	public static final int TEN   =  9;
	public static final int JACK  = 10;
	public static final int QUEEN = 11;
	public static final int KING  = 12;

	// Suits
	public static final int SUIT_SPADES   = 0;
	public static final int SUIT_DIAMONDS = 1;
	public static final int SUIT_CLUBS    = 2;
	public static final int SUIT_HEARTS   = 3;

	// Card back image number
	public static final int CARD_BACK = 52;

	// String representations
	private static final String FACES[] = {
		"Ace",   "Deuce", "Three", "Four", "Five",  "Six", "Seven",
		"Eight", "Nine",  "Ten",   "Jack", "Queen", "King" };
    private static final String SUITS[] = {"Spades", "Diamonds", "Clubs", "Hearts"};

    private final int face; // face of card
    private final int suit; // suit of card
    private final int number;
    private boolean faceDown = false;
    private static MHImageGroup cardImages;


    /*****************************************************************
     *
     * @param cardFace
     * @param cardSuit
     */
   public MHPlayingCard( final int cardFace, final int cardSuit )
   {
      face = cardFace; // initialize face of card
      suit = cardSuit; // initialize suit of card
      number = suit * 13 + face;

      setImageGroup(getImageGroup());
   }


   /*****************************************************************
   *
   * @param cardFace
   * @param cardSuit
   */
   public MHPlayingCard(final int cardNumber)
 {
    number = cardNumber;
    face = number % 13;
    suit = number / 13;
   }


   @Override
public MHImageGroup getImageGroup()
   {
       if (cardImages == null)
       {
       cardImages = new MHImageGroup();
       cardImages.addSequence(0);

       // number of cards + 1 because of the card back image
       for (int i = 0; i < MHPlayingCardDeck.NUMBER_OF_CARDS+1; i++)
           cardImages.addFrame(0, "images/"+i+".gif", 1);
       }

       return cardImages;
   }


   @Override
public Image getImage()
   {
       if (isFaceDown())
           setFrameNumber(CARD_BACK);
       else
           setFrameNumber(number);

       return cardImages.getImage(0, number);
   }


   public boolean equals(final MHPlayingCard otherCard)
   {
       return (face == otherCard.getFace() && suit == otherCard.getSuit());
   }


   public boolean equals(final int otherCardNumber)
   {
       return (number == otherCardNumber);
   }


   public boolean equals(final int otherCardFace, final int otherCardSuit)
   {
       return (face == otherCardFace && suit == otherCardSuit);
   }


   @Override
   public void render(final Graphics2D g)
   {
       if (isFaceDown())
           setFrameNumber(CARD_BACK);
       else
           setFrameNumber(number);

       super.render(g);
   }


   /*****************************************************************
    * Return an integer indicating the face of this card.
    *
    * @return An integer indicating the face of this card.  See the
    *         constants defined in this class for possible values.
    */
   public int getFace()
   {
		return face;
   }


   /*****************************************************************
   * Return the unique identifier for this card.
   *
   * @return The unique identifier for this card.
   */
  public int getCardNumber()
  {
       return number;
  }


  /*****************************************************************
   * Return an integer indicating the suit of this card.
   *
   * @return An integer indicating the suit of this card.  See the
   *         constants defined in this class for possible values.
   */
   public int getSuit() {
		return suit;
	}


   public void setFaceDown(final boolean isFaceDown)
   {
       faceDown = isFaceDown;
   }


   public boolean isFaceDown()
   {
       return faceDown;
   }


   /*****************************************************************
    * Returns a string representation of this card.
    *
    * @return A string representation of this card.  For example,
    *         "Four of Diamonds".
    */
   @Override
   public String toString()
   {
      return FACES[face] + " of " + SUITS[suit];
   }
}
