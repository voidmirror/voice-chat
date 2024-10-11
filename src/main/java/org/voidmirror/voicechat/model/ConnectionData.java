package org.voidmirror.voicechat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.regex.Pattern;

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
     * If remoteHost is not in proper pattern
     * @param remoteHost
     */
    public void setRemoteHost(String remoteHost) {
        System.out.println("### Incoming remoteHost: " + remoteHost);
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
            this.remoteHost = builder.toString();;
        }
        System.out.println("### Resulted remoteHost: " + this.remoteHost);

    }
}
