

/**
 * Created by User on 5/17/2017.
 */
public class ElementCoefficientPair {
    private String e;
    private double c;

    public ElementCoefficientPair(String s){
        System.out.println(s);
        if (s.length() == 1){
            e = s.substring(0, 1);
            c = 1;
        }else{
            e = s.substring(1, 2);
            c = Integer.valueOf(s.substring(0,1));
        }
    }

    public String getCharacter(){
        return e;
    }
    public double getCoefficient(){
        return c;
    }
    public void multCoefficient(double num){
        c = c*num;
    }
}

