module com.das.commitfinder {
    requires org.jetbrains.annotations;
    requires retrofit2;
    requires retrofit2.converter.gson;
    requires com.google.gson;

    exports com.das747.commitfinder.api;
}