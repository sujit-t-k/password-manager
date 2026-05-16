module org.ajikhoji.passwordmanager {
    requires javafx.controls;
    requires java.sql;

    opens org.ajikhoji.passwordmanager to javafx.base;
    opens org.ajikhoji.passwordmanager.model to javafx.base;
    exports org.ajikhoji.passwordmanager;
}