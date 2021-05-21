package application;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;


public class Main extends Application {

	// Scene globale
	private static Pane root;
	// Image publique
	public static ImageView image = new ImageView();
	// Images vu par les personnes
	public static ArrayList<ImageView> images = new ArrayList<ImageView>();
	// Chemin vers l'image à traiter
	public String imagePath = null;
	// Texte de notification en haut
	public static Text msgBox = null;
	// Parseur de détection de visage
	public static FaceDetectionImage fdi = null;
	// Objet pour l'algo de cryptage
	public static Partage_secrets ps = null;
	// Seuil de révélation
	public static int seuil_de_revelation = -1;
	// Liste des personnes détectées
	public static ArrayList<Person> personList = new ArrayList<Person>();
	// Coefficient de réduction pour que l'image "fit" la zone prévue
	public static double reducCoeff = 0;


	// Centre l'image dans la zone prévu
	public void centerImage(ImageView image) {
		Image img = image.getImage();
		if (img != null) {
			double w = 0;
			double h = 0;

			double ratioX = image.getFitWidth() / img.getWidth();
			double ratioY = image.getFitHeight() / img.getHeight();

			reducCoeff = 0;
			if(ratioX >= ratioY) {
				reducCoeff = ratioY;
			} else {
				reducCoeff = ratioX;
			}

			w = img.getWidth() * reducCoeff;
			h = img.getHeight() * reducCoeff;
			
			image.setTranslateX((image.getFitWidth() - w) / 2);
			image.setTranslateY((image.getFitHeight() - h) / 2);
		}
	}

	// Supprime tous les fichiers dans /images
	public void deleteFiles(Path p) {
		File path = new File(p.toString());
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				files[i].delete();
			}
		}
	}

	// Méthode permettant de parser nos données et générer un fichier XML avec les méta-données (coordonnées/status des visages) 
	public static void jaxbToXML(FaceDetectionImage fdi,ArrayList<Person> personList) {
		JAXBContext contextObj;
		ArrayList<Person> persons =new  ArrayList<Person>();
		try {
			HashMap<Person,ArrayList<Point>> data = new HashMap<Person,ArrayList<Point>>();
			contextObj = JAXBContext.newInstance(XMLData.class);
			Marshaller marshallerObj = contextObj.createMarshaller();  
			marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  

			for(int i = 0;i<fdi.getFacesNumber();i++) {
				Person p=personList.get(i);
				p.setPoints(fdi.getPoints().get(i));
				p.getPoints().get(0).setInfos("TL");
				p.getPoints().get(1).setInfos("BR");
				persons.add(p);

			}

			XMLData x = new XMLData(persons);
			marshallerObj.marshal(x, new File("./src/xmldata/data.xml"));
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
	}


	// Fonction appelé lorsqu'on clique sur un bouton pour crypter/décrypter une personne
	static public void toggleSelection(int i) {
		if(!personList.get(i).isRevealed()) {
			System.out.println("Révélation du perso n°"+(i+1));
			// Révèle un visage
			ps.reveal(i+1);
			personList.get(i).setRevealed(true);

			// Charge les nouvelle image avec le visage décrypté
			String cryptedPath = Paths.get("images/0.png").toAbsolutePath().toString();
			Image cryptedImage = new Image("file:"+cryptedPath);
			image.setImage(cryptedImage);
			for (int j = 0; j < fdi.getFacesNumber(); j++) {
				cryptedPath = Paths.get("images/"+(j+1)+".png").toAbsolutePath().toString();
				cryptedImage = new Image("file:"+cryptedPath);	
			}

			// On check si le seuil est atteint
			int nb_revealed = 0;
			for (int j = 0; j < fdi.getFacesNumber(); j++) {	
				if(personList.get(j).isRevealed()) nb_revealed++;
			}

			// Si oui tout va être révélé donc on met tous les boutons vert
			if(nb_revealed >= seuil_de_revelation) {
				for (int j = 0; j < fdi.getFacesNumber(); j++) {	
					personList.get(j).getButton().setSelected(true);
					if(!personList.get(j).isRevealed()) personList.get(j).setRevealed(true);
				}
				msgBox.setText("Seuil atteint !");
				System.out.println("Révélation du reste des persos (seuil atteint)");
			}
		} else {
			// Recrypter la personne
		}

		// Update le fichier XML (coords/status)
		jaxbToXML(fdi, personList);
	}




	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Pane();
			Scene scene = new Scene(root,1280,720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Social Security Interface"); 	// Nom de la fenêtre
			primaryStage.setResizable(false); 						// Taille de la fenêtre fixe
			primaryStage.getIcons().add(new Image("/logo.png")); 	// Icône de la fênetre

			// Zone où va être acceuilli l'image
			Rectangle area = new Rectangle(0, 0, 896, 504);
			area.setLayoutX(100);
			area.setLayoutY(80);
			area.setArcWidth(25);
			area.setArcHeight(25);
			area.setFill(Color.TRANSPARENT);
			area.setStroke(Color.web("#3c3c3c"));
			area.setStrokeType(StrokeType.OUTSIDE);
			area.setStrokeWidth(5);





			// Placement de l'ImageView
			image.setPreserveRatio(true);
			image.setLayoutX(area.getLayoutX());
			image.setLayoutY(area.getLayoutY());
			image.setFitHeight(area.getHeight());
			image.setFitWidth(area.getWidth());


			// Définition des Bouton et zone de text
			msgBox = new Text("");
			Button loadImage = new Button("Importer une image");
			Button processImage = new Button("Détecter les visages");
			Button cryptImage = new Button("Crypter les visages");
			Button downloadImage = new Button("Télécharger l'image");
			Button resetImage = new Button("Réinitialiser");

			// Placement des boutons
			msgBox.setLayoutX(area.getLayoutX()+350);
			msgBox.setLayoutY(area.getLayoutY()-25);
			msgBox.setId("upperText");
			loadImage.setLayoutX(area.getLayoutX());
			loadImage.setLayoutY(area.getLayoutY()-70);
			processImage.setLayoutX(area.getLayoutX());
			processImage.setLayoutY(area.getLayoutY()+area.getHeight()+10);
			cryptImage.setLayoutX(area.getLayoutX()); // +240
			cryptImage.setLayoutY(area.getLayoutY()+area.getHeight()+10);
			resetImage.setLayoutX(area.getLayoutX());
			resetImage.setLayoutY(area.getLayoutY()+area.getHeight()+10);
			downloadImage.setLayoutX(area.getLayoutX()+area.getWidth()-225);
			downloadImage.setLayoutY(area.getLayoutY()+area.getHeight()+10);

			
			// Onglet dans lequel est l'image publique
			Tab tabMain = new Tab();
			tabMain.setText("Publique");
			tabMain.setContent(image);
			
			// Zone de selection des onglets
			TabPane tp = new TabPane();
			tp.getTabs().add(tabMain);
			tp.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
			tp.setSide(Side.LEFT);
			tp.setLayoutX(area.getLayoutX()-32);
			tp.setLayoutY(area.getLayoutY());
			tp.setMaxHeight(area.getHeight());
			tp.setMinHeight(area.getHeight());
			
//			tabMain.setOnSelectionChanged(e->{
//				System.out.println("Tab Main Selected");
//			});
			
			root.getChildren().add(tp);


			// Element dans lequel vont être acceuilli, les boutons de rélévalation
			GridPane buttonsContainer = new GridPane();
			buttonsContainer.setVgap(5);
			buttonsContainer.setPadding(new Insets(5));
			
			
			// Element scrollable pour acceuillir l'élément ci-dessus
			ScrollPane sp = new ScrollPane();
			sp.setLayoutX(1030);
			sp.setLayoutY(50);
			sp.setPrefSize(240, 615);
			sp.setContent(buttonsContainer);
			sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
			sp.setStyle("-fx-box-border: transparent;");

			// OnClick "Importer une image"
			loadImage.setOnMouseClicked(e->{

				// On supprime le contenu de /images/ pour éviter les conflits
				deleteFiles(Paths.get("images/").toAbsolutePath());

				FileChooser filechooser = new FileChooser();
				File file = filechooser.showOpenDialog(primaryStage);
				if(file!=null) {
					imagePath = file.getAbsolutePath();
					Image importedImage = new Image("file:"+imagePath);
					image.setImage(importedImage);
					centerImage(image);
					if(!root.getChildren().contains(processImage)) root.getChildren().addAll(processImage);
					if(root.getChildren().contains(cryptImage)) root.getChildren().removeAll(cryptImage);
					if(root.getChildren().contains(resetImage)) root.getChildren().removeAll(resetImage);
					if(root.getChildren().contains(downloadImage)) root.getChildren().removeAll(downloadImage);


					// On clear la list de personne si elle n'est pas vide
					if(!personList.isEmpty()) {
						for (int i = 0; i < personList.size(); i++) {	
							root.getChildren().removeAll(personList.get(i).getRegion());
						}
						buttonsContainer.getChildren().clear();
						personList.clear();
						tp.getTabs().remove(1, tp.getTabs().size());
					}
					msgBox.setText("");

					try {
						RenderedImage ri = SwingFXUtils.fromFXImage(image.getImage(), null);
						Files.copy(Paths.get(imagePath), Paths.get("images/0.png").toAbsolutePath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});

			// OnClick "Détecter les visages"
			processImage.setOnMouseClicked(e->{
				fdi = new FaceDetectionImage(imagePath);
				WritableImage img = fdi.process();
				image.setImage(img);
				msgBox.setText("Nombre de visage détecté : "+fdi.getFacesNumber());
				if(!root.getChildren().contains(downloadImage)) {
					root.getChildren().addAll(downloadImage, cryptImage);
				}
				root.getChildren().removeAll(processImage);
				for (int i = 0; i < fdi.getFacesNumber(); i++) {	
					personList.add(new Person(i, area));
				}				
			});

			// OnClick "Crypter les visages"
			cryptImage.setOnMouseClicked(e->{

				ArrayList<Coordonnee_visage> coordonnee_visages = new ArrayList<Coordonnee_visage>();
				int nombre_de_personne = fdi.getFacesNumber();
				scene.setCursor(Cursor.WAIT);

				// Popup pour définir le seuil
				TextInputDialog dialog = new TextInputDialog("3");
				dialog.setHeaderText(null);
				dialog.setTitle("Choix du seuil");
				dialog.setContentText("Entrez le seuil de découverte : ");

				Optional<String> result = dialog.showAndWait();

				String s = result.get();
				int seuil = Integer.parseInt(s);
				
				// Seuil valide
				if(seuil > 0 && seuil <= nombre_de_personne) {
					seuil_de_revelation = seuil;
					root.getChildren().removeAll(cryptImage);
					root.getChildren().addAll(resetImage);
					msgBox.setText("Seuil défini à "+seuil+" personne(s).");

					for (int i = 0; i < nombre_de_personne; i++) {	
						coordonnee_visages.add(new Coordonnee_visage(fdi.getPoints().get(i).get(0).getX(), fdi.getPoints().get(i).get(0).getY(), fdi.getPoints().get(i).get(1).getX(), fdi.getPoints().get(i).get(1).getY()));
						// System.out.println((fdi.getPoints().get(i).get(0).getX()+" ; "+fdi.getPoints().get(i).get(0).getY()+" | "+ fdi.getPoints().get(i).get(1).getX()+" ; "+ fdi.getPoints().get(i).get(1).getY()));
					}

					ps = new Partage_secrets(coordonnee_visages,nombre_de_personne,seuil_de_revelation);
					ps.creation_images_secretes();			        

					// Chargement de l'image cryptée publique
					String cryptedPath = Paths.get("images/0.png").toAbsolutePath().toString();
					Image cryptedImage = new Image("file:"+cryptedPath);
					image.setImage(cryptedImage);
					
					// Création et chargment des images cryptées vue par chacune des personnes
					for (int i = 0; i < nombre_de_personne; i++) {
						images.add(new ImageView());
						cryptedPath = Paths.get("images/"+(i+1)+".png").toAbsolutePath().toString();
						cryptedImage = new Image("file:"+cryptedPath);
						images.get(i).setPreserveRatio(true);
						images.get(i).setLayoutX(area.getLayoutX());
						images.get(i).setLayoutY(area.getLayoutY());
						images.get(i).setFitHeight(area.getHeight());
						images.get(i).setFitWidth(area.getWidth());
						images.get(i).setImage(cryptedImage);
						centerImage(images.get(i));
						
					}

					
					// Génère le premier fichier XML (coords)
					jaxbToXML(fdi,personList);
					
					// Ajoute autant d'onglet que de personne
					for (int i = 0; i < nombre_de_personne; i++) {	
						personList.get(i).getTab().setContent(images.get(i));
						tp.getTabs().add(personList.get(i).getTab());
					}

					// Ajoute les boutons de révélation dans son container
					for (int i = 0; i < nombre_de_personne; i++) {	
						buttonsContainer.add(personList.get(i).getButton(), 0, i); // add(button, col, row)
					}

					
					// Cadre et affiche les rectangles de hover
					for (int i = 0; i < nombre_de_personne; i++) {
						personList.get(i).scaleRect(image.getTranslateX(), image.getTranslateY(), reducCoeff);
						root.getChildren().addAll(personList.get(i).getRegion());
					}

				// Seuil inférieur à zéro ou suppérieur au nombre de personne
				} else {
					msgBox.setText("Seuil invalide...");
				}
				scene.setCursor(Cursor.DEFAULT);
			});


			// OnClick "Télécharger"
			downloadImage.setOnMouseClicked(e->{
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png"));
				File file = fileChooser.showSaveDialog(primaryStage);

				if(file != null){			    	
					try {
						RenderedImage ri = SwingFXUtils.fromFXImage(image.getImage(), null);
						ImageIO.write(ri, "png", file);
					} catch (IOException e1) {
						// TODO Bloc catch généré automatiquement
						e1.printStackTrace();
					}
				}
			});

			// OnClick "Reset"
			resetImage.setOnMouseClicked(e->{
				// On clear la list de personne si elle n'est pas vide
				if(!personList.isEmpty()) {
					for (int i = 0; i < personList.size(); i++) {	
						root.getChildren().removeAll(personList.get(i).getRegion());
						images.get(i).setImage(null);
					}
					buttonsContainer.getChildren().clear();
					personList.clear();
					images.clear();
					tp.getTabs().remove(1, tp.getTabs().size());
				}
				if(root.getChildren().contains(processImage)) root.getChildren().removeAll(processImage);
				if(root.getChildren().contains(resetImage)) root.getChildren().removeAll(resetImage);
				if(root.getChildren().contains(cryptImage)) root.getChildren().removeAll(cryptImage);
				if(root.getChildren().contains(downloadImage)) root.getChildren().removeAll(downloadImage);
				msgBox.setText("");
				image.setImage(null);
			});

			root.getChildren().addAll(area, image, msgBox, sp, loadImage);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
