package org.voidmirror.voicechat.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSyncDto {
    private String user;
    private Long time;

    @JsonSetter
    public void setUser(String user) {
        this.user = user;
    }

    @JsonSetter
    public void setTime(long time) {
        this.time = time;
    }
}
