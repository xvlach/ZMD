package jpeg;

import Jama.Matrix;
import enums.TransformType;

public class Transform {

    public static Matrix transform (Matrix input, TransformType type, int blockSize) {
        return helperMethodMatrixOperations(input, type, blockSize, false);
    }

    public static Matrix inverseTransform (Matrix input, TransformType type, int blockSize) {
        return helperMethodMatrixOperations(input, type, blockSize, true);
    }

    public static Matrix getTransformMatrix (TransformType type, int blockSize) {
        Matrix matrix;
        switch (type){
            case DCT:
                matrix = new Matrix(blockSize, blockSize);
                for (int i = 0; i < blockSize; i ++) {
                    for (int j = 0; j < blockSize; j ++) {
                        matrix.set(i,j,(Math.sqrt((double) (i == 0 ? 1 : 2) / blockSize) *
                                Math.cos(((2 * j + 1) * i * Math.PI) / (2 * blockSize))));
                    }
                }break;
            case WHT:
                matrix = new Matrix(2,2);
                matrix.set(0,0,1);
                matrix.set(0,1,1);
                matrix.set(1,0,1);
                matrix.set(1,1,-1);
                for (int i = 4; i <= blockSize; i *= 2){
                    Matrix newMatrix = new Matrix(i,i);
                    matrix = helperWHT(matrix, newMatrix, i);
                }
                matrix = matrix.times((double) 1/Math.sqrt(blockSize));
                break;
            default:
                matrix = null;
        }
        return matrix;
    }

    public static Matrix helperWHT (Matrix matrix, Matrix newMatrix, int num) {
        newMatrix.setMatrix(0,num / 2 - 1,0,num / 2 - 1, matrix);
        newMatrix.setMatrix(0,num / 2 - 1,num / 2,num - 1, matrix);
        newMatrix.setMatrix(num / 2,num - 1,0,num / 2 - 1, matrix);
        newMatrix.setMatrix(num / 2,num -1,num / 2,num-1, matrix.uminus());
        return newMatrix;
    }

    public static Matrix helperMethodMatrixOperations (Matrix input, TransformType type, int blockSize, boolean inverse) {
        Matrix returnMatrix = new Matrix(input.getRowDimension(), input.getColumnDimension());
        Matrix A = getTransformMatrix(type, blockSize);
        Matrix At = A.transpose();
        for (int row = 0; row < input.getRowDimension(); row += blockSize) {
            for (int col = 0; col < input.getColumnDimension(); col += blockSize) {
                Matrix block = input.getMatrix(row,row + blockSize - 1, col, col + blockSize -1);
                if (inverse) {
                    returnMatrix.setMatrix(row,row + blockSize - 1, col, col + blockSize -1,At.times(block).times(A));
                } else {
                    returnMatrix.setMatrix(row,row + blockSize - 1, col, col + blockSize -1,A.times(block).times(At));
                }
            }
        }
        return returnMatrix;
    }
}