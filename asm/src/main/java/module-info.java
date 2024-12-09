/**
 * Модуль проекта asm.
 */

module project.asm {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.objectweb.asm.util;
    requires java.compiler;


    opens project.asm to javafx.fxml;
    exports project.asm;
}