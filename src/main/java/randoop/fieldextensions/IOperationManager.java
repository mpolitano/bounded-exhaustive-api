package randoop.fieldextensions;

import java.util.List;
import java.util.Set;

import randoop.operation.TypedOperation;

public interface IOperationManager {

	List<TypedOperation> getBuilders(int seqLength);

	void writeBuilders(String output_computed_builders);

	boolean isBuilder(TypedOperation operation);

	void addBuilder(TypedOperation operation, int seqLength, Set<Integer> indexes);

	Set<Integer> getIndexes(TypedOperation builder);

}
