package com.example.demo.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlashComm extends ListenerAdapter {

    private final JDA jda;

    // JDA 인스턴스를 받는 생성자
    public SlashComm(JDA jda) {
        this.jda = jda;
        // 슬래시 커맨드를 설정합니다.
        setupSlashCommand();
    }

    private void setupSlashCommand() {
        CommandData command = Commands.slash("channel", "채널 관련 명령어")
                .addSubcommands(
                        new SubcommandData("edit", "채널 수정")
                                .addOptions(
                                        new OptionData(OptionType.CHANNEL, "channel", "수정할 채널", true),
                                        new OptionData(OptionType.USER, "user", "권한을 변경할 사용자", true),
                                        new OptionData(OptionType.INTEGER, "permission", "허용할 권한", true)
                                                .addChoice("관리", 0)
                                                .addChoice("보기", 1)
                                ),
                        new SubcommandData("recommend", "식사 메뉴 추천")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "cuisine", "원하는 방식을 선택하세요", true)
                                                .addChoice("선택", "선택")
                                                .addChoice("랜덤", "랜덤")
                                )
                );

        jda.upsertCommand(command).queue();
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("channel")) {
            String subcommandGroup = event.getSubcommandGroup();
            String subcommandName = event.getSubcommandName();

            if (subcommandGroup != null && subcommandName != null) {
                switch (subcommandGroup) {
                    default:
                        event.reply("Unknown subcommand group: " + subcommandGroup).queue();
                        break;
                }
            } else {
                event.reply("Subcommand group or name is null.").queue();
            }
        }

        if (event.getName().equals("channel")) {
            String subcommandGroup = event.getSubcommandGroup();
            String subcommandName = event.getSubcommandName();

            if (subcommandGroup != null && subcommandName != null) {
                switch (subcommandGroup) {
                    case "recommend":
                        if (subcommandName.equals("recommend")) {
                            // "recommend" 서브커맨드에 대한 처리
                            String cuisine = event.getOption("cuisine").getAsString();
                            if (cuisine.equalsIgnoreCase("랜덤")) {
                                // 랜덤한 음식 추천을 위한 로직 추가
                                // 추천된 음식을 메시지로 전송
                            } else if (cuisine.equalsIgnoreCase("선택")) {
                                // 사용자가 선택한 방식에 따라 해당하는 버튼을 생성하고 메시지로 전송
                                Channel channel = event.getChannel();
                                if (channel instanceof TextChannel) {
                                    TextChannel textChannel = (TextChannel) channel;

                                    // 여기서 사용자가 선택한 음식 종류에 따라 랜덤 음식을 추천하고 메시지로 전송하는 로직을 추가하면 됩니다.
                                    String recommendedFood = recommendFood(cuisine);
                                    textChannel.sendMessage("당신을 위한 추천 메뉴: " + recommendedFood).queue();
                                } else {
                                    event.reply("You can only use this command in a text channel.").queue();
                                }
                            } else {
                                // 유효하지 않은 선택일 경우 처리
                            }
                        }
                        break;
                    default:
                        event.reply("Unknown subcommand group: " + subcommandGroup).queue();
                        break;
                }
            } else {
                event.reply("Subcommand group or name is null.").queue();
            }
        }
    }}
