module com.das.commitfinder {
    requires org.jetbrains.annotations;
    requires retrofit2;
    requires retrofit2.converter.gson;
    requires com.google.gson;
    requires okhttp3;
    requires org.slf4j;

    exports com.das747.commitfinder.api;
}