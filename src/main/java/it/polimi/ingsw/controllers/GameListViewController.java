package it.polimi.ingsw.controllers;

import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.views.GamesListFrame;

public class GameListViewController extends BaseViewController<GamesListFrame> {
    public GameListViewController() {
        super(new GamesListFrame());
    }
}