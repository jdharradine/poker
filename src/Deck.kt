class Deck {

    /**
     * Internal Deck variable
     */
    private var list = ArrayList<Card>()

    init {

        this.reset()
    }

    /**
     * Resets internal list variable.
     * Removes current cards and replaces them with ordered deck
     */
    private fun resetDeck() {

        this.list = ArrayList()
        for (suit in Suit.values()) {
            for (value in Value.values()) {

                list.add(Card(suit, value))
            }
        }
    }

    /**
     * Randomly shuffles internal list of cards
     */
    private fun shuffleDeck() {

        this.list.shuffle()
    }

    /**
     * Returns next card in deck
     */
    fun getNextCard(): Card {

        if (this.list.size == 0) {

            throw IllegalAccessError("Deck is depleted")
        }

        val card = this.list[0]
        this.list.removeAt(0)

        return card
    }

    /**
     * Resets deck to original state and shuffles
     */
    fun reset() {

        this.resetDeck()
        this.shuffleDeck()
    }

    override fun toString(): String {

        var string = ""

        for (card in list) {

            string += card.toString() + "\n"
        }

        return string
    }
}