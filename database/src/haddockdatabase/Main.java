/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package haddockdatabase;

/**
 *
 * @author jrrpl
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CodeGenerator cg = new CodeGenerator();
        cg.pack();
        cg.setVisible(true);
    }

}
