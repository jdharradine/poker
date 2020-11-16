import java.lang.IllegalArgumentException

class Ranking(private val communityCards: ArrayList<Card>, private val hand: ArrayList<Card>, val playerIdx: Int): Comparable<Ranking> {

    /**
     * handRanking stores the hand rank category of the hand
     * @type {HandRanking}
     */
    var handRanking = HandRanking.HIGH_CARD

    /**
     * Score is a metric that can be used to directly compare hands
     * It determines the total card value of the significant cards in hand
     * @type {Int}
     */
    var score: Int = 0

    /**
     * Stores all the cards to be ranked
     * @type {Card[]}
     */
    private val combinedCards: ArrayList<Card> = ArrayList()

    /** Stores the combinations found in combined cards
     * @type {Card[][]}
     */
    private var combinations = ArrayList<ArrayList<Card>>()

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
        this.score = this.extractScore()

        println()
        println(this.handRanking.name)
        println("Score: " + String.format("%d", this.score))
    }

    /** Returns list of combinations of cards
     * @return {Card[][]} list of combinations of cards in this hand
     */
    private fun initCombinations() {
        var index = 0
        this.combinations.add(ArrayList())

        for (card in 1 until this.combinedCards.size) {

            if (this.combinedCards[card].value == this.combinedCards[card - 1].value) {

                this.combinations[index].add(this.combinedCards[card])

                if (!this.combinations[index].contains(this.combinedCards[card -1])) {

                    this.combinations[index].add(this.combinedCards[card - 1])
                }
            } else if (this.combinations[index].size != 0){

                index++
                this.combinations.add(ArrayList())
            }
        }

        /* Clean up empty combinations and return */
        this.cleanCombinations()
    }

    /** Returns cleaned list of card combination. Also sorts combinations by ranking
     * and assures only 5 cards is in returned array
     * @param {Card[][]} combinations - list of combinations to clean
     * @return {Card[][]} cleaned list of combinations
     */
    private fun cleanCombinations() {

        val tempCombinations = ArrayList<ArrayList<Card>>()

        /* Remove empty combinations */
        for (combination in this.combinations) {

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

        this.combinations = tempCombinations
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
        this.initCombinations()
        this.printCombinations(this.combinations)

        /* extract ranking from combinations */
        this.getScoreFromCombinations(this.combinations)

        /* Check combinations of flush and straight */
        this.getScoreFromStraightAndFlush()

        return score
    }

    /** Sets score and handRanking to best handRanking found in combinations
     * Note: if no combination ranking can be found in combinations
     * score and handRanking are left unchanged
     * @param {Card[][]} combinations - list of combinations to check
     */
    private fun getScoreFromCombinations(combinations: ArrayList<ArrayList<Card>>) {

        if (combinations.size == 1) {

            when (combinations[0].size) {


                2 -> this.handRanking = HandRanking.ONE_PAIR

                3 -> this.handRanking = HandRanking.THREE_OF_A_KIND

                4 -> this.handRanking = HandRanking.FOUR_OF_A_KIND

                else -> throw IllegalStateException("Combination can't consist of more cards then there are suits")
            }

            /* Sub score consists of the value of the highest pair */
            this.score = combinations.last().first().value.value

        } else if (combinations.size == 2) {

            if (combinations[0].size == 2 && combinations[1].size == 2) {

                this.handRanking = HandRanking.TWO_PAIR
            } else if ((combinations[0].size == 3 && combinations[1].size == 2) ||
                    (combinations[1].size == 3 && combinations[0].size == 2)) {

                this.handRanking = HandRanking.FULL_HOUSE
            }
        }
    }

    /** Sets score and handRanking to best flush, straight combination found in hand
     * Note: if no other hand ranking can be found in combinations
     * score and handRanking are left unchanged
     */
    private fun getScoreFromStraightAndFlush() {

        val largestSuitSet: ArrayList<Card> = this.getLargestSuitSet(this.combinedCards)

        /* Check if flush is possible */
        if (largestSuitSet.isNotEmpty()) {

            /* Check if hand is also a flush */
            val straightFlush: ArrayList<Card> = this.getLargestStraight(largestSuitSet)
            if (straightFlush.isNotEmpty()) {

                /* Check if hand is also royal flush */
                if (this.isRoyalFlush(straightFlush)) {

                    setScore(straightFlush, HandRanking.ROYAL_FLUSH)
                } else { // Hand is straight flush

                    setScore(straightFlush, HandRanking.STRAIGHT_FLUSH)
                }
                this.printHand("Largest Straight Flush", straightFlush)
            } else { // Hand may be flush

                val largestFlush = this.getHighestCards(largestSuitSet)

                if (largestFlush.isNotEmpty()) {

                    this.printHand("Largest Flush", largestFlush)
                    setScore(largestFlush, HandRanking.FLUSH)
                }
            }
            return
        }

        /* Check hand for simple straight */
        val largestStraight: ArrayList<Card> = this.getLargestStraight(this.combinedCards)
        if (largestStraight.isNotEmpty()) {

            setScore(largestStraight, HandRanking.STRAIGHT)

            this.printHand("Largest Straight", largestStraight)
        }
     }

    /**
     * Takes in the highest value hand found and the associated handRanking and sets score properties of object
     * @param {Card[][]} bestHand - highest ranked hand found
     * @param {HandRanking} handRanking - hand ranking of bestHand
     */
    private fun setScore(bestHand: ArrayList<Card>, handRanking: HandRanking) {

        this.handRanking = handRanking

        this.score = getScoreFromHand(bestHand)

        /* If hand is straight (or straight flush) starting at ACE apply offset to account for ACE's default value */
        if ((handRanking != HandRanking.FLUSH) &&
                bestHand.last().value == Value.ACE &&
                bestHand.first().value == Value.TWO) {

            this.score -= Value.ACE.value
            this.score += 1
        }
    }

    /**
     * Returns subset of cards consisting of cards that have the most frequent suit
     * There must be at-least 5 cards of the most frequent suit for something to be returned otherwise empty list
     * @param {Card[]} cards - list of cards to get flush
     * @return {Card[]} list of cards of most frequent suit (if there are 5 or more)
     */
    private fun getLargestSuitSet(cards: ArrayList<Card>): ArrayList<Card> {
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

        return if (suitBins.last().size < 5) {

            ArrayList()
        } else {

            suitBins.last()
        }
    }

    /**
     * Returns subset of cards consisting of the largest value ordered cards of size 5
     * There must be at-least 5 cards in order for something to be returned otherwise empty list
     * @param {Card[]} cards - list of cards to get flush
     * @return {Card[]} list of cards of most frequent suit (if there are 5 or more)
     */
    private fun getLargestStraight(cards: ArrayList<Card>): ArrayList<Card> {

        val straights: ArrayList<ArrayList<Card>> = ArrayList()
        straights.add(ArrayList())

        /* If there is an ace at the beginning of the cards list add it to the end */
        if (cards[0].value == Value.ACE) {

            cards.add(cards[0])
        }

        /* Extract lists of ordered cards */
        for (card in 1 until cards.size) {

            /* If card is not ordered add next ordered cards to next list */
            if (cards[card].value.value - 1 != cards[card - 1].value.value &&
                    !(cards[card].value == Value.TWO && cards[card - 1].value == Value.ACE) &&
                    cards[card].value != cards[card - 1].value) {

                straights.add(ArrayList())
            } else {

                /* Add previous card to list if not already in list */
                if (!straights.last().contains(cards[card - 1]) &&
                        cards[card].value != cards[card - 1].value) {

                    straights.last().add(cards[card - 1])
                }

                /* Add current card to list if value already not in list */
                if (cards[card].value != cards[card - 1].value) {

                    straights.last().add(cards[card])
                }
            }
        }

        /* Sort straights by length and return largest array if size is greater than 5 */
        straights.sortBy {it.size}

        return this.getHighestCards(straights.last())
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

    /** Returns true if hand is royal flush
     * @ensure hand is at minimum a straight-flush
     * @param {Card[]} hand - list of cards to check if royal flush
     * @return {Boolean} true is hand is royal flush else false
     */
    private fun isRoyalFlush(hand: ArrayList<Card>): Boolean {

        return (hand[0].value == Value.TEN)
    }

    /** Returns sub-score of list of cards (hand).
     * Note: also helper function for getSubScoreFromCombinations
     * @param {Card[]} hand - list of cards
     * @return {Double} sub-score of all cards in hand array
     **/
    private fun getScoreFromHand(hand: ArrayList<Card>): Int {

        hand.sortBy {it.value.value}

        return hand.last().value.value
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
                else -> throw IllegalStateException("Invalid combination size of ${combination.size}")
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
            /* HandRankings match therefore need to compare other properties */
            this.handRanking == other.handRanking -> {

                /* If hand ranking depends on combinations determine best combinations */
                if (this.isCombinationHandRanking(this.handRanking) &&
                        compareCombinations(other) != 0) {

                    return compareCombinations(other)
                }

                when {
                    /* Score metrics match therefore need to compare highest cards */
                    this.score == other.score -> compareHighestCard(other)

                    this.score > other.score -> 1

                    else -> -1
                }
            }

            this.handRanking.ordinal > other.handRanking.ordinal -> 1

            else -> -1
        }
    }

    /** Takes other Ranking object and determines whether or not this object has the highest ranked card
     * @param {Ranking} other - other ranking used for comparison
     * @return {Int} - 0 if no difference found, 1 if this has highest card, else -1
     */
    private fun compareHighestCard(other: Ranking): Int {

        for (i in this.combinedCards.lastIndex downTo 0) {

            if (this.combinedCards[i].value > other.combinedCards[i].value) {

                return 1
            } else if (this.combinedCards[i].value < other.combinedCards[i].value) {

                return -1
            }
        }

        /* If all cards are equal in value than no highest card winner can be determined */
        return 0
    }

    /**
     * Takes HandRanking object and determines if it is a combination category handRanking
     * @param {HandRanking} handRanking - handRanking to check for combination category
     * @return {Boolean} - true if combination type handRanking, else false
     */
    private fun isCombinationHandRanking(handRanking: HandRanking): Boolean {

        return (handRanking == HandRanking.ONE_PAIR ||
                handRanking == HandRanking.TWO_PAIR ||
                handRanking == HandRanking.THREE_OF_A_KIND ||
                handRanking == HandRanking.FULL_HOUSE ||
                handRanking == HandRanking.FOUR_OF_A_KIND)
    }

    /** Takes other Ranking object and determines whether or not this object has the better set of combinations
     * @param {Ranking} other - other ranking used for comparison
     * @return {Int} - 0 if no difference found, 1 if this has best combinations, else -1
     */
    private fun compareCombinations(other: Ranking): Int {

        for (i in this.combinations.lastIndex downTo 0) {

            if (this.combinations[i].first().value > other.combinations[i].first().value) {

                return 1
            } else if (this.combinations[i].first().value < other.combinations[i].first().value) {

                return -1
            }
        }

        /* If all cards are equal in value than no highest card winner can be determined */
        return 0
    }
}