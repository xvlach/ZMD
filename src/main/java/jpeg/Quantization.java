package jpeg;

import Jama.Matrix;

public class Quantization {
    private static final double [][] intMatrixY = {
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}
    };
    private static final double [][] intMatrix = {
            {17, 18, 24, 47, 99, 99, 99, 99},
            {18, 21, 26, 66, 99, 99, 99, 99},
            {24, 26, 56, 99, 99, 99, 99, 99},
            {47, 66, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99}
    };

    public static Matrix matrixVal = new Matrix(8,8).constructWithCopy(intMatrix);
    public static Matrix matrixYval = new Matrix(8,8).constructWithCopy(intMatrixY);

    public static Matrix quantize (Matrix input, int blockSize, double quality, boolean matrixY) {
        Matrix quantizationMatrix = getQuantizationMatrix(blockSize, quality, matrixY);

        Matrix returnMatrix = new Matrix(input.getRowDimension(), input.getColumnDimension());

        for (int row = 0; row < input.getRowDimension(); row += blockSize) {
            for (int col = 0; col < input.getColumnDimension(); col += blockSize) {
                Matrix block = input.getMatrix(row,row + blockSize - 1, col, col + blockSize -1);

                for (int rowBlock = 0; rowBlock < block.getRowDimension(); rowBlock ++) {
                    for (int columnBlock =0; columnBlock < block.getColumnDimension(); columnBlock ++) {
                        double value = block.get(rowBlock, columnBlock) / quantizationMatrix.get(rowBlock, columnBlock);
                        block.set(rowBlock,columnBlock, value >= -0.2 && value <= 0.2 ? (double) Math.round(value*100)/100 : (double) Math.round(value*10)/10);
                    }
                }
                returnMatrix.setMatrix(row,row + blockSize - 1, col, col + blockSize -1,block);
            }
        }
        return returnMatrix;
    }

    public static Matrix inverseQuantize (Matrix input, int blockSize, double quality, boolean matrixY) {
        Matrix quantizationMatrix = getQuantizationMatrix(blockSize, quality, matrixY);

        Matrix returnMatrix = new Matrix(input.getRowDimension(), input.getColumnDimension());

        for (int row = 0; row < input.getRowDimension(); row += blockSize) {
            for (int col = 0; col < input.getColumnDimension(); col += blockSize) {
                Matrix block = input.getMatrix(row,row + blockSize - 1, col, col + blockSize -1);

                returnMatrix.setMatrix(row,row + blockSize - 1, col, col + blockSize -1,block.arrayTimes(quantizationMatrix));
            }
        }
        return returnMatrix;
    }

    public static Matrix getQuantizationMatrix (int blockSize, double quality, boolean matrixY) {
        if (quality == 100) {
            return new Matrix(blockSize, blockSize, 1);
        }

        Matrix returnMatrix;

        if (matrixY) {
            returnMatrix = matrixYval;
        } else {
            returnMatrix = matrixVal;
        }

        if (blockSize != 8) {
            switch (blockSize) {
                case 4:
                    returnMatrix = Sampling.downSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    returnMatrix = Sampling.downSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    break;
                case 16:
                    returnMatrix = Sampling.upSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    returnMatrix = Sampling.upSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    break;
                case 32:
                    returnMatrix = Sampling.upSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    returnMatrix = Sampling.upSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    returnMatrix = Sampling.upSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    returnMatrix = Sampling.upSample(returnMatrix);
                    returnMatrix = returnMatrix.transpose();
                    break;
            }
        }
        double alpha;
        if (quality < 50) {
            alpha = 50 / quality;
        } else {
            alpha = 2 - ((2 * quality) / 100);
        }
        return returnMatrix.times(alpha);
    }

    public static Matrix helperMatrix(Matrix matrix, Matrix matrixToSet, int row, int column) {
        matrix.setMatrix(row, matrixToSet.getRowDimension() + row,column,matrixToSet.getColumnDimension() + column, matrixToSet);
        return matrix;
    }
}
