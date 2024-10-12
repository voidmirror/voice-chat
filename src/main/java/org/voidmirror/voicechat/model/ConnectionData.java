package org.voidmirror.voicechat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@Getter
@NoArgsConstructor
@ToString
public class ConnectionData {

    private String remoteHost;
    @Setter
    private int remotePort;
    @Setter
    private int localPort;

    /**
     * Check remote host pattern
     * @param remoteHost
     */
    public void setRemoteHost(String remoteHost) {
        if (remoteHost.equals("localhost")) {
            this.remoteHost = "127.0.0.1";
            return;
        }
        if (Pattern.matches("(\\d{1,3}\\.){3}\\d{1,3}", remoteHost)) {
            this.remoteHost = remoteHost;
        } else {
            StringBuilder builder;
            if (remoteHost.contains(":")) {
                builder = new StringBuilder(remoteHost.split(":")[0]);
            } else {
                builder = new StringBuilder(remoteHost);
            }
            while (!Pattern.matches("^\\d.+", builder)) {
                builder.deleteCharAt(0);
            }
            this.remoteHost = builder.toString();
        }
    }
}
