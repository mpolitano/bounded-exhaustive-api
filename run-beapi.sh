# #!/bin/bash


#java -Xmx8g -ea 
#-cp build/libs/randoop-all-3.0.6.jar:./examples/bin/ 
#randoop.main.Main gentests 
#--testclass=org.apache.commons.collections4.list.NodeCachingLinkedList 
#--literals-file=literals/literals3.txt 
#--canonicalizer-cfg=properties/scope3.all.canonicalizer.properties 
#--builder-methods="org.apache.commons.collections4.list.NodeCachingLinkedList.<init>\(int\)|org.apache.commons.collections4.list.AbstractLinkedList.add\(java.lang.Integer\)|org.apache.commons.collections4.list.AbstractLinkedList.clear\(\)|org.apache.commons.collections4.list.AbstractLinkedList.remove\(int\)" 
#--serialize-objects=objects.ser
#
BEAPI_LIB="build/libs/randoop-all-3.0.6.jar"
CLASSPATH=""
CLASS=""
LITERALS=""
BOUNDS=""
SERIALIZE=""
OUTPUT=""
PACKAGE=""
API=""
TIME=""
GENERICS=""
OBJECTS=""
ITERATIONS=""
LENGTH=""
MATCHING=""
for i in "$@"; do
  case $i in
    -cp=*|--classpath=*)
      CLASSPATH="${i#*=}"
      shift
      ;;
    -c=*|--class=*)
      CLASS="${CLASS} --testclass=${i#*=}"
      shift
      ;;
    -l=*|--literals=*)
      LITERALS="--literals-file=${i#*=}"
      shift
      ;;
    -b=*|--bounds=*)
      BOUNDS="--canonicalizer-cfg=${i#*=}"
      shift
      ;;
    -m=*|--methods=*)
      file_name=${i#*=}
      strres=$(cat $file_name | grep -v "#" | paste -s -d "|" \-)
    
      METHODS="--builder-methods=${strres}"
      shift
      ;;
    -s=*|--serialize=*)
      SERIALIZE="--serialize-objects=${i#*=}"
      shift
      ;;
    -d=*|--output-dir=*)
      OUTPUT="--junit-output-dir=${i#*=}"
      shift
      ;;    
    -p=*|--package=*)
      PACKAGE="--junit-package-name=${i#*=}"
      shift
      ;;
    -a=*|--list-api=*)  
      API="--only-list-methods=${i#*=}"
      shift
      ;;
    -t=*|--time=*)  
      TIME="--timelimit=${i#*=}"
      shift
      ;; 
    -io=*|--instance-objects-int=*)  
      OBJECTS="--instance-object-integer=${i#*=} --instance-generics-integer=${i#*=}"
      shift
      ;;
    -i=*|--iterations=*)  
      ITERATIONS="--max-BE-iterations=${i#*=}"
      shift
      ;;
    -tl=*|--test-length=*)  
      LENGTH="--BEmaxsize=${i#*=}"
      shift
      ;;  
    -sm=*|--matching=*)  
      MATCHING="--filtering=${i#*=}"
      shift
      ;; 
    -*|--*)
      echo "Unknown option $i"
      exit 1
      ;;
    *)
      ;;
  esac
done

if [ "$CLASSPATH" = "" ]; then
   echo "ERROR: Mandatory argument -cp missing."
fi
if [ "$CLASS" = "" ]; then
   echo "ERROR: Mandatory argument -c missing."
fi
if [ "$LITERALS" = "" ]; then
   echo "ERROR: Mandatory argument -l missing."
fi
if [ "$BOUNDS" = "" ]; then
   echo "ERROR: Mandatory argument -b missing."
fi



CMD="java -cp $BEAPI_LIB:$CLASSPATH randoop.main.Main gentests $CLASS $LITERALS $BOUNDS $METHODS $SERIALIZE $OUTPUT $PACKAGE $API $TIME $GENERICS $OBJECTS $ITERATIONS $LENGTH $MATCHING"
echo ""
echo ""
echo "Running: $CMD"
echo ""
echo ""
$CMD
