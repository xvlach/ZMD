package core;

import Jama.Matrix;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class Helper {
    /**
     * Vrátí šířku matice.
     *
     * @param matrix matice
     * @return šířka matice
     */
    public static int GetWidth(Matrix matrix) {
        if (matrix == null) return 0;
        return matrix.getColumnDimension();
    }

    /**
     * Vrátí šířku pole.
     *
     * @param array pole
     * @return šířka pole
     */
    public static int GetWidth(int[][] array) {
        if (array == null || array.length == 0) return 0;
        return array[0].length;
    }

    /**
     * Vrátí výšku matice.
     *
     * @param matrix matice
     * @return výška matice
     */
    public static int GetHeight(Matrix matrix) {
        if (matrix == null) return 0;
        return matrix.getRowDimension();
    }

    /**
     * Vrátí výšku pole.
     *
     * @param array pole
     * @return výška pole
     */
    public static int GetHeight(int[][] array) {
        if (array == null) return 0;
        return array.length;
    }

    /**
     * Formatter pro omezení textového pole pouze na čísla.
     */
    public static final UnaryOperator<TextFormatter.Change> NUMBER_FORMATTER = change -> {
        String text = change.getText();
        if (text.matches("[0-9]*")) return change;
        return null;
    };

    /**
     * Metoda, která zaokrouhlí double na int a také
     * zajistí, že hodnota bude v rozsahu 0-255.
     *
     * @param value Hodnota, která se má zaokrouhlit.
     * @return Zaokrouhlená hodnota v rozmezí 0-255.
     */
    public static int checkValue(double value) {
        int w = (int) Math.round(value);
        if (w < 0) w = 0;
        if (w > 255) w = 255;
        return w;
    }

    /**
     * Převod int pole na double pole
     *
     * @param intArray
     * @return double arr
     */
    public static double[][] convertIntToDouble(int[][] intArray) {
        double[][] doubleArray = new double[intArray.length][intArray[0].length];
        for (int i = 0; i < intArray.length; i++) {
            for (int j = 0; j < intArray[0].length; j++) {
                doubleArray[i][j] = intArray[i][j];
            }
        }
        return doubleArray;
    }

    public static int roundRange(double num) {
        return Math.min(Math.max((int) Math.round(num), 0), 255);
    }
}
