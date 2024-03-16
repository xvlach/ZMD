package jpeg;
import Jama.Matrix;
import core.Helper;
import enums.ColorType;
import enums.QualityType;
import enums.SamplingType;
import enums.TransformType;
import graphics.Dialogs;

import java.awt.*;
import java.awt.image.BufferedImage;

import static enums.ColorType.*;

public class Process {

    private BufferedImage originalImage;
    private int imageHeight;
    private int imageWidth;
    private int [][] originalRed, modifiedRed;
    private int [][] originalGreen, modifiedGreen;
    private int [][] originalBlue, modifiedBlue;

    private Matrix originalY, modifiedY;
    private Matrix originalCb, modifiedCb;
    private Matrix originalCr, modifiedCr;

    public double mse, sae, mae, psnr, ssim, mssim;


    /**
     * Kontruktor
     * @param path Cesta k obrazku
     */
    public Process(String path) {

        this.originalImage = Dialogs.loadImageFromPath(path);

        imageWidth = originalImage.getWidth();
        imageHeight = originalImage.getHeight();

        originalRed = new int[imageHeight][imageWidth];
        originalGreen = new int[imageHeight][imageWidth];
        originalBlue = new int[imageHeight][imageWidth];

        originalY = new Matrix(imageHeight, imageWidth);
        originalCb = new Matrix(imageHeight, imageWidth);
        originalCr = new Matrix(imageHeight, imageWidth);
        setOriginalRGB();
    }
    /**
     * Naplnění matic barvama
     */
    private void setOriginalRGB(){
        for(int h = 0; h < imageHeight; h++){
            for (int w = 0; w < imageWidth; w++){
                Color color = new Color(originalImage.getRGB(w,h));
                originalRed[h][w] = color.getRed();
                originalGreen[h][w] = color.getGreen();
                originalBlue[h][w] = color.getBlue();
            }
        }
    }

    /**
     * Zobrazení obrázku
     * @return Vraci obrazek
     */
    public BufferedImage getImageFromRGB(){
        BufferedImage bfImage = new BufferedImage(
                imageWidth, imageHeight,
                BufferedImage.TYPE_INT_RGB);
        for(int h = 0; h < imageHeight; h++){
            for (int w = 0; w < imageWidth; w++) {
                bfImage.setRGB(w,h,
                        (new Color(modifiedRed[h][w],
                                modifiedGreen[h][w],
                                modifiedBlue[h][w])).getRGB());
            }
        }
        return bfImage;
    }
    /**
     * Zobrazi obrazek
     *
     * @param color Matice barvy
     * @param type Barva podle enumu
     * @return Vrací obrazek
     */
    public BufferedImage showOneColorImageFromRGB(int[][] color, enums.ColorType type)
    {
        BufferedImage bfImage = new BufferedImage(
                imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        for (int h = 0; h < imageHeight; h++) {
            for (int w = 0; w < imageWidth; w++) {
                switch (type) {
                    case RED:
                        bfImage.setRGB(w, h, (new Color(color[h][w], 0, 0)).getRGB());
                        break;
                    case GREEN:
                        bfImage.setRGB(w, h, (new Color(0, color[h][w], 0)).getRGB());
                        break;
                    case BLUE:
                        bfImage.setRGB(w, h, (new Color(0, 0, color[h][w])).getRGB());
                        break;
                }
            }
        }

        return bfImage;
    }

    /**
     * Zobrazi YCbCr obrázek
     *
     * @param color Matice barvy
     * @return Vrací obrazek
     */
    public BufferedImage showOneColorImageFromYCbCr(Matrix color)
    {

        int width = Helper.GetWidth(color);
        int height = Helper.GetHeight(color);

        BufferedImage bfImage = new BufferedImage(
                width, height,
                BufferedImage.TYPE_INT_RGB);
        for(int h = 0; h < height; h++){
            for (int w = 0; w < width; w++) {
                bfImage.setRGB(w,h,
                        (new Color(Helper.roundRange(color.get(h,w)),
                                Helper.roundRange(color.get(h,w)) ,
                                Helper.roundRange(color.get(h,w))).getRGB()));
            }
        }
        return bfImage;
    }

    /**
     * Convertuje do RGB
     */
    public void convertToRGB()
    {
        int[][][] pom = ColorTransform.convertModifiedYcBcRtoRGB(modifiedY, modifiedCb, modifiedCr);
        modifiedRed = pom[0];
        modifiedGreen = pom[1];
        modifiedBlue = pom[2];
    }
    /**
     * Convertuje do YCbCr
     */
    public void convertToYCbCr()
    {
        Matrix[] pom = ColorTransform.convertOriginalRGBtoYcBcR(originalRed, originalGreen, originalBlue);
        originalY = pom[0];
        originalCb = pom[1];
        originalCr = pom[2];

        modifiedY = originalY;
        modifiedCb = originalCb;
        modifiedCr = originalCr;
    }

    /**
     * Metoda provolávající downSample
     * @param samplingType hodnota na kterou chceme samplovat
     */
    public void sampleDown (SamplingType samplingType) {
        modifiedCb = Sampling.sampleDown(modifiedCb, samplingType);
        modifiedCr = Sampling.sampleDown(modifiedCr, samplingType);
    }

    /**
     * Metoda provádějící upSample
     * @param samplingType hodnota na kterou chceme samlovat
     */
    public void sampleUp (SamplingType samplingType) {
        modifiedCb = Sampling.sampleUp(modifiedCb, samplingType);
        modifiedCr = Sampling.sampleUp(modifiedCr, samplingType);
    }

    public BufferedImage showoriginalBlue()
    {
        return showOneColorImageFromRGB(originalBlue, BLUE);
    }
    public BufferedImage showOrigGreen()
    {
        return showOneColorImageFromRGB(originalGreen, GREEN);
    }
    public BufferedImage showOrigRed()
    {
        return showOneColorImageFromRGB(originalRed,  RED);
    }
    public BufferedImage showModifBlue()
    {
        return showOneColorImageFromRGB(modifiedBlue, BLUE);
    }
    public BufferedImage showModifGreen()
    {
        return showOneColorImageFromRGB(modifiedGreen, GREEN);
    }
    public BufferedImage showModifRed()
    {
        return showOneColorImageFromRGB(modifiedRed,  RED);
    }
    public BufferedImage showOrigY()
    {
        return  showOneColorImageFromYCbCr(originalY);
    }
    public BufferedImage showModifY()
    {
        return  showOneColorImageFromYCbCr(modifiedY);
    }
    public BufferedImage showOrigCb()
    {
        return  showOneColorImageFromYCbCr(originalCb);
    }
    public BufferedImage showModifCb()
    {
        return  showOneColorImageFromYCbCr(modifiedCb);
    }
    public BufferedImage showOrigCr()
    {
        return  showOneColorImageFromYCbCr(originalCr);
    }
    public BufferedImage showModifCr()
    {
        return  showOneColorImageFromYCbCr(modifiedCr);
    }

    public void count(QualityType qualityType) {
        double mseRed = 0, mseBlue = 0, mseGreen = 0, maeRed = 0, maeBlue = 0, maeGreen = 0, saeRed = 0, saeBlue = 0, saeGreen = 0, mseY = 0, maeY = 0, saeY = 0, mseCb = 0, maeCb = 0, saeCb = 0, mseCr = 0, maeCr = 0, saeCr = 0, ssimY = 0, ssimCb = 0, ssimCr = 0, mssimY = 0, mssimCb = 0, mssimCr = 0;

        if (qualityType == QualityType.RGB || qualityType == QualityType.Red || qualityType == QualityType.Green || qualityType == QualityType.Blue) {
            mseRed = Quality.countMSE(Helper.convertIntToDouble(originalRed), Helper.convertIntToDouble(modifiedRed));
            mseBlue = Quality.countMSE(Helper.convertIntToDouble(originalBlue), Helper.convertIntToDouble(modifiedBlue));
            mseGreen = Quality.countMSE(Helper.convertIntToDouble(originalGreen), Helper.convertIntToDouble(modifiedGreen));

            maeRed = Quality.countMAE(Helper.convertIntToDouble(originalRed), Helper.convertIntToDouble(modifiedRed));
            maeBlue = Quality.countMAE(Helper.convertIntToDouble(originalBlue), Helper.convertIntToDouble(modifiedBlue));
            maeGreen = Quality.countMAE(Helper.convertIntToDouble(originalGreen), Helper.convertIntToDouble(modifiedGreen));

            saeRed = Quality.countSAE(Helper.convertIntToDouble(originalRed), Helper.convertIntToDouble(modifiedRed));
            saeBlue = Quality.countSAE(Helper.convertIntToDouble(originalBlue), Helper.convertIntToDouble(modifiedBlue));
            saeGreen = Quality.countSAE(Helper.convertIntToDouble(originalGreen), Helper.convertIntToDouble(modifiedGreen));
        } else
            if (qualityType.equals(QualityType.YCbCr) || qualityType.equals(QualityType.Y) || qualityType.equals(QualityType.Cb) || qualityType.equals(QualityType.Cr)) {
                mseY = Quality.countMSE(originalY.getArray(), modifiedY.getArray());
                maeY = Quality.countMAE(originalY.getArray(), modifiedY.getArray());
                saeY = Quality.countSAE(originalY.getArray(), modifiedY.getArray());
                ssimY = Quality.countSSIM(originalY.getArray(), modifiedY.getArray());
                mssimY = Quality.countMSSIM(originalY.getArray(), modifiedY.getArray());

                mseCb = Quality.countMSE(originalCb.getArray(), modifiedCb.getArray());
                maeCb = Quality.countMAE(originalCb.getArray(), modifiedCb.getArray());
                saeCb = Quality.countSAE(originalCb.getArray(), modifiedCb.getArray());
                ssimCb = Quality.countSSIM(originalCb.getArray(), modifiedCb.getArray());
                mssimCb = Quality.countMSSIM(originalCb.getArray(), modifiedCb.getArray());

                mseCr = Quality.countMSE(originalCr.getArray(), modifiedCr.getArray());
                maeCr = Quality.countMAE(originalCr.getArray(), modifiedCr.getArray());
                saeCr = Quality.countSAE(originalCr.getArray(), modifiedCr.getArray());
                ssimCr = Quality.countSSIM(originalCr.getArray(), modifiedCr.getArray());
                mssimCr = Quality.countMSSIM(originalCr.getArray(), modifiedCr.getArray());
        }

        switch (qualityType){
            case Y:
                mse = mseY;
                mae = maeY;
                sae = saeY;
                psnr = Quality.countPSNR(mse);
                ssim = ssimY;
                mssim = mssimY;
                break;
            case Cb:
                mse = mseCb;
                mae = maeCb;
                sae = saeCb;
                psnr = Quality.countPSNR(mse);
                ssim = ssimCb;
                mssim = mssimCb;
                break;
            case Cr:
                mse = mseCr;
                mae = maeCr;
                sae = saeCr;
                psnr = Quality.countPSNR(mse);
                ssim = ssimCr;
                mssim = mssimCr;
                break;
            case Red:
                mse = mseRed;
                mae = maeRed;
                sae = saeRed;
                psnr = Quality.countPSNR(mse);
                break;
            case Blue:
                mse = mseBlue;
                mae = maeBlue;
                sae = saeBlue;
                psnr = Quality.countPSNR(mse);
                break;
            case Green:
                mae = maeGreen;
                sae = saeGreen;
                psnr = Quality.countPSNR(mse);
                break;
            case RGB:
                mse = (mseRed + mseBlue + mseGreen) / 3;
                mae = (maeRed + maeBlue + maeGreen) / 3;
                sae = (saeRed + saeBlue + saeGreen) / 3;
                psnr = Quality.countPSNRforRGB(mseRed, mseBlue, saeBlue);
                break;

            case YCbCr:
                mse = (mseY + mseCb + mseCr) / 3;
                mae = (maeY + maeCb + maeCr) / 3;
                sae = (saeY + saeCb + saeCr) / 3;
                psnr = Quality.countPSNR(mse);
                break;
        }
    }

    public void transform (TransformType transformType, int blockSize) {
        modifiedY = Transform.transform(modifiedY, transformType, blockSize);
        modifiedCb = Transform.transform(modifiedCb, transformType, blockSize);
        modifiedCr = Transform.transform(modifiedCr, transformType, blockSize);
    }

    public void inversionTransform (TransformType transformType, int blockSize) {
        modifiedY = Transform.inverseTransform(modifiedY, transformType, blockSize);
        modifiedCb = Transform.inverseTransform(modifiedCb, transformType, blockSize);
        modifiedCr = Transform.inverseTransform(modifiedCr, transformType, blockSize);
    }
}