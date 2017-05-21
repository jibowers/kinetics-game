

import com.aquafx_project.AquaFx;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//import org.aerofx.AeroFX;

import java.io.*;
import java.util.ArrayList;

public class Main extends Application implements EventHandler<ActionEvent>{

    private int lawWins = 0;
    private int lawLosses = 0;
    private int mechWins = 0;
    private int mechLosses = 0;
    private int molecWins = 0;
    private int molecLosses = 0;

    private boolean lawHasBeenWon = false;
    private boolean mechHasBeenWon = false;
    private boolean molecHasBeenWon = false;

    private double[] rateLaw;
    private TextField[][] fields = new TextField[3][2];

    private Label mechInstructions = new Label("Propose a mechanism for this reaction");
    private Label lawInstructions = new Label("Determine k, m, and n from the data above\n\tR = k[A]^m[B]^n");


    private Label reactL = new Label("Reactants");
    private Label yieldsL = new Label("-->");
    private Label yieldsSym = new Label("-->");
    private Label productsL = new Label("Products");

    private ToggleGroup speed = new ToggleGroup();
    private RadioButton slow1 = new RadioButton("Slow");
    private RadioButton slow2 = new RadioButton("Slow");
    private RadioButton slow3 = new RadioButton("Slow");
    private RadioButton allFast = new RadioButton("All fast");


    private Label kL = new Label("k (to 5% accuracy):");
    private Label mL = new Label("m:");
    private Label nL = new Label("n:");

    private TextField kF = new TextField();
    private TextField mF = new TextField();
    private TextField nF = new TextField();


    private Text[][] experimental = new Text[3][3];

    private Text molecularityInstructions = new Text("Choose the molecularity of this reaction");
    private ToggleGroup molecularityToggle = new ToggleGroup();
    private RadioButton uni = new RadioButton("Unimolecular");
    private RadioButton bi = new RadioButton("Bimolecular");
    private RadioButton tri = new RadioButton("Trimolecular");


    private Button checkMech = new Button("Check answer");
    private Button checkLaw = new Button("Check answer");
    private Button refresh = new Button("New Game");
    private Button clearMech = new Button("Clear");
    private Button checkMolecularity = new Button("Check answer");


    private Menu menu1 = new Menu("Game");
    //final Menu menu2 = new Menu("Options");
    //final Menu menu3 = new Menu("Help");
    private MenuItem statsMenuItem = new MenuItem("Open Stats");
    private MenuItem instructionsMenuItem = new MenuItem("Instructions");
    private MenuItem resetMenuItem = new MenuItem("Reset All");
    // ADD ITEM TO RESET EVERYTHING


    private MenuBar menuBar = new MenuBar();


    private Text lawCorrect = new Text("Yay you got it right!");
    private Text lawIncorrect = new Text("Try again");

    private Text mechCorrect = new Text("Good job!");
    private Text mechIncorrect = new Text("Aw man, give it another shot");

    private Text molecCorrect = new Text("Correct!");
    private Text molecIncorrect = new Text("Try again");


    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Kinetics Game!!!");
        primaryStage.setOnCloseRequest(we -> {
            //System.out.println("Stage is closing");
            saveToFile();
        });

        //
        AquaFx.style();
        //AeroFX.style();
        VBox vbox = new VBox(8);

        vbox.setAlignment(Pos.TOP_CENTER);
        GridPane expGrid = new GridPane();

        expGrid.setVgap(20);
        expGrid.setHgap(20);

        retrieveFromFile();

        for (TextField[] r: fields){
            r[0] = new TextField("2A,B");
            r[0].setPrefColumnCount(5);
            r[1] = new TextField("C,D");
            r[1].setPrefColumnCount(5);
        }

        refreshRateLaw();

        expGrid.add(new Text("Experimental Rate Law Data"), 1, 0, 3, 1);
        expGrid.add(new Text("[A], M"), 1, 1);
        expGrid.add(new Text("[B], M"), 2, 1);
        expGrid.add(new Text("Rate, M/s"), 3, 1);


        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {
                expGrid.add(experimental[i][j], i+1, j+2);
            }
        }

        slow1.setToggleGroup(speed);
        slow2.setToggleGroup(speed);
        slow3.setToggleGroup(speed);
        allFast.setToggleGroup(speed);

        checkMech.setOnAction(this);
        checkLaw.setOnAction(this);
        refresh.setOnAction(this);
        clearMech.setOnAction(this);
        checkMolecularity.setOnAction(this);


        Color rightC = Color.GREEN;
        Color wrongC = Color.RED;

        lawCorrect.setFill(rightC);
        mechCorrect.setFill(rightC);
        molecCorrect.setFill(rightC);
        lawIncorrect.setFill(wrongC);
        mechIncorrect.setFill(wrongC);
        molecIncorrect.setFill(wrongC);


        TabPane tabs = new TabPane();
        Tab mechanismTab = new Tab();
        GridPane mechanismPane = new GridPane();
        mechanismTab.setText("Mechanism");
        mechanismTab.setContent(mechanismPane);


        mechanismPane.add(mechInstructions, 0, 0, 4, 1);

        mechanismPane.add(reactL, 0, 1);
        mechanismPane.add(yieldsL, 1, 1);
        mechanismPane.add(productsL, 2, 1);
        mechanismPane.add(yieldsSym, 1, 2);

        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 2; j++){
                mechanismPane.add(fields[i][j], j*2, i+2);  // TODO FLIP??? idk check this
            }
        }

        mechanismPane.add(slow1, 3, 2);
        mechanismPane.add(slow2, 3, 3);
        mechanismPane.add(slow3, 3, 4);
        mechanismPane.add(allFast, 3, 5);
        mechanismPane.add(checkMech, 3, 6);
        mechanismPane.add(clearMech, 2, 6);

        mechanismPane.add(mechCorrect, 0, 7, 2, 1);
        mechanismPane.add(mechIncorrect, 2, 7, 2, 1);


        Tab rateLawTab = new Tab();
        GridPane rateLawPane = new GridPane();
        rateLawTab.setText("Rate Law");
        rateLawTab.setContent(rateLawPane);

        rateLawPane.setHgap(10);
        rateLawPane.setVgap(10);
        rateLawPane.add(lawInstructions, 0, 0, 3, 1);

        kF.setMaxSize(150, 2);
        mF.setMaxSize(30, 2);
        nF.setMaxSize(30, 2);
        rateLawPane.add(kL, 1, 1);
        rateLawPane.add(kF, 2, 1);
        rateLawPane.add(mL, 1, 2);
        rateLawPane.add(mF, 2, 2);
        rateLawPane.add(nL, 1, 3);
        rateLawPane.add(nF, 2, 3);

        rateLawPane.add(checkLaw, 0, 4, 3, 1);

        rateLawPane.add(lawCorrect, 0, 5, 2, 1);
        rateLawPane.add(lawIncorrect, 2, 5, 2, 1);


        Tab molecularityTab = new Tab();
        GridPane molecularityPane = new GridPane();
        molecularityTab.setText("Molecularity");
        molecularityTab.setContent(molecularityPane);

        molecularityPane.setVgap(10);
        molecularityPane.setHgap(10);

        uni.setToggleGroup(molecularityToggle);
        bi.setToggleGroup(molecularityToggle);
        tri.setToggleGroup(molecularityToggle);

        molecularityPane.add(molecularityInstructions, 0, 0, 3, 1);
        molecularityPane.add(uni, 1, 1);
        molecularityPane.add(bi, 1, 2);
        molecularityPane.add(tri, 1, 3);
        molecularityPane.add(checkMolecularity, 1, 4);
        molecularityPane.add(molecCorrect, 1, 5);
        molecularityPane.add(molecIncorrect, 2, 5);


        tabs.getTabs().add(rateLawTab);
        tabs.getTabs().add(mechanismTab);
        tabs.getTabs().add(molecularityTab);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);


        menu1.getItems().addAll(statsMenuItem, instructionsMenuItem, resetMenuItem);
        statsMenuItem.setOnAction(this);
        instructionsMenuItem.setOnAction(this);
        resetMenuItem.setOnAction(this);
        menuBar.getMenus().addAll(menu1);

        // add to main vBox
        vbox.getChildren().add(menuBar);
        vbox.getChildren().add(expGrid);
        vbox.getChildren().add(new Separator());
        vbox.getChildren().add(tabs);
        vbox.getChildren().add(refresh);

        Scene primaryScene = new Scene(vbox, 300, 475, Color.GHOSTWHITE);
        //primaryScene.setUserAgentStylesheet("/MaterialFX.css");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    public double[] generateRateLaw(){
        double k = Math.random();
        int m = (int) Math.floor(Math.random() * 3);    // between 0 and 2
        int n = (int) Math.floor(Math.random() * (3-m));    // between 0 and 2, but can't be 2 if m is also 2
        while (m == 0 && n ==0){                // make sure both aren't equal to 0
            n = (int) Math.floor(Math.random() * (3-m));
        }
        return new double[] {k, m, n};
    }

    public String sciNotation(double num){
        return String.format("%6.3e", num);
    }

    public String[][] generateData(double[] rateLaw){
        // make sure to round and to put in sci notation
        /*
        [A] [B]  rate
        0.1 0.1  x
        0.2 0.1  y
        0.1 0.2  z
         */
        double initialA = (double)Math.round(Math.random() * 1000d) / 1000d;
        double initialB = (double)Math.round(Math.random() * 1000d) / 1000d;
        double[][] experimentalArray= new double[3][3];
        experimentalArray[0][0] = initialA;
        experimentalArray[0][1] = initialB;
        experimentalArray[0][2] = rateLaw[0] * Math.pow(initialA, rateLaw[1]) * Math.pow(initialB, rateLaw[2]);

        double changedA = initialA *2;
        double changedB = initialB *2;
        experimentalArray[1][0] = changedA;
        experimentalArray[1][1] = initialB;
        experimentalArray[1][2] = rateLaw[0] * Math.pow(experimentalArray[1][0], rateLaw[1]) * Math.pow(experimentalArray[1][1], rateLaw[2]);

        experimentalArray[2][0] = initialA;
        experimentalArray[2][1] = changedB;
        experimentalArray[2][2] = rateLaw[0] * Math.pow(experimentalArray[2][0], rateLaw[1]) * Math.pow(experimentalArray[2][1], rateLaw[2]);

        String[][] printableArray = new String[3][3];
        for (int i = 0; i < 3 ;i++){
            for (int j = 0; j< 3; j++){
                printableArray[i][j] = sciNotation(experimentalArray[i][j]);
                //System.out.println(printableArray[i][j]);
            }
        }
        //System.out.println(printableArray);

        return printableArray;
    }
    public void refreshRateLaw(){
        rateLaw = generateRateLaw();
        String[][] data = generateData(rateLaw);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //System.out.println(data[i][j]);
                try {
                    experimental[i][j].setText(data[j][i]);// must flip i and j
                }catch(NullPointerException n){
                    experimental[i][j] = new Text(data[j][i]);
                }
            }
        }

        for (TextField[] r: fields){
            r[0].setText("2A,B");
            r[1].setText("C,D");
        }

        kF.clear();
        mF.clear();
        nF.clear();

        try{molecularityToggle.getSelectedToggle().setSelected(false);}catch(NullPointerException n){}

        lawCorrect.setVisible(false);
        lawIncorrect.setVisible(false);
        mechCorrect.setVisible(false);
        mechIncorrect.setVisible(false);
        molecCorrect.setVisible(false);
        molecIncorrect.setVisible(false);

        lawHasBeenWon = false;
        mechHasBeenWon = false;
        molecHasBeenWon = false;
    }

    public boolean isValidMechanism(){
        //check if agrees with rateLaw
        //System.out.println("Checking...");
        // convert into Reactions format
        try {
            ArrayList<Reaction> reactions = new ArrayList<>();
            for (TextField[] row : fields) {
                if (!row[0].getText().isEmpty()) { // not empty
                    reactions.add(new Reaction(row[0].getText() + "-" + row[1].getText()));
                }
            }

            int mechM = 0;
            int mechN = 0;

            Reaction slowStep = null;
            if (slow1.isSelected()) {
                reactions.get(0).setSlow();
                slowStep = reactions.get(0);
            } else if (slow2.isSelected()) {
                reactions.get(1).setSlow();
                slowStep = reactions.get(1);
            } else if (slow3.isSelected()) {
                reactions.get(2).setSlow();
                slowStep = reactions.get(2);
            } else {// means there is no slow step
                for (Reaction r : reactions) {
                    for (ElementCoefficientPair p : r.getReactants()) {
                        if (p.getCharacter().toLowerCase().equals("a")) {
                            mechM += p.getCoefficient();
                        } else if (p.getCharacter().toLowerCase().equals("b")) {
                            mechN += p.getCoefficient();
                        }
                    }
                }
            }

            // there is a slow step, so call checkIntermediates
            try{
                int[] mandn = checkIntermediates(reactions, slowStep);
                mechM = mandn[0];
                mechN = mandn[1];
            }catch(NullPointerException n){
                //pass - there is no slowStep
            }

            //System.out.println("[A]^" + mechM + "[B]^" + mechN);
            //System.out.println(rateLaw[1] + " " + rateLaw[2]);
            return mechM == rateLaw[1] && mechN == rateLaw[2];
        }catch(NullPointerException e){
            return false;
        }
    }
    public int[] checkIntermediates(ArrayList<Reaction> reactions, Reaction slowStep){

        int intM = 0;
        int intN = 0;

        ArrayList<ElementCoefficientPair> intermediates = new ArrayList<>();
        ArrayList<ElementCoefficientPair> slowReactants = slowStep.getReactants();

        for (ElementCoefficientPair p: slowReactants){
            if (p.getCharacter().toLowerCase().equals("a")){
                intM += p.getCoefficient();
            }else if(p.getCharacter().toLowerCase().equals("b")){
                intN += p.getCoefficient();
            }else{
                intermediates.add(p);
            }
        }
        // relies on previous step
        for (int x = 0; x < intermediates.size(); x++){
            ElementCoefficientPair i = intermediates.get(x);
            for (Reaction r: reactions){
                for (ElementCoefficientPair e: r.getProducts()){
                    if (e.getCharacter().toLowerCase().equals(i.getCharacter().toLowerCase())){
                        // found reaction it depends on
                        r.multiply(i.getCoefficient()/e.getCoefficient());
                        for (ElementCoefficientPair p: r.getReactants()){
                            if (p.getCharacter().toLowerCase().equals("a")){
                                intM += p.getCoefficient();
                            }else if(p.getCharacter().toLowerCase().equals("b")){
                                intN += p.getCoefficient();
                            }else{
                                intermediates.add(p);
                                /// SHOULDN'T NEED THIS I THINK?
                            }
                        }
                    }
                }
            }
        }
        return new int[]{intM, intN};
    }
    public void handle(ActionEvent e){
        if (e.getSource() == checkMech){
            if (isValidMechanism()){
                if (!mechHasBeenWon){
                    mechWins ++;
                }
                //System.out.println("Yay you got the mechanism right! You've got skills!");
                mechCorrect.setVisible(true);
                mechIncorrect.setVisible(false);
                mechHasBeenWon = true;
            }else{
                if (!mechHasBeenWon){
                    mechLosses++;
                }
                //System.out.println("Aw man :(");
                mechIncorrect.setVisible(true);
                mechCorrect.setVisible(false);
            }
        }else if(e.getSource() == checkLaw){
            //System.out.println(sciNotation(rateLaw[0]) + " " + rateLaw[1] + " " + rateLaw[2]);
            try {
                if ((Double.parseDouble(mF.getText()) == rateLaw[1]) && (Double.parseDouble(nF.getText()) == rateLaw[2]) && (Math.abs(rateLaw[0] - Double.parseDouble(kF.getText())) < (rateLaw[0] * .05))) {
                    if(!lawHasBeenWon){
                        lawWins++;
                    }
                    //System.out.println("Yay you got it right!");
                    lawCorrect.setVisible(true);
                    lawIncorrect.setVisible(false);
                    lawHasBeenWon = true;
                } else {
                    if(!lawHasBeenWon){
                        lawLosses++;
                    }
                    //System.out.println(":(");
                    lawIncorrect.setVisible(true);
                    lawCorrect.setVisible(false);
                }
            }catch(NumberFormatException exception){
                // tell that it's incorrect
                lawIncorrect.setVisible(true);
                lawCorrect.setVisible(false);
            }

        }else if(e.getSource() == refresh) {
            //losses++;
            refreshRateLaw();

        }else if(e.getSource() == clearMech){
            for (TextField[] row: fields){
                for (TextField t: row){
                    t.clear();
                }
            }
        }else if(e.getSource() == checkMolecularity){
            int molecularity = (int)(rateLaw[1] + rateLaw[2]);
            int molecularityAnswer = 0;
            if(molecularityToggle.getSelectedToggle() == uni){
                molecularityAnswer = 1;
            }else if(molecularityToggle.getSelectedToggle() == bi){
                molecularityAnswer = 2;
            }else if(molecularityToggle.getSelectedToggle() == tri){
                molecularityAnswer = 3;
            }
            if (molecularity == molecularityAnswer){
                // correct
                if (!molecHasBeenWon){
                    molecWins ++;
                }
                molecCorrect.setVisible(true);
                molecIncorrect.setVisible(false);
                molecHasBeenWon = true;
            }else{
                // incorrect
                if (!molecHasBeenWon){
                    molecLosses ++;
                }
                molecIncorrect.setVisible(true);
                molecCorrect.setVisible(false);
            }
        }else if(e.getSource() == statsMenuItem){
            // open statistics window
            Stage statsWindow = new Stage();
            statsWindow.setTitle("Game Statistics");
            GridPane statsPane = new GridPane();
            statsPane.setHgap(20);

            // add Labels or Text to show statistics
            Text mechStat = new Text("Mechanism game");
            Text lawStat = new Text("Rate law game");
            Text molecStat = new Text("Molecularity game");
            Text totalStat = new Text("Overall");

            //System.out.println(mechWins + mechLosses + lawWins + lawLosses);
            Text mechWinsText = new Text("Wins: "+ mechWins + " (" + String.format("%.2f",(100 * (double)mechWins/(double)(mechWins + mechLosses))) + "%)");
            Text mechLossesText = new Text("Losses: "+ mechLosses + " (" + String.format("%.2f",(100 * (double)mechLosses/(double)(mechWins + mechLosses))) + "%)");

            Text lawWinsText = new Text("Wins: "+ lawWins + " (" + String.format("%.2f",(100 * (double)lawWins/(double)(lawWins + lawLosses))) + "%)");
            Text lawLossesText = new Text("Losses: "+ lawLosses + " (" + String.format("%.2f",(100 * (double)lawLosses/(double)(lawWins + lawLosses))) + "%)");

            Text molecWinsText = new Text("Wins: "+ molecWins + " (" + String.format("%.2f",(100 * (double)molecWins/(double)(molecWins + molecLosses))) + "%)");
            Text molecLossesText = new Text("Losses: "+ molecLosses + " (" + String.format("%.2f",(100 * (double)molecLosses/(double)(molecWins + molecLosses))) + "%)");

            Text totalWinsText = new Text("Wins: "+ (mechWins + lawWins + molecWins) + " (" + String.format("%.2f",(100 * (double)(mechWins+lawWins+molecWins)/(double)(mechWins + mechLosses + lawWins + lawLosses + molecWins + molecLosses))) + "%)");
            Text totalLossesText = new Text("Losses: "+ (mechLosses+lawLosses + molecLosses) + " (" + String.format("%.2f",(100 * (double)(mechLosses+lawLosses+molecLosses)/(double)(mechWins + mechLosses + lawWins + lawLosses + molecWins + molecLosses))) + "%)");

            statsPane.setHgap(10);
            statsPane.setVgap(10);

            statsPane.add(lawStat, 0, 0);
            statsPane.add(lawWinsText, 0, 1);
            statsPane.add(lawLossesText, 0, 2);

            statsPane.add(mechStat, 1, 0);
            statsPane.add(mechWinsText, 1, 1);
            statsPane.add(mechLossesText, 1, 2);

            statsPane.add(molecStat, 2, 0);
            statsPane.add(molecWinsText, 2, 1);
            statsPane.add(molecLossesText, 2, 2);

            statsPane.add(totalStat, 1, 4);
            statsPane.add(totalWinsText, 1, 5);
            statsPane.add(totalLossesText, 1, 6);

            statsWindow.setScene(new Scene(statsPane, 350, 275, Color.GHOSTWHITE));
            statsWindow.show();
        }else if(e.getSource() == instructionsMenuItem){
            // open instructions window, has "OK" button

            Stage instWindow = new Stage();
            instWindow.setTitle("Instructions");

            VBox instPane = new VBox();

            Button instructionsOk = new Button("OK");
            instructionsOk.setOnAction(we -> {
                instWindow.close();
            });

            TextArea lawHelp = new TextArea("Using the experimental rate law data, determine the reaction orders with respect to [A] and [B] based on the formula rate = k * [A]^m * [B]^n. " +
                    "If it is first order with respect to [A], then as [A] doubles, rate will as well. If it is second order, as [A] doubles, rate quadruples. " +
                    "If it is instead zeroth order, rate will not change with [A]. " +
                    "Once you have determined m and n, you can then use data values to calculate the rate constant k. ");
            TextArea mechHelp = new TextArea("To write a mechanism, it can really be anything that you want- so be creative! " +
                    "You just need to make sure that your rate-determining step (the slow step) agrees with the rate law. \n" +
                    "\n" +
                    "For example, if your reaction was first order with respect to both [A] and [B], your reaction could be as simple as A + B -> C " +
                    "or more complicated like A -> 2X (fast) ; 2X + B -> C (slow). " +
                    "In this case, you must substitute A for 2X because the slow step relies on the progression of the first step. \n" +
                    "\n" +
                    "You can use intermediates (such as X in the previous example), and the products can be anything. " +
                    "Use this game to explore different mechanisms :)");
            TextArea molecHelp = new TextArea("Molecularity is the number of molecules that participate in the rate-determining step. " +
                    "If you know the orders with respect to [A] and [B], it is simple to find the molecularity (m+n). \n" +
                    "1 - unimolecular; 2 - bimolecular; 3 - trimolecular.");

            lawHelp.setEditable(false);
            mechHelp.setEditable(false);
            molecHelp.setEditable(false);
            lawHelp.setWrapText(true);
            mechHelp.setWrapText(true);
            molecHelp.setWrapText(true);

            lawHelp.setPrefHeight(140);
            mechHelp.setPrefHeight(240);
            molecHelp.setPrefHeight(80);

            instPane.setAlignment(Pos.TOP_CENTER);
            instPane.getChildren().addAll(lawHelp, mechHelp, molecHelp, instructionsOk);
            instWindow.setScene(new Scene(instPane, 400, 480, Color.GHOSTWHITE));
            instWindow.show();
        }else if(e.getSource() == resetMenuItem){
            // reset everything
            lawWins = 0;
            lawLosses = 0;
            mechWins = 0;
            mechLosses = 0;
            molecWins = 0;
            molecLosses = 0;
            lawHasBeenWon = false;
            mechHasBeenWon = false;
            molecHasBeenWon = false;
            refreshRateLaw();
        }
    }
    public void saveToFile(){
        // TODO save stats to file
        File myFile = new File("KineticsGameStats.txt");

        String s = lawWins + "/" + lawLosses +
                "\n" + mechWins + "/" + mechLosses +
                "\n" + molecWins + "/" + molecLosses;

        try{
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
            myOutWriter.append(s);
            myOutWriter.close();
            fOut.close();
        }catch(IOException e){
            //System.out.println("Unable to save");
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void retrieveFromFile(){
        try{
            File myFile = new File("KineticsGameStats.txt");
            if (myFile.exists()){
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                // row 1
                String buffer = myReader.readLine();
                String[] s = buffer.split("/");
                lawWins = Integer.parseInt(s[0]);
                lawLosses = Integer.parseInt(s[1]);

                buffer = myReader.readLine();
                s = buffer.split("/");
                mechWins = Integer.parseInt(s[0]);
                mechLosses = Integer.parseInt(s[1]);

                buffer = myReader.readLine();
                s = buffer.split("/");
                molecWins = Integer.parseInt(s[0]);
                molecLosses = Integer.parseInt(s[1]);

                myReader.close();
            }
        }catch(IOException i){}

    }
    public static void main(String[] args) {
        launch(args);
    }
}
