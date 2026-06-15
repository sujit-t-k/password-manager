module org.ajikhoji.passwordmanager {
    requires javafx.controls;
    requires java.sql;
    requires com.opencsv;
    requires org.apache.commons.logging;

    opens org.ajikhoji.passwordmanager to javafx.base;
    opens org.ajikhoji.passwordmanager.dto to javafx.base;
    opens org.ajikhoji.passwordmanager.model to javafx.base;
    exports org.ajikhoji.passwordmanager;
}