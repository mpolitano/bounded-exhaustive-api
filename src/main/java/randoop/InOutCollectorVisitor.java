package randoop;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import randoop.sequence.ExecutableSequence;

public final class InOutCollectorVisitor implements ExecutionVisitor {

	private XStream xstream = new XStream();
	private List<Object> inputs;
	private List<Object> outputs;

	public List<Object> getInputs() {
		return inputs;
	}

	public List<Object> getOutputs() {
		return outputs;
	}

	@Override
	public void initialize(ExecutableSequence executableSequence) {
		inputs = null;
		outputs = null;
	}

	@Override
	public void visitBeforeStatement(ExecutableSequence sequence, int i) {
		// TODO: What happens if there are flakys?
		if (i == sequence.sequence.size() - 1) {
			inputs = new ArrayList<>();
			int last = sequence.sequence.size() - 1;
			for (Object in: sequence.getRuntimeInputs(last))
				inputs.add(cloneObject(in, xstream));
		}
	}

	@Override
	public void visitAfterStatement(ExecutableSequence sequence, int i) {
		if (sequence.isNormalExecution() && i == sequence.sequence.size() - 1) {
			outputs = sequence.getLastStmtObjectsResLast();
		}
	}

	@Override
	public void visitAfterSequence(ExecutableSequence executableSequence) {
		// do nothing
	}


	private static Object cloneObject(Object o, XStream xstream) {
		String xml = xstream.toXML(o);
		return xstream.fromXML(xml);
	}
}
