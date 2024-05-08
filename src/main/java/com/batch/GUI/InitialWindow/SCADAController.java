package com.batch.GUI.InitialWindow;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.LinkedHashMap;
import java.util.Map;

public class SCADAController {

    @FXML
    private AnchorPane BackGround;

    @FXML
    private ImageView MixerVessel_1;

    @FXML
    private ImageView Tank_1;

    @FXML
    private ImageView MixerVessel_2;

    @FXML
    private ImageView Tank_3;

    @FXML
    private ImageView Tank_2;

    @FXML
    private ImageView P_2;

    @FXML
    private ImageView P_3;

    @FXML
    private ImageView P_4;

    @FXML
    private ImageView M_2;

    @FXML
    private ImageView M_1;

    @FXML
    private ImageView V_10;

    @FXML
    private ImageView V_3;

    @FXML
    private ImageView V_17;

    @FXML
    private ImageView V_24;
    
    @FXML
    private ImageView V_27;
    
    @FXML
    private ImageView V_28;
    
    @FXML
    private ImageView V_29;

    @FXML
    private ImageView MixerVessel_11;

    @FXML
    private ImageView V_1;

    @FXML
    private ImageView V_5;

    @FXML
    private ImageView V_19;

    @FXML
    private ImageView V_8;

    @FXML
    private ImageView V_7;

    @FXML
    private ImageView V_21;

    @FXML
    private ImageView V_25;

    @FXML
    private ImageView V_12;

    @FXML
    private ImageView V_11;

    @FXML
    private ImageView V_22;

    @FXML
    private ImageView V_18;

    @FXML
    private ImageView V_16;

    @FXML
    private ImageView V_4;

    @FXML
    private ImageView V_2;

    @FXML
    private ImageView P_1;

    @FXML
    private ImageView V_15;

    @FXML
    private ImageView P_5;

    @FXML
    private ImageView V_26;

    @FXML
    private ImageView V_9;

    @FXML
    private Pane Mixer_1_LevelBar;

    @FXML
    private Label Mixer_1_LevelLabel;

    @FXML
    private Pane Tank_1_LevelBar;

    @FXML
    private Label Tank_1_LevelLabel;

    @FXML
    private Pane Tank_2_LevelBar;

    @FXML
    private Label Tank_2_LevelLabel;

    @FXML
    private Pane Mixer_2_LevelBar;

    @FXML
    private Label Mixer_2_LevelLabel;

    @FXML
    private Pane Tank_3_LevelBar;

    @FXML
    private Label Tank_3_LevelLabel;

    @FXML
    private ImageView V_23;

    @FXML
    private ImageView V_13;

    @FXML
    private ImageView V_14;

    @FXML
    private ImageView V_6;

    @FXML
    private ImageView V_20;

    @FXML
    private Label Mixer_2_R;

    @FXML
    private Label Mixer_1_R;

    @FXML
    private Label Tank_3_R;

    @FXML
    private Label Tank_1_R;

    @FXML
    private Label Tank_2_R;
    
    @FXML
    private Pane Water_Press;

    @FXML
    private Pane Air_Press;

    @FXML
    private Pane Water_Level;

    private static Map<String, ImageView> valves = new LinkedHashMap<String, ImageView>();
    private static Map<String, ImageView> pumps = new LinkedHashMap<String, ImageView>();
    private static Map<String, ImageView> mixers = new LinkedHashMap<String, ImageView>();
    private static Map<String, Pane> levelBars = new LinkedHashMap<String, Pane>();
    private static Map<String, Label> levelLabels = new LinkedHashMap<String, Label>();
    private static Map<String, Label> weightLabels = new LinkedHashMap<String, Label>();
    private static Pane waterLevel;
    private static AnchorPane Parent;
    private static Pane WaterPress;
    private static Pane AirPress;

    @FXML
    void initialize() {
        try {
            valves.put("V01", V_1);
            valves.put("V02", V_2);
            valves.put("V03", V_3);
            valves.put("V04", V_4);
            valves.put("V05", V_5);
            valves.put("V06", V_6);
            valves.put("V07", V_7);
            valves.put("V08", V_8);
            valves.put("V09", V_9);
            valves.put("V10", V_10);
            valves.put("V11", V_11);
            valves.put("V12", V_12);
            valves.put("V13", V_13);
            valves.put("V14", V_14);
            valves.put("V15", V_15);
            valves.put("V16", V_16);
            valves.put("V17", V_17);
            valves.put("V18", V_18);
            valves.put("V19", V_19);
            valves.put("V20", V_20);
            valves.put("V21", V_21);
            valves.put("V22", V_22);
            valves.put("V23", V_23);
            valves.put("V24", V_24);
            valves.put("V25", V_25);
            valves.put("V26", V_26);
            valves.put("V27", V_27);
            valves.put("V28", V_28);
            valves.put("V29", V_29);

            pumps.put("P01", P_1);
            pumps.put("P02", P_2);
            pumps.put("P03", P_3);
            pumps.put("P04", P_4);
            pumps.put("P05", P_5);

            mixers.put("M01", M_1);
            mixers.put("M02", M_2);

            levelBars.put("W01", Mixer_2_LevelBar);
            levelBars.put("W02", Tank_3_LevelBar);
            levelBars.put("W03", Mixer_1_LevelBar);
            levelBars.put("W04", Tank_1_LevelBar);
            levelBars.put("W05", Tank_2_LevelBar);

            levelLabels.put("W01", Mixer_2_LevelLabel);
            levelLabels.put("W02", Tank_3_LevelLabel);
            levelLabels.put("W03", Mixer_1_LevelLabel);
            levelLabels.put("W04", Tank_1_LevelLabel);
            levelLabels.put("W05", Tank_2_LevelLabel);
            
            weightLabels.put("W01", Mixer_1_R);
            weightLabels.put("W02", Tank_3_R);
            weightLabels.put("W03", Mixer_2_R);
            weightLabels.put("W04", Tank_1_R);
            weightLabels.put("W05", Tank_2_R);

            Parent = BackGround;
            
            waterLevel = Water_Level;
            WaterPress = Water_Press;
            AirPress = Air_Press;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Map<String, ImageView> getValves() {
        return valves;
    }

    public static Map<String, ImageView> getPumps() {
        return pumps;
    }

    public static Map<String, ImageView> getMixers() {
        return mixers;
    }

    public static Map<String, Pane> getLevelBars() {
        return levelBars;
    }

    public static Map<String, Label> getLevelLabels() {
        return levelLabels;
    }

    public static Map<String, Label> getWeightLabels() {
        return weightLabels;
    }

    public static void setWeightLabels(Map<String, Label> weightLabels) {
        SCADAController.weightLabels = weightLabels;
    }

    public static AnchorPane getParent() {
        return Parent;
    }

    public static Pane getWaterPress() {
        return WaterPress;
    }

    public static Pane getAirPress() {
        return AirPress;
    }

    public static Pane getWaterLevel() {
        return waterLevel;
    }


}
