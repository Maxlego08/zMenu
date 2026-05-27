package fr.maxlego08.menu.common.network;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;

public class ForceChannelPromise extends DefaultChannelPromise {
    public ForceChannelPromise(Channel channel) {
        super(channel);
    }
}