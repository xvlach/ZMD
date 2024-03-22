package jpeg;

import Jama.Matrix;
import core.Helper;

public class ColorTransform {

    /**
     * Metoda pro RGB do YCbCr
     * @param red Red pole
     * @param green Green pole
     * @param blue Blue pole
     * @return Matrix YCbCr
     */
    public static Matrix[] convertOriginalRGBtoYcBcR(int[][] red, int[][] green, int[][] blue){

        Matrix convertedY = new Matrix(red.length, red[0].length);
        Matrix convertedCb = new Matrix(red.length, red[0].length);
        Matrix convertedCr = new Matrix(red.length, red[0].length);

        for (int i = 0; i < red.length; i++) {
            for (int j = 0; j < red[0].length; j++) {

                convertedY.set(i,j,0.257*red[i][j] + 0.504*green[i][j] + 0.098*blue[i][j] + 16);
                convertedCb.set(i,j,-0.148*red[i][j] - 0.291*green[i][j] + 0.439*blue[i][j] + 128);
                convertedCr.set(i,j,0.439*red[i][j] - 0.368*green[i][j] - 0.071*blue[i][j] + 128);
            }
        }
        return new Matrix[]{convertedY, convertedCb, convertedCr};
    }

    /**
     * Metoda pro YCbCr do RGB
     * @param Y Matrix Y
     * @param Cb Matrix Cb
     * @param Cr Matrix Cr
     * @return Pole poli RGB
     */
    public static int[][][] convertModifiedYcBcRtoRGB(Matrix Y, Matrix Cb, Matrix Cr){
        int[][] convertedRed = new int[Y.getRowDimension()][Y.getColumnDimension()];
        int[][] convertedGreen = new int[Y.getRowDimension()][Y.getColumnDimension()];;
        int[][] convertedBlue = new int[Y.getRowDimension()][Y.getColumnDimension()];;

        for (int row = 0; row < Y.getRowDimension(); row++) {
            for (int column = 0; column < Y.getColumnDimension(); column++) {
                int red = Math.round((float)(1.164*(Y.get(row, column)-16) + 1.596*(Cr.get(row, column)-128)));
                red = Helper.roundRange(red);

                int green = Math.round((float)((1.164*(Y.get(row, column) - 16) - 0.813*(Cr.get(row, column)-128)) - 0.391*(Cb.get(row, column) -128)));
                green = Helper.roundRange(green);

                int blue = Math.round((float)(1.164*(Y.get(row, column) -16) + 2.018*(Cb.get(row, column)-128)));
                blue = Helper.roundRange(blue);

                convertedRed[row][column] = red;
                convertedGreen[row][column] = green;
                convertedBlue[row][column] = blue;
            }
        }
        return new int[][][]{convertedRed, convertedGreen, convertedBlue};
    }
}
