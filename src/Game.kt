class Game(private val numPlayers: Int, private val startMoney: Int) {

    /* Deck used for game */
    private var deck = Deck()

    /* Array of players in game */
    private var players = ArrayList<Player>()

    /* Object storing cards used for community */
    private var communityCards = CommunityCards()

    init {

        /* Initialise game */
        this.initPlayers()
        this.givePlayersCards()

//        this.printPlayers()

        /* Begin round */
        initFlop()
        initTurn()
        initRiver()

        println("Community Cards")
        println(this.communityCards.toString())

        val rankings: ArrayList<Ranking> = ArrayList()

        for (playerIdx in 0 until this.players.size) {

            println("----------------")
            println(this.players[playerIdx])
            rankings.add(Ranking(this.communityCards.getList(), this.players[playerIdx].getHand(), playerIdx))
        }

        rankings.sort()
        println("----------------")
        println("${players[rankings.last().playerIdx].name} wins")
        println("----------------")
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

            players.add(Player("player $player",
                    startMoney = startMoney))
        }
    }

    /**
     * Gives players cards for round
     */
    private fun givePlayersCards() {

        for (index in 1..2) {

            for (player in players) {

                player.giveCard(this.deck.getNextCard())
            }
        }
    }

    /**
     * Prints array of formatted player strings
     */
    fun printPlayers() {

        for (player in this.players) {

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