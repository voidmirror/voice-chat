package org.voidmirror.voicechat.misc;

import lombok.Getter;
import lombok.Setter;

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

}
