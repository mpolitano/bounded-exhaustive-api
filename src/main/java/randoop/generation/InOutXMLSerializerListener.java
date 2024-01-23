package randoop.generation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import randoop.InOutCollectorVisitor;
import randoop.sequence.ExecutableSequence;

public class InOutXMLSerializerListener implements IEventListener {

	private String serializeClass;
	private String serialFolder;
	private XStream xstream;
	private List<ObjectOutputStream> inOoss;
	private List<ObjectOutputStream> outOoss;
	private InOutCollectorVisitor inOutCollector;
	private int inObjs = -1;
	private int outObjs = -1;
	private String serializeOp;
	private boolean first = true;

	public InOutXMLSerializerListener(String serializeClass, String serialFolder, InOutCollectorVisitor inOutCollector) {
		this.serializeClass = serializeClass;
		this.serialFolder = serialFolder;
		this.inOutCollector = inOutCollector;
	}
	
	@Override
	public void explorationStart() { 
		xstream = new XStream();
	}

	@Override
	public void explorationEnd() {
		closeStream(inOoss);
		closeStream(outOoss);
	}

	@Override
	public void generationStepPre() { 
		inOoss = new ArrayList<>();
		outOoss = new ArrayList<>();
	}

	@Override
	public void generationStepPost(ExecutableSequence s) {
		if (s.getLastStmtActiveObjects(serializeClass).isEmpty())
			return;
		
		String lastOp = s.sequence.getStatement(s.sequence.size() - 1).getOperation().toParsableString();
		List<Object> inputs = inOutCollector.getInputs();
		List<Object> outputs = inOutCollector.getOutputs();
		if (first) {
			initialize(lastOp, inputs, outputs);
			first = false;
		}
		else {
			// Consistency checks
			if (!serializeOp.equals(lastOp))
				throw new Error("Serializing operation: " + serializeOp + " but current sequence ends with operation: " + lastOp + ".\nSequence: " + s.toCodeString());
			if (inObjs != inputs.size())
				throw new Error("Serializing " + inObjs + " inputs, but current has " + inputs.size() + " inputs. \nSequence: " + s.toCodeString());
			if (outObjs != outputs.size())
				throw new Error("Serializing " + outObjs + " outputs, but current has " + outputs.size() + " outputs. \nSequence: " + s.toCodeString());
		}
		writeObjects(inputs, inOoss);
		writeObjects(outputs, outOoss);
	}

	@Override
	public void progressThreadUpdate() { }

	@Override
	public boolean stopGeneration() {
		return false;
	}
	
	private void initialize(String lastOp, List<Object> inputs, List<Object> outputs) {
		serializeOp = lastOp;
		inObjs = inputs.size();
		outObjs = outputs.size();
		createStream(inObjs, inOoss, "in");
		createStream(outObjs, outOoss, "out");
	}
	
	private void createStream(int n, List<ObjectOutputStream> loos, String inOut) {
		for (int k = 0; k < n; k++) {
			String currFile = serialFolder + "/" + inOut + String.valueOf(k) + ".xml";
			try {
				loos.add(xstream.createObjectOutputStream(
						   new FileOutputStream(currFile)));
			} catch (IOException e) {
				throw new Error("Cannot create serial file: " + currFile);
			}
		}
	}
	
	private void writeObjects(List<Object> objs, List<ObjectOutputStream> loos) {
		for (int k = 0; k < objs.size(); k++) {
			try {
				loos.get(k).writeObject(objs.get(k));	
			} catch (IOException e) {
				throw new Error("Cannot serialize object: " + objs.get(k).toString());
			}
		}
	}
	
	private void closeStream(List<ObjectOutputStream> loos) {
		for (ObjectOutputStream oos: loos) {
			try {
				oos.close();
			} catch (IOException e) {
				throw new Error("Cannot close files in folder: " + serialFolder);
			}
		}
	}
	

}
