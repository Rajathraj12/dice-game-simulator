import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
public class MultiplayerHostDialog extends JDialog {
    private JTextField portField;
    private JButton startButton;
    private JButton cancelButton;
    private DiceGameGUI parent;
    public MultiplayerHostDialog(DiceGameGUI parent) {
        super(parent, "Host Multiplayer Game", true);
        this.parent = parent;
        initializeUI();
    }
    private void initializeUI() {
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        portField = new JTextField("12345", 10);
        add(portField, gbc);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start Server");
        cancelButton = new JButton("Cancel");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }
    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            if (port < 1024 || port > 65535) {
                JOptionPane.showMessageDialog(this, "Port must be between 1024 and 65535!", "Invalid Port", JOptionPane.ERROR_MESSAGE);
                return;
            }
            MultiplayerServer server = new MultiplayerServer(port);
            server.start();
            JOptionPane.showMessageDialog(this, "Server started on port " + port + "\nWaiting for players...", "Server Started", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error starting server: " + ex.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
