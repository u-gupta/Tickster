import org.encog.neural.networks.BasicNetwork;
import java.io.*;

public class A4main {

    private static BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String choice1 =args.length==0?"":args[0];
        //Makes it work with console or IDE with no args
        if(choice1.equalsIgnoreCase("")) {
            System.out.println("Choose your agent : Bas, Int, Adv");
            choice1=br.readLine();
        }
        IOData d1=new IOData(new double[0], new double[0],new String[0], "tickets.csv");

        switch(choice1){
            case "Bas":basic_agent(d1, "Network"); break;
            case "Int":intermediate_agent(d1,null); break;
            case "Adv": advanced_agent(d1); break;
            default: System.out.println("Wrong agent choice");
        }
    }
//basic agent being called
    private static void basic_agent(IOData d1, String filename) {
        //just trains the network and saves it
        	Basic b1=new Basic(null,d1, false);
        	b1.save(b1.train(0),filename);
    }
// intermediate agent being called
    private static void intermediate_agent(IOData d1, IOData d2) throws IOException, ClassNotFoundException {
        //calls the saved network and uses it to predict team based on user input. If prediction is wrong ,retrains the network
        new Intermediate(d1,d2,null);
    }
// advanced agent being called to either change the structure of the data, or to predict days along with team.
    private static void advanced_agent(IOData d1) throws IOException, ClassNotFoundException {
        String c="Yes";
        BasicNetwork n=null;
        IOData d2= new IOData(new double[0], new double[0], new String[0], "TicketDays.csv");
        //keeps running till the user wants to continue
        while(c.equalsIgnoreCase("Yes")){
            System.out.println("Are you an admin or a user?");
            String choice=br.readLine();
            switch (choice){
                case "user": new Intermediate(d1,d2,n); break;
                case "admin":{
                    System.out.println("Do you want to train a network or make structural changes?");
                    String ch=br.readLine();
                    if(ch.equalsIgnoreCase("train")){
                        System.out.println("Choose network to train: Network 1 or Network 2");
                        switch(br.readLine()){
                            case "Network 1": basic_agent(d1,"Network"); break;
                            case "Network 2": basic_agent(d2, "Network1"); break;
                            default: System.out.println("Wrong choice");
                        }
                    }
                    else if(ch.equalsIgnoreCase("structural changes")){
                        Admin a=new Admin(d1);
                        n=a.network;
                    }
                }
            }
            System.out.println("Want to continue?");
            c=br.readLine();
        }
    }

}
