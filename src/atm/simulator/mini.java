package atm.simulator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.LinkedList;

public class mini extends JFrame implements ActionListener {
    String Aid;
    JButton button;

    mini(String Aid) {
        this.Aid = Aid;
        getContentPane().setBackground(new Color(255, 204, 204));
        setSize(400, 600);
        setLocation(20, 20);
        setLayout(null);

        JLabel label1 = new JLabel();
        label1.setBounds(20, 80, 400, 400);
        add(label1);

        JLabel label2 = new JLabel("MINI STATEMENT");
        label2.setFont(new Font("System", Font.BOLD, 15));
        label2.setBounds(150, 20, 200, 20);
        add(label2);

        JLabel label3 = new JLabel();
        label3.setBounds(20, 80, 300, 20);
        add(label3);

        JLabel label4 = new JLabel();
        label4.setBounds(20, 450, 300, 20);
        add(label4);

        try {
            Connn c = new Connn();
            ResultSet resultSet = c.statement.executeQuery("SELECT * FROM Account1 WHERE Aid = '" + Aid + "'");
            while (resultSet.next()) {
                label3.setText("Card Number:  " + resultSet.getString("card_number").substring(0, 4) + "XXXXXXXX" + resultSet.getString("card_number").substring(12));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            StringBuilder transactions = new StringBuilder();
            int totalBalance = 0;
            Connn c = new Connn();
            ResultSet resultSet = c.statement.executeQuery("SELECT * FROM Transaction WHERE Aid = '" + Aid + "' ORDER BY DOT DESC");

            // Create a list to store the transactions with a maximum size of 10
            LinkedList<String> transactionList = new LinkedList<>();

            while (resultSet.next()) {
                String depositAmt = resultSet.getString("Deposit_amt");
                String withdrawalAmt = resultSet.getString("Withdrawal_amt");

                if (!depositAmt.equals("0") && !withdrawalAmt.equals("0")) {
                    continue; // Skip transactions where both deposit and withdrawal amounts are 0
                }

                if (!depositAmt.equals("0")) {
                    totalBalance += Integer.parseInt(depositAmt);
                    transactionList.addFirst("<html>Tid: " + resultSet.getString("Tid") + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + resultSet.getString("DOT") + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Deposit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + depositAmt + "<br><br><html>");
                } else if (!withdrawalAmt.equals("0")) {
                    totalBalance -= Integer.parseInt(withdrawalAmt);
                    transactionList.addFirst("<html>Tid: " + resultSet.getString("Tid") + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + resultSet.getString("DOT") + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Withdrawal&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + withdrawalAmt + "<br><br><html>");
                }

                // Limit the list size to 10 and apply LIFO method
                if (transactionList.size() > 10) {
                    transactionList.removeLast();
                }
            }

            // Append the transactions to the StringBuilder
            for (String transaction : transactionList) {
                transactions.append(transaction);
            }

            label1.setText(transactions.toString());
            label4.setText("Your Total Balance is Rs " + totalBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        button = new JButton("Back");
        button.setBounds(20, 500, 100, 25);
        button.addActionListener(this);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        add(button);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            setVisible(false);
            new main_Class(Aid).setVisible(true);
        }
    }

    public static void main(String[] args) {
        new mini("");
    }
}

