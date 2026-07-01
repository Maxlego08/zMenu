package fr.maxlego08.menu.nms.v1_21_R1.network;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;

public class ForceChannelPromise extends DefaultChannelPromise {
    public ForceChannelPromise(Channel channel) {
        super(channel);
    }
}