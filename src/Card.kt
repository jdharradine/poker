class Card(val suit: Suit, val value: Value) {

    /**
     * Stores string to print when toString is called
     */
    private var unicode: String = "DC" /* Unicode base */

    init {

        initCardSymbol()
    }

    /**
     * Sets up string when toString is called
     **/
    private fun initCardSymbol() {

        /* Assign suit characters */
        when (this.suit) {

            Suit.HEARTS -> {

                this.unicode += "B"
            }

            Suit.DIAMONDS -> {

                this.unicode += "C"
            }

            Suit.CLUBS -> {

                this.unicode += "D"
            }

            Suit.SPADES -> {

                this.unicode += "A"
            }
        }

        /* Assign value characters */
        if (this.value == Value.QUEEN || this.value == Value.KING) {

            this.unicode += "%01X".format(this.value.value + 1)
        } else {

            this.unicode += this.value.toString()
        }
    }

    override fun toString(): String {
        return "\uD83C" +
                (Integer.parseInt(this.unicode, 16)).toChar().toString()
    }

    override fun equals(other: Any?): Boolean {

        if (other !is Card) {

            return false
        }

        return this.suit == other.suit &&
                this.value == other.value
    }
}