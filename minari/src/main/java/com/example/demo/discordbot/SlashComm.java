package com.example.demo.discordbot;

import java.util.Arrays;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
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
        CommandData command = Commands.slash("channel", "A command dedicated to channels")
            .addSubcommandGroups(
                new SubcommandGroupData("edit", "Edits a channel")
                    .addSubcommands(
                        new SubcommandData("allow", "Allows a permission to a user for a channel")
                            .addOptions(
                                new OptionData(OptionType.CHANNEL, "channel", "The channel to modify", true),
                                new OptionData(OptionType.USER, "user", "The user which permissions should be changed", true),
                                new OptionData(OptionType.INTEGER, "permission", "The permission to allow", true)
                                    .addChoice("manage", 0)
                                    .addChoice("show", 1)
                            )
                    )
            );

        // 서버 전용 커맨드 등록
        jda.getGuildById("YOUR_GUILD_ID") // 서버 ID로 교체
            .updateCommands()
            .addCommands(command)
            .queue();
    }
}
