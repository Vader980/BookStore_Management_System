import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginSystem extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private JLabel animatedPersonLabel;
    private JCheckBox showPasswordCheckBox;

    public LoginSystem() {
        setTitle("Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        errorLabel = new JLabel();
        animatedPersonLabel = new JLabel(new ImageIcon("animated_person.gif"));
        showPasswordCheckBox = new JCheckBox("Show Password");

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);

        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);

        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(showPasswordCheckBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        panel.add(loginButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(errorLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 5;
        panel.add(animatedPersonLabel, constraints);

        loginButton.addActionListener(this);
        showPasswordCheckBox.addActionListener(this);

        add(panel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Perform login validation
            if (validateLogin(username, password)) {
                errorLabel.setText("");
                JOptionPane.showMessageDialog(this, "Login successful!");
                clearFields();
                // Open the BookstoreSystem
                openBookstoreSystem();
            } else {
                errorLabel.setText("Invalid username or password. Please try again.");
                errorLabel.setForeground(Color.RED);
                animateLoginForm();
            }
        } else if (e.getSource() == showPasswordCheckBox) {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            if (checkBox.isSelected()) {
                animatedPersonLabel.setIcon(new ImageIcon("animated_person_look.gif"));
                passwordField.setEchoChar((char) 0);
            } else {
                animatedPersonLabel.setIcon(new ImageIcon("animated_person.gif"));
                passwordField.setEchoChar('*');
            }
        }
    }

    private boolean validateLogin(String username, String password) {
        // Replace with your actual login validation logic
        return username.equals("admin") && password.equals("password");
    }

    private void animateLoginForm() {
        Point originalLocation = getLocation();

        for (int i = 0; i <= 10; i++) {
            setLocation(originalLocation.x + i, originalLocation.y);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i <= 10; i++) {
            setLocation(originalLocation.x - i, originalLocation.y);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setLocation(originalLocation);
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    private void openBookstoreSystem() {
        SwingUtilities.invokeLater(BookstoreSystem::new);
        dispose(); // Close the login window
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginSystem::new);
    }
}
