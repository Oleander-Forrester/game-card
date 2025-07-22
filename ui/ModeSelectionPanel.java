package ui;
import assetsmanager.SoundManager;


public class ModeSelectionPanel extends AbstractMenuPanel {

    public ModeSelectionPanel() {
        super("menu-utama-sakura.gif");
        this.title = "MEMORIZING CARD";
        this.subtitle = "PILIH MODE";
        SoundManager.playSound("button-click.wav");
        this.menuOptions = new String[]{"1 Player", "2 Players", "Kembali"};
    }

    @Override
    protected void onEnterPressed() {
        switch (selectedIndex) {
            case 0:
                GameWindow.getInstance().showDifficultySelection(1);
                break;
            case 1:
                GameWindow.getInstance().showDifficultySelection(2);
                break;
            case 2:
                GameWindow.getInstance().showPlayPanel();
                break;
        }
    }

    @Override
    protected void onBackPressed() {
        GameWindow.getInstance().showPlayPanel();
    }
}