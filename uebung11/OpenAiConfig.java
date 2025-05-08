package ch.zhaw.freelancer4u.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Autowired
    OpenAiChatModel chatModel;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(chatModel).defaultAdvisors(new MessageChatMemoryAdvisor(MessageWindowChatMemory.builder().build()), new SimpleLoggerAdvisor()).build();
    }
}