package jpeg;

import Jama.Matrix;
import enums.Operations;

public class Quality {

    public static double countMSE(double[][] original, double[][] modified) {
        checkArrays(original, modified);
        return helperMethodQualityType (Operations.MSE, original, modified, 0, 0);
    }

    public static double countMAE(double[][] original, double[][] modified) {
        checkArrays(original, modified);
        return helperMethodQualityType (Operations.MAE, original, modified, 0, 0) ;
    }

    public static double countSAE(double[][] original, double[][] modified) {
        checkArrays(original, modified);
        return helperMethodQualityType (Operations.SAE, original, modified, 0, 0);
    }

    public static double countPSNR(double MSE) {
        double maxPixelValue = 255; // assuming pixel values are in the range 0-255
        return 20 * Math.log10(maxPixelValue) - 10 * Math.log10(MSE);
    }

    public static double countPSNRforRGB(double mseRed, double mseGreen, double mseBlue) {
        return countPSNR((mseRed + mseGreen + mseBlue) / 3);
    }

    public static double countSSIM(double[][] original, double[][] modified) {
        double K1 = 0.01, K2 = 0.03, L = 255;
        double C1 = Math.pow(K1*L,2);
        double C2 = Math.pow(K2*L,2);

        double MYx = helperMethodQualityType(Operations.MYx,original, modified,0,0);
        double MYy = helperMethodQualityType(Operations.MYy,original, modified,0,0);
        double SIGMAx = helperMethodQualityType(Operations.SIGMAx,original, modified, MYx, 0);
        double SIGMAy = helperMethodQualityType(Operations.SIGMAy,original, modified, 0, MYy);
        double SIGMAxy = helperMethodQualityType(Operations.SIGMAxy,original, modified, MYx, MYy);

        return ((((2 * MYx * MYy) + C1) * ((2 * SIGMAxy) + C2)) / ((Math.pow(MYx,2) + Math.pow(MYy,2) + C1) * (Math.pow(SIGMAx,2) + Math.pow(SIGMAy,2) + C2)));
    }

    public static double countMSSIM(double[][] original, double[][] modified) {
        int windowSize = 8;
        double mssim = 0;

        for (int i = 0; i < original.length; i += windowSize) {
            for (int j = 0; j < original[0].length; j += windowSize) {
                double[][] windowOriginal = helperMethodMSSIM(original, i, j, windowSize);
                double[][] windowModified = helperMethodMSSIM(modified, i, j, windowSize);
                mssim += countSSIM(windowOriginal, windowModified);
            }
        }
        return mssim / (((double) original.length / windowSize)*((double) original[0].length / windowSize));
    }

    public static void checkArrays (double[][] original, double[][] modified) {
        if (original.length != modified.length || original[0].length != modified[0].length) {
            throw new IllegalArgumentException("Pole musí mít stejné velikosti.");
        }
    }

    public static double helperMethodQualityType (Operations operations, double[][] firstArray, double[][] secondArray, double numX, double numY) {
        double num = 0;

        for (int i = 0; i < firstArray.length; i++) {
            for (int j = 0; j < firstArray[0].length; j++) {
                switch (operations) {
                    case SAE, MAE: num += Math.abs(firstArray[i][j] - secondArray[i][j]);break;
                    case MSE: num += Math.pow(firstArray[i][j] - secondArray[i][j], 2);break;
                    case MYx: num += firstArray[i][j];break;
                    case MYy: num += secondArray[i][j];break;
                    case SIGMAx: num += Math.pow(firstArray[i][j] - numX, 2);break;
                    case SIGMAy: num += Math.pow(secondArray[i][j] - numY, 2);break;
                    case SIGMAxy: num += (firstArray[i][j] - numX) * (secondArray[i][j] - numY);break;
                }
            }
        }

        return switch (operations) {
            case SAE -> num;
            case MSE, MAE, MYx, SIGMAxy -> num / (firstArray.length * firstArray[0].length);
            case MYy -> num / (secondArray.length * secondArray[0].length);
            case SIGMAx -> Math.pow(num / (firstArray.length * firstArray[0].length), (double) 1/2);
            case SIGMAy -> Math.pow(num / (secondArray.length * secondArray[0].length), (double) 1/2);
        };
    }

    private static double[][] helperMethodMSSIM(double[][] data, int startX, int startY, int windowSize) {
        double[][] window = new double[windowSize][windowSize];
        for (int i = 0; i < windowSize; i++) {
            System.arraycopy(data[startX + i], startY, window[i], 0, windowSize);
        }
        return window;
    }
}

