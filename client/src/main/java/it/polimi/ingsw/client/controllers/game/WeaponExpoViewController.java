package it.polimi.ingsw.client.controllers.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.common.models.Weapon;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WeaponExpoViewController extends BaseViewController {
    private JPanel panel;
    private JFXPanel jfxPanel;
    private JButton infoButton;

    public WeaponExpoViewController(@Nullable NavigationController navigationController, @NotNull Object... params) {
        super(((Weapon) params[0]).name(), 800, 500, navigationController);
        $$$setupUI$$$();
        setContentPane(panel);
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            try {
                var front = new ImageView(SwingFXUtils.toFXImage(((Weapon) params[0]).getFrontImage(), null));
                var back = new ImageView(SwingFXUtils.toFXImage(((Weapon) params[0]).getBackImage(), null));
                var stackPane = new StackPane();
                stackPane.getChildren().addAll(front, back);
                var scene = new Scene(stackPane, 450, 500, true, SceneAntialiasing.BALANCED);
                scene.setCamera(new PerspectiveCamera());
                jfxPanel.setScene(scene);
                back.setVisible(false);
                var rotator = new RotateTransition(Duration.millis(2500), front);
                rotator.setAxis(Rotate.Y_AXIS);
                rotator.setFromAngle(0);
                rotator.setToAngle(90);
                rotator.setInterpolator(Interpolator.LINEAR);
                rotator.setCycleCount(1);
                rotator.setOnFinished(e -> createRotator(front, back));
                rotator.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        infoButton.addActionListener(e -> {
            try {
                var info = Utils.getStrings("cli", "weapons_details", ((Weapon) params[0]).name().toLowerCase()).get("fire_description").getAsString();
                JOptionPane.showMessageDialog(this, "<html><body><p style='width: 200px;'>" + info.replaceAll("\n", " ") + "</p></body></html>", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void createRotator(@NotNull Node front, @NotNull Node back) {
        var rotator1 = new RotateTransition(Duration.millis(5000), back);
        rotator1.setAxis(Rotate.Y_AXIS);
        rotator1.setFromAngle(90);
        rotator1.setToAngle(270);
        rotator1.setInterpolator(Interpolator.LINEAR);
        rotator1.setCycleCount(1);
        rotator1.setOnFinished(e -> {
            var rotator2 = new RotateTransition(Duration.millis(5000), front);
            rotator2.setAxis(Rotate.Y_AXIS);
            rotator2.setFromAngle(-90);
            rotator2.setToAngle(90);
            rotator2.setInterpolator(Interpolator.LINEAR);
            rotator2.setCycleCount(1);
            rotator2.setOnFinished(f -> createRotator(front, back));
            back.setVisible(false);
            front.setVisible(true);
            rotator2.play();
        });
        front.setVisible(false);
        back.setVisible(true);
        rotator1.play();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        jfxPanel = new JFXPanel();
        panel.add(jfxPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        infoButton = new JButton();
        infoButton.setText("Info");
        panel.add(infoButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
