package org.voidmirror.voicechat.misc;

import lombok.Getter;
import lombok.Setter;
import org.voidmirror.voicechat.model.ConnectionData;

public class RuntimeConfig {

    private static RuntimeConfig self;

    private RuntimeConfig() {}

    public static RuntimeConfig getInstance() {
        if (self == null) {
            self = new RuntimeConfig();
        }
        return self;
    }

    @Getter
    @Setter
    private boolean isMicroActive = true;
    @Getter
    @Setter
    private boolean isWhileNeeded = true;
    @Getter
    @Setter
    private boolean microphoneFlushed = false;

    @Getter
    private final int servicePort = 9035;
    @Getter
    private final int voicePort = 9034;
    @Getter
    private final ConnectionData connectionData = new ConnectionData();
    @Getter
    @Setter
    private String remoteHost;

}
