class Player(val name: String = "Undefined", val startMoney: Int) {

    /* Internal list of Cards */
    private var hand = ArrayList<Card>()

    /**
     * Add card to internal list
     * @param {Card} card - card to store
     * @throws {IllegalStateException} if hand has more than two cards
     */
    fun giveCard(card: Card) {

        this.hand.add(card)

        if (hand.size > 2) {

            throw IllegalStateException("Hand must consist of two cards")
        }
    }

    /**
     * Removes all cards from hand
     */
    fun resetHand() {

        for (card in 0..this.hand.size) {

            this.hand.removeAt(card)
        }
    }

    /**
     * Returns enum of what the players turn is
     * @return {Turn} turn taken by player
     */
    fun getTurn(): Turn {

        return Turn.CHECK
    }

    /**
     * Returns internal list of cards constituting hand
     * @return {Card[]} hand of player
     */
    fun getHand(): ArrayList<Card> {

        return this.hand
    }

    override fun toString(): String {

        var string = "${this.name}\n"

        for (card in this.hand) {

            string += "$card "
        }

        return string
    }
}