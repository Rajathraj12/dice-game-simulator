import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
public class MultiplayerClientDialog extends JDialog {
    private JTextField hostField;
    private JTextField portField;
    private JTextField nameField;
    private JButton connectButton;
    private JButton cancelButton;
    private DiceGameGUI parent;
    public MultiplayerClientDialog(DiceGameGUI parent) {
        super(parent, "Join Multiplayer Game", true);
        this.parent = parent;
        initializeUI();
    }
    private void initializeUI() {
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        hostField = new JTextField("localhost", 15);
        add(hostField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        portField = new JTextField("12345", 15);
        add(portField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Player Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField("Player", 15);
        add(nameField, gbc);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        connectButton = new JButton("Connect");
        cancelButton = new JButton("Cancel");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }
    private void connectToServer() {
        try {
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            String playerName = nameField.getText().trim();
            if (host.isEmpty() || playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (port < 1024 || port > 65535) {
                JOptionPane.showMessageDialog(this, "Port must be between 1024 and 65535!", "Invalid Port", JOptionPane.ERROR_MESSAGE);
                return;
            }
            MultiplayerClient client = new MultiplayerClient(host, port, playerName);
            client.connect();
            JOptionPane.showMessageDialog(this, "Connected to server at " + host + ":" + port, "Connected", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
