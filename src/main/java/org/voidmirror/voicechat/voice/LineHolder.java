package org.voidmirror.voicechat.voice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import java.util.HashMap;

public class LineHolder {

    private static LineHolder self;

    private LineHolder() {}

    public static LineHolder getInstance() {
        if (self == null) {
            self = new LineHolder();
        }
        return self;
    }

    private VoiceHolder voiceHolder = new VoiceHolder();

    @Getter
    @Setter
    private float microMuteVolumeHolder = 0f;
    @Getter
    @Setter
    private Boolean microActiveBooleanHolder = Boolean.TRUE;

    public void addFloatControl(FloatControl floatControl, String name) {
        this.voiceHolder.getFloatControlHashMap().put(name, floatControl);
    }

    public void addDataLine(DataLine dataLine, String name) {
        this.voiceHolder.getDataLineHashMap().put(name, dataLine);
    }

    public FloatControl getFloatControl(String name) {
        return this.voiceHolder.getFloatControlHashMap().get(name);
    }

    public DataLine getDataLine(String name) {
        return this.voiceHolder.getDataLineHashMap().get(name);
    }

}

@Getter
@NoArgsConstructor
class VoiceHolder {
    private HashMap<String, FloatControl> floatControlHashMap = new HashMap<>();
    private HashMap<String, DataLine> dataLineHashMap = new HashMap<>();
}