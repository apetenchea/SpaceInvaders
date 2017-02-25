package spaceinvaders.client.gui;

import static javax.swing.JFrame.DO_NOTHING_ON_CLOSE;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.exceptions.IllegalPortNumberException;

/**
 * Used to configure the game before playing.
 *
 * <p>This is the first UI element encountered by the user.
 */
public class Menu implements UiObject {
  public static final ClientConfig CONFIG = ClientConfig.getInstance();

  private final JFrame menuFrame = new JFrame("SpaceInvaders");
  private final JButton playBtn = new JButton("Play");
  private final JButton quitBtn = new JButton("Quit");
  private final JLabel serverLbl = new JLabel("Game server");
  private final JLabel userNameLbl = new JLabel("User name");
  private final JTextField serverAddrTxt = new JTextField(CONFIG.getServerAddr());
  private final JTextField serverPortTxt = new JTextField(Integer.toString(CONFIG.getServerPort()));
  private final JTextField userNameTxt = new JTextField(CONFIG.getUserName(),
      CONFIG.getMaxUserNameLength());
  private final JSpinner teamSizeSpn = new JSpinner(
      new SpinnerNumberModel(1,1,CONFIG.getMaxPlayers(),1));

  /** Construct a default menu. */
  public Menu() {
    menuFrame.setSize(500,500);
    menuFrame.setResizable(false);
    menuFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    menuFrame.setLayout(new FlowLayout());
    menuFrame.add(serverLbl);
    menuFrame.add(serverAddrTxt);
    menuFrame.add(serverPortTxt);
    menuFrame.add(userNameLbl);
    menuFrame.add(userNameTxt);
    menuFrame.add(playBtn);
    menuFrame.add(quitBtn);
    // Disable spinner's text editing feature
    ((JSpinner.DefaultEditor) teamSizeSpn.getEditor()).getTextField().setEditable(false);
    menuFrame.add(teamSizeSpn);
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

  /** Add a listener for the Play button. */
  public void addPlayListener(ActionListener listener) {
    playBtn.addActionListener(listener);
  }

  /** Add a listener for the Quit button. */
  public void addQuitListener(ActionListener listener) {
    quitBtn.addActionListener(listener);
  }

  /**
   * Save the values entered in the form into the config.
   *
   * @throws IllegalPortNumberException if the port field contains illegal data.
   */
  public void setConfig() {
    CONFIG.setTeamSize((Integer) teamSizeSpn.getValue());
    CONFIG.setServerAddr(serverAddrTxt.getText());
    try {
      CONFIG.setServerPort(Integer.parseInt(serverPortTxt.getText()));
    } catch (NumberFormatException numException) {
      throw new IllegalPortNumberException();
    }
    CONFIG.setUserName(userNameTxt.getText());
  }

  public JFrame getFrame() {
    return menuFrame;
  }
}
