package jpeg;

import enums.SamplingType;
import Jama.Matrix;

public class Sampling {

    /**
     * Metoda pro down sample
     * @param inputMatrix Matrix YCbCr
     * @param samplingType Enum samplingType
     * @return Samplovaný matrix
     */
    public static Matrix sampleDown(Matrix inputMatrix, SamplingType samplingType) {
        Matrix sampledMatrix = null;

        switch (samplingType) {
            case S_4_4_4:
                sampledMatrix = inputMatrix;
                break;
            case S_4_2_2:
                sampledMatrix = downSample(inputMatrix);
                break;
            case S_4_2_0:
                sampledMatrix = downSample(inputMatrix);
                sampledMatrix = sampledMatrix.transpose();
                sampledMatrix = downSample(sampledMatrix);
                sampledMatrix = sampledMatrix.transpose();
                break;
            case S_4_1_1:
                sampledMatrix = downSample(inputMatrix);
                sampledMatrix = downSample(sampledMatrix);
                break;
            default:
                System.out.println("Wrong sampling type!!");
                sampledMatrix = inputMatrix;
        }

        return sampledMatrix;
    }

    /**
     * Metoda pro up sample
     * @param inputMatrix Matrix YCbCr
     * @param samplingType Enum samplingType
     * @return Samplovaný matrix
     */
    public static Matrix sampleUp(Matrix inputMatrix, SamplingType samplingType){


        Matrix sampledMatrix = null;

        switch (samplingType) {
            case S_4_4_4:
                sampledMatrix = inputMatrix;
                break;
            case S_4_2_2:
                sampledMatrix = upSample(inputMatrix);
                break;

            case S_4_2_0:
                sampledMatrix = upSample(inputMatrix);
                sampledMatrix = sampledMatrix.transpose();
                sampledMatrix = upSample(sampledMatrix);
                sampledMatrix = sampledMatrix.transpose();
                //sampledMatrix = upSample(inputMatrix);
                break;

            case S_4_1_1:
                sampledMatrix = upSample(inputMatrix);
                sampledMatrix = upSample(sampledMatrix);
                break;
        }
        return sampledMatrix;
    }

    /**
     * Helper metoda pro downSampling
     *
     * @param matrix Matrix YCbCr
     * @return Matrix YCbCr
     */
    public static Matrix downSample(Matrix matrix) {
        int numbRows = matrix.getRowDimension();
        int numbColumns = matrix.getColumnDimension();
        double[][] newMatrix = new double[numbRows][numbColumns/2];

        int tempCol = 0;
        for (int row = 0; row < numbRows; row++) {
            for (int col = 0; col < numbColumns; col++) {
                if ((col+1) % 2 != 0) {
                    newMatrix[row][col-tempCol] = matrix.get(row, col);
                    tempCol++;
                }
            }

            tempCol = 0;
        }

        return new Matrix(newMatrix);
    }

    /**
     * Helper metoda pro upSample
     *
     * @param matrix Matrix YCbCr
     * @return Matrix YCbCr
     */
    public static Matrix upSample (Matrix matrix) {
        Matrix newMat = new Matrix (matrix.getRowDimension(), matrix.getColumnDimension()*2);
        for (int i = 0; i < matrix.getColumnDimension(); i++) {
            newMat.setMatrix(0, matrix.getRowDimension()-1, 2*i, 2*i, matrix.getMatrix(0, matrix.getRowDimension()-1, i, i));
            newMat.setMatrix(0, matrix.getRowDimension()-1, 2*i+1, 2*i+1, matrix.getMatrix(0, matrix.getRowDimension()-1, i, i));
        }
        return newMat;
    }
}