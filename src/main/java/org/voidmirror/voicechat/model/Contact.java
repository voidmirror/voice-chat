package org.voidmirror.voicechat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Contact {

    private UUID uid;
    private String name;
    private String displayName;

    // TODO: String staticIp, PublicKey publicKey

}
