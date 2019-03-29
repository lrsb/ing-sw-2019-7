package it.polimi.ingsw.controllers;

import it.polimi.ingsw.library.BaseViewController;
import it.polimi.ingsw.views.SettingsFrame;

public class SettingsViewController extends BaseViewController<SettingsFrame> {
    public SettingsViewController() {
        super(new SettingsFrame());
        getFrame().backButton.addActionListener(e -> getNavigationController().popViewController());
    }
}
