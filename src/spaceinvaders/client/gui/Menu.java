package spaceinvaders.client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import spaceinvaders.client.ClientConfig;

/**
 * Game menu.
 *
 * <p>This is the first UI element encountered by the user. It can be used to CONFIGure the game.
 */
public class Menu implements GraphicalObject {
  public static final ClientConfig CONFIG = ClientConfig.getInstance();

  private JFrame menuFrame;
  private JButton playBtn;
  private JButton quitBtn;
  private JLabel serverLbl;
  private JLabel userNameLbl;
  private JTextField serverAddrTxt;
  private JTextField serverPortTxt;
  private JTextField userNameTxt;
  private JSpinner noOfPlayersSpn;

  /**
   * Construct a new menu with the defaults already entered.
   */
  public Menu() {
    menuFrame = new JFrame("SpaceInvaders - Menu");
    menuFrame.setSize(500,500);
    menuFrame.setResizable(false);
    menuFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    menuFrame.setLayout(new FlowLayout());

    serverLbl = new JLabel("Game server");
    menuFrame.add(serverLbl);

    serverAddrTxt = new JTextField(CONFIG.getServerAddr());
    menuFrame.add(serverAddrTxt);

    serverPortTxt = new JTextField(Integer.valueOf(CONFIG.getServerPort()).toString());
    menuFrame.add(serverPortTxt);

    userNameLbl = new JLabel("User name");
    menuFrame.add(userNameLbl);

    userNameTxt = new JTextField(CONFIG.getUserName(),10);
    menuFrame.add(userNameTxt);

    noOfPlayersSpn = new JSpinner(new SpinnerNumberModel(1,1,3,1));
    // Disable spinner's text editing feature
    ((JSpinner.DefaultEditor) noOfPlayersSpn.getEditor()).getTextField().setEditable(false);
    menuFrame.add(noOfPlayersSpn);

    playBtn = new JButton("Play");
    menuFrame.add(playBtn);

    quitBtn = new JButton("Quit");
    menuFrame.add(quitBtn);
  }

  @Override
  public void destroy() {
    menuFrame.dispose();
  }

  @Override
  public void hide() {
    menuFrame.setVisible(false);
  }

  @Override
  public void show() {
    menuFrame.setVisible(true);
  }

  /**
   * Add a listener for the Play button.
   */
  public void addPlayListener(ActionListener listener) {
    playBtn.addActionListener(listener);
  }

  /**
   * Add a listener for the Quit button.
   */
  public void addQuitListener(ActionListener listener) {
    quitBtn.addActionListener(listener);
  }

  /**
   * Get the game CONFIGuration from the UI elements.
   */
  public ClientConfig getConfig() {
    CONFIG.setNoOfPlayers((Integer) noOfPlayersSpn.getValue());
    CONFIG.setServerAddr(serverAddrTxt.getText());
    CONFIG.setServerPort(Integer.parseInt(serverPortTxt.getText()));
    CONFIG.setUserName(userNameTxt.getText());
    return CONFIG;
  }

  public JFrame getFrame() {
    return menuFrame;
  }
}
