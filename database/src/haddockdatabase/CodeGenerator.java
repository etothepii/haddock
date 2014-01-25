/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package haddockdatabase;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author jrrpl
 */
public class CodeGenerator extends JFrame {

    private JTextArea in;
    private JButton parse;
    private JTextArea out;

    public CodeGenerator() {
        in = new JTextArea();
        out = new JTextArea();
        parse = new JButton("Parse");
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(new JScrollPane(in, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),
                new GridBagConstraints(0, 0, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(new JScrollPane(out, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),
                new GridBagConstraints(0, 2, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));
        getContentPane().add(parse, new GridBagConstraints(0, 1, 1, 1, 1d, 0d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 20));
        setPreferredSize(new Dimension(400, 600));
        parse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.setText(process(in.getText()));
            }
        });
    }

    private String process(String inText) {
        String[] lines = inText.split(";");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(processLine(line.trim()));
        }
        return sb.toString();
    }

    private String processLine(String line) {
        StringBuilder sb = new StringBuilder();
        String[] split = line.split(" ");
        if (split.length == 3) {
            String varType = split[1];
            String varName = split[2];
            String upperCaseName = upperCaseVar(varName);
            String upperCaseFirstLetterName =
                    upperCaseFirstLetterVar(varName);
            sb.append("    public ");
            sb.append(varType);
            sb.append(" get");
            sb.append(upperCaseFirstLetterName);
            sb.append("() {\n        return ");
            sb.append(varName);
            sb.append(";\n    }\n\n    public void set");
            sb.append(upperCaseFirstLetterName);
            sb.append("(");
            sb.append(varType);
            sb.append(" ");
            sb.append(varName);
            sb.append(") {\n        if (different(");
            sb.append(varName);
            sb.append(", this.");
            sb.append(varName);
            sb.append(")) {\n");
            sb.append("            ");
            sb.append(varType);
            sb.append(" old = this.");
            sb.append(varName);
            sb.append(";\n            this.");
            sb.append(varName);
            sb.append(" = ");
            sb.append(varName);
            sb.append(";\n            fireFieldChangedListener(new FieldChangedEvent(this, ");
            sb.append(upperCaseName);
            sb.append(", old, ");
            sb.append(varName);
            sb.append("));\n            setChanged(true);\n        }\n    }\n\n");
        }
        return sb.toString();
    }

    private String upperCaseVar(String varName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < varName.length(); i++) {
            char c = varName.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
            }
            sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }

    private String upperCaseFirstLetterVar(String varName) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(varName.charAt(0)));
        for (int i = 1; i < varName.length(); i++) {
            sb.append(varName.charAt(i));
        }
        return sb.toString();
    }

}
