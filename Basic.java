import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.*;

class Basic {

    private int input_units, hidden_units, output_units;
    BasicNetwork network;
    private IOData d1;
// building the values
    Basic(BasicNetwork n1, IOData d1, boolean adv){
        input_units=d1.input_data[0].length;
        hidden_units=adv?7:4;
        output_units=d1.output_data[0].length;
        if(n1==null){
            network=new BasicNetwork();
            setup_network();
        }
        else
            network= (BasicNetwork) n1.clone();
        this.d1=d1;
    }
//setting up the network
    private void setup_network(){
        network.addLayer(new BasicLayer(null,false,input_units));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,hidden_units));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,output_units));
        network.getStructure().finalizeStructure();
        network.reset();
    }
//training the network
    BasicNetwork train(int a){
        double[][] input=d1.input_data;
        double[][] output=d1.output_data;
        int maxEpoch=20000;
        MLDataSet trainingSet = new BasicMLDataSet(input, output);

        Backpropagation train=new Backpropagation (network, trainingSet,0.2,0.2);
        int epoch = 1;
        do {
            train.iteration();
            if(a!=1)
                System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while(train.getError() > 0.005 && (epoch<=maxEpoch || a!=2));
        train.finishTraining();

        return epoch>=maxEpoch?a==2?train(a):network:network;
    }
//saving the trained network
    void save(BasicNetwork network, String filename) {
        EncogDirectoryPersistence.saveObject(new File(filename+".eg"), network);
    }

}
