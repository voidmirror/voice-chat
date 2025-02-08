package org.voidmirror.voicechat.frontend;

import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class StageParams {
    private Stage stage;
    private double xShift;  // shift on X relative to main window
    private double yShift;  // shift on Y relative to main window
}
