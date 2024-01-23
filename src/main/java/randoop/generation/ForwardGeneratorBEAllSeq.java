package randoop.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import randoop.BugInRandoopException;
import randoop.DummyVisitor;
import randoop.ExecutionVisitor;
import randoop.Globals;
import randoop.NormalExecution;
import randoop.SubTypeSet;
import randoop.fieldextensions.DummyOperationManager;
import randoop.fieldextensions.GlobalExtensionsRedundancy;
import randoop.fieldextensions.ComputeBuildersManager;
import randoop.fieldextensions.CanonicalStringsRedundancy;
import randoop.fieldextensions.IOperationManager;
import randoop.fieldextensions.IRedundancyStrategy;
import randoop.fieldextensions.OriginalRandoopManager;
import randoop.main.GenInputsAbstract;
import randoop.main.GenInputsAbstract.Filtering;
import randoop.operation.NonreceiverTerm;
import randoop.operation.Operation;
import randoop.operation.TypedOperation;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.sequence.Value;
import randoop.sequence.Variable;
import randoop.test.DummyCheckGenerator;
import randoop.types.JavaTypes;
import randoop.types.InstantiatedType;
import randoop.types.JDKTypes;
import randoop.types.Type;
import randoop.types.TypeTuple;
import randoop.util.ArrayListSimpleList;
import randoop.util.ListOfLists;
import randoop.util.Log;
import randoop.util.MultiMap;
import randoop.util.OneMoreElementList;
import randoop.util.Randomness;
import randoop.util.SimpleList;
import utils.Tuple;

/**
 * Randoop's forward, component-based generator.
 */
public class ForwardGeneratorBEAllSeq extends ForwardGeneratorBE {


  public ForwardGeneratorBEAllSeq(
      List<TypedOperation> operations,
      Set<TypedOperation> observers,
      long timeMillis,
      int maxGenSequences,
      int maxOutSequences,
      int maxSeqLength, 
      ComponentManager componentManager,
      RandoopListenerManager genManager,
      RandoopListenerManager assertManager) {
    this(
        operations,
        observers,
        timeMillis,
        maxGenSequences,
        maxOutSequences,
        componentManager,
        null,
        genManager,
        assertManager);
    this.maxSeqLength = maxSeqLength;
    this.listenerMgr = genManager;
    this.assertMgr = assertManager;
  }

  public ForwardGeneratorBEAllSeq(
      List<TypedOperation> operations,
      Set<TypedOperation> observers,
      long timeMillis,
      int maxGenSequences,
      int maxOutSequences,
      ComponentManager componentManager,
      IStopper stopper,
      RandoopListenerManager listenerManager,
      RandoopListenerManager assertManager) {
    super(
        operations,
        observers, timeMillis,
        maxGenSequences,
        maxOutSequences,
        componentManager,
        stopper,
        listenerManager, assertManager);
  }

  
  private static List<TypedOperation> getMatchingOperations(Pattern assertMethodsExpr, List<TypedOperation> ops) {
		List<TypedOperation> result = new ArrayList<>();
		  for (TypedOperation op: ops) {
			  if (assertMethodsExpr.matcher(op.toParsableString()).find())
				  result.add(op);
		  }
		return result;
	}

  TypedOperation toStringOp = null; 

  public void gen() {
	  // Notify listeners that exploration is starting.
	  if (listenerMgr != null) {
		  listenerMgr.explorationStart();
	  }
	  
	  List<TypedOperation> allOps = operations;
	  
	  if (GenInputsAbstract.tostring_oracle) {
		  String toStringName = ".toString";
		  String className = GenInputsAbstract.testclass.get(0);
		  String regex = className+"(<[a-zA-z.,]*>)?"+toStringName+".*";
		  for (TypedOperation op: allOps) {
			  if (op.toString().matches(regex) && op.getInputTypes().size() == 1) {
				  assert toStringOp == null: "Duplicated toString operation?";
				  toStringOp = op;
				  System.out.println("USING ORACLE: " + op.toParsableString());
			  }
		  }
		  assert toStringOp != null: "Could not find toString operation";
	  }
	  
	  if (GenInputsAbstract.builder_methods != null) {
		  operations = getMatchingOperations(GenInputsAbstract.builder_methods, operations);
		  System.out.println("GENERATION PHASE. USING BUILDERS:");
		  for (TypedOperation op: operations) {
			  System.out.println(op.toParsableString());
		  }
		  
		  buildersManager = new DummyOperationManager(operations);
	  }

	  ComponentManager prevMan = new ComponentManager();
	  // componentManager has only primitive sequences, we copy them to the current manager
	  prevMan.copyAllSequences(componentManager);
	  
	  ComponentManager allSeqMan = null;
	  if (GenInputsAbstract.assert_methods != null) {
		  allSeqMan = new ComponentManager(); 
		  allSeqMan.copyAllSequences(componentManager);
	  }

	  if (GenInputsAbstract.instance_object_integer_gen_phase)
		  GenInputsAbstract.instance_object_integer = true;
	  
	  int itNum = 1;
	  for (; itNum <= maxSeqLength; itNum++) {
		  ComponentManager currMan = new ComponentManager(); 
		  currMan.copyAllSequences(componentManager);

		  num_steps++;

		  if (Log.isLoggingOn()) {
			  Log.logLine("-------------------------------------------");
		  }
		  
		  boolean newObjects = BEIteration(prevMan, currMan, allSeqMan, redundancyStrat, buildersManager, executionVisitor, operations, itNum, true);
		  if (GenInputsAbstract.strict_object_scope && !newObjects) {
			  itNum++;
			  break;
		  }

		  operations = buildersManager.getBuilders(itNum);
		  if (GenInputsAbstract.all_sequences)
			  // Previous component manager is 
			  prevMan.copyAllSequences(currMan);
		  else
			  prevMan = currMan;
	  } // End of all iterations
	  
	  if (GenInputsAbstract.output_computed_extensions != null)
		  redundancyStrat.writeResults(GenInputsAbstract.output_computed_extensions, GenInputsAbstract.output_full_extensions);
	  if (GenInputsAbstract.serialize_objects != null)
		  redundancyStrat.closeSerializer();
	  if (GenInputsAbstract.output_computed_builders != null)
		  buildersManager.writeBuilders(GenInputsAbstract.output_computed_builders);
	  
	  // Notify listeners that exploration is ending.
	  if (listenerMgr != null) {
		  listenerMgr.explorationEnd();
	  }
	  
	  System.out.println("\nNumber of sequences explored: " + exploredSeqs);
	  System.out.println("\nNumber of builder sequences: " + allBuildersInScope);
	  if (!GenInputsAbstract.strict_object_scope)
		  System.out.println("\nTotal builder sequences (incluiding out of scope): " + allBuilders);
	  
	  if (GenInputsAbstract.assert_methods != null) {
		  
		  if (GenInputsAbstract.instance_object_integer_gen_phase)
			  GenInputsAbstract.instance_object_integer = false;
		  
		  operations = allOps;
		  List<TypedOperation> assertMethods = getMatchingOperations(GenInputsAbstract.assert_methods, operations);
		  
		  System.out.println("ASSERT PHASE. USING METHODS:");
		  for (TypedOperation op: assertMethods) {
			  System.out.println(op.toParsableString());
		  }

		  // New listener manager
		  listenerMgr = assertMgr;

		  // Notify listeners that exploration is starting.
		  if (listenerMgr != null) {
			  listenerMgr.explorationStart();
		  }
		  
		  if (GenInputsAbstract.assert_single_object_hack)
			  ((CanonicalStringsRedundancy)assertRedundancyStrat).saveAllObjectsHack = true;

		  // Make an additional BE iteration for a given list of methods
		  ComponentManager currMan = new ComponentManager(); 
		  currMan.copyAllSequences(componentManager);
		  
		  // Clear all sequences, otherwise some sequences for the last method won't be generated because they might
		  // have been generated during the first phase
		  allSequences.clear();
		  // Get operations from new user parameter; for specs the single method we want to compute its post 
		  BEIteration(allSeqMan, currMan, null, assertRedundancyStrat, new DummyOperationManager(operations), assertionPhaseVisitor, assertMethods, itNum, false);

		  // Notify listeners that exploration is ending.
		  if (listenerMgr != null) {
			  listenerMgr.explorationEnd();
		  }
		  System.out.println("\nTotal number of sequences explored: " + exploredSeqs);

	  }
	  
  }

  private Sequence extendWithToString(Sequence sequence, Type receiverType, Variable variable) {
	  if (variable == null) {
		  throw new BugInRandoopException("type: " + receiverType + ", sequence: " + sequence);
	  }
	  List<Sequence> seqs = new ArrayList<>();
	  int totStatements = 0;
	  List<Integer> variables = new ArrayList<>();
	  variables.add(totStatements + variable.index);
	  seqs.add(sequence);
	  totStatements += sequence.size();
	  
	  InputsAndSuccessFlag sequences = new InputsAndSuccessFlag(true, seqs, variables);

	  if (!sequences.success) {
		  if (Log.isLoggingOn()) Log.logLine("Failed to find inputs for statement.");
		  throw new Error("Failed to find inputs for toString.");
	  }

	  Sequence concatSeq = Sequence.concatenate(sequences.sequences);

	  // Figure out input variables.
	  List<Variable> inputs = new ArrayList<>();
	  for (Integer oneinput : sequences.indices) {
		  Variable v = concatSeq.getVariable(oneinput);
		  inputs.add(v);
	  }

	  Sequence toStringSequence = concatSeq.extend(toStringOp, inputs);
	  return toStringSequence;
  }

  int allBuildersInScope, exploredSeqs, allBuilders;
  private boolean BEIteration(ComponentManager prevMan, 
		ComponentManager currMan, 
		ComponentManager allSeqMan, 
		IRedundancyStrategy redundancyStrat, 
		IOperationManager buildersManager, 
		ExecutionVisitor executionVisitor, 
		List<TypedOperation> op, 
		int seqLength,
		boolean genPhase)
		throws Error {
	  int exceptionSeqs = 0;
	  int builders = 0;
	  int execSeqs = 0;
	  int genSeqs = 0;
	  boolean newObjects = false;
	  for (int opIndex = 0; opIndex < op.size(); opIndex++) {

		  if (genPhase && stop()) {
			  System.out.println("DEBUG: Stopping criteria reached");
			  return false;
		  }

		  if (genPhase && genSeqs > GenInputsAbstract.max_BE_inputs) {
			  System.out.println("DEBUG: Max input sequences reached");
			  return false;
		  }

		  // Notify listeners we are about to perform a generation step.
		  if (listenerMgr != null) {
			  listenerMgr.generationStepPre();
		  }

		  TypedOperation operation = op.get(opIndex);
		  if (Log.isLoggingOn()) {
			  Log.logLine("Selected operation: " + operation.toString());
		  }
		  if (!genPhase) 
			  System.out.println("\n> Current operation: " + operation.toParsableString());
		  
		  /*
		  if (operation.toString().equals("java.lang.Object.<init> : () -> java.lang.Object"))
			  // TODO: Think about how to deal with the operation that builds a single Object
			  continue;
			  */

		  // jhp: add flags here
		  //InputsAndSuccessFlag sequences = selectInputs(operation);
		  TypeTuple inputTypes = operation.getInputTypes();
		  CartesianProduct<Sequence> inputsCP = new CartesianProduct<>(inputTypes.size());
		  for (int i = 0; i < inputTypes.size(); i++) {
		      boolean isReceiver = (i == 0 && (operation.isMessage()) && (!operation.isStatic()));
			  
			  SimpleList<Sequence> l = prevMan.getSequencesForType(operation, i);
			  
			  if (!GenInputsAbstract.forbid_null) {
				  if (!isReceiver && !inputTypes.get(i).isPrimitive()) {
					  TypedOperation st = TypedOperation.createNullOrZeroInitializationForType(inputTypes.get(i));
					  Sequence nullSeq = new Sequence().extend(st, new ArrayList<Variable>());
					  OneMoreElementList<Sequence> res = new OneMoreElementList<>(l, nullSeq);
					  l = res;
				  }
			  }
			  
			  inputsCP.setIthComponent(i, l);
		  }
		  
		  // For each sequence in the cartesian product of feasible inputs
		  while (inputsCP.hasNext()) {
			  List<Sequence> currParams = inputsCP.next();

			  
			  CartesianProduct<Variable> varsCP = new CartesianProduct<>(inputTypes.size());
			  for (int i = 0; i < inputTypes.size(); i++) {
				  Type inputType = inputTypes.get(i);

				  Sequence chosenSeq = currParams.get(i); 
				  
				  // TODO: We are not generating all feasible sequences here, just choosing a variable randomly
				  varsCP.setIthComponent(i, new ArrayListSimpleList<Variable>(chosenSeq.variablesForTypeLastStatement(inputType)));
			  }
			  
			  while (varsCP.hasNext()) {
				  List<Variable> currVars = varsCP.next();

				  List<Sequence> seqs = new ArrayList<>();
				  int totStatements = 0;
				  List<Integer> variables = new ArrayList<>();

				  for (int i = 0; i < inputTypes.size(); i++) {
					  Type inputType = inputTypes.get(i);

					  Sequence chosenSeq = currParams.get(i); 


					  // TODO: We are not generating all feasible sequences here, just choosing a variable randomly
					  //Variable randomVariable = chosenSeq.variableForTypeLastStatement(inputType);

					  // We are now using all output variables of each input sequence
					  Variable randomVariable = currVars.get(i);

					  if (randomVariable == null) {
						  System.out.println(operation.toParsableString());
						  System.out.println(chosenSeq.toParsableString());
						  System.out.println(currParams.toString());
						  System.out.println(chosenSeq.getBuilderIndexes().toString());


						  throw new BugInRandoopException("type: " + inputType + ", sequence: " + chosenSeq);

					  }
					  if (i == 0
							  && operation.isMessage()
							  && !(operation.isStatic())
							  && (chosenSeq.getCreatingStatement(randomVariable).isPrimitiveInitialization()
									  || randomVariable.getType().isPrimitive())) {

						  throw new Error("we were unlucky and selected a null or primitive value as the receiver for a method call");
						  // return new InputsAndSuccessFlag(false, null, null);
					  }

					  variables.add(totStatements + randomVariable.index);
					  seqs.add(chosenSeq);
					  totStatements += chosenSeq.size();
				  }

				  InputsAndSuccessFlag sequences = new InputsAndSuccessFlag(true, seqs, variables);

				  if (!sequences.success) {
					  if (Log.isLoggingOn()) Log.logLine("Failed to find inputs for statement.");
					  throw new Error("Failed to find inputs for statement.");
				  }

				  Sequence concatSeq = Sequence.concatenate(sequences.sequences);

				  // Figure out input variables.
				  List<Variable> inputs = new ArrayList<>();
				  for (Integer oneinput : sequences.indices) {
					  Variable v = concatSeq.getVariable(oneinput);
					  inputs.add(v);
				  }

				  Sequence newSequence = concatSeq.extend(operation, inputs);
				  genSeqs++;
				  exploredSeqs++;
				  
				  if (genPhase && stop()) {
					  System.out.println("DEBUG: Stopping criteria reached");
					  return false;
				  }

				  if (genPhase && genSeqs > GenInputsAbstract.max_BE_inputs) {
					  System.out.println("DEBUG: Max sequences reached");
					  return false;
				  }
				  
				  if (!genPhase && genSeqs > GenInputsAbstract.max_BE_second_phase_seqs) {
					  System.out.println("DEBUG: Max sequences reached");
					  return false;
				  }

				  if (!genPhase && builders > GenInputsAbstract.max_BE_second_phase_outputs) {
					  System.out.println("DEBUG: Max output sequences reached");
					  return false;
				  }

				  num_sequences_generated++;
				  // Avoid repetition of, for example, single constructors.
				  // TODO: Check if this does not break anything.

				  /*
			  String seqStr = newSequence.toCodeString();
			  if (this.strSeqs.contains(seqStr))
				  continue;

			  this.strSeqs.add(seqStr);
				   */
				  if (newSequence.size() > GenInputsAbstract.BEmaxsize)
					  continue;

				  if (GenInputsAbstract.enable_syntactic_redundancy) {
					  if (this.allSequences.contains(newSequence)) 
						  continue;

					  // To prune sequences generated twice 
					  this.allSequences.add(newSequence);
				  }
				  

				  // If parameterless statement, subsequence inputs
				  // will all be redundant, so just remove it from list of statements.
				  // XXX does this make sense? especially in presence of side-effects
				  /*
			  if (operation.getInputTypes().isEmpty()) {
				operations.remove(operation);
			  }
				   */

				  /*
			  randoopConsistencyTests(newSequence);
			  randoopConsistencyTest2(newSequence);
				   */

				  if (Log.isLoggingOn()) {
					  Log.logLine(
							  String.format("Successfully created new unique sequence:%n%s%n", newSequence.toString()));
				  }
				  

				  ExecutableSequence eSeq = new ExecutableSequence(newSequence);

				  // Execute new sequence
				  setCurrentSequence(eSeq.sequence);

				  // long endTime = System.nanoTime();
				  // long gentime = endTime - startTime;
				  //				     startTime = endTime; // reset start time.
				  long startTime = System.nanoTime(); // reset start time.

				  eSeq.execute(executionVisitor, checkGenerator);

				  long endTime = System.nanoTime();

				  eSeq.exectime = endTime - startTime;
				  startTime = endTime; // reset start time.

				  processSequence(eSeq);

				  //endTime = System.nanoTime();
				  //gentime += endTime - startTime;
				  // eSeq.gentime = gentime;
				  eSeq.gentime = 0;

				  // Now we count repeated sequences generated
				  // num_sequences_generated++;
				  
				  
				  if (!genPhase && GenInputsAbstract.tostring_oracle) {
					  if (eSeq.sequence.hasActiveFlags() && !operation.isStatic()) {
						  Type receiverType = toStringOp.getInputTypes().get(0);
						  for (Variable variable: newSequence.variablesForTypeLastStatement(receiverType)) {
							  ExecutableSequence oldSeq = eSeq;

							  newSequence = extendWithToString(newSequence, receiverType, variable);
							  eSeq = new ExecutableSequence(newSequence);
							  // Execute new sequence
							  setCurrentSequence(eSeq.sequence);
							  startTime = System.nanoTime(); // reset start time.
							  eSeq.execute(executionVisitor, checkGenerator);
							  endTime = System.nanoTime();
							  eSeq.exectime = endTime - startTime;
							  startTime = endTime; // reset start time.
							  processSequence(eSeq);

							  // Restore the last sequence with toString appended that does not fail
							  if (!eSeq.sequence.hasActiveFlags()) {
								  eSeq = oldSeq;
								  setCurrentSequence(eSeq.sequence);

								  startTime = System.nanoTime(); // reset start time.
								  eSeq.execute(executionVisitor, checkGenerator);
								  endTime = System.nanoTime();
								  eSeq.exectime = endTime - startTime;
								  startTime = endTime; // reset start time.
								  processSequence(eSeq);
								  break;
							  }
						  }
					  }
				  }

				  execSeqs++;
				  if (eSeq.sequence.hasActiveFlags()) {
					  // TODO: Decouple opManager from currMan
					  if (!redundancyStrat.checkGenNewObjects(operation, eSeq)) {
						  eSeq.clean();
						  continue;
					  }
					  Set<Integer> activeIndexes = eSeq.getActiveIndexes();
					  currMan.addGeneratedSequence(eSeq.sequence, activeIndexes);
					  if (allSeqMan != null)
						  allSeqMan.addGeneratedSequence(eSeq.sequence, activeIndexes);
					  buildersManager.addBuilder(operation, seqLength, activeIndexes);

					  builders++;
					  newObjects = true;
					  if (genPhase) {
						  if (GenInputsAbstract.strict_object_scope || !eSeq.exceedsScopes)
							  allBuildersInScope++;
						  allBuilders++;
					  }
				  }
				  else {
					  exceptionSeqs++;
					  if (GenInputsAbstract.keep_only_builder_seqs) {
						  eSeq.clean();
						  continue;
					  }
					  if (!genPhase && !redundancyStrat.checkGenNewObjects(operation, eSeq)) {
						  eSeq.clean();
						  continue;
					  }
				  }
				  
				  /*
			  for (Sequence is : sequences.sequences) {
				  subsumed_sequences.add(is);
		      }
				   */

				  if (eSeq.hasFailure()) {
					  num_failing_sequences++;
				  }

				  // Save sequence as regression test if needed
				  if (outputTest.test(eSeq)) {
					  if (!eSeq.hasInvalidBehavior()) {
						  if (eSeq.hasFailure()) {
							  outErrorSeqs.add(eSeq);
						  } else {
							  if (!genPhase || !GenInputsAbstract.discard_generation_seqs) {
								  if (GenInputsAbstract.strict_object_scope || !eSeq.exceedsScopes)
									  outRegressionSeqs.add(eSeq);
							  }
						  }
					  }
				  }

				  // Notify listeners we just completed generation step.
				  if (listenerMgr != null) {
					  listenerMgr.generationStepPost(eSeq);
				  }
				  eSeq.clean();

			  } // End loop for current operation

		  } // End loop for cartesian product of all output variables of an input sequence
	  } // End loop for cartesian products of all inputs 

	  if (!GenInputsAbstract.noprogressdisplay) 
		  System.out.println("\n>>> Iteration: " + seqLength + 
				  ", Gen: " + genSeqs + 
				  ", Exec: " + execSeqs + 
				  ", Build: " + builders +
				  ", Excep: " + exceptionSeqs);
	  return newObjects;
}
  

 }
