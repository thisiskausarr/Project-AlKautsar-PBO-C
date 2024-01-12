import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class CalculatorGUI extends JFrame {
    private JTextField display;
    private JButton[] numberButtons;
    private JButton[] operationButtons;
    private JButton equalsButton, clearButton, backspaceButton, darkModeButton;

    private JPanel mainPanel;  
    private StringBuilder input;  
    private boolean darkMode = false;

    public CalculatorGUI() {
        setTitle("Kalkulator Kompleks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLayout(new BorderLayout());

        mainPanel = new JPanel();  
        mainPanel.setBackground(new Color(173, 216, 230)); 
        mainPanel.setLayout(new BorderLayout());

        display = new JTextField();
        display.setEditable(false);

        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].setBackground(new Color(240, 248, 255)); 
        }

        operationButtons = new JButton[5];
        operationButtons[0] = new JButton("+");
        operationButtons[1] = new JButton("-");
        operationButtons[2] = new JButton("*");
        operationButtons[3] = new JButton("/");
        operationButtons[4] = new JButton("=");
        for (int i = 0; i < 5; i++) {
            operationButtons[i].setBackground(new Color(135, 206, 250)); 
        }

        mainPanel.add(display, BorderLayout.NORTH);

        JPanel numbersPanel = new JPanel(new GridLayout(4, 3));
        for (int i = 1; i <= 9; i++) {
            numbersPanel.add(numberButtons[i]);
        }
        numbersPanel.add(numberButtons[0]);
        mainPanel.add(numbersPanel, BorderLayout.CENTER);

        JPanel operationsPanel = new JPanel(new GridLayout(4, 1));
        for (int i = 0; i < 4; i++) {
            operationsPanel.add(operationButtons[i]);
        }
        mainPanel.add(operationsPanel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 4));
        clearButton = new JButton("C");
        clearButton.setBackground(new Color(255, 69, 0)); 
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        bottomPanel.add(clearButton);

        backspaceButton = new JButton("X ");
        backspaceButton.setBackground(new Color(255, 165, 0)); 
        backspaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeLastDigit();
            }
        });
        bottomPanel.add(backspaceButton);

        equalsButton = new JButton("=");
        equalsButton.setBackground(new Color(50, 205, 50)); 
        equalsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateResult();
            }
        });
        bottomPanel.add(equalsButton);

        darkModeButton = new JButton("Dark Mode");
        darkModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleDarkMode();
            }
        });
        bottomPanel.add(darkModeButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        JLabel copyrightLabel = new JLabel("<html><i><b>Copyright Al Kautsar 2332099029</b></i></html>");
        copyrightLabel.setHorizontalAlignment(JLabel.CENTER);
        add(copyrightLabel, BorderLayout.SOUTH);

        add(mainPanel);

        addListeners();

        setVisible(true);

        input = new StringBuilder();
    }

    private void addListeners() {
        for (int i = 0; i < 10; i++) {
            final int digit = i;
            numberButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    input.append(digit);
                    display.setText(input.toString());
                }
            });
        }

        for (int i = 0; i < 4; i++) {
            final String op = operationButtons[i].getText();
            operationButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    processOperator(op);
                }
            });
        }

        operationButtons[4].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateResult();
            }
        });
    }

    private void processOperator(String op) {
        input.append(" ").append(op).append(" ");
        display.setText(input.toString());
    }

    private void calculateResult() {
        try {
            String postfixExpression = infixToPostfix(input.toString());
            double result = evaluatePostfix(postfixExpression);
            display.setText(String.valueOf(result));
            input.setLength(0);
            input.append(result);
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            clearAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Invalid expression");
            clearAll();
        }
    }

    private String infixToPostfix(String infixExpression) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();

        String[] tokens = infixExpression.split(" ");

        for (String token : tokens) {
            if (isNumeric(token)) {
                postfix.append(token).append(" ");
            } else if (isOperator(token.charAt(0))) {
                while (!operatorStack.isEmpty() &&
                        hasPrecedence(token.charAt(0), operatorStack.peek())) {
                    postfix.append(operatorStack.pop()).append(" ");
                }
                operatorStack.push(token.charAt(0));
            }
        }

        while (!operatorStack.isEmpty()) {
            postfix.append(operatorStack.pop()).append(" ");
        }

        return postfix.toString().trim();
    }

    private double evaluatePostfix(String postfixExpression) {
        Stack<Double> operandStack = new Stack<>();

        String[] tokens = postfixExpression.split(" ");

        for (String token : tokens) {
            if (isNumeric(token)) {
                operandStack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                double operand2 = operandStack.pop();
                double operand1 = operandStack.pop();

                switch (token.charAt(0)) {
                    case '+':
                        operandStack.push(operand1 + operand2);
                        break;
                    case '-':
                        operandStack.push(operand1 - operand2);
                        break;
                    case '*':
                        operandStack.push(operand1 * operand2);
                        break;
                    case '/':
                        if (operand2 != 0) {
                            operandStack.push(operand1 / operand2);
                        } else {
                            throw new ArithmeticException("Division by zero");
                        }
                        break;
                }
            }
        }

        return operandStack.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        return (op2 != '(' && op2 != ')' && getPrecedence(op1) <= getPrecedence(op2));
    }

    private int getPrecedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        }
        return 0;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void clearAll() {
        input.setLength(0);
        display.setText("");
    }

    private void removeLastDigit() {
        if (input.length() > 0) {
            input.deleteCharAt(input.length() - 1);
            display.setText(input.toString());
        }
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        updateDarkMode();
    }

    private void updateDarkMode() {
        if (darkMode) {
            mainPanel.setBackground(new Color(30, 30, 30));
            display.setBackground(new Color(40, 40, 40));
            display.setForeground(Color.WHITE);
            darkModeButton.setText("Light Mode");
        } else {
            mainPanel.setBackground(new Color(173, 216, 230));
            display.setBackground(Color.WHITE);
            display.setForeground(Color.BLACK);
            darkModeButton.setText("Dark Mode");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }
}
