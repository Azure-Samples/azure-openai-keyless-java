package com.example.app;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.identity.DefaultAzureCredentialBuilder;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample demonstrates how to create a client with keyless authentication.
 */
public class Example {
    /**
     * Runs the sample algorithm and demonstrates how to create a client with keyless authentication.
     *
     * @param args Unused. Arguments to the program.
     */
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String azureOpenaiService = dotenv.get("AZURE_OPENAI_SERVICE");
        String deploymentOrModelId = dotenv.get("AZURE_OPENAI_GPT_DEPLOYMENT");
        if (azureOpenaiService == null || deploymentOrModelId == null) {
            System.out.println("AZURE_OPENAI_SERVICE or AZURE_OPENAI_GPT_MODEL environment variables are empty. See README.");
            System.exit(1);
        }

        String endpoint = "https://" + azureOpenaiService + ".openai.azure.com/";
        OpenAIClientBuilder builder = new OpenAIClientBuilder()
            .endpoint(endpoint)
            .credential(new DefaultAzureCredentialBuilder().build());

        OpenAIClient client = builder.buildClient();

        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage("You are a helpful assistant. You will talk like a pirate."));
        chatMessages.add(new ChatRequestUserMessage("Can you help me?"));
        chatMessages.add(new ChatRequestAssistantMessage("Of course, me hearty! What can I do for ye?"));
        chatMessages.add(new ChatRequestUserMessage("What's the best way to train a parrot?"));

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        System.out.printf("Model ID=%s is created at %s.%n", chatCompletions.getId(), chatCompletions.getCreatedAt());
        for (ChatChoice choice : chatCompletions.getChoices()) {
            ChatResponseMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            System.out.println("Message:");
            System.out.println(message.getContent());
        }

        System.out.println();
        CompletionsUsage usage = chatCompletions.getUsage();
        System.out.printf("Usage: number of prompt token is %d, "
                + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
            usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
    }
}



