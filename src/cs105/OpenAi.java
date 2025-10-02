package cs105;

import com.openai.client.*;
import com.openai.client.okhttp.*;
import com.openai.models.chat.completions.*;

import java.util.*;

public class OpenAi {
    /**
     * Creates a conversation, which is a list of chat completion messages.
     * The conversation is initialized with a system prompt and the user's first request.
     *
     * @param systemPrompt The initial system message to guide the conversation
     * @param firstRequest The first message from the user to kickstart the conversation
     * @return An ArrayList containing the initial conversation messages
     */
    public static List<ChatCompletionMessageParam> createConversation(String systemPrompt, String firstRequest) {

        var systemMsgParam = ChatCompletionMessageParam.ofSystem(ChatCompletionSystemMessageParam.builder().content(systemPrompt).build());
        var userMsgParam = ChatCompletionMessageParam.ofUser(ChatCompletionUserMessageParam.builder().content(firstRequest).build());

        var conversation = new ArrayList<ChatCompletionMessageParam>();  // This list will keep track of user and ai responses
        conversation.add(systemMsgParam);
        conversation.add(userMsgParam);

        return conversation;
    }


    /**
     * Creates and configures an OpenAI client to connect to the specified URL.
     *
     * @param URL The base URL of the AI server to connect to
     * @return A configured OpenAI client
     */
    public static OpenAIClient getOllamaClient(String URL) {
        var client = OpenAIOkHttpClient.builder()
                .baseUrl(URL)
                .apiKey("ollama")
                .putHeader("Authorization", "Bearer ollama-local")
                .build();

        return client;
    }


    /**
     * Gets the AI's response to the current state of the conversation.
     *
     * @param client       The OpenAI client used to communicate with the language model
     * @param aiModel      The name of the AI model to use
     * @param conversation The current conversation history including all past exchanges
     * @return The text response from the AI continuing the adventure
     */
    public static String getAiResponse(OpenAIClient client, String aiModel, List<ChatCompletionMessageParam> conversation) {
        // Create the next request params which include the entire conversation.
        var requestParams = ChatCompletionCreateParams.builder()
                .model(aiModel)
                .messages(conversation)
                .build();

        // Get the model's response, and add it to the conversation
        var chatCompletion = client.chat().completions().create(requestParams);
        conversation.add(getModelMsgParam(chatCompletion));
        pruneConversation(conversation, 128);

        // Return the AI's current response as text.
        return getAiResponseText(chatCompletion);
    }


    /**
     * Extracts the model's first message from a chat completion and formats it as a message parameter.
     *
     * @param completion The chat completion response from the AI
     * @return A ChatCompletionMessageParam containing the assistant's message
     */
    public static ChatCompletionMessageParam getModelMsgParam(ChatCompletion completion) {
        var msg = completion.choices().getFirst().message();
        var modelMsgParam = ChatCompletionMessageParam.ofAssistant(msg.toParam());
        return modelMsgParam;
    }


    /**
     * Removes older messages from the conversation history to keep it within the maximum size limit.
     * This prevents the context from becoming too large for the model.
     *
     * @param conversation The conversation history to prune
     * @param MAX_HISTORY  The maximum number of messages to keep in the history
     */
    public static void pruneConversation(List<ChatCompletionMessageParam> conversation, int MAX_HISTORY) {
        if (conversation.size() > MAX_HISTORY)
            conversation.subList(0, conversation.size() - MAX_HISTORY).clear();
    }


    /**
     * Extracts the text content from the AI's response.
     *
     * @param completion The chat completion response from the AI
     * @return The text content of the AI's message
     */
    public static String getAiResponseText(ChatCompletion completion) {
        return completion.choices().getFirst().message().content().get();
    }


    /**
     * Creates a user message parameter for the conversation.
     *
     * @param userResponse The user's input text (this should end with the REMINDER).
     * @return A ChatCompletionMessageParam containing the message
     */
    public static ChatCompletionMessageParam buildUserMsgParam(String userResponse) {
        return ChatCompletionMessageParam.ofUser(
                ChatCompletionUserMessageParam.builder()
                        .content(userResponse)
                        .build()
        );
    }
}
