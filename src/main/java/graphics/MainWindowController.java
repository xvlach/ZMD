package graphics;

import core.FileBindings;
import core.Helper;
import enums.QualityType;
import enums.SamplingType;
import enums.TransformType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jpeg.ColorTransform;
import jpeg.Process;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private TextField MAE;

    @FXML
    private TextField MSE;

    @FXML
    private TextField MSSIM;

    @FXML
    private TextField PSNR;

    @FXML
    private TextField SAE;

    @FXML
    private TextField SSIM;

    @FXML
    private Button buttonBlue;

    @FXML
    private Button buttonBlue2;

    @FXML
    private Button buttonCb;

    @FXML
    private Button buttonCb2;

    @FXML
    private Button buttonCountPSNR;

    @FXML
    private Button buttonCountSSIM;

    @FXML
    private Button buttonCr;

    @FXML
    private Button buttonCr2;

    @FXML
    private Button buttonDownSample;

    @FXML
    private Button buttonGreen;

    @FXML
    private Button buttonGreen2;

    @FXML
    private Button buttonIQuantize;

    @FXML
    private Button buttonITransform;

    @FXML
    private Button buttonOverSample;

    @FXML
    private Button buttonQuantize;

    @FXML
    private Button buttonRGB;

    @FXML
    private Button buttonRGBtoYCbCr;

    @FXML
    private Button buttonRed;

    @FXML
    private Button buttonRed2;

    @FXML
    private Button buttonShowImage;

    @FXML
    private Button buttonTransform;

    @FXML
    private Button buttonY;

    @FXML
    private Button buttonY2;

    @FXML
    private Button buttonYCbCrtoRGB;

    @FXML
    private CheckBox checkboxShadow;

    @FXML
    private CheckBox checkboxSteps;

    @FXML
    private ComboBox<QualityType> comboboxPSNR;

    @FXML
    private ComboBox<QualityType> comboboxSSIM;

    @FXML
    private Slider quantizeQuality;

    @FXML
    private TextField quantizeQualityField;

    @FXML
    Button buttonSample;

    @FXML
    Spinner<Integer> transformBlock;
    @FXML
    ComboBox<TransformType> transformType;
    @FXML
    ComboBox<SamplingType> sampling;

    private Process process;

    /**
     * Inicializace okna, nastavení výchozích hodnot. Naplnění prvků v rozhraní.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nastavení všech hodnot do combo boxů
        sampling.getItems().setAll(SamplingType.values());
        transformType.getItems().setAll(TransformType.values());
        comboboxPSNR.getItems().setAll(QualityType.values());
        comboboxSSIM.getItems().setAll(QualityType.Y,QualityType.Cb,QualityType.Cr);

        // Nastavení výchozích hodnot
        sampling.getSelectionModel().select(SamplingType.S_4_4_4);
        transformType.getSelectionModel().select(TransformType.DCT);
        comboboxPSNR.getSelectionModel().select(QualityType.Red);
        comboboxSSIM.getSelectionModel().select(QualityType.Y);

        quantizeQuality.setValue(50);

        // Vytvoření listu možností, které budou uvnitř spinneru
        ObservableList<Integer> blocks = FXCollections.observableArrayList(2, 4, 8, 16, 32, 64, 128, 256, 512);
        SpinnerValueFactory<Integer> spinnerValues = new SpinnerValueFactory.ListSpinnerValueFactory<>(blocks);
        spinnerValues.setValue(8);
        transformBlock.setValueFactory(spinnerValues);


        // Nastavení formátu čísel v textových polích, aby bylo možné zadávat pouze čísla. Plus metoda, která je na konci souboru.
        quantizeQualityField.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));

        // Propojení slideru s textovým polem
        quantizeQualityField.textProperty().bindBidirectional(quantizeQuality.valueProperty(), NumberFormat.getIntegerInstance());


        process = new Process(FileBindings.defaultImage);
    }

    /**
     * Jednotlivé metody pro napojené na frontend.
     * Všechny tlačítka by měly být ošetřeny try..catch, aby aplikace nespadla, když nastane vyjímka.
     */

    /**
     * Zavření okna.
     */
    @FXML
    public void close(ActionEvent event) {
        Stage stage = ((Stage) buttonSample.getScene().getWindow());
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    public void closeWindows() {
        Dialogs.closeAllWindows();
    }

    /**
     * Zobrazení originálního obrázku
     */
    @FXML
    public void showOriginal(ActionEvent event) {
        File f = new File(FileBindings.defaultImage);
        try {
            Dialogs.showImageInWindow(ImageIO.read(f), "Original", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TBD změna obrázku
     */
    @FXML
    public void changeImage(ActionEvent event) {
    }

    /**
     * TBD zrušení změn
     */
    @FXML
    public void reset(ActionEvent event) {
    }

    /**
     * Zobrazení upraveného RGB obrázku
     */
    @FXML
    public void showRGBModified(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.getImageFromRGB(), "Modified RGB", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Převod z grayscale do RBG
     */
    @FXML
    public void convertToRGB(ActionEvent event) {
        try {
            process.convertToRGB();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Převod z RGB do grayscale
     */
    @FXML
    public void convertToYCbCr(ActionEvent event) {
        try {
            process.convertToYCbCr();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void sample(ActionEvent event) {
        try {
            process.sampleDown(sampling.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void inverseSample(ActionEvent event) {
        try {
            process.sampleUp(sampling.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void transform(ActionEvent event) {


    }

    @FXML
    public void inverseTransform(ActionEvent event) {

    }

    @FXML
    public void quantize(ActionEvent event) {

    }

    @FXML
    public void inverseQuantize(ActionEvent event) {

    }

    /**
     * Zobrazení modré složky modifikovaného RGB obrázku
     */
    @FXML
    public void showBlueModified(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showModifBlue(), "Modified Blue", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení modré složky originálního RGB obrázku
     */
    @FXML
    public void showBlueOriginal(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showoriginalBlue(), "Original Blue", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení Cb složky modifikovaného YCbCr obrázku
     */
    @FXML
    public void showCbModified(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showModifCb(), "Modified Cb", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení Cb složky originálního YCbCr obrázku
     */
    @FXML
    public void showCbOriginal(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showOrigY(), "Original Cb", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení Cr složky modifikovaného YCbCr obrázku
     */
    @FXML
    public void showCrModified(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showModifCr(), "Modified Cr", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení Cr složky originálního YCbCr obrázku
     */
    @FXML
    public void showCrOriginal(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showOrigCr(), "Original Cr", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení zelené složky modifikovaného RGB obrázku
     */
    @FXML
    public void showGreenModified(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showModifGreen(), "Modified Green", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení zelené složky originálního RGB obrázku
     */
    @FXML
    public void showGreenOriginal(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showOrigGreen(), "Original Green", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení červené složky modifikovaného RGB obrázku
     */
    @FXML
    public void showRedModified(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showModifRed(), "Modified Red", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení červené složky originálního RGB obrázku
     */
    @FXML
    public void showRedOriginal(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showOrigRed(), "Original Red", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení Y složky modifikovaného YCbCr obrázku
     */
    @FXML
    public void showYModified(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showModifY(), "Modified Y", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zobrazení Y složky originálního YCbCr obrázku
     */
    @FXML
    public void showYOriginal(ActionEvent event) {
        try {
            Dialogs.showImageInWindow(process.showOrigY(), "Original Y", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void countPSNR(ActionEvent event) {
        process.count(comboboxPSNR.getValue());
        MSE.textProperty().set(String.format("%.4f", process.mse));
        MAE.textProperty().set(String.format("%.4f", process.mae));
        SAE.textProperty().set(String.format("%.4f", process.sae));
        PSNR.textProperty().set(String.format("%.4f", process.psnr));
    }

    @FXML
    public void countSSIM(ActionEvent event) {
        process.count(comboboxSSIM.getValue());
        SSIM.textProperty().set(String.format("%.4f", process.ssim));
        MSSIM.textProperty().set(String.format("%.4f", process.mssim));
    }
}