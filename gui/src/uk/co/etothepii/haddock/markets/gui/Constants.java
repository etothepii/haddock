/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.etothepii.haddock.markets.gui;

import java.awt.Color;
import java.awt.Font;
import org.apache.log4j.Logger;

/**
 *
 * @author James Robinson for and on behalf of Etothepii Ltd
 */
public class Constants {

    private static final Logger LOG = Logger.getLogger(Constants.class);

    private static final Color defaultBackColour = new Color(224, 224, 255);

    public static Color getDefaultBackColour() {
        return defaultBackColour;
    }

    private static final Color defaultSelectedBackColour =
            new Color(192, 192, 255);

    public static Color getDefaultSelectedBackColour() {
        return defaultSelectedBackColour;
    }

    private static final Color defaultLayColour = new Color(255, 224, 224);

    public static Color getDefaultLayColour() {
        return defaultLayColour;
    }

    private static final Color defaultSelectedLayColour =
            new Color(255, 192, 192);

    public static Color getDefaultSelectedLayColour() {
        return defaultSelectedLayColour;
    }

    private static final Color defaultProfitColour = Color.GREEN;

    public static Color getDefaultProfitColour() {
        return defaultProfitColour;
    }

    private static final Color defaultLossColour = Color.RED;

    public static Color getDefaultLossColour() {
        return defaultLossColour;
    }

    private static final Color defaultNoneColour = new Color(255, 255, 255);

    public static Color getDefaultNoneColour() {
        return defaultNoneColour;
    }

    private static final Color defaultSelectedNoneColour =
            new Color(224, 224, 224);

    public static Color getDefaultSelectedNoneColour() {
        return defaultSelectedNoneColour;
    }

    private static final Font defaultRunnerPriceFont =
            new Font("Georgia", Font.PLAIN, 12);

    public static Font getDefaultRunnerPriceFont() {
        return defaultRunnerPriceFont;
    }

    private static final Font defaultRunnerPriceTitleFont =
            new Font("Georgia", Font.BOLD, 12);

    public static Font getDefaultRunnerPriceTitleFont() {
        return defaultRunnerPriceTitleFont;
    }

    private static final Color defaultBorderColor = new Color(128, 128, 128);

    public static Color getDefaultBorderColor() {
        return defaultBorderColor;
    }
}
