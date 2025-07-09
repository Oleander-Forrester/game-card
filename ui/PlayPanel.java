package ui;

public class PlayPanel extends AbstractMenuPanel {

    public PlayPanel() {
        super("menu-utama-sakura.gif");
        this.title = "MEMORIZE CARD";
        this.subtitle = "MAIN MENU";
        this.menuOptions = new String[]{"Play", "High Scores", "Keluar"};
    }

    @Override
    protected void onEnterPressed() {
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