package randoop.generation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.thoughtworks.xstream.XStream;
import randoop.sequence.ExecutableSequence;

public class XMLSerializerListener implements IEventListener {

	private String serialFile;
	private String serializeClass;
	private XStream xstream;
	private ObjectOutputStream oos;

	public XMLSerializerListener(String serializeClass, String serialFile) {
		this.serialFile = serialFile;
		this.serializeClass = serializeClass;
	}
	
	@Override
	public void explorationStart() { 
		xstream = new XStream();
		try {
			oos = xstream.createObjectOutputStream(
					   new FileOutputStream(serialFile));
		} catch (IOException e) {
			throw new Error("Cannot create serial file: " + serialFile);
		}
	}

	@Override
	public void explorationEnd() {
		try {
			oos.close();
		} catch (IOException e) {
			throw new Error("Cannot close serial file: " + serialFile);
		}
	}

	@Override
	public void generationStepPre() { }

	@Override
	public void generationStepPost(ExecutableSequence s) {
		for (Object o: s.getLastStmtActiveObjects(serializeClass))
			try {
				oos.writeObject(o);
			} catch (IOException e) {
				throw new Error("Cannot serialize object: " + o.toString());
			}
	}

	@Override
	public void progressThreadUpdate() { }

	@Override
	public boolean stopGeneration() {
		return false;
	}

}
