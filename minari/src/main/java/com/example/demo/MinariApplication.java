package com.example.demo;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.demo.discordbot.DiscordListener;
import com.example.demo.discordbot.SlashComm;

import javax.security.auth.login.LoginException;

@SpringBootApplication
public class MinariApplication {
    
	 public static void main(String[] args) throws LoginException {

	        ApplicationContext context = SpringApplication.run(MinariApplication.class, args);

	        DiscordBotToken discordBotTokenEntity = context.getBean(DiscordBotToken.class);
	        String discordBotToken = discordBotTokenEntity.getDiscordBotToken();

	        JDA jda = JDABuilder.createDefault(discordBotToken)
	                .setActivity(Activity.playing("미나리 굽굽"))
	                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
	                .addEventListeners(new DiscordListener())
	                .build();
	        
	        // JDA 인스턴스를 사용하여 SlashComm 인스턴스 생성 및 등록
	        SlashComm slashComm = new SlashComm(jda);
	        jda.addEventListener(slashComm);
	    }
}
