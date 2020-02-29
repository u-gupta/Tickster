import org.encog.neural.networks.BasicNetwork;
import java.io.*;
//class to handle admin requests
class Admin {

    private BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
    private IOData d1;
    BasicNetwork network;

    Admin(IOData d1) throws IOException {
        this.d1=d1;
        network=get_input();
    }

    private BasicNetwork get_input() throws IOException {
        int choice;
        Basic b1 = null;
        do {
            System.out.println("Press '1' to add a new team, '2' to add a new tag or '3' to finalize");
            choice = Integer.parseInt(br.readLine());
            switch (choice) {
                case 1: {
                    System.out.println("Please enter the name of the new team");
                    d1.add_team(br.readLine());

                }
                break;
                case 2: {
                    System.out.println("Please enter the name of the new tag");
                    d1.add_new_tag(br.readLine());

                }
                break;
                default: {
                    b1 = new Basic(null, d1, false);
                    b1.train(2);
                }
            }
        } while(choice!=3);
        return b1.network;
    }

}