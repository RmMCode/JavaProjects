import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calculator {
    int boardWidth = 420;
    int boardHeight = 680;

    Color customLightGray = new Color(212, 212, 210);
    Color customDarkGray  = new Color(80, 80, 80);
    Color customBlack = new Color(28, 28, 28);
    Color customOrange = new Color(255, 149, 0);

    String[] buttonValues = {
        "AC", "DEL", "+/-", "%",
        "sin", "cos", "tan", "log",
        "7", "8", "9", "÷",
        "4", "5", "6", "×",
        "1", "2", "3", "-",
        "0", ".", "^", "+",
        "√", "|x|", "=", ""
    };

    String[] rightSymbols = {"÷", "×", "-", "+", "="};
    String[] topSymbols = {"AC", "+/-", "%"};

    JFrame frame = new JFrame("Calculator"); // Window + Title
    JLabel displayLabel = new JLabel();
    JPanel displayPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

    // Functionalities of additon, subtraction, multiplication, divsion
    // A + B, A - B, A*B, A/B
    String A = "0";
    String operator = null;
    String B = null;

    Calculator() {
        // Used to create a more Platform-Independent Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); // N,S,W,E Components on Window
        frame.getContentPane().setBackground(customBlack); // Set Container Background

        displayLabel.setBackground(customBlack);
        displayLabel.setForeground(Color.white); // Text color
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 80));
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setText("0");
        displayLabel.setOpaque(true);

        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(displayLabel);
        frame.add(displayPanel, BorderLayout.NORTH);

        buttonsPanel.setLayout(new GridLayout(7,4,5,5));
        buttonsPanel.setBackground(customBlack);
        frame.add(buttonsPanel);

        for (int i = 0; i < buttonValues.length; i++) {
            JButton button = new JButton();
            String buttonValue = buttonValues[i];
            button.setFont(new Font("Arial", Font.PLAIN, 30));
            button.setText(buttonValue);
            button.setFocusable(false);
            button.setBorder(new LineBorder(customBlack));
            button.setContentAreaFilled(true); // ensures background shows up
            button.setOpaque(true); // adds background paint on mac
            button.setBorderPainted(false); // cleaner/more modern flat look
            // Style up the buttons
            if (Arrays.asList(topSymbols).contains(buttonValue)) {
                button.setBackground(customLightGray);
                button.setForeground(customBlack);
            }
            else if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                button.setBackground(customOrange);
                button.setForeground(Color.white);
            }
            // Digit (0-9) or Decimal Place symbol
            else {
                button.setBackground(customDarkGray);
                button.setForeground(Color.white);
            }
            buttonsPanel.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    String buttonValue = button.getText();
                    if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                        if (buttonValue.equals("=")) {
                            if (A != null && operator != null) {
                                B = displayLabel.getText();
                                double numA = Double.parseDouble(A);
                                double numB = Double.parseDouble(B);
                                if (operator.equals("+")) {
                                    displayLabel.setText(removeZeroDecimal(numA + numB));
                                }
                                else if (operator.equals("-")) {
                                    displayLabel.setText(removeZeroDecimal(numA - numB));
                                }
                                else if (operator.equals("×")) {
                                    displayLabel.setText(removeZeroDecimal(numA*numB));
                                }
                                else if (operator.equals("÷")) {
                                    displayLabel.setText(removeZeroDecimal(numA/numB));
                                }
                                else if (operator.equals("^")) {
                                    displayLabel.setText(removeZeroDecimal(Math.pow(numA, numB)));
                                }
                                clearAll();
                            }
                        }
                        else if("+-×÷^".contains(buttonValue)) {
                            if (operator == null) {
                                A = displayLabel.getText();
                                displayLabel.setText("0");
                                B = "0";
                            }
                            operator = buttonValue;
                        }
                        else if (buttonValue.equals("DEL")) {
                            String current = displayLabel.getText();
                            if (current.length() > 1) {
                                displayLabel.setText(current.substring(0, current.length() - 1));
                            }
                            else {
                                displayLabel.setText("0");
                            }
                        }
                        else if (buttonValue.equals("^")) {
                            if (operator == null) {
                                A = displayLabel.getText();
                                displayLabel.setText("0");
                                B = "0";
                                operator = "^";
                            }
                        }
                    }
                    else if (Arrays.asList(topSymbols).contains(buttonValue)) {
                        if (buttonValue.equals("AC")) {
                            clearAll();
                            displayLabel.setText("0");
                        }
                        else if (buttonValue.equals("+/-")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay *= -1;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        }
                        else if (buttonValue.equals("%")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay /= 100;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        }
                    }
                    // Added functionality for Absolute value
                    else if (buttonValue.equals("|x|")) {
                        double numDisplay = Double.parseDouble(displayLabel.getText());
                        displayLabel.setText(removeZeroDecimal(Math.abs(numDisplay)));
                    }

                    else if (buttonValue.equals("|x|")) {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            displayLabel.setText(removeZeroDecimal(Math.abs(numDisplay)));
                        }
                        else if (buttonValue.equals("sin")) {
                            double val = Math.toRadians(Double.parseDouble(displayLabel.getText()));
                            displayLabel.setText(removeZeroDecimal(Math.sin(val)));
                        }
                        else if (buttonValue.equals("cos")) {
                            double val = Math.toRadians(Double.parseDouble(displayLabel.getText()));
                            displayLabel.setText(removeZeroDecimal(Math.cos(val)));
                        }
                        else if (buttonValue.equals("tan")) {
                            double val = Math.toRadians(Double.parseDouble(displayLabel.getText()));
                            displayLabel.setText(removeZeroDecimal(Math.tan(val)));
                        }
                        else if (buttonValue.equals("log")) {
                            double val = Double.parseDouble(displayLabel.getText());
                            if (val > 0) {
                                displayLabel.setText(removeZeroDecimal(Math.log10(val)));
                            }
                            else {
                                displayLabel.setText("Error");
                            }
                        }
                    // Added functionality for square roots
                    else if (buttonValue.equals("√")) {
                        double numDisplay = Double.parseDouble(displayLabel.getText());
                        if (numDisplay >= 0) {
                            double result = Math.sqrt(numDisplay);
                            displayLabel.setText(removeZeroDecimal(result));
                            clearAll(); // Clear the stored A/B/operator
                        }
                        else {
                            displayLabel.setText("Error"); // Square root of negative number
                        }
                    }
                    else { // digits or .(period/decimals)
                        if (buttonValue.equals(".")) {
                            if (!displayLabel.getText().contains(buttonValue)) {
                                displayLabel.setText(displayLabel.getText() + buttonValue);
                            }
                        }
                        else if ("0123456789".contains(buttonValue)) {
                            if (displayLabel.getText().equals("0")) {
                                displayLabel.setText(buttonValue);
                            }
                            else {
                                displayLabel.setText(displayLabel.getText() + buttonValue);
                            }
                        }
                    }
                }
            });
        }
        frame.setVisible(true);
    }

    void clearAll() {
        A = "0";
        operator = null;
        B = null;
    }

    String removeZeroDecimal(double numDisplay) {
        if (numDisplay % 1 == 0) {
            return Integer.toString((int) numDisplay);
        }
        return Double.toString(numDisplay);
    }
}