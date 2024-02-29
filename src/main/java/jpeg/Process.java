package jpeg;
import Jama.Matrix;
import core.Helper;
import enums.ColorType;
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
                        (new Color((int)color.get(h,w),
                                (int)color.get(h,w),
                                (int)color.get(h,w)).getRGB()));
            }
        }
        return bfImage;
    }

    /**
     * Convertuje do RGB
     */
    public void convertToRGB()
    {
        int[][][] pom = ColorTransform.convertModifiedYcBcRtoRGB(originalY, originalCb, originalCr);
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

    public BufferedImage showOrigBlue()
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

}
