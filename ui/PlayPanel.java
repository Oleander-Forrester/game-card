package ui;
import assetsmanager.SoundManager;


public class PlayPanel extends AbstractMenuPanel {

    public PlayPanel() {

        super("menu-utama-sakura.gif");
        this.title = "MEMORIZING CARD";
        this.subtitle = "MAIN MENU";
        this.menuOptions = new String[]{"Play", "Leaderboards", "Keluar"};
    }

    @Override
    protected void onEnterPressed() {
        SoundManager.playSound("button-click.wav");
        switch (selectedIndex) {
            case 0:
                GameWindow.getInstance().showModeSelection();
                break;
            case 1:
                GameWindow.getInstance().showLeaderboard();
                break;
            case 2:
                System.exit(0);
                break;
        }
    }

    @Override
    protected void onBackPressed() {
//        GameWindow.getInstance().showPlayPanel();
    }
}