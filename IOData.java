import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
//class to handle the input output data
class IOData {

    private String filename;
    private String[] data;
    private String[] unencoded_output;
    String[] teams,tags;
    double[][] output_data;
    private double[][] truth_table;
    double[][] input_data;
    private int n;

    IOData(double[] inputs, double[] output, String[] tags, String filename) throws FileNotFoundException {
        this.filename=filename;
        data=new String[0];
        this.tags=tags;
        unencoded_output=new String[0];
        input_data=new double[0][tags.length];
        teams=new String[0];
        create_IO_data(inputs, output);
    }
//adds a new tag to the list of tags and edits the number of inputs accordingly
    void add_new_tag(String tag){
        add_tag(tag);
        for(int i=0;i<input_data.length;i++){
            double[]temp=input_data[i];
            input_data[i]=new double[temp.length+1];
            System.arraycopy(temp, 0, input_data[i], 0, temp.length);
            input_data[i][temp.length]=0;
        }
    }

    private void set_n(){
        for(int i=0;;i++){
            if(Math.pow(2,i)>=teams.length){
                n=i;
                return;
            }
        }
    }
//add tags as we read the dataset
    private void add_tag(String tag){
        String[] temp=tags;
        tags=new String[temp.length+1];
        if (temp.length >= 0) System.arraycopy(temp, 0, tags, 0, temp.length);
        tags[temp.length]=tag;
    }
//add new team to the list of teams
    void add_team(String team){
        if(!in_team(team)){
            String[] temp=teams;
            teams=new String[temp.length+1];
            if (temp.length >= 0) System.arraycopy(temp, 0, teams, 0, temp.length);
            teams[temp.length]=team;
        }
    }
//checking if a team is already in the array or not
    private boolean in_team(String team){
        for (String s : teams)
            if (s.equalsIgnoreCase(team))
                return true;

        return false;
    }
//class handling all the data from the dataset and encoding it as per the relevant codes
    private void create_IO_data(double[] inputs, double[] output) throws FileNotFoundException {
        int counter=0;
        if(inputs.length==0 && output.length==0){
            Scanner readData= new Scanner(new File(filename));
            readData.useDelimiter(",");
            while (readData.hasNext()){
                String s=readData.nextLine();
                data=add_data(s,data);
            }
            readData.close();
            boolean flag=true;
            for (String datum : data) {
                if(flag){
                    String[] temp=datum.split(",");
                    for(int i=0;i<temp.length-1;i++)
                        add_tag(temp[i]);
                    flag=false;
                    continue;
                }
                String[] temp=datum.split(",");
                for(int i=0;i<temp.length;i++){
                    if((i+1)%10==0){
                        add_team(temp[i]);
                        unencoded_output=add_data(temp[i],unencoded_output);
                    }
                    else{
                        input_data=add_data(temp[i],input_data,counter++);
                        if(counter>tags.length)
                            counter=1;
                    }
                }
            }
            output_data=encode_output(unencoded_output);
        }

        if(inputs.length>0){
            for (double i : inputs) {
                input_data=add_data(i==1?"Yes":"No",input_data,counter++);
                if(counter>tags.length)
                    counter=1;
            }
        }
        if(output.length>0){
            output_data=new double[0][3];
            output_data=add_data(output,output_data);
        }
    }
//called to return encoded output for a string input
    private double[][] encode_output(String[] data){
        generate_truth_table();
        double[][] output=new double[data.length][truth_table[0].length];
        for(int i=0;i<data.length;i++){
            output[i]=truth_table[search_output(data[i])];
        }
        return output;
    }

    double[] encode_output(String data){
        generate_truth_table();
        return truth_table[search_output(data)];
    }
//potentially used to decode encoded outputs for an entire array but is never called
    private String[] decode_output(double[][] data){
        generate_truth_table();
        String[] output=new String[data.length];
        for(int i=0;i<data.length;i++){
            output[i]=teams[search_output(data[i])];
        }
        return output;
    }
//used to calculate single decoded output
    String decode_output(double[] data){
        generate_truth_table();
        String output="";
        for(int i=0;i<data.length;i++){
            output=teams[search_output(data)];
        }
        return output;
    }
//search if the output exists
    private int search_output(String s){
        for(int i=0;i<teams.length;i++)
            if(teams[i].equalsIgnoreCase(s))
                return i;

        return -1;
    }

    private int search_output(double[] d){
        for(int i=0;i<truth_table.length;i++) {
            boolean flag=true;
            for (int j = 0; j < truth_table[i].length; j++)
                if (d[j] != truth_table[i][j]) {
                    flag = false;
                    break;
                }
            if(flag)
                return i;
        }
        return -1;
    }
//generate truth table to help encode the teams in binary encoding
    private void generate_truth_table(){
        set_n();
        truth_table=new double[(int)Math.pow(2,n)][n];
        int counter;
        double limit;
        int value=1;
        for(int j=0;j<truth_table[0].length;j++){
            limit=truth_table.length/Math.pow(2,j+1);
//            System.out.println("TEST LIMIT: "+limit);
            counter=0;
            for(int i=0;i<truth_table.length;i++){
                if(counter%limit==0)
                    value=value==1?0:1;
                truth_table[i][j]=value;
                counter++;
            }
        }
    }
//overloaded function to add data to multiple types of arrays
    private double[][] add_data(double[]out, double[][] arr){
        double[][] data=new double[arr.length+1][3];
        System.arraycopy(arr, 0, data, 0, arr.length);
        data[arr.length]=out;
        return data;
    }

    private String[] add_data(String s, String[] arr){
        String[] data=new String[arr.length+1];
        System.arraycopy(arr, 0, data, 0, arr.length);
        data[arr.length]=s;
        return data;
    }

    private double[][] add_data(String s, double[][] arr, int index){
        double[][] data;

        if(arr.length==0)
            data=new double[1][tags.length];
        else if(index==tags.length){
            data=new double[arr.length+1][tags.length];
            System.arraycopy(arr, 0, data, 0, arr.length);
        }
        else
            data=arr;

        int pass=0;

        switch(s){
            case "Yes": pass=1; break;
            case "No": pass=0; break;
        }
        if(index==tags.length)
            data[arr.length][0]=pass;
        else if(arr.length==0)
            data[0][index]=pass;
        else
            data[arr.length-1][index]=pass;
        return data;
    }

}
