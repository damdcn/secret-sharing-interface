compile:
	javac -encoding ISO-8859-1 --module-path libs --add-modules javafx.swing --add-modules javafx.controls -cp libs/opencv-452.jar:libs/jaxb-api-2.3.1.jar code/*.java

exec:
	java --module-path libs --add-modules javafx.swing,javafx.controls,javafx.fxml -cp :.libs/opencv-452.jar code.Main

clean:
	rm -f code/*.class
