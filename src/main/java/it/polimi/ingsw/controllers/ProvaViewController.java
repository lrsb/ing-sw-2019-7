package it.polimi.ingsw.controllers;

import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.controllers.base.NavigationController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ProvaViewController extends BaseViewController {
    private JPanel panel;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private String name;

    public ProvaViewController(@NotNull NavigationController navigationController) {
        super(500, 300, navigationController);
        setContentPane(panel);
    }

    @Override
    protected void onShow() {
        radioButton2.setText(name);
    }

    public void setName1(String name) {
        this.name = name;
    }
}
