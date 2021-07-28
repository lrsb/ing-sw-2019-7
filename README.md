<h1 align="center">
<br>
<a href="https://czechgames.com/en/adrenaline/"><img src="https://raw.githubusercontent.com/lrsb/ing-sw-2019-7/master/client/src/main/resources/it/polimi/ingsw/client/controllers/pregame/MainViewController/logo.png" alt="Adrenaline" width="600"></a>
<br>
Adrenaline
</h1>
<h3 align="center">First person shooter board game.</h3>
<h5 align="center">A Java implementation for <a href="https://www11.ceda.polimi.it/schedaincarico/schedaincarico/controller/scheda_pubblica/SchedaPublic.do?&evn_default=evento&c_classe=691149&__pj0=0&__pj1=214fcd028567da8bc874b070cc3683eb">085923 - PROVA FINALE (PROGETTO DI INGEGNERIA DEL SOFTWARE</a>.</h5>

<p align="center">
  <a href="https://travis-ci.com/lrsb/ing-sw-2019-7">
    <img src="https://travis-ci.com/lrsb/ing-sw-2019-7.svg?token=yNsiH96VqTJK1Jj3JizM&branch=master"
         alt="Travis-CI">
  </a>
</p>

<p align="center">
  <a href="#description">Description</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#rules">Rules</a> •
  <a href="#screenshots">Screenshots</a> •
  <a href="#docs">Docs</a> •
  <a href="#license">License</a>
</p>

## Description

In the future, war has left the world in complete destruction and split the people into factions. The factions have decided to stop the endless war and settle their dispute in the arena. A new virtual bloodsport was created. The Adrenaline tournament. Every faction has a champion, every champion has a chance to fight and the chance to win. Will you take the chance of becoming the next champion of the Adrenaline tournament?

## How To Use

To run this application, you may download JARs [here](https://github.com/lrsb/ing-sw-2019-7/releases/tag/1.0) or clone the repository.

```bash
# Run server
java -jar server-1.0-jar-with-dependencies.jar --help

# Run client
java --add-modules=javafx.fxml,javafx.graphics,javafx.controls,javafx.base -jar client-1.0-jar-with-dependencies.jar --help
```
A server is available here: [https://ing-sw-2019-7.herokuapp.com/](https://ing-sw-2019-7.herokuapp.com/)

## Rules

General rules [here](specs/Manuali/adrenaline-rules-en.pdf).

Weapon rules [here](specs/Manuali/adrenaline-rules-weapons-en.pdf).

## Screenshots
### GUI
![gui](specs/gui%20example.png)

### CLI
![](specs/cli%20example.png)

## Docs

Documentation is available [here](https://lrsb.github.io/ing-sw-2019-7/).

## License

MIT
