package randoop.fieldextensions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import canonicalizer.BFHeapCanonicalizer;
import canonicalizer.CanonicalizerConfig;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedOperation;
import randoop.sequence.ExecutableSequence;


public class GlobalExtensionsRedundancy implements IRedundancyStrategy {
	
	protected BFHeapCanonicalizer canonicalizer;
	//protected ExtensionsStore currExt;
//	protected int maxObjects;
//	protected int maxArrayObjects;
	protected int maxFieldDistance;
	protected int maxBFDepth;
	
	
	// classesUnderTest = null to consider all classes as relevant
	public GlobalExtensionsRedundancy(int maxStoppingObjs, int maxStoppingArr,
			Pattern omitfields) {
		if (maxStoppingObjs <= 0 || maxStoppingArr <= 0)
			throw new Error("BoundedExtensionsComputerVisitor must be used with max_stopping_objects > 0 and max_stopping_primitives > 0");
		
//		this.maxObjects = Integer.MAX_VALUE; 
//		this.maxArrayObjects = Integer.MAX_VALUE;
		this.maxFieldDistance = Integer.MAX_VALUE;
		this.maxBFDepth = Integer.MAX_VALUE;
		
		CanonicalizerConfig cfg = null;
		if (GenInputsAbstract.canonicalizer_cfg == null) {
			cfg = new CanonicalizerConfig();
			cfg.setMaxObjects(maxStoppingObjs);
			cfg.setMaxArrayValues(maxStoppingArr);
			cfg.setIgnoreFields(omitfields);
		}
		else {
			try {
				cfg = CanonicalizerConfig.fromFile(GenInputsAbstract.canonicalizer_cfg);
			} catch (IOException e) {
				throw new Error("Canonicalizer config file: " + GenInputsAbstract.canonicalizer_cfg + " does not exist");
			}
		}
		canonicalizer = new BFHeapCanonicalizer(cfg);
		
		//currExt = new ExtensionsStore(maxStoppingPrims, true);
	}
	

	// Returns the indices where the objects that initialize new field values are, null if 
	// some object exceeds given bounds or the execution of the sequence fails 
	protected Set<Integer> newFieldValuesInitialized(ExecutableSequence sequence) {
//		Set<Integer> indices = new LinkedHashSet<>();
//		int i = sequence.sequence.size() -1;
//
//		ExecutionOutcome statementResult = sequence.getResult(i);	
//		TypedOperation op = sequence.sequence.getStatement(i).getOperation();
//
//		//String className = Utils.getOperationClass(op);
//		//if (!Utils.classUnderTest(className)) return;
//		
//		if (statementResult instanceof NormalExecution) {
//			int index = 0;
//			Statement stmt = sequence.sequence.getStatement(i);
//			// Sort objects by type first
//			Map<String, List<Tuple<Object, Integer>>> objsByType = new LinkedHashMap<>();
//			if (!stmt.getOutputType().isVoid()) {
//				Object retVal = ((NormalExecution)statementResult).getRuntimeValue();
//				if (retVal != null && !Utils.isPrimitive(retVal)) {
//					List<Tuple<Object, Integer>> l = new LinkedList<>();
//					l.add(new Tuple<>(retVal, index));
//					objsByType.put(retVal.getClass().getName(), l);
//				}
//				index++;
//			}
//
//			Object[] objsAfterExec = sequence.getRuntimeInputs(i);
//			for (int j = 0; j < objsAfterExec.length; j++) {
//				Object curr = objsAfterExec[j];
//				if (curr == null || Utils.isPrimitive(curr)) { index++; continue; }
//				String cls = curr.getClass().getName();
//				if (objsByType.get(cls) == null) {
//					List<Tuple<Object, Integer>> l = new LinkedList<>();
//					l.add(new Tuple<>(curr, index));;
//					objsByType.put(cls, l);
//				}
//				else 
//					objsByType.get(cls).add(new Tuple<>(curr, index));
//				index++;
//			}	
//			
//			for (String cls: objsByType.keySet()) {
//				FieldExtensionsCollector collector = outputExt.getOrCreateCollectorForMethodParam(cls);
////				FieldExtensionsCollector collector = currExt.getOrCreateCollectorForMethodParam(cls);
//				collector.start();
//				collector.setTestMode();
//				for (Tuple<Object, Integer> t: objsByType.get(cls)) {
//					if (!canonicalizer.canonicalize(t.getFirst(), collector))
//						return null;
//				}
//				collector.testCommitAllPairs();
//				if (collector.testExtensionsLimitExceeded())
//					return null;
//			}
//			
//			// Test does not exceed the limits
//			for (String cls: objsByType.keySet()) {
//				FieldExtensionsCollector collector = outputExt.getOrCreateCollectorForMethodParam(cls);
////				FieldExtensionsCollector collector = currExt.getOrCreateCollectorForMethodParam(cls);
//				if (collector.testExtensionsWereExtended()) {
//					collector.commitSuccessfulTestsPairs();
//					// FIXME: We currently do not keep track of the exact object that has extended the extensions
//					// but return all indices of the objects for the class for which we saw new field values
//					for (Tuple<Object, Integer> t: objsByType.get(cls)) {
//						indices.add(t.getSecond());
//					}
//				}
//			}	
//		}
//		else {
//			// Abnormal execution
//			throw new Error("Computing active indices for an invalid sequence");
//		}
//
//		return indices;
		throw new Error("Field exhaustive not implemented");
	}
	
	@Override
	public void writeResults(String filename, boolean fullExt) {
		try {
			FileWriter writer = new FileWriter(filename);
//			writer.write(outputExt.getStatistics(fullExt));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	@Override
	public boolean checkGenNewObjects(TypedOperation operation, ExecutableSequence eSeq) {
		
		Set<Integer> activeIndexes = newFieldValuesInitialized(eSeq);
		eSeq.setActiveIndexes(activeIndexes);

		boolean genNewObjs = true;
		if (activeIndexes == null || activeIndexes.size() == 0) 
			genNewObjs = false;

		eSeq.setGenNewObjects(genNewObjs);
		
		return genNewObjs;
	}


	@Override
	public void closeSerializer() {
	}


}
