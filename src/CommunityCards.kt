import java.lang.IllegalArgumentException

class CommunityCards {

    /**
     * List of cards for the community
     */
    private var list = ArrayList<Card>()

    /**
     * Adds cards in flop array to internal list
     * @param {Card[]} flop - contains the cards to add to the community cards
     * @throws {IllegalArgumentException} if this is not the first card providing function
     * called after object init or reset
     * @throws {IllegalArgumentException} if flop array does not contain three cards
     **/
    fun receiveFlop(flop: ArrayList<Card>) {

        if (this.list.size != 0) {

            throw IllegalArgumentException("Flop must be presented at the begining of the turn")
        }

        if (flop.size != 3) {

            throw IllegalArgumentException("Flop must be of size 3")
        }

        this.list.addAll(flop)
    }

    /**
     * Adds card 'turn' to internal list
     * @param {Card} turn - card to add to internal list
     * @throws {IllegalArgumentException} if this is not called after receiveFlop
     **/
    fun receiveTurn(turn: Card) {

        this.receiveSingleCard(card = turn, numExpected = 3)
    }

    /**
     * Adds card 'river' to internal list
     * @param {Card} river - card to add to internal list
     * @throws {IllegalArgumentException} if this is not called after receiveTurn
     **/
    fun receiveRiver(river: Card) {

        this.receiveSingleCard(card = river, numExpected = 4)
    }

    /**
     * Adds card to internal list
     * @param {Card} card - card to add to internal list
     * @param {Int} numExpected - expected number of cards before adding card
     * @throws {IllegalArgumentException} if size of original internal list is not numExpected
     **/
    private fun receiveSingleCard(card: Card, numExpected: Int) {

        if (this.list.size != numExpected) {

            throw IllegalArgumentException("Card received out of order")
        }

        this.list.add(card)
    }

    /**
     * Resets internal list
     */
    fun reset() {
        for (card in this.list) {

            this.list.remove(card)
        }
    }

    /**
     * Returns internal list
     * @return {Card[]} internal list of cards
     */
    fun getList(): ArrayList<Card> {

        return this.list
    }

    override fun toString(): String {

        var string = ""

        for (card in this.list) {

            string += "$card "
        }

        return string
    }
}