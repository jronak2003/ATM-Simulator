package atm.simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class Withdrawal extends JFrame implements ActionListener {
    JButton b1, b2;
    TextField textField;
    String Aid;
    Withdrawal(String Aid){
        this.Aid = Aid;

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/atm2.png"));
        Image i2 = i1.getImage().getScaledInstance(1550,830,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l3 = new JLabel(i3);
        l3.setBounds(0,0,1550,830);
        add(l3);

        JLabel label1 = new JLabel("MAXIMUM WITHDRAWAL IS RS.10,000");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("System", Font.BOLD, 16));
        label1.setBounds(460,180,700,35);
        l3.add(label1);

        JLabel label2 = new JLabel("PLEASE ENTER YOUR AMOUNT");
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("System", Font.BOLD, 16));
        label2.setBounds(460,220,400,35);
        l3.add(label2);

        textField = new TextField();
        textField.setBackground(new Color(65,125,128));
        textField.setForeground(Color.WHITE);
        textField.setBounds(460,260,320,25);
        textField.setFont(new Font("Raleway", Font.BOLD,22));
        l3.add(textField);

        b1 = new JButton("WITHDRAW");
        b1.setForeground(Color.WHITE);
        b1.setBackground(new Color(65,125,128));
        b1.setBounds(700,362,150,35);
        b1.addActionListener(this);
        l3.add(b1);

        b2 = new JButton("Back");
        b2.setBounds(700, 406, 150, 35);
        b2.setBackground(new Color(65, 125, 128));
        b2.setForeground(Color.WHITE);
        b2.addActionListener(this);
        l3.add(b2);

        setLayout(null);
        setSize(1550,1080);
        setLocation(0,0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String amount = textField.getText();
            java.util.Date utilDate = new java.util.Date(); // Get the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(utilDate);
            java.sql.Date sqlDate = java.sql.Date.valueOf(formattedDate); // Convert to SQL Date format

            if (e.getSource() == b1) {
                if (textField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter the Amount you want to withdraw");
                } else {
                    int withdrawAmount = Integer.parseInt(amount);
                    if (withdrawAmount % 100 != 0) {
                        JOptionPane.showMessageDialog(null, "Please enter an amount that is a multiple of 100");
                    } else if (withdrawAmount > 10000) {
                        JOptionPane.showMessageDialog(null, "Maximum withdrawal amount is 10,000");
                    } else {
                        Connn c = new Connn();
                        ResultSet resultSet = c.statement.executeQuery("SELECT SUM(Deposit_amt) - SUM(Withdrawal_amt) AS Current_bal " +
                                "FROM Transaction WHERE Aid = '" + Aid + "'");
                        int balance = 0;
                        if (resultSet.next()) {
                            balance = resultSet.getInt("Current_bal");
                        }
                        if (balance < withdrawAmount) {
                            JOptionPane.showMessageDialog(null, "Insufficient Balance");
                        } else {
                            String tid = generateTid();
                            c.statement.executeUpdate("insert into Transaction (Tid, Aid, DOT, Withdrawal_amt, Deposit_amt, Current_bal) " +
                                    "values('" + tid + "', '" + Aid + "', '" + sqlDate + "', '" + amount + "', '0', '" + (balance - withdrawAmount) + "')");
                            JOptionPane.showMessageDialog(null, "Rs. " + amount + " Debited Successfully");
                            setVisible(false);
                            new main_Class(Aid);
                        }
                    }
                }
            }
            else if (e.getSource() == b2){
                setVisible(false);
                new main_Class(Aid);
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Data truncation: Incorrect date value")) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please check the date value.");
            } else {
                ex.printStackTrace();
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
    private String generateTid() {
        // Generate a unique Tid, e.g., using UUID or a combination of current timestamp and random number
        return "T" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    public static void main(String[] args) {
        new Withdrawal("");
    }
}

