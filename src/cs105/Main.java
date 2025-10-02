package cs105;

import java.io.IOException;

import static cs105.OpenAi.*;
import static sbcc.Core.*;

/**
 * Simple text adventure driven by an LLM.
 */
public final class Main {

    // A system prompt is a special instruction that sets the context for the conversation
    static String SYSTEM_PROMPT = """
            You are a game master for a whimsical, silly choose-your-own-adventure game.
            Start a short silly adventure set in a sword-and-sorcery fantasy land.
            """;

    // Kick-off instruction
    static String START_PROMPT = "Begin the adventure with a silly quest.";

    // Appended to each request to remind the model of what each response should look like.
    static String REMINDER = """
            Respond with a short 1-2 sentence paragraph.
            
            Also provide me with 2-3 very simple choices for what to do next (labeled a, b, and c).  
            The choices must be less than 30 characters long.            
            """;

    public static void main(String[] args) throws IOException {
        println("\n🔮🐉⚔️ AI INFINITE ADVENTURES! ⚔️🐉🔮\n\n        Starting the adventure…");


    }

}