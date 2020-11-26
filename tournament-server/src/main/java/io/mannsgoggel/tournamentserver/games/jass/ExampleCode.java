package io.mannsgoggel.tournamentserver.games.jass;

public class ExampleCode {

    public static final String EXAMPLE_CODE = "\n" +
            "/**\n" +
            " * Is called in the beginning of a round if you are the first player.\n" +
            " *\n" +
            " * @param {object[]} handCards  The cards in the players hands\n" +
            " * @param {object} gameState    The current state of the game\n" +
            " * @returns {boolean}           If your team mate should choose the playing mode («Gschobe»)\n" +
            " */\n" +
            "function decideShift(handCards, gameState) {\n" +
            "    return false;\n" +
            "}\n" +
            "\n" +
            "/**\n" +
            " * This function chooses a playing mode. Following playing modes are valid:\n" +
            " * \n" +
            " * TOP_DOWN, BOTTOM_UP, TRUMP_HEARTS, TRUMP_SPADES, TRUMP_DIAMONDS, TRUMP_CLUBS\n" +
            " *\n" +
            " * @param {object[]} handCards  The cards in the players hands\n" +
            " * @param {object} gameState    The current state of the game\n" +
            " * @returns {string}            The playing mode for this round\n" +
            " */\n" +
            "function choosePlayingMode(handCards, gameState) {\n" +
            "    return 'TRUMP_HEARTS';\n" +
            "}\n" +
            "\n" +
            "/**\n" +
            " * This function is called whenever your player starts a «Stich». All hand cards can be played.\n" +
            " * \n" +
            " * @param {object[]} handCards  The cards in the players hands\n" +
            " * @param {object} gameState    The current state of the game\n" +
            " * @returns {object}            The card to play as first player\n" +
            " */\n" +
            "function startStich(handCards, gameState) {\n" +
            "    return playCard(handCards, handCards, [], gameState);\n" +
            "}\n" +
            "\n" +
            "/**\n" +
            " * This function is called whenever your player needs to play. Only the playableCards can be played.\n" +
            " * \n" +
            " * @param {object[]} handCards      The cards in the players hands\n" +
            " * @param {object[]} playableCards  The playable cards in the players hand\n" +
            " * @param {object[]} tableStack     The cards laying on the table in correct order\n" +
            " * @param {object} gameState        The current state of the game\n" +
            " * @returns {object}                The card to play\n" +
            " */\n" +
            "function playCard(handCards, playableCards, tableStack, gameState) {\n" +
            "    return playableCards[0];\n" +
            "}\n";
}
