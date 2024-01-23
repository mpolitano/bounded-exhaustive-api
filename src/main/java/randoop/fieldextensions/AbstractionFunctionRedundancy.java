package randoop.fieldextensions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


import randoop.ExecutionOutcome;
import randoop.NormalExecution;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedOperation;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Statement;
import representations.FieldExtensions;
import representations.FieldRepresentation;
import utils.Tuple;


public class AbstractionFunctionRedundancy extends GlobalExtensionsRedundancy {
	
	public boolean saveAllObjectsHack = false;
	
	private Map<String, Set<FieldRepresentation>> canStrs = new LinkedHashMap<>();
	
	FileWriter debugWriter;
	
	// classesUnderTest = null to consider all classes as relevant
	public AbstractionFunctionRedundancy(int maxStoppingObjs, int maxStoppingArr,
			Pattern omitfields, String debugOutput) {
		super(maxStoppingObjs, maxStoppingArr, omitfields);
		
		try {
			if (debugOutput != null)
				debugWriter = new FileWriter(debugOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	// Returns the indices where the objects that initialize new field values are, null if 
	// some object exceeds given bounds or the execution of the sequence fails 
	@Override
	protected Set<Integer> newFieldValuesInitialized(ExecutableSequence sequence) {
		Set<Integer> indices = new LinkedHashSet<>();
		int i = sequence.sequence.size() -1;

		ExecutionOutcome statementResult = sequence.getResult(i);	
		TypedOperation op = sequence.sequence.getStatement(i).getOperation();

		//String className = Utils.getOperationClass(op);
		//if (!Utils.classUnderTest(className)) return;
		
		if (statementResult instanceof NormalExecution) {
			int index = 0;
			Statement stmt = sequence.sequence.getStatement(i);
			// Sort objects by type first
			Map<String, List<Tuple<Object, Integer>>> objsByType = new LinkedHashMap<>();
			if (!stmt.getOutputType().isVoid()) {
				Object retVal = ((NormalExecution)statementResult).getRuntimeValue();
				if (retVal != null && !Utils.isPrimitive(retVal)) {
					List<Tuple<Object, Integer>> l = new LinkedList<>();
					l.add(new Tuple<>(retVal, index));
					objsByType.put(retVal.getClass().getName(), l);
				}
				index++;
			}

			Object[] objsAfterExec = sequence.getRuntimeInputs(i);
			for (int j = 0; j < objsAfterExec.length; j++) {
				Object curr = objsAfterExec[j];
				if (curr == null || Utils.isPrimitive(curr)) { index++; continue; }
				String cls = curr.getClass().getName();
				if (objsByType.get(cls) == null) {
					List<Tuple<Object, Integer>> l = new LinkedList<>();
					l.add(new Tuple<>(curr, index));;
					objsByType.put(cls, l);
				}
				else 
					objsByType.get(cls).add(new Tuple<>(curr, index));
				index++;
			}	
			
			// Test does not exceed the limits
			// objsByType: Classnames -> (Object, Position)
			// Position should be added to indices if the object at that position is new
			for (String cls: objsByType.keySet()) {
				for (Tuple<Object, Integer> t: objsByType.get(cls)) {
					FieldRepresentation f = new FieldRepresentation();
					f.setAbstractionFuction(GenInputsAbstract.abstraction);
					if (GenInputsAbstract.dont_save_fields != null)
						f.dontSaveFields(GenInputsAbstract.dont_save_fields);

					// An object exceeded the extensions limits
					// tell randoop to drop the sequence by returning null
					if (!canonicalizer.canonicalize(t.getFirst(), f))
						return null;
					if (addRepresentation(cls, f, sequence)) {
						indices.add(t.getSecond());

						if (debugWriter != null) {
							try {
								String objStr = t.getFirst().toString();
								debugWriter.write("---\n");
								debugWriter.write(sequence.toCodeString() + "\n");
								debugWriter.write("Index: " + t.getSecond() + "\n\n");
								debugWriter.write("Object toString(): \n");
								debugWriter.write(objStr + "\n\n");
								debugWriter.write("Object extensions: \n");
								String objCanStr = t.toString();
								debugWriter.write(objCanStr + "\n");
								debugWriter.write("---\n");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}	
		}
		else {
			// Abnormal execution
			throw new Error("Computing active indices for an invalid sequence");
		}

		// The indices in the sequence where new objects are stored
		return indices;
	}
	
	public boolean addRepresentation(String key, FieldRepresentation f, ExecutableSequence seq) {
		if (saveAllObjectsHack) return true;
		
		Set<FieldRepresentation> keyCanStrs = canStrs.get(key);
		if (keyCanStrs == null) {
			keyCanStrs = new LinkedHashSet<>();
			canStrs.put(key, keyCanStrs);
		}
		
		boolean result = keyCanStrs.add(f);

		if (GenInputsAbstract.save_all_constructors && seq.sequence.size() == 1 && seq.sequence.getStatement(0).isConstructorCall()) 
			return true;
		
		return result;
	}
	
	@Override
	public void writeResults(String filename, boolean fullres) {
		try {
			FileWriter writer = new FileWriter(filename);
			// writer.write(outputExt.getStatistics(fullres));
			int totalObjs = 0;
			for (String cls: canStrs.keySet()) {
				writer.write(cls + " objects: " + canStrs.get(cls).size() + "\n");
				totalObjs += canStrs.get(cls).size();
			}
			writer.write("Total objects sum: "+ totalObjs + "\n");
			writer.close();
			
			if (debugWriter != null)  
				debugWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}



}
