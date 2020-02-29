import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.*;
//class to handle user requests/ ticket booking
class Intermediate {

    private BufferedReader br;
    private double[] inputs;
    private String[] choices;
    private BasicNetwork network;
    private BasicNetwork network2;
    private IOData d1;
    private IOData d2;

    Intermediate(IOData d1, IOData d2, BasicNetwork n) throws IOException, ClassNotFoundException {
        br=new BufferedReader(new InputStreamReader(System.in));
        choices=d1.tags;
        inputs=new double[choices.length];
        this.d1=d1;
        this.d2=d2;
        if(n==null)
            load_network();
        else
            network=n;
        read_inputs();
    }
//reading inputs from user to make an array of inputs used to predict/compute output
    private void read_inputs() throws IOException{

        System.out.println("Create new ticket?");
        boolean flag= br.readLine().equalsIgnoreCase("Yes");
        boolean prediction_flag=false;
        boolean days_flag=false;
        while(flag) {
        System.out.println("Please answer the following questions with a 'Yes' or a 'No'");
        prediction_flag=false;
        for(int i=0;i<choices.length && !prediction_flag;i++){
            System.out.println(choices[i]+"?");
            inputs[i]=br.readLine().equalsIgnoreCase("Yes")?1:0;
            if(i>=3 && i<choices.length-1){
                prediction_flag=predict(inputs,i);
                if(prediction_flag && d2!=null){
                    compute_days(inputs,1, i);
                    days_flag=true;
                }
            }
        }
        if(!prediction_flag){
            compute_output(inputs,1);
            if(!check_output()){
                retrain();
            }
            days_flag=false;
        }
        if(!days_flag && d2!=null){
            compute_days(inputs,1, -1);
        }
        System.out.println("Create new ticket?");
        flag= br.readLine().equalsIgnoreCase("Yes");
        }
    }
//early prediction
    private boolean predict(double[] i, int index) throws IOException {

        double[]input=search(pad(i,index), d1);
        if(input.length>0){
            compute_output(input,0);
            return check_output();
        }
        else
            return false;
    }
//padding the array to fit the size required by the network if search result for the data is empty
    private double[] pad(double[] in, int index){
        for(int i=index+1;i<in.length;i++)
            in[i]=-1;
        return in;
    }
//search for the array in the training data to find matches to help in prediction
    private double[] search(double[] input, IOData d1){
        double[][] list=d1.input_data;
        boolean flag=false;
        int j=0;
        for (double[] doubles : list) {

            for(int i=0;i<doubles.length;i++){
                if ((i < 4 && doubles[i] != input[i]) || (i>=4 && doubles[i] != input[i] && input[i] != -1)) {
                    flag = true;
                    break;
                }
                else{
                    flag=false;
                }
            }
            j++;
            if (!flag)
                return doubles;
        }
        return input;
    }

//    private void print(double[] arr){
//        for (double a: arr) {
//            System.out.print(a+"-");
//        }
//    }
//getting user input for team info
    private boolean check_output() throws IOException {
        System.out.println("Is this the correct team?");
        return br.readLine().equalsIgnoreCase("Yes");
    }
// if the user says the team is wrong, retrain the network
    private void retrain() throws IOException {
        System.out.println("Please confirm the correct team from the following list: ");
        for(int i=0;i<d1.teams.length;i++){
            System.out.println(d1.teams[i]);
        }
        String option=br.readLine();
        double[] correct_output=d1.encode_output(option);
        Basic b1=new Basic(network,new IOData(inputs,correct_output,d1.tags, "src/tickets.csv"), false);
        network=b1.train(1);
    }
//loading the network from the saved file
    private void load_network() throws IOException, ClassNotFoundException {
        network=  (BasicNetwork) EncogDirectoryPersistence.loadObject(new File ("Network.eg") ) ;

        if(d2!=null){
            network2= (BasicNetwork) EncogDirectoryPersistence.loadObject(new File ("Network1.eg") ) ;
        }
    }
// running the inputs in the network to get the output
    private void compute_output(double[] inputs,int n){

        MLData data = new BasicMLData(inputs);
        MLData output=network.compute(data);
        System.out.print("\nSending ticket to: ");

        double[] outputValue=output.getData();
        for(int i=0;i<outputValue.length;i++)
            if(outputValue[i]>0.5)
                outputValue[i]=1;
            else
                outputValue[i]=0;
//        for(double s:outputValue)
//        System.out.println(s);
        System.out.println(d1.decode_output(outputValue));
    }
// if advanced, running the input in the second network to get an estimate of days
    private void compute_days(double[] inputs,int n, int index){
        if(index!=-1)
            inputs=search(pad(inputs,index), d2);
        if(n==1 && d2!=null){
            MLData data = new BasicMLData(inputs);
            MLData output1=network2.compute(data);
            double[] outputValue2=output1.getData();
            for(int i=0;i<outputValue2.length;i++)
                if(outputValue2[i]>0.5)
                    outputValue2[i]=1;
                else
                    outputValue2[i]=0;
//            for(double s:outputValue2)
//                System.out.println(s);
            System.out.println("Approximate Time required: "+d2.decode_output(outputValue2)+" days\n");
        }
    }

}
