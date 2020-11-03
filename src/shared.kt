/* Enum storing suit of a card */
enum class Suit(val type: Int) {

    HEARTS(1),
    DIAMONDS(2),
    CLUBS(3),
    SPADES(4)
}

/* Enum storing value of card */
enum class Value(val value: Int) {

    ACE(14), // Special value because also 14
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13);

    override fun toString(): String {

        return "%01X".format(super.ordinal + 1)
    }
}

/* Enum storing types of poker hands */
enum class HandRanking(val value: Int) {

    HIGH_CARD(0),
    ONE_PAIR(1),
    TWO_PAIR(2),
    THREE_OF_A_KIND(3),
    STRAIGHT(4),
    FLUSH(5),
    FULL_HOUSE(6),
    FOUR_OF_A_KIND(7),
    STRAIGHT_FLUSH(8),
    ROYAL_FLUSH(9)
}

/* Enum storing possible moves a player can make */
enum class Turn(val move: Int) {

    FOLD(0),
    CHECK(1),
    RAISE(2)
}