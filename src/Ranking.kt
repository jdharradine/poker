import java.lang.IllegalArgumentException

class Ranking(private val communityCards: ArrayList<Card>, private val hand: ArrayList<Card>) {

    /**
     * Stores the value of the highest card to distinguish between similar hands
     * @type {Int}
     */
    private var highestCardValue: Int = 0;

    /** handRanking stores the hand rank category of the hand
     * @type {HandRanking}
     */
    private var handRanking = HandRanking.HIGH_CARD

    /** Score is a metric that can be used to directly compare hands
     * The number to the left of the decimal point determines the hand type (e.g. pair, flush, etc)
     * Whereas the number to the right determines the total card value of the significant cards in hand
     * @type {Double}
     **/
    private var score: Double = 0.0

    init {

        /* Throw error if community Cards is not of correct size */
        if (this.communityCards.size != 5) {

            throw IllegalArgumentException("Community cards must consist of five cards to be ranked")
        }

        /* Throw error if hand is not of correct size */
        if (this.hand.size != 2) {

            throw IllegalArgumentException("Hand must consist of two cards to be ranked")
        }

        /* Sort cards */
        this.communityCards.sortBy{it.value}
        this.hand.sortBy{it.value}

        /* Extract combinations from hand */
        val combinations: ArrayList<ArrayList<Card>> = this.getCombinations()

        /* Determine hand ranking */
        this.score = this.extractScore(combinations)
    }

    /** Returns list of combinations of cards
     * @return {Card[][]} list of combinations of cards in this hand
     */
    private fun getCombinations(): ArrayList<ArrayList<Card>> {
        var index = 0
        val combinations = ArrayList<ArrayList<Card>>()
        combinations.add(ArrayList())

        for (card in 1 until this.communityCards.size) {

            if (this.communityCards[card].value == this.communityCards[card - 1].value) {

                combinations[index].add(this.communityCards[card])

                if (!combinations[index].contains(this.communityCards[card -1])) {

                    combinations[index].add(this.communityCards[card - 1])
                }
            } else if (combinations[index].size != 0){

                index++
                combinations.add(ArrayList())
            }
        }

        /* Clean up empty combinations and return */
        return this.getCleanCombinations(combinations)
    }

    /** Returns cleaned list of card combination
     * @param {Card[][]} combinations - list of combinations to clean
     * @return {Card[][]} cleaned list of combinations
     */
    private fun getCleanCombinations(combinations: ArrayList<ArrayList<Card>>): ArrayList<ArrayList<Card>> {

        val tempCombinations = ArrayList<ArrayList<Card>>()
        for (combination in combinations) {

            if (combination.size != 0) {

                tempCombinations.add(combination)
            }
        }

        return tempCombinations
    }

    /** Sets the internal handRanking property and returns score value based off of hand and
     * extracted combinations
     * @param {Card[]} hand - list of cards in hand
     * @param {Card[][]} combinations
     * @return {Double} sub-score of hand
     **/
    private fun extractScore(combinations: ArrayList<ArrayList<Card>>): Double {

        /* Default score is the card with the highest value */
        this.highestCardValue = this.communityCards.last().value.ordinal
        this.score = (this.highestCardValue / 100.0)

        /* extract ranking from combinations */
        this.getScoreFromCombinations(combinations)

        /* Check combinations of flush and straight */
        this.getScoreFromOther()

        println(this.handRanking.name)
        println("Score: " + String.format("%.2f", this.score))

        return score
    }

    /** Returns true if hand is flush
     * @param {Card[]} hand - list of cards to check if flush
     * @return {Boolean} true is hand is flush else false
     **/
    private fun isFlush(hand: ArrayList<Card>): Boolean {

        val suitToMatch: Suit = hand[0].suit

        for (card in hand) {

            if (card.suit != suitToMatch) {

                return false
            }
        }

        return true
    }

    /** Returns true if hand is straight
     * @param {Card[]} hand - list of cards to check if straight
     * @return {Boolean} true is hand is straight else false
     **/
    private fun isStraight(hand: ArrayList<Card>): Boolean {

        /* Try without rearranging aces */
        if (this.isOrdered(hand)) {

            return true
        }

        /* If ace is in hand just, move it to the end of the list */
        if (hand[0].value == Value.ACE) {

            hand.add(hand[0])
            hand.removeAt(0)

            if (this.isOrdered(hand)) {

                return true
            }
        }

        return false
    }

    /** Returns true if hand is in order. Note that ACE may constitute 1 or 14
     * @param {Card[]} hand - list of cards to check if ordered
     * @return {Boolean} true if ordered else false
     **/
    private fun isOrdered(hand: ArrayList<Card>): Boolean {

        for (card in 1 until hand.size) {

            if ((hand[card].value.ordinal - 1) !=
                    hand[card - 1].value.ordinal) {

                if (hand[card].value == Value.ACE &&
                        hand[card - 1].value == Value.KING &&
                        card == hand.size - 1) { // Skip if last two elements consist of king (13) and ace (1)

                    continue
                }

                return false
            }
        }

        return true
    }

    /** Returns true if hand is royal flush
     * @param {Card[]} hand - list of cards to check if royal flush
     * @return {Boolean} true is hand is royal flush else false
     */
    private fun isRoyalFlush(hand: ArrayList<Card>): Boolean {

        return this.isFlush(hand) &&
                this.isStraight(hand) &&
                (hand[0].value == Value.TEN)
    }

    /** Sets score and handRanking to best handRanking found in combinations
     * Note: if no combination ranking can be found in combinations
     * score and handRanking are left unchanged
     * @param {Card[][]} combinations - list of combinations to check
     */
    private fun getScoreFromCombinations(combinations: ArrayList<ArrayList<Card>>) {

        if (combinations.size == 1) {

            when (combinations[0].size) {

                2 -> {
                    this.score = HandRanking.ONE_PAIR.value.toDouble()
                    this.handRanking = HandRanking.ONE_PAIR
                }
                3 -> {
                    this.score = HandRanking.THREE_OF_A_KIND.value.toDouble()
                    this.handRanking = HandRanking.THREE_OF_A_KIND
                }
                4 -> {
                    this.score = HandRanking.FOUR_OF_A_KIND.value.toDouble()
                    this.handRanking = HandRanking.FOUR_OF_A_KIND
                }
            }
        } else if (combinations.size == 2) {

            if (combinations[0].size == 2 && combinations[1].size == 2) {

                score = HandRanking.TWO_PAIR.value.toDouble()
                this.handRanking = HandRanking.TWO_PAIR
            } else if ((combinations[0].size == 3 && combinations[1].size == 2) ||
                    (combinations[1].size == 3 && combinations[0].size == 2)) {

                score = HandRanking.FULL_HOUSE.value.toDouble()
                this.handRanking = HandRanking.FULL_HOUSE
            }
        }
        this.score += getSubScoreFromCombinations(combinations)
    }

    /** Sets score and handRanking to best handRanking found in hand
     * Note: if no other hand ranking can be found in combinations
     * score and handRanking are left unchanged
     */
    private fun getScoreFromOther() {

        if (this.isFlush(this.communityCards) && this.isStraight(this.communityCards)) {

            if (this.isRoyalFlush(this.communityCards)) {

                this.score = HandRanking.ROYAL_FLUSH.value.toDouble()
                this.handRanking = HandRanking.ROYAL_FLUSH
            } else {

                this.score = HandRanking.STRAIGHT_FLUSH.value.toDouble()
                this.handRanking = HandRanking.STRAIGHT_FLUSH
                this.score += this.getSubScoreFromHand(communityCards)

            }
        } else if (this.isFlush(this.communityCards)) {

            this.score = HandRanking.FLUSH.value.toDouble()
            this.handRanking = HandRanking.FLUSH
            this.score += this.getSubScoreFromHand(communityCards)
        } else if (this.isStraight(this.communityCards)) {

            this.score = HandRanking.STRAIGHT.value.toDouble()
            this.handRanking = HandRanking.STRAIGHT
            this.score += this.getSubScoreFromHand(this.communityCards)
        }
    }

    /** Returns sub-score of list of combinations
     * @param {Card[][]} combinations - list of combinations of cards
     * @return {Double} sub-score of all cards in combinations array
     **/
    private fun getSubScoreFromCombinations(combinations: ArrayList<ArrayList<Card>>): Double {

        var score = 0.0
        for (combination in combinations) {

            score += this.getSubScoreFromHand(combination)
        }

        return score
    }

    /** Returns sub-score of list of cards (hand).
     * Note: also helper function for getSubScoreFromCombinations
     * @param {Card[]} hand - list of cards
     * @return {Double} sub-score of all cards in hand array
     **/
    private fun getSubScoreFromHand(hand: ArrayList<Card>): Double {

        for (card in hand) {

            score += card.value.ordinal + 1
        }

        return score / 100.0
    }

    /** Prints formatted combinations found in hand
     * @param {Card[][]} combinations - combinations to print
     * @throws {IllegalStateException} if combination list does not contain 2,3 or 4 cards
     */
    private fun printCombinations(combinations: ArrayList<ArrayList<Card>>) {

        if (combinations.size < 1) {

            println("No combinations found")
            return
        }

        for (combination in combinations) {

            /* Print combination type */
            when (combination.size) {

                2 -> println("Pair")
                3 -> println("Three of a kind")
                4 -> println("Four of a kind")
                else -> throw IllegalStateException("Something went wrong (${combination.size})")
            }

            var string = ""
            for (card in combination) {

                string += "$card "
            }

            println(string)
        }
    }

    /** Prints formatted hand
     * @param {String} prefix - string to print on separate line before cards are printed
     * @param {Card[]} hand - list of cards to print
     */
    private fun printHand(prefix: String, hand: ArrayList<Card>) {

        println(prefix)

        var string = ""
        for (card in hand) {

            string += "$card "
        }

        println(string)
    }
}