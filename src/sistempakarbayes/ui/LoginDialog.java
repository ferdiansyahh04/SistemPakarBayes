package sistempakarbayes.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import sistempakarbayes.data.UserDAO;
import sistempakarbayes.model.User;

/**
 * Dialog login admin/pakar.
 * Setelah login berhasil, objek User tersimpan di {@link #getUser()}.
 */
public class LoginDialog extends JDialog {

    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private User user;

    public LoginDialog(JFrame parent) {
        super(parent, "Login Administrator", Dialog.ModalityType.APPLICATION_MODAL);
        setSize(340, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Login Admin / Pakar", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        form.add(new JLabel("Username:"));
        form.add(txtUsername);
        form.add(new JLabel("Password:"));
        form.add(txtPassword);
        add(form, BorderLayout.CENTER);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        JButton btnLogin = new JButton("Login");
        JButton btnBatal = new JButton("Batal");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 12));
        tombol.add(btnLogin);
        tombol.add(btnBatal);
        add(tombol, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> prosesLogin());
        btnBatal.addActionListener(e -> dispose());

        // Enter di field password = login
        txtPassword.addActionListener(e -> prosesLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    private void prosesLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan password wajib diisi.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User hasil = UserDAO.login(u, p);
        if (hasil == null) {
            JOptionPane.showMessageDialog(this,
                    "Username atau password salah.",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            return;
        }
        this.user = hasil;
        dispose();
    }

    /** User yang berhasil login, atau null bila batal. */
    public User getUser() {
        return user;
    }
}