package randoop.fieldextensions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import randoop.operation.TypedOperation;

public class ComputeBuildersManager implements IOperationManager {

	private List<TypedOperation> operations;
	private int buildersSeqLength;
	private List<TypedOperation> builders = new ArrayList<>();
	private Map<String, Set<Integer>> buildersIndexes = new HashMap<>();
	private Set<String> buildersNames = new HashSet<>();

	public ComputeBuildersManager(List<TypedOperation> operations, int buildersSeqLength) {
		this.operations = operations;
		this.buildersSeqLength = buildersSeqLength;
	}

	@Override
	public void addBuilder(TypedOperation operation, int seqLength, Set<Integer> indexes) {
		if (seqLength <= buildersSeqLength) {
			String opName = operation.toString();
			if (!buildersNames.contains(opName)) {
				builders.add(operation);
				buildersNames.add(opName);
				buildersIndexes.put(opName, indexes);
			}
			else {
				buildersIndexes.get(opName).addAll(indexes);
			}
		}
	}
	
	@Override
	public Set<Integer> getIndexes(TypedOperation builder) {
		return buildersIndexes.get(builder.toString());
	}

	boolean first = true;
	
	@Override
	public List<TypedOperation> getBuilders(int seqLength) {
		if (seqLength < buildersSeqLength)
			return operations;
		else {
			if (first) {
				first = false;
				System.out.println("\nDEBUG: Using only builders now...");
			}
			return builders; 
		}
	}

	@Override
	public void writeBuilders(String filename) {
		try {
			FileWriter writer = new FileWriter(filename);
			for (String builder: buildersNames) {
				writer.write(builder + " -- indexes=" + buildersIndexes.get(builder) + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public boolean isBuilder(TypedOperation operation) {
		return buildersNames.contains(operation.toString());
	}



}
