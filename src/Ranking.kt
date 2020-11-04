import java.lang.IllegalArgumentException

class Ranking(private val communityCards: ArrayList<Card>, private val hand: ArrayList<Card>, val playerIdx: Int): Comparable<Ranking> {

    /**
     * Stores the value of the highest card to distinguish between similar hands
     * @type {Int}
     */
    private lateinit var highestCard: Card

    /**
     * handRanking stores the hand rank category of the hand
     * @type {HandRanking}
     */
    private var handRanking = HandRanking.HIGH_CARD

    /**
     * Score is a metric that can be used to directly compare hands
     * It determines the total card value of the significant cards in hand
     * @type {Int}
     */
    private var cardSum: Int = 0

    /**
     * Stores all the cards to be ranked
     * @type {Card[]}
     */
    private val combinedCards: ArrayList<Card> = ArrayList()

    init {

        /* Throw error if community Cards is not of correct size */
        if (this.communityCards.size != 5) {

            throw IllegalArgumentException("Community cards must consist of five cards to be ranked")
        }

        /* Throw error if hand is not of correct size */
        if (this.hand.size != 2) {

            throw IllegalArgumentException("Hand must consist of two cards to be ranked")
        }

        /* Initialise combined cards */
        this.combinedCards.addAll(this.communityCards)
        this.combinedCards.addAll(this.hand)

        /* Sort cards */
        this.hand.sortBy{it.value.ordinal}
        this.combinedCards.sortBy{it.value.ordinal}

        /* Determine hand ranking */
        this.cardSum = this.extractScore()

        println()
        println(this.handRanking.name)
        println("Card Sum: " + String.format("%d", this.cardSum))
        println("Highest Card: ${highestCard.value.name}")
    }

    /** Returns list of combinations of cards
     * @return {Card[][]} list of combinations of cards in this hand
     */
    private fun getCombinations(): ArrayList<ArrayList<Card>> {
        var index = 0
        val combinations = ArrayList<ArrayList<Card>>()
        combinations.add(ArrayList())

        for (card in 1 until this.combinedCards.size) {

            if (this.combinedCards[card].value == this.combinedCards[card - 1].value) {

                combinations[index].add(this.combinedCards[card])

                if (!combinations[index].contains(this.combinedCards[card -1])) {

                    combinations[index].add(this.combinedCards[card - 1])
                }
            } else if (combinations[index].size != 0){

                index++
                combinations.add(ArrayList())
            }
        }

        /* Clean up empty combinations and return */
        return this.getCleanCombinations(combinations)
    }

    /** Returns cleaned list of card combination. Also sorts combinations by ranking
     * and assures only 5 cards is in returned array
     * @param {Card[][]} combinations - list of combinations to clean
     * @return {Card[][]} cleaned list of combinations
     */
    private fun getCleanCombinations(combinations: ArrayList<ArrayList<Card>>): ArrayList<ArrayList<Card>> {

        val tempCombinations = ArrayList<ArrayList<Card>>()

        /* Remove empty combinations */
        for (combination in combinations) {

            if (combination.size != 0) {

                tempCombinations.add(combination)
            }
        }

        /* Sort combinations by ranking */
        tempCombinations.sortBy{it[0].value.value} // organise by card value
        tempCombinations.sortBy{it.size} // organise by number of cards in combination

        /* Find top 5 card combination */
        while (this.getCombinationsSize(tempCombinations) > 5) {

            if (tempCombinations[0].size > 2) {

                tempCombinations[0].removeAt(0)
            } else {

                tempCombinations.removeAt(0)
            }
        }

        return tempCombinations
    }

    /**
     * Returns the number of cards in the array of array of cards
     * @param {Card[][]} combinations - 2d array to count elements of
     * @return the total number of cards in combinations
     */
    private fun getCombinationsSize(combinations: ArrayList<ArrayList<Card>>): Int {

        var size = 0
        for (combination in combinations) {

            size += combination.size
        }

        return size
    }

    /** Sets the internal handRanking property and returns score value based off of hand and
     * extracted combinations
     * @param {Card[]} hand - list of cards in hand
     * @param {Card[][]} combinations
     * @return {Double} sub-score of hand
     **/
    private fun extractScore(): Int {

        /* Extract combinations from hand */
        val combinations: ArrayList<ArrayList<Card>> = this.getCombinations()
        this.printCombinations(combinations)

        /* Default score is the card with the highest value */
        if (this.hand.first().value == Value.ACE) {

            this.highestCard = this.hand.first()
        } else {

            this.highestCard = this.hand.last()
        }

        this.cardSum = this.highestCard.value.value

        /* extract ranking from combinations */
        this.getScoreFromCombinations(combinations)

        /* Check combinations of flush and straight */
        this.getScoreFromStraightAndFlush()

        return cardSum
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
                    this.handRanking = HandRanking.ONE_PAIR
                }
                3 -> {
                    this.handRanking = HandRanking.THREE_OF_A_KIND
                }
                4 -> {
                    this.handRanking = HandRanking.FOUR_OF_A_KIND
                }
            }
        } else if (combinations.size == 2) {

            if (combinations[0].size == 2 && combinations[1].size == 2) {

                this.handRanking = HandRanking.TWO_PAIR
            } else if ((combinations[0].size == 3 && combinations[1].size == 2) ||
                    (combinations[1].size == 3 && combinations[0].size == 2)) {

                this.handRanking = HandRanking.FULL_HOUSE
            }
        }
        getSubScoreFromCombinations(combinations)
    }

    /** Sets score and handRanking to best flush, straight combination found in hand
     * Note: if no other hand ranking can be found in combinations
     * score and handRanking are left unchanged
     */
    private fun getScoreFromStraightAndFlush() {

        val largestFlush: ArrayList<Card> = this.getLargestFlush(this.combinedCards)
        val orderedCards: ArrayList<Card> = this.getOrderedCards(this.combinedCards)

        /* Check if straight is possible */
        if (orderedCards.isNotEmpty()) {

            /* Check if hand is also a flush */
            val straightFlush: ArrayList<Card> = this.getLargestFlush(this.combinedCards)
            if (straightFlush.isNotEmpty()) {

                /* Check if hand is also royal flush */
                if (this.isRoyalFlush(straightFlush)) {

                    setScore(straightFlush, HandRanking.ROYAL_FLUSH)
                } else { // Hand is straight flush

                    setScore(straightFlush, HandRanking.STRAIGHT_FLUSH)
                }
                this.printHand("Largest Straight Flush", straightFlush)
            } else { // Hand may be straight

                val largestStraight = this.getHighestCards(this.removeDuplicateValues(orderedCards))

                if (largestStraight.isNotEmpty()) {

                    this.printHand("Largest Straight (${largestStraight.size})", largestStraight)
                    setScore(largestStraight, HandRanking.STRAIGHT)
                }
            }
            return
        }

        /* Check hand for flush */
        if (largestFlush.isNotEmpty()) {

            setScore(largestFlush, HandRanking.FLUSH)

            this.printHand("Largest Flush", largestFlush)
        }
     }

    /**
     * Takes in the highest value hand found and the associated handRanking and sets score properties of object
     * @param {Card[][]} bestHand - highest ranked hand found
     * @param {HandRanking} handRanking - hand ranking of bestHand
     */
    private fun setScore(bestHand: ArrayList<Card>, handRanking: HandRanking) {

        this.handRanking = handRanking

        /* If hand is straight (or straight flush) starting at ACE apply offset to account for ACE's default value */
        if ((handRanking == HandRanking.STRAIGHT || handRanking == HandRanking.STRAIGHT_FLUSH) &&
                bestHand[0].value == Value.ACE) {

            this.cardSum -= (Value.ACE.value - 1)
        }

        this.cardSum += getSubScoreFromHand(bestHand)
    }

    /**
     * Returns subset of cards consisting of cards that have the most frequent suit
     * There must be at-least 5 cards of the most frequent suit for something to be returned otherwise empty list
     * @param {Card[]} cards - list of cards to get flush
     * @return {Card[]} list of cards of most frequent suit (if there are 5 or more)
     */
    private fun getLargestFlush(cards: ArrayList<Card>): ArrayList<Card> {
        /* Initialise array of suit quantities */
        val suitBins = ArrayList<ArrayList<Card>>()

        /* Initialise arrays in suitBins */
        for (suit in Suit.values()) {

            suitBins.add(ArrayList())
        }

        /* Organise cards into bins */
        for (card in cards) {

            suitBins[card.suit.ordinal].add(card)
        }

        /* Sort suitBins by length and determine if largest bin constitutes flush */
        suitBins.sortBy { it.size }
        return this.getHighestCards(suitBins.last())
    }

    /**
     * Returns subset of cards consisting of the largest value ordered cards of size 5
     * There must be at-least 5 cards in order for something to be returned otherwise empty list
     * @param {Card[]} cards - list of cards to get flush
     * @return {Card[]} list of cards of most frequent suit (if there are 5 or more)
     */
    private fun getOrderedCards(cards: ArrayList<Card>): ArrayList<Card> {

        val straights: ArrayList<ArrayList<Card>> = ArrayList()
        straights.add(ArrayList())

        /* If there is an ace at the beginning of the cards list add it to the end */
        if (cards[0].value == Value.ACE) {

            cards.add(cards[0])
        }

        /* Extract lists of ordered cards */
        for (card in 1 until cards.size) {

            /* If card is not ordered or same as last add next ordered cards to next list */
            if (cards[card].value.value - 1 != cards[card - 1].value.value &&
                    cards[card].value.value != cards[card - 1].value.value &&
                    !(cards[card].value == Value.TWO && cards[card - 1].value == Value.ACE)) {

                straights.add(ArrayList())
            } else {

                /* Add previous card to list if not already in list */
                if (!straights.last().contains(cards[card - 1])) {

                    straights.last().add(cards[card - 1])
                }

                /* Add current cards to list if previous card did not have the same value property */
                if (cards[card].value.value != cards[card - 1].value.value) {

                    straights.last().add(cards[card])
                }
            }
        }

        /* Sort straights by length and return largest array if size is greater than 5 */
        straights.sortBy {it.size}

        return if (straights.last().size < 5) {

            ArrayList()
        } else {

            straights.last()
        }
    }

    /**
     * Takes an array of array of cards and returns the highest five cards in array
     * Note: If less than 5 cards exist in the largest array then returns empty array
     * @param {Card[][]} cards - cards to look for highest five cards
     * @return {Card[]} Highest five cards in cards variable
     */
    private fun getHighestCards(cards: ArrayList<Card>): ArrayList<Card> {

        if (cards.size < 5) { // if insufficient return empty array

            return ArrayList()
        }

        /* if larger than required remove smallest value cards */
        while (cards.size > 5) {

            cards.removeAt(0)
        }

        return cards
    }

    /**
     * Takes in list of cards and returns a list with no duplicate values
     * @param {Card[]} cards - list of cards to remove duplicates from
     * @return {Card[]} list of cards without duplicates
     */
    private fun removeDuplicateValues(cards: ArrayList<Card>): ArrayList<Card> {

        val tempCards = ArrayList<Card>()

        tempCards.add(cards[0])

        for (card in 1 until cards.size) {

            if (cards[card].value != cards[card - 1].value) {

                tempCards.add(cards[card])
            }
        }

        return tempCards
    }

    /** Returns true if hand is royal flush
     * @ensure hand is at minimum a straight-flush
     * @param {Card[]} hand - list of cards to check if royal flush
     * @return {Boolean} true is hand is royal flush else false
     */
    private fun isRoyalFlush(hand: ArrayList<Card>): Boolean {

        return (hand[0].value == Value.TEN)
    }

    /** Returns sub-score of list of combinations
     * @param {Card[][]} combinations - list of combinations of cards
     * @return {Double} sub-score of all cards in combinations array
     **/
    private fun getSubScoreFromCombinations(combinations: ArrayList<ArrayList<Card>>) {

        this.cardSum = 0
        for (combination in combinations) {

            this.cardSum += this.getSubScoreFromHand(combination)
        }
    }

    /** Returns sub-score of list of cards (hand).
     * Note: also helper function for getSubScoreFromCombinations
     * @param {Card[]} hand - list of cards
     * @return {Double} sub-score of all cards in hand array
     **/
    private fun getSubScoreFromHand(hand: ArrayList<Card>): Int {

        var subScore = 0

        for (card in hand) {

            subScore += card.value.value
        }

        return subScore
    }

    /** Prints formatted combinations found in hand
     * @param {Card[][]} combinations - combinations to print
     * @throws {IllegalStateException} if combination list does not contain 2,3 or 4 cards
     */
    private fun printCombinations(combinations: ArrayList<ArrayList<Card>>) {

        if (combinations.size < 1) {

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

        if (hand.isEmpty()) {

            println("No cards found")
            return
        }

        var string = ""
        for (card in hand) {

            string += "$card "
        }

        println(string)
    }

    override operator fun compareTo(other: Ranking): Int {

        return when {
            this.handRanking == other.handRanking -> {
                when {
                    this.cardSum == other.cardSum -> {
                        when {
                            this.highestCard.value.value == other.highestCard.value.value -> 0

                            this.highestCard.value.value > other.highestCard.value.value -> 1

                            else -> -1
                        }
                    }

                    this.cardSum > other.cardSum -> 1

                    else -> -1
                }
            }

            this.handRanking.ordinal > other.handRanking.ordinal -> 1

            else -> -1
        }
    }
}