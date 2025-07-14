package ui;
import assetsmanager.SoundManager;

public class DifficultySelectionPanel extends AbstractMenuPanel {

    private final int mode;

    public DifficultySelectionPanel(int mode) {
        super("menu-utama-sakura.gif");
        this.title = "MEMORIZE CARD";
        this.subtitle = "PILIH TINGKAT KESULITAN";
        this.mode = mode;
        SoundManager.playSound("button-click.wav");
        this.menuOptions = new String[]{"Easy", "Medium", "Hard", "Kembali"};
    }

    @Override
    protected void onEnterPressed() {
        switch (selectedIndex) {
            case 0:
                GameWindow.getInstance().showPlayerNameInput(this.mode, 0);
                break;
            case 1:
                GameWindow.getInstance().showPlayerNameInput(this.mode, 1);
                break;
            case 2:
                GameWindow.getInstance().showPlayerNameInput(this.mode, 2);
                break;
            case 3:
                GameWindow.getInstance().showModeSelection();
                break;
        }
    }

    @Override
    protected void onBackPressed() {
        GameWindow.getInstance().showModeSelection();
    }
}