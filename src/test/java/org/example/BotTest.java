package org.example;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BotTest {




    @Test
    void quit_whenMessageReceivedIsNull_thenChangeNothing(){
        //GIVEN
        Bot bot = new Bot();
        bot.setState(State.QUIZ);
        State expected = State.QUIZ;
        String messageReceived = null;
        //WHEN
        bot.quit(messageReceived);
        State actual = bot.getState();

        //THEN
        assertEquals(expected,actual);
    }

    @Test
    void quit_whenMessageDoesNotStartWithQuit_thenChangeNothing(){
        //GIVEN
        Bot bot = new Bot();
        bot.setState(State.QUIZ);
        State expected = State.QUIZ;
        String messageReceived = "%$41hallodri";

        //WHEN
        bot.quit(messageReceived);
        State actual = bot.getState();

        //THEN
        assertEquals(expected,actual);
    }

    @Test
    void quit_whenMessageStartsWithQuit_thenStateChangesToDefault(){
        Bot bot = new Bot();
        bot.setState(State.QUIZ);
        State expected = State.DEFAULT;
        String messageReceived = "quit";

        //WHEN
        bot.quit(messageReceived);
        State actual = bot.getState();

        //THEN
        assertEquals(expected,actual);

    }
}