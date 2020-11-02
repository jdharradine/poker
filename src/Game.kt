class Game(private val numPlayers: Int, private val startMoney: Int) {

    /* Deck used for game */
    private var deck = Deck()

    /* Array of players in game */
    private var Players = ArrayList<Player>()

    /* Object storing cards used for community */
    private var communityCards = CommunityCards()

    init {

        /* Initialise game */
        this.initPlayers()
        this.givePlayersCards()

        this.printPlayers()

        /* Begin round */
        initFlop()
        initTurn()
        initRiver()

        println("Community Cards")
        println(this.communityCards.toString())

        var ranking = Ranking(this.communityCards.getList(), this.Players[0].getHand())
    }

    /**
     * Gets three cards from deck for flop and sends them to internal communityCards object
     */
    private fun initFlop() {

        val flop = ArrayList<Card>()
        for (card in 1..3) {

            flop.add(this.deck.getNextCard())
        }

        this.communityCards.receiveFlop(flop)
    }

    /**
     * Gets next card from deck for turn and sends it to internal communityCards object
     */
    private fun initTurn() {

        this.communityCards.receiveTurn(this.deck.getNextCard())
    }

    /**
     * Gets next card from deck for turn and sends it to internal communityCards object
     */
    private fun initRiver() {

        this.communityCards.receiveRiver(this.deck.getNextCard())
    }

    /**
     * Initialises array of players
     */
    private fun initPlayers() {

        for (player in 1..numPlayers) {

            Players.add(Player("player $player",
                    startMoney = startMoney))
        }
    }

    /**
     * Gives players cards for round
     */
    private fun givePlayersCards() {

        for (index in 1..2) {

            for (player in Players) {

                player.giveCard(this.deck.getNextCard())
            }
        }
    }

    /**
     * Prints array of formatted player strings
     */
    fun printPlayers() {

        for (player in this.Players) {

            println(player.toString())
        }
    }

    /**
     * Prints formatted cards that remain in the deck
     */
    fun printRemainingCards() {

        println(this.deck.toString())
    }
}