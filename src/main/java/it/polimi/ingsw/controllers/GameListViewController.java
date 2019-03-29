package it.polimi.ingsw.controllers;

import it.polimi.ingsw.library.BaseViewController;
import it.polimi.ingsw.views.GamesListFrame;

public class GameListViewController extends BaseViewController<GamesListFrame> {
    public GameListViewController() {
        super(new GamesListFrame());
    }
}