package randoop.generation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import randoop.operation.TypedClassOperation;
import randoop.operation.TypedOperation;
import randoop.sequence.ClassLiterals;
import randoop.sequence.PackageLiterals;
import randoop.sequence.Sequence;
import randoop.sequence.SequenceCollection;
import randoop.types.ClassOrInterfaceType;
import randoop.types.JavaTypes;
import randoop.types.PrimitiveType;
import randoop.types.Type;
import randoop.util.ListOfLists;
import randoop.util.SimpleList;

/**
 * Stores and provides means to access the component sequences generated during
 * a run of Randoop. "Component sequences" are sequences that Randoop uses to
 * create larger sequences.
 *
 * This class manages different collections of component sequences:
 *
 * <ul>
 * <li>General components that can be used as input to any method in any class.
 * <li>Class literals: components representing literal values that apply only to
 * a specific class and should not be used as inputs to other classes.
 * <li>Package literals: analogous to class literals but at the package level.
 * </ul>
 *
 * SEED SEQUENCES. Seed sequences are sequences that were not created during the
 * generation process but obtained via other means. They include (1) sequences
 * passed via the constructor, (2) class literals, and (3) package literals. The
 * only different treatment of seed sequences is during calls to the
 * clearGeneratedSequences() method, which removes only general, non-seed
 * components from the collection.
 */
public class ComponentManager {

  /**
   * The principal set of sequences used to create other, larger sequences by
   * the generator.
   */
  // Is never null. Contains both general components
  // and seed sequences.
  protected SequenceCollection gralComponents;

  public void copyAllSequences(ComponentManager other) {
	  for (Sequence s: other.gralComponents.getAllSequences())
		  addGeneratedSequence(s);
  }
  
  /**
   * The subset of the sequences that were given pre-generation to the component
   * manager (via its constructor).
   */
  // Seeds are all contained in gralComponents. This list
  // is kept to restore seeds if the user calls
  // clearGeneratedSequences().
  protected Collection<Sequence> gralSeeds;

  /**
   * A set of additional components representing literals that should only be
   * used as input to specific classes.
   */
  // May be null, which represents no class literals present.
  private ClassLiterals classLiterals = null;

  /**
   * A set of additional components representing literals that should only be
   * used as input to specific packages.
   */
  // May be null, which represents no package literals present.
  private PackageLiterals packageLiterals = null;

  /**
   * Create an empty component manager, with an empty seed sequence set.
   */
  public ComponentManager() {
    gralComponents = new SequenceCollection();
    gralSeeds = Collections.unmodifiableSet(Collections.<Sequence>emptySet());
  }

  /**
   * Create a component manager, initially populated with the given sequences,
   * which are considered seed sequences.
   *
   * @param generalSeeds
   *          seed sequences. Can be null, in which case the seed sequences set
   *          is considered empty.
   */
  public ComponentManager(Collection<Sequence> generalSeeds) {
    Set<Sequence> seedSet = new LinkedHashSet<>(generalSeeds.size());
    seedSet.addAll(generalSeeds);
    this.gralSeeds = Collections.unmodifiableSet(seedSet);
    gralComponents = new SequenceCollection(seedSet);
  }

  /**
   * Returns the number of (non-seed) sequences stored by the manager.
   *
   * @return count of generated sequences in this {@link ComponentManager}.
   */
  // FIXME subtract size of seeds!
  public int numGeneratedSequences() {
    return gralComponents.size();
  }

  /**
   * Add a sequence representing a literal value that can be used when testing
   * members of the given class.
   *
   * @param type  the class literal to add for the sequence
   * @param seq  the sequence
   */
  public void addClassLevelLiteral(ClassOrInterfaceType type, Sequence seq) {
    if (classLiterals == null) {
      classLiterals = new ClassLiterals();
    }
    classLiterals.addSequence(type, seq);
  }

  /**
   * Add a sequence representing a literal value that can be used when testing
   * classes in the given package.
   *
   * @param pkg the package to add for the sequence
   * @param seq the sequence
   */
  public void addPackageLevelLiteral(Package pkg, Sequence seq) {
    if (packageLiterals == null) {
      packageLiterals = new PackageLiterals();
    }
    packageLiterals.addSequence(pkg, seq);
  }

  /**
   * Add a component sequence.
   *
   * @param sequence the sequence
   */
  public void addGeneratedSequence(Sequence sequence) {
    gralComponents.add(sequence);
  }
  
  public void addGeneratedSequence(Sequence sequence, Set<Integer> indexes) {
	if (indexes == null)
		addGeneratedSequence(sequence);
	else 
		gralComponents.add(sequence, indexes);
  }

  /**
   * Removes any components sequences added so far, except for seed sequences,
   * which are preserved.
   */
  void clearGeneratedSequences() {
    gralComponents = new SequenceCollection(this.gralSeeds);
  }

  /*
   * @return the set of generated sequences
   */
  Set<Sequence> getAllGeneratedSequences() {
    return gralComponents.getAllSequences();
  }

  /**
   * Returns all the general component sequences that create values of the given
   * class. If exactMatch==true returns only sequences that declare values of
   * the exact class specified; if exactMatch==false returns sequences declaring
   * values of cls or any other class that can be used as a cls (i.e. a subclass
   * of cls).
   *
   * @param cls  the query type
   *@return the sequences that create values of the given type
   */
  SimpleList<Sequence> getSequencesForType(Type cls) {
    return gralComponents.getSequencesForType(cls, false);
  }

  /**
   * Returns component sequences that create values of the type required by the
   * i-th input value of the given statement. Any applicable class- or
   * package-level literals, those are added to the collection as well.
   *
   * @param operation  the statement
   * @param i  the input value index of statement
   * @return the sequences that create values of the given type
   */
  
  Map<String, SimpleList<Sequence>> cache = new HashMap<>();
  
  @SuppressWarnings("unchecked")
  SimpleList<Sequence> getSequencesForType(TypedOperation operation, int i) {

    Type neededType = operation.getInputTypes().get(i);
    
    // Cache
    /*
    String typeName = neededType.getName();
    if (cache.containsKey(typeName)) return cache.get(typeName);
    */

    SimpleList<Sequence> ret = gralComponents.getSequencesForType(neededType, false);
    if (operation instanceof TypedClassOperation) {
      if (classLiterals != null || packageLiterals != null) {

        ClassOrInterfaceType declaringCls = ((TypedClassOperation) operation).getDeclaringType();
        if (declaringCls != null) {
          if (classLiterals != null) {
            SimpleList<Sequence> sl = classLiterals.getSequences(declaringCls, neededType);
            if (!sl.isEmpty()) {
              ret = new ListOfLists<>(ret, sl);
            }
          }

          if (packageLiterals != null) {
            Package pkg = declaringCls.getPackage();
            if (pkg != null) {
              SimpleList<Sequence> sl = packageLiterals.getSequences(pkg, neededType);
              if (!sl.isEmpty()) {
                ret = new ListOfLists<>(ret, sl);
              }
            }
          }
        }
      }
    }
    
    // Cache
    //cache.put(typeName, ret);
    return ret;
  }

  /**
   * Returns all sequences that represent primitive values (e.g. sequences like
   * "Foo var0 = null" or "int var0 = 1"), including general components, class
   * literals and package literals.
   *
   * @return the sequences for primitive values
   */
  Set<Sequence> getAllPrimitiveSequences() {

    Set<Sequence> ret = new LinkedHashSet<>();
    if (classLiterals != null) {
      ret.addAll(classLiterals.getAllSequences());
    }
    if (packageLiterals != null) {
      ret.addAll(packageLiterals.getAllSequences());
    }
    for (PrimitiveType type : JavaTypes.getPrimitiveTypes()) {
      ret.addAll(gralComponents.getSequencesForType(type, true).toJDKList());
    }
    ret.addAll(gralComponents.getSequencesForType(JavaTypes.STRING_TYPE, true).toJDKList());
    return ret;
  }
}
