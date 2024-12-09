package project.asm;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.scene.control.Label;


/**
 *
 * Главный класс (Analyzer), который получает на вход путь до class-файла,
 * получает его байт-код, выводит статистическую информацию байт-кода:
 * кол-во опкодов циклов, условных переходов
 * и объявления переменных
 *
 * Класс Analyzer, наследующийся от класса Application
 * Класс Application позволяет использовать графический интерфейс
 */
public class Analyzer extends Application {
    private int loopOpcodesCount;
    private int conditionalOpcodesCount;
    private int variableDeclarationsCount;


    /**
     *
     * Конструктор класса Analyzer
     */
    public Analyzer() {
        loopOpcodesCount = 0;
        conditionalOpcodesCount = 0;
        variableDeclarationsCount = 0;
    }

    /**
     *
     * Открывает графический интерфейс
     *
     * @param stage  the stage.
     */

    @Override
    public void start(Stage stage) {

        Button chooseFileButton = new Button("Выбрать .class файл");
        Label resultsLabel = new Label("");

        chooseFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите .class файл");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Class Files", "*.class"));
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                try {
                    analyzeAndDisplay(selectedFile, resultsLabel);
                } catch (IOException e) {
                    resultsLabel.setText("Ошибка при анализе файла: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        VBox vbox = new VBox(10, chooseFileButton, resultsLabel);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 400, 200);
        stage.setScene(scene);
        stage.setTitle("Bytecode Analyzer");
        stage.show();
    }
    /**
     *
     * Analyze and display
     *
     * @param file  входной файл для программы
     * @param resultsLabel  the results label.
     * @throws   IOException если файл не найден
     */
    private void analyzeAndDisplay(File file, Label resultsLabel) throws IOException {

        loopOpcodesCount = 0;
        conditionalOpcodesCount = 0;
        variableDeclarationsCount = 0;


        analyzeClass(file);
        String results = String.format(
                "Количество опкодов циклов: %d\nУсловных переходов: %d\nОбъявления переменных: %d",
                getLoopOpcodesCount(), getConditionalOpcodesCount(), getVariableDeclarationsCount()
        );
        resultsLabel.setText(results);
    }

    /**
     *
     * Анализ класса
     *
     * @param file  входной файл
     * @throws   IOException если файл не найден
     */
    public void analyzeClass(File file) throws IOException {

        try (FileInputStream fis = new FileInputStream(file)) {
            ClassReader reader = new ClassReader(fis);
            reader.accept(new ClassVisitor(Opcodes.ASM9) {


                /**
                 *
                 * Метод вызывается для каждого метода в классе.
                 *
                 * @param access  модификаторы доступа метода
                 * @param name  имя метода
                 * @param desc  дескриптор метода (описывает типы аргументов и тип возвращаемого значения)
                 * @param signature  дженерик-сигнатура метода (если есть)
                 * @param exceptions  список исключений, которые метод может бросать
                 * @return возвращаем анонимный MethodVisitor
                 */
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    return new MethodVisitor(Opcodes.ASM9) {
                        /**
                         *
                         * Метод вызывается для каждой инструкции, которая работает с локальной переменной
                         *
                         * @param opcode  код операции (opcode) инструкции
                         * @param var  индекс локальной переменной, с которой работает инструкция
                         */

                        @Override
                        public void visitVarInsn(int opcode, int var) {

                            if (opcode == Opcodes.ILOAD || opcode == Opcodes.LLOAD || opcode == Opcodes.FLOAD || opcode == Opcodes.DLOAD ||
                                    opcode == Opcodes.ISTORE || opcode == Opcodes.LSTORE || opcode == Opcodes.FSTORE || opcode == Opcodes.DSTORE ||
                                    opcode == Opcodes.ASTORE) {
                                variableDeclarationsCount++;
                            }
                        }


                        /**
                         *
                         * Метод вызывается для каждой инструкции перехода
                         *
                         * @param opcode  код операции инструкции перехода
                         * @param label  метка (Label) в байт-коде, на которую происходит переход, в случае если условие выполняется
                         */

                        public void visitJumpInsn(int opcode, org.objectweb.asm.Label label) {

                            super.visitJumpInsn(opcode, label);

                            if (opcode == Opcodes.GOTO) {
                                loopOpcodesCount++;
                            }
                            if (opcode == Opcodes.IFEQ || opcode == Opcodes.IFNE || opcode == Opcodes.IFLT ||
                                    opcode == Opcodes.IFGE || opcode == Opcodes.IFGT || opcode == Opcodes.IFLE ||
                                    opcode == Opcodes.IF_ICMPEQ || opcode == Opcodes.IF_ICMPNE ||
                                    opcode == Opcodes.IF_ICMPLT || opcode == Opcodes.IF_ICMPGE ||
                                    opcode == Opcodes.IF_ICMPGT || opcode == Opcodes.IF_ICMPLE ||
                                    opcode == Opcodes.IF_ACMPEQ || opcode == Opcodes.IF_ACMPNE ||
                                    opcode == Opcodes.IFNULL || opcode == Opcodes.IFNONNULL) {
                                conditionalOpcodesCount++;
                            }
                        }
                    };
                }
            }, 0);
        }
    }


    /**
     *
     * Получение количества опкодов циклов
     *
     * @return количество опкодов циклов
     */
    public int getLoopOpcodesCount() {
        return loopOpcodesCount;
    }

    /**
     * Сеттер для опкодов цикла (используется только для тестирования)
     * @param number
     */

    public void setLoopOpcodesCount(int number) {
        this.loopOpcodesCount = number;
    }


    /**
     *
     * Получение количества условных опкодов класса
     *
     * @return количество условных опкодов класса
     */
    public int getConditionalOpcodesCount() {
        return conditionalOpcodesCount;
    }

    /**
     * Сеттер для условных опкодов (используется только для тестирования)
     * @param number устанавливаемое число
     */

    public void setConditionalOpcodesCount(int number) {
        this.conditionalOpcodesCount = number;
    }


    /**
     *
     * Получения количества объявленных переменных
     *
     * @return количество объявленных переменных
     */
    public int getVariableDeclarationsCount() {
        return variableDeclarationsCount;
    }

    /**
     * Сеттер для переменных (используется только для тестирования)
     * @param number устанавливаемое число
     */
    public void setVariableDeclarationsCount(int number) {
        this.variableDeclarationsCount = number;
    }

    /**
     *
     * Точка входа
     *
     * @param args args
     */
    public static void main(String[] args) {

        launch(args);
    }
}

