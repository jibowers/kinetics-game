

import java.util.ArrayList;

/**
 * Created by User on 5/17/2017.
 */
public class Reaction {
    private ArrayList<ElementCoefficientPair> reactants = new ArrayList<ElementCoefficientPair>();
    private ArrayList<ElementCoefficientPair> products = new ArrayList<ElementCoefficientPair>();
    private Boolean isSlow = false;

    public Reaction(String input){
        // 2A B||X Y||true
        System.out.println(input);
        String[] all = input.split("-");


        for (String e: all[0].split(",")) {
            System.out.println(e);
            reactants.add(new ElementCoefficientPair(e.trim()));
        }

        for (String e: all[1].split(",")){
            products.add(new ElementCoefficientPair(e.trim()));
        }
    }
    public void setSlow(){
        isSlow = true;
    }
    public boolean isSlow(){
        return isSlow;
    }
    public ArrayList<ElementCoefficientPair> getReactants(){
        return reactants;
    }
    public ArrayList<ElementCoefficientPair> getProducts(){
        return reactants;
    }
    public void multiply(double mult){
        for(ElementCoefficientPair e: reactants){
            e.multCoefficient(mult);
        }
        for(ElementCoefficientPair e: products){
            e.multCoefficient(mult);
        }
    }


}

