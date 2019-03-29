package it.polimi.ingsw.controllers;

import it.polimi.ingsw.library.BaseViewController;
import it.polimi.ingsw.views.GameFrame;

public class GameViewController extends BaseViewController<GameFrame> {
    public GameViewController() {
        super(new GameFrame());
    }
}