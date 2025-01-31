package com.jaoafa.chatwatcher.event;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class AutoDisconnect extends ListenerAdapter {
    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null || event.getChannelLeft() == null) return; // 退出以外は除外
        if (event.getGuild().getSelfMember().getVoiceState() == null ||
                event.getGuild().getSelfMember().getVoiceState().getChannel() == null) {
            return; // 自身がどのVCにも参加していない
        }
        if (event.getGuild().getSelfMember().getVoiceState().getChannel().getIdLong() != event.getChannelLeft().getIdLong()) {
            return; // 退出されたチャンネルが自身のいるVCと異なる
        }

        // VCに残ったユーザーが全員Bot、または誰もいなくなった
        boolean existsUser = event
                .getChannelLeft()
                .getMembers()
                .stream()
                .anyMatch(member -> !member.getUser().isBot()); // Bot以外がいるかどうか

        if (existsUser) {
            return;
        }
        event.getGuild().getAudioManager().closeAudioConnection();
    }
}